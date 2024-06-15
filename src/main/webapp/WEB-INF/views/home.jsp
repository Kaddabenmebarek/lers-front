<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@include file="includes/checkUser.jsp"%>

<t:site>
    <jsp:attribute name="calendar">
        <script>
            $( document ).ready(function() {
                updateMenu('menu-home');
        		$('#mixedSlider').multislider({
        			duration: 750,
        			interval: 3000000
        		});              
            });
        </script>
    </jsp:attribute>
    <jsp:body>
        <div id="central-row" class="row">
            <div class="col-12 text-center home-content">
                <h4>Welcome to the DD-Planner tool</h4>

               <%--  <div class="row img-click">
                    <a class="instruments" href="instrumentlist">
                        <img src="resources/images/instruments.svg" />
                        <%@include file="/resources/images/instruments.svg" %>
                        <span>Manage your instruments</span>
                    </a>
                    <a class="favorites" href="favorites-reservation">
                        <i class="far fa-star"></i>
                        <span>Manage your favorites reservation</span>
                    </a>
                </div> --%>
                <%--<br /><br />--%>
                <%--<p><span>You will find information on labs / instruments</span></p>
                <p><span>You could manage your instruments in the <a href="instrumentlist">Inventory</a> tab</span></p>
                <p><span>You could manage your reservations in the <a href="reservation">Reservation</a> tab</span></p>--%>
            </div>
            <%@include file="includes/news.html" %>
        </div>
    </jsp:body>
</t:site>