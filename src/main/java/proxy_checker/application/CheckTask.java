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

	public CheckTask(int threadId, EntityManagerFactory entityManagerFactory, BrowserSettings browserSettings, Proxies proxy) {
		this.setProxy(proxy);
		this.setThreadId(threadId);
		this.setEntityManagerFactory(entityManagerFactory);
		this.setBrowserSettings(browserSettings);
		startChecking();
	}

	/**
	 * metoda uruchamiajaca test wg ustawien przegladarki
	 */
	public void startChecking() {
		logger.info("wejscie w metode start checking");
		if (this.getBrowserSettings().getBrowser() == Browser.HtmlUnit) {
			logger.info("htmlUnit wybrano");
			this.htmlUnitCheck();
			logger.info("koniec htmlUnit check");
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
		logger.info("adresproxy = "+this.getProxy().getAdres());
		logger.info("port="+this.getProxy().getPort());
		logger.info("url="+this.getBrowserSettings().getUrl());
		try {
			logger.info("nastapi stworzenie WebClient");
			WebClient client = new WebClient(BrowserVersion.FIREFOX_38, this.getProxy().getAdres(),
					this.getProxy().getPort());
			client.setHTMLParserListener(HTMLParserListener.LOG_REPORTER);
			client.getOptions().setTimeout(this.getBrowserSettings().getTimeOut());
			// Wy³¹czenie javaScript
			if (this.getBrowserSettings().isJsDisable())
				client.getOptions().setJavaScriptEnabled(false);
			else
				client.getOptions().setJavaScriptEnabled(true);
			int retry = 0;
			HtmlPage page = null;
			// n-powtórzeñ dozwolonych; zliczanie czasu dla n-powtórzeñ
			long startMs = System.currentTimeMillis();
			do {
				page = client.getPage(this.getBrowserSettings().getUrl());
				logger.info("Koniec pobierania strony");
			} while (retry < this.getBrowserSettings().getRetry() && page.getWebResponse().getStatusCode() != 200);
			long stopMs = System.currentTimeMillis();
			this.setResult(Integer.toString(page.getWebResponse().getStatusCode()));
			if (page.getWebResponse().getStatusCode() == 200) {
				logger.info("Webresponse status code ==200");
				// Stworzenie obiektu klasy Score i wstawienie do get (+ insert
				// w db)
				Score score = new Score();
				score.setMs(toIntExact(stopMs - startMs));
				score.setPotwierdzenie(htmlUnitParse(page));
				score.setProby(retry);
				insertScore(score);
				logger.info(score.toString());

			}
		} catch (Exception e) {
			logger.error("Wyst¹pi³ problem z htmlUnit dla url=" + this.getBrowserSettings().getUrl());
			e.printStackTrace();
		}
	}

	public String htmlUnitParse(HtmlPage page) {
		List<HtmlDivision> parsedString = (List<HtmlDivision>) page.getByXPath(this.getBrowserSettings().getxPath());
		if (parsedString.size() > 0) {
			return parsedString.get(0).asText();
		} else
			return "FAIL";
	}

	public void insertScore(Score score) {
		try {
			logger.info("INSERT SCORE");
			EntityManager em = this.getEntityManagerFactory().createEntityManager();
			if(!em.getTransaction().isActive())em.getTransaction().begin();
//			this.proxy.getScores().add(score);
			Proxies p = em.find(Proxies.class, this.getProxy().getId());
			p.getScores().add(score);
//			TypedQuery<Proxies> q = em.createNamedQuery("SELECT p FROM Proxies Where id like :id", Proxies.class).setParameter("id", this.getProxy().getId());
//			Proxies p = q.getSingleResult();
			logger.info("PROXIES="+p.toString());
			if(em.getTransaction().isActive())em.getTransaction().commit();
			em.close();
		} catch (Exception e) {
			logger.error("Nie mo¿na wstawiæ obiektu Score do bazy danych");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void SeleniumCheck() {
		logger.warn("NIE ZAIMPLEMENTOWANO METODY DLA SELENIUM");
	}

}
