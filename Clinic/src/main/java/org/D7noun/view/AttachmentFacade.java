package org.D7noun.view;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.D7noun.model.Attachment;
import org.D7noun.model.File;

@Stateful
@Local
public class AttachmentFacade implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@PersistenceContext(unitName = "clinic-pu", type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	public Attachment save(Attachment attachment) {
		return entityManager.merge(attachment);
	}

	public Attachment findById(long attachmentItemId) {
		try {
			Query query = getEntityManager().createNamedQuery(Attachment.findById, Attachment.class);
			query.setParameter(1, attachmentItemId);
			return (Attachment) query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("D7noun: getAttachmentDataFromAttachmentId");
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Attachment> findALl() {
		try {
			Query query = getEntityManager().createNamedQuery(Attachment.getAttacmentsByFileId, Attachment.class);
			return query.getResultList();
		} catch (Exception e) {
			System.err.println("D7noun: getAllValues");
			e.printStackTrace();
		}
		return null;
	}

	public void removeAttachmentFromTable(File file, Attachment deletedAttachment) {
		try {
			deletedAttachment.setFile(null);
			this.entityManager.merge(file);
			this.entityManager.remove(this.entityManager.contains(deletedAttachment) ? deletedAttachment
					: this.entityManager.merge(deletedAttachment));
			this.entityManager.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
