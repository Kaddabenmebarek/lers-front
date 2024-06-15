package org.research.kadda.labinventory.ui;

import com.actelion.research.security.entity.User;
import com.actelion.research.security.exception.AuthenticationException;
import com.actelion.research.security.exception.DataReadAccessException;
import com.actelion.research.security.service.UserServiceFactory;
import com.actelion.research.util.SSO;
import org.research.kadda.oauth.ConnectorUtils;
import org.research.kadda.oauth.OktaLogon;
import org.research.kadda.oauth.OktaLogonUser;
import org.research.kadda.oauth.OpenIDConnector;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.research.kadda.labinventory.core.model.EmployeeModel;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Properties;

@Controller
@Scope("session")
public class ConnectController {

	private static final Logger logger = LogManager.getLogger(ConnectController.class);
	public static final String OKTA_LOGIN_PATH = "connectByOkta";
	public static final String OKTA_LOGIN_URL = "/" + OKTA_LOGIN_PATH;

	@RequestMapping(value = "/main", method = RequestMethod.GET)
	public ModelAndView displayMain() {
		return new ModelAndView("home");
	}

	@RequestMapping(value = OKTA_LOGIN_URL, method = RequestMethod.GET)
	public ModelAndView connectByOkta(HttpServletRequest request) {
		HttpSession session = request.getSession();

		OktaLogonUser oktaLogon = new OktaLogonUser(request){
			@Override
			public String handleNullAccessToken(String logonPage) throws MalformedURLException {
				String requestedPage = getRequestedPage();
				if(requestedPage.endsWith(logonPage)){
					return null;
				}
				return redirectToOktaOauthUrl();
			}

			@Override
			public void loadData(OpenIDConnector openIDConnector, AccessToken accessToken) throws Exception {
				super.loadData(openIDConnector, accessToken);

				String connectedUser = openIDConnector.getFullName(accessToken);
				String username = openIDConnector.getUserName(accessToken);

				setLoginSession(request, connectedUser, username);

				UserInfo userInfo = openIDConnector.getUserInfo(accessToken);
				session.setAttribute("userInfo", userInfo);
				session.setAttribute("userPicture", userInfo.getPicture());
				session.setMaxInactiveInterval(14400000); //4h
			}
		};

		if (request.getParameter("redirectToOktaOauth") != null) {
			try {
				String oktaRedirect = oktaLogon.redirectToOktaOauthUrl();
				return new ModelAndView("redirect:" + oktaRedirect);
			} catch (MalformedURLException e) {
				logger.error(e.getMessage(), e);
				session.setAttribute("loginError", e.getMessage());
				return new ModelAndView("redirect:/login");
			}
		}

		String username = null;
		try {
			String redirect = oktaLogon.logon("/login");
			if(redirect != null){
				return new ModelAndView("redirect:" + redirect );
			}
			username = (String) session.getAttribute(OktaLogon.USERNAME_ATTRIBUTE);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			session.setAttribute("loginError", e.getMessage());
			return new ModelAndView("redirect:/login");
		}

		if(username == null){
			return new ModelAndView("redirect:/login");
		}

		return new ModelAndView("redirect:/home");
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView displayLogin(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		EmployeeModel employeeModel = new EmployeeModel();
		String loginError = (String) session.getAttribute("loginError");
		if (loginError != null) {
			employeeModel.setErrorLogin(loginError);
		}
		String[] credentials = SSO.retrieveLogin(request, response);
		if(session.getAttribute("username") == null && (credentials != null && credentials[0] != null)) {
			String username = credentials[0].toUpperCase();
			session.setAttribute("username", username);
			return new ModelAndView("redirect:/main");
		}
		ModelAndView mv = new ModelAndView("/login");
		mv.addObject("employeeModel", employeeModel);
		return mv;
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ModelAndView displayLogout(HttpServletRequest request, HttpServletResponse response, ModelAndView model) {
		ModelAndView mv = new ModelAndView("redirect:/login");
		setLoginSession(request, null, null);
		ConnectorUtils.clearAccessTokenAndUserFromSession(request);
		HttpSession session = request.getSession();
		session.removeAttribute("userInfo");
		session.removeAttribute("userPicture");
		session.removeAttribute("connectedUser");
		//This is destroying the session
		SSO.logout(request, response);
		return mv;
	}
	
	@RequestMapping(value = "/connect", method = RequestMethod.POST)
	public ModelAndView connectWithOsiris(@ModelAttribute("SpringWeb") EmployeeModel employeeModel, HttpServletRequest request,
										  HttpServletResponse response) {

		HttpSession session = request.getSession();
		String username = employeeModel.getUserName();
		User user = null;
		try {
			user = UserServiceFactory.getUserService().authenticateOsirisUser(username, employeeModel.getPassword(), true);
			//Workaround isSuperAdmin
			ConnectorUtils.updateIsSuperAdmin(user, username);
		} catch (AuthenticationException | DataReadAccessException e) {
			session.setAttribute("loginError", e.getMessage());
			logger.error(e);
		}
		session.setMaxInactiveInterval(14400000); //4h
		if (user != null) {
			String connectedUser = user.getFullName();
			setLoginSession(request, connectedUser, username);
			session.setAttribute("user", user);
			SSO.saveLogin(request, response, employeeModel.getUserName().toUpperCase(), employeeModel.getPassword());

			return new ModelAndView("redirect:/main");
		}
		return new ModelAndView("redirect:/login");
	}

	private void setLoginSession(HttpServletRequest request, String connectedUser, String username) {
		HttpSession session = request.getSession();
		if(connectedUser != null) {
			logger.info("User is logged in : " + connectedUser + " (" + username + ")");
			session.setMaxInactiveInterval(60*48);
		}
		session.setAttribute("connectedUser", connectedUser);
		session.setAttribute("username", username);
	}

	//TODO: use it in context init
	void logAppVersion(HttpServletRequest request){
		String version = getClass().getPackage().getImplementationVersion();
		if (version==null) {
			Properties prop = new Properties();
			try {
				InputStream is = request.getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF");
				if (is != null) {
					prop.load(is);
					version = prop.getProperty("Implementation-Version");
				}
			} catch (IOException e) {
				logger.error(e.toString());
			}
		}
		logger.info("Starting App version "+version);
		/*session.setAttribute("impversion", "v" + version);*/
	}
}
