package org.D7noun.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.PhaseId;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.D7noun.model.Attachment;
import org.D7noun.model.File;
import org.D7noun.model.File.Sex;
import org.D7noun.model.MedicalHistory;
import org.D7noun.model.Settings;
import org.D7noun.model.Settings.SettingType;
import org.D7noun.model.Treatment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.omnifaces.util.Faces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 * Backing bean for File entities.
 * <p/>
 * This class provides CRUD functionality for all File entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class FileBean implements Serializable {

	/**
	 * 
	 * D7noun Start
	 * 
	 */

	@EJB
	private SettingsFacade settingsFacade;

	private boolean male;
	private Long fileIdForTreatments;

	private boolean sixMonthsPatients = false;

	private File fileForDelete;

	public File getFileForDelete() {
		return fileForDelete;
	}

	public void setFileForDelete(File fileForDelete) {
		this.fileForDelete = fileForDelete;
	}

	public List<Settings> getAllDoctorNames() {
		try {
			return settingsFacade.findSettingByType(SettingType.DOCTOR);
		} catch (Exception e) {
			System.err.println("D7noun: get all doctor names");
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	public void selectForDelete(File f) {
		fileForDelete = f;
	}

	/**
	 * 
	 * D7noun End
	 * 
	 */

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving File entities
	 */

	private Long id;

	// D7noun: This attribute is used for searching only
	private String drName;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private File file;

	public File getFile() {
		return this.file;
	}

	public void setFile(File file) {
		this.file = file;
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

	public File save(File file) {
		return entityManager.merge(file);
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
			this.file = this.example;
			this.file.setHasRemainingDept(false);
			List<MedicalHistory> medicalHistories = new ArrayList<MedicalHistory>();
			List<Settings> diseases = settingsFacade.findSettingByType(SettingType.DISEASE);
			for (Settings settings : diseases) {
				medicalHistories.add(new MedicalHistory(settings.getValue()));
			}
			this.file.setMedicalHistories(medicalHistories);

		} else {
			this.file = findById(getId());
			if (this.file.getSex() == Sex.MALE) {
				setMale(true);
			} else {
				setMale(false);
			}
		}
	}

	public File findById(Long id) {

		return this.entityManager.find(File.class, id);
	}

	/*
	 * Support updating and deleting File entities
	 */

	public String update() {
		this.conversation.end();

		try {

			if (isMale()) {
				file.setSex(Sex.MALE);
			} else {
				file.setSex(Sex.FEMALE);
			}

			if (this.id == null) {
				this.entityManager.persist(this.file);
				return "create?faces-redirect=true&id=" + this.file.getFileNumber();
			} else {
				this.entityManager.merge(this.file);
				return "create?faces-redirect=true&id=" + this.file.getFileNumber();
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public String delete() {
		this.conversation.end();

		try {
			File deletableEntity = findById(getId());
			this.entityManager.remove(deletableEntity);
			this.entityManager.flush();
			return "search?faces-redirect=true";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
			return null;
		}
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final FileBean ejbProxy = this.sessionContext.getBusinessObject(FileBean.class);

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

				return String.valueOf(((File) value).getFileNumber());
			}
		};
	}

	/*
	 * Support searching File entities with pagination
	 */

	private int page;
	private long count;
	private List<File> pageItems;

	private File example = new File();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public File getExample() {
		return this.example;
	}

	public void setExample(File example) {
		this.example = example;
	}

	public String search() {
		this.page = 0;
		return null;
	}

	public String getDrNames(File file) {
		try {
			String drNames = "";
			List<Treatment> treatments = file.getTreatments();
			List<String> distinctDrNameTreatments = new ArrayList<String>();

			if (treatments != null && !treatments.isEmpty()) {
				for (Treatment treatment : treatments) {
					if (treatment.getDrName() != null && !treatment.getDrName().isEmpty()) {
						if (!distinctDrNameTreatments.contains(treatment.getDrName())) {
							distinctDrNameTreatments.add(treatment.getDrName());
						}
					}
				}

				for (String t : distinctDrNameTreatments) {
					drNames += t + ", ";
				}
				if (drNames.length() > 0) {
					return drNames.substring(0, drNames.length() - 2);
				}
			}

		} catch (Exception e) {
			System.err.println("D7noun:getDrNames");
			e.printStackTrace();
		}
		return "";
	}

	public void paginate() {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<File> root = countCriteria.from(File.class);
		countCriteria = countCriteria.select(builder.count(root)).where(getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria).getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<File> criteria = builder.createQuery(File.class).distinct(true);
		root = criteria.from(File.class);
		TypedQuery<File> query = this.entityManager.createQuery(criteria.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<File> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String name = this.example.getName();
		boolean hasRemainingDept = this.example.isHasRemainingDept();

		if (name != null && !"".equals(name)) {
			predicatesList.add(builder.like(builder.lower(root.<String>get("name")), '%' + name.toLowerCase() + '%'));
		}

		if (hasRemainingDept == true) {
			predicatesList.add(builder.equal(root.<Boolean>get("hasRemainingDept"), hasRemainingDept));
		}

		if (drName != null && !"".equals(drName)) {
			Join<File, Treatment> join = root.join("treatments");
			Predicate predicate = builder.like(builder.lower(join.<String>get("drName")),
					'%' + drName.toLowerCase() + '%');
			predicatesList.add(predicate);
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<File> getPageItems() {

		if (this.pageItems != null && !this.pageItems.isEmpty()) {
			if (sixMonthsPatients) {
				for (Iterator<File> iterator = this.pageItems.iterator(); iterator.hasNext();) {
					File file = iterator.next();
					if (!isFileSixMonthAbsent(file)) {
						iterator.remove();
					}
				}
			}
		}

		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back File entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<File> getAll() {

		CriteriaQuery<File> criteria = this.entityManager.getCriteriaBuilder().createQuery(File.class);
		return this.entityManager.createQuery(criteria.select(criteria.from(File.class))).getResultList();
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private File add = new File();

	public File getAdd() {
		return this.add;
	}

	public File getAdded() {
		File added = this.add;
		this.add = new File();
		return added;
	}

	/**
	 * @return the male
	 */
	public boolean isMale() {
		return male;
	}

	/**
	 * @param male the male to set
	 */
	public void setMale(boolean male) {
		this.male = male;
	}

	/**
	 * @return the drName
	 */
	public String getDrName() {
		return drName;
	}

	/**
	 * @param drName the drName to set
	 */
	public void setDrName(String drName) {
		this.drName = drName;
	}

	public Long getFileIdForTreatments() {
		return fileIdForTreatments;
	}

	public boolean isSixMonthsPatients() {
		return sixMonthsPatients;
	}

	public void setSixMonthsPatients(boolean sixMonthsPatients) {
		this.sixMonthsPatients = sixMonthsPatients;
	}

	/**
	 * 
	 * Start Attachments
	 * 
	 */

	public void handleFileUpload(FileUploadEvent event) {
		try {

			UploadedFile uploadedFile = event.getFile();

			Attachment attachment = new Attachment();
			attachment.setFile(file);
			attachment.setFileName(uploadedFile.getFileName());
			attachment.setSize(uploadedFile.getSize());
			attachment.setContentType(uploadedFile.getContentType());
			attachment.setData(uploadedFile.getContents());
			file.getAttachments().add(attachment);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("ItemBean: handleFileUpload");
		}
	}

	public void download(Attachment attachment) {
		try {
			Faces.sendFile(attachment.getData(), attachment.getFileName(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@EJB
	private AttachmentFacade attachmentFacade;
	private Attachment attachmentFordelete;

	public void selectForDelete(Attachment attachment) {
		attachmentFordelete = attachment;
	}

	public void deleteAttachment() {
		try {
			file.getAttachments().remove(attachmentFordelete);
			attachmentFacade.removeAttachmentFromTable(file, attachmentFordelete);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public StreamedContent getPicture() {
		FacesContext context = FacesContext.getCurrentInstance();

		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			return new DefaultStreamedContent();
		} else {
			String attachmentId = context.getExternalContext().getRequestParameterMap().get("attachmentId");
			if (attachmentId != null) {
				Attachment attachmentItem = attachmentFacade.findById(Long.parseLong(attachmentId));
				StreamedContent streamedContent = new DefaultStreamedContent(
						new ByteArrayInputStream(attachmentItem.getData()));
				return streamedContent;
			}
		}
		return new DefaultStreamedContent();
	}

	/**
	 * 
	 * End Attachments
	 * 
	 */

	public void exportToExcel() {

		try {

			List<String> phoneNumbers = getPhoneNumbers();

			// Create a Workbook
			Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for

			// Create a Sheet
			Sheet sheet = workbook.createSheet("Phone Numbers");

			int rowNum = 0;
			for (String phoneNumber : phoneNumbers) {
				Row row = sheet.createRow(rowNum);
				row.createCell(0).setCellValue(phoneNumber);
				rowNum++;
			}

			// Resize all columns to fit the content size
			for (int i = 0; i < phoneNumbers.size(); i++) {
				sheet.autoSizeColumn(i);
			}

			// Write the output to a file
			ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
			workbook.write(fileOut);

			Faces.sendFile(fileOut.toByteArray(), "phonenumbers.xlsx", true);

			fileOut.close();
			// Closing the workbook
			workbook.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("D7noun: exportToExcel");
		}

	}

	@SuppressWarnings("unchecked")
	private List<String> getPhoneNumbers() {
		try {
			Query query = this.entityManager.createNamedQuery(File.getAllPhoneNumbers, String.class);
			return query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("D7noun: getPhoneNumbers");
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public boolean isFileSixMonthAbsent(File file) {
		Date currentDate = new Date();
		Date lastAppointmentDate = null;
		Query query = this.entityManager.createNamedQuery(File.getMaxAppointmentDateByFileId);
		query.setParameter("file", file);
		List<Date> result = query.getResultList();
		if (result != null && !result.isEmpty()) {
			lastAppointmentDate = result.get(0);
		}
		if (lastAppointmentDate == null) {
			return false;
		}
		long diff = Math.abs(currentDate.getTime() - lastAppointmentDate.getTime());
		long diffDays = diff / (24 * 60 * 60 * 1000);
		if (diffDays < 180) {
			return false;
		}
		return true;
	}

}