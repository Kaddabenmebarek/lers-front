<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@include file="includes/checkUser.jsp"%>

<t:site>
	<jsp:attribute name="title">
        <title>DD Planner - HTMC Service Order</title>
    </jsp:attribute>
	<jsp:attribute name="calendarDlg">
        <%@include file="includes/htmcCalendar-dlg.html"%>
    </jsp:attribute>
	<jsp:attribute name="calendar">
        <script language="JavaScript">
	        document.addEventListener('DOMContentLoaded', function() {
	            let resources = [${htmcOrderResources}];
	            let calendar = initCalendar(resources, 'menu-htmcorder', true);
	            $(".fc-datagrid-expander").trigger("click");
	        });
        </script>
    </jsp:attribute>
	<jsp:body>
        <div id="central-row" class="row">
            <div class="col-12">
                <div id="calendar" class="orderCalendar"></div>
            </div>
        </div>
    <div id="userGroupsValues" style="display:none">${userGroups}</div>
    </jsp:body>
</t:site>