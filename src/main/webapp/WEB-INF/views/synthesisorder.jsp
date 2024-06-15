<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>

<%@include file="includes/checkUser.jsp"%>

<t:site>
	<jsp:attribute name="title">
        <title>DD Planner - Synthesis Service Order</title>
    </jsp:attribute>
	<jsp:attribute name="calendarDlg">
        <%@include file="includes/synthesisCalendar-dlg.html"%>
    </jsp:attribute>
	<jsp:attribute name="calendar">
        <script language="JavaScript">
	        document.addEventListener('DOMContentLoaded', function() {
	            let resources = [${synthesisOrderResources}];
	            let calendar = initCalendar(resources, 'menu-synthesisorder', true);
	            $(".fc-datagrid-expander").trigger("click");
	        });
        </script>
    </jsp:attribute>
	<jsp:body>
        <div id="central-row" class="row">
            <div class="col-12">
				<input type="text" id="synthesisFilter" placeholder="Filter orders by project/compound/requester (4 char min)" size="50" type="search">
				<button id="clearFilter">clear filters applied</button>
				<br /><br />
                <div id="calendar" class="orderCalendar"></div>
            </div>
        </div>
    <div id="userGroupsValues" style="display:none">${userGroups}</div>
    </jsp:body>
</t:site>