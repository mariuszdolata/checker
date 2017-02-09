package proxy_checker.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.swing.JProgressBar;
import javax.swing.JTable;

import proxy_checker.db.Proxies;

public class StartTask {
	//

	private BrowserSettings browserSettings;
	private List<Proxies> proxiesToCheck = Collections.synchronizedList(new ArrayList<Proxies>());
	private List<String> status = Collections.synchronizedList(new ArrayList<String>());
	private EntityManagerFactory entityManagerFactory;
	private JProgressBar progressBar;
	private JTable table;

	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(JProgressBar progressBar) {
		this.progressBar = progressBar;
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public BrowserSettings getBrowserSettings() {
		return browserSettings;
	}

	public void setBrowserSettings(BrowserSettings browserSettings) {
		this.browserSettings = browserSettings;
	}

	public List<Proxies> getProxiesToCheck() {
		return proxiesToCheck;
	}

	public void setProxiesToCheck(List<Proxies> proxiesToCheck) {
		this.proxiesToCheck = proxiesToCheck;
	}

	public List<String> getStatus() {
		return status;
	}

	public void setStatus(List<String> status) {
		this.status = status;
	}

	public StartTask(EntityManagerFactory entityManagerFactory, BrowserSettings browserSettings,
			List<Proxies> proxiesToCheck, List<String> status, JProgressBar progressBar, JTable table) {
		this.setBrowserSettings(browserSettings);
		this.setProxiesToCheck(proxiesToCheck);
		this.setStatus(status);
		this.setEntityManagerFactory(entityManagerFactory);
		this.setProgressBar(progressBar);
		this.setTable(table);
	}

	public void start() {
		CheckTaskRepository[] tasks = new CheckTaskRepository[this.getBrowserSettings().getNumberOfThreads()];
		Thread[] threads = new Thread[this.getBrowserSettings().getNumberOfThreads()];
		for (int i = 0; i < this.getBrowserSettings().getNumberOfThreads(); i++) {
			tasks[i] = new CheckTaskRepository(i, this.getEntityManagerFactory(), this.getBrowserSettings(),
					this.getProxiesToCheck(), this.getStatus(), this.getProgressBar(), this.getTable());
		}
		for (int i = 0; i < this.getBrowserSettings().getNumberOfThreads(); i++) {
			threads[i] = new Thread(tasks[i]);
		}
		for (int i = 0; i < this.getBrowserSettings().getNumberOfThreads(); i++) {
			threads[i].start();
		}
	}

}
