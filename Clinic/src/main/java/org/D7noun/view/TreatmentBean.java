package org.D7noun.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.D7noun.model.Treatment;
import org.D7noun.model.File;

/**
 * Backing bean for Treatment entities.
 * <p/>
 * This class provides CRUD functionality for all Treatment entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class TreatmentBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving Treatment entities
	 */

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Treatment treatment;

	public Treatment getTreatment() {
		return this.treatment;
	}

	public void setTreatment(Treatment treatment) {
		this.treatment = treatment;
	}

	@Inject
	private Conversation conversation;

	@PersistenceContext(unitName = "clinic-pu", type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	public String create() {

		this.conversation.begin();
		this.conversation.setTimeout(1800000L);
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.conversation.isTransient()) {
			this.conversation.begin();
			this.conversation.setTimeout(1800000L);
		}

		if (this.id == null) {
			this.treatment = this.example;
		} else {
			this.treatment = findById(getId());
		}
	}

	public Treatment findById(Long id) {

		return this.entityManager.find(Treatment.class, id);
	}

	/*
	 * Support updating and deleting Treatment entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.entityManager.persist(this.treatment);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.treatment);
				return "view?faces-redirect=true&id=" + this.treatment.getId();
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public Treatment save(Treatment treatment) {
		return this.entityManager.merge(treatment);
	}

	public String delete() {
		this.conversation.end();

		try {
			Treatment deletableEntity = findById(getId());
			File File = deletableEntity.getFile();
			File.getTreatments().remove(deletableEntity);
			deletableEntity.setFile(null);
			this.entityManager.merge(File);
			this.entityManager.remove(deletableEntity);
			this.entityManager.flush();
			return "search?faces-redirect=true";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public void removeTreatmentFromFileTable(File relatedFile, Treatment deletedTreatment) {
		try {
			deletedTreatment.setFile(null);
			this.entityManager.merge(relatedFile);
			this.entityManager.remove(this.entityManager.contains(deletedTreatment) ? deletedTreatment
					: this.entityManager.merge(deletedTreatment));
			this.entityManager.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Support searching Treatment entities with pagination
	 */

	private int page;
	private long count;
	private List<Treatment> pageItems;

	private Treatment example = new Treatment();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Treatment getExample() {
		return this.example;
	}

	public void setExample(Treatment example) {
		this.example = example;
	}

	public String search() {
		this.page = 0;
		return null;
	}

	public void paginate() {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<Treatment> root = countCriteria.from(Treatment.class);
		countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Treatment> criteria = builder.createQuery(Treatment.class);
		root = criteria.from(Treatment.class);
		TypedQuery<Treatment> query = this.entityManager
				.createQuery(criteria.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Treatment> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String treatment = this.example.getTreatment();
		if (treatment != null && !"".equals(treatment)) {
			predicatesList.add(
					builder.like(builder.lower(root.<String>get("treatment")), '%' + treatment.toLowerCase() + '%'));
		}
		String drName = this.example.getDrName();
		if (drName != null && !"".equals(drName)) {
			predicatesList
					.add(builder.like(builder.lower(root.<String>get("drName")), '%' + drName.toLowerCase() + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Treatment> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Treatment entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Treatment> getAll() {

		CriteriaQuery<Treatment> criteria = this.entityManager.getCriteriaBuilder().createQuery(Treatment.class);
		return this.entityManager.createQuery(criteria.select(criteria.from(Treatment.class))).getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final TreatmentBean ejbProxy = this.sessionContext.getBusinessObject(TreatmentBean.class);

		return new Converter() {

			@Override
			public Object getAsObject(FacesContext context, UIComponent component, String value) {

				return ejbProxy.findById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context, UIComponent component, Object value) {

				if (value == null) {
					return "";
				}

				return String.valueOf(((Treatment) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Treatment add = new Treatment();

	public Treatment getAdd() {
		return this.add;
	}

	public Treatment getAdded() {
		Treatment added = this.add;
		this.add = new Treatment();
		return added;
	}

	/**
	 * 
	 * D7noun
	 * 
	 */

	@SuppressWarnings("unchecked")
	public List<Treatment> getTreatmentsByFileId(String fileId) {
		try {
			Query query = entityManager.createNamedQuery(Treatment.getTreatmentsByFileId, Treatment.class);
			query.setParameter(1, Long.parseLong(fileId));
			return query.getResultList();
		} catch (Exception exception) {
			System.err.println("D7noun: getTreatmentsByFileId");
			exception.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public double getReceivedSum(Date fromDate, Date toDate) {
		try {

			Query query = entityManager.createNamedQuery(Treatment.getAllTreatmentsBetweenDates, Treatment.class);
			query.setParameter(1, fromDate);
			query.setParameter(2, toDate);
			List<Treatment> allTreatments = query.getResultList();
			if (allTreatments != null) {
				double sum = 0;
				for (Treatment treatment : allTreatments) {
					sum += treatment.getReceived();
				}
				return sum;
			}
		} catch (Exception exception) {
			System.err.println("D7noun: getReceivedSum");
			exception.printStackTrace();
		}
		return 0;
	}

}