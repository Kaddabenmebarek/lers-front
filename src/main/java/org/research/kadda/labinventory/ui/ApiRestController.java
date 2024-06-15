package org.research.kadda.labinventory.ui;

import org.research.kadda.oauth.OktaLogon;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
public class ApiRestController {

    @RequestMapping(value="/sat", method = RequestMethod.GET)
    public String getSsoTokenAccess(HttpServletRequest request) {
        AccessToken accessToken = (AccessToken) request.getSession().getAttribute(OktaLogon.ACCESS_TOKEN_O_ATTRIBUTE);
        String accessTokenJson = null;
        if (accessToken != null){
            accessTokenJson = OktaLogon.ACCESS_TOKEN_O_ATTRIBUTE + "=" + URLEncoder.encode(accessToken.toAuthorizationHeader(), StandardCharsets.UTF_8);
        }
        return accessTokenJson;
    }
}
