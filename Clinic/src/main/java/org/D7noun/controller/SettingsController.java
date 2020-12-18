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

import org.D7noun.model.Settings;
import org.D7noun.model.Settings.SettingType;
import org.D7noun.view.SettingsFacade;
import org.omnifaces.util.Ajax;

@ManagedBean
@ViewScoped
public class SettingsController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	private SettingsFacade settingsFacade;

	private List<Settings> doctors = new ArrayList<Settings>();
	private List<Settings> expenses = new ArrayList<Settings>();
	private List<Settings> diseases = new ArrayList<Settings>();
	private List<Settings> treatments = new ArrayList<Settings>();

	private Settings selectForDelete;

	@PostConstruct
	public void init() {
		doctors = settingsFacade.findSettingByType(SettingType.DOCTOR);
		expenses = settingsFacade.findSettingByType(SettingType.EXPENSE);
		diseases = settingsFacade.findSettingByType(SettingType.DISEASE);
		treatments = settingsFacade.findSettingByType(SettingType.TREATMENT);
	}

	public void addDoctor() {
		try {
			Settings settings = new Settings();
			settings.setType(SettingType.DOCTOR);
			doctors.add(settings);
			Ajax.oncomplete("PF('doctorVar').filter()");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addExpense() {
		try {
			Settings settings = new Settings();
			settings.setType(SettingType.EXPENSE);
			expenses.add(settings);
			Ajax.oncomplete("PF('expenseVar').filter()");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addDisease() {
		try {
			Settings settings = new Settings();
			settings.setType(SettingType.DISEASE);
			diseases.add(settings);
			Ajax.oncomplete("PF('diseaseVar').filter()");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addTreatment() {
		try {
			Settings settings = new Settings();
			settings.setType(SettingType.TREATMENT);
			treatments.add(settings);
			Ajax.oncomplete("PF('treatmentVar').filter()");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void selectForDeleteAction(Settings settings) {
		selectForDelete = settings;
	}

	public void deleteDisease() {
		try {
			if (selectForDelete != null) {
				diseases.remove(selectForDelete);
				settingsFacade.remove(selectForDelete);
				Ajax.oncomplete("PF('diseaseVar').filter()");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteDoctor() {
		try {
			if (selectForDelete != null) {
				doctors.remove(selectForDelete);
				settingsFacade.remove(selectForDelete);
				Ajax.oncomplete("PF('doctorVar').filter()");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteExpense() {
		try {
			if (selectForDelete != null) {
				expenses.remove(selectForDelete);
				settingsFacade.remove(selectForDelete);
				Ajax.oncomplete("PF('expenseVar').filter()");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteTreatment() {
		try {
			if (selectForDelete != null) {
				treatments.remove(selectForDelete);
				settingsFacade.remove(selectForDelete);
				Ajax.oncomplete("PF('treatmentVar').filter()");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		FacesContext context = FacesContext.getCurrentInstance();
		try {
			settingsFacade.save(diseases);
			settingsFacade.save(expenses);
			settingsFacade.save(doctors);
			settingsFacade.save(treatments);
			context.addMessage(null, new FacesMessage("Save Succeeded"));
		} catch (Exception exception) {
			exception.printStackTrace();
			context.addMessage(null, new FacesMessage("Save Failed"));
		}
	}

	/**
	 * 
	 * D7noun
	 * 
	 */

	public SettingsFacade getSettingsFacade() {
		return settingsFacade;
	}

	public void setSettingsFacade(SettingsFacade settingsFacade) {
		this.settingsFacade = settingsFacade;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the doctors
	 */
	public List<Settings> getDoctors() {
		return doctors;
	}

	/**
	 * @param doctors the doctors to set
	 */
	public void setDoctors(List<Settings> doctors) {
		this.doctors = doctors;
	}

	/**
	 * @return the expenses
	 */
	public List<Settings> getExpenses() {
		return expenses;
	}

	/**
	 * @param expenses the expenses to set
	 */
	public void setExpenses(List<Settings> expenses) {
		this.expenses = expenses;
	}

	/**
	 * @return the diseases
	 */
	public List<Settings> getDiseases() {
		return diseases;
	}

	/**
	 * @param diseases the diseases to set
	 */
	public void setDiseases(List<Settings> diseases) {
		this.diseases = diseases;
	}

	/**
	 * @return the treatments
	 */
	public List<Settings> getTreatments() {
		return treatments;
	}

	/**
	 * @param treatments the treatments to set
	 */
	public void setTreatments(List<Settings> treatments) {
		this.treatments = treatments;
	}

	/**
	 * @return the selectForDelete
	 */
	public Settings getSelectForDelete() {
		return selectForDelete;
	}

	/**
	 * @param selectForDelete the selectForDelete to set
	 */
	public void setSelectForDelete(Settings selectForDelete) {
		this.selectForDelete = selectForDelete;
	}

}