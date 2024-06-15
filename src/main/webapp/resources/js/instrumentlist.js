$(document).ready(function () {
    $('#instruments-list').DataTable({
        order: [[0, "asc"]],
        fixedHeader: true,
        lengthMenu: [[10, 25, 50, 100, -1], [10, 25, 50, 100, "All"]],
        paging: true,
        searchHighlight: true,
        pageLength: 200,
		stateSave: true
    });
    var table = $('#instruments-list').DataTable();
    table.on('draw', function () {
        var body = $(table.table().body());

        // those 2 lines are producing an error in the log console
        //body.unhighlight();
        //body.highlight(table.search());
    });

    var employees = $('#employeeList')[0].options;
	var employeesArray = $.map(employees, function(elem) {
	    return (elem.text.toUpperCase());
	});

    var click_disabled = false;
    $('.favorite').click(function () {
        //alert(this.name);
        if (click_disabled) {
            return;
        }
        $(this).toggleClass('faved');
        var label = $(this).attr('aria-label') == 'Favourite' ? 'Unfavourite' : 'Favourite';
        $(this).attr('aria-label', label);
        click_disabled = true;
        setTimeout(function () {
            click_disabled = false;
        }, 1000);
        if (this.name == 1) {
            //alert("unbook");
            $(location).attr("href", "/lers/unBookInstrument_" + this.id);
        }
        if (this.name == 0) {
            //alert("book");
            $(location).attr("href", "/lers/bookInstrument_" + this.id);
        }
    });

	$("#instrument-modal button.save").on("click", function(ev) {
        let id = $('#instrument-modal #instrument-id').attr('value');
        let name = $('#instrument-modal #instrument-name').val();
        let description = $('#instrument-modal #instrument-description').val();
        //let statusValue = $('#instrument-modal #instrument-status').val();
        let status = $('#instrument-modal #instrument-status option:selected').text();
        let location = $('#instrument-modal #instrument-location').val();
        let group = $('#instrument-modal #instrument-group option:selected').text();
        let owner = $('#instrument-modal #instrument-owner').val();
        let reservableValue = $('#instrument-modal #instrument-reservable').val();
        let emailNotificationValue = $('#instrument-modal #instrument-email').val();
        let selectOverlapValue = $('#instrument-modal #instrument-overlap').val();
        let stepIncrementValue = $('#instrument-modal #instrument-stepIncrement').val();
        if(parseInt(stepIncrementValue) > 100){
        	alert("Minimal reservation must be lower than 100 ");
        	return;
        }
        let ratioComment = $('#instrument-modal #ratioComment').val();
		let startTimepoint = $('#instrument-modal #start-timepoint').val();
		let maxDays = $('#instrument-modal #instrument-maxDay').val();
		let highlightComment = $('#instrument-modal #instrument-highlight-comment').val();
		
        var getJson = function (b) {
           var result = $.fn.filterMultiSelect.applied
               .map((e) => JSON.parse(e.getSelectedOptionsAsJson(b)))
               .reduce((prev,curr) => {
                 prev = {
                   ...prev,
                   ...curr,
                 };
                 return prev;
               });
           return result;
        }
        let multiOptionFieldAsJson = JSON.stringify(getJson(true),null,"  ");
	
        // ajax call to update instrument
        let jsonData = {
            id: parseInt(id),
            name: name,
            description: description,
            status: status,
            location: location,
            //group: group,
            username: owner,
            multiOptionFieldAsJson: multiOptionFieldAsJson,
            reservable: parseInt(reservableValue),
            selectOverlap: parseInt(selectOverlapValue),
            emailNotification: parseInt(emailNotificationValue),
            stepIncrement: parseInt(stepIncrementValue),
            ratioComment: ratioComment,
			startTimepoint: startTimepoint,
			maxDays: maxDays,
			highlightComment: highlightComment
        };
        $.ajax({
            type: 'PUT',
            url: '/lers/instrument/update',
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(jsonData)
        }).done(function (data) {
            window.location = "/lers/instrumentlist"
        }).fail(function (data) {
            if (data && data.responseJSON && data.responseJSON.statusCode === "403") {
                alert("Cannot update the instrument. " + data.responseJSON.errorMessage);
            } else {
                alert("Instrument update failed! "  + data.responseJSON.errorMessage);
            }
        });
    });

	$('button.instrument-link').on('click', function(event) {
		//$('#wheelLoader').removeClass('hidden');
		
        let connectedUser = $('input#user-id').val();
        let tr = $(this).closest('tr');
        let owner = tr.find('td[data-instrument-owner]').text().trim();
        let deputies = tr.find('td[data-instrument-deputies]').text().split(',');
        let priorityUsers = tr.find('td[data-instrument-priority-users]').text().split(',');
        let ratioComment = tr.find('td[data-instrument-ratioComment]').text().trim();
		let startTimepoint = tr.find('td[data-instrument-startTimepoint]').text().trim();
		let maxDays = tr.find('td[data-instrument-maxDays]').text().trim();
		
        var deputiesArray = new Array()
        for(var i=0; i<deputies.length;i++){
        	deputiesArray[i] = deputies[i].trim();
        }
        var priorityUsersArray = new Array();
        for(var i=0; i<priorityUsers.length;i++){
        	priorityUsersArray[i] = priorityUsers[i].trim();
        }
        
        var missingData = !connectedUser || !owner || !deputiesArray;
        var match = false;
        if(!missingData){
        	match = connectedUser.toLowerCase() == owner.toLowerCase() || deputiesArray.includes(connectedUser.toUpperCase());
        }
        if (missingData || !match) {
            alert('You must be the owner or the deputy of the instrument to modify it.');
            return;
        }
        

        $('#instrument-modal-title').html('<div>Instrument Details</div><div>' + $(this).text() + '</div>');

        // init controls
        // <!-- fields : name, description, status, location, group, owner, reservable -->
        $('#instrument-modal #instrument-name').val($(this).text().trim());
        $('#instrument-modal #instrument-description').val(tr.find('td[data-instrument-description]').text().trim());
        let status = tr.find('td[data-instrument-status]').text().trim();
        $('#instrument-modal #instrument-status option').each(function() {
            if($(this).text().trim().toLowerCase() == status.toLowerCase()) {
                $(this).attr('selected', 'selected');
            } else {
                $(this).removeAttr('selected');
            }
        });
        $('#instrument-modal #instrument-location').val(tr.find('td[data-instrument-location]').text().trim());
        let group = tr.find('td[data-instrument-group]').text().trim();
        $('#instrument-modal #instrument-group option').each(function() {
            if($(this).text().trim().toLowerCase() == group.toLowerCase()) {
                $(this).attr('selected', 'selected');
            } else {
                $(this).removeAttr('selected');
            }
        });
        $('#instrument-modal #instrument-owner').val(owner);
        //$('#instrument-modal #instrument-owner').prop('disabled', true);

        $('#instrument-modal #deputies-div').children().remove();
        $('#instrument-modal #deputies-div').append('<select multiple class="filter-multi-select" name="instrument-deputies" id="instrument-deputies">');
        //$('#deputies-div').append('<select multiple name="instrument-deputies" id="instrument-deputies">');
        $('#instrument-modal #deputies-div').append('</select>');
        
        $('#instrument-modal #priorityUsers-div').children().remove();
        $('#instrument-modal #priorityUsers-div').append('<select multiple class="filter-multi-select" name="instrument-priority-users" id="instrument-priority-users">');
        $('#instrument-modal #priorityUsers-div').append('</select>');

		//const start = Date.now();
        
       	$.each( employeesArray, function( i, val ) {
	    	if (deputiesArray.includes(val.toUpperCase())){
	    		$('#instrument-modal #instrument-deputies').append('<option value="'+val+'" selected>'+val+'</option>');
	    	}else{
	    		$('#instrument-modal #instrument-deputies').append('<option value="'+val+'">'+val+'</option>');
	    	}
	    });

		$.each( employeesArray, function( i, val ) {
			if (priorityUsersArray.includes(val.toUpperCase())){
	    		$('#instrument-modal #instrument-priority-users').append('<option value="'+val+'" selected>'+val+'</option>');
	    	}else{	    		
	    		$('#instrument-modal #instrument-priority-users').append('<option value="'+val+'">'+val+'</option>');
	    	}
	    });

		//const end = Date.now();
		//alert(`Execution time: ${end - start} ms`);

       	$('#instrument-modal #ratioComment').val(ratioComment);

		$('#instrument-modal #start-timepoint').val(startTimepoint);
		$('#instrument-modal #instrument-maxDay').val(maxDays);
		
				
        includeJs("./resources/js/filter-multi-select-bundle.min.js");
        //const initDepCombo = $('#instrument-modal #instrument-deputies').filterMultiSelect();

		$("#instrument-modal #instrument-owner").autocomplete({
			minLength: 0,
			delay: 100,
			appendTo: "#instrument-modal .modal-body",
			source: employeesArray,
			select: function(event, ui) {
			},
			close: function() {
				$("#instrument-modal #instrument-owner").autocomplete('close');
			}
		});
		
        let reservable = tr.find('td[data-instrument-reservable]').text().trim();
        $("#instrument-modal #instrument-reservable").prop("selectedIndex", 0).change();
        $('#instrument-modal #instrument-reservable option').each(function() {
            if($(this).text().trim().toLowerCase() == reservable.toLowerCase()) {
                $(this).attr('selected', 'selected');
                if($(this).text().trim().toLowerCase() == 'yes'){                            	
                	$("#instrument-modal #instrument-reservable").prop("selectedIndex", 1).change();
                }
            } else {
                //$(this).removeAttr('selected');
            }
        });
        let overlap = tr.find('td[data-instrument-overlap]').text().trim();
       	$("#instrument-modal #instrument-overlap").prop("selectedIndex", 0).change();
        $('#instrument-modal #instrument-overlap option').each(function() {
            if($(this).text().trim().toLowerCase() == overlap.toLowerCase()) {
                //$(this).attr('selected', 'selected');
                if($(this).text().trim().toLowerCase() == 'yes' || $(this).text().trim().toLowerCase() == 'yes same'){             
                	$('#instrument-modal #div-stepIncrement1').css("display","block");
                	$('#instrument-modal #div-stepIncrement2').css("display","block");
                	$('#instrument-modal #div-stepIncrement3').css("display","block");
                	let index = $(this).text().trim().toLowerCase() == 'yes' ? 1 : 2;
                	$("#instrument-modal #instrument-overlap").prop("selectedIndex", index).change();
                }else{
                	$('#instrument-modal #div-stepIncrement1').css("display","none");
                	$('#instrument-modal #div-stepIncrement2').css("display","none");
                	$('#instrument-modal #div-stepIncrement3').css("display","none");
                	$("#instrument-modal #instrument-overlap").prop("selectedIndex", 0).change();
                }
            } else {                        	
                $(this).removeAttr('selected');
            }
        });
        let emailNotification = tr.find('td[data-instrument-email]').text().trim();
        $("#instrument-modal #instrument-email").prop("selectedIndex", 0).change();
        $('#instrument-modal #instrument-email option').each(function() {
            if($(this).text().trim().toLowerCase() == emailNotification.toLowerCase()) {
                $(this).attr('selected', 'selected');
                if($(this).text().trim().toLowerCase() == 'yes'){                            	
                	$("#instrument-modal #instrument-email").prop("selectedIndex", 1).change();
                }
                if($(this).text().trim().toLowerCase() == 'yes to all'){                            	
                	$("#instrument-modal #instrument-email").prop("selectedIndex", 2).change();
                }
            } else {
                //$(this).removeAttr('selected');
            }
        });
        let stepIncrement = tr.find('td[data-instrument-stepIncrement]').text().trim();
        $('#instrument-modal input#instrument-stepIncrement').val(stepIncrement);
        $('#instrument-modal input#instrument-id').val($(this).attr('data-instrument-id'));

		let highlightComment = tr.find('td[data-instrument-highlightComment]').text().trim();
		$('#instrument-modal #instrument-highlight-comment option').each(function() {
            if($(this).text().trim().toLowerCase() == highlightComment.toLowerCase()) {
                $(this).attr('selected', 'selected');
            } else {
                $(this).removeAttr('selected');
            }
        });
		
		
        // hide Delete button
        //$('#instrument-modal button.delete').show();
        
		$('#instrument-modal').modal('show');
		//$('#wheelLoader').addClass('hidden');
    });

    
	function includeJs(jsFilePath) {
        var js = document.createElement("script");
        js.type = "text/javascript";
        js.src = jsFilePath;
        document.body.appendChild(js);
    }

	function clearControls() {
        $('#instrument-modal #instrument-modal-title').html('<div>Instrument Details</div>');

        // clear controls
        // <!-- fields : name, description, status, location, group, owner, reservable -->
        $('#instrument-modal #instrument-name').val('');
        $('#instrument-modal #instrument-description').val('');
        $('#instrument-modal #instrument-status').val(0);
        $('#instrument-modal #instrument-location').val('');
        $('#instrument-modal #instrument-group').val(0);
        $('#instrument-modal #instrument-owner').val('');
        $('#instrument-modal #instrument-reservable').val(1); // yes (1) is the default
        $('#instrument-modal #instrument-email').val(0); 
        $('#instrument-modal #instrument-overlap').val(0);
        $('#instrument-modal #instrument-stepIncrement').val(0);

        $('#instrument-modal').modal('show');
    }

    $('#instrument-modal #instrument-overlap').on('change', function() {
        //alert( $(this).find(":selected").val() );
        if($(this).find(":selected").val() != 0){
        	$('#instrument-modal #div-stepIncrement1').css("display","block");
        	$('#instrument-modal #div-stepIncrement2').css("display","block");    
        	$('#instrument-modal #div-stepIncrement3').css("display","block");
        }else{
        	$('#instrument-modal #div-stepIncrement1').css("display","none");
        	$('#instrument-modal #div-stepIncrement2').css("display","none");
        	$('#instrument-modal #div-stepIncrement3').css("display","none");
        }
    });
    
    $('#start-timepoint-picker').datetimepicker({
		use24hours: true,
        format: 'HH:mm'
    });

    $('#addstartTimepoint-picker').datetimepicker({
		use24hours: true,
        format: 'HH:mm'
    });
    
   /*  $("#startTimepoint").on("input", function() {
    	$("#maxDaysDiv").css('display','block');
	});
	*/
	
    $(function () {
    	$('[data-toggle="tooltip"]').tooltip()
    });

	/*$('.btn-favorite').click(function () {

	});*/
	
    $('#instrument-add').on('click', function(event) {
		//$('#wheelLoader').removeClass('hidden');
        //window.open("https://snprod.service-now.com/sp", '_blank');

		$("#addinstrument-modal #addinstrumentOwner").autocomplete({
			minLength: 0,
			delay: 100,
			appendTo: "#addinstrument-modal .modal-body",
			source: employeesArray,
			select: function(event, ui) {
			},
			close: function() {
				$("#addinstrument-modal #addinstrumentOwner").autocomplete('close');
			}
		});

		$('#addinstrument-modal #adddeputies-div').children().remove();
        $('#addinstrument-modal #adddeputies-div').append('<select multiple class="filter-multi-select" name="addinstrument-deputies" id="addinstrument-deputies">');
        $('#addinstrument-modal #adddeputies-div').append('</select>');

       	$.each( employeesArray, function( i, val ) {
    		$('#addinstrument-modal #addinstrument-deputies').append('<option value="'+val+'">'+val+'</option>');
	    });
		
		$('#addinstrument-modal #addResaOptions-div').children().remove();
		$('#addinstrument-modal #addResaOptions-div').append('<select multiple class="filter-multi-select" name="addReservationOptions" id="addReservationOptions">');
        $('#addinstrument-modal #addResaOptions-div').append('</select>');

		var resaOptionList = $('#resaOptionList')[0].options;
		var resaOptionArray = $.map(resaOptionList, function(elem) {
		    return (elem.text);
		});
		$.each( resaOptionArray, function( i, val ) {
    		$('#addinstrument-modal #addReservationOptions').append('<option value="'+val+'">'+val+'</option>');
	    });
		
		$('#addinstrument-modal #addInstrGrp-div').children().remove();
		$('#addinstrument-modal #addInstrGrp-div').append('<select multiple class="filter-multi-select" name="addInstrumentGroup" id="addInstrumentGroup">');
        $('#addinstrument-modal #addInstrGrp-div').append('</select>');

		var instrGrpList = $('#instrGrpList')[0].options;
		var instrGrpArray = $.map(instrGrpList, function(elem) {
		    return (elem.text);
		});
		$.each(instrGrpArray, function( i, val ) {
    		$('#addinstrument-modal #addInstrumentGroup').append('<option value="'+val+'">'+val+'</option>');
	    });

		includeJs("./resources/js/filter-multi-select-bundle.min.js");
		
		$('#addinstrument-modal').modal('show');
		//$('#wheelLoader').addClass('hidden');
    });
	
	$("#addinstrument-modal button.save").on("click", function(ev) {
        let name = $('#addinstrument-modal #addinstrument-name').val();
        let description = $('#addinstrument-modal #addinstrument-description').val();
        let statusValue = $('#addinstrument-modal #addinstrument-status').val();
        let status = $('#addinstrument-modal #addinstrument-status option:selected').text();
        let location = $('#addinstrument-modal #addinstrument-location').val();
        let owner = $('#addinstrument-modal #addinstrumentOwner').val();
        let reservableValue = $('#addinstrument-modal #addinstrument-reservable').val();
        let emailNotificationValue = $('#addinstrument-modal #addinstrument-email').val();
        let selectOverlapValue = $('#addinstrument-modal #addinstrument-overlap').val();
        let stepIncrementValue = $('#addinstrument-modal #addinstrument-stepIncrement').val();
		let groupname = $('#addinstrument-modal #addinstrument-group option:selected').text();
        if(parseInt(stepIncrementValue) > 100){
        	alert("Minimal reservation must be lower than 100 ");
        	return;
        }
        var getJson = function (b) {
           var result = $.fn.filterMultiSelect.applied
               .map((e) => JSON.parse(e.getSelectedOptionsAsJson(b)))
               .reduce((prev,curr) => {
                 prev = {
                   ...prev,
                   ...curr,
                 };
                 return prev;
               });
           return result;
        }
        let multiOptionFieldAsJson = JSON.stringify(getJson(true),null,"  ");
        
        // ajax call to update instrument
        let jsonData = {
            name: name,
            description: description,
            status: status,
            location: location,
            username: owner,
            multiOptionFieldAsJson: multiOptionFieldAsJson,
            reservable: parseInt(reservableValue),
            selectOverlap: parseInt(selectOverlapValue),
            emailNotification: parseInt(emailNotificationValue),
            stepIncrement: parseInt(stepIncrementValue),
			groupname: groupname
        };
        $.ajax({
            type: 'POST',
            url: '/lers/instrument/add',
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(jsonData)
        }).done(function (data) {
            window.location = "/lers/instrumentlist";
			alert("Intrument "+name+" well added!")
        }).fail(function (data) {
            if (data && data.responseJSON && data.responseJSON.statusCode === "403") {
                alert("Cannot save the instrument.");
            } else {
                alert("Save Instrument failed!");
            }
        });
    });

	$('button.instrument-delete').on('click', function(event) {
		let insId = $(this).attr('data-instrument-id');
		let instrumentName = $(this).attr('data-instrument-name');
		$('#deleteinstrument-modal-title').html('<div>Delete Instrument <u>' + instrumentName + '</u></div>');
		$('#instidref').html('<input type="hidden" id="instrument-id" value="'+insId+'">');		
		
		$('#deleteinstrument-modal').modal('show');
	});

	$('#deleteinst-btn').on('click', function(event) {
		let insId = $('#deleteinstrument-modal #instrument-id').val();
		$('#deleteinstrument-modal').modal('hide');
		$.ajax({
			type: 'DELETE',
			url: '/lers/instrument/delete/' + insId,
			dataType: 'json',
			contentType: 'application/json',
			success: function(data) {
				window.location = "/lers/instrumentlist";
				alert("Intrument with id="+insId+" was removed!")
			},
			error: function(data) {
				console.log(data);
			}
		});
	});
	
});