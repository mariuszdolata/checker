package proxy_checker.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.swing.JProgressBar;
import javax.swing.JTable;

import org.apache.log4j.Logger;

import proxy_checker.db.Proxies;

public class CheckTaskRepository implements Runnable {
//
	public Logger logger = Logger.getLogger(CheckTaskRepository.class);
	public Logger db = Logger.getLogger("db");
	private List<Proxies> proxiesToCheck = Collections.synchronizedList(new ArrayList<Proxies>());
	private List<String> status = Collections.synchronizedList(new ArrayList<String>());
	private BrowserSettings browserSettings;
	private EntityManagerFactory entityManagerFactory;
	private int threadId;
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

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
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

	public CheckTaskRepository(int threadId, EntityManagerFactory entityManagerFactory,
			BrowserSettings browserSettings, List<Proxies> proxiesToCheck, List<String> status, JProgressBar progressBar, JTable table) {
		this.setBrowserSettings(browserSettings);
		this.setProxiesToCheck(proxiesToCheck);
		this.setStatus(status);
		this.setThreadId(threadId);
		this.setEntityManagerFactory(entityManagerFactory);
		this.setProgressBar(progressBar);
		this.setTable(table);
	}

	public void run() {
		do{//petla restartuj¹ca watek po bledzie
			try{//glowny blok wykonywania w¹tku
				Integer index = null;
				do{
					synchronized (status){
						index = getProxy(status);	
//						logger.fatal("REPO thread="+this.getThreadId()+"i="+index);
					}
					if(index!=null){
						//uurchomienie pojedynczego testu
						db.info("Wybrano thread="+this.getThreadId()+", i="+index+", proxy="+this.getProxiesToCheck().get(index).getAdres()+", port="+this.getProxiesToCheck().get(index).getPort());
						CheckTask task = new CheckTask(this.getThreadId(),  this.getEntityManagerFactory(), this.getBrowserSettings(), this.getProxiesToCheck().get(index), index);
						//zapisanie statusu dla danego proxy
						task.startChecking();
						synchronized (status){
							setProxy(status, index, task.getResult());							
						}
						//zablokowanie paska postêpu
						synchronized(progressBar){
							progressBar.setValue(progressBar.getValue()+1);
						}
						synchronized(table){
							table.setValueAt(task.getResult(), index, 5);
							table.repaint();
						}
					}
				}while(index!= null);
				
				
			}catch(Exception e){
				logger.error("Thread - zosta³ rzucony wyj¹tek, nast¹pi³o ponowienie");
				logger.error(e.getMessage());
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}while(!Thread.interrupted());
	}

	public synchronized Integer getProxy(List<String> status) {
		int j;
		for (int i = 0; i < status.size(); i++) {
			if (status.get(i).contains("DO SPRAWDZENIA")){
				status.set(i, "PENDING");
				logger.info("WYBRANO watek="+this.getThreadId()+", i="+i+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
				return i;				
			}
		}
		return (Integer) null;
	}

	public synchronized void setProxy(List<String> status, int i, String statusString) {
		status.set(i, statusString);
	}

}
