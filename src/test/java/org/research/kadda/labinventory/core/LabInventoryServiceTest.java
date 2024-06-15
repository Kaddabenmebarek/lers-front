package org.research.kadda.labinventory.core;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.research.kadda.labinventory.LabInventoryService;
import org.research.kadda.labinventory.data.FavoriteDto;
import org.research.kadda.labinventory.data.InstrumentDto;
import org.research.kadda.labinventory.data.InstrumentPriorityUsersDto;

public class LabInventoryServiceTest {

	@Test
	public void testList() {
		List<InstrumentDto> instrumentDtoList  = LabInventoryService.getAllInstruments();
		assertNotNull(instrumentDtoList);
		assertTrue(instrumentDtoList.size() > 0);
	}
	
	@Test
	public void testGetSingleInstrument() {
		InstrumentDto inst = LabInventoryService.getInstrumentById(1541);
		assertNotNull(inst);
		assertTrue(inst.getId() == 1541);
		assertEquals("14:00", inst.getStartTimepoint());
	}
	
	@Test
	public void testUpdateInstrument() {
		String desc = "new_desc";
		InstrumentDto inst = LabInventoryService.getInstrumentById(860);		
		inst.setDescription(desc);
		LabInventoryService.updateInstrument(inst);
		InstrumentDto instUpdated = LabInventoryService.getInstrumentById(860);		
		assertTrue(desc.equals(instUpdated.getDescription()));
	}
	
	@Test
	public void testAddInstrument() {
		InstrumentDto instrument = new InstrumentDto();
        instrument.setGroupname("HTMC");
        instrument.setIsPublic(1);
        instrument.setReservable(1);
        instrument.setUsername("BENMEKA1");
        instrument.setName("Inst Test LC-MS_HXX.01.KXX");
        instrument.setStatus("Working");
        instrument.setDescription("Test instrument to be added");
        instrument.setLocation("HXX.01.KXX");
		LabInventoryService.addInstrument(instrument);
	}
	
	@Test
	public void testDeleteInstrument() {
		InstrumentDto inst = LabInventoryService.getInstrumentById(920);
		assertNotNull(inst);
		LabInventoryService.deleteInstrument("920");
		InstrumentDto target = LabInventoryService.getInstrumentById(920);
		assertNull(target);
	}	
	
	@Test
	public void testAddGetFavorite() {
		FavoriteDto fav = new FavoriteDto();
		fav.setInstrid(860);
		fav.setUserName("BENMEKA1");
		fav.setIsActive(1);
		fav.setCreationDate(new Date());
		LabInventoryService.addFavorite(fav);
		List<FavoriteDto> savedFavorites = LabInventoryService.getUserFavorites("BENMEKA1");
		assertNotNull(savedFavorites);
		assertTrue(savedFavorites.size()==1);
		assertTrue(savedFavorites.get(0).getInstrid() == 860);
	}
	
	@Test
	public void testRemoveFavorite() {
		FavoriteDto favToRemove = null;
		List<FavoriteDto> savedFavorites = LabInventoryService.getUserFavorites("BENMEKA1");
		if(savedFavorites != null && savedFavorites.size() >= 1) {
			favToRemove = savedFavorites.get(0);
			int favId = favToRemove.getId();
			LabInventoryService.deleteFavorite(String.valueOf(favId));
			FavoriteDto favRemoved = LabInventoryService.getFavoriteById(favId);
			assertNull(favRemoved);
		}
	}
	
	@Test
	public void testGetAllInstrumentPriorityUsers() {
		List<InstrumentPriorityUsersDto> instPriorityUsers = LabInventoryService.getAllInstrumentPriorityUsers();
		assertNotNull(instPriorityUsers);
		assertTrue(instPriorityUsers.size() > 0);
	}

	@Test
	public void testGetPriorityUsersByInstrumentId() {
		List<String> priorityUsers = LabInventoryService.getPriorityUsersByInstrumentId(1541);
		assertNotNull(priorityUsers);
		assertTrue(priorityUsers.size() > 0);
	}
	
	@Test
	public void testJodaTime() {
		LocalDateTime currentDateAndTime = LocalDateTime.now();
		System.out.println(currentDateAndTime.getHour());
		System.out.println(LocalDateTime.now().withHour(21).withMinute(0).withSecond(0));
		System.out.println(LocalDateTime.now().plusDays(1));
	}
	
	@Test
	public void testGetAllowInst() {
		List<Integer> alloawedInstId = LabInventoryService.getAllowedInstrumentForReservationUsages();
		assertNotNull(alloawedInstId);
		assertTrue(!alloawedInstId.isEmpty());
	}
}
