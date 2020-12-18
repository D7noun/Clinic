package org.D7noun.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.D7noun.abstraction.AbstractEntity;

@NamedQueries({
		@NamedQuery(name = Treatment.getTreatmentsByFileId, query = "SELECT a from Treatment as a where a.file.fileNumber = ?1 order by a.date asc"),
		@NamedQuery(name = Treatment.getAllTreatments, query = "SELECT t from Treatment t order by t.date asc"),
		@NamedQuery(name = Treatment.getAllTreatmentsBetweenDates, query = "SELECT t FROM Treatment t where t.date >= ?1 AND t.date <= ?2 order by t.date asc") })
@Entity
public class Treatment extends AbstractEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String getAllTreatments = "getAllTreatments";
	public static final String getAllTreatmentsBetweenDates = "Treatment.getAllTreatmentsBetweenDates";
	public static final String getTreatmentsByFileId = "getTreatmentsByFileId";

	@Column
	@Temporal(TemporalType.DATE)
	private Date date;

	@Column
	private String treatment;

	@Column
	private String details;

	@Column
	private double total;

	@Column
	private double received;

	@Column
	private double balance;

	@Column
	private String drName;

	@ManyToOne(fetch = FetchType.LAZY)
	private File file;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTreatment() {
		return treatment;
	}

	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}

	public String getDrName() {
		return drName;
	}

	public void setDrName(String drName) {
		this.drName = drName;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public double getReceived() {
		return received;
	}

	public void setReceived(double received) {
		this.received = received;
	}

	public double getBalance() {
		return total - received;
	}

	public void setBalance(double balance) {
		this.balance = total - received;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public static String getGettreatmentsbyfileid() {
		return getTreatmentsByFileId;
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}

	/**
	 * @param details the details to set
	 */
	public void setDetails(String details) {
		this.details = details;
	}

}