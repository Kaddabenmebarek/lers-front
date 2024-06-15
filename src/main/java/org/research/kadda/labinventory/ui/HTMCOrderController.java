package org.research.kadda.labinventory.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.research.kadda.labinventory.SynthesisService;
import org.research.kadda.labinventory.core.service.EmailService;
import org.research.kadda.labinventory.core.service.StructureService;
import org.research.kadda.labinventory.core.utils.MiscUtils;
import org.research.kadda.labinventory.data.JsonUtils;
import org.research.kadda.labinventory.data.ReservationHistoryDto;
import org.research.kadda.labinventory.data.SynthesisLibraryOrderDto;
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
import com.fasterxml.jackson.databind.node.FloatNode;
import org.research.kadda.osiris.OsirisService;
import org.research.kadda.osiris.data.EmployeeDto;


@Controller
@Scope("session")
public class HTMCOrderController {
    private static Logger logger = LogManager.getLogger(HTMCOrderController.class);
    private String message = "";
    private static String HTMC_DPT = "HTMC";
    
    @Autowired
    private StructureService structureService;
    @Autowired
	private EmailService emailService;
    
    @RequestMapping("/htmcorder")
    public ModelAndView displaySynthesisOrder(HttpServletRequest request, Model model) {
        HttpSession session = request.isRequestedSessionIdValid() ? request.getSession() : null;
        if (session == null || session.getAttribute("username") == null) {
            return new ModelAndView("redirect:/home");
        }
        String username = (String) session.getAttribute("username");
        String userGroups = getUserGroups(username);
        ModelAndView mv = new ModelAndView("/htmcorder");
        mv.addObject("userGroups", userGroups);

        StringBuffer resources = new StringBuffer();
        
        Map<Integer, SynthesisLibraryOrderDto> ordersMap = new HashMap<>();
        List<SynthesisLibraryOrderDto> allOrders = SynthesisService.getAllSynthesisOrders();
        Date today = new Date();
        LocalDateTime ldt = LocalDateTime.ofInstant(today.toInstant(), ZoneId.systemDefault()).minusDays(30);;
        Date monthAgo = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        if(allOrders != null && allOrders.size() >1) {
        	Iterator<SynthesisLibraryOrderDto> it = allOrders.iterator();
            while(it.hasNext()) {
            	SynthesisLibraryOrderDto order = it.next();
            	if(!HTMC_DPT.equalsIgnoreCase(order.getDepartmentName())) {
            		it.remove();
            		continue;
            	}
            	if(MiscUtils.checkDiffDays(monthAgo, order)) {
            		it.remove();
            	}
            }
        	Collections.sort(allOrders, new Comparator<SynthesisLibraryOrderDto>() {
        		@Override
        		public int compare(SynthesisLibraryOrderDto o1, SynthesisLibraryOrderDto o2) {
        			return Integer.compare(o1.getId(), o2.getId());
        		}
        	});
        }
        String[] alphaB = MiscUtils.charArray(500);
        
        //for(SynthesisLibraryOrderDto order : allOrders) {
        for(int i=0; i<allOrders.size(); i++) {
        	resources.append("{");
            resources.append("id:");
            resources.append(allOrders.get(i).getId() + ",");
            resources.append("connectedUser:");
            resources.append("'" + username + "',");
            resources.append("hackOrder:");
            resources.append("'" + alphaB[i] + "',");            
            resources.append("title:");
            resources.append("'" + allOrders.get(i).getTitle().trim() + " (for " + allOrders.get(i).getRequester() + ")" + "'},");
            
            ordersMap.put(allOrders.get(i).getId(), allOrders.get(i));
        }
        
        request.setAttribute("htmcOrderResources", resources.toString());

        return mv;
    }

