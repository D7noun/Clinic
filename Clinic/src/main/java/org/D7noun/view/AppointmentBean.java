package org.D7noun.view;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
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

import org.D7noun.dtos.AppointmentDto;
import org.D7noun.dtos.ScheduleDto;
import org.D7noun.model.Appointment;
import org.D7noun.model.File;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 * Backing bean for Appointment entities.
 * <p/>
 * This class provides CRUD functionality for all Appointment entities. It
 * focuses purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt>
 * for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class AppointmentBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving Appointment entities
	 */

	private Long id;

	@Inject
	private FileBean fileBean;

	@Inject
	private Conversation conversation;

	private Appointment appointmentForDelete;

	private Appointment appointment;

	@PersistenceContext(unitName = "clinic-pu", type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	public String create() {
		this.conversation.begin();
		this.conversation.setTimeout(1800000L);
		return "schedule?faces-redirect=true";
	}

	public Appointment findById(Long id) {

		return this.entityManager.find(Appointment.class, id);
	}

	public void delete() {
		try {
			if (appointmentForDelete != null) {
				appointmentForDelete.setFile(null);
				this.entityManager.remove(appointmentForDelete);
				this.entityManager.flush();
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
		}
	}

	/*
	 * Support searching Appointment entities with pagination
	 */

	private int page;
	private long count;
	private List<Appointment> pageItems;

	private Appointment example = new Appointment();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Appointment getExample() {
		return this.example;
	}

	public void setExample(Appointment example) {
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
		Root<Appointment> root = countCriteria.from(Appointment.class);
		countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Appointment> criteria = builder.createQuery(Appointment.class);
		root = criteria.from(Appointment.class);
		TypedQuery<Appointment> query = this.entityManager
				.createQuery(criteria.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Appointment> root) {
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		try {
			File file = this.example.getFile();
			Date startDate = this.example.getStartDate();
			Date endDate = this.example.getEndDate();
			String drName = this.example.getDrName();

			if (file != null) {
				predicatesList.add(builder.equal(root.get("file"), file));
			}
			if (startDate != null) {
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
				String paramString = formatter.format(startDate);
				Date paramDate = formatter.parse(paramString);
				predicatesList.add(builder.greaterThanOrEqualTo(root.<Date>get("startDate"), paramDate));

				if (endDate == null) {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
					String string = simpleDateFormat.format(startDate);
					Date date = formatter.parse(string);
					paramDate = lastHourOfDate(date);
					predicatesList.add(builder.lessThanOrEqualTo(root.<Date>get("startDate"), paramDate));
				}
			}

			if (endDate != null) {
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
				String paramString = formatter.format(endDate);
				Date paramDate = formatter.parse(paramString);
				paramDate = lastHourOfDate(paramDate);
				predicatesList.add(builder.lessThanOrEqualTo(root.<Date>get("endDate"), paramDate));
			}
			if (drName != null && !"".equals(drName)) {
				predicatesList.add(builder.equal(root.get("drName"), drName));
			}

		} catch (Exception e) {
			System.err.println("D7noun: getSearchPredicates");
			e.printStackTrace();
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);

	}

	private Date lastHourOfDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		return cal.getTime();
	}

	public List<Appointment> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Appointment entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Appointment> getAll() {

		CriteriaQuery<Appointment> criteria = this.entityManager.getCriteriaBuilder().createQuery(Appointment.class);
		return this.entityManager.createQuery(criteria.select(criteria.from(Appointment.class))).getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final AppointmentBean ejbProxy = this.sessionContext.getBusinessObject(AppointmentBean.class);

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

				return String.valueOf(((Appointment) value).getId());
			}
		};
	}

	/**
	 * 
	 * D7noun: Primefaces Schedual
	 * 
	 */

	private ScheduleModel lazyEventModel;

	private ScheduleEvent event = new DefaultScheduleEvent();

	@PostConstruct
	public void init() {

		if (lazyEventModel == null) {
			lazyEventModel = new LazyScheduleModel() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void loadEvents(Date startDate, Date endDate) {
					List<AppointmentDto> appointments = new ArrayList<AppointmentDto>();
					appointments = findAppointmentsBetweenTwoDates(startDate, endDate);

					if (appointments != null) {
						for (AppointmentDto appointment : appointments) {

							ScheduleDto scheduleDto = new ScheduleDto();
							scheduleDto.setAppointmentId(appointment.getAppointmentId());

							scheduleDto.setFile(fileBean.findById(appointment.getFileNumber()));
							scheduleDto.setDrName(appointment.getDrName());

							event = new DefaultScheduleEvent(appointment.display(), appointment.getStartDate(),
									appointment.getEndDate(), scheduleDto);

							lazyEventModel.addEvent(event);
							event = new DefaultScheduleEvent();
						}
					}

				}

			};
		}

	}

	@SuppressWarnings("unchecked")
	private List<AppointmentDto> findAppointmentsBetweenTwoDates(Date startDate, Date endDate) {
		try {
			Query query = entityManager.createNamedQuery(Appointment.findAppointmentsBetweenTwoDates);

			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);

			List<AppointmentDto> appointments = query.getResultList();
			return appointments;

		} catch (Exception e) {
			System.err.println("D7noun: findAppointmentsBetweenTwoDates");
			e.printStackTrace();
		}
		return null;
	}

	public void saveEvent() {
		if (event.getId() == null) {
			lazyEventModel.addEvent(event);
			ScheduleDto scheduleDto = (ScheduleDto) event.getData();

			if (scheduleDto != null) {
				appointment = new Appointment();
				appointment.setFile(scheduleDto.getFile());
				appointment.setDrName(scheduleDto.getDrName());
				appointment.setStartDate(event.getStartDate());
				appointment.setEndDate(event.getEndDate());
				appointment = this.entityManager.merge(appointment);
				this.entityManager.flush();
			}

		} else {
			lazyEventModel.updateEvent(event);
			ScheduleDto scheduleDto = (ScheduleDto) event.getData();

			if (scheduleDto != null) {
				appointment = findById(scheduleDto.getAppointmentId());
				appointment.setFile(scheduleDto.getFile());
				appointment.setDrName(scheduleDto.getDrName());
				appointment.setStartDate(event.getStartDate());
				appointment.setEndDate(event.getEndDate());
				appointment = this.entityManager.merge(appointment);
				this.entityManager.flush();
			}
		}

		event = new DefaultScheduleEvent();
	}

	public void selectForDelete() {
		if (event.getId() != null) {
			ScheduleDto dto = (ScheduleDto) event.getData();
			appointmentForDelete = findById(dto.getAppointmentId());
		}
	}

	public void onEventSelect(SelectEvent selectEvent) {
		event = (ScheduleEvent) selectEvent.getObject();
	}

	public void onDateSelect(SelectEvent selectEvent) {

		Date startDate = (Date) selectEvent.getObject();
		Date endDate = (Date) selectEvent.getObject();
		startDate = returnDateAt12Pm(startDate);
		endDate = returnDateAfterParamMinuites(startDate, 30);

		event = new DefaultScheduleEvent("", startDate, endDate, new ScheduleDto());
	}

	public void onEventMove(ScheduleEntryMoveEvent scheduleEntryMoveEvent) {
		ScheduleDto scheduleDto = (ScheduleDto) scheduleEntryMoveEvent.getScheduleEvent().getData();
		if (scheduleDto != null) {
			appointment = findById(scheduleDto.getAppointmentId());
			appointment.setFile(scheduleDto.getFile());
			appointment.setDrName(scheduleDto.getDrName());
			appointment.setStartDate(scheduleEntryMoveEvent.getScheduleEvent().getStartDate());
			appointment.setEndDate(scheduleEntryMoveEvent.getScheduleEvent().getEndDate());
			this.entityManager.merge(appointment);
		}
	}

	public void onEventResize(ScheduleEntryResizeEvent scheduleEntryResizeEvent) {
		ScheduleDto scheduleDto = (ScheduleDto) scheduleEntryResizeEvent.getScheduleEvent().getData();
		if (scheduleDto != null) {
			appointment = findById(scheduleDto.getAppointmentId());
			appointment.setFile(scheduleDto.getFile());
			appointment.setDrName(scheduleDto.getDrName());
			appointment.setStartDate(scheduleEntryResizeEvent.getScheduleEvent().getStartDate());
			appointment.setEndDate(scheduleEntryResizeEvent.getScheduleEvent().getEndDate());
			this.entityManager.merge(appointment);
		}
	}

	private Date returnDateAfterParamMinuites(Date date, int mins) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, mins);
		return cal.getTime();
	}

	private Date returnDateAt12Pm(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, 12);
		return calendar.getTime();
	}

	/**
	 * @return the lazyEventModel
	 */
	public ScheduleModel getLazyEventModel() {
		return lazyEventModel;
	}

	/**
	 * @param lazyEventModel the lazyEventModel to set
	 */
	public void setLazyEventModel(ScheduleModel lazyEventModel) {
		this.lazyEventModel = lazyEventModel;
	}

	public ScheduleEvent getEvent() {
		return event;
	}

	public void setEvent(ScheduleEvent event) {
		this.event = event;
	}

	public Appointment getAppointment() {
		return this.appointment;
	}

	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}

	public FileBean getFileBean() {
		return fileBean;
	}

	public void setFileBean(FileBean fileBean) {
		this.fileBean = fileBean;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the conversation
	 */
	public Conversation getConversation() {
		return conversation;
	}

	/**
	 * @param conversation the conversation to set
	 */
	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}

	public Appointment getAppointmentForDelete() {
		return appointmentForDelete;
	}

	public void setAppointmentForDelete(Appointment appointmentForDelete) {
		this.appointmentForDelete = appointmentForDelete;
	}

}