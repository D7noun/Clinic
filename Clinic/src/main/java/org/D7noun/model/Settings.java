package org.D7noun.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.D7noun.abstraction.AbstractEntity;

@Entity
@Table(name = "settings")
@NamedQueries({
		@NamedQuery(name = Settings.getAllSettingByType, query = "SELECT s FROM Settings s where s.type = :type "),
		@NamedQuery(name = Settings.getAllSettingValuesByType, query = "SELECt s.value FROM Settings s where s.type = :type") })
public class Settings extends AbstractEntity implements Serializable {

	public static final String getAllSettingByType = "Setting.getAllSettingByType";
	public static final String getAllSettingValuesByType = "Setting.getAllSettingValuesByType";

	public enum SettingType {
		TREATMENT, DISEASE, DOCTOR, EXPENSE;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.STRING)
	private SettingType type;

	private String value;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Settings [id=" + id + ", type=" + type + ", value=" + value + "]";
	}

	/**
	 * @return the getAllSettingByType
	 */
	public static String getGetAllSettingByType() {
		return getAllSettingByType;
	}

	/**
	 * @return the type
	 */
	public SettingType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(SettingType type) {
		this.type = type;
	}

}