    @RequestMapping("/htmcorder/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getAllHTMCOrder(HttpServletRequest request) {

        HttpSession session = request.isRequestedSessionIdValid() ? request.getSession() : null;
        if (session == null || session.getAttribute("username") == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json")
                    .body("{}");
        }

        logger.info("Getting all htmc orders");
        List<SynthesisLibraryOrderDto> allOrders = SynthesisService.getAllSynthesisOrders();
        Iterator<SynthesisLibraryOrderDto> it = allOrders.iterator();
        Date today = new Date();
        LocalDateTime ldt = LocalDateTime.ofInstant(today.toInstant(), ZoneId.systemDefault()).minusDays(30);;
        Date monthAgo = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        while(it.hasNext()) {
        	SynthesisLibraryOrderDto order = it.next();
        	if(!HTMC_DPT.equalsIgnoreCase(order.getDepartmentName())) {
        		it.remove();
        		continue;
        	}
        	if(MiscUtils.checkDiffDays(monthAgo, order)) {
        		it.remove();
        	}
        	boolean done = order.getDone() == 1;
        	order.setEventColor(!done ? "#ff3300": "#9BA4A9");
        }
        StringBuffer bodyResponse = new StringBuffer("{");
        try {
            bodyResponse.append("\"htmcorders\":");
            bodyResponse.append(JsonUtils.mapToJson(allOrders));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        bodyResponse.append("}");

        logger.info("Got htmc orders : " + bodyResponse.toString());

        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(bodyResponse.toString());
    }

    @RequestMapping("/htmcorder/all/inrange")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getHTMCOrderForRange(HttpServletRequest request) {

        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String username = request.getSession().getAttribute("username").toString().toUpperCase();

        logger.info("User : " + username + " From : " + startDateStr + " to " + endDateStr);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.RFC_1123_DATE_TIME;
        
		List<SynthesisLibraryOrderDto> htmcorders = SynthesisService.getAllSynthesisOrdersInRange(
				Timestamp.valueOf(LocalDateTime.parse(startDateStr, dateTimeFormatter)),
				Timestamp.valueOf(LocalDateTime.parse(endDateStr, dateTimeFormatter)));
		Iterator<SynthesisLibraryOrderDto> it = htmcorders.iterator();
		Date today = new Date();
        LocalDateTime ldt = LocalDateTime.ofInstant(today.toInstant(), ZoneId.systemDefault()).minusDays(30);;
        Date monthAgo = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        while(it.hasNext()) {
        	SynthesisLibraryOrderDto order = it.next();
        	if(!HTMC_DPT.equalsIgnoreCase(order.getDepartmentName())) {
        		it.remove();
        		continue;
        	}
        	if(MiscUtils.checkDiffDays(monthAgo, order)) {
        		it.remove();
        	}
        	boolean done = order.getDone() == 1;
        	order.setEventColor(!done ? "#ff3300": "#9BA4A9");
        	order.setConnectedUser(username);
        }
		
        StringBuffer bodyResponse = new StringBuffer("{");
        try {
            bodyResponse.append("\"htmcorders\":");
            bodyResponse.append(JsonUtils.mapToJson(htmcorders));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        bodyResponse.append("}");

        logger.info("Got htmc orders : " + bodyResponse.toString());

        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(bodyResponse.toString());		
    }
    
	public String getUserGroups(String connectedUser) {
		if(connectedUser != null && !"".equals(connectedUser)) {
			StringBuilder sb = new StringBuilder();
			List<String> userGroups = OsirisService.getGroupsIdsByUserId(connectedUser.toLowerCase());
			for(int i=0; i<userGroups.size(); i++) {
				sb.append(userGroups.get(i));
				if(i<userGroups.size()-1) {
					sb.append(";");
				}
			}
			return sb.toString();
		}
		return null;
	}
	
	
    @RequestMapping(method = RequestMethod.POST, path="/htmcorder/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> addHTMCOrder(HttpServletRequest request, @RequestBody String jsonData) {
        logger.info("Adding Order");
        logger.info("JsonData Received : " + jsonData);
        SynthesisLibraryOrderDto orderDto = null;
        HttpStatus status = HttpStatus.CREATED;
        jsonData = JsonUtils.formatUTF8(jsonData);
        try {
        	orderDto = getHTMCOrderFromJsonData(request, jsonData);
        	orderDto.setDepartmentName(HTMC_DPT);
        } catch (JsonProcessingException e) {
            logger.error("The Json data is not well formatted");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            logger.error("Resource Id must be an integer.");
            nfe.printStackTrace();
        }

        StringBuffer bodyResponse = new StringBuffer("");
        if (validateOrder(orderDto, jsonData)) {
            if (SynthesisService.addSynthesisOrder(orderDto)) {
                bodyResponse.append(getOrderJson(orderDto));
            } else {
                status = HttpStatus.ACCEPTED;
                bodyResponse = new StringBuffer("{\"message\":\"Register order failed.\",\"statusCode\":\"" + status.toString() + "\"}");
            }
        } else {
            status = HttpStatus.FORBIDDEN;
            bodyResponse = new StringBuffer("{\"message\":\"" + message + "\",\"statusCode\":\"" + status.toString() + "\"}");
        }

        return ResponseEntity.status(status)
                .header("Content-Type", "application/json")
                .body(bodyResponse.toString());
    }
    
    @RequestMapping(method = RequestMethod.PUT, path="/htmcorder/update")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> updateHTMCOrder(HttpServletRequest request, @RequestBody String jsonData) {
        logger.info("Update Order...");
        logger.info("JsonData Received : " + jsonData);
        SynthesisLibraryOrderDto synthesisOrderDto = null;
        HttpStatus status = HttpStatus.CREATED;
        jsonData = JsonUtils.formatUTF8(jsonData);
        try {
        	synthesisOrderDto = getHTMCOrderFromJsonData(request, jsonData);
        	synthesisOrderDto.setDepartmentName(HTMC_DPT);
        } catch (JsonProcessingException e) {
            logger.error("The Json data is not well formatted");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            logger.error("Resource Id must be an integer.");
            nfe.printStackTrace();
        }

        StringBuffer bodyResponse = new StringBuffer("");
        if (SynthesisService.updateSynthesisOrder(synthesisOrderDto)) {
        	bodyResponse.append(getOrderJson(synthesisOrderDto));
        } else {
        	status = HttpStatus.ACCEPTED;
        	bodyResponse = new StringBuffer("{\"message\":\"Update failed.\",\"statusCode\":\"" + status.toString() + "\"}");
        }

        return ResponseEntity.status(status)
                .header("Content-Type", "application/json")
                .body(bodyResponse.toString());
    }
    
    @RequestMapping(method = RequestMethod.DELETE, path="/htmcorder/delete/{orderid}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteHTMCOrder(@PathVariable(value = "orderid") String orderid) {
        logger.info("Deleting Order");
        StringBuffer bodyResponse = new StringBuffer("{");
        HttpStatus status = HttpStatus.OK;

        SynthesisLibraryOrderDto synthesisOrderDto = SynthesisService.getSynthesisOrderById(Integer.valueOf(orderid));
        if (!SynthesisService.deleteSynthesisOrder(orderid)) {
            status = HttpStatus.BAD_REQUEST;
            bodyResponse.append("\"message\":\""+ SynthesisService.getResponseMessage() +"\"");
        } else {
            if (synthesisOrderDto != null) {
                try {
                    bodyResponse.append("\"order\":");
                    bodyResponse.append(JsonUtils.mapToJson(synthesisOrderDto));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
        bodyResponse.append("}");
        return ResponseEntity.status(status)
                .header("Content-Type", "application/json")
                .body(bodyResponse.toString());
    }

	@RequestMapping("/htmchistory")
	public String htmchistory(ReservationHistoryDto reservationHistoryDto, Model model) throws ParseException {
		List<SynthesisLibraryOrderDto> htmcorders = new ArrayList<SynthesisLibraryOrderDto>();
		if(reservationHistoryDto != null && reservationHistoryDto.getStartDate() != null && reservationHistoryDto.getEndDate() != null) {
			String start = reservationHistoryDto.getStartDate();
			String end = reservationHistoryDto.getEndDate();
			model.addAttribute("htmcRequestStartDate", start);
			model.addAttribute("htmcRequestEndDate", end);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date from = simpleDateFormat.parse(start + " 00:00:00");
			Date to = simpleDateFormat.parse(end + " 00:00:00");
			List<SynthesisLibraryOrderDto> allOrdersInRange = SynthesisService.getAllSynthesisOrdersInRange(from, to);
			Iterator<SynthesisLibraryOrderDto> it = allOrdersInRange.iterator();
			while(it.hasNext()) {
				SynthesisLibraryOrderDto ord = it.next();
				if(!ord.getDepartmentName().equalsIgnoreCase(HTMC_DPT) || ord.getId() == 2) {
					it.remove();
				}
			}
			htmcorders.addAll(allOrdersInRange);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			for(SynthesisLibraryOrderDto order : htmcorders) {
				if(order.getCompound() != null) {
					String base64Img = order.getCompound() != null ? structureService.drawStructure(order.getCompound(), false) : null;
					order.setBase64Img(base64Img);
					order.setIsStructureAvailable("Y");
				}else {
					order.setIsStructureAvailable("N");
				}
				order.setFromTimeToDisplay(dateFormat.format(order.getFromTime()));
				order.setToTimeToDisplay(dateFormat.format(order.getToTime()));
				order.setRequestTimeToDisplay(dateFormat.format(order.getRequestTime()));
			}
		}
		model.addAttribute("htmcorders",htmcorders);
		model.addAttribute("department","htmc");
		
		Map<Integer, String> instrumentMap =  ReservationController.getInstrumentList();
		String instrumentMultiSelectHtml = ReservationController.buildHtmlMultiSelect(instrumentMap, new ArrayList<Integer>());
		model.addAttribute("instrumentMultiSelectHtml",instrumentMultiSelectHtml);
		
		return "logbook";

	}
	
	@RequestMapping(method = RequestMethod.POST, path="/htmcorder/email")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> sendEmailToRequester(HttpServletRequest request, @RequestBody String jsonData) {
        logger.info("JsonData Received : " + jsonData);
        SynthesisLibraryOrderDto orderDto = null;
        HttpStatus status = HttpStatus.CREATED;
        jsonData = JsonUtils.formatUTF8(jsonData);
        try {
        	orderDto = getHTMCOrderFromJsonData(request, jsonData);
        	orderDto.setDepartmentName(HTMC_DPT);
        } catch (JsonProcessingException e) {
            logger.error("The Json data is not well formatted");
            e.printStackTrace();
        } catch (NumberFormatException nfe) {
            logger.error("Resource Id must be an integer.");
            nfe.printStackTrace();
        }
        
        //preparing email to be sent
        preparingEmailNotification(orderDto, request);

        orderDto.setDone(MiscUtils.booleanToInt(true));
        orderDto.setDoneTime(new Date());
        StringBuffer bodyResponse = new StringBuffer("");
        if (SynthesisService.updateSynthesisOrder(orderDto)) {
            bodyResponse.append(getOrderJson(orderDto));
        } else {
            status = HttpStatus.ACCEPTED;
            bodyResponse = new StringBuffer("{\"message\":\"Order update on email processing failed.\",\"statusCode\":\"" + status.toString() + "\"}");
        }

        return ResponseEntity.status(status)
                .header("Content-Type", "application/json")
                .body(bodyResponse.toString());
    }
    
    private SynthesisLibraryOrderDto getHTMCOrderFromJsonData(HttpServletRequest request, String jsonData) throws JsonProcessingException, NumberFormatException {
    	SynthesisLibraryOrderDto synthesisOrderDto = new SynthesisLibraryOrderDto();

    	JsonNode id = JsonUtils.getJsonNode(jsonData, "resourceId");
        JsonNode fromDateNode = JsonUtils.getJsonNode(jsonData, "fromDate");
        JsonNode toDateNode = JsonUtils.getJsonNode(jsonData, "toDate");
        JsonNode titleNode = JsonUtils.getJsonNode(jsonData, "title");
        JsonNode requesterNode = JsonUtils.getJsonNode(jsonData, "requester");
        JsonNode requesDateNode = JsonUtils.getJsonNode(jsonData, "reqDate");
        JsonNode projectNode = JsonUtils.getJsonNode(jsonData, "project");
        JsonNode compoundNode = JsonUtils.getJsonNode(jsonData, "compound");
        JsonNode quantityNode = JsonUtils.getJsonNode(jsonData, "quantity");
        JsonNode unitNode = JsonUtils.getJsonNode(jsonData, "unit");
        JsonNode linkNode = JsonUtils.getJsonNode(jsonData, "link");
        JsonNode libraryOutcomeNode = JsonUtils.getJsonNode(jsonData, "libraryOutcome");
        if (fromDateNode != null && toDateNode != null) {
            String fromDate = fromDateNode.textValue();
            String toDate = toDateNode.textValue();
            String user = request.getSession().getAttribute("username").toString();
            if(id != null) {            	
            	Integer orderId = id.asInt();
            	synthesisOrderDto.setId(orderId);
            }
            String username = user != null ? user.toUpperCase() : "";
            synthesisOrderDto.setUsername(username);
            String title = titleNode != null ? titleNode.textValue() : "";
            synthesisOrderDto.setTitle(title);
            synthesisOrderDto.setFromTime(MiscUtils.parseUTCDateWithOffset(fromDate));
            synthesisOrderDto.setToTime(MiscUtils.parseUTCDateWithOffset(toDate));
            String reqDate = requesDateNode.textValue();
            if(reqDate != null) synthesisOrderDto.setRequestTime(MiscUtils.parseUTCDateWithOffset(reqDate));
            String project = projectNode != null ? projectNode.textValue() : "";
            synthesisOrderDto.setProject(project);
            String compound = compoundNode != null ? compoundNode.textValue() : "";
            synthesisOrderDto.setCompound(compound);
            String quantity;
            if(quantityNode instanceof FloatNode) {            	
            	quantity =  quantityNode != null ? String.valueOf(quantityNode.intValue()) : null;
            }else {
            	quantity =  quantityNode != null ? quantityNode.textValue() : null;
            }
            if(quantity != null)synthesisOrderDto.setQuantity(Float.valueOf(quantity));
            String unit = unitNode != null ? unitNode.textValue() : "";
            synthesisOrderDto.setUnit(unit);
            String libraryOutcome = libraryOutcomeNode != null ? libraryOutcomeNode.textValue() : "";
            synthesisOrderDto.setLibraryoutcome(libraryOutcome);
            String link = linkNode != null ? linkNode.textValue() : "";
            synthesisOrderDto.setLink(link);
            synthesisOrderDto.setRequester(requesterNode.textValue().toUpperCase());
            synthesisOrderDto.setCreationTime(new Date());
        }

        return synthesisOrderDto;
    }
    
	private String getOrderJson(SynthesisLibraryOrderDto synthesisOrderDto) {
        StringBuffer buf = new StringBuffer("{");
        try {
            buf.append("\"synthesisorders\":");
            buf.append(JsonUtils.mapToJson(synthesisOrderDto));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        buf.append("}");
        return buf.toString();
    }
	
	private boolean validateOrder(SynthesisLibraryOrderDto synthesisOrderDto, String jsonData) {
        boolean validated = true;
        Date fromDate = synthesisOrderDto.getFromTime();
        Date toDate = synthesisOrderDto.getToTime();
        //check if order with same title and overlaped dates already recorded
        List<SynthesisLibraryOrderDto> orders = SynthesisService.getAllSynthesisOrdersInRange(fromDate, toDate);
        for(SynthesisLibraryOrderDto order : orders) {
        	if(order.getTitle().trim().equalsIgnoreCase(synthesisOrderDto.getTitle().trim())) validated = false;
        }

        return validated;
    }
	
	private void preparingEmailNotification(SynthesisLibraryOrderDto synthesisOrderDto, HttpServletRequest request) {
		try {
			String message = buildSynthesisOrderMailMessage(synthesisOrderDto);
			if (message != null && !"".equals(message)) {
				String to = "";
				EmployeeDto requester = null;
				Set<String> ccs = new HashSet<String>();
				EmployeeDto cc = OsirisService.getEmployeeByUserId(synthesisOrderDto.getUsername().toLowerCase());
				ccs.add(cc.getEmail());
				requester = OsirisService.getEmployeeByUserId(synthesisOrderDto.getRequester().toLowerCase());
				to = requester.getEmail();
				String subject = "Your HTMC Order is ready: " + synthesisOrderDto.getTitle();
				boolean testMode = request.getRequestURL() != null && !request.getRequestURL().toString().contains("ares");
				emailService.sendJavaMail(message, to, ccs, subject, testMode);
			}
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String buildSynthesisOrderMailMessage(SynthesisLibraryOrderDto synthesisOrderDto) {
		String mailContent = "";
		StringBuilder message  = new StringBuilder("Following Order ");
		message.append(" <br />");
		message.append(synthesisOrderDto.getTitle()).append("<br /><br />");
		message.append("Made by <br />").append(synthesisOrderDto.getUsername());
		message.append("<br /><br />");
		message.append("Starting from <br />").append(synthesisOrderDto.getFromTime().toString());
		message.append("<br />");
		message.append("Ended the <br />").append(synthesisOrderDto.getToTime().toString());
		message.append("<br /><br />");
		message.append("<b>Is ready for you and withdrawable.</b><br />");
		message.append("<br />");
		String projectInfo = synthesisOrderDto.getProject() != null && !"".equals(synthesisOrderDto.getProject()) ? "Project: " +synthesisOrderDto.getProject() +"<br />" : ""; 
		String linkInfo = synthesisOrderDto.getLink() != null && !"".equals(synthesisOrderDto.getLink()) ? "Link: <a href=\"" +synthesisOrderDto.getLink() +"\">Report Link</a><br />" : "";
		String libraryOuctomeInfo = synthesisOrderDto.getLibraryoutcome() != null && !"".equals(synthesisOrderDto.getLibraryoutcome()) ? "Link: " +synthesisOrderDto.getLibraryoutcome() +"<br />" : "";
		String compoundInfo = synthesisOrderDto.getCompound() != null && !"".equals(synthesisOrderDto.getCompound()) ? "Compound: " +synthesisOrderDto.getCompound() +"<br />" : "";
		String quantityInfo = synthesisOrderDto.getQuantity() != null ? "Quantity: " +synthesisOrderDto.getQuantity() + " " + synthesisOrderDto.getUnit() + "<br />" : "";
		if(!"".equals(projectInfo) || !"".equals(linkInfo)) {
			message.append("Addition informations:");
			message.append("<br />");
			message.append(projectInfo).append(linkInfo).append(libraryOuctomeInfo).append(compoundInfo).append(quantityInfo);
		}
		if(compoundInfo!=null && !"".equals(compoundInfo)) {
			message.append("Structure: <br />");
			String base64Structure = structureService.drawStructure(synthesisOrderDto.getCompound(), true);
			String structureImage = "<img height=\"250\" width=\"250\" src=\"data:image/png;base64, " + base64Structure + "\"/>";
			message.append(structureImage).append("<br />");
		}
		
		try {
			mailContent = emailService.generateContent("email.html", message.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mailContent;
	}	
	
}
