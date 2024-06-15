package org.research.kadda.labinventory.core.model;

import java.io.Serializable;
import java.util.Date;

public class InstrResaModel implements Serializable {

	private static final long serialVersionUID = 8983384186855693704L;

	private int instrumentId;
	private int ratioLeft;
	private int resoptid;
	private int step;
	private Date startDate;
	private Date endDate;

	public InstrResaModel() {
		super();
	}

	public InstrResaModel(int instrumentId, int ratioLeft, int resoptid, int step, Date startDate, Date endDate) {
		super();
		this.instrumentId = instrumentId;
		this.ratioLeft = ratioLeft;
		this.resoptid = resoptid;
		this.step = step;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public int getInstrumentId() {
		return instrumentId;
	}

	public void setInstrumentId(int instrumentId) {
		this.instrumentId = instrumentId;
	}

	public int getRatioLeft() {
		return ratioLeft;
	}

	public void setRatioLeft(int ratioLeft) {
		this.ratioLeft = ratioLeft;
	}

	public int getResoptid() {
		return resoptid;
	}

	public void setResoptid(int resoptid) {
		this.resoptid = resoptid;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
