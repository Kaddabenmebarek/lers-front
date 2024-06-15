package org.research.kadda.labinventory.core.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.research.kadda.labinventory.data.InstrumentDto;
import org.research.kadda.labinventory.data.ReservationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import org.research.kadda.osiris.OsirisService;
import org.research.kadda.osiris.data.EmployeeDto;


@Component
public class EmailServiceImpl implements EmailService {

	private final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
	
    @Override
    public void sendJavaMail(String message, String recipe, Set<String> ccs, String subject, boolean testMode)
			throws AddressException, MessagingException, IOException {
    	
    	Properties emailProp = fetchProperties();
    	
		Properties props = new Properties();
		props.setProperty("mail.smtp.host", emailProp.getProperty("mail.host"));
		props.setProperty("mail.smtp.starttls.enable", emailProp.getProperty("mail.smtp.starttls.enable"));

		Session mailConnection = Session.getInstance(props, null);
		mailConnection.setDebug(false);
		Message msg = new MimeMessage(mailConnection);
		msg.setSentDate(new Date());
		String purpose = subject == null ? emailProp.getProperty("mail.subject") : subject;
		/*if(testMode) {			
			recipe = emailProp.getProperty("mail.testto");
			ccs = new HashSet<String>();
			ccs.add(emailProp.getProperty("mail.testcc"));
			ccs.add(emailProp.getProperty("mail.testcci"));
			purpose = purpose + " (apollo)";
		}*/
		Address recip = new InternetAddress(recipe);
		msg.setRecipient(Message.RecipientType.TO, recip);
		List<Address> recipeCcs = new ArrayList<Address>();
		ccs.removeAll(Collections.singleton(null));
		for(String cc : ccs) {
			Address recipcc = new InternetAddress(cc);
			recipeCcs.add(recipcc);
		}
		msg.setRecipients(Message.RecipientType.CC, recipeCcs.toArray(new Address[recipeCcs.size()]));
		msg.setSubject(purpose);
		Address from = new InternetAddress(emailProp.getProperty("mail.from"));
		msg.setFrom(from);

		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(message, "text/html");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		
		/*MimeBodyPart attachPart = new MimeBodyPart();
		attachPart.attachFile("claspath:/lers/resources/images/banner.png");
		multipart.addBodyPart(attachPart);*/

		msg.setContent(multipart);
		Transport.send(msg);

	}
	
	@Override
	public void sendSimpleMessage(String msg, String to, String cc) {
		Properties emailProp = fetchProperties();
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setCc(cc);
		message.setFrom(emailProp.getProperty("mail.from"));
		message.setSubject(emailProp.getProperty("mail.subject"));
		message.setText(msg);
		getJavaMailSender().send(message);
	}
	
	public JavaMailSender getJavaMailSender() {
		
		Properties emailProp = fetchProperties();
	    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	    
	    mailSender.setHost(emailProp.getProperty("mail.host"));
	    mailSender.setPort(Integer.valueOf(emailProp.getProperty("mail.port")));
	    mailSender.setUsername(emailProp.getProperty("mail.username"));
	    mailSender.setPassword(emailProp.getProperty("mail.password"));
	     
	    Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.smtp.auth", emailProp.getProperty("mail.smtp.auth"));
	    props.put("mail.smtp.starttls.enable", emailProp.getProperty("mail.smtp.starttls.enable"));
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.debug", "true");
	     
	    return mailSender;
	}
	
	
    public Properties fetchProperties(){
        Properties properties = new Properties();
        try {
        	File file = ResourceUtils.getFile("classpath:email.properties");
            InputStream in = new FileInputStream(file);
            properties.load(in);
        } catch (IOException e) {
        	logger.error(e.getMessage());
        }
        return properties;
    }

	@Override
	public String buildReservaionMailMessage(ReservationDto reservationDto, InstrumentDto inst, boolean done, List<String> usersWhoBookedOnSameSlot) {
		String mailContent = "";
		StringBuilder message  = new StringBuilder("Following instrument ");
		if(done) {
			message.append("booking has ended");
		}else {
			message.append("was booked");
		}
		message.append(" <br /><b>");
		message.append(inst.getName()).append("</b>").append("<br /><br />");
		message.append("Starting from <br /><b>").append(reservationDto.getFromTime().toString());
		message.append("</b><br /><br />");
		message.append("Ending at <br /><b>").append(reservationDto.getToTime().toString());
		message.append("</b><br /><br />");
		EmployeeDto booker = OsirisService.getEmployeeByUserId(reservationDto.getUsername().toLowerCase());
		message.append("Booked by <br /><b>").append(booker.getFirstName()).append(" ").append(booker.getLastName());
		if(!usersWhoBookedOnSameSlot.isEmpty() && !done) {			
			message.append("</b><br /><br />");
			message.append("This instrument was already booked on the same interval by <b>");
			for(int i=0;i < usersWhoBookedOnSameSlot.size();i++) {
				EmployeeDto emp = OsirisService.getEmployeeByUserId(usersWhoBookedOnSameSlot.get(i).toLowerCase());
				message.append(emp.getFirstName()).append(" ").append(emp.getLastName());
				if(i<usersWhoBookedOnSameSlot.size()-1)
					message.append(", ");	
			}
			message.append("</b>.");
			
		}
		message.append("<br />");
		try {
			mailContent = generateContent("email.html", message.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mailContent;
	}
	
	@Override
	public String generateContent(String inputfileName, String mailContent) throws FileNotFoundException, IOException {
		File sourceFile = ResourceUtils.getFile("classpath:" + inputfileName);
		StringBuilder sb = new StringBuilder();
		FileReader fr = new FileReader(sourceFile);
		BufferedReader br = new BufferedReader(fr);
		String s = br.readLine();
		try {
			while (s != null) {
				System.out.println(s);
				if(s.equals("{content}")) {
					s = mailContent;
				}
				sb.append(s);
				s = br.readLine();
			}
		} finally {
			br.close();
		}
		return sb.toString();
	}

}
