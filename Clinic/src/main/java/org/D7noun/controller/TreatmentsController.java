package org.D7noun.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.D7noun.model.File;
import org.D7noun.model.Treatment;
import org.D7noun.model.Settings.SettingType;
import org.D7noun.view.FileBean;
import org.D7noun.view.SettingsFacade;
import org.D7noun.view.TreatmentBean;

@ManagedBean
@ViewScoped
public class TreatmentsController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	private FileBean fileBean;
	@EJB
	private TreatmentBean treatmentBean;
	@EJB
	private SettingsFacade settingsFacade;

	private String fileId;
	private File file;
	///////////////////////////
	private Treatment treatmentForDelete;
	private List<String> allTreatments = new ArrayList<String>();
	private List<String> allDoctors = new ArrayList<String>();

	public void preRenderViewEvent() {
		if (!FacesContext.getCurrentInstance().isPostback()) {
			if (fileId != null) {
				file = fileBean.findById(Long.parseLong(fileId));
			}
		}
	}

	@PostConstruct
	public void init() {
		try {
			allTreatments = settingsFacade.findSettingValuesByType(SettingType.TREATMENT);
			allDoctors = settingsFacade.findSettingValuesByType(SettingType.DOCTOR);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("D7noun: TreatmentController.init");
		}
	}

	public void addTreatment() {
		try {
			Treatment treatment = new Treatment();
			file.addNewTreatment(treatment);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

	public void save() {
		try {
			if (sumBalance() > 0) {
				this.file.setHasRemainingDept(true);
			} else {
				this.file.setHasRemainingDept(false);
			}
			file = fileBean.save(file);
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Save Succeeded"));
		} catch (Exception exception) {
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Save Failed"));
			exception.printStackTrace();
		}
	}

	public void selectForDelete(Treatment treatment) {
		treatmentForDelete = treatment;
	}

	public void delete() {
		try {
			file.removeTreatment(treatmentForDelete);
			file = fileBean.save(file);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	// D7noun Summation

	public double sumTotal() {
		double sum = 0;
		if (file != null && !file.getTreatments().isEmpty()) {
			for (Treatment treatment : file.getTreatments()) {
				sum += treatment.getTotal();
			}
		}
		return sum;
	}

	public double sumReceived() {
		double sum = 0;
		if (file != null && !file.getTreatments().isEmpty()) {
			for (Treatment treatment : file.getTreatments()) {
				sum += treatment.getReceived();
			}
		}
		return sum;
	}

	public double sumBalance() {
		double sum = 0;
		if (file != null && !file.getTreatments().isEmpty()) {
			for (Treatment treatment : file.getTreatments()) {
				sum += treatment.getBalance();
			}
		}
		return sum;
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * D7noun: Getters and Setters
	 * 
	 * 
	 * 
	 * 
	 * *
	 */
	/**
	 * @return the fileId
	 */
	public String getFileId() {
		return fileId;
	}

	/**
	 * @param fileId the fileId to set
	 */
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
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
	 * @return the fileBean
	 */
	public FileBean getFileBean() {
		return fileBean;
	}

	/**
	 * @param fileBean the fileBean to set
	 */
	public void setFileBean(FileBean fileBean) {
		this.fileBean = fileBean;
	}

	public Treatment getTreatmentForDelete() {
		return treatmentForDelete;
	}

	public void setTreatmentForDelete(Treatment treatmentForDelete) {
		this.treatmentForDelete = treatmentForDelete;
	}

	/**
	 * @return the allTreatments
	 */
	public List<String> getAllTreatments() {
		return allTreatments;
	}

	/**
	 * @param allTreatments the allTreatments to set
	 */
	public void setAllTreatments(List<String> allTreatments) {
		this.allTreatments = allTreatments;
	}

	/**
	 * @return the allDoctors
	 */
	public List<String> getAllDoctors() {
		return allDoctors;
	}

	/**
	 * @param allDoctors the allDoctors to set
	 */
	public void setAllDoctors(List<String> allDoctors) {
		this.allDoctors = allDoctors;
	}
}