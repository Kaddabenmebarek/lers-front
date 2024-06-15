<%@ page import="java.sql.*,java.text.*,java.util.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="f" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:site>
    <jsp:attribute name="title">
        <title>DD Planner - Instrument Detail</title>
    </jsp:attribute>
    <jsp:body>
        <div id="central-row" class="row">
            <div class="col-12 text-center">
                <br/>
                <h3>Instrument ${instrument.name}</h3>
                <br/>&nbsp;


                <f:form modelAttribute="instrument" method="post" action="bookInstrument" id="bookInstrument_form">
                    <table class="table">
                        <tr>
                            <td>
                                <div align="left">Name:</div>
                            </td>
                            <td>
                                <f:hidden path="name" value="${instrument.name}"/>
                                <div align="left" style="color:#111">${instrument.name}</div>
                            </td>
                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td>
                                <div align="left">Description:</div>
                            </td>
                            <td>
                                <f:hidden path="description" value="${instrument.description}"/>
                                <div align="left" style="color:#111">${instrument.description}</div>
                            </td>
                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td>
                                <div align="left">Status:</div>
                            </td>
                            <td>
                                <f:hidden path="status" value="${instrument.status}"/>
                                <div align="left" style="color:#111">${instrument.status}</div>
                            </td>
                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td>
                                <div align="left">Location:</div>
                            </td>
                            <td>
                                <f:hidden path="location" value="${instrument.location}"/>
                                <div align="left" style="color:#111">${instrument.location}</div>
                            </td>
                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td>
                                <div align="left">Group:</div>
                            </td>
                            <td>
                                <f:hidden path="groupname" value="${instrument.groupname}"/>
                                <div align="left" style="color:#111">${instrument.groupname}</div>
                            </td>
                            <td>&nbsp;</td>
                        <tr>
                        <tr>
                            <td>
                                <div align="left">Owner:</div>
                            </td>
                            <td>
                                <f:hidden path="username" value="${instrument.username}"/>
                                <div align="left" style="color:#111">${instrument.username}</div>
                            </td>
                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td>
                                <div align="left">Deputy:</div>
                            </td>
                            <td>
                                <f:hidden path="deputy" value="${instrument.deputy}"/>
                                <div align="left" style="color:#111">${instrument.deputy}</div>
                            </td>
                            <td>&nbsp;</td>
                        </tr>                        
                        <tr>
                            <td>
                                <div align="left">Reservable:</div>
                            </td>
                            <td>
                                <f:hidden path="reservable" value="${instrument.reservable}"/>
                                <div align="left" style="color:#111">
                                    <c:choose>
                                        <c:when test="${instrument.reservable=='1'}"><span
                                                style="color:#111">Yes</span></c:when>
                                        <c:when test="${instrument.reservable=='0'}"><span style="color:#111">No</span></c:when>
                                    </c:choose>
                                </div>
                            </td>
                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td>
                                <div align="left">From:</div>
                            </td>
                            <td>
                                <input id="fromDatetimepicker" type="text">
                                <div id="fromDate"></div>
                            </td>

                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td>
                                <div align="left">To:</div>
                            </td>
                            <td>
                                <input id="toDatetimepicker" type="text">
                                <div id="toDate"></div>
                            </td>

                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td>
                                <div align="left">Percentage:</div>
                            </td>
                            <td>
                                    <%-- <f:hidden path="percentage" value="${instrument.percentage}" /> --%>
                                <div align="left" style="color:#111">
                                    <input type="text" value="100" size="2"/> %
                                </div>
                            </td>
                            <td>&nbsp;</td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td>
                                <div align="left"><input type="submit" name="Submit" value="Save"></div>
                            </td>
                        </tr>
                    </table>
                </f:form>
            </div>
        </div>
    </jsp:body>
</t:site>
