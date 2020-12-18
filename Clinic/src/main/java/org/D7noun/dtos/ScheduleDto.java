package org.D7noun.dtos;

import org.D7noun.model.File;

public class ScheduleDto {

	private Long appointmentId;
	private File file;
	private String drName;

	public ScheduleDto() {
		super();
	}

	public ScheduleDto(Long appointmentId, File file, String drName) {
		super();
		this.appointmentId = appointmentId;
		this.file = file;
		this.drName = drName;
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(File file) {
		this.file = file;
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