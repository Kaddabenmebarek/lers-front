<!-- %@ page import="java.sql.*,java.text.*,java.util.*"%-->
<%@page import="com.validate.research.oauth.OktaLogonUser" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>


<%
    if (session.getAttribute("connectedUser") != null) {
        response.sendRedirect("main");
    }
%>


<t:site>
    <jsp:attribute name="title">
        <title>DD Planner - Login</title>
    </jsp:attribute>
    <jsp:attribute name="header">
        <link href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700&display=swap" rel="stylesheet" />
        <link href="https://cdnjs.cloudflare.com/ajax/libs/mdb-ui-kit/3.6.0/mdb.min.css" rel="stylesheet" />
        <link rel="stylesheet" type="text/css" href="resources/css/oktaStyle.css" />
        <link rel="stylesheet" type="text/css" href="resources/css/oktaStyle-dark.css" media="screen and (prefers-color-scheme: dark)"/>
    </jsp:attribute>
    <jsp:body>
        <form:form modelAttribute="employeeModel" method="post" action="connect" id="login_form">
            <div id="login-table">

                <c:set value="${pageContext != null && pageContext.request != null && !OktaLogonUser.isRestrictedVLAN(pageContext.request)}" var="isNotVLANRestricted"/>
                <c:if test="${isNotVLANRestricted}">
                <div id="okta-login">
                    <a href="connectByOkta?redirectToOktaOauth" class="btn btn-primary btn-lg" role="button" id="okta-signin">
                        <i class="fas fa-sign-in-alt"></i>
                        <span>Sign In with</span>
                        <img src="https://www.okta.com/themes/custom/okta_www_theme/images/logo.svg?v2" height="26px" alt="Okta"/>
                    </a>
                </div>
                </c:if>
                <c:if test="${!isNotVLANRestricted}">
                    <div id="restrictedVlanMsgToBeLoaded">
                        <div class="warn my-3" id="restrictedVlanMsg">
                            <h5>You are on restricted VLAN</h5>
                            <span>Auto logon with Okta is not possible</span>
                            <p class="font-italic">If you are on your PC, please contact <a href="mailto:employees.chemistry.scdd.applications.bio-pharmadev-support@validate.com">SCDD team</a></p>
                        </div>
                        <script>
                            $( "#restrictedVlanMsgToBeLoaded" ).load( "/widget/msg/restrictedVlan" );
                        </script>
                    </div>
                    <%--<embed src="/widget/msg/restrictedVlan" width=200 height=200 />--%>
                </c:if>

                <div id="osiris-login">
                    <h2>Login with Osiris</h2>
                    <hr class="my-3">

                    <div class="row">
                        <form:label path="userName">Login&nbsp;</form:label>
                        <form:input path="userName"/>
                    </div>
                    <div class="row">
                        <form:label path="password">Password&nbsp;</form:label>
                        <form:password path="password"/>
                    </div>
                    <div class="row">
                        <input type="submit" name="Submit" value="Login" class="btn btn-secondary">
                    </div>
                </div>
                <p class="error_login">${employeeModel.errorLogin}</p>
            </div>
        </form:form>
    </jsp:body>
</t:site>
