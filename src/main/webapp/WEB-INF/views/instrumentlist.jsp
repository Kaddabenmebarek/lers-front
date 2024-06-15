<%@ page contentType="text/html;encoding=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@include file="includes/checkUser.jsp"%>

<t:site>
    <jsp:attribute name="title">
        <title>DD Planner - Instruments</title>
    </jsp:attribute>
    <jsp:attribute name="instrumentDlg">
        <%@include file="includes/instrument-dlg.html" %>
        <%@include file="includes/addinstrument-dlg.html" %>
        <%@include file="includes/deleteinstrument-dlg.html" %>
    </jsp:attribute>
    <jsp:attribute name="calendar">
        <script language="JavaScript">
            $( document ).ready(function() {
                updateMenu('menu-inventory');
            });
        </script>
    </jsp:attribute>
    <jsp:body>
        <input type="hidden" id="user-id" value="${connectedUser}">
        <div class="text-center">
            <h2>Instruments list</h2>
            <button type="button" class="btn btn-dark" id="instrument-add" data-toggle="tooltip" data-placement="top">
                <i class="fa fa-plus"></i> Add Instrument <i class="fas fa-external-link-alt"></i></button>
               <%-- <div id="instrument-toolbar" class="col-12"><span><i>To add a new instrument please open a ticket in <a href="https://snprod.service-now.com/sp">ASSiST</a></i></span></div>--%>
        </div>
        <table id="instruments-list" class="display">
            <thead>
            <tr>
                <th align="left">Add to favorites</th>
                <th align="left">Name</th>
                <th align="left">Description</th>
                <th align="left">Status</th>
                <th align="left">Location</th>
                <th align="left">Group</th>
                <th align="left">Owner</th>
                <th align="left">Deputies</th>
                <th align="left">Reservable</th>
                <th align="left">Overlap allowed</th>
                <th align="left">Email notification</th>
                <th align="left">Priority Users</th>
                <th align="left">Timepoint</th>
                <th align="left">Retention<br />(days)</th>
                <th align="left">Highlight Comment</th>
                <c:if test = "${canDeleteInstrument == 'true'}">
		        	<th align="left">Action</th>
		      	</c:if>
                <th align="left" style="display:none">Ratio comment</th>
                <th align="left" style="display:none">Default ratio (%)</th>
                <!-- <th>Action</th> -->
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${instruments}" var="instrument">
                <tr>
                    <td data-instrument-favorite>
                        <c:choose>
                            <c:when test="${instrument.reservable=='1'}">
                                <c:choose>
                                    <c:when test="${instrument.favorite==0}">
                                        <button class="favorite" aria-label="Favourite" id="${instrument.id}" name="${instrument.favorite}" ></button>
                                    </c:when>
                                    <c:when test="${instrument.favorite==1}">
                                        <button class="favorite faved" aria-label="Favourite" id="${instrument.id}" name="${instrument.favorite}" ></button>
                                    </c:when>
                                </c:choose>
                            </c:when>
                        </c:choose>
                    </td>                
                    <td>
                        <button type="button" class="btn instrument-link" data-instrument-id="${instrument.id}">
                            ${instrument.name}
                        </button>
                        <%--add a star with instrument name and remove "add to favorite" column--%>
                        <%--<c:choose>
                            <c:when test="${instrument.reservable=='1'}">
                                <c:choose>
                                    <c:when test="${instrument.favorite==0}">
                                        &lt;%&ndash;<button class="favorite" aria-label="Favourite" id="${instrument.id}" name="${instrument.favorite}"></button>&ndash;%&gt;
                                        <span class="btn-favorite"><i class="fa fa-star-o"></i></span>
                                    </c:when>
                                    <c:when test="${instrument.favorite==1}">
                                        &lt;%&ndash;<button class="favorite faved" aria-label="Favourite" id="${instrument.id}" name="${instrument.favorite}" ></button>&ndash;%&gt;
                                        <span class="btn-favorite"><i class="fa fa-star"></i></span>
                                    </c:when>
                                </c:choose>
                            </c:when>
                        </c:choose>--%>
                    </td>
                    <td data-instrument-description>${instrument.description}</td>
                    <td data-instrument-status>${instrument.status}</td>
                    <td data-instrument-location>${instrument.location}</td>
                    <td data-instrument-group>${instrument.groupname}</td>
                    <td data-instrument-owner>${instrument.username}</td>
                    <td data-instrument-deputies>${instrument.deputiesAsJson}</td>
                    <td data-instrument-reservable>
                        <c:choose>
                            <c:when test="${instrument.reservable=='1'}">Yes</c:when>
                            <c:when test="${instrument.reservable=='0'}">No</c:when>
                        </c:choose>
                    </td>
                    <td data-instrument-overlap>
                        <c:choose>
                            <c:when test="${instrument.selectOverlap=='0'}">No</c:when>
                            <c:when test="${instrument.selectOverlap=='1'}">Yes</c:when>
                            <c:when test="${instrument.selectOverlap=='2'}">Yes Same</c:when>
                        </c:choose>
                    </td>
                    <td data-instrument-email>
                        <c:choose>
                            <c:when test="${instrument.emailNotification=='0'}">No</c:when>
                            <c:when test="${instrument.emailNotification=='1'}">Yes to all</c:when>
                            <c:when test="${instrument.emailNotification=='2'}">Yes</c:when>
                        </c:choose>
                    </td>
                    <td data-instrument-priority-users>${instrument.priorityUsersAsJson}</td>
                    <td data-instrument-startTimepoint>${instrument.startTimepoint}</td>
                    <td data-instrument-maxDays>${instrument.maxDays}</td>
                    
                    <td data-instrument-ratioComment style="display:none">${instrument.ratioComment}</td>
                    <td data-instrument-stepIncrement style="display:none">${instrument.stepIncrement}</td>
                    <td data-instrument-highlightComment>
                        <c:choose>
                            <c:when test="${instrument.highlightComment=='1'}">Yes</c:when>
                            <c:when test="${instrument.highlightComment=='0'}">No</c:when>
                        </c:choose>
                    </td>
                    <c:if test = "${canDeleteInstrument == 'true'}">
			        	<td>
			        		<button type="button" class="btn instrument-delete" data-instrument-id="${instrument.id}" data-instrument-name="${instrument.name}">
                            	Delete
                        	</button>
			        	</td>
			      	</c:if>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <div id="wheelLoader" class="lds-dual-ring hidden overlay"></div>
        <div style="display:none"><select id="employeeList"><c:forEach items="${allUsers}" var="employee"><option value="${employee}">${employee}</option></c:forEach></select></div>
        <div style="display:none"><select id="resaOptionList"><c:forEach items="${allResaOption}" var="resaOption"><option value="${resaOption}">${resaOption}</option></c:forEach></select></div>
        <div style="display:none"><select id="instrGrpList"><c:forEach items="${instrumentGroupNames}" var="instGrp"><option value="${instGrp}">${instGrp}</option></c:forEach></select></div>
    </jsp:body>
</t:site>
