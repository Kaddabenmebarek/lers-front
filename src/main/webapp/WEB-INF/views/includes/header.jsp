<%--
<div id="logo">
	<a href="main">
	<table><tbody>
	 <tr>
	 	<td><img src="resources/images/banner4.png" /></td>
		<td><span style="margin-left: 650px;"><img src="resources/images/loogo.png" /></span></td>
		</tr>
	</tbody>
	</table>
	</a>
	<div class="line">
	<div id="maintitle" >
		Lab &amp; Equipment Reservation system
	</div>
	<div id="logout">
		<!-- TODO get the one from the pom -->
		<i><%=session.getAttribute("impversion")%></i>
		<% 
		if(session.getAttribute("connectedUser")!=null && !session.getAttribute("connectedUser").equals("")) {
		%> 
		<i>User connected:</i> <%=session.getAttribute("connectedUser")%> <a href="logout" class="btn2">logout</a>
		<% } %>
		
	</div>	
	<div style="clear: both;"></div>
	</div>
</div>
--%>
