package org.D7noun.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.D7noun.abstraction.AbstractEntityWithoutId;

@NamedQueries({
		@NamedQuery(name = File.findAllFilesByTreatmentDrName, query = "SELECT DISTINCT f FROM File f inner join f.treatments t WHERE t.drName = :p1"),
		@NamedQuery(name = File.getAllPhoneNumbers, query = "SELECT DISTINCT f.phoneNumber FROM File f"),
		@NamedQuery(name = File.getMaxAppointmentDateByFileId, query = "SELECT MAX(a.startDate) FROM Appointment a WHERE a.file = :file") })
@Entity
@Table(name = "file")
public class File extends AbstractEntityWithoutId implements Serializable {

	public static final String findAllFilesByTreatmentDrName = "findAllFilesByTreatmentDrName";
	public static final String getAllPhoneNumbers = "getAllPhoneNumbers";
	public static final String getMaxAppointmentDateByFileId = "File.getMaxAppointmentDateByFileId";

	public enum Sex {
		MALE, FEMALE;
	}

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", updatable = false, nullable = false)
	private Long fileNumber;

	@Column(nullable = false)
	private String name;

	@Column
	@Enumerated(EnumType.STRING)
	private Sex sex;

	@Column
	@Temporal(TemporalType.DATE)
	private Date dateOfBirth;

	@Column
	private String address;

	@Column
	private String phoneNumber;

	@Column
	private String occupation;

	private String nationality;
	private String inssuranceCompany;
	/////////////////////////////////////
	/////////////////////////////////////
	/////////////////////////////////////
	private String bloodPressure;
	private String bloodType;
	private String weight;
	private String height;
	private String temperature;
	private String breathing;
	/////////////////////////////////////
	/////////////////////////////////////
	/////////////////////////////////////

	@Column
	private boolean hasRemainingDept;

	@Column(length = 1000)
	private String mainProblem;
	@Column(length = 1000)
	private String firstTest;
	@Column(length = 1000)
	private String firstDiagnose;
	@Column(length = 1000)
	private String treatmentPlan;

	@Column(length = 1000)
	private String detailsOfPayment;

