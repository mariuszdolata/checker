package proxy_checker.application;

import static java.lang.Math.toIntExact;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import crawler.api.DatabaseAccess;
import proxy_checker.db.Proxies;
import proxy_checker.db.Score;

public class CheckTask extends DatabaseAccess {
	private Proxies proxy;
	private BrowserSettings browserSettings;
	private Score score;

	

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



	public CheckTask(int threadId, Properties properties, EntityManagerFactory entityManagerFactory) {
		super(threadId, properties, entityManagerFactory);
		// TODO Auto-generated constructor stub
	}

	public Logger logger = Logger.getLogger(CheckTask.class);


	

	

	/**
	 * Wczytanie strony w przegladarce htmlUnit z ustawionym proxy
	 */
	public void htmlUnitCheck() {
		try {
			WebClient client = new WebClient(BrowserVersion.FIREFOX_45, this.getProxy().getAdres(),
					this.getProxy().getPort());
			client.getOptions().setTimeout(Integer.parseInt(this.getTimeOut().getText()));
			// Wy³¹czenie javaScript
			if (this.jsDisable.isSelected())
				client.getOptions().setJavaScriptEnabled(false);
			else
				client.getOptions().setJavaScriptEnabled(true);
			int retry = 0;
			HtmlPage page = null;
			
			// n-powtórzeñ dozwolonych; zliczanie czasu dla n-powtórzeñ
			long startMs = System.currentTimeMillis();
			do {
				page = client.getPage(this.getUrlToScrape().getText());
			} while (retry < Integer.parseInt(this.getNumberOfRetry().getText())
					&& page.getWebResponse().getStatusCode() != 200);
			long stopMs = System.currentTimeMillis();
			
			if (page.getWebResponse().getStatusCode() == 200) {
				// Stworzenie obiektu klasy Score i wstawienie do get (+ insert
				// w db)
				Score score = new Score();
				score.setMs(toIntExact(stopMs - startMs));
				score.setPotwierdzenie(htmlUnitParse(page));
				score.setProby(retry);
				insertScore(score);
			}
		} catch (Exception e) {
			logger.error("Wyst¹pi³ problem z htmlUnit dla url=" + this.getUrlToScrape().getText());
		}
	}

	public String htmlUnitParse(HtmlPage page) {
		List<HtmlDivision> parsedString = (List<HtmlDivision>) page.getByXPath(this.getxPath().getText());
		if (parsedString.size() > 0) {
			return parsedString.get(0).asText();
		} else
			return "FAIL";
	}

	public void insertScore(Score score) {
		try {
			EntityManager em = this.getEntityManagerFactory().createEntityManager();
			em.getTransaction().begin();
			this.proxy.getScores().add(score);
			em.getTransaction().commit();
			em.close();
		} catch (Exception e) {
			logger.error("Nie mo¿na wstawiæ obiektu Score do bazy danych");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

}
