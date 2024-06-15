package org.research.kadda.labinventory.ui;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.research.kadda.labinventory.LabInventoryService;
import org.research.kadda.labinventory.core.model.InstrResaModel;
import org.research.kadda.labinventory.core.service.EmailService;
import org.research.kadda.labinventory.core.utils.MiscUtils;
import org.research.kadda.labinventory.data.FavoriteDto;
import org.research.kadda.labinventory.data.InstrumentDto;
import org.research.kadda.labinventory.data.InstrumentGroupDto;
import org.research.kadda.labinventory.data.InstrumentPriorityUsersDto;
import org.research.kadda.labinventory.data.JsonUtils;
import org.research.kadda.labinventory.data.ReservationDto;
import org.research.kadda.labinventory.data.ReservationHistoryDto;
import org.research.kadda.labinventory.data.ReservationUsageDto;
import org.research.kadda.labinventory.data.ResoptionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.research.kadda.osiris.OsirisService;
import org.research.kadda.osiris.data.BatchDto;
import org.research.kadda.osiris.data.EmployeeDto;
import org.research.kadda.osiris.data.ProjectDto;
import org.research.kadda.osiris.data.SampleDto;
import org.research.kadda.osiris.data.SubstanceDto;

/**
 * Author: Kadda
 */

@Controller
@Scope("session")
public class ReservationController {
	private static final int RESOURCE_ID_OFFSET = 100000;
	private static Logger logger = LogManager.getLogger(ReservationController.class);
	private List<InstrumentDto> instruments = null;
	private List<InstrumentGroupDto> instrumentGroups = null;
	private Map<String, List<Integer>> groupInstrumentIdsMap = new HashMap<>();
	private Map<String, List<Integer>> favGroupInstrumentIdsMap = new HashMap<>();
	private Map<String, InstrumentGroupDto> instrumentGroupsMap = new HashMap<>();
	private String message = "";
	private static final SimpleDateFormat FORMATER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
	private static final String BOOKING_SUBJECT = "Following instrument was booked ";
	private static final String END_BOOKING_SUBJECT = "Following instrument booking has ended ";
	private static final String HMS = " 00:00:00";

	@Autowired
	private EmailService emailService;

	public static int getResourceIdOffset() {
		return RESOURCE_ID_OFFSET;
	}

	@RequestMapping("/reservation")
	public ModelAndView displayTimeline(HttpServletRequest request, Model model) {

		HttpSession session = request.isRequestedSessionIdValid() ? request.getSession() : null;
		if (session == null) {
			return new ModelAndView("redirect:/home");
		}
		String username = (String) session.getAttribute("username");
		if (session == null || username == null) {
			return new ModelAndView("redirect:/home");
		}
		String userGroups = getUserGroups(username);
		ModelAndView mv = new ModelAndView("/reservation");
		mv.addObject("userGroups", userGroups);
		String forceParam = request.getParameter("force");
		boolean force = true; // set force to true to avoid refresh problems

		logger.info("[" + username + "] Opening reservation page");

		if (forceParam != null) {
			force = true;
		}

		// first create resources for all instruments group by groupname
		StringBuffer resources = new StringBuffer();
		Map<Integer, InstrumentDto> instrumentsMap = new HashMap<>();
		Map<Integer, List<InstrumentPriorityUsersDto>> instPrioUsrMap = getPrioUsersMap();
		if (instruments == null || force) {
			logger.info("[" + username + "] Getting all instruments");
			instruments = LabInventoryService.getAllInstruments();
			groupInstrumentIdsMap = new HashMap<>();
			instrumentGroupsMap = new HashMap<>();
		}
		
		List<Integer> allowedInstForResaUsg = LabInventoryService.getAllowedInstrumentForReservationUsages();
		
		for (InstrumentDto inst : instruments) {
			boolean isPriorityUsersSet = false;
			boolean isPriorityUser = false;
			if (inst.getReservable() == 0) {
				continue;
			}
			if (!groupInstrumentIdsMap.keySet().contains(inst.getGroupname())) {
				groupInstrumentIdsMap.put(inst.getGroupname(), new ArrayList<>());
			}
			if(instPrioUsrMap.get(inst.getId())!=null) {
				isPriorityUsersSet = true;
				for(InstrumentPriorityUsersDto ipu : instPrioUsrMap.get(inst.getId())) {
					if(ipu.getPriorityUser().equalsIgnoreCase(username)) {
						isPriorityUser = true;
					}
				}
			}
			resources.append("{");
			resources.append("id:");
			resources.append("'" + inst.getId() + "',");
			resources.append("extendedProps: {groupname:");
			resources.append("'" + inst.getGroupname() + "',");
			resources.append("rootId:" + inst.getId() + ",");
			resources.append("stepIncrement:" + inst.getStepIncrement() + ",");
			resources.append("instrumentID:" + inst.getId() + ",");
			
			resources.append("highlightComment:" + inst.getHighlightComment() + ",");
			Integer normalUsersOpen = null;
			resources.append("normalUsersOpen:" + normalUsersOpen + ",");
			resources.append("maxDays:" + inst.getMaxDays() + ",");
			resources.append("isPriorityUsersSet:" + isPriorityUsersSet + ",");
			resources.append("isPriorityUser:" + isPriorityUser + ",");
			resources.append("allowedInstForResaUsg:" + allowedInstForResaUsg + ",");
			
			resources.append("selectOverlap:" + inst.getSelectOverlap() + "},");
			resources.append("title:");
			resources.append("'" + inst.getName() + "',");
			resources.append("instrumentEmployeeGroups:");
			resources.append("'" + getInstrumentEmployeeGroups(String.valueOf(inst.getId())) + "',");
			resources.append("ratioComment:");
			String ratioComment = inst.getRatioComment() != null ? inst.getRatioComment() : "";
			resources.append("'" + ratioComment + "'},");

			// populate instrument map
			instrumentsMap.put(inst.getId(), inst);
			// populate instrument grouped by groups (Biology, Tecan...)
			groupInstrumentIdsMap.get(inst.getGroupname()).add(inst.getId());
		}

		// Then create groups with category of instruments (Tecan, Prep systems...)
		if (instrumentGroups == null || force) {
			logger.info("[" + username + "] Getting all instrument groups");
			instrumentGroups = LabInventoryService.getAllInstrumentGroups();
		}
		for (InstrumentGroupDto group : instrumentGroups) {
			if (!groupInstrumentIdsMap.keySet().contains(group.getName())) {
				groupInstrumentIdsMap.put(group.getName(), new ArrayList<>());
			}
			if (!instrumentGroupsMap.keySet().contains(group.getName())) {
				instrumentGroupsMap.put(group.getName(), group);
			}
			
			logger.info("[" + username + "] Getting instruments by group id");
			List<Integer> ids = LabInventoryService.getInstrumentIdsByGroupId(group.getId());

			for (Integer instId : ids) {
				boolean isPriorityUsersSet = false;
				boolean isPriorityUser = false;
				InstrumentDto inst = instrumentsMap.get(instId);
				if (inst == null || inst.getReservable() == 0) {
					continue;
				}
				if(instPrioUsrMap.get(instId)!=null) {
					isPriorityUsersSet = true;
					for(InstrumentPriorityUsersDto ipu : instPrioUsrMap.get(instId)) {
						if(ipu.getPriorityUser().equalsIgnoreCase(username)) {
							isPriorityUser = true;
						}
					}
				}
				resources.append("{");
				resources.append("id:");
				resources.append("'" + (inst.getId() + (group.getId() * RESOURCE_ID_OFFSET)) + "',");
				resources.append("extendedProps: {groupname:");
				resources.append("'" + group.getName() + "',");
				resources.append("rootId:" + inst.getId() + ",");
				resources.append("stepIncrement:" + inst.getStepIncrement() + ",");
				resources.append("instrumentID:" + inst.getId() + ",");
				
				resources.append("highlightComment:" + inst.getHighlightComment() + ",");
				Integer normalUsersOpen = null;
				resources.append("normalUsersOpen:" + normalUsersOpen + ",");
				resources.append("maxDays:" + inst.getMaxDays() + ",");
				resources.append("isPriorityUsersSet:" + isPriorityUsersSet + ",");
				resources.append("isPriorityUser:" + isPriorityUser + ",");	
				resources.append("allowedInstForResaUsg:" + allowedInstForResaUsg + ",");
				
				
				resources.append("selectOverlap:" + inst.getSelectOverlap() + "},");
				resources.append("title:");
				resources.append("'" + inst.getName() + "'},");

				groupInstrumentIdsMap.get(group.getName()).add(inst.getId());
			}
		}

		request.setAttribute("calendarResources", resources.toString());
		logger.info("[" + username + "] Return reservation page");
		return mv;
	}

