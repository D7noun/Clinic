package org.D7noun.abstraction;

import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;

public class GeneralUtility {
	public static String getOutcomeURL(String outcome) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		try {
			return ((ConfigurableNavigationHandler) facesContext.getApplication().getNavigationHandler())
					.getNavigationCase(facesContext, "", outcome).getRedirectURL(facesContext).toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getResourceBundleString(String resourceBundleKey) {
		String bundleString = "";
		FacesContext facesContext = FacesContext.getCurrentInstance();

		ResourceBundle bundle = ResourceBundle.getBundle("resources/application");
		try {
			bundleString = bundle.getString(resourceBundleKey);
		} catch (Exception e) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "error", resourceBundleKey));
			e.printStackTrace();
		}
		return bundleString;
	}

	public static String getResourceBundleString(String resourceBundleKey, Locale locale) {
		String bundleString = "";
		FacesContext facesContext = FacesContext.getCurrentInstance();
		try {
			bundleString = ResourceBundle.getBundle("bundle", locale).getString(resourceBundleKey);
		} catch (Exception e) {
			facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "error", resourceBundleKey));
			e.printStackTrace();
		}
		return bundleString;
	}

	public static String getResourceBundleString(String resourceBundleKey, Object... params) {
		String pattern = getResourceBundleString(resourceBundleKey);
		return MessageFormat.format(pattern, params);
	}

	public static String getResourceBundleString(String resourceBundleKey, Locale locale, Object... params) {
		String pattern = getResourceBundleString(resourceBundleKey, locale);
		return MessageFormat.format(pattern, params);
	}

	public static void showInfoMessage(String messageKey) {
		FacesContext context = FacesContext.getCurrentInstance();

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, getResourceBundleString("info"),
				getResourceBundleString(messageKey)));
	}

	public static void showWarningMessage(String messageKey) {
		FacesContext context = FacesContext.getCurrentInstance();

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, getResourceBundleString("warning"),
				getResourceBundleString(messageKey)));
	}

	public static void showErrorMessage(String messageKey) {
		FacesContext context = FacesContext.getCurrentInstance();

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, getResourceBundleString("error"),
				getResourceBundleString(messageKey)));
	}

	public static void showSuccessMessage(String messageKey, Object... params) {
		FacesContext context = FacesContext.getCurrentInstance();

		context.addMessage(null,
				new FacesMessage(getResourceBundleString("successful"), getResourceBundleString(messageKey, params)));
	}

	public static void showInfoMessageString(String messageString) {
		FacesContext context = FacesContext.getCurrentInstance();

		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, getResourceBundleString("info"), messageString));
	}

	public static void showWarningMessageString(String messageString) {
		FacesContext context = FacesContext.getCurrentInstance();

		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_WARN, getResourceBundleString("warning"), messageString));
	}

	public static void showErrorMessageString(String messageString) {
		FacesContext context = FacesContext.getCurrentInstance();

		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, getResourceBundleString("error"), messageString));
	}

	public static void showErrorMessage(Exception e) {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, getResourceBundleString("error"), e.getMessage()));
	}

	public static void showSaveMessage() {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null,
				new FacesMessage(getResourceBundleString("successful"), getResourceBundleString("msg_data_saved")));
	}

	public static Date getEndOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	public static Date getStartOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static boolean isPictureExt(String fileExt) {
		String ext = fileExt.toUpperCase();
		return ext.equals("JPG") || ext.equals("PNG") || ext.equals("JPEG") || ext.equals("BMP") || ext.equals("GIF");
	}

}