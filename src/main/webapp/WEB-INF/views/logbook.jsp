<%@ page contentType="text/html;encoding=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="f"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@include file="includes/checkUser.jsp"%>

<t:site>
    <jsp:attribute name="title">
        <title>DD Planner - Logbook</title>
    </jsp:attribute>
	<jsp:attribute name="calendar">
		<script src="./resources/js/filter-multi-select-bundle.min.js"></script>
        <script language="JavaScript">
			$(document).ready(
				function() {
					updateMenu('menu-history');
					$('#reservation-container').css('display','none');
					$('#synthesis-container').css('display','none');
					$('#htmc-container').css('display','none');
					$("#requestBookings").css('display','none');
					
					let departmentVal = $('#department').val();
					$('#'+ departmentVal +'-container').css('display','block');
					$("#resource-select").val(departmentVal).change();
					//resa datatable
					$("#resaHistoryTable").on('init.dt', function() {
						$('#resaHistoryTable').show();
					})
					.DataTable( {
						mark: true,
				        stateSave: false,
				        colReorder: true,
				        fixedHeader: true,
				        order: [[ 3, "desc" ]],
				        pagingType: "full_numbers",
				        lengthMenu: [[25, 50, 100, -1], [25, 50, 100, "All"]],
				        dom: 'Blfrtip',
				        buttons: [
				        	{
				                extend: 'pdfHtml5',
				                orientation: 'landscape',
				                //pageSize: 'LEGAL',
				                title: 'Lab Equipment Reservations' + '\n' + 'Logbook',
				                exportOptions: {
				                    columns: [ 0, 1, 2, 3, 4, 5, 6, 7, 8 ]
				                }
				            },
				            'print'
				        ]
				    } );
					//synthesis datatable
					$("#orderHistoryTable").on('init.dt', function() {
						$('#orderHistoryTable').show();
					})
					.DataTable( {
						mark: true,
				        stateSave: false,
				        colReorder: true,
				        fixedHeader: true,
				        order: [[ 4, "desc" ]],
				        pagingType: "full_numbers",
				        lengthMenu: [[25, 50, 100, -1], [25, 50, 100, "All"]],
				        dom: 'Blfrtip',
				        buttons: [
				        	{
				                extend: 'pdfHtml5',
				                orientation: 'landscape',
				                title: 'Synthesis Orders' + '\n' + 'Logbook',
				                exportOptions: {
				                	columns: ':visible',
				                    stripHtml: true,
				                },
				                customize: function(doc) {
				                    var arr2 = $('.img-fluid').map(function(){
				                    	return this.src;
				                    }).get();
				                  	for (var i = 0, c = 1; i < arr2.length; i++, c++) {
				                    	doc.content[1].table.body[c][10] = {
				                        	image: arr2[i],
				                            width: 80
										}
				                    }
				                  }
				            },
				            /* {
				                extend: 'excel',
				                orientation: 'landscape',
				                title: 'Synthesis Orders' + '\n' + 'Logbook',
				                exportOptions: {
				                    columns: [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9	 ]
				                }
				            } */
				        ]
				    } );
					//htmc datatable
					$("#htmcHistoryTable").on('init.dt', function() {
						$('#htmcHistoryTable').show();
					})
					.DataTable( {
						mark: true,
				        stateSave: false,
				        colReorder: true,
				        fixedHeader: true,
				        order: [[ 4, "desc" ]],
				        pagingType: "full_numbers",
				        lengthMenu: [[25, 50, 100, -1], [25, 50, 100, "All"]],
				        dom: 'Blfrtip',
				        buttons: [
				        	{
				                extend: 'pdfHtml5',
				                orientation: 'landscape',
				                title: 'HTMC Orders' + '\n' + 'Logbook',
				                exportOptions: {
				                    columns: [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9	 ]
				                }
				            },
				             {
				                extend: 'excel',
				                orientation: 'landscape',
				                title: 'HTMC Orders' + '\n' + 'Logbook',
				                exportOptions: {
				                    //columns: [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9	 ]
				                	columns: ':visible'
				                }
				            }
				        ]
				    } );					
				    $( "#from" ).datepicker({
				        changeMonth: true,
				        numberOfMonths: 1,
				        dateFormat: 'yy-mm-dd',
				        /*onSelect: function(date) {
				        }, */
				        onClose: function( selectedDate ) {
				          	$( "#to" ).datepicker( "option", "minDate", selectedDate );
				    	  	let toField = $("#to").val();
				    	  	if($('.selected-items').children().length <= 0 || !toField){
				    	  		$("#requestBookings").css('display','none');		
				    	  	}else{
				    	  		$("#requestBookings").css('display','block');
				    	  	}
				        }
				      });
				      $( "#to" ).datepicker({
				        defaultDate: "+1w",
				        changeMonth: true,
				        numberOfMonths: 1,
				        dateFormat: 'yy-mm-dd',
				        /*onSelect: function(date) {
				        }, */
				        onClose: function( selectedDate ) {
				          	$( "#to" ).datepicker( "option", "maxDate", selectedDate );
				    	  	let fromField = $("#from").val();
				    	  	if($('.selected-items').children().length <= 0 || !fromField){
				    	  		$("#requestBookings").css('display','none');		
				    	  	}else{
				    	  		$("#requestBookings").css('display','block');
				    	  	}				          
				        }
				      });
				      $( "#ordersFrom" ).datepicker({
					        changeMonth: true,
					        numberOfMonths: 1,
					        dateFormat: 'yy-mm-dd',
					        /*onSelect: function(date) {
					        },*/ 
					        onClose: function( selectedDate ) {
					        	$( "#ordersTo" ).datepicker( "option", "minDate", selectedDate );
					        }
					      });
				      $( "#ordersTo" ).datepicker({
				        defaultDate: "+1w",
				        changeMonth: true,
				        numberOfMonths: 1,
				        dateFormat: 'yy-mm-dd',
				        /*onSelect: function(date) {
				        },*/
				        onClose: function( selectedDate ) {
				        	$( "#ordersTo" ).datepicker( "option", "minDate", selectedDate );
				        }
				      });	
				      $( "#htmcFrom" ).datepicker({
				        changeMonth: true,
				        numberOfMonths: 1,
				        dateFormat: 'yy-mm-dd',
				        /*onSelect: function(date) {
				        },*/ 
				        onClose: function( selectedDate ) {
				        	$( "#htmcTo" ).datepicker( "option", "minDate", selectedDate );
				        }
				      });
				      $( "#htmcTo" ).datepicker({
				        defaultDate: "+1w",
				        changeMonth: true,
				        numberOfMonths: 1,
				        dateFormat: 'yy-mm-dd',
				        /*onSelect: function(date) {
				        }, */
				        onClose: function( selectedDate ) {
				        	$( "#htmcTo" ).datepicker( "option", "minDate", selectedDate );
				        }
				      });					      
				      
					  	$(".custom-checkbox").on("click",function(){
					        if(this.checked){
					        	let fromField = $("#from").val();
					    	  	let toField = $("#to").val();
					    	  	if(!fromField && !toField){
					    	  		$("#requestBookings").css('display','none');		
					    	  	}else{
					    	  		$("#requestBookings").css('display','block');
					    	  	}
					         }
					    });
					  	
					  	$("#resource-select").change(function(){
					  		var currentlySelected = $('#resource-select :selected').val();
					  		//alert(currentlySelected);
					  		clearInputs();
					  		$('#reservation-container').css('display','none');
					  		$('#synthesis-container').css('display','none');
					  		$('#htmc-container').css('display','none');
					  		$('#'+ currentlySelected +'-container').css('display','block');
					    });
					  	
					  	function clearInputs(){
					  		$('#resaHistoryTable').dataTable().fnClearTable();
					  		$('#orderHistoryTable').dataTable().fnClearTable();
					  		$('#htmcHistoryTable').dataTable().fnClearTable();
							
							var $resadates = $('#from, #to').datepicker();
							$resadates.datepicker('setDate', null);
							var $orderdates = $('#ordersFrom, #ordersTo').datepicker();
							$orderdates.datepicker('setDate', null);
							var $htmcdates = $('#htmcFrom, #htmcTo').datepicker();
							$htmcdates.datepicker('setDate', null);
							
							//$('#instrSelect').val('');
							$('.selected-items').empty();
					  	}
					  	
					  	$("#orderHistory_form").submit(
							function() {
								let ordersFrom = $("#ordersFrom").val();
								let ordersTo = $("#ordersTo").val();
								if (ordersFrom && ordersTo) {
									return true;
								} else {
									alert("Please fill the date fields");
									return false;
								}
							});
					  	$("#htmcHistory_form").submit(
							function() {
								let htmcFrom = $("#htmcFrom").val();
								let htmcTo = $("#htmcTo").val();
								if (htmcFrom && htmcTo) {
									return true;
								} else {
									alert("Please fill the date fields");
									return false;
								}
							});
				});
    	</script>
    </jsp:attribute>
	<jsp:body>
	<br />
	<div>
		<div class="row">
		<div class="col-md-4">&nbsp;</div>
		<div class="col-md-1">Service</div>
			<div class="col-md-3">
				<div class="form-group">
					<select class="form-control form-control-lg" id="resource-select">
	                    <option value="reservation">Lab Equipment Reservations History</option>
	                    <option value="synthesis">Synthesis Planner History</option>
	  					<option value="htmc">HTMC Planner History</option>
	                </select>
                </div> 
              </div>
          </div>	
          <br />
        <div id="reservation-container">
			<f:form modelAttribute="reservationHistoryDto" method="post" action="logbook">
				<div class="row">
					<div class="col-md-4">&nbsp;</div>
					<div class="col-md-1">Instrument</div>
	                <div class="col-md-7">
						<div class="form-group">${instrumentMultiSelectHtml}</div>
	                </div>
	            </div>		
	            <div class="row">
					<div class="col-md-4">&nbsp;</div>
					<div class="col-md-1">From</div>
	                <div class="col-md-7">
						<div class="form-group">
	                    	<input type="text" id="from" name="startDate" readonly="readonly" class="date-field" value="${requestStartDate}"/>&nbsp;To&nbsp;<input type="text" id="to" name="endDate" readonly="readonly" class="date-field" value="${requestEndDate}"/>
	                    </div>
	                </div>
	            </div>
				<div class="row">
					<div class="col-md-5">&nbsp;</div>
	                <div class="col-md-7">
						<div class="form-group">
	                    	<input type="submit" name="Submit" value="Show Reservations" id="requestBookings" class="btn btn-secondary">
	                    </div>
	                </div>
	            </div> 
			</f:form>
            <div class="col-12 text-center">
		    	<h5>Lab Equipment Reservation history</h5>
		    </div>			
		<table id="resaHistoryTable" class="display">
			<thead>
				<tr>
					<th>Instrument</th>
					<th>Owner</th>
					<th>Booked by</th>
					<th>From</th>
					<th>To</th>
					<th>Option</th>
					<th>Project</th>
					<th>Compound</th>
					<th>Batch</th>
				</tr>
			</thead>
			<tbody>
	 			<c:forEach items="${reservations}" var="reservation">
	 			<tr class="reservation">
					<td>${reservation.instrumentName}</td>
					<td>${reservation.instrumentOwner}</td>
					<td>${reservation.username}</td>
					<td>${reservation.fromTimeToDisplay}</td>
					<td>${reservation.toTimeToDisplay}</td>
					<td>${reservation.reservationOptions}</td>
					<td>${reservation.reservationUsage.project}</td>
					<td>${reservation.reservationUsage.compound}</td>
					<td>${reservation.reservationUsage.batch}</td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
		</div>
		<div id="synthesis-container">
			<f:form modelAttribute="reservationHistoryDto" method="post" action="orderhistory" id="orderHistory_form">
	            <div class="row">
					<div class="col-md-4">&nbsp;</div>
					<div class="col-md-1">From</div>
	                <div class="col-md-7">
						<div class="form-group">
	                    	<input type="text" id="ordersFrom" name="startDate" readonly="readonly" class="date-field" value="${orderRequestStartDate}"/>&nbsp;To&nbsp;<input type="text" id="ordersTo" name="endDate" readonly="readonly" class="date-field" value="${orderRequestEndDate}"/>
	                    </div>
	                </div>
	            </div>
				<div class="row">
					<div class="col-md-5">&nbsp;</div>
	                <div class="col-md-7">
						<div class="form-group">
	                    	<input type="submit" name="Submit" value="Show Orders" id="requestOrderHistory" class="btn btn-secondary">
	                    </div>
	                </div>
	            </div> 
			</f:form>
			<div class="col-12 text-center">
		    	<h5>Synthesis Orders History</h5>
		    </div>			
			<table id="orderHistoryTable" class="display">
			<thead>
				<tr>
					<th>Title</th>
					<th>Recorded by</th>
					<th>Requester</th>
					<th>Request Date</th>
					<th>From</th>
					<th>To</th>
					<th>Project</th>
					<th>Quantity</th>
					<th>Compound</th>
					<th>Structure</th>
					<th>External Link</th>
				</tr>
			</thead>
			<tbody>
	 			<c:forEach items="${orders}" var="order">
	 			<tr class="order">
					<td>${order.title}</td>
					<td>${order.username}</td>
					<td>${order.requester}</td>
					<td>${order.requestTimeToDisplay}</td>
					<td>${order.fromTimeToDisplay}</td>
					<td>${order.toTimeToDisplay}</td>
					<td>${order.project}</td>
					<td>${order.quantity} ${order.unit}</td>
					<td>${order.compound}</td>
					<td>
						<c:if test="${order.isStructureAvailable == 'Y'}">
							<img src="data:image/png;base64, ${order.base64Img}" class="img-fluid"/>
						</c:if>
					</td>
					<td>
						<a href="${order.link}" target="blank">${order.link}</a>
					</td>					
				</tr>
				</c:forEach>
			</tbody>
		</table>
		</div>
		<div id="htmc-container">
			<f:form modelAttribute="reservationHistoryDto" method="post" action="htmchistory" id="htmcHistory_form">
	            <div class="row">
					<div class="col-md-4">&nbsp;</div>
					<div class="col-md-1">From</div>
	                <div class="col-md-7">
						<div class="form-group">
	                    	<input type="text" id="htmcFrom" name="startDate" readonly="readonly" class="date-field" value="${htmcRequestStartDate}"/>&nbsp;To&nbsp;<input type="text" id="htmcTo" name="endDate" readonly="readonly" class="date-field" value="${htmcRequestEndDate}"/>
	                    </div>
	                </div>
	            </div>
				<div class="row">
					<div class="col-md-5">&nbsp;</div>
	                <div class="col-md-7">
						<div class="form-group">
	                    	<input type="submit" name="Submit" value="Show Orders" id="requestHtmcHistory" class="btn btn-secondary">
	                    </div>
	                </div>
	            </div> 
			</f:form>
			<div class="col-12 text-center">
		    	<h5>HTMC Orders History</h5>
		    </div>			
			<table id="htmcHistoryTable" class="display">
			<thead>
				<tr>
					<th>Title</th>
					<th>Recorded by</th>
					<th>Requester</th>
					<th>Request Date</th>
					<th>From</th>
					<th>To</th>
					<th>Project</th>
					<th>Library Outcome</th>
					<th>Report Link</th>
				</tr>
			</thead>
			<tbody>
	 			<c:forEach items="${htmcorders}" var="htmcorder">
	 			<tr class="order">
					<td>${htmcorder.title}</td>
					<td>${htmcorder.username}</td>
					<td>${htmcorder.requester}</td>
					<td>${htmcorder.requestTimeToDisplay}</td>
					<td>${htmcorder.fromTimeToDisplay}</td>
					<td>${htmcorder.toTimeToDisplay}</td>
					<td>${htmcorder.project}</td>
					<td>${htmcorder.libraryoutcome}</td>
					<td>
						<a href="${htmcorder.link}" target="blank">${htmcorder.link}</a>
					</td>					
				</tr>
				</c:forEach>
			</tbody>
		</table>
		</div>	
		<div style="display:none"><input type="text" id="department" value="${department}"/></div>	
	</div>
    </jsp:body>
</t:site>