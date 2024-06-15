<%@tag description="Web site layout" pageEncoding="UTF-8" %>
<%@attribute name="header" fragment="true" %>
<%@attribute name="title" fragment="true" %>
<%@attribute name="calendar" fragment="true" %>
<%@attribute name="calendarDlg" fragment="true" %>
<%@attribute name="instrumentDlg" fragment="true" %>

<html>
<jsp:invoke fragment="title"/>
<%@include file="/WEB-INF/views/includes/head.jsp" %>
<jsp:invoke fragment="header"/>
<body>
    <div id="banner" class="row">
        <div id="logos" class="row flex-nowrap">
            <a id="home" href="./" class="col-1 ms-1" title="Home">
                <img id="logo" src="resources/images/calendar-icon.svg" />
                <div id="application-name" class="col-4">
                    <span id="application-name-1">DD Planner</span>
                </div>
            </a>
            <a id="logo-link" href="https://sharepoint.com/sites/research-hub" target="_blank" class="d-flex align-items-center">
                <img id="logo"/>
            </a>
        </div>

        <div id="menu-bar" <%--class="col-6"--%>>
            <div id="menu-bar-items">
                <%@include file="/WEB-INF/views/includes/menu.jsp" %>
            </div>
        </div>
        <ul id="menu-right" class="navbar-nav">
            <li class="nav-item">
                <jsp:include page="/WEB-INF/views/includes/themer.jsp" />
            </li>
            <li class="nav-item">
                <jsp:include page="/WEB-INF/views/includes/ddResearchAppMenuIcon.jsp" />
            </li>
            <li class="nav-item">
                <jsp:include page="/WEB-INF/views/includes/userInfo.jsp" />
            </li>
        </ul>
    </div>
    <main role="main">
        <div id="central-row" class="row">
            <div id="main-content" class="col-12">
                <jsp:doBody/>
            </div>
        </div>

        <jsp:invoke fragment="calendar"/>
        <jsp:invoke fragment="calendarDlg"/>
        <jsp:invoke fragment="instrumentDlg"/>
    </main>

    <script>
        function getMonthFormatted(value) {
            var month = value + 1;
            return month < 10 ? '0' + month : month;
        }
        function getDayFormatted(value) {
            return value < 10 ? '0' + value : value;
        }
        function updateMenu(menuId) {
            $('nav.nav .nav-item').each(function() {
               $(this).removeClass('active');
            });
            $('nav.nav .nav-item#' + menuId).addClass('active');
        }

        /* COOKIE MANAGEMENT */
        function setCookie(cname, cvalue, exdays) {
            var d = new Date();
            d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
            var expires = "expires="+d.toUTCString();
            document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
        }

        function getCookie(cname) {
            var name = cname + "=";
            var ca = document.cookie.split(';');
            for(var i = 0; i < ca.length; i++) {
                var c = ca[i];
                while (c.charAt(0) == ' ') {
                    c = c.substring(1);
                }
                if (c.indexOf(name) == 0) {
                    return c.substring(name.length, c.length);
                }
            }
            return "";
        }

        function checkCookie() {
            var user = getCookie("username");
            if (user != "") {
                alert("Welcome again " + user);
            } else {
                user = prompt("Please enter your name:", "");
                if (user != "" && user != null) {
                    setCookie("username", user, 365);
                }
            }
        }
    </script>
</body>
</html>
