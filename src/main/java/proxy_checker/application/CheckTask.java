package proxy_checker.application;

import static java.lang.Math.toIntExact;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParserListener;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import proxy_checker.application.BrowserSettings.Browser;
import proxy_checker.db.Proxies;
import proxy_checker.db.Score;

public class CheckTask {
	//
	public Logger logger = Logger.getLogger(CheckTask.class);
	public Logger dbLogger = Logger.getLogger("db");
	private int threadId;
	private EntityManagerFactory entityManagerFactory;
	private Proxies proxy;
	private BrowserSettings browserSettings;
	private Score score;
	/**
	 * Zmienna przechowujaca statusCode
	 */
	private String result;

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

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Proxies getProxy() {
		return proxy;
	}

	public void setProxy(Proxies proxy) {
		this.proxy = proxy;
	}

	public BrowserSettings getBrowserSettings() {
		return browserSettings;
	}

	public void setBrowserSettings(BrowserSettings browserSettings) {
		this.browserSettings = browserSettings;
	}

	public Score getScore() {
		return score;
	}

	public void setScore(Score score) {
		this.score = score;
	}

	public CheckTask(int threadId, EntityManagerFactory entityManagerFactory, BrowserSettings browserSettings,
			Proxies proxy, int i) {
		this.setThreadId(threadId);
		dbLogger.info("Wczytano Thread="+this.getThreadId()+", i="+i+", proxy="+proxy.getAdres()+", port="+proxy.getPort());
		logger.info("="+this.getThreadId()+"============================================================================");
//		logger.fatal("TASK = "+this.getThreadId()+" PROXY="+this.getProxy().getAdres());
		this.setProxy(proxy);
		this.setEntityManagerFactory(entityManagerFactory);
		this.setBrowserSettings(browserSettings);
//		startChecking();
	}

	/**
	 * metoda uruchamiajaca test wg ustawien przegladarki
	 */
	public void startChecking() {
		logger.info("wejscie w metode start checking");
		if (this.getBrowserSettings().getBrowser() == Browser.HtmlUnit) {
			this.htmlUnitCheck();
		} else if (this.getBrowserSettings().getBrowser() == Browser.Selenium) {
			SeleniumCheck();
			JOptionPane.showMessageDialog(null, "Nie zaimplementowano metody dla Selenium - modu³ rozwojowy", "Browser",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, "¯adna przegl¹darka nie zosta³a wybrana", "Browser",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Wczytanie strony w przegladarce htmlUnit z ustawionym proxy
	 */
	public void htmlUnitCheck() {
		logger.info("adresproxy = " + this.getProxy().getAdres() + ", port=" + this.getProxy().getPort() + ", url="
				+ this.getBrowserSettings().getUrl());
		try {
			logger.info("tworzenie obiektu WebClient");
			WebClient client = new WebClient(BrowserVersion.FIREFOX_45, this.getProxy().getAdres(),
					this.getProxy().getPort());
//			client.setHTMLParserListener(HTMLParserListener.LOG_REPORTER);
			client.getOptions().setTimeout(this.getBrowserSettings().getTimeOut());
			// Wy³¹czenie javaScript
			if (this.getBrowserSettings().isJsDisable())
				client.getOptions().setJavaScriptEnabled(false);
			else
				client.getOptions().setJavaScriptEnabled(true);
			int retry = -1;
			HtmlPage page = null;
			// n-powtórzeñ dozwolonych; zliczanie czasu dla n-powtórzeñ
			long startMs = System.currentTimeMillis();
			int statusCode;
			do {
				try {
					logger.info("Iteracja nr = " + (retry + 1));
					page = client.getPage(this.getBrowserSettings().getUrl());
					retry++;
					statusCode = page.getWebResponse().getStatusCode();
				} catch (Exception e) {
					logger.warn("B³¹d pobierania strony");
					e.printStackTrace();
					retry++;
					statusCode = 404;
				}
			} while (retry < this.getBrowserSettings().getRetry() && statusCode != 200);
			logger.info("Koniec cyklu pobierania strony");
			long stopMs = System.currentTimeMillis();
			if (statusCode == 200) {
				this.setResult("Success");
				logger.info("Webresponse status code SUCCESS");
				// Stworzenie obiektu klasy Score i wstawienie do get (+ insert
				// w db)
				Score score = new Score();
				score.setMs(toIntExact(stopMs - startMs));
				score.setPotwierdzenie(htmlUnitParse(page));
				score.setProby(retry);
				score.setWynik(1.0 - ((score.getMs() * score.getProby()) / (score.getMs() * 3.0)));
				score.setThreadId(this.getThreadId());
				insertScore(score);

			} else {
				this.setResult("FAIL");
				logger.info("WebResponse status Code FAIL =" + page.getWebResponse().getStatusCode());
				// sprawdzenie proxy zakoñczone niepowodzeniem
				Score score = new Score();
				score.setWynik(-1);
				score.setThreadId(this.getThreadId());
				insertScore(score);
			}
		} catch (Exception e) {
			// FAIL
			this.setResult("FAIL");
			Score score = new Score();
			score.setWynik(-1);
			score.setThreadId(this.getThreadId());
			insertScore(score);
			logger.error("Wyst¹pi³ problem z htmlUnit dla url=" + this.getBrowserSettings().getUrl());
			e.printStackTrace();
		}
	}

	public String htmlUnitParse(HtmlPage page) {
		List<HtmlDivision> parsedString = (List<HtmlDivision>) page.getByXPath(this.getBrowserSettings().getxPath());
		if (parsedString.size() > 0) {
			return parsedString.get(0).asText();
		} else{
			this.setResult("FAIL");
			return "FAIL";
		}
	}

	synchronized public void insertScore(Score score) {
		try {
			logger.info("INSERT SCORE->" + score.toString());
			Proxies p = null;
			EntityManager em = this.getEntityManagerFactory().createEntityManager();

			if (!em.getTransaction().isActive())
				em.getTransaction().begin();
			Long id = 0L;
			try {
				id = this.getProxy().getId();
				logger.info("id=" + id);
				p = em.find(Proxies.class, this.getProxy().getId());
				p.getScores().add(score);
				logger.info("PROXIES=" + p.toString());
			} catch (Exception e) {
				logger.info("Problem z pobraniem Proxy o zadanym ID");
				logger.info("Proxy_id=" + id);
			}
			if (em.getTransaction().isActive())
				em.getTransaction().commit();
			em.close();

		} catch (Exception e) {
			logger.error("Nie mo¿na wstawiæ obiektu Score do bazy danych");
			e.printStackTrace();
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void SeleniumCheck() {
		logger.warn("NIE ZAIMPLEMENTOWANO METODY DLA SELENIUM");
	}

}
