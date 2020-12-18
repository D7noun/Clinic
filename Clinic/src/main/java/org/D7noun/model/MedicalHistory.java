package org.D7noun.model;

import java.io.Serializable;

import javax.persistence.Entity;

import org.D7noun.abstraction.AbstractEntity;

@Entity
public class MedicalHistory extends AbstractEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String disease;
	private boolean exist;
	private String medication;

	public MedicalHistory() {
		super();
	}

	public MedicalHistory(String disease) {
		super();
		this.disease = disease;
		exist = false;
		medication = "";
	}

	/**
	 * @return the disease
	 */
	public String getDisease() {
		return disease;
	}

	/**
	 * @param disease the disease to set
	 */
	public void setDisease(String disease) {
		this.disease = disease;
	}

	/**
	 * @return the exist
	 */
	public boolean isExist() {
		return exist;
	}

	/**
	 * @param exist the exist to set
	 */
	public void setExist(boolean exist) {
		this.exist = exist;
	}

	/**
	 * @return the medication
	 */
	public String getMedication() {
		return medication;
	}

	/**
	 * @param medication the medication to set
	 */
	public void setMedication(String medication) {
		this.medication = medication;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
