package org.research.kadda.labinventory.ui;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.research.kadda.labinventory.LabInventoryService;
import org.research.kadda.labinventory.data.FavoriteDto;
import org.research.kadda.labinventory.data.GroupInstrumentDto;
import org.research.kadda.labinventory.data.InstrumentDeputyDto;
import org.research.kadda.labinventory.data.InstrumentDto;
import org.research.kadda.labinventory.data.InstrumentGroupDto;
import org.research.kadda.labinventory.data.InstrumentPriorityUsersDto;
import org.research.kadda.labinventory.data.JsonUtils;
import org.research.kadda.labinventory.data.ResOptionLinkDto;
import org.research.kadda.labinventory.data.ReservationDto;
import org.research.kadda.labinventory.data.ReservationUsageDto;
import org.research.kadda.labinventory.data.ResoptionDto;
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

import com.actelion.research.security.entity.User;
import com.actelion.research.security.exception.DataReadAccessException;
import com.actelion.research.security.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.research.kadda.osiris.OsirisService;

@Controller
@Scope("session")
public class InstrumentController {

	private static Logger logger = LogManager.getLogger(InstrumentController.class);
	private static final String CAN_DELETE_GROUP = "Applications";

	@RequestMapping("/home")
	public ModelAndView displayHome(HttpServletRequest request, Model model) {
		ModelAndView mv = new ModelAndView("/home");
		return mv;
	}	
	
	@RequestMapping("/instrumentlist")
	public ModelAndView displayInstrumentList(HttpServletRequest request, Model model) {
		HttpSession session = request.isRequestedSessionIdValid() ? request.getSession() : null;
		if (session == null || session.getAttribute("username") == null) {
			return new ModelAndView("redirect:/home");
		}

		ModelAndView mv = new ModelAndView("/instrumentlist");

		String username = session.getAttribute("username").toString();
		List<FavoriteDto> savedFavorites = LabInventoryService.getUserFavorites(username.toUpperCase());
		List<Integer> intrumentIdList = new ArrayList<Integer>();
		if(savedFavorites != null && savedFavorites.size() >= 1) {			
			for(FavoriteDto fav : savedFavorites) {
				intrumentIdList.add(fav.getInstrid());
			}
		}

		List<InstrumentDto> instruments  = LabInventoryService.getAllInstruments();
		if(instruments != null) {
			Collections.sort(instruments, Collections.reverseOrder(new Comparator<InstrumentDto>() {
				@Override
				public int compare(InstrumentDto i1, InstrumentDto i2) {
					return i2.getName().compareTo(i1.getName());
				}
			}));
			for(InstrumentDto inst : instruments) {
				if(intrumentIdList.contains(inst.getId())) {
					inst.setFavorite(1);
				}else {
					inst.setFavorite(0);
				}
				List<String> deputies  = LabInventoryService.getDeputiesByInstrumentId(inst.getId());
				StringBuilder depSb = stringify(deputies);
				inst.setDeputiesAsJson(depSb.toString());
				
				List<String> priorityUsers  = LabInventoryService.getPriorityUsersByInstrumentId(inst.getId());
				StringBuilder puSb = stringify(priorityUsers);
				inst.setPriorityUsersAsJson(puSb.toString());
				
			}
		}

		List<String> employees = OsirisService.getScientificEmployees();
		List<ResoptionDto> resOptions = LabInventoryService.getAllResoptions();
		List<String> allResaOption = new ArrayList<String>();
		for(ResoptionDto resOpt:resOptions) {
			allResaOption.add(resOpt.getName());
		}
		List<String> instrumentGroupNames = new ArrayList<String>();
		List<InstrumentGroupDto> instrumentGroups = LabInventoryService.getAllInstrumentGroups();
		for(InstrumentGroupDto instGrp : instrumentGroups) {
			instrumentGroupNames.add(instGrp.getName());
		}
		
		boolean canDeleteInstrument = false;
		
		try {
			UserService userService = new UserService();
			User usr = userService.loadUserByUsername(username, true, true);
			canDeleteInstrument = CAN_DELETE_GROUP.equalsIgnoreCase(usr.getDepartment());
		} catch (DataReadAccessException e) {
			e.printStackTrace();
		}

		mv.addObject("instruments", instruments);
		mv.addObject("connectedUser", username);
		mv.addObject("allUsers", employees);
		mv.addObject("allResaOption", allResaOption);
		mv.addObject("instrumentGroupNames", instrumentGroupNames);
		mv.addObject("canDeleteInstrument", canDeleteInstrument);
		return mv;
	}

