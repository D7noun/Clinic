package org.D7noun.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.D7noun.model.Expense;
import org.D7noun.model.Settings;
import org.D7noun.model.Settings.SettingType;
import org.D7noun.view.ExpenseFacade;
import org.D7noun.view.SettingsFacade;
import org.D7noun.view.TreatmentBean;

@ManagedBean
@ViewScoped
public class ExpenseController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	private ExpenseFacade expenseFacade;
	@EJB
	private TreatmentBean treatmentBean;
	@EJB
	private SettingsFacade settingsFacade;
	//////////////////////////////////////

	private Date fromDate;
	private Date toDate;
	//////////////////////////////////////
	private List<Expense> allExpenses = new ArrayList<Expense>();
	private List<Settings> allExpenseKinds = new ArrayList<Settings>();
	private Expense selectExpense;

	/////////////////////////
	/////////////////////////
	/////////////////////////

	@PostConstruct
	public void init() {
		allExpenseKinds = settingsFacade.findSettingByType(SettingType.EXPENSE);
		/////////////////
	}

	public void search() {
		try {
			allExpenses = expenseFacade.FindAllExpensesFromDateToDate(fromDate, toDate);
		} catch (Exception e) {
			System.err.println("D7noun: Search Expenses");
			e.printStackTrace();
		}
	}

	public TreatmentBean getTreatmentBean() {
		return treatmentBean;
	}

	public void setTreatmentBean(TreatmentBean treatmentBean) {
		this.treatmentBean = treatmentBean;
	}

	public SettingsFacade getSettingsFacade() {
		return settingsFacade;
	}

	public void setSettingsFacade(SettingsFacade settingsFacade) {
		this.settingsFacade = settingsFacade;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public void addNewExpense() {
		Expense expense = new Expense();
		allExpenses.add(expense);
	}

	public void selectExpenseForDelete(Expense expense) {
		selectExpense = expense;
	}

	public void deleteExpense() {
		try {
			if (selectExpense != null) {
				expenseFacade.delete(selectExpense);
				allExpenses.remove(selectExpense);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			expenseFacade.save(allExpenses);
			FacesContext context = FacesContext.getCurrentInstance();
			context.addMessage(null, new FacesMessage("Save Succeeded"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public double getExpenseSum() {
		return expenseFacade.getExpenseSum(allExpenses);
	}

	public double getIncome() {
		return treatmentBean.getReceivedSum(fromDate, toDate);
	}

	public double getTotal() {
		return getIncome() - getExpenseSum();
	}

	/**
	 * 
	 * 
	 * 
	 * D7noun
	 * 
	 * 
	 */
	public ExpenseFacade getExpenseFacade() {
		return expenseFacade;
	}

	public void setExpenseFacade(ExpenseFacade expenseFacade) {
		this.expenseFacade = expenseFacade;
	}

	public List<Expense> getAllExpenses() {
		return allExpenses;
	}

	public void setAllExpenses(List<Expense> allExpenses) {
		this.allExpenses = allExpenses;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Expense getSelectExpense() {
		return selectExpense;
	}

	public void setSelectExpense(Expense selectExpense) {
		this.selectExpense = selectExpense;
	}

	/**
	 * @return the allExpenseKinds
	 */
	public List<Settings> getAllExpenseKinds() {
		return allExpenseKinds;
	}

	/**
	 * @param allExpenseKinds the allExpenseKinds to set
	 */
	public void setAllExpenseKinds(List<Settings> allExpenseKinds) {
		this.allExpenseKinds = allExpenseKinds;
	}

}