	private Map<Integer, List<InstrumentPriorityUsersDto>> getPrioUsersMap() {
		Map<Integer, List<InstrumentPriorityUsersDto>> resultMap = new HashMap<Integer, List<InstrumentPriorityUsersDto>>();
		List<InstrumentPriorityUsersDto> priorityUsers = LabInventoryService.getAllInstrumentPriorityUsers();
		for(InstrumentPriorityUsersDto prioUsr : priorityUsers) {
			if(resultMap.get(prioUsr.getInstrumentId()) !=null) {
				resultMap.get(prioUsr.getInstrumentId()).add(prioUsr);
			}else {
				List<InstrumentPriorityUsersDto> prioUsrList = new ArrayList<InstrumentPriorityUsersDto>();
				prioUsrList.add(prioUsr);
				resultMap.put(prioUsr.getInstrumentId(), prioUsrList);
			}
		}
		return resultMap;
	}

	@RequestMapping("/reservation/resoption/all")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> getAllResoptions(HttpServletRequest request) {

		HttpSession session = request.isRequestedSessionIdValid() ? request.getSession() : null;
		if (session == null || session.getAttribute("username") == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Content-Type", "application/json")
					.body("{}");
		}

		logger.info("Getting all resoptions");
		List<ResoptionDto> resoptions = LabInventoryService.getAllResoptions();
		StringBuffer bodyResponse = new StringBuffer("{");
		try {
			bodyResponse.append("\"resoptions\":");
			bodyResponse.append(JsonUtils.mapToJson(resoptions));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		bodyResponse.append("}");

		logger.info("Got resoptions : " + bodyResponse.toString());

		return ResponseEntity.status(HttpStatus.OK).header("Content-Type", "application/json")
				.body(bodyResponse.toString());
	}

	@RequestMapping("/reservation/group")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> getReservationsForInstrumentGroup(HttpServletRequest request) {
		StringBuffer bodyResponse = new StringBuffer("{");

		if (groupInstrumentIdsMap.size() == 0) {
			displayTimeline(request, null);
		}

		// get request parameters (instrIds, fromDate, toDate)
		String groupName = request.getParameter("groupName");
		String startDateStr = request.getParameter("startDate");
		String endDateStr = request.getParameter("endDate");
		String username = request.getSession().getAttribute("username").toString().toUpperCase();

		logger.info("Getting reservations for group : " + groupName);
		logger.info("User : " + username + " From : " + startDateStr + " to " + endDateStr);

		if (groupInstrumentIdsMap.containsKey(groupName)) {
			// input date format (ISO) : Sun, 10 Jan 2021 23:00:00 GMT
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.RFC_1123_DATE_TIME;
			List<ReservationDto> reservations = LabInventoryService.getReservationsByInstrIdsAndDateRange(
					groupInstrumentIdsMap.get(groupName), null,
					Timestamp.valueOf(LocalDateTime.parse(startDateStr, dateTimeFormatter)),
					Timestamp.valueOf(LocalDateTime.parse(endDateStr, dateTimeFormatter)), false);
			List<ReservationUsageDto> reservationUsages = new ArrayList<ReservationUsageDto>();
			for (ReservationDto res : reservations) {
				List<ReservationUsageDto> reservationUsageDtos = LabInventoryService
						.getReservationUsageByReservationId(res.getId());
				reservationUsages.addAll(reservationUsageDtos);
				List<String> deputies = LabInventoryService.getDeputiesByInstrumentId(res.getInstrid());
				// add the owner to the deputies to manage the reservations
				InstrumentDto inst = LabInventoryService.getInstrumentById(res.getInstrid());
				deputies.add(inst.getUsername());
				res.setDeputies(deputies);
			}
			try {
				boolean applyOffset = false;
				int offset = 0;
				bodyResponse.append("\"applyInstrIdOffset\":");
				applyOffset = instrumentGroupsMap.keySet().contains(groupName) ? true : false;
				bodyResponse.append(applyOffset);
				InstrumentGroupDto instrumentGroupDto = instrumentGroupsMap.get(groupName);
				bodyResponse.append(",\"instrIdOffset\":");
				if (instrumentGroupDto != null) {
					offset = instrumentGroupDto.getId() * RESOURCE_ID_OFFSET;
				}
				bodyResponse.append(offset);
				bodyResponse.append(",\"connectedUser\":");
				bodyResponse.append("\"" + username + "\"");
				bodyResponse.append(",\"reservations\":");
				bodyResponse.append(JsonUtils.mapToJson(reservations));
				bodyResponse.append(",\"reservationUsages\":");
				bodyResponse.append(JsonUtils.mapToJson(reservationUsages));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		bodyResponse.append("}");

		return ResponseEntity.status(HttpStatus.OK).header("Content-Type", "application/json")
				.body(bodyResponse.toString());
	}

	// fullcalendar date format : 2021-01-06T00:30:00+01:00
	// DB date format : 2020-12-26 08:45:00
	@RequestMapping(method = RequestMethod.POST, path = "/reservation/add")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> addReservation(HttpServletRequest request, @RequestBody String jsonData) {
		logger.info("Adding Reservation");
		logger.info("JsonData Received : " + jsonData);
		ReservationDto reservationDto = null;
		ReservationUsageDto reservationUsageDto = null;
		HttpStatus status = HttpStatus.CREATED;
		jsonData = JsonUtils.formatUTF8(jsonData);
		// extract data from json
		try {
			reservationDto = getReservationFromJsonData(request, jsonData);
			// process instr id offset
			reservationDto.setInstrid(reservationDto.getInstrid() % RESOURCE_ID_OFFSET);
		} catch (JsonProcessingException e) {
			logger.error("The Json data is not well formatted");
			e.printStackTrace();
		} catch (NumberFormatException nfe) {
			logger.error("Resource Id must be an integer.");
			nfe.printStackTrace();
		}

		StringBuffer bodyResponse = new StringBuffer("");
		// check for reservation conflict
		Map<Boolean, String> validateReservationMap = validateReservation(reservationDto, jsonData);
		if (validateReservationMap.keySet().iterator().next()) {
			Set<String> usersThatHaveBookedOnTheSameSlot = getOverlapUsers(reservationDto, reservationDto.getInstrid());
			if (LabInventoryService.addReservation(reservationDto)) {
				bodyResponse.append(getReservationJson(reservationDto));
				try {
					reservationUsageDto = getReservationUsageFromJsonData(request, jsonData, reservationDto);
					boolean noResusage = "".equals(reservationUsageDto.getProject())
							&& "".equals(reservationUsageDto.getCompound())
							&& "".equals(reservationUsageDto.getBatch())
							&& "".equals(reservationUsageDto.getSampleType())
							&& "".equals(reservationUsageDto.getSpecie());
					if (!noResusage) {
						LabInventoryService.addReservationUsage(reservationUsageDto);
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				checkIfEmailNotification(reservationDto, false, request, usersThatHaveBookedOnTheSameSlot);
			} else {
				status = HttpStatus.ACCEPTED;
				bodyResponse = new StringBuffer(
						"{\"message\":\"Reservation failed.\",\"statusCode\":\"" + status.toString() + "\"}");
			}
		} else {
			String errorMessage = validateReservationMap.values().iterator().next();
			status = HttpStatus.FORBIDDEN;
			bodyResponse = new StringBuffer(
					"{\"message\":\"" + message + "\",\"statusCode\":\"" + status.toString() + "\",\"errorMessage\":\"" + errorMessage + "\"}");
		}

		return ResponseEntity.status(status).header("Content-Type", "application/json").body(bodyResponse.toString());
	}

	private void checkIfEmailNotification(ReservationDto reservationDto, boolean done, HttpServletRequest request, Set<String> usersThatHaveBookedOnTheSameSlot) {
		InstrumentDto inst = LabInventoryService.getInstrumentById(reservationDto.getInstrid());
		boolean emailToAll = inst.getEmailNotification() == 1;
		boolean emailToRequesters = inst.getEmailNotification() == 2;
		if (emailToAll || emailToRequesters) {
			try {
				List<String> usersWhoBookedOnSameSlot = new ArrayList<String>();
				usersWhoBookedOnSameSlot.addAll(usersThatHaveBookedOnTheSameSlot);
				String message = emailService.buildReservaionMailMessage(reservationDto, inst, done, usersWhoBookedOnSameSlot);
				boolean requesterSameAsOwnmer = reservationDto.getUsername().equalsIgnoreCase(inst.getUsername());
				if (message != null && !"".equals(message)) {
					String to = "";
					EmployeeDto employee = null;
					Set<String> ccs = new HashSet<String>();
					if(emailToAll) {
						List<String> deputies = LabInventoryService.getDeputiesByInstrumentId(inst.getId());
						if (deputies != null) {
							for (String deputy : deputies) {
								EmployeeDto dep = OsirisService.getEmployeeByUserId(deputy.toLowerCase());
								ccs.add(dep.getEmail());
							}
						}
						if (requesterSameAsOwnmer) {
							employee = OsirisService.getEmployeeByUserId(inst.getUsername().toLowerCase());
						} else {
							employee = OsirisService.getEmployeeByUserId(reservationDto.getUsername().toLowerCase());
							EmployeeDto owner = OsirisService.getEmployeeByUserId(inst.getUsername().toLowerCase());
							ccs.add(owner.getEmail());
						}
					}else {
						employee = OsirisService.getEmployeeByUserId(reservationDto.getUsername().toLowerCase());
					}
					// make sure to remove duplicate
					to = employee.getEmail();
					if (ccs.contains(to)) {
						ccs.remove(to);
					}
					for (String userThatHaveBookOnSameSlot : usersThatHaveBookedOnTheSameSlot) {
						EmployeeDto usr = OsirisService.getEmployeeByUserId(userThatHaveBookOnSameSlot.toLowerCase());
						if(!to.equals(usr.getEmail())) {							
							ccs.add(usr.getEmail());
						}
					}
					String subject = done ? END_BOOKING_SUBJECT : BOOKING_SUBJECT;
					boolean testMode = request.getRequestURL() != null
							&& (request.getRequestURL().toString().contains("apollo")
									|| request.getRequestURL().toString().contains("localhost"));
					emailService.sendJavaMail(message, to, ccs, subject + inst.getName(), testMode);
				}
			} catch (AddressException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Set<String> getOverlapUsers(ReservationDto reservationDto, int instId) {
		Set<String> userThatHaveBookOnSameSlot = new HashSet<String>();
		List<ReservationDto> reservations = LabInventoryService.getReservationsByInstrIdsAndDateRange(
				Collections.singletonList(instId), null, reservationDto.getFromTime(), reservationDto.getToTime(),
				false);
		for (ReservationDto resa : reservations) {
			userThatHaveBookOnSameSlot.add(resa.getUsername());
		}
		return userThatHaveBookOnSameSlot;
	}

	private Map<Boolean, String> validateReservation(ReservationDto reservationDto, String jsonData) {
		Map<Boolean, String> result = new HashMap<Boolean, String>();
		boolean validated = true;
		String message = "";
		
		int selectOverlap = 0;
		int ratio = -1;
		try {
			JsonNode selectOverlapNode = JsonUtils.getJsonNode(jsonData, "selectOverlap");
			JsonNode ratioNode = JsonUtils.getJsonNode(jsonData, "ratio");
			if (selectOverlapNode != null) {
				selectOverlap = selectOverlapNode.asInt();
			}
			if (ratioNode != null) {
				ratio = ratioNode.asInt();
			}
			
		} catch (JsonProcessingException e) {
			logger.error("SelectOverlap json data is missing.");
		}
		List<Integer> instrIds = Arrays.asList(reservationDto.getInstrid());
		Date fromDate = reservationDto.getFromTime();
		Date toDate = reservationDto.getToTime();
		// check if instrument got instruments that can't be booked at the same time
		List<Integer> restrictedInstrumentids = LabInventoryService.getRestrictedInstrumentIdsByInstId(instrIds.get(0));
		List<ReservationDto> restrictedReservations = null;
		List<ReservationDto> reservations = LabInventoryService.getReservationsByInstrIdsAndDateRange(instrIds,
				reservationDto.getId(), new Timestamp(fromDate.getTime()), new Timestamp(toDate.getTime()), true);
		if (restrictedInstrumentids != null && !restrictedInstrumentids.isEmpty()) {
			restrictedReservations = LabInventoryService.getReservationsByInstrIdsAndDateRange(restrictedInstrumentids,
					reservationDto.getId(), new Timestamp(fromDate.getTime()), new Timestamp(toDate.getTime()), false);
		}
		boolean restrictedReservationFound = restrictedReservations != null && !restrictedReservations.isEmpty();
		if (restrictedReservationFound)
			reservations.addAll(restrictedReservations);
		if (reservations != null && reservations.size() > 0) {
			if (restrictedReservationFound) {
				for (ReservationDto restrictedResa : restrictedReservations) {
					if (isOverlapingWithRestrictedInstrument(restrictedResa, fromDate, toDate)) {
						validated = false;
						String restrictedInstrumentsName = getRestrictedInstrumentsName(restrictedInstrumentids);
						message = "Reservation failed. This instrument can't be booked at the same time with other instruments ("
								+ restrictedInstrumentsName + ").";
						break;
					}
				}
			} else {
				if (selectOverlap == 0 || ratio <= 0) {
					validated = false;
					message = "Reservation failed. Bad ratio or reservation overlap not allowed for this instrument";
				} else {
					// check if overlap possible
					if (!overlapValidation(reservationDto, ratio, fromDate, toDate)) {
						validated = false;
						message = "Reservation is exceeding maximum instrument capacity.";
					}
				}
			}
		}
		
		Map<Boolean, Map<Date,Date>> validPrioInstMap = checkIfValidPrio(fromDate, toDate, reservationDto.getInstrid(), reservationDto.getUsername());
		if(!validPrioInstMap.keySet().iterator().next()) {
			validated = false;
			Date minRange = validPrioInstMap.values().iterator().next().keySet().iterator().next();
			Date maxRange = validPrioInstMap.values().iterator().next().values().iterator().next();
			message = "Your reservation cannot be registered, as you are not a member of the priority group! You can make this reservation after " + minRange + " and before " + maxRange;
		}
		
		result.put(validated, message);
		return result;
	}

	private String getReservationJson(ReservationDto reservationDto) {
		List<Integer> resourceIds = getResourceIds(reservationDto.getInstrid());
		StringBuffer buf = new StringBuffer("{");
		try {
			buf.append("\"reservation\":");
			buf.append(JsonUtils.mapToJson(reservationDto));
			buf.append(",\"resourceIds\":");
			buf.append(JsonUtils.mapToJson(resourceIds));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		buf.append("}");
		return buf.toString();
	}

	@RequestMapping(method = RequestMethod.PUT, path = "/reservation/update")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> updateReservation(HttpServletRequest request, @RequestBody String jsonData) {
		logger.info("Update Reservation...");
		logger.info("JsonData Received : " + jsonData);
		ReservationDto reservationDto = null;
		ReservationUsageDto reservationUsageDto = null;
		HttpStatus status = HttpStatus.CREATED;
		jsonData = JsonUtils.formatUTF8(jsonData);
		// extract data from json
		try {
			reservationDto = getReservationFromJsonData(request, jsonData);
			// process instr id offset
			reservationDto.setInstrid(reservationDto.getInstrid() % RESOURCE_ID_OFFSET);
			reservationUsageDto = getReservationUsageFromJsonData(request, jsonData, reservationDto);
			if (reservationUsageDto != null && !("").equals(reservationUsageDto.getProject())) {
				ReservationUsageDto persistedReservationUsage = LabInventoryService
						.getReservationUsageByReservationId(reservationDto.getId()).iterator().next();
				int reservationUsageId = persistedReservationUsage.getId();
				reservationUsageDto.setId(reservationUsageId);
			}
		} catch (JsonProcessingException e) {
			logger.error("The Json data is not well formatted");
			e.printStackTrace();
		} catch (NumberFormatException nfe) {
			logger.error("Resource Id must be an integer.");
			nfe.printStackTrace();
		}

		StringBuffer bodyResponse = new StringBuffer("");
		// check for reservation conflict
		Map<Boolean, String> validateReservationMap = validateReservation(reservationDto, jsonData);
		if (validateReservationMap.keySet().iterator().next()) {
			Set<String> usersThatHaveBookedOnTheSameSlot = getOverlapUsers(reservationDto, reservationDto.getInstrid());
			if (LabInventoryService.updateReservation(reservationDto)) {
				bodyResponse.append(getReservationJson(reservationDto));
				LabInventoryService.updateReservationUsage(reservationUsageDto);
				// if email notification is set for the instrument send an email to owner cc
				// deputies
				checkIfEmailNotification(reservationDto, false, request, usersThatHaveBookedOnTheSameSlot);
			} else {
				status = HttpStatus.ACCEPTED;
				bodyResponse = new StringBuffer(
						"{\"message\":\"Reservation failed.\",\"statusCode\":\"" + status.toString() + "\"}");
			}
		} else {
			String errorMessage = validateReservationMap.values().iterator().next();
			status = HttpStatus.FORBIDDEN;
			bodyResponse = new StringBuffer(
					"{\"message\":\"" + message + "\",\"statusCode\":\"" + status.toString() + "\",\"errorMessage\":\"" + errorMessage + "\"}");
		}

		return ResponseEntity.status(status).header("Content-Type", "application/json").body(bodyResponse.toString());
	}

	private List<Integer> getResourceIds(Integer instrumentId) {
		List<Integer> groupIds = LabInventoryService.getGroupIdsByInstrumentId(instrumentId);
		List<Integer> resourceIds = new ArrayList<>();
		resourceIds.add(instrumentId);
		if (groupIds != null) {
			for (Integer groupId : groupIds) {
				resourceIds.add(instrumentId + (groupId * RESOURCE_ID_OFFSET));
			}
		}

		return resourceIds;
	}

	@RequestMapping(method = RequestMethod.PUT, path = "/reservation/{resid}/done")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> finishReservation(HttpServletRequest request,
			@PathVariable(value = "resid") String resid) {
		logger.info("Finishing Reservation...");
		ReservationDto reservationDto = null;
		HttpStatus status = HttpStatus.CREATED;
		StringBuffer bodyResponse = new StringBuffer("");
		boolean success = true;

		reservationDto = LabInventoryService.getReservationById(Integer.valueOf(resid));
		if (reservationDto != null) {
			reservationDto.setToTime(new Date());
			if (!LabInventoryService.updateReservation(reservationDto)) {
				success = false;
			}
		} else {
			success = false;
		}

		if (success) {
			bodyResponse.append(getReservationJson(reservationDto));
			// if email notification is set for the instrument send an email to owner cc
			// deputies
			checkIfEmailNotification(reservationDto, true, request, new HashSet<String>());
		} else {
			status = HttpStatus.ACCEPTED;
			bodyResponse = new StringBuffer(
					"{\"message\":\"Cannot finish the reservation.\",\"statusCode\":\"" + status.toString() + "\"}");
		}

		return ResponseEntity.status(status).header("Content-Type", "application/json").body(bodyResponse.toString());
	}

	@RequestMapping(method = RequestMethod.DELETE, path = "/reservation/delete/{resid}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> deleteReservation(@PathVariable(value = "resid") String resid) {
		logger.info("Deleting Reservation");
		StringBuffer bodyResponse = new StringBuffer("{");
		HttpStatus status = HttpStatus.OK;

		ReservationDto reservationDto = LabInventoryService.getReservationById(Integer.valueOf(resid));
		List<ReservationUsageDto> reservationUsageDto = LabInventoryService
				.getReservationUsageByReservationId(Integer.valueOf(resid));
		if (reservationUsageDto != null && !reservationUsageDto.isEmpty()) {
			String reservationUsageId = String.valueOf(reservationUsageDto.iterator().next().getId());
			LabInventoryService.deleteReservationUsage(reservationUsageId);
		}
		if (!LabInventoryService.deleteReservation(resid)) {
			status = HttpStatus.BAD_REQUEST;
			bodyResponse.append("\"message\":\"" + LabInventoryService.getResponseMessage() + "\"");
		} else {
			if (reservationDto != null) {
				List<Integer> resourceIds = getResourceIds(reservationDto.getInstrid());
				try {
					bodyResponse.append("\"reservation\":");
					bodyResponse.append(JsonUtils.mapToJson(reservationDto));
					bodyResponse.append(",\"resourceIds\":");
					bodyResponse.append(JsonUtils.mapToJson(resourceIds));
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		}
		bodyResponse.append("}");

		return ResponseEntity.status(status).header("Content-Type", "application/json").body(bodyResponse.toString());
	}

	@RequestMapping("/reservation/project/all")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> getAllProjects(HttpServletRequest request) {

		HttpSession session = request.isRequestedSessionIdValid() ? request.getSession() : null;
		if (session == null || session.getAttribute("username") == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Content-Type", "application/json")
					.body("{}");
		}

		logger.info("Getting all projects");
		List<ProjectDto> projectDtos = OsirisService.getAllProjects();
		Collections.sort(projectDtos, new Comparator<ProjectDto>() {
			@Override
			public int compare(ProjectDto p1, ProjectDto p2) {
				return p1.getProjectName().compareTo(p2.getProjectName());
			}
		});
		List<String> projects = new ArrayList<String>();
		for (ProjectDto dto : projectDtos) {
			projects.add(dto.getProjectName());
		}
		StringBuffer bodyResponse = new StringBuffer("{");
		try {
			bodyResponse.append("\"projects\":");
			bodyResponse.append(JsonUtils.mapToJson(projects));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		bodyResponse.append("}");

		logger.info("Got projects: " + bodyResponse.toString());

		return ResponseEntity.status(HttpStatus.OK).header("Content-Type", "application/json")
				.body(bodyResponse.toString());
	}

	@RequestMapping("/reservation/batch/all")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> getAllBatches(HttpServletRequest request) {

		HttpSession session = request.isRequestedSessionIdValid() ? request.getSession() : null;
		if (session == null || session.getAttribute("username") == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Content-Type", "application/json")
					.body("{}");
		}

		logger.info("Getting all batches");
		List<String> batches = OsirisService.getAllBatches();
		StringBuffer bodyResponse = new StringBuffer("{");
		try {
			bodyResponse.append("\"batches\":");
			bodyResponse.append(JsonUtils.mapToJson(batches));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		bodyResponse.append("}");

		logger.info("Got batches: " + bodyResponse.toString());

		return ResponseEntity.status(HttpStatus.OK).header("Content-Type", "application/json")
				.body(bodyResponse.toString());
	}

	@RequestMapping("/reservation/batch/batches/{compoundName}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> getBatchesByCompound(@PathVariable String compoundName, HttpServletRequest request) {

		HttpSession session = request.isRequestedSessionIdValid() ? request.getSession() : null;
		if (session == null || session.getAttribute("username") == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Content-Type", "application/json")
					.body("{}");
		}

		logger.info("Getting batches for compound " + compoundName);
		List<BatchDto> batchDtos = OsirisService.getBatchesByActNo(compoundName);
		List<SampleDto> sampleDtos = OsirisService.getSamplesByActNo(compoundName);

		List<String> batches = new ArrayList<String>();
		for (BatchDto bdto : batchDtos) {
			batches.add(bdto.getChemLabJournal());
		}
		for (SampleDto sdto : sampleDtos) {
			batches.add(sdto.getExtReferfence());
		}

		Collections.sort(batches, new Comparator<String>() {
			@Override
			public int compare(String b1, String b2) {
				return b1.compareTo(b2);
			}
		});

		StringBuffer bodyResponse = new StringBuffer("{");
		try {
			bodyResponse.append("\"batches\":");
			bodyResponse.append(JsonUtils.mapToJson(batches));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		bodyResponse.append("}");

		logger.info("Got batches: " + bodyResponse.toString());

		return ResponseEntity.status(HttpStatus.OK).header("Content-Type", "application/json")
				.body(bodyResponse.toString());
	}

	@RequestMapping("/reservation/batch/batches/sample/{externalReference}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> getBatchesBySampleExternalReference(@PathVariable String externalReference,
			HttpServletRequest request) {

		HttpSession session = request.isRequestedSessionIdValid() ? request.getSession() : null;
		if (session == null || session.getAttribute("username") == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Content-Type", "application/json")
					.body("{}");
		}

		logger.info("Getting batches for sample external reference " + externalReference);
		List<BatchDto> batchDtos = OsirisService.getBatchesBySampleExternalReference(externalReference);
		Collections.sort(batchDtos, new Comparator<BatchDto>() {
			@Override
			public int compare(BatchDto b1, BatchDto b2) {
				return b1.getChemLabJournal().compareTo(b2.getChemLabJournal());
			}
		});
		List<String> batches = new ArrayList<String>();
		for (BatchDto dto : batchDtos) {
			batches.add(dto.getChemLabJournal());
		}
		StringBuffer bodyResponse = new StringBuffer("{");
		try {
			bodyResponse.append("\"batches\":");
			bodyResponse.append(JsonUtils.mapToJson(batches));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		bodyResponse.append("}");

		logger.info("Got batches: " + bodyResponse.toString());

		return ResponseEntity.status(HttpStatus.OK).header("Content-Type", "application/json")
				.body(bodyResponse.toString());
	}

	@RequestMapping("/reservation/sample/all")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> getAllSamples(HttpServletRequest request) {

		HttpSession session = request.isRequestedSessionIdValid() ? request.getSession() : null;
		if (session == null || session.getAttribute("username") == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Content-Type", "application/json")
					.body("{}");
		}

		logger.info("Getting all samples");
		List<String> samples = OsirisService.getAllSamples();
		StringBuffer bodyResponse = new StringBuffer("{");
		try {
			bodyResponse.append("\"samples\":");
			bodyResponse.append(JsonUtils.mapToJson(samples));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		bodyResponse.append("}");

		logger.info("Got samples: " + bodyResponse.toString());

		return ResponseEntity.status(HttpStatus.OK).header("Content-Type", "application/json")
				.body(bodyResponse.toString());
	}

	@RequestMapping("/reservation/sample/samples/{compoundName}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> getSamplesByCompound(@PathVariable String compoundName, HttpServletRequest request) {

		HttpSession session = request.isRequestedSessionIdValid() ? request.getSession() : null;
		if (session == null || session.getAttribute("username") == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Content-Type", "application/json")
					.body("{}");
		}

		logger.info("Getting samples for compound " + compoundName);
		List<SampleDto> sampleDtos = OsirisService.getSamplesByActNo(compoundName);
		Collections.sort(sampleDtos, new Comparator<SampleDto>() {
			@Override
			public int compare(SampleDto s1, SampleDto s2) {
				return s1.getExtReferfence().compareTo(s2.getExtReferfence());
			}
		});
		List<String> samples = new ArrayList<String>();
		for (SampleDto dto : sampleDtos) {
			samples.add(dto.getExtReferfence());
		}
		StringBuffer bodyResponse = new StringBuffer("{");
		try {
			bodyResponse.append("\"samples\":");
			bodyResponse.append(JsonUtils.mapToJson(samples));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		bodyResponse.append("}");

		logger.info("Got samples: " + bodyResponse.toString());

		return ResponseEntity.status(HttpStatus.OK).header("Content-Type", "application/json")
				.body(bodyResponse.toString());
	}

	@RequestMapping("/reservation/substance/all")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> getAllSubstances(HttpServletRequest request) {

		HttpSession session = request.isRequestedSessionIdValid() ? request.getSession() : null;
		if (session == null || session.getAttribute("username") == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Content-Type", "application/json")
					.body("{}");
		}

		logger.info("Getting all substances");
		List<String> substances = OsirisService.getAllSubstances();
		StringBuffer bodyResponse = new StringBuffer("{");
		try {
			bodyResponse.append("\"substances\":");
			bodyResponse.append(JsonUtils.mapToJson(substances));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		bodyResponse.append("}");

		logger.info("Got substances: " + bodyResponse.toString());

		return ResponseEntity.status(HttpStatus.OK).header("Content-Type", "application/json")
				.body(bodyResponse.toString());
	}

	@RequestMapping("/reservation/substance/{compoundName}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> getCompoundByName(@PathVariable String compoundName, HttpServletRequest request) {
		HttpSession session = request.isRequestedSessionIdValid() ? request.getSession() : null;
		if (session == null || session.getAttribute("username") == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Content-Type", "application/json")
					.body("{}");
		}
		logger.info("Getting compound " + compoundName);
		SubstanceDto compound = OsirisService.getSubstanceByName(compoundName);
		StringBuffer bodyResponse = new StringBuffer("{");
		try {
			bodyResponse.append("\"compound\":");
			bodyResponse.append(JsonUtils.mapToJson(compound));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		bodyResponse.append("}");
		logger.info("Got compound: " + bodyResponse.toString());
		return ResponseEntity.status(HttpStatus.OK).header("Content-Type", "application/json")
				.body(bodyResponse.toString());
	}

	@RequestMapping("/reservation/reservationUsage/reservationId/{reservationId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> getReservationUsageByResId(@PathVariable String reservationId,
			HttpServletRequest request) {
		HttpSession session = request.isRequestedSessionIdValid() ? request.getSession() : null;
		if (session == null || session.getAttribute("username") == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Content-Type", "application/json")
					.body("{}");
		}
		logger.info("Getting reservation usage  for reservation " + reservationId);
		List<ReservationUsageDto> reservationUsageDtos = LabInventoryService
				.getReservationUsageByReservationId(Integer.valueOf(reservationId));
		ReservationUsageDto target = reservationUsageDtos.isEmpty() ? new ReservationUsageDto()
				: reservationUsageDtos.iterator().next();
		StringBuffer bodyResponse = new StringBuffer("{");
		try {
			bodyResponse.append("\"reservationUsages\":");
			bodyResponse.append(JsonUtils.mapToJson(target));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		bodyResponse.append("}");
		logger.info("Got reservation usage: " + bodyResponse.toString());
		return ResponseEntity.status(HttpStatus.OK).header("Content-Type", "application/json")
				.body(bodyResponse.toString());
	}

	@RequestMapping("/reservation/instrumentDeputies/instrument/{instrumentId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> getDeputiesByInstrumentId(@PathVariable String instrumentId) {
		logger.info("Getting deputies for instrument " + instrumentId);
		List<String> deputies = LabInventoryService.getDeputiesByInstrumentId(Integer.valueOf(instrumentId));
		StringBuffer bodyResponse = new StringBuffer("{");
		try {
			bodyResponse.append("\"deputies\":");
			bodyResponse.append(JsonUtils.mapToJson(deputies));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		bodyResponse.append("}");
		logger.info("Got deputies: " + bodyResponse.toString());
		return ResponseEntity.status(HttpStatus.OK).header("Content-Type", "application/json")
				.body(bodyResponse.toString());
	}

	private ReservationDto getReservationFromJsonData(HttpServletRequest request, String jsonData)
			throws JsonProcessingException, NumberFormatException {
		ReservationDto reservationDto = new ReservationDto();

		JsonNode reservationIdNode = JsonUtils.getJsonNode(jsonData, "id");
		JsonNode resourceIdNode = JsonUtils.getJsonNode(jsonData, "resourceId");
		JsonNode fromDateNode = JsonUtils.getJsonNode(jsonData, "fromDate");
		JsonNode toDateNode = JsonUtils.getJsonNode(jsonData, "toDate");
		JsonNode remarkNode = JsonUtils.getJsonNode(jsonData, "remark");
		JsonNode resOptIdNode = JsonUtils.getJsonNode(jsonData, "resoptid");
		JsonNode ratioNode = JsonUtils.getJsonNode(jsonData, "ratio");
		if (resourceIdNode != null && fromDateNode != null && toDateNode != null) {
			Integer reservationId = null;
			String resourceId = resourceIdNode.textValue();
			Integer resId = null;
			String fromDate = fromDateNode.textValue();
			String toDate = toDateNode.textValue();
			String user = request.getSession().getAttribute("username").toString();
			String remark = "";
			String resOptId = null;
			Integer optId = null;
			Integer ratio = 0;

			if (reservationIdNode != null) {
				reservationId = reservationIdNode.asInt();
			} else {
				reservationId = 0;
			}
			if (remarkNode != null) {
				remark = remarkNode.textValue();
			}
			resId = Integer.valueOf(resourceId);
			if (resId > RESOURCE_ID_OFFSET) {
				resId -= RESOURCE_ID_OFFSET;
			}
			if (resOptIdNode != null) {
				resOptId = resOptIdNode.textValue();
				if (resOptId != null && !resOptId.isEmpty()) {
					optId = Integer.valueOf(resOptId);
				}
			}
			if (ratioNode != null) {
				ratio = ratioNode.asInt();
			}

			reservationDto.setId(reservationId);
			reservationDto.setInstrid(resId);
			reservationDto.setFromTime(MiscUtils.parseUTCDateWithOffset(fromDate));
			reservationDto.setToTime(MiscUtils.parseUTCDateWithOffset(toDate));
			reservationDto.setUsername(user.toUpperCase());
			reservationDto.setRemark(remark);
			if (optId > 0) {
				reservationDto.setResoptid(optId);
			}
			reservationDto.setRatio(ratio);
		}

		return reservationDto;
	}

	private ReservationUsageDto getReservationUsageFromJsonData(HttpServletRequest request, String jsonData,
			ReservationDto reservationDto) throws JsonProcessingException, NumberFormatException {
		ReservationUsageDto reservationUsageDto = null;

		JsonNode reservationUsageProjectNode = JsonUtils.getJsonNode(jsonData, "project");
		JsonNode reservationUsageCompoundNode = JsonUtils.getJsonNode(jsonData, "compound");
		JsonNode reservationUsageSampleNode = JsonUtils.getJsonNode(jsonData, "sample");
		JsonNode reservationUsageBatchNode = JsonUtils.getJsonNode(jsonData, "batch");
		JsonNode reservationUsageSampleTypeNode = JsonUtils.getJsonNode(jsonData, "sampleType");
		JsonNode reservationUsageSpecieNode = JsonUtils.getJsonNode(jsonData, "specie");

		if (reservationUsageProjectNode != null && reservationUsageBatchNode != null) {
			reservationUsageDto = new ReservationUsageDto();
			Integer reservationId = null;
			String project = reservationUsageProjectNode.textValue();
			String compound = reservationUsageCompoundNode != null ? reservationUsageCompoundNode.textValue() : "";
			String sample = reservationUsageSampleNode != null ? reservationUsageSampleNode.textValue() : "";
			String batch = reservationUsageBatchNode.textValue();
			String sampleType = reservationUsageSampleTypeNode != null? reservationUsageSampleTypeNode.textValue(): "";
			String specie = reservationUsageSpecieNode != null? reservationUsageSpecieNode.textValue(): "";
			
			if (reservationDto != null) {
				reservationId = reservationDto.getId();
			}
			reservationUsageDto.setReservationId(reservationId);
			reservationUsageDto.setProject(project);
			reservationUsageDto.setCompound(compound);
			reservationUsageDto.setSample(sample);
			reservationUsageDto.setBatch(batch);
			reservationUsageDto.setSampleType(sampleType);
			reservationUsageDto.setSpecie(specie);
		}
		return reservationUsageDto;
	}

	@RequestMapping("/reservation/instrumentEmployeeGroups/instrument/{instrumentId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> getEmployeeGroupsByInstrumentId(@PathVariable String instrumentId) {
		logger.info("Getting employee groups for instrument " + instrumentId);
		List<String> employeeGroups = LabInventoryService.getEmployeeGroupsByInstrumentId(instrumentId);
		StringBuffer bodyResponse = new StringBuffer("{");
		try {
			bodyResponse.append("\"employeeGroups\":");
			bodyResponse.append(JsonUtils.mapToJson(employeeGroups));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		bodyResponse.append("}");
		logger.info("Got employee groups: " + bodyResponse.toString());
		return ResponseEntity.status(HttpStatus.OK).header("Content-Type", "application/json")
				.body(bodyResponse.toString());
	}

	/**
	 * Controls if the overlapping reservation is possible by calculating max ratios
	 * max ratio must not exceed 100 %
	 * 
	 * @param reservationDto
	 * @param requestedRatio
	 * @param t1
	 * @param t2
	 * @return
	 */
	private boolean overlapValidation(ReservationDto reservationDto, int requestedRatio, Date t1, Date t2) {
		List<Integer> instrIds = Arrays.asList(reservationDto.getInstrid());
		Date endDate = (Date) t2.clone();
		boolean acceptOverlap = true;
		List<ReservationDto> reservations = null;
		List<ReservationDto> reservationsSubset = null;
		ReservationDto res = null;

		do {
			reservations = LabInventoryService.getReservationsByInstrIdsAndDateRange(instrIds, reservationDto.getId(),
					new Timestamp(t1.getTime()), new Timestamp(t2.getTime()), true);
			if (reservations == null || reservations.size() == 0) {
				break;
			}
			res = getFirstEndingReservation(reservations, t2);
			if (res == null || res.getToTime().after(t2)) { // all res endings are beyond t2
				if (getMaxRatio(reservations) + requestedRatio > 100) {
					acceptOverlap = false;
					break;
				}
			} else {
				t2 = res.getToTime();
				reservationsSubset = LabInventoryService.getReservationsByInstrIdsAndDateRange(instrIds, 0,
						new Timestamp(t1.getTime()), new Timestamp(t2.getTime()), false);
				if (getMaxRatio(reservationsSubset) + requestedRatio > 100) {
					acceptOverlap = false;
					break;
				}
				t1 = t2;
				t2 = endDate;
			}
		} while (t1.compareTo(t2) < 0 && res != null);

		return acceptOverlap;
	}

	private ReservationDto getFirstEndingReservation(List<ReservationDto> reservations, Date limit) {
		ReservationDto ret = null;
		int resCount = reservations.size();
		if (resCount == 0) {
			return null;
		}
		ret = reservations.get(0);
		ReservationDto currentRes = null;
		ReservationDto nextRes = null;
		for (int i = 0; i < reservations.size() - 1; i++) {
			currentRes = reservations.get(i);
			nextRes = reservations.get(i + 1);
			if (currentRes.getToTime().compareTo(nextRes.getToTime()) > 0) {
				ret = nextRes;
			}
		}
		if (ret.getToTime().after(limit)) {
			ret = null;
		}

		return ret;
	}

	private int getMaxRatio(List<ReservationDto> reservations) {
		int maxRatio = 0;
		if (reservations != null) {
			for (ReservationDto res : reservations) {
				maxRatio += res.getRatio();
			}
		}
		return maxRatio;
	}

	public String getUserGroups(String connectedUser) {
		if (connectedUser != null && !"".equals(connectedUser)) {
			StringBuilder sb = new StringBuilder();
			// List<String> userGroups =
			// OsirisService.getGroupsByusername(connectedUser.toLowerCase());
			List<String> userGroups = OsirisService.getGroupsIdsByUserId(connectedUser.toLowerCase());
			for (int i = 0; i < userGroups.size(); i++) {
				sb.append(userGroups.get(i));
				if (i < userGroups.size() - 1) {
					sb.append(";");
				}
			}
			return sb.toString();
		}
		return null;
	}

	public String getInstrumentEmployeeGroups(String instrumentId) {
		StringBuilder sb = new StringBuilder("");
		List<String> instrumentEmployeeGroups = LabInventoryService.getEmployeeGroupsByInstrumentId(instrumentId);
		for (int i = 0; i < instrumentEmployeeGroups.size(); i++) {
			sb.append(instrumentEmployeeGroups.get(i));
			if (i < instrumentEmployeeGroups.size() - 1) {
				sb.append(";");
			}
		}
		return sb.toString();
	}

	private boolean isOverlapingWithRestrictedInstrument(ReservationDto restrictedResa, Date fromDate, Date toDate) {
		Date restrictedResaStart = restrictedResa.getFromTime();
		Date restrictedResaEnd = restrictedResa.getToTime();
		return (fromDate.after(restrictedResaStart) && fromDate.before(restrictedResaEnd))
				|| (toDate.after(restrictedResaStart) && toDate.before(restrictedResaEnd))
				|| (fromDate.before(restrictedResaStart) && toDate.after(restrictedResaEnd))
				|| (fromDate.after(restrictedResaStart) && toDate.before(restrictedResaEnd))
				|| (DateUtils.isSameDay(fromDate, restrictedResaStart)
						&& DateUtils.isSameDay(toDate, restrictedResaEnd));
	}

	private String getRestrictedInstrumentsName(List<Integer> restrictedInstrumentids) {
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < restrictedInstrumentids.size(); i++) {
			InstrumentDto inst = LabInventoryService.getInstrumentById(restrictedInstrumentids.get(i));
			sb.append(inst != null ? inst.getName() : "");
			sb.append(i < restrictedInstrumentids.size() - 1 ? ", " : "");
		}
		return sb.toString();
	}

	// Favorite controller
	@RequestMapping("/favorites-reservation")
	public ModelAndView displayFavTimeline(HttpServletRequest request, Model model) {
		HttpSession session = request.isRequestedSessionIdValid() ? request.getSession() : null;
		if (session == null || session.getAttribute("username") == null) {
			return new ModelAndView("redirect:/home");
		}
		String username = (String) session.getAttribute("username");
		String userGroups = getUserGroups(username);
		ModelAndView mv = new ModelAndView("/favorites-reservation");
		mv.addObject("userGroups", userGroups);

		StringBuffer resources = new StringBuffer();
		Map<Integer, InstrumentDto> instrumentsMap = new HashMap<>();
		List<InstrumentDto> instruments = new ArrayList<InstrumentDto>();
		List<InstrumentDto> allInstruments = LabInventoryService.getAllInstruments();
		List<FavoriteDto> allUserfavorite = LabInventoryService.getUserFavorites(username.toUpperCase());
		List<Integer> userInstrumentIdList = new ArrayList<Integer>();
		for (FavoriteDto fav : allUserfavorite) {
			userInstrumentIdList.add(fav.getInstrid());
		}
		for (InstrumentDto inst : allInstruments) {
			if (userInstrumentIdList.contains(Integer.valueOf(inst.getId()))) {
				instruments.add(inst);
			}
		}
		Map<Integer, List<InstrumentPriorityUsersDto>> instPrioUsrMap = getPrioUsersMap();
		List<Integer> allowedInstForResaUsg = LabInventoryService.getAllowedInstrumentForReservationUsages();
		boolean isPriorityUsersSet = false;
		boolean isPriorityUser = false;
		for (InstrumentDto inst : instruments) {
			if(!MiscUtils.intToBoolean(inst.getReservable())) {
				continue;
			}
			if (!favGroupInstrumentIdsMap.keySet().contains(inst.getGroupname())) {
				favGroupInstrumentIdsMap.put(inst.getGroupname(), new ArrayList<>());
			}
			if(instPrioUsrMap.get(inst.getId())!=null) {
				isPriorityUsersSet = true;
				for(InstrumentPriorityUsersDto ipu : instPrioUsrMap.get(inst.getId())) {
					if(ipu.getPriorityUser().equalsIgnoreCase(username)) {
						isPriorityUser = true;
					}
				}
			}
			resources.append("{");
			resources.append("id:");
			resources.append("'" + inst.getId() + "',");
			resources.append("extendedProps: {groupname:");
			resources.append("'" + inst.getGroupname() + "',");
			resources.append("rootId:" + inst.getId() + ",");
			resources.append("stepIncrement:" + inst.getStepIncrement() + ",");
			resources.append("instrumentID:" + inst.getId() + ",");
			
			resources.append("highlightComment:" + inst.getHighlightComment() + ",");
			Integer normalUsersOpen = null;
			resources.append("normalUsersOpen:" + normalUsersOpen + ",");
			resources.append("maxDays:" + inst.getMaxDays() + ",");
			resources.append("isPriorityUsersSet:" + isPriorityUsersSet + ",");
			resources.append("isPriorityUser:" + isPriorityUser + ",");
			resources.append("allowedInstForResaUsg:" + allowedInstForResaUsg + ",");			
			
			resources.append("selectOverlap:" + inst.getSelectOverlap() + "},");
			resources.append("title:");
			resources.append("'" + inst.getName() + "',");
			resources.append("instrumentEmployeeGroups:");
			resources.append("'" + getInstrumentEmployeeGroups(String.valueOf(inst.getId())) + "',");
			resources.append("ratioComment:");
			String ratioComment = inst.getRatioComment() != null ? inst.getRatioComment() : "";
			resources.append("'" + ratioComment + "'},");

			// populate instrument map
			instrumentsMap.put(inst.getId(), inst);
			// populate instrument grouped by groups (Biology, Tecan...)
			favGroupInstrumentIdsMap.get(inst.getGroupname()).add(inst.getId());
		}

		request.setAttribute("calendarResources", resources.toString());

		return mv;
	}

	@RequestMapping("/logbook")
	public String logbook(ReservationHistoryDto reservationHistoryDto, Model model) {
		Map<Integer, String> instrumentMap = getInstrumentList();
		String instrumentMultiSelectHtml;
		if (reservationHistoryDto.getInstrIds() != null && reservationHistoryDto.getStartDate() != null
				&& reservationHistoryDto.getEndDate() != null) {
			String start = reservationHistoryDto.getStartDate();
			String end = reservationHistoryDto.getEndDate();
			model.addAttribute("requestStartDate", start);
			model.addAttribute("requestEndDate", end);
			reservationHistoryDto.setStartDate(start + HMS);
			reservationHistoryDto.setEndDate(end + HMS);
			List<ReservationDto> reservations = LabInventoryService
					.getReservationsByInstrIdsAndDateRange(reservationHistoryDto);
			for (ReservationDto resa : reservations) {
				// add option and usages
				InstrumentDto inst = LabInventoryService.getInstrumentById(resa.getInstrid());
				resa.setInstrumentName(inst.getName());
				resa.setInstrumentOwner(inst.getUsername());
				resa.setFromTimeToDisplay(FORMATER.format(resa.getFromTime()));
				resa.setToTimeToDisplay(FORMATER.format(resa.getToTime()));
				if (resa.getResoptid() != null) {
					ResoptionDto resOptions = LabInventoryService.getResoptionById(resa.getResoptid());
					resa.setReservationOptions(resOptions.getName());
				}
				List<ReservationUsageDto> resaUsages = LabInventoryService
						.getReservationUsageByReservationId(resa.getId());
				if (resaUsages != null && !resaUsages.isEmpty()) {
					resa.setReservationUsageDto(resaUsages.get(0));
				} else {
					resa.setReservationUsageDto(new ReservationUsageDto());
				}
			}
			model.addAttribute("reservations", reservations);
			instrumentMultiSelectHtml = buildHtmlMultiSelect(instrumentMap, reservationHistoryDto.getInstrIds());
		} else {
			model.addAttribute("requestStartDate", "");
			model.addAttribute("requestEndDate", "");
			instrumentMultiSelectHtml = buildHtmlMultiSelect(instrumentMap, new ArrayList<Integer>());
		}
		model.addAttribute("instrumentMultiSelectHtml", instrumentMultiSelectHtml);
		model.addAttribute("department", "reservation");
		return "logbook";

	}

	public static Map<Integer, String> getInstrumentList() {
		List<InstrumentDto> instruments = LabInventoryService.getAllInstruments();
		Collections.sort(instruments, new Comparator<InstrumentDto>() {
			@Override
			public int compare(InstrumentDto i1, InstrumentDto i2) {
				return i1.getName().compareTo(i2.getName());
			}
		});
		Map<Integer, String> instrumentsMap = new LinkedHashMap<Integer, String>();
		for (InstrumentDto inst : instruments) {
			instrumentsMap.put(inst.getId(), inst.getName());
		}
		return instrumentsMap;
	}

	public static String buildHtmlMultiSelect(Map<Integer, String> instrumentsMap, List<Integer> requestedInstId) {
		StringBuilder sb = new StringBuilder(
				"<select multiple class=\"filter-multi-select\" name=\"instrIds\" id=\"instrSelect\">");
		for (Entry<Integer, String> entry : instrumentsMap.entrySet()) {
			if (requestedInstId.contains(entry.getKey())) {
				sb.append("<option value=\"").append(entry.getKey()).append("\" selected>").append(entry.getValue())
						.append("</option>");
			} else {
				sb.append("<option value=\"").append(entry.getKey()).append("\">").append(entry.getValue())
						.append("</option>");
			}
		}
		sb.append("</select>");
		return sb.toString();
	}

	@RequestMapping("/reservation/ratioleft/{instrId}/{from}/{to}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> getReservationRatioLeft(@PathVariable String instrId, @PathVariable String from,
			@PathVariable String to, HttpServletRequest request) {		
		HttpSession session = request.isRequestedSessionIdValid() ? request.getSession() : null;
		if (session == null || session.getAttribute("username") == null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Content-Type", "application/json")
					.body("{}");
		}
		logger.info("Getting ratio left for instrument " + instrId);
		
		int instrumentId = Integer.valueOf(instrId);
		InstrumentDto inst = LabInventoryService.getInstrumentById(instrumentId);
		
		InstrResaModel resp = new InstrResaModel();		
		resp.setInstrumentId(instrumentId);
		resp.setStep(inst.getStepIncrement() == 0 ? 1 :inst.getStepIncrement());
		
		Integer ratioLeft = 100;
		List<ReservationDto> resaOnTheSameSlot = LabInventoryService
				.getReservationsByInstrIdDateRange(instrumentId, Long.valueOf(from), Long.valueOf(to));
		
		StringBuffer bodyResponse = new StringBuffer("");
		try {
			for (ReservationDto resa : resaOnTheSameSlot) {
				if (resa.getRatio() != null) {
					ratioLeft -= resa.getRatio();
				}
			}
			if (ratioLeft < 0) {ratioLeft = 0;}
			resp.setRatioLeft(ratioLeft);
			if(!resaOnTheSameSlot.isEmpty()) {
				if(resaOnTheSameSlot.get(0).getResoptid() != null) {					
					resp.setResoptid(resaOnTheSameSlot.get(0).getResoptid());
				}
				resp.setStartDate(resaOnTheSameSlot.get(0).getFromTime());
				resp.setEndDate(resaOnTheSameSlot.get(0).getToTime());
			}			
			bodyResponse.append(JsonUtils.mapToJson(resp));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		logger.info("Got ratio left: " + bodyResponse.toString());
		return ResponseEntity.status(HttpStatus.OK).header("Content-Type", "application/json")
				.body(bodyResponse.toString());
	}
	
	Map<Boolean, Map<Date,Date>> checkIfValidPrio(Date from, Date to, int instrumentId, String user){
		Map<Boolean, Map<Date,Date>> res = new HashMap<Boolean, Map<Date,Date>>();
		Map<Date,Date> rangeDates = new HashMap<Date, Date>();
		InstrumentDto inst = LabInventoryService.getInstrumentById(instrumentId);
		List<String> prioUsers = LabInventoryService.getPriorityUsersByInstrumentId(inst.getId());
		if(prioUsers != null && !prioUsers.isEmpty()) {
			int maxDays = inst.getMaxDays();
			String openStartTimepoint = inst.getStartTimepoint();
			if(!prioUsers.contains(user.toUpperCase())) {
				LocalDateTime fromLocalDateTime = MiscUtils.convertDateToLocalDateTime(from);
				LocalDateTime toLocalDateTime = MiscUtils.convertDateToLocalDateTime(to);
				
				LocalDateTime now = LocalDateTime.now();
				int hour = Integer.valueOf(StringUtils.substringBefore(openStartTimepoint, ":"));
				int minute = Integer.valueOf(StringUtils.substringAfter(openStartTimepoint, ":"));
				LocalDateTime openStartLocalTime = now.withHour(hour).withMinute(minute).withSecond(0);
				
				LocalDateTime minimumAllowedEndLocalDateTime;
				LocalDateTime maximumAllowedEndLocalDateTime;
				if(now.isBefore(openStartLocalTime)) {//before timepoint defined, can book until 9pm today
					minimumAllowedEndLocalDateTime = now.withHour(06).withMinute(0).withSecond(0);
					maximumAllowedEndLocalDateTime = now.withHour(21).withMinute(0).withSecond(0);

				}else {//after timepoint defined, can book until 9pm tomorrow
					minimumAllowedEndLocalDateTime = now.plusDays(1).withHour(06).withMinute(0).withSecond(0);
					maximumAllowedEndLocalDateTime = now.plusDays(maxDays).withHour(21).withMinute(0).withSecond(0);
				}
				boolean notInAllowedSlot = fromLocalDateTime.isBefore(minimumAllowedEndLocalDateTime)
						|| toLocalDateTime.isBefore(minimumAllowedEndLocalDateTime)
						|| fromLocalDateTime.isAfter(maximumAllowedEndLocalDateTime)
						|| toLocalDateTime.isAfter(maximumAllowedEndLocalDateTime);
				
				if(notInAllowedSlot) {
					rangeDates.put(MiscUtils.convertLocalDateTimeToDate(minimumAllowedEndLocalDateTime), MiscUtils.convertLocalDateTimeToDate(maximumAllowedEndLocalDateTime));
					res.put(false, rangeDates);		
					return res;
				}
			}
		}
		res.put(true, rangeDates);		
		return res;
	}

}
