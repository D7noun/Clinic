package org.D7noun.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.D7noun.abstraction.AbstractEntity;

@NamedQueries(@NamedQuery(name = Appointment.findAppointmentsBetweenTwoDates, query = "SELECT new org.D7noun.dtos.AppointmentDto(a.id,a.startDate, a.endDate,a.file.fileNumber, a.file.name, a.file.phoneNumber, a.drName) FROM Appointment a WHERE a.startDate >= :startDate AND a.endDate <= :endDate"))
@Entity
public class Appointment extends AbstractEntity implements Serializable {

	public static final String findAppointmentsBetweenTwoDates = "Appointment.findAppointmentsBetweenTwoDates";

	private static final long serialVersionUID = 1L;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	@ManyToOne(cascade = CascadeType.MERGE)
	private File file;

	@Column(name = "dr_name")
	private String drName;

	public String getDrName() {
		return drName;
	}

	public void setDrName(String drName) {
		this.drName = drName;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Appointment)) {
			return false;
		}
		Appointment other = (Appointment) obj;
		if (id != null) {
			if (!id.equals(other.id)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public File getFile() {
		return this.file;
	}

	public void setFile(final File file) {
		this.file = file;
	}

	public String display() {
		String s = "";
		if (getFile() != null) {
			s += getFile().displayName() + " | ";
		}
		if (getDrName() != null) {
			s += getDrName();
		}
		return s;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}