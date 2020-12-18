package org.D7noun.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.D7noun.abstraction.AbstractFacade;
import org.D7noun.model.Settings;
import org.D7noun.model.Settings.SettingType;

@Stateful
@Local
public class SettingsFacade extends AbstractFacade<Settings> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SettingsFacade() {
		super(Settings.class);
	}

	@PersistenceContext(unitName = "clinic-pu", type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public List<Settings> findSettingByType(SettingType settingType) {
		List<Settings> result = new ArrayList<Settings>();
		try {
			Query query = this.entityManager.createNamedQuery(Settings.getAllSettingByType, Settings.class);
			query.setParameter("type", settingType);
			result = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<String> findSettingValuesByType(SettingType settingType) {
		List<String> result = new ArrayList<String>();
		try {
			Query query = this.entityManager.createNamedQuery(Settings.getAllSettingValuesByType, String.class);
			query.setParameter("type", settingType);
			result = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
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
