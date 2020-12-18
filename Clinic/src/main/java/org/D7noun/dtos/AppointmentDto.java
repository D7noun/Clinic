package org.D7noun.dtos;

import java.util.Date;

public class AppointmentDto {

	private Long appointmentId;
	private Date startDate;
	private Date endDate;
	private Long fileNumber;
	private String name;
	private String phoneNumber;
	private String drName;

	public AppointmentDto() {
		super();
	}

	public AppointmentDto(Long appointmentId, Date startDate, Date endDate, Long fileNumber, String name,
			String phoneNumber, String drName) {
		super();
		this.appointmentId = appointmentId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.fileNumber = fileNumber;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.drName = drName;
	}

	public String display() {
		String s = "";
		if (getName() != null) {
			s += getName() + " | ";
		}
		if (getPhoneNumber() != null) {
			s += getPhoneNumber() + " | ";
		}
		if (getDrName() != null) {
			s += getDrName();
		}
		return s;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the fileNumber
	 */
	public Long getFileNumber() {
		return fileNumber;
	}

	/**
	 * @param fileNumber
	 *            the fileNumber to set
	 */
	public void setFileNumber(Long fileNumber) {
		this.fileNumber = fileNumber;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber
	 *            the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the drName
	 */
	public String getDrName() {
		return drName;
	}

	/**
	 * @param drName
	 *            the drName to set
	 */
	public void setDrName(String drName) {
		this.drName = drName;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the appointmentId
	 */
	public Long getAppointmentId() {
		return appointmentId;
	}

	/**
	 * @param appointmentId
	 *            the appointmentId to set
	 */
	public void setAppointmentId(Long appointmentId) {
		this.appointmentId = appointmentId;
	}

}