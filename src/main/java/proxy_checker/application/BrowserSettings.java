package proxy_checker.application;

import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class BrowserSettings {
	private int timeOut;
	private boolean jsDisable;
	private boolean imageDisable;
	private int retry;
	private String url;
	private String xPath;
	private String success;
	private String fail;
	private Browser browser;
	private boolean nonStop;
	private int numberOfThreads;
	public enum Browser{Selenium, HtmlUnit}
	public int getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	public boolean isJsDisable() {
		return jsDisable;
	}
	public void setJsDisable(boolean jsDisable) {
		this.jsDisable = jsDisable;
	}
	public boolean isImageDisable() {
		return imageDisable;
	}
	public void setImageDisable(boolean imageDisable) {
		this.imageDisable = imageDisable;
	}
	public int getRetry() {
		return retry;
	}
	public void setRetry(int retry) {
		this.retry = retry;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getxPath() {
		return xPath;
	}
	public void setxPath(String xPath) {
		this.xPath = xPath;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getFail() {
		return fail;
	}
	public void setFail(String fail) {
		this.fail = fail;
	}
	public Browser getBrowser() {
		return browser;
	}
	public void setBrowser(Browser browser) {
		this.browser = browser;
	}
	
	public boolean isNonStop() {
		return nonStop;
	}
	public void setNonStop(boolean nonStop) {
		this.nonStop = nonStop;
	}
	public int getNumberOfThreads() {
		return numberOfThreads;
	}
	public void setNumberOfThreads(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}
	public BrowserSettings(JTextField timeOut, JCheckBox jsDisabled, JCheckBox imageDisabled, JTextField retry, JTextField url, JTextField xPath,
			JTextField success, JTextField fail, JRadioButton selenium, JRadioButton htmlUnit, JCheckBox nonStop, JTextField numberOfThreads) {
		super();
		if(selenium.isSelected())
			this.setBrowser(Browser.Selenium);
		else if(htmlUnit.isSelected())
			this.setBrowser(Browser.HtmlUnit);
		else
			throw new NullPointerException();
		this.setTimeOut(Integer.parseInt(timeOut.getText()));
		this.setFail(fail.getText());
		if(imageDisabled.isSelected())
			this.setImageDisable(true);
		else
			this.setImageDisable(false);
		if(jsDisabled.isSelected())
			this.setJsDisable(true);
		else
			this.setJsDisable(false);
		this.setRetry(Integer.parseInt(retry.getText()));
		this.setSuccess(success.getText());
		this.setUrl(url.getText());
		this.setxPath(xPath.getText());
		if(nonStop.isSelected())
			this.setNonStop(true);
		else
			this.setNonStop(false);
		this.setNumberOfThreads(Integer.parseInt(numberOfThreads.getText()));
		
	};
	
	

}
