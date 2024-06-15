<%--
  Created by IntelliJ IDEA.
  User: Kadda
  Date: 17.12.2020
  Time: 21:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%@include file="includes/checkUser.jsp"%>

<t:site>
    <jsp:attribute name="title">
        <title>DD Planner - Favorite Reservations</title>
    </jsp:attribute>
    <jsp:attribute name="calendarDlg">
        <%@include file="includes/calendar-dlg.html" %>
    </jsp:attribute>
    <jsp:attribute name="calendar">
        <script language="JavaScript">
            document.addEventListener('DOMContentLoaded', function() {
                let resources = [${calendarResources}];
                let calendar = initCalendar(resources, 'menu-favorite', false);
                //$(".fc-allReservationButton-button").css("display","none");
                $(".fc-datagrid-expander").trigger("click");
            });
        </script>
    </jsp:attribute>
    <jsp:body>
        <div id="central-row" class="row">
            <div class="col-12">
                <div id="calendar"></div>
            </div>
        </div>
    <div id="userGroupsValues" style="display:none">${userGroups}</div>
    </jsp:body>
</t:site>