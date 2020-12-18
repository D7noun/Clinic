package org.D7noun.view;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.D7noun.abstraction.AbstractFacade;
import org.D7noun.model.Expense;

@Stateful
@Local
public class ExpenseFacade extends AbstractFacade<Expense> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PersistenceContext(unitName = "clinic-pu", type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	public Expense save(Expense expense) {
		return entityManager.merge(expense);
	}

	public void delete(Expense expense) {
		entityManager.remove(expense);
	}

	@SuppressWarnings("unchecked")
	public List<Expense> FindAllExpensesFromDateToDate(Date fromDate, Date toDate) {
		try {
			Query query = entityManager.createNamedQuery(Expense.fromDateToDate, Expense.class);
			query.setParameter("p1", fromDate);
			query.setParameter("p2", toDate);
			return query.getResultList();
		} catch (Exception e) {
			System.err.println("D7noun: FindAllExpensesFromDateToDate");
			e.printStackTrace();
		}
		return null;
	}

	public double getExpenseSum(List<Expense> allExpenses) {
		if (allExpenses != null) {
			double sum = 0;
			for (Expense expense : allExpenses) {
				sum += expense.getValue();
			}
			return sum;
		}
		return 0;
	}

	/**
	 * 
	 * D7noun
	 * 
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