	@Column(length = 1000)
	private String notes;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "file_id")
	private List<MedicalHistory> medicalHistories = new ArrayList<>();

	@OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Treatment> treatments = new ArrayList<>();

	@OneToMany(mappedBy = "file", cascade = CascadeType.MERGE, orphanRemoval = true)
	private Set<Attachment> attachments = new HashSet<>();

	public int getAge() {
		if (dateOfBirth != null) {
			Calendar a = getCalendar(dateOfBirth);
			Calendar b = getCalendar(new Date());
			int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
			if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) || (a.get(Calendar.MONTH) == b.get(Calendar.MONTH)
					&& a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
				diff--;
			}
			return diff;
		} else {
			return 0;
		}
	}

	public File addNewTreatment(Treatment treatment) {
		if (treatment != null) {
			this.treatments.add(treatment);
			treatment.setFile(this);
		}
		return this;
	}

	public File removeTreatment(Treatment treatment) {
		if (treatment != null) {
			this.treatments.remove(treatment);
			treatment.setFile(null);
		}
		return this;
	}

	public static Calendar getCalendar(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		cal.setTime(date);
		return cal;
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * D7noun: Getters And Setters
	 * 
	 * 
	 * 
	 * 
	 * *
	 * 
	 * @return
	 */

	/**
	 * @return the fileNumber
	 */
	public Long getFileNumber() {
		return fileNumber;
	}

	/**
	 * @param fileNumber the fileNumber to set
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
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the sex
	 */
	public Sex getSex() {
		return sex;
	}

	/**
	 * @param sex the sex to set
	 */
	public void setSex(Sex sex) {
		this.sex = sex;
	}

	/**
	 * @return the dateOfBirth
	 */
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * @param dateOfBirth the dateOfBirth to set
	 */
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the occupation
	 */
	public String getOccupation() {
		return occupation;
	}

	/**
	 * @param occupation the occupation to set
	 */
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	/**
	 * @return the hasRemainingDept
	 */
	public boolean isHasRemainingDept() {
		return hasRemainingDept;
	}

	/**
	 * @param hasRemainingDept the hasRemainingDept to set
	 */
	public void setHasRemainingDept(boolean hasRemainingDept) {
		this.hasRemainingDept = hasRemainingDept;
	}

	/**
	 * @return the detailsOfPayment
	 */
	public String getDetailsOfPayment() {
		return detailsOfPayment;
	}

	/**
	 * @param detailsOfPayment the detailsOfPayment to set
	 */
	public void setDetailsOfPayment(String detailsOfPayment) {
		this.detailsOfPayment = detailsOfPayment;
	}

	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * @param notes the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * @return the treatments
	 */
	public List<Treatment> getTreatments() {
		return treatments;
	}

	/**
	 * @param treatments the treatments to set
	 */
	public void setTreatments(List<Treatment> treatments) {
		this.treatments = treatments;
	}

	/**
	 * @return the attachments
	 */
	public Set<Attachment> getAttachments() {
		return attachments;
	}

	/**
	 * @param attachments the attachments to set
	 */
	public void setAttachments(Set<Attachment> attachments) {
		this.attachments = attachments;
	}

	/**
	 * @return the findallfilesbytreatmentdrname
	 */
	public static String getFindallfilesbytreatmentdrname() {
		return findAllFilesByTreatmentDrName;
	}

	/**
	 * @return the getallphonenumbers
	 */
	public static String getGetallphonenumbers() {
		return getAllPhoneNumbers;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String displayName() {
		return getName() + " | " + getPhoneNumber();
	}

	/**
	 * @return the medicalHistories
	 */
	public List<MedicalHistory> getMedicalHistories() {
		return medicalHistories;
	}

	/**
	 * @param medicalHistories the medicalHistories to set
	 */
	public void setMedicalHistories(List<MedicalHistory> medicalHistories) {
		this.medicalHistories = medicalHistories;
	}

	/**
	 * @return the nationality
	 */
	public String getNationality() {
		return nationality;
	}

	/**
	 * @param nationality the nationality to set
	 */
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	/**
	 * @return the inssuranceCompany
	 */
	public String getInssuranceCompany() {
		return inssuranceCompany;
	}

	/**
	 * @param inssuranceCompany the inssuranceCompany to set
	 */
	public void setInssuranceCompany(String inssuranceCompany) {
		this.inssuranceCompany = inssuranceCompany;
	}

	/**
	 * @return the mainProblem
	 */
	public String getMainProblem() {
		return mainProblem;
	}

	/**
	 * @param mainProblem the mainProblem to set
	 */
	public void setMainProblem(String mainProblem) {
		this.mainProblem = mainProblem;
	}

	/**
	 * @return the firstTest
	 */
	public String getFirstTest() {
		return firstTest;
	}

	/**
	 * @param firstTest the firstTest to set
	 */
	public void setFirstTest(String firstTest) {
		this.firstTest = firstTest;
	}

	/**
	 * @return the firstDiagnose
	 */
	public String getFirstDiagnose() {
		return firstDiagnose;
	}

	/**
	 * @param firstDiagnose the firstDiagnose to set
	 */
	public void setFirstDiagnose(String firstDiagnose) {
		this.firstDiagnose = firstDiagnose;
	}

	/**
	 * @return the treatmentPlan
	 */
	public String getTreatmentPlan() {
		return treatmentPlan;
	}

	/**
	 * @param treatmentPlan the treatmentPlan to set
	 */
	public void setTreatmentPlan(String treatmentPlan) {
		this.treatmentPlan = treatmentPlan;
	}

	/**
	 * @return the bloodPressure
	 */
	public String getBloodPressure() {
		return bloodPressure;
	}

	/**
	 * @param bloodPressure the bloodPressure to set
	 */
	public void setBloodPressure(String bloodPressure) {
		this.bloodPressure = bloodPressure;
	}

	/**
	 * @return the bloodType
	 */
	public String getBloodType() {
		return bloodType;
	}

	/**
	 * @param bloodType the bloodType to set
	 */
	public void setBloodType(String bloodType) {
		this.bloodType = bloodType;
	}

	/**
	 * @return the weight
	 */
	public String getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(String weight) {
		this.weight = weight;
	}

	/**
	 * @return the height
	 */
	public String getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * @return the temperature
	 */
	public String getTemperature() {
		return temperature;
	}

	/**
	 * @param temperature the temperature to set
	 */
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	/**
	 * @return the breathing
	 */
	public String getBreathing() {
		return breathing;
	}

	/**
	 * @param breathing the breathing to set
	 */
	public void setBreathing(String breathing) {
		this.breathing = breathing;
	}
}