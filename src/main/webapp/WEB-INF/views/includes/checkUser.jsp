<%@ page import="org.research.kadda.labinventory.ui.ConnectController" %><%--
  Created by IntelliJ IDEA.
  User: Kadda
  Date: 08.01.2021
  Time: 15:02
  To change this template use File | Settings | File Templates.
--%>

<%
	//System.out.println("connectedUser = " + session.getAttribute("connectedUser"));
	if (session.getAttribute("connectedUser") == null) {
        response.sendRedirect(ConnectController.OKTA_LOGIN_PATH);
    }
%>