	private StringBuilder stringify(List<String> values) {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<values.size(); i++) {
			sb.append(values.get(i));
			if(i<values.size()-1) {
				sb.append(", ");
			}
		}
		return sb;
	}
	
	@RequestMapping(method = RequestMethod.POST, path = "/instrument/add")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> addInstrument(HttpServletRequest request, @RequestBody String jsonData) {
		logger.info("Adding Instrument");
		logger.info("JsonData Received : " + jsonData);
		InstrumentDto instrumentDto = null;
		HttpStatus status = HttpStatus.CREATED;
		boolean success = true;
		jsonData = JsonUtils.formatUTF8(jsonData);
		//extract data from json
		List<ResoptionDto> instrResaOptions = new ArrayList<ResoptionDto>();
		List<InstrumentGroupDto> instrGroups = new ArrayList<InstrumentGroupDto>();
		try {
			instrumentDto = getInstrumentFromJsonData(request, jsonData);
			List<String> deputies = new ArrayList<String>();			
			instrumentDto.setDeputies(deputies);
			List<String> priorityUsers = new ArrayList<String>();		
			instrumentDto.setPriorityUsers(priorityUsers);
			if(instrumentDto.getMultiOptionFieldAsJson() != null) {
				for(JsonNode jsonNode : JsonUtils.getJsonNode(instrumentDto.getMultiOptionFieldAsJson(), "addinstrument-deputies")) {
					String deputy = jsonNode.asText();
					deputies.add(deputy);
				}
				Map<String, ResoptionDto> resaOptionsMap = getResaOptionsMap();
				for(JsonNode jsonNode : JsonUtils.getJsonNode(instrumentDto.getMultiOptionFieldAsJson(), "addReservationOptions")) {
					String resaOptionName = jsonNode.asText();
					if(resaOptionsMap.get(resaOptionName)!=null) {
						instrResaOptions.add(resaOptionsMap.get(resaOptionName));
					}
				}
				Map<String, InstrumentGroupDto> instrumentGroupMap = getInstrumentGroupMap();
				for(JsonNode jsonNode : JsonUtils.getJsonNode(instrumentDto.getMultiOptionFieldAsJson(), "addInstrumentGroup")) {
					String instrumentGroupName = jsonNode.asText();
					if(instrumentGroupMap.get(instrumentGroupName)!=null) {
						instrGroups.add(instrumentGroupMap.get(instrumentGroupName));
					}
				}
			}
		} catch (IOException ioe) {
			logger.error("The Json data is not well formatted");
			ioe.printStackTrace();
			success = false;
		}

		StringBuffer bodyResponse = new StringBuffer("");
		Map<Boolean, String> checkValidInstMap = validateInstrument(instrumentDto);
		if(checkValidInstMap.keySet().iterator().next()) {			
			int instId = LabInventoryService.addInstrument(instrumentDto);
			if (instId > 0) {
				try {
					for(String dep : instrumentDto.getDeputies()) {
						InstrumentDeputyDto intsDep = new InstrumentDeputyDto(null, instId, dep);
						LabInventoryService.addInstrumentDeputy(intsDep);
					}
					for(ResoptionDto resopt : instrResaOptions) {
						ResOptionLinkDto resOptLnk = new ResOptionLinkDto(instId, resopt.getId());
						LabInventoryService.addReservationOptionLink(resOptLnk);
					}
					for(InstrumentGroupDto insGrp : instrGroups) {
						GroupInstrumentDto grpInst = new GroupInstrumentDto(instId, insGrp.getId());
						LabInventoryService.addGroupInstrument(grpInst);
					}
					bodyResponse.append(JsonUtils.mapToJson(instrumentDto));
				} catch (JsonProcessingException e) {
					e.printStackTrace();
					success = false;
				}
			}
		}else {
			success = false;
		}

		if (!success) {
			String errorMessage = checkValidInstMap.values().iterator().next();
			status = HttpStatus.FORBIDDEN;
			bodyResponse = new StringBuffer("{\"message\":\"Cannot add instrument.\",\"statusCode\":\"" + status.toString() + "\",\"errorMessage\":\"" + errorMessage + "\"}");
		}

		return ResponseEntity.status(status)
				.header("Content-Type", "application/json")
				.body(bodyResponse.toString());
	}

	@RequestMapping(method = RequestMethod.DELETE, path = "/instrument/delete/{instId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> deleteInstrument(@PathVariable(value = "instId") String instId) {
		logger.info("Deleting intrument");
		StringBuffer bodyResponse = new StringBuffer("{");
		HttpStatus status = HttpStatus.OK;
		boolean fail = false;
		InstrumentDto intrumentToRemove = LabInventoryService.getInstrumentById(Integer.valueOf(instId));
		try {
			//cascade: 
			LabInventoryService.deleteFavoriteByInstrument(instId);
			LabInventoryService.deleteGroupInstrument(instId);				
			LabInventoryService.deleteInstrumentDeputies(instId);		
			LabInventoryService.deleteInstrumentGroupEmployee(instId);
			LabInventoryService.deleteInstrumentPriorityUser(instId);
			LabInventoryService.deleteReservationOptionLinks(instId);
			List<ReservationDto> instrumentReservations = LabInventoryService.getReservationsByInstrId(intrumentToRemove.getId());
			for(ReservationDto resa : instrumentReservations) {
				String resaId = String.valueOf(resa.getId());
				List<ReservationUsageDto> resaUsages = LabInventoryService.getReservationUsageByReservationId(resa.getId());
				for(ReservationUsageDto ru : resaUsages) {
					LabInventoryService.deleteReservationUsage(String.valueOf(ru.getId()));
				}
				LabInventoryService.deleteReservation(resaId);
			}
			LabInventoryService.deleteInstrument(instId);
		} catch (Exception e) {
			fail = true;
			e.printStackTrace();
		}
		if (fail) {
			status = HttpStatus.BAD_REQUEST;
            bodyResponse.append("\"message\":\""+ LabInventoryService.getResponseMessage() +"\"");
		} else {
			try {
				bodyResponse.append("\"instrument\":");
                bodyResponse.append(JsonUtils.mapToJson(intrumentToRemove));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		bodyResponse.append("}");
        return ResponseEntity.status(status)
                .header("Content-Type", "application/json")
                .body(bodyResponse.toString());
	}
	
	
	private Map<String, ResoptionDto> getResaOptionsMap() {
		Map<String, ResoptionDto> resaOptionsMap = new HashMap<String, ResoptionDto>();
		List<ResoptionDto> allResaOptions = LabInventoryService.getAllResoptions();
		for(ResoptionDto resOpt : allResaOptions) {
			resaOptionsMap.put(resOpt.getName(), resOpt);
		}
		return resaOptionsMap;
	}

	private Map<String, InstrumentGroupDto> getInstrumentGroupMap() {
		Map<String, InstrumentGroupDto> instGroupMap = new HashMap<String, InstrumentGroupDto>();
		List<InstrumentGroupDto> instGroups = LabInventoryService.getAllInstrumentGroups();
		for(InstrumentGroupDto instgrp : instGroups) {
			instGroupMap.put(instgrp.getName(), instgrp);
		}
		return instGroupMap;
	}
	
	@RequestMapping(method = RequestMethod.PUT, path="/instrument/update")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> updateInstrument(HttpServletRequest request, @RequestBody String jsonData) {
		logger.info("Update instrument...");
		logger.info("JsonData Received : " + jsonData);
		InstrumentDto newInstrumentDto = null;
		InstrumentDto instrumentDto = null;
		HttpStatus status = HttpStatus.CREATED;
		StringBuffer bodyResponse = new StringBuffer("");
		boolean success = true;
		jsonData = JsonUtils.formatUTF8(jsonData);
		//extract data from json
		try {
			newInstrumentDto = getInstrumentFromJsonData(request, jsonData);
			List<String> deputies = new ArrayList<String>();
			newInstrumentDto.setDeputies(deputies);
			List<String> priorityUsers = new ArrayList<String>();
			newInstrumentDto.setPriorityUsers(priorityUsers);
			if(newInstrumentDto.getMultiOptionFieldAsJson() != null) {
				for(JsonNode jsonNode : JsonUtils.getJsonNode(newInstrumentDto.getMultiOptionFieldAsJson(), "instrument-deputies")) {
					String deputy = jsonNode.asText();
					deputies.add(deputy);
				}
				for(JsonNode jsonNode : JsonUtils.getJsonNode(newInstrumentDto.getMultiOptionFieldAsJson(), "instrument-priority-users")) {
					String priorityUser = jsonNode.asText();
					priorityUsers.add(priorityUser);
				}
			}
			instrumentDto = LabInventoryService.getInstrumentById(newInstrumentDto.getId());
			instrumentDto.setName(newInstrumentDto.getName());
			instrumentDto.setDescription(newInstrumentDto.getDescription());
			instrumentDto.setStatus(newInstrumentDto.getStatus());
			instrumentDto.setLocation(newInstrumentDto.getLocation());
			instrumentDto.setReservable(newInstrumentDto.getReservable());
			instrumentDto.setDeputies(newInstrumentDto.getDeputies());
			instrumentDto.setPriorityUsers(newInstrumentDto.getPriorityUsers());
			instrumentDto.setUsername(newInstrumentDto.getUsername().toUpperCase());
			if(newInstrumentDto.getRatioComment() != null) {
				String ratioComment = newInstrumentDto.getRatioComment();
				String[] ratioCommentArray = ratioComment.split("[\r\n]+");
				StringBuilder sb = new StringBuilder();
				for(String s : ratioCommentArray) {
					sb.append(s).append(" ");
				}
				instrumentDto.setRatioComment(sb.toString());
			}
			instrumentDto.setEmailNotification(newInstrumentDto.getEmailNotification());
			instrumentDto.setSelectOverlap(newInstrumentDto.getSelectOverlap());
			instrumentDto.setStepIncrement(newInstrumentDto.getStepIncrement());
			instrumentDto.setStartTimepoint(newInstrumentDto.getStartTimepoint());
			instrumentDto.setMaxDays(newInstrumentDto.getMaxDays());
			instrumentDto.setHighlightComment(newInstrumentDto.getHighlightComment());
		} catch (IOException ioe) {
			logger.error("The Json data is not well formatted");
			ioe.printStackTrace();
			success = false;
		}
		
		Map<Boolean, String> checkValidInstMap = validateInstrument(instrumentDto);
		if(checkValidInstMap.keySet().iterator().next()) {
			if (LabInventoryService.updateInstrument(instrumentDto)) {
				try {
					deleteInstrumentDeputies(String.valueOf(instrumentDto.getId()));
					for(String dep : instrumentDto.getDeputies()) {
						InstrumentDeputyDto intsDep = new InstrumentDeputyDto(null, instrumentDto.getId(), dep);
						LabInventoryService.addInstrumentDeputy(intsDep);
					}
					deleteInstrumentPriorityUsers(String.valueOf(instrumentDto.getId()));
					for(String prioUsr : instrumentDto.getPriorityUsers()) {
						InstrumentPriorityUsersDto intsPrioUsr = new InstrumentPriorityUsersDto(null, instrumentDto.getId(), prioUsr);
						LabInventoryService.addInstrumentPriorityUser(intsPrioUsr);
					}
					bodyResponse.append(JsonUtils.mapToJson(instrumentDto));
				} catch (JsonProcessingException e) {
					e.printStackTrace();
					success = false;
				}
			} else {
				success = false;
			}			
		}else {
			success = false;
		}

		if (!success) {
			String errorMessage = checkValidInstMap.values().iterator().next();
			status = HttpStatus.FORBIDDEN;
			bodyResponse = new StringBuffer("{\"message\":\"Cannot update instrument.\",\"statusCode\":\"" + status.toString() + "\",\"errorMessage\":\"" + errorMessage + "\"}");
		}

		return ResponseEntity.status(status)
				.header("Content-Type", "application/json")
				.body(bodyResponse.toString());
	}


    private Map<Boolean, String> validateInstrument(InstrumentDto instrumentDto) {
    	Map<Boolean, String> resMap = new HashMap<Boolean, String>();
    	Boolean res = true;
    	StringBuilder sb = new StringBuilder("Following fields are missing: ");
    	
		if("".equals(instrumentDto.getName()) || instrumentDto.getName() == null) {
			res = false;
			sb.append("Instrument Name ");
		}
		if("".equals(instrumentDto.getUsername()) || instrumentDto.getUsername() == null) {
			res = false;
			sb.append(" Owner ");
		}
		if(StringUtils.startsWith(instrumentDto.getStatus(), "Choose Status...")) {
			res = false;
			sb.append(" Status ");
		}
		if(StringUtils.startsWith(instrumentDto.getStatus(), "Choose Group...")) {
			res = false;
			sb.append(" Group ");
		}
		if(instrumentDto.getPriorityUsers() != null && !instrumentDto.getPriorityUsers().isEmpty()) {
			if("".equals(instrumentDto.getStartTimepoint()) || instrumentDto.getStartTimepoint() == null || instrumentDto.getMaxDays() == null) {				
				res = false;
				sb.append(" Priority User, Timepoint and Retention must be filled together");
			}
		}else {
			if ((!"".equals(instrumentDto.getStartTimepoint()) && instrumentDto.getStartTimepoint() != null)
					|| instrumentDto.getMaxDays() != null) {				
				res = false;
				sb.append(" Priority User, Timepoint and Retention must be filled together");
			}
		}

		resMap.put(res, sb.toString());
		return resMap;
	}

	@RequestMapping(method = RequestMethod.DELETE, path="/instrumentDeputy/delete/{instrumentId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteInstrumentDeputies(@PathVariable(value = "instrumentId") String instrumentId) {
        logger.info("Deleting deputies");
        StringBuffer bodyResponse = new StringBuffer("{");
        HttpStatus status = HttpStatus.OK;

        if (!LabInventoryService.deleteInstrumentDeputies(instrumentId)) {
            status = HttpStatus.BAD_REQUEST;
            bodyResponse.append("\"message\":\""+ LabInventoryService.getResponseMessage() +"\"");
        } else {
            bodyResponse.append("\"deputies removed\":");
            bodyResponse.append("\"message\":\""+ LabInventoryService.getResponseMessage() +"\"");
        }
        bodyResponse.append("}");

        return ResponseEntity.status(status)
                .header("Content-Type", "application/json")
                .body(bodyResponse.toString());
    }
    
    @RequestMapping(method = RequestMethod.DELETE, path="/instrumentPriorityUsers/delete/{instrumentId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteInstrumentPriorityUsers(@PathVariable(value = "instrumentId") String instrumentId) {
        logger.info("Deleting priority users");
        StringBuffer bodyResponse = new StringBuffer("{");
        HttpStatus status = HttpStatus.OK;

        if (!LabInventoryService.deleteInstrumentPriorityUser(instrumentId)) {
            status = HttpStatus.BAD_REQUEST;
            bodyResponse.append("\"message\":\""+ LabInventoryService.getResponseMessage() +"\"");
        } else {
            bodyResponse.append("\"deputies removed\":");
            bodyResponse.append("\"message\":\""+ LabInventoryService.getResponseMessage() +"\"");
        }
        bodyResponse.append("}");

        return ResponseEntity.status(status)
                .header("Content-Type", "application/json")
                .body(bodyResponse.toString());
    }


	private InstrumentDto getInstrumentFromJsonData(HttpServletRequest request, String jsonData)  throws IOException {
		return JsonUtils.mapFromJson(jsonData, InstrumentDto.class);
	}

	@RequestMapping("/instrument/{instrId}/resoptions")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> getResoptionIdsForInstrument(HttpServletRequest request,
															   @PathVariable Integer instrId) {
		StringBuffer bodyResponse = new StringBuffer("{");
		String instrumentId = String.valueOf(instrId % ReservationController.getResourceIdOffset());
		List<Integer> resoptionIds = LabInventoryService.getResoptionIdsByInstrumentId(instrumentId);

		bodyResponse.append("\"resoptionIds\":");
		if (resoptionIds != null) {
			try {
				bodyResponse.append(JsonUtils.mapToJson(resoptionIds));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		} else {
			bodyResponse.append("[]");
		}

		bodyResponse.append("}");

		return ResponseEntity.status(HttpStatus.OK)
				.header("Content-Type", "application/json")
				.body(bodyResponse.toString());
	}
	
	@RequestMapping(value = "/instrument/{instrumentId}", method = RequestMethod.GET)
	public ModelAndView displayDetailInstrument(@PathVariable Integer instrumentId) {
		ModelAndView mv = new ModelAndView("/instrumentDetail");
		InstrumentDto instrument = LabInventoryService.getInstrumentById(instrumentId);
		mv.addObject("instrument", instrument);
		return mv;
	}

	
	@RequestMapping(value = "/bookInstrument_{instrumentId}", method = RequestMethod.GET)
	public ModelAndView displayBookInstrument(@PathVariable Integer instrumentId, HttpServletRequest request) {
		HttpSession session = request.getSession();
		String username = session.getAttribute("username").toString();
		FavoriteDto fav = new FavoriteDto();
		fav.setInstrid(instrumentId);
		fav.setUserName(username.toUpperCase());
		fav.setIsActive(1);
		fav.setCreationDate(new Date());
		try {			
			LabInventoryService.addFavorite(fav);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		ModelAndView mv = new ModelAndView("redirect:/instrumentlist");
		return mv;
	}
	
	@RequestMapping(value = "/unBookInstrument_{instrumentId}", method = RequestMethod.GET)
	public ModelAndView displayUnBookInstrument(@PathVariable Integer instrumentId, HttpServletRequest request) {
		FavoriteDto favToRemove = null;
		HttpSession session = request.getSession();
		String username = session.getAttribute("username").toString();
		List<FavoriteDto> savedFavorites = LabInventoryService.getUserFavorites(username.toUpperCase());
		if(savedFavorites != null && savedFavorites.size() >= 1) {
			for(FavoriteDto fav : savedFavorites) {
				if(fav.getInstrid().equals(instrumentId)) {
					favToRemove = fav;
					break;
				}
			}
			if(favToRemove != null) {				
				int favId = favToRemove.getId();
				try {
					LabInventoryService.deleteFavorite(String.valueOf(favId));					
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}			
		ModelAndView mv = new ModelAndView("redirect:/instrumentlist");
		return mv;
	}

}
