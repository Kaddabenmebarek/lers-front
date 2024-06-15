package org.research.kadda.labinventory.core.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.research.kadda.labinventory.data.InstrumentDto;
import org.research.kadda.labinventory.data.ReservationDto;

public interface EmailService {

	void sendSimpleMessage(String message, String to, String cc);
	
	void sendJavaMail(String message, String recipe, Set<String> ccs, String subject, boolean testMode) throws AddressException, MessagingException, IOException;

	String buildReservaionMailMessage(ReservationDto reservationDto, InstrumentDto inst, boolean done, List<String> usersWhoBookedOnSameSlot);

	String generateContent(String inputfileName, String mailContent) throws FileNotFoundException, IOException;

}
