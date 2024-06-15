package org.research.kadda.labinventory.ui;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

public class PageAccess {
    public static ModelAndView requirementLoginRedirection(HttpSession session) {
        if(session.getAttribute("connectedUser")==null) {
            return new ModelAndView("redirect:/main");
        }
        if(session.getAttribute("username")==null) {
            return new ModelAndView("redirect:/main");
        }
        return null;
    }


}
