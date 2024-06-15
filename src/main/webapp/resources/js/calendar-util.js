
function initCalendar(resources, menuId, isNotReservationCalendar) {

	console.log('Updating menu');
	updateMenu(menuId);
	let synthesisCalendar = false;

	function updateTooltip(el, event) {
		let startDate = new Date(Date.parse(event.startStr));
		let endDate = new Date(Date.parse(event.endStr));
		let tooltipText = "";
		if (isNotReservationCalendar === true) {
			let options = { year: 'numeric', month: '2-digit', day: '2-digit' };
			let project = event.extendedProps.project;
			let requester = event.extendedProps.requester;
			let doneTime;
			if (event.extendedProps.doneTime) {
				doneTime = new Date(Date.parse(event.extendedProps.doneTime));
			}
			if (synthesisCalendar === true) {
				let compound = event.extendedProps.compound;
				tooltipText = tooltipText + "<div class=\"event-tooltip\">";
				if (requester) { tooltipText = tooltipText + "<br /><div>Request:</div>" + requester; }
				if (startDate) { tooltipText = tooltipText + "<br /><div>From:</div>" + startDate.toLocaleDateString('en-gb', options); }
				if (endDate) { tooltipText = tooltipText + "<br /><div>To:</div>" + endDate.toLocaleDateString('en-gb', options); }
				if (project) { tooltipText = tooltipText + "<br /><div>Project:</div>" + project; }
				if (doneTime) { tooltipText = tooltipText + "<br /><div>Done:</div>" + doneTime.toLocaleDateString('en-gb', options); }
				if (!compound) {
					compound = "N/A";
				}
				tooltipText = tooltipText + "<br /><div>Compound:</div>" + compound;
				getMolStructureByCoumpound(compound, tooltipText, el);
			} else {
				let libraryOutcome = event.extendedProps.libraryOutcome;
				tooltipText = tooltipText + "<div class=\"event-tooltip\">" + event.title;
				if (startDate) { tooltipText = tooltipText + "<br /><div>From:</div>" + startDate.toLocaleDateString('en-gb', options); }
				if (endDate) { tooltipText = tooltipText + "<br /><div>To:</div>" + endDate.toLocaleDateString('en-gb', options); }
				if (project) { tooltipText = tooltipText + "<br /><div>Project:</div>" + project; }
				if (libraryOutcome) { tooltipText = tooltipText + "<br /><div>Library outcome:</div>" + libraryOutcome; }
				tooltipText = tooltipText + "</div>";
				el.attr('data-original-title', tooltipText);
			}
		} else {
			let remark = event.extendedProps.remark;
			let resOptionValue = event.extendedProps.resOptionValue;
			let resOption = event.extendedProps.resOption;
			let ratio = event.extendedProps.ratio;
			let options = { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' };
			if (!ratio) { ratio = 0; }

			tooltipText = tooltipText + "<div class=\"event-tooltip\">" + event.title;
			if (startDate) { tooltipText = tooltipText + "<br /><div>From:</div>" + startDate.toLocaleDateString('en-gb', options); }
			if (endDate) { tooltipText = tooltipText + "<br /><div>To:</div>" + endDate.toLocaleDateString('en-gb', options); }
			if (resOption && (resOptionValue || resOptionValue != 0)) { tooltipText = tooltipText + "<br /><div>Option:</div>" + resOption; }
			if (remark) { tooltipText = tooltipText + "<br /><div>Comment:</div>" + remark; }
			if (event.extendedProps.project) { tooltipText = tooltipText + "<br /><div>Project:</div>" + event.extendedProps.project; }
			if (event.extendedProps.compound) { tooltipText = tooltipText + "<br /><div>Compound:</div>" + event.extendedProps.compound; }
			if (event.extendedProps.batch && event.extendedProps.batch !== "0") { tooltipText = tooltipText + "<br /><div>Batch:</div>" + event.extendedProps.batch; }
			tooltipText = tooltipText + "</div>";
			el.attr('data-original-title', tooltipText);
		}
	}

	function registerEvent(info) {
		let jsonData = {
			resourceId: info.resource.id,
			selectOverlap: info.resource.extendedProps.selectOverlap,
			fromDate: info.startStr,
			toDate: info.endStr,
			remark: info.comment,
			resoptid: info.resOptionValue,
			ratio: info.ratio,
			project: info.project,
			compound: info.compound,
			//sample: info.sample,
			batch: info.batch,
			sampleType: info.sampleType,
			specie: info.specie
		};
		$.ajax({
			type: 'POST',
			url: '/lers/reservation/add',
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify(jsonData)
		}).done(function(data) {
			let fromDate = new Date(info.startStr);
			let toDate = new Date(info.endStr);
			let resoptid = data.reservation.resoptid;
			let ratio = data.reservation.ratio;
			let username = data.reservation.username;
			let title = username;
			let resOption = getResOption(resoptid);
			//alert(data.reservation.project);
			let project = info.project;
			let compound = info.compound;
			//let sample = info.sample;
			let batch = info.batch;
			let sampleType = info.sampleType;
			let specie = info.specie;

			if (!resoptid) { resoptid = 0; }
			if (!ratio) {
				ratio = 0;
			} else {
				title += ' - ' + ratio + '%';
			}

			if (!isNaN(fromDate.valueOf())) { // valid?
				let event = {
					title: title,
					start: fromDate,
					end: toDate,
					remark: data.reservation.remark,
					resOption: resOption.name,
					resOptionValue: resoptid,
					ratio: ratio,
					project: project,
					compound: compound,
					//sample: sample,
					batch: batch,
					sampleType: sampleType,
					specie: specie,
					username: username,
					resid: data.reservation.id,
					color: resOption.color
				};
				if (data.resourceIds && data.resourceIds.length > 0) {
					event.resourceIds = data.resourceIds;
				} else {
					event.resourceId = info.resource.id;
				}
				calendar.addEvent(event);
			} else {
				alert('Invalid date.');
			}
			refreshEvents(false);
		}).fail(function(data) {
			let json = data.responseJSON;
			if (data && json.statusCode === "403") {
				alert("Cannot register your reservation!\n" + json.errorMessage);
			} else {
				alert("Reservation Failed !");
			}
		});
	}

	function eventDone(info) {
		$.ajax({
			type: 'PUT',
			url: '/lers/reservation/' + info.extendedProps.resid + '/done'
		}).done(function(data) {
			if (data && data.reservation) {
				info.remove();
				if (!isNaN(data.reservation.fromTime.valueOf())) { // valid?
					let ratio = data.reservation.ratio;
					let username = data.reservation.username;
					let title = username;
					let resoptid = data.reservation.resoptid;
					let resOption = getResOption(resoptid);

					if (!resoptid) { resoptid = 0; }
					if (!ratio) {
						ratio = 0;
					} else {
						title += ' - ' + ratio + '%';
					}
					let event = {
						title: title,
						start: data.reservation.fromTime,
						end: data.reservation.toTime,
						remark: data.reservation.remark,
						//resOption: $("#reservation-option option[value=" + resoptid + "]").text(),
						resOption: resOption.name,
						resOptionValue: resoptid,
						ratio: ratio,
						username: username,
						resid: data.reservation.id,
						color: resOption.color
					};
					if (data.resourceIds && data.resourceIds.length > 0) {
						event.resourceIds = data.resourceIds;
						removeEvents(event.resourceIds, data.reservation.id);
					} else {
						event.resourceId = info.resource.id;
					}
					calendar.addEvent(event);
				}
			}
		}).fail(function(data) {
			alert("An error occurred while setting this reservation as finished.");
		});
	}

	function updateEvent(info) {
		let jsonData = {
			id: info.extendedProps.resid,
			resourceId: info.resource.id,
			selectOverlap: info.resource.extendedProps.selectOverlap,
			fromDate: info.startStr,
			toDate: info.endStr,
			remark: info.extendedProps.remark,
			resoptid: info.extendedProps.resOptionValue,
			ratio: info.extendedProps.ratio,
			project: info.extendedProps.project,
			compound: info.extendedProps.compound,
			//sample: info.extendedProps.sample,
			batch: info.extendedProps.batch,
			sampleType: info.extendedProps.sampleType,
			specie: info.extendedProps.specie
		};
		$.ajax({
			type: 'PUT',
			url: '/lers/reservation/update',
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify(jsonData)
		}).done(function(data) {
			let fromDate = new Date(info.startStr);
			let toDate = new Date(info.endStr);
			let resoptid = data.reservation.resoptid;
			let ratio = data.reservation.ratio;
			let username = data.reservation.username;
			let title = username;
			let resOption = getResOption(resoptid);
			let project = info.extendedProps.project;
			let compound = info.extendedProps.compound;
			//let sample = info.extendedProps.sample;
			let batch = info.extendedProps.batch;
			let sampleType = info.extendedProps.sampleType;
			let specie = info.extendedProps.specie;

			if (!resoptid) { resoptid = 0; }
			if (!ratio) {
				ratio = 0;
			} else {
				title += ' - ' + ratio + '%';
			}

			info.remove();
			if (!isNaN(fromDate.valueOf())) { // valid?
				let event = {
					title: title,
					start: fromDate,
					end: toDate,
					remark: data.reservation.remark,
					//resOption: $("#reservation-option option[value=" + data.reservation.resoptid + "]").text(),
					resOption: resOption.name,
					resOptionValue: resoptid,
					ratio: ratio,
					username: username,
					project: project,
					compound: compound,
					//sample: sample,
					batch: batch,
					sampleType: sampleType,
					specie: specie,
					resid: data.reservation.id,
					color: resOption.color
				};
				if (data.resourceIds && data.resourceIds.length > 0) {
					event.resourceIds = data.resourceIds;
					removeEvents(event.resourceIds, data.reservation.id);
				} else {
					event.resourceId = info.resource.id;
				}
				calendar.addEvent(event);
			} else {
				alert('Invalid date.');
			}
			window.eventInfo.setExtendedProp('isChanging', false);
		}).fail(function(data) {
			let json = data.responseJSON;
			let originalStartStr = $('#original-start-str').attr('value');
			let originalEndStr = $('#original-end-str').attr('value');
			window.eventInfo.setDates(originalStartStr, originalEndStr);
			window.eventInfo.setExtendedProp('isChanging', false);
			if (data && json.statusCode === "403") {
				alert("Cannot register your reservation!\n" + json.errorMessage);
			} else {
				alert("Reservation Failed !");
			}
		});
	}


	function registerSynthesisOrder(info) {
		let url;
		if (synthesisCalendar === true) {
			url = '/lers/synthesisorder/add'
		} else {
			url = '/lers/htmcorder/add'
		}
		let jsonData = {
			//resourceId: info.resource.id,
			title: info.title,
			requester: info.extendedProps.requester,
			project: info.extendedProps.project,
			link: info.extendedProps.link,
			fromDate: info.startStr,
			toDate: info.endStr,
			reqDate: info.extendedProps.reqDate,
			compound: info.extendedProps.compound,
			quantity: info.extendedProps.quantity,
			unit: info.extendedProps.unit,
			libraryOutcome: info.extendedProps.libraryOutcome,
			remarks: info.extendedProps.remarks
		};
		$.ajax({
			type: 'POST',
			url: url,
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify(jsonData)
		}).done(function(data) {
			let fromDate = new Date(info.startStr);
			let toDate = new Date(info.endStr);
			let title = info.title;
			let requester = info.extendedProps.requester;
			let project = info.extendedProps.project;
			let link = info.extendedProps.link;
			let libraryOutcome = info.extendedProps.libraryOutcome;
			let username = info.extendedProps.username;
			let reqDate = info.extendedProps.reqDate;
			let compound = info.extendedProps.compound;
			let quantity = info.extendedProps.quantity;
			let unit = info.extendedProps.unit;
			let remarks = info.extendedProps.remarks;

			if (!isNaN(fromDate.valueOf())) { // valid?
				let event = {
					title: title,
					start: fromDate,
					end: toDate,
					requester: requester,
					project: project,
					link: link,
					libraryOutcome: libraryOutcome,
					username: username,
					reqDate: reqDate,
					compound: compound,
					quantity: quantity,
					unit: unit,
					remarks: remarks,
				};
				event.id = info.id;
				calendar.addEvent(event);
			} else {
				alert('Invalid date.');
			}
			//refreshEvents(false);
			location.reload();
		}).fail(function(data) {
			let json = data.responseJSON;
			if (data && json.statusCode === "403") {
				alert("Cannot register your order!\n" + json.message);
			} else {
				alert("Register New Order Failed !");
			}
		});
	}

	function updateSynthesisOrder(info) {
		let url;
		if (synthesisCalendar === true) {
			url = '/lers/synthesisorder/update'
		} else {
			url = '/lers/htmcorder/update'
		}
		let jsonData = {
			resourceId: info.resource.id,
			title: info.title,
			requester: info.extendedProps.requester,
			project: info.extendedProps.project,
			link: info.extendedProps.link,
			fromDate: info.startStr,
			toDate: info.endStr,
			reqDate: info.extendedProps.reqDate,
			compound: info.extendedProps.compound,
			quantity: info.extendedProps.quantity,
			unit: info.extendedProps.unit,
			libraryOutcome: info.extendedProps.libraryOutcome,
			remarks: info.extendedProps.remarks,
			doneTime: info.extendedProps.doneTime
		};
		$.ajax({
			type: 'PUT',
			url: url,
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify(jsonData)
		}).done(function(data) {
			let fromDate = new Date(info.startStr);
			let toDate = new Date(info.endStr);
			let title = info.title;
			let requester = info.extendedProps.requester;
			let project = info.extendedProps.project;
			let link = info.extendedProps.link;
			let libraryOutcome = info.extendedProps.libraryOutcome;
			let username = info.extendedProps.username;
			let reqDate = info.extendedProps.reqDate;
			let compound = info.extendedProps.compound;
			let quantity = info.extendedProps.quantity;
			let unit = info.extendedProps.unit;
			let remarks = info.extendedProps.remarks;

			info.remove();
			if (!isNaN(fromDate.valueOf())) {
				let event = {
					title: title,
					start: fromDate,
					end: toDate,
					requester: requester,
					project: project,
					link: link,
					libraryOutcome: libraryOutcome,
					username: username,
					reqDate: reqDate,
					compound: compound,
					quantity: quantity,
					unit: unit,
				};
				event.id = info.id;
				calendar.addEvent(event);
			} else {
				alert('Invalid date.');
			}
			window.eventInfo.setExtendedProp('isChanging', false);
			location.reload();
		}).fail(function(data) {
			if (data.responseJSON.statusCode === "403") {
				alert("Cannot register your order!");
			} else {
				alert("Registration Failed !");
			}
		});
	}


	function getResOption(resOptionId) {
		if (resOptionId) {
			let resOptions = window.resoptions;
			for (let opt of resOptions) {
				if (opt.id === resOptionId) {
					return opt;
				}
			}
		}
		return {
			id: 0,
			name: '',
			color: '#9ba4a9'
		}
	}

	function getInstrumentOptions(instrId, selectedOptionVal) {
		$.ajax({
			type: 'GET',
			url: '/lers/instrument/' + instrId + '/resoptions',
			success: function(data) {
				let resoptionIds = data.resoptionIds;
				if (resoptionIds) {
					populateResoptionsDropdown(resoptionIds, selectedOptionVal);
				}
			},
			error: function(jqXhr) {
				alert('get resoptions for instrument failed.');
			}
		});
	}


	function getReservationRatioLeft(instrId, from, to, selectOverlap, startMoment, endMoment) {
		$.ajax({
			type: 'GET',
			url: '/lers/reservation/ratioleft/' + instrId + '/' + from + '/' + to,
			success: function(data) {
				let ratioLeft = data.ratioLeft;
				if (ratioLeft == 0) {
					$("#ratio-left-val").empty().append("% <span style=\" color:red\" >(" + ratioLeft + "% left)</span>");
				} else {
					$("#ratio-left-val").empty().append("% (" + ratioLeft + "% left)");
				}
				let step = data.step;
				if (step) {
					$("#ratio-input").attr("step", "" + step + "");
					$("#ratio-input").attr("min", "" + step + "");
				}
				let resOptionId = data.resoptid;
				if (resOptionId) {
					let textToKeep = $('#reservation-modal #reservation-option').find('option[value="' + resOptionId + '"]').text();
					$('#reservation-modal #reservation-option')
						.empty()
						.append('<option selected="selected" value="' + resOptionId + '">' + textToKeep + '</option>')
						;
					$('#reservation-modal #reservation-option').val(resOptionId).change();
				}
				let startDate = data.startDate;
				let endDate = data.endDate;
				if(selectOverlap == 2 && startDate && endDate){
					let startDate = new Date(data.startDate);
					let endDate = new Date(data.endDate);
					startMoment = moment(
						getDayFormatted(startDate.getDate()) + '/' +
						getMonthFormatted(startDate.getMonth()) + '/' +
						startDate.getFullYear() + ' ' +
						startDate.getHours() + ':' +
						startDate.getMinutes(), 'DD/MM/YYYY HH:mm');
					endMoment = moment(
						getDayFormatted(endDate.getDate()) + '/' +
						getMonthFormatted(endDate.getMonth()) + '/' +
						endDate.getFullYear() + ' ' +
						endDate.getHours() + ':' +
						endDate.getMinutes(), 'DD/MM/YYYY HH:mm');
					$("#start-datetime").attr('disabled', 'disabled');
					$("#end-datetime").attr('disabled', 'disabled');
				}else{
					$("#start-datetime").removeAttr('disabled');
					$("#end-datetime").removeAttr('disabled');
				}
				$('#start-datetime-picker').datetimepicker('date', startMoment);
				$('#end-datetime-picker').datetimepicker('date', endMoment);						
			},
			error: function(jqXhr) {
				console.log('get reservation left failed.');
			}
		});
	}

	function getBatchesByCompound(compoundName, selectedBatch) {
		$.ajax({
			type: 'GET',
			url: '/lers/reservation/batch/batches/' + compoundName,
			success: function(data) {
				let batches = data.batches;
				if (batches) {
					populateBatchesDropdown(batches, selectedBatch);
				}
			},
			error: function(jqXhr) {
				alert('get batches for compound failed.');
			}
		});
	}

	function getCompoundByName(compoundName) {
		$.ajax({
			type: 'GET',
			url: '/lers/reservation/substance/' + compoundName,
			success: function(data) {
				let compound = data.compound;
				if (compound) {
					//alert("success");
					$('#reservation-modal button.save').show();
				} else {
					$("#reservation-usage-compounds-option").css("color", "#f54b42");
					$('#reservation-modal button.save').hide();
				}
			},
			error: function(jqXhr) {
				$("#reservation-usage-compounds-option").css("color", "#f54b42");
				$('#reservation-modal button.save').hide();
			}
		});
	}

	/*function getBatchesBySampleExternalReference(externalReference, selectedBatch) {
		$.ajax({
			type: 'GET',
			url: '/lers/reservation/batch/batches/sample/' + externalReference,
			success: function(data) {
				let batches = data.batches;
				if (batches) {
					populateBatchesDropdown(batches, selectedBatch);
				}
			},
			error: function(jqXhr) {
				alert('get batches for external reference failed.');
			}
		});
	}*/

	function populateBatchesDropdown(batches, selectedBatch) {
		$("#reservation-usage-batches-value").val("");
		$("#reservation-usage-batches-option option").remove();
		if (batches && batches.length > 0) {
			$("#reservation-usage-batches-option").append(new Option('Choose a Batch...'));
			for (let batche of batches) {
				$("#reservation-usage-batches-option").append(new Option(batche, batche));
			}
			if (selectedBatch) {
				//$('#reservation-usage-batches-option').val(selectedBatch).prop('selected', true);
				$('#reservation-usage-batches-value').val(selectedBatch);
			}
		}
	}

	function populateResoptionsDropdown(resOptionIds, selectedOptionVal) {
		let resoptions = window.resoptions;
		$("#reservation-option option").remove();
		$("#reservation-option").removeAttr('disabled');
		//TODO if tissue type label Choose tissue instead
		$("#reservation-option").append(new Option('Choose Option...', '0'));

		if (resOptionIds && resOptionIds.length > 0) {
			for (let resoption of resoptions) {
				if (resOptionIds.includes(resoption.id)) {
					$("#reservation-option").append(new Option(resoption.name, resoption.id));
				}
			}
			if (selectedOptionVal && selectedOptionVal >= 0) {
				$('#reservation-modal #reservation-option').val(selectedOptionVal);
			}
		} else {
			$("#reservation-option").attr('disabled', 'disabled');
		}
	}

	function getSynthesysOrder() {
		let startDate = calendar.view.currentStart.toUTCString();
		let endDate = calendar.view.currentEnd.toUTCString();
		let url;
		if (synthesisCalendar === true) {
			url = '/lers/synthesisorder/all/inrange?' + '&startDate=' + startDate + '&endDate=' + endDate
		} else {
			url = '/lers/htmcorder/all/inrange?' + '&startDate=' + startDate + '&endDate=' + endDate
		}
		run_waitMe();
		$.ajax({
			type: 'GET',
			url: url,
			success: function(data) {
				let orders = data.synthesisorders;
				if (synthesisCalendar === true) {
					orders = data.synthesisorders;
				} else {
					orders = data.htmcorders;
				}
				for (let synthesisorder of orders) {
					let title = synthesisorder.title;
					let username = synthesisorder.username;
					let displayTitle = title + ' (recorded by ' + username + ')';
					let requester = synthesisorder.requester;
					let start = new Date(synthesisorder.fromTime);
					let end = new Date(synthesisorder.toTime);
					let reqDate = new Date(synthesisorder.requestTime);
					let project = synthesisorder.project;
					let compound = synthesisorder.compound;
					let quantity = synthesisorder.quantity;
					let unit = synthesisorder.unit;
					let link = synthesisorder.link;
					let eventColor = synthesisorder.eventColor;
					let libraryOutcome = synthesisorder.libraryoutcome;
					let remarks = synthesisorder.remarks;
					let doneTime;
					if (synthesisorder.doneTime) {
						doneTime = new Date(synthesisorder.doneTime);
					}
					let connectedUser = synthesisorder.connectedUser;
					if (connectedUser) {
						window.connectedUser = synthesisorder.connectedUser;
					}
					let event = {
						link: link,
						title: displayTitle,
						libraryOutcome: libraryOutcome,
						start: synthesisorder.fromTime,
						end: synthesisorder.toTime,
						reqDate: reqDate,
						resourceId: synthesisorder.id,
						resid: synthesisorder.id,
						username: username,
						project: project,
						compound: compound,
						quantity: quantity,
						unit: unit,
						requester: requester,
						color: eventColor,
						remarks: remarks,
						doneTime: doneTime,
						connectedUser: connectedUser
					};

					if (isEventInCalendar(event.resourceId, event.resid) === false) {
						calendar.addEvent(event);
					}
				}
				$("#calendar").waitMe("hide");
				var events = synthesisCalendar ? calendar.getResourceById('1').getEvents() : calendar.getResourceById('2').getEvents();
				var eventTitles = events.map(function(event) {
					return event.title
				});

			},
			error: function(jqXhr) {
				$("#calendar").waitMe("hide");
				alert('get synthesis order failed.');
			}
		});
	}

	function getReservationsForGroup(groupName, displayMyReservation) {
		let startDate = calendar.view.currentStart.toUTCString();
		let endDate = calendar.view.currentEnd.toUTCString();
		run_waitMe();
		$.ajax({
			type: 'GET',
			url: '/lers/reservation/group?groupName=' + groupName + '&startDate=' + startDate + '&endDate=' + endDate,
			success: function(data) {
				let reservations = data.reservations;
				let reservationUsages = data.reservationUsages;
				for (let reservation of reservations) {
					let username = reservation.username;
					let title = username;
					let ratio = reservation.ratio;
					reservation.fromTime = new Date(reservation.fromTime);
					reservation.toTime = new Date(reservation.toTime);
					if (data.applyInstrIdOffset === true) {
						reservation.instrid = reservation.instrid + data.instrIdOffset;
					}
					if (data.connectedUser) {
						window.connectedUser = data.connectedUser;
					}
					if (ratio && ratio > 0) {
						title += ' - ' + ratio + '%';
					}
					let project;
					let compound;
					//let sample;
					let batch;
					for (let reservationUsage of reservationUsages) {
						if (reservationUsage.reservationId == reservation.id) {
							project = reservationUsage.project;
							compound = reservationUsage.compound;
							//sample = reservationUsage.sample;
							batch = reservationUsage.batch;
						}
					}
					let resOption = getResOption(reservation.resoptid);
					let deputies = reservation.deputies;
					let event = {
						title: title,
						start: reservation.fromTime,
						end: reservation.toTime,
						resourceId: reservation.instrid,
						remark: reservation.remark,
						resid: reservation.id,
						resOption: resOption.name,
						resOptionValue: reservation.resoptid,
						ratio: ratio,
						username: username,
						color: resOption.color,
						project: project,
						compound: compound,
						//sample: sample,
						batch: batch,
						deputies: deputies
					};

					if (isEventInCalendar(event.resourceId, event.resid) === false) {
						if (displayMyReservation) {
							if (username == data.connectedUser) {
								calendar.addEvent(event);
							}
						} else {
							calendar.addEvent(event);
						}
					}
				}
				$("#calendar").waitMe("hide");
			},
			error: function(jqXhr) {
				$("#calendar").waitMe("hide");
				alert('get reservations failed.');
			}
		});
	};

	function getReservationUsageByResId(reservationId) {
		$.ajax({
			type: 'GET',
			url: '/lers/reservation/reservationUsage/reservationId/' + reservationId,
			success: function(data) {
				let reservationUsage = data.reservationUsages;
				let project = reservationUsage.project;
				$('#reservation-usage-projects-option').val(project);
				let compound = reservationUsage.compound;
				let selectedBatch = reservationUsage.batch;
				let sampleType = reservationUsage.sampleType;
				$('#reservation-usage-sampleType-value').val(sampleType);
				let specie = reservationUsage.specie;
				$('#reservation-usage-specie-value').val(specie);
				if (compound) {
					$('#reservation-usage-compounds-option').val(compound);
					let batches = getBatchesByCompound(compound, selectedBatch);
					populateBatchesDropdown(batches, selectedBatch);
				}
			},
			error: function(jqXhr) {
				alert('get reservation usage for failed.');
			}
		});
	}

	function isEventInCalendar(resourceId, reservationId) {
		if (resourceId) {
			let resource = calendar.getResourceById(resourceId);
			if (resource) {
				let events = resource.getEvents();
				for (let event of events) {
					let resid = event.extendedProps.resid;
					if (resid && resid === reservationId) {
						return true;
					}
				}
			}
		}

		return false;
	}

	function removeEvents(resourceIds, reservationId) {
		if (resourceIds && Array.isArray(resourceIds) && reservationId) {
			for (let resourceId of resourceIds) {
				let resource = calendar.getResourceById(resourceId);
				if (resource) {
					let events = resource.getEvents();
					if (events) {
						for (let event of events) {
							if (event && event.extendedProps.resid === reservationId) {
								event.remove();
							}
						}
					}
				}
			}
		}
	}

	function refreshEvents(displayMyReservation) {
		$('#calendar .fc-datagrid-body .fc-datagrid-expander.events-loaded').each(function() {
			calendar.removeAllEvents();
			if (isNotReservationCalendar === true) {
				getSynthesysOrder();
				//location.reload();
			} else {
				if (displayMyReservation) {
					getReservationsForGroup($(this).next().text(), true);
				} else {
					getReservationsForGroup($(this).next().text(), false);
				}
			}
		});
		scrollToToday();
	}

	function clearReservationFields() {
		$('#start-datetime-picker').datetimepicker('clear');
		$('#end-datetime-picker').datetimepicker('clear');
		$('#reservation-comment').val('');
		$('#reservation-option').val(0);
		$('#reservation-usage-projects-option').val('');
		$('#reservation-usage-compounds-option').val('');
		//$('#reservation-usage-samples-option').val('');
		populateBatchesDropdown(new Array(), null);
	}

	function clearSynthesisOrderFields() {
		$('#synthesisorder-id').attr('value', '0');
		$('#order-start-datetime-picker').datetimepicker('clear');
		$('#order-end-datetime-picker').datetimepicker('clear');
		$('#req-datetime-picker').datetimepicker('clear');
		$('#synthesisorder-title').val('');
		$('#synthesisorder-remarks').val('');
		$('#synthesisorder-requester-option').val('');
		$('#synthesisorder-projects-option').val('');
		$('#synthesisorder-coumpounds-option').val('');
		$('#synthesisorder-link').val('');
		$('#synthesisorder-quantity').val('');
		$("#synthesisorder-unit option").each(function() {
			$(this).removeAttr("selected");
			if ($(this).val() == '0') {
				$(this).attr("selected", "selected");
			}
		});
		$("#structBtn").css("display", "none");
		$("#htmcDescriptionLinkBtn").css("display", "none");
		$("#orderDescriptionLinkBtn").css("display", "none");
	}

	function clearHtmcOrderFields() {
		$('#htmcorder-id').attr('value', '0');
		$('#htmc-start-datetime-picker').datetimepicker('clear');
		$('#htmc-end-datetime-picker').datetimepicker('clear');
		$('#req-datetime-picker').datetimepicker('clear');
		$('#htmcorder-title').val('');
		$('#htmcorder-requester-option').val('');
		$('#htmcorder-projects-option').val('');
		$('#htmcorder-coumpounds-option').val('');
		$('#htmcorder-link').val('');
		$('#htmcorder-link').val('');
		$('#htmcorder-quantity').val('');
		$('#htmcorder-libraryOutcome').val('');
		$("#htmcorder-unit option").each(function() {
			$(this).removeAttr("selected");
			if ($(this).val() == '0') {
				$(this).attr("selected", "selected");
			}
		});
		$("#structBtn").css("display", "none");
	}

	function updateButtonsWithTime(startTime, endTime) {
		let now = Date.now();
		if (now < startTime) {
			$('#reservation-modal button.delete').show();
			$('#reservation-modal button.done').hide();
			$('#reservation-modal button.save').show();
		} else if (now > startTime && now < endTime) {
			$('#reservation-modal button.delete').hide();
			$('#reservation-modal button.done').show();
			$('#reservation-modal button.save').show();
		} else {
			$('#reservation-modal button.delete').hide();
			$('#reservation-modal button.done').hide();
			$('#reservation-modal button.save').hide();
		}
	}

	let defaultTimelineView = getCookie('timeline_view');
	if (defaultTimelineView.length == 0) {
		defaultTimelineView = 'resourceTimelineWeek';
	}
	// Calendar
	console.log('Creating calendar');
	let stepping = 5;
	let calendarEl = document.getElementById('calendar');
	let calendar;
	if (isNotReservationCalendar === true) {
		let groupWarningMsg = "You can't book on this calendar as you are not part of the allowed group.";
		synthesisCalendar = menuId === 'menu-synthesisorder';
		//let color = synthesisCalendar === true ? '#ef7812' : '#ff3300';
		calendar = new FullCalendar.Calendar(calendarEl, {
			locale: 'en-GB',
			timeZone: 'local',
			selectable: true,
			nowIndicator: true,
			resourceAreaWidth: "28%",
			//eventColor: color,
			schedulerLicenseKey: 'CC-Attribution-NonCommercial-NoDerivatives',
			customButtons: {
				refreshButton: {
					text: 'refresh',
					click: function() {
						refreshEvents(false);
					}
				},
				addButton: {
					text: 'add',
					click: function() {
						clearSynthesisOrderFields();
						clearHtmcOrderFields();
						$('#synthesisorder-modal button.delete').hide();
						$('#synthesisorder-modal button.done').hide();
						$('#htmcorder-modal button.delete').hide();
						$('#htmcorder-modal button.done').hide();
						var allowedToBook = checkIfAllowedToBook();
						if (allowedToBook == false) {
							alert(groupWarningMsg);
						} else {
							window.eventInfo = calendar.getEvents()[0];
							if (synthesisCalendar === true) {
								$('#synthesisorder-modal').modal('show');
							} else {
								$('#htmcorder-modal').modal('show');
							}
						}
					}
				},
			},
			headerToolbar: {
				left: 'today prev,next refreshButton addButton',
				center: 'title',
				right: 'resourceTimelineWeek,resourceTimelineMonth,quarterly,halfyear,year'
			},
			aspectRatio: 1.6,
			initialView: defaultTimelineView,
			views: {
				resourceTimelineWeek: {
					titleFormat: { year: 'numeric', month: 'short', day: '2-digit' },
					slotMinTime: '06:00',
					slotMaxTime: '21:00',
					slotLabelFormat: [
						{ weekday: 'short', year: 'numeric', month: 'short', day: 'numeric' }, // top level of text
						{ hour: 'numeric' } // lower level of text
					]
				},
				resourceTimelineMonth: {
					titleFormat: { year: 'numeric', month: 'short', /*weekday: 'short', */day: '2-digit' },
					slotLabelFormat: [
						{ weekday: 'short', month: 'short', day: 'numeric' }
					]
				},
				quarterly: {
					type: 'resourceTimeline',
					duration: { months: 3 }
				},
				halfyear: {
					type: 'resourceTimeline',
					duration: { months: 6 }
				},
				year: {
					type: 'resourceTimeline',
					duration: { months: 12 }
				}
			},
			businessHours: {
				// days of week. an array of zero-based day of week integers (0=Sunday)
				daysOfWeek: [0, 1, 2, 3, 4, 5, 6],
				firstDay: 1,
				startTime: '06:00', // a start time (00 in this example)
				endTime: '21:00', // an end time (24 in this example)
			},
			weekNumberCalculation: 'ISO', // first day of week is Monday
			resources: resources,
			resourceGroupField: 'groupname',
			resourcesInitiallyExpanded: false,
			resourceOrder: '-hackOrder',
			//resourceOrder: '-id',
			//filterResourcesWithEvents: true,
			selectOverlap: true,

			resourceLabelDidMount: function(arg) {
				//for now do nothing
			},
			select: function(info) {
				clearSynthesisOrderFields();
				clearHtmcOrderFields();
				let startDate = new Date(Date.parse(info.startStr))
				let startMoment = moment(
					getDayFormatted(startDate.getDate()) + '/' +
					getMonthFormatted(startDate.getMonth()) + '/' +
					startDate.getFullYear() + ' ' +
					startDate.getHours() + ':' +
					startDate.getMinutes(), 'DD/MM/YYYY HH:mm');

				let endDate = new Date(Date.parse(info.endStr))
				let endMoment = moment(
					getDayFormatted(endDate.getDate()) + '/' +
					getMonthFormatted(endDate.getMonth()) + '/' +
					endDate.getFullYear() + ' ' +
					endDate.getHours() + ':' +
					endDate.getMinutes(), 'DD/MM/YYYY HH:mm');
				if (synthesisCalendar === true) {
					$('#order-start-datetime-picker').datetimepicker('date', startMoment);
					$('#order-end-datetime-picker').datetimepicker('date', endMoment);
				} else {
					$('#htmc-start-datetime-picker').datetimepicker('date', startMoment);
					$('#htmc-end-datetime-picker').datetimepicker('date', endMoment);
				}
				let title = info.resource.title
				let realTitle = title.split('(')[0];
				$('#synthesisorderTitle').html('<div>Synthesis Order </div>');
				$('#synthesisorder-title').val(realTitle);
				$('#htmcorderTitle').html('<div>HTMC Order </div>');
				$('#htmcorder-title').val(realTitle);
				updateButtonsWithTime(startDate.getTime(), endDate.getTime());
				$('#synthesisorder-link').val('');
				$('#htmcorder-link').val('');

				$('#synthesisorder-modal button.delete').hide();
				$('#synthesisorder-modal button.done').hide();
				$('#synthesisorder-id').attr('value', '0');
				$('#synthesisorder-usage-projects-option').val('');
				$('#synthesisorder-usage').css('display', 'none');

				$('#htmcorder-modal button.delete').hide();
				$('#htmcorder-modal button.done').hide();
				$('#htmcorder-id').attr('value', '0');
				$('#htmcorder-usage-projects-option').val('');
				$('#htmcorder-usage').css('display', 'none');
				var allowedToBook = checkIfAllowedToBook();
				if (allowedToBook == false) {
					alert(groupWarningMsg);
				} else {
					if (startDate < new Date()) {
						alert("Please don't select a previous date.");
					} else {
						if (synthesisCalendar === true) {
							$('#synthesisorder-modal').modal('show');
						} else {
							$('#htmcorder-modal').modal('show');
						}
					}
				}
				window.eventInfo = calendar.getEvents()[0];
				// set cookie for current view
				setCookie('timeline_view', calendar.view.type, 365);
			},
			eventClick: function(info) {
				var allowedToBook = checkIfAllowedToBook();
				if (allowedToBook == false) {
					alert(groupWarningMsg);
					return;
				}
				window.eventInfo = info.event;
				window.eventInfo.resource = info.event.getResources()[0];
				let startDate = new Date(Date.parse(info.event.start))
				let startMoment = moment(
					getDayFormatted(startDate.getDate()) + '/' +
					getMonthFormatted(startDate.getMonth()) + '/' +
					startDate.getFullYear() + ' ' +
					startDate.getHours() + ':' +
					startDate.getMinutes(), 'DD/MM/YYYY HH:mm');

				let endDate = new Date(Date.parse(info.event.end))
				let endMoment = moment(
					getDayFormatted(endDate.getDate()) + '/' +
					getMonthFormatted(endDate.getMonth()) + '/' +
					endDate.getFullYear() + ' ' +
					endDate.getHours() + ':' +
					endDate.getMinutes(), 'DD/MM/YYYY HH:mm');
				let reqDate = new Date(Date.parse(info.event.extendedProps.reqDate))
				let reqMoment = moment(
					getDayFormatted(reqDate.getDate()) + '/' +
					getMonthFormatted(reqDate.getMonth()) + '/' +
					reqDate.getFullYear() + ' ' +
					reqDate.getHours() + ':' +
					reqDate.getMinutes(), 'DD/MM/YYYY HH:mm');
				let title = info.event.title;
				let realTitle = title.split('(')[0];
				let project = info.event.extendedProps.project;
				let libraryOutcome = info.event.extendedProps.libraryOutcome;
				let link = info.event.extendedProps.link;
				let requester = info.event.extendedProps.requester;
				let compound = info.event.extendedProps.compound;
				let quantity = info.event.extendedProps.quantity;
				let unit = info.event.extendedProps.unit;
				let remarks = info.event.extendedProps.remarks;

				$('#req-datetime-picker').datetimepicker('date', reqMoment);
				if (synthesisCalendar === true) {
					$('#order-start-datetime-picker').datetimepicker('date', startMoment);
					$('#order-end-datetime-picker').datetimepicker('date', endMoment);
				} else {
					$('#htmc-start-datetime-picker').datetimepicker('date', startMoment);
					$('#htmc-end-datetime-picker').datetimepicker('date', endMoment);
				}
				updateButtonsWithTime(startDate.getTime(), endDate.getTime());

				if (compound) {
					getMolStructureByCoumpound(compound, null, null);
				}

				if (synthesisCalendar === true) {
					$('#synthesisorderTitle').html('<div>Synthesis Order </div>');
					$('#synthesisorder-title').val(realTitle);
					checkOrderDescription(link);
					$('#synthesisorder-projects-option').val(project);
					$('#synthesisorder-link').val(link);
					$('#synthesisorder-requester-option').val(requester);
					$('#synthesisorder-coumpounds-option').val(compound);
					$('#synthesisorder-quantity').val(quantity);
					$("#synthesisorder-unit option").each(function() {
						if (unit && $(this).val() == unit) {
							$(this).attr("selected", "selected");
						}
					});
					$('#synthesisorder-remarks').val(remarks);
					$('#synthesisorder-id').attr('value', info.event.extendedProps.resid);
					$('#synthesisorder-modal').modal('show');
				} else {
					$('#htmcorderTitle').html('<div>HTMC Order </div>');
					$('#htmcorder-title').val(realTitle);
					checkHtmcDescription(link);
					$('#htmcorder-projects-option').val(project);
					$('#htmcorder-link').val(link);
					$('#htmcorder-requester-option').val(requester);
					//$('#htmcorder-coumpounds-option').val(compound);
					//$('#htmcorder-quantity').val(quantity);
					$('#htmcorder-libraryOutcome').val(libraryOutcome);
					$("#htmcorder-unit option").each(function() {
						if (unit && $(this).val() == unit) {
							$(this).attr("selected", "selected");
						}
					});
					$('#htmcorder-id').attr('value', info.event.extendedProps.resid);
					$('#htmcorder-modal').modal('show');
				}

				setCookie('timeline_view', calendar.view.type, 365);
			},
			eventChange: function(info) {
				let event = info.event;
				if (event && event._instance && event.extendedProps) {
					let isChanging = event.extendedProps.isChanging;
					if (isChanging === true) {
						let el = event.extendedProps.element;
						updateTooltip(el, event);
					}
				}
			},
			eventDidMount: function(info) {
				let el = $(info.el);
				el.attr('data-toggle', 'tooltip');
				el.attr('data-placement', 'top');
				updateTooltip(el, info.event);
				el.tooltip({
					html: true
				});
				info.event.setExtendedProp('element', el);
			}
		});

	} else {
		synthesisCalendar = false;
		calendar = new FullCalendar.Calendar(calendarEl, {
			locale: 'en-GB',
			timeZone: 'local',
			selectable: true,
			nowIndicator: true,
			resourceAreaWidth: "15%",
			schedulerLicenseKey: 'CC-Attribution-NonCommercial-NoDerivatives',
			customButtons: {
				refreshButton: {
					text: 'refresh',
					click: function() {
						refreshEvents(false);
					}
				},
				myReservartionButton: {
					text: 'my reservations',
					click: function() {
						refreshEvents(true);
						//$(".fc-allReservationButton-button").show();
					}
				},
				allReservationButton: {
					text: 'all reservations',
					click: function() {
						refreshEvents(false);
						//$(this).hide();
					}
				}
			},
			headerToolbar: {
				left: 'today prev,next refreshButton myReservartionButton allReservationButton',
				center: 'title',
				right: 'resourceTimelineDay,resourceTimelineWeek,resourceTimelineMonth'
			},
			aspectRatio: 1.6,
			initialView: defaultTimelineView,
			views: {
				resourceTimelineDay: {
					slotDuration: '00:30', // 10 min
					slotMinTime: '06:00',
					slotMaxTime: '21:00',
					slotMinWidth: 10,
					slotLabelFormat: [
						{ weekday: 'short', year: 'numeric', month: 'short', day: 'numeric' }, // top level of text
						{ hour: 'numeric', minute: '2-digit' } // lower level of text
					],
				},
				resourceTimelineWeek: {
					titleFormat: { year: 'numeric', month: 'short', day: '2-digit' },
					slotMinTime: '06:00',
					slotMaxTime: '21:00',
					slotLabelFormat: [
						{ weekday: 'short', year: 'numeric', month: 'short', day: 'numeric' }, // top level of text
						{ hour: 'numeric' } // lower level of text
					]
				},
				resourceTimelineMonth: {
					titleFormat: { year: 'numeric', month: 'short', /*weekday: 'short', */day: '2-digit' },
					slotLabelFormat: [
						{ weekday: 'short', month: 'short', day: 'numeric' }
					]
				},
				quarterly: {
					type: 'resourceTimeline',
					duration: { months: 3 }
				},
				halfyear: {
					type: 'resourceTimeline',
					duration: { months: 6 }
				},
				year: {
					type: 'resourceTimeline',
					duration: { months: 12 }
				}
			},
			businessHours: {
				// days of week. an array of zero-based day of week integers (0=Sunday)
				daysOfWeek: [0, 1, 2, 3, 4, 5, 6],
				firstDay: 1,
				startTime: '06:00', // a start time (00 in this example)
				endTime: '21:00', // an end time (24 in this example)
			},
			weekNumberCalculation: 'ISO', // first day of week is Monday
			resourceGroupField: 'groupname',
			resourcesInitiallyExpanded: false,
			resourceOrder: 'title,id',
			resources: resources,
			selectOverlap: true,
			resourceLabelDidMount: function(arg) {
				let selectOverlap = arg.resource.extendedProps.selectOverlap;
				let ratioDisplay = 'none';
				let isPriorityUsersSet = arg.resource._resource.extendedProps.isPriorityUsersSet;
				let ratioVal = 0;
				if (selectOverlap == 1 || selectOverlap == 2) {
					$(arg.el).addClass('select-overlap');
					ratioDisplay = 'flex';
					ratioVal = arg.resource.extendedProps.stepIncrement;
				}
				$(arg.el).css('cursor', 'default');
				if(isPriorityUsersSet == true){
					let resourceId = parseInt(arg.el.dataset.resourceId);
					$(`.fc-resource[data-resource-id="${resourceId}"]`).addClass('prioresource');
					$(`.fc-resource[data-resource-id="${resourceId}"]`).attr('title', 'This instrument got priority users');
				}
				$(arg.el).on('click', function(ev) {
					let startMoment = moment();
					let endMoment = moment();
					endMoment.add(30, 'minutes');
					$('#reservationModalTitle').html('<div>Reservation Details for</div><div>' + arg.resource.title + '</div>');
					$('#start-datetime-picker').datetimepicker('date', startMoment);
					$('#end-datetime-picker').datetimepicker('date', endMoment);
					$('#reservation-modal .reservation-ratio').css('display', ratioDisplay);
					$('#reservation-modal #ratio-input').val(ratioVal);
					updateButtonsWithTime(parseInt(startMoment.format('x')), parseInt(endMoment.format('x')));
					$('#reservation-modal button.delete').hide();
					$('#reservation-modal button.done').hide();

					$('#reservation-usage').css('display', 'none');
					$('#reservation-usage-extra').css('display', 'none');
					var instId = info.resource.id;
					var allowedInstForResaUsg = info.resource.extendedProps.allowedInstForResaUsg;
					if ($.inArray(instId, allowedInstForResaUsg) !== -1) {
						$('#reservation-usage-projects-option').val('');
						$('#reservation-usage-compounds-option').val('');
						//$('#reservation-usage-samples-option').val('');
						$('#reservation-usage-sampleType-value').val('');
						$('#reservation-usage-specie-value').val('');
						populateBatchesDropdown(new Array(), null);
						if(instId == 1231){ //Tissue LyzerII Qiagen 							
							$('#reservation-usage-extra').css('display', 'block');
							$('#reservation-usage').css('display', 'none');
						}else{						
							$('#reservation-usage').css('display', 'block');
							$('#reservation-usage-extra').css('display', 'none');
						}
					}
					//$('#reservation-usage-batches-value').val("");
					$('#reservation-modal').modal('show');
					let eventInfo = {
						title: arg.resource.title,
						resourceId: arg.resource.id
					}
					window.eventInfo = eventInfo;
					window.eventInfo.resource = arg.resource;
				});
			},
			select: function(info) {
				let startDate = new Date(Date.parse(info.startStr))
				let endDate = new Date(Date.parse(info.endStr))
				let startMoment = moment(
					getDayFormatted(startDate.getDate()) + '/' +
					getMonthFormatted(startDate.getMonth()) + '/' +
					startDate.getFullYear() + ' ' +
					startDate.getHours() + ':' +
					startDate.getMinutes(), 'DD/MM/YYYY HH:mm');
				let endMoment = moment(
					getDayFormatted(endDate.getDate()) + '/' +
					getMonthFormatted(endDate.getMonth()) + '/' +
					endDate.getFullYear() + ' ' +
					endDate.getHours() + ':' +
					endDate.getMinutes(), 'DD/MM/YYYY HH:mm');
				getInstrumentOptions(info.resource.id, -1);
				clearReservationFields();
				let selectOverlap = info.resource.extendedProps.selectOverlap;
				let ratioDisplay = 'none';
				let ratioVal = 0;
				let datesReset = false;
				let highlightComment = info.resource.extendedProps.highlightComment;
				if (selectOverlap && selectOverlap != 0) {
					ratioDisplay = 'flex';
					ratioVal = info.resource.extendedProps.stepIncrement;
					getReservationRatioLeft(info.resource.extendedProps.instrumentID, Date.parse(startDate), Date.parse(endDate), selectOverlap, startMoment, endMoment);
					$('#reservation-type-overlap').val('yes');
					$('#step-increment-val').val(ratioVal);
					datesReset = true;
				} else {
					$('#reservation-type-overlap').val('no');
				}
				let ratioComment = info.resource.extendedProps.ratioComment;
				if(!datesReset){
					$('#start-datetime-picker').datetimepicker('date', startMoment);	
					$('#end-datetime-picker').datetimepicker('date', endMoment);
				}
				$('#reservationModalTitle').html('<div>Reservation Details for</div><div>' + info.resource.title + '</div>');
				updateButtonsWithTime(startDate.getTime(), endDate.getTime());
				$('#reservation-modal button.delete').hide();
				$('#reservation-modal button.done').hide();
				$('#reservation-modal #ratio-input').val(ratioVal);
				$('#reservation-modal .reservation-ratio').css('display', ratioDisplay);
				$('#reservation-id').attr('value', '0');
				$('#reservation-modal #ratioComment-value').text(ratioComment);
				if(highlightComment == 1){
					$('#reservation-modal #ratioComment-value').addClass("highlighted");
				}else{
					$('#reservation-modal #ratioComment-value').removeClass("highlighted");
				}
				$('#reservation-usage-projects-option').val('');
				$('#reservation-usage-compounds-option').val('');
				//$('#reservation-usage-samples-option').val('');
				populateBatchesDropdown(new Array(), null);
				$('#reservation-usage').css('display', 'none');
				$('#reservation-usage-extra').css('display', 'none');
				//var instId = info.resource.id;
				var instId = info.resource.extendedProps.instrumentID;
				var allowedInstForResaUsg = info.resource.extendedProps.allowedInstForResaUsg;
				if ($.inArray(instId, allowedInstForResaUsg) !== -1) {
					if(instId == 1231){ //Tissue LyzerII Qiagen
						$('#reservation-usage').css('display', 'none');
						$('#reservation-usage-extra').css('display', 'block');
					}else{						
						$('#reservation-usage').css('display', 'block');
						$('#reservation-usage-extra').css('display', 'none');
					}
				}
				//restriction for climate chambers
				var allowedToBook = false;
				var instrumentEmployeeGroups = info.resource.extendedProps.instrumentEmployeeGroups ? info.resource.extendedProps.instrumentEmployeeGroups.split(';') : [];
				var instrumentEmployeeGroupsProvided = instrumentEmployeeGroups.length > 0;
				//instrumentEmployeeGroups.push("Spirit");
				var userGroups = $("#userGroupsValues").text() ? $("#userGroupsValues").text().split(';') : [];
				$.each(instrumentEmployeeGroups, function(i, val) {
					if ($.inArray(val, userGroups) !== -1) {
						allowedToBook = true;
					}
				});
				if (instrumentEmployeeGroupsProvided == true && allowedToBook == false) {
					alert("You can't book this instrument as you are not part of the allowed groups.");
				} else {// allowed group not provided for the instrument so open bar!
					if (startDate < new Date()) {
						alert("Please don't select a previous date.");
					} else {
						$('#reservation-usage-batches-value').val("");
						$('#reservation-modal').modal('show');
					}
				}
				window.eventInfo = info;

				// set cookie for current view
				setCookie('timeline_view', calendar.view.type, 365);
			},
			eventClick: function(info) {
				$(info.el).tooltip('hide');
				// check if the connected user is owner of the event
				let username = info.event.extendedProps.username;
				let deputies = info.event.extendedProps.deputies;
				if (deputies) {
					let match = false;
					let deputiesArray = new Array()
					for (let i = 0; i < deputies.length; i++) {
						deputiesArray[i] = deputies[i].trim();
					}
					if (username) {
						match = window.connectedUser.toLowerCase() == username.toLowerCase() || deputiesArray.includes(window.connectedUser.toUpperCase());
					}
					if (!username || !match) {
						alert('You must be the owner of the reservation to modify it.');
						return;
					}
				} else {
					if (username && window.connectedUser.toLowerCase() !== username.toLowerCase()) {
						alert('You must be the owner of the reservation to modify it.');
						return;
					}
				}

				window.eventInfo = info.event;
				window.eventInfo.resource = info.event.getResources()[0];
				let startDate = new Date(Date.parse(info.event.startStr))
				let startMoment = moment(
					getDayFormatted(startDate.getDate()) + '/' +
					getMonthFormatted(startDate.getMonth()) + '/' +
					startDate.getFullYear() + ' ' +
					startDate.getHours() + ':' +
					startDate.getMinutes(), 'DD/MM/YYYY HH:mm');

				let endDate = new Date(Date.parse(info.event.endStr))
				let endMoment = moment(
					getDayFormatted(endDate.getDate()) + '/' +
					getMonthFormatted(endDate.getMonth()) + '/' +
					endDate.getFullYear() + ' ' +
					endDate.getHours() + ':' +
					endDate.getMinutes(), 'DD/MM/YYYY HH:mm');

				let remark = info.event.extendedProps.remark;
				let resOptionValue = info.event.extendedProps.resOptionValue;
				let ratio = info.event.extendedProps.ratio;

				if (!remark) { remark = ''; }
				if (!resOptionValue) { resOptionValue = 0; }
				if (!ratio) { ratio = 0; }

				let selectOverlap = info.event.resource.extendedProps.selectOverlap;
				let ratioComment = info.event.resource.extendedProps.ratioComment;
				let highlightComment = info.event.resource.extendedProps.highlightComment;
				let ratioDisplay = 'none';
				let ratioVal = 0;
				let datesReset = false;
				if (selectOverlap && selectOverlap != 0) {
					ratioDisplay = 'flex';
					ratioVal = info.event.resource.extendedProps.stepIncrement;
					//getReservationRatioLeft(info.event.resource.id, Date.parse(startDate), Date.parse(endDate), selectOverlap, startMoment, endMoment);
					getReservationRatioLeft(info.event.resource.extendedProps.instrumentID, Date.parse(startDate), Date.parse(endDate), selectOverlap, startMoment, endMoment);
					$('#reservation-type-overlap').val('yes');
					$('#step-increment-val').val(ratioVal);
					datesReset = true;
				} else {
					$('#reservation-type-overlap').val('no');
				}

				getInstrumentOptions(info.event.resource.id, resOptionValue);
				$('#reservation-modal #ratioComment-value').text(ratioComment);
				if(highlightComment == 1){
					$('#reservation-modal #ratioComment-value').addClass("highlighted");
				}else{
					$('#reservation-modal #ratioComment-value').removeClass("highlighted");
				}
				
				if(!datesReset){					
					$('#start-datetime-picker').datetimepicker('date', startMoment);
					$('#end-datetime-picker').datetimepicker('date', endMoment);
				}
				$('#reservation-modal #reservation-comment').val(remark);
				$('#reservationModalTitle').html('<div>Reservation Details for</div><div>' + info.event.getResources()[0].title + '</div>');
				// delete past reservations not possible
				updateButtonsWithTime(startDate.getTime(), endDate.getTime());
				$('#reservation-id').attr('value', info.event.extendedProps.resid);
				$('#original-start-str').attr('value', $('#start-datetime-picker').datetimepicker('viewDate').format());
				$('#original-end-str').attr('value', $('#end-datetime-picker').datetimepicker('viewDate').format());
				$('#reservation-modal #ratio-input').val(ratio);
				$('#reservation-modal #ratio-input').val(ratioVal);
				$('#reservation-modal .reservation-ratio').css('display', ratioDisplay);

				$('#reservation-usage').css('display', 'none');
				$('#reservation-usage-extra').css('display', 'none');
				
				var instId = parseInt(info.event.resource.id);
				var allowedInstForResaUsg = info.event.resource.extendedProps.allowedInstForResaUsg;
				if ($.inArray(instId, allowedInstForResaUsg) !== -1) {
					var reservationId = info.event.extendedProps.resid;
					getReservationUsageByResId(reservationId);
					if(instId == 1231){ //Tissue LyzerII Qiagen
						$('#reservation-usage').css('display', 'none');
						$('#reservation-usage-extra').css('display', 'block');
					}else{						
						$('#reservation-usage').css('display', 'block');
						$('#reservation-usage-extra').css('display', 'none');
					}
				}
				$('#reservation-modal').modal('show');

				// set cookie for current view
				setCookie('timeline_view', calendar.view.type, 365);
			},
			eventMouseEnter: function(mouseEnterInfo) {
				$('.tooltip-inner').css('background-color', 'black');
				$('.tooltip-inner').css('padding', '1px');
			},
			eventChange: function(info) {
				let event = info.event;
				if (event && event._instance && event.extendedProps) {
					let isChanging = event.extendedProps.isChanging;
					if (isChanging === true) {
						let el = event.extendedProps.element;
						updateTooltip(el, event);
					}
				}
			},
			eventDidMount: function(info) {
				let el = $(info.el);
				el.attr('data-toggle', 'tooltip');
				el.attr('data-placement', 'top');
				updateTooltip(el, info.event);
				el.tooltip({
					html: true
				});
				info.event.setExtendedProp('element', el);
			}
		});
	}
	console.log("Rendering calendar...");
	calendar.render();
	console.log("Calendar rendered.");
	if (isNotReservationCalendar === true) {
		$(".fc-datagrid-body.fc-scrollgrid-sync-table tr:first").css("display", "none");
		$("#orderDescriptionLinkBtn").css("display", "none");
		$("#htmcDescriptionLinkBtn").css("display", "none");
		setTimeout(() => {
			let firstRaw;
			if (synthesisCalendar === true) {
				firstRaw = 1;
			} else {
				firstRaw = 2;
			}
			$(`.fc-resource[data-resource-id="${firstRaw}"]`).css("display", "none");
		}, 20);
	}

	$('#calendar .fc-datagrid-body .fc-datagrid-expander').on("click", function(ev) {
		if (!$(this).hasClass("events-loaded")) {
			if (isNotReservationCalendar === true) {
				getSynthesysOrder();
			} else {
				getReservationsForGroup($(this).next().text());
			}
			$(this).addClass("events-loaded");
		}
	});

	// date navigation button events
	$("button.fc-prev-button").on("click", function(ev) {
		refreshEvents(false);
	});
	$("button.fc-next-button").on("click", function(ev) {
		refreshEvents(false);
	});
	$("button.fc-today-button").on("click", function(ev) {
		refreshEvents(false);
	});
	$("button.fc-resourceTimelineDay-button").on("click", function(ev) {
		refreshEvents(false);
	});
	$("button.fc-resourceTimelineMonth-button").on("click", function(ev) {
		refreshEvents(false);
	});
	$("button.fc-resourceTimelineWeek-button").on("click", function(ev) {
		refreshEvents(false);
	});
	if (isNotReservationCalendar === true) {
		$("button.fc-quarterly-button").on("click", function(ev) {
			refreshEvents(false);
		});
		$("button.fc-halfyear-button").on("click", function(ev) {
			refreshEvents(false);
		});
		$("button.fc-year-button").on("click", function(ev) {
			refreshEvents(false);
		});
	}

	// Modal reservation dialog events
	$("button.delete").on("click", function(ev) {
		$('#confirm-delete').modal('show');
	});
	$("button.done").on("click", function(ev) {
		if (isNotReservationCalendar === true) {
			$('#emailconfirm').modal('show');
		} else {
			let info = window.eventInfo;
			eventDone(info);
			$('#reservation-modal').modal('hide');
			clearReservationFields();
		}

	});
	$("button.cancel").on("click", function(ev) {
		$('#reservation-modal').modal('hide');
		clearReservationFields();
	});
	$("button.save").on("click", function(ev) {
		if (isNotReservationCalendar === true) {
			let startStr = synthesisCalendar ? $('#order-start-datetime-picker').datetimepicker('viewDate').format() : $('#htmc-start-datetime-picker').datetimepicker('viewDate').format();
			let endStr = synthesisCalendar ? $('#order-end-datetime-picker').datetimepicker('viewDate').format() : $('#htmc-end-datetime-picker').datetimepicker('viewDate').format();
			let startDate = new Date(Date.parse(startStr));
			let endDate = new Date(Date.parse(endStr));
			let reqStr = $('#req-datetime-picker').datetimepicker('viewDate').format();
			let reqDate = new Date(Date.parse(reqStr));
			let orderId = synthesisCalendar ? $('#synthesisorder-id').attr('value') : $('#htmcorder-id').attr('value');
			let title = synthesisCalendar ? $('#synthesisorder-title').val() : $('#htmcorder-title').val();
			if (!isNaN(title.valueOf())) {
				alert('Field Title is empty.');
				return;
			}
			let realTitle = title.split('(')[0];
			let requester = synthesisCalendar ? $('#synthesisorder-requester-option').val() : $('#htmcorder-requester-option').val();
			if (!isNaN(requester.valueOf())) {
				alert('Field Requester is empty.');
				return;
			}
			let project = synthesisCalendar ? $('#synthesisorder-projects-option').val() : $('#htmcorder-projects-option').val();
			let compound = synthesisCalendar ? $('#synthesisorder-coumpounds-option').val() : $('#htmcorder-coumpounds-option').val();
			let libraryOutcome = $('#htmcorder-libraryOutcome').val();
			let link = synthesisCalendar ? $('#synthesisorder-link').val() : $('#htmcorder-link').val();
			let quantity = $('#synthesisorder-quantity').val();
			let unit = $('#synthesisorder-unit').val();
			let remarks = $('#synthesisorder-remarks').val();
			if (endDate <= startDate) {
				alert('Please verify start and end dates.');
				return;
			}
			window.eventInfo.setExtendedProp('requester', requester);
			window.eventInfo.setExtendedProp('requestDate', reqDate);
			window.eventInfo.setExtendedProp('project', project);
			window.eventInfo.setExtendedProp('link', link);
			window.eventInfo.setExtendedProp('libraryOutcome', libraryOutcome);
			window.eventInfo.setExtendedProp('reqDate', reqDate);
			window.eventInfo.setExtendedProp('compound', compound);
			window.eventInfo.setExtendedProp('quantity', quantity);
			window.eventInfo.setExtendedProp('unit', unit);
			window.eventInfo.setExtendedProp('remarks', remarks);
			window.eventInfo.setDates(startStr, endStr);
			if (orderId === '0' || !orderId) {
				window.eventInfo.setProp('title', title);
				window.eventInfo.setProp('id', null);
				registerSynthesisOrder(window.eventInfo);
				$('#synthesisorder-modal').modal('hide');
				$('#htmcorder-modal').modal('hide');
				clearSynthesisOrderFields();
				clearHtmcOrderFields();
			} else {
				window.eventInfo.setExtendedProp('isChanging', true);
				window.eventInfo.setProp('title', realTitle);
				updateSynthesisOrder(window.eventInfo);
				refreshEvents(false);
				location.reload();
			}
		} else {
			let startStr = $('#start-datetime-picker').datetimepicker('viewDate').format();
			let endStr = $('#end-datetime-picker').datetimepicker('viewDate').format();

			let startDate = new Date(Date.parse(startStr));
			let endDate = new Date(Date.parse(endStr));
			let comment = $('#reservation-comment').val();
			let resOptionValue = $('#reservation-option').val();
			let ratio = $('#reservation-modal #ratio-input').val();
			let resOption = $("#reservation-option option[value=" + resOptionValue + "]").text();
			let reservationId = $('#reservation-id').attr('value');
			let project = $('#reservation-usage-projects-option').val();
			let compound = $('#reservation-usage-compounds-option').val();
			//let sample = $('#reservation-usage-samples-option').val();
			let batch = $('#reservation-usage-batches-value').val();
			let sampleType = $('#reservation-usage-sampleType-value').val();
			let specie = $('#reservation-usage-specie-value').val();
			//alert("reservationId" + reservationId);
			if (endDate <= startDate) {
				alert('Please verify start and end dates.');
				return;
			}

			let selectOverlapOn = $('#reservation-type-overlap').val() == 'yes';
			if (selectOverlapOn) {
				let stepIncrementValue = $("#step-increment-val").val();
				if (ratio == 0 || ratio > 100) {
					alert("The ratio value is wrong, please set a value between " + stepIncrementValue + "and 100.");
					return;
				}
				if ($("#ratio-left-val").html().indexOf("(") >= 0) {
					let ratioLeft = $("#ratio-left-val").html().split('(')[1].split('%')[0];
					if (parseInt(ratioLeft, 10) - ratio < 0) {
						alert("The ratio set is greater than what is available (" + ratioLeft + "% left)");
						return;
					}
				}
				if (ratio % stepIncrementValue != 0) {
					alert("The ratio set is not a multiple of the defined step increment. Please change for a value which is a multiple of " + stepIncrementValue + ".");
					return;
				}
			}


			// reservation id = 0 (event creation) => registerEvent
			if (reservationId === '0' || !reservationId) {
				window.eventInfo.startStr = startStr;
				window.eventInfo.endStr = endStr;
				window.eventInfo.comment = comment;
				window.eventInfo.resOptionValue = resOptionValue;
				window.eventInfo.ratio = ratio;
				window.eventInfo.project = project;
				window.eventInfo.compound = compound;
				//window.eventInfo.sample = sample;
				window.eventInfo.batch = batch;
				window.eventInfo.sampleType = sampleType;
				window.eventInfo.specie = specie;
				registerEvent(window.eventInfo);
			} else {
				window.eventInfo.setExtendedProp('isChanging', true);
				window.eventInfo.setExtendedProp('originalStartStr', window.eventInfo.extendedProps.startStr);
				window.eventInfo.setExtendedProp('originalEndStr', window.eventInfo.extendedProps.endStr);
				window.eventInfo.setExtendedProp('resid', reservationId);
				window.eventInfo.setDates(startStr, endStr);
				window.eventInfo.setExtendedProp('remark', comment);
				window.eventInfo.setExtendedProp('resOptionValue', resOptionValue);
				if (resOptionValue === '0') resOption = '';
				window.eventInfo.setExtendedProp('resOption', resOption);
				window.eventInfo.setExtendedProp('ratio', ratio);
				window.eventInfo.setExtendedProp('project', project);
				window.eventInfo.setExtendedProp('compound', compound);
				//window.eventInfo.setExtendedProp('sample', sample);
				window.eventInfo.setExtendedProp('batch', batch);
				window.eventInfo.setExtendedProp('sampleType', sampleType);
				window.eventInfo.setExtendedProp('specie', specie);
				updateEvent(window.eventInfo);
				refreshEvents(false);
			}

			$('#reservation-modal').modal('hide');
			clearReservationFields();
		}
	});

	$('#start-datetime-picker').datetimepicker({
		stepping: stepping,
		locale: 'en-gb',
		format: 'DD/MM/YYYY HH:mm'
	});
	$('#end-datetime-picker').datetimepicker({
		stepping: stepping,
		locale: 'en-gb',
		format: 'DD/MM/YYYY HH:mm'
	});
	$('#order-start-datetime-picker').datetimepicker({
		stepping: stepping,
		locale: 'en-gb',
		format: 'DD/MM/YYYY'
	});
	$('#order-end-datetime-picker').datetimepicker({
		stepping: stepping,
		locale: 'en-gb',
		format: 'DD/MM/YYYY'
	});
	$('#htmc-start-datetime-picker').datetimepicker({
		stepping: stepping,
		locale: 'en-gb',
		format: 'DD/MM/YYYY'
	});
	$('#htmc-end-datetime-picker').datetimepicker({
		stepping: stepping,
		locale: 'en-gb',
		format: 'DD/MM/YYYY'
	});
	$('#req-datetime-picker').datetimepicker({
		stepping: stepping,
		locale: 'en-gb',
		format: 'DD/MM/YYYY'
	});


	console.log('Getting all reservation options');
	$.ajax({
		type: 'GET',
		url: '/lers/reservation/resoption/all',
		success: function(data) {
			window.resoptions = data.resoptions;
		},
		error: function(jqXhr) {
			console.log('Error. Cannot get reservation options.');
		}
	});

	$("#reservation-usage-projects-option").autocomplete({
		minLength: 0,
		delay: 100,
		appendTo: ".modal-body",
		source: function(request, response) {
			$.ajax({
				url: "/lers/reservation/project/all",
				dataType: "json",
				success: function(data) {
					response($.ui.autocomplete.filter(data.projects, $("#reservation-usage-projects-option").val()));
				},
				error: function(jqXHR, textStatus, errorThrown) {
					console.log(jqXHR);
					console.log(textStatus);
					console.log(errorThrown);
				}
			})
		},
		select: function(event, ui) {
			//alert("Selected: value: " + ui.item.value);
		},
		close: function() {
			$("#reservation-usage-projects-option").autocomplete('close');
		}
	});

	$("#reservation-usage-compounds-option").autocomplete({
		minLength: 9,
		delay: 0,
		appendTo: ".modal-body",
		source: function(request, response) {
			$.ajax({
				url: "/lers/reservation/substance/all",
				dataType: "json",
				success: function(data) {
					var results = $.ui.autocomplete.filter(data.substances, $("#reservation-usage-compounds-option").val());
					response(results.slice(0, 30));
				},
				error: function(jqXHR, textStatus, errorThrown) {
					console.log(jqXHR);
					console.log(textStatus);
					console.log(errorThrown);
				}
			})
		},
		select: function(event, ui) {
			//$('#reservation-usage-samples-option').val('');
			var coumpoundName = ui.item.value;
			getBatchesByCompound(coumpoundName, null);
		},
		close: function() {
			$("#reservation-usage-compounds-option").autocomplete('close');
		}
	});

	$("#reservation-usage-compounds-option").on("input", function() {
		var search = $(this).val();
		if (search.length < 9) {
			$("#reservation-usage-compounds-option").css("color", "#f54b42")
			$('#reservation-modal button.save').hide();
		} else {
			$("#reservation-usage-compounds-option").css("color", "#000")
			$('#reservation-modal button.save').show();
		}
	});

	$("#reservation-usage-compounds-option").focusout(function() {
		//alert($("#reservation-usage-compounds-option").val());
		var coumpoundName = $("#reservation-usage-compounds-option").val();
		if (coumpoundName) {
			getCompoundByName(coumpoundName);
			getBatchesByCompound(coumpoundName.toUpperCase(), null);
		} else {
			$('#reservation-modal button.save').show();
		}
	});

	$("#synthesisorder-link").focusout(function() {
		var desc = $("#synthesisorder-link").val();
		checkOrderDescription(desc);
	});

	function checkOrderDescription(desc) {
		if (desc) {
			$("#orderDescriptionLinkBtn").css("display", "block");
		} else {
			$("#orderDescriptionLinkBtn").css("display", "none");
		}
	}

	$("#orderDescriptionLinkBtn").click(function() {
		var desc = $("#synthesisorder-link").val();
		window.open(desc, "_blank");
	});

	$("#htmcorder-link").focusout(function() {
		var desc = $("#htmcorder-link").val();
		checkHtmcDescription(desc);
	});

	function checkHtmcDescription(link) {
		if (link) {
			$("#htmcDescriptionLinkBtn").css("display", "block");
		} else {
			$("#htmcDescriptionLinkBtn").css("display", "none");
		}
	}

	$("#htmcDescriptionLinkBtn").click(function() {
		var desc = $("#htmcorder-link").val();
		window.open(desc, "_blank");
	});

	$("#synthesisorder-projects-option").autocomplete({
		minLength: 0,
		delay: 100,
		appendTo: ".modal-body",
		source: function(request, response) {
			$.ajax({
				url: "/lers/synthesisorder/project/all",
				dataType: "json",
				success: function(data) {
					response($.ui.autocomplete.filter(data.projects, $("#synthesisorder-projects-option").val()));
				},
				error: function(jqXHR, textStatus, errorThrown) {
					console.log(jqXHR);
					console.log(textStatus);
					console.log(errorThrown);
				}
			})
		},
		select: function(event, ui) {
			//alert("Selected: value: " + ui.item.value);
		},
		close: function() {
			$("#synthesisorder-projects-option").autocomplete('close');
		}
	});

	$("#synthesisorder-coumpounds-option").autocomplete({
		minLength: 9,
		delay: 0,
		appendTo: ".modal-body",
		source: function(request, response) {
			$.ajax({
				url: "/lers/synthesisorder/substance/all",
				dataType: "json",
				success: function(data) {
					var results = $.ui.autocomplete.filter(data.substances, $("#synthesisorder-coumpounds-option").val());
					response(results.slice(0, 30));
				},
				error: function(jqXHR, textStatus, errorThrown) {
					console.log(jqXHR);
					console.log(textStatus);
					console.log(errorThrown);
				}
			})
		},
		select: function(event, ui) {
			var coumpoundName = ui.item.value;
			//display structure into modal
			getMolStructureByCoumpound(coumpoundName.toUpperCase(), null, null);
		},
		close: function() {
			$("#synthesisorder-coumpounds-option").autocomplete('close');
		}
	});

	$("#synthesisorder-coumpounds-option").on("input", function() {
		var search = $(this).val();
		if (search.length < 9) {
			$("#synthesisorder-coumpounds-option").css("color", "#f54b42")
			$('#synthesisorder-modal button.save').hide();
		} else {
			$("#synthesisorder-coumpounds-option").css("color", "#000")
			$('#synthesisorder-modal button.save').show();
		}
	});

	$("#synthesisorder-coumpounds-option").focusout(function() {
		var coumpoundName = $("#synthesisorder-coumpounds-option").val();
		if (coumpoundName) {
			getMolStructureByCoumpound(coumpoundName.toUpperCase(), null, null);
		} else {
			$('#synthesisorder-modal button.save').show();
		}
	});

	$("#synthesisorder-requester-option").autocomplete({
		minLength: 4,
		delay: 100,
		appendTo: ".modal-body",
		source: function(request, response) {
			$.ajax({
				url: "/lers/synthesisorder/requester/all",
				dataType: "json",
				success: function(data) {
					response($.ui.autocomplete.filter(data.requesters, $("#synthesisorder-requester-option").val()));
				},
				error: function(jqXHR, textStatus, errorThrown) {
					console.log(jqXHR);
					console.log(textStatus);
					console.log(errorThrown);
				}
			})
		},
		select: function(event, ui) {
			//alert("Selected: value: " + ui.item.value);
		},
		close: function() {
			$("#synthesisorder-requester-option").autocomplete('close');
		}
	});

	$("#synthesisorder-requester-option").on("input", function() {
		var search = $(this).val();
		if (search.length < 4) {
			$("#synthesisorder-requester-option").css("color", "#f54b42")
			//$('#synthesisorder-modal button.save').hide();
		} else {
			$("#synthesisorder-requester-option").css("color", "#000")
			//$('#synthesisorder-modal button.save').show();
		}
	});

	$("#htmcorder-projects-option").autocomplete({
		minLength: 0,
		delay: 100,
		appendTo: ".modal-body",
		source: function(request, response) {
			$.ajax({
				url: "/lers/synthesisorder/project/all",
				dataType: "json",
				success: function(data) {
					response($.ui.autocomplete.filter(data.projects, $("#htmcorder-projects-option").val()));
				},
				error: function(jqXHR, textStatus, errorThrown) {
					console.log(jqXHR);
					console.log(textStatus);
					console.log(errorThrown);
				}
			})
		},
		select: function(event, ui) {
			//alert("Selected: value: " + ui.item.value);
		},
		close: function() {
			$("#htmcorder-projects-option").autocomplete('close');
		}
	});

	$("#htmcorder-coumpounds-option").autocomplete({
		minLength: 9,
		delay: 0,
		appendTo: ".modal-body",
		source: function(request, response) {
			$.ajax({
				url: "/lers/synthesisorder/substance/all",
				dataType: "json",
				success: function(data) {
					var results = $.ui.autocomplete.filter(data.substances, $("#htmcorder-coumpounds-option").val());
					response(results.slice(0, 30));
				},
				error: function(jqXHR, textStatus, errorThrown) {
					console.log(jqXHR);
					console.log(textStatus);
					console.log(errorThrown);
				}
			})
		},
		select: function(event, ui) {
			var coumpoundName = ui.item.value;
			//display structure into modal
			getMolStructureByCoumpound(coumpoundName.toUpperCase(), null, null);
		},
		close: function() {
			$("#htmcorder-coumpounds-option").autocomplete('close');
		}
	});

	$("#htmcorder-coumpounds-option").on("input", function() {
		var search = $(this).val();
		if (search.length < 9) {
			$("#htmcorder-coumpounds-option").css("color", "#f54b42")
			$('#htmcorder-modal button.save').hide();
		} else {
			$("#htmcorder-coumpounds-option").css("color", "#000")
			$('#htmcorder-modal button.save').show();
		}
	});

	$("#htmcorder-coumpounds-option").focusout(function() {
		var coumpoundName = $("#htmcorder-coumpounds-option").val();
		if (coumpoundName) {
			getMolStructureByCoumpound(coumpoundName.toUpperCase(), null, null);
		} else {
			$('#htmcorder-modal button.save').show();
		}
	});

	$("#htmcorder-requester-option").autocomplete({
		minLength: 4,
		delay: 100,
		appendTo: ".modal-body",
		source: function(request, response) {
			$.ajax({
				url: "/lers/synthesisorder/requester/all",
				dataType: "json",
				success: function(data) {
					response($.ui.autocomplete.filter(data.requesters, $("#htmcorder-requester-option").val()));
				},
				error: function(jqXHR, textStatus, errorThrown) {
					console.log(jqXHR);
					console.log(textStatus);
					console.log(errorThrown);
				}
			})
		},
		select: function(event, ui) {
			//alert("Selected: value: " + ui.item.value);
		},
		close: function() {
			$("#htmcorder-requester-option").autocomplete('close');
		}
	});

	$("#htmcorder-requester-option").on("input", function() {
		var search = $(this).val();
		if (search.length < 4) {
			$("#htmcorder-requester-option").css("color", "#f54b42")
			//$('#synthesisorder-modal button.save').hide();
		} else {
			$("#htmcorder-requester-option").css("color", "#000")
			//$('#synthesisorder-modal button.save').show();
		}
	});

	function getMolStructureByCoumpound(compoundName, tooltipText, el) {
		$.ajax({
			type: 'GET',
			url: '/lers/synthesisorder/structure/' + compoundName,
			success: function(data) {
				let base64Structure = data.base64Structure;
				if (tooltipText) {
					tooltipText = tooltipText + "<img src=\"data:image/jpg;base64,";
					tooltipText = tooltipText + base64Structure;
					tooltipText = tooltipText + "\" width=\"160\" height=\"160\"/></div>"
					el.attr('data-original-title', tooltipText);
				} else {
					structureImg.setAttribute('src', "data:image/jpg;base64," + base64Structure);
					$("#structBtn").css("display", "block");
				}
			},
			error: function(jqXhr) {
				console.log('get structure compound failed.');
				el.attr('data-original-title', tooltipText);
			}
		});
	}

	$("#structBtn").click(function() {
		setTimeout(() => {
			$('#confirm-structure').modal('show');
		}, 100);
	});

	/*console.log('Getting all samples');
	$("#reservation-usage-samples-option").autocomplete({
		minLength: 9,
		delay: 0,
		appendTo: ".modal-body",
		source: function(request, response) {
			$.ajax({
				url: "/lers/reservation/sample/all",
				dataType: "json",
				success: function(data) {
					var results = $.ui.autocomplete.filter(data.samples, $("#reservation-usage-samples-option").val());
					response(results.slice(0, 30));
				},
				error: function(jqXHR, textStatus, errorThrown) {
					console.log(jqXHR);
					console.log(textStatus);
					console.log(errorThrown);
				}
			})
		},
		select: function(event, ui) {
			$('#reservation-usage-compounds-option').val('');
			var sampleName = ui.item.value;
			getBatchesBySampleExternalReference(sampleName, null);
		},
		close: function() {
			$("#reservation-usage-samples-option").autocomplete('close');
		}
	});
	
	$("#reservation-usage-samples-option").on("input", function() {
		var search = $(this).val();
		if (search.length < 9) {
			$("#reservation-usage-samples-option").css("color", "#f54b42")
		} else {
			$("#reservation-usage-samples-option").css("color", "#000")
		}
	});*/

	$('#emailconfirmok').on('click', function() {
		let title = window.eventInfo.title.split('(')[0];
		let jsonData = {
			resourceId: window.eventInfo.extendedProps.resid,
			title: title,
			requester: window.eventInfo.extendedProps.requester,
			reqDate: window.eventInfo.extendedProps.reqDate,
			project: window.eventInfo.extendedProps.project,
			compound: window.eventInfo.extendedProps.compound,
			quantity: window.eventInfo.extendedProps.quantity,
			unit: window.eventInfo.extendedProps.unit,
			libraryOutcome: window.eventInfo.extendedProps.libraryOutcome,
			link: window.eventInfo.extendedProps.link,
			username: window.eventInfo.extendedProps.username,
			remarks: window.eventInfo.extendedProps.remarks,
			doneTime: window.eventInfo.extendedProps.doneTime,
			fromDate: window.eventInfo.startStr,
			toDate: window.eventInfo.endStr
		};
		let url = synthesisCalendar === true ? '/lers/synthesisorder/email' : '/lers/htmcorder/email'
		$.ajax({
			type: 'POST',
			url: url,
			dataType: 'json',
			contentType: 'application/json',
			data: JSON.stringify(jsonData)
		}).done(function(data) {
			window.eventInfo.setExtendedProp('isChanging', false);
			location.reload();
		}).fail(function(data) {
			alert("Cannot send email to requester!");
		});
	});

	$('#confirm-delete #confirm-delete-ok').on('click', function() {
		if (isNotReservationCalendar === false) {
			let resid = window.eventInfo.extendedProps.resid;
			$.ajax({
				type: 'DELETE',
				url: '/lers/reservation/delete/' + resid,
				dataType: 'json',
				contentType: 'application/json',
				success: function(data) {
					let reservation = data.reservation;
					let resourceIds = data.resourceIds;
					if (reservation && resourceIds) {
						removeEvents(resourceIds, reservation.id);
						//window.eventInfo.remove();
					}
					$('#confirm-delete').modal('hide');
					$('#reservation-modal').modal('hide');
				},
				error: function(data) {
					console.log(data);
				}
			});
		} else {
			let orderid = window.eventInfo.extendedProps.resid;
			let url;
			if (synthesisCalendar === true) {
				url = '/lers/synthesisorder/delete/' + orderid
			} else {
				url = '/lers/htmcorder/delete/' + orderid
			}
			$.ajax({
				type: 'DELETE',
				url: url,
				dataType: 'json',
				contentType: 'application/json',
				success: function(data) {
					location.reload();
				},
				error: function(data) {
					console.log(data);
				}
			});
		}
	});

	scrollToToday();

	function scrollToToday() {
		const scroller = $('.fc-scrollgrid-section-body .fc-scroller').last();
		const [date] = moment().toISOString().split(':');
		//const position = $(`.fc-timeline-slot[data-date^="${date}"]`).last().position();
		let wellFormatedDate = moment().toISOString().split(':')[0].split('T')[0];
		const position = $(`.fc-timeline-slot[data-date^="${wellFormatedDate}"]`).last().position();
		if (position) {
			scroller.scrollLeft(position.left);
		}
	}

	function checkIfAllowedToBook() {
		var allowedToBook = false;
		// 21360 = Synthesis Services group
		// 21522 = HTMC Groups
		// 21462 = Scientific computing		
		var resourceGroup = synthesisCalendar ? 21360 : 21522;
		var userGroups = $("#userGroupsValues").text() ? $("#userGroupsValues").text().split(';') : [];
		$.each(userGroups, function(i, val) {
			var grp = parseInt(val, 10);
			if (grp == resourceGroup || grp == 21462) {
				allowedToBook = true;
			}
		});
		return allowedToBook;
	}
	
	$("#synthesisFilter").on("input", function() {
		var filterValue = $(this).val();
		if (filterValue.length < 1) {
			resetFilter();
		}else{
			if (filterValue.length < 4) {
				$("#synthesisFilter").css("color", "#f54b42");
			} else {
				$("#synthesisFilter").css("color", "#000");
				let resources = calendar.getResources();
				if (resources) {
					for (let resource of resources) {
						let events = resource.getEvents();
						if (events && events.length>0) {
							for (let event of events) {
								/*let resid = event.extendedProps.resid
								let project = event.extendedProps.project;
								let compound = event.extendedProps.compound;*/
								let requester = event.extendedProps.requester;
								let title = event._def.title;
								if (/*parseInt(resid) !== 1 && parseInt(resid) !== 2
									&& (project && project.toUpperCase().indexOf(filterValue.toUpperCase()) >= 0)
									|| (compound && compound.toUpperCase().indexOf(filterValue.toUpperCase()) >= 0)
									||*/ 
									(requester && requester.toUpperCase().indexOf(filterValue.toUpperCase()) >= 0)
									|| title.toUpperCase().indexOf(filterValue.toUpperCase()) >= 0
									) {
									event.setProp('display', 'auto');
								} else {
									event.setProp('display', 'none');
									resource.remove();
								}
							}
						}else{
							//console.log(resource._resource.title);
							resource.remove();
						}
					}
	
				}
				calendar.render();
			}
		}
	});

	$("#clearFilter").click(function() {
		resetFilter();
	});

	function resetFilter() {
		$("#synthesisFilter").val("");
		calendar.refetchResources();
		let resources = calendar.getResources();
		if (resources) {
			for (let resource of resources) {
				let events = resource.getEvents();
				if (events) {
					for (let event of events) {
						let resid = event.extendedProps.resid
						if (parseInt(resid) !== 1 && parseInt(resid) !== 2) {
							event.setProp('display', 'auto');
						} else {
							event.setProp('display', 'none');
							resource.remove();
						}
					}
				}
			}

		}
		calendar.render();
		refreshEvents(false);
	}

}