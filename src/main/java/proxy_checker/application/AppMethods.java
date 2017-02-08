package proxy_checker.application;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import proxy_checker.db.Proxies;

/**
 * Klasa zawieraj¹ca metody do obs³ugi GUI//
 * 
 * @author mariusz
 *
 */
public class AppMethods {
	public static Logger logger = Logger.getLogger(AppMethods.class);

	/**
	 * Metoda zwracaj¹ca tabelê (w JScrollPane!) z wynikami na podstawie zbioru
	 * proxy
	 * 
	 * @param data
	 */
	public static JScrollPane fillTable(List<Proxies> data, List<String> status) {

		if(data.size()!=status.size()){
			logger.info("DATA != STATUS");
			logger.info("DATA="+data.size());
			logger.info("STATUS="+status.size());
		}
		Object[] col = { "id", "data dodania", "adres", "port", "ranking", "status" };
		Object[][] proxies = new Object[data.size()][6];
		try {

			for (int i = 0; i < data.size(); i++) {
				logger.info("i="+i);
				proxies[i][0] = data.get(i).getId();
				proxies[i][1] = data.get(i).getDataDodania();
				proxies[i][2] = data.get(i).getAdres();
				proxies[i][3] = data.get(i).getPort();
				proxies[i][4] = data.get(i).getRank();
				proxies[i][5] = status.get(i);
			}
		} catch (Exception e) {
			logger.error("Blad podczas wype³niania tabeli danymi nowych proxy");
			e.printStackTrace();
		}

		JScrollPane scrollPane = new JScrollPane(new JTable(proxies, col));
		scrollPane.setPreferredSize(new Dimension(600, 800));
		return scrollPane;
	}

	/**
	 * Metoda wczytuj¹ca plik tekstowy z list¹ proxy, wyznaczaj¹ca tylko nowe
	 * adresy proxy, oraz wstawiaj¹ca nowoznalezione do bazy danych
	 * (poprzedzaj¹ce pytanie)
	 * 
	 * @param entityManagerFactory
	 */
	public static JScrollPane selectOpenFile(EntityManagerFactory entityManagerFactory, JScrollPane scrollPane) {
		File file;
		JFileChooser fileChooser = new JFileChooser("C://crawlers//proxies");

		FileNameExtensionFilter filter = new FileNameExtensionFilter("text files", "txt");
		fileChooser.setFileFilter(filter);
		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
			try {
				logger.info("Wczytano plik: " + file.getCanonicalPath().toString());

				AddProxy addProxy = new AddProxy(file.getCanonicalPath().toString(), entityManagerFactory);
				// Wypelnienie tabeli nowymi adresami proxy
				List<String> status = new ArrayList<String>();
				for(int i=0;i<addProxy.getNewProxies().size();i++)
					status.add("DODANO");
				scrollPane = fillTable(addProxy.getNewProxies(), status);
				int insert = JOptionPane.showConfirmDialog(null, "Czy umieœciæ nowe adresy proxy w bazie", "INSERT",
						JOptionPane.YES_NO_OPTION);
				if (insert == JOptionPane.YES_OPTION) {
					try {
						addProxy.insertProxies(entityManagerFactory);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "B³¹d podczas wstawiania nowych adresów proxy do bazy");
						logger.error("B³¹d podczas wstawiania nowych adresów proxy do bazy");
						logger.error(e.getLocalizedMessage());
					}
				} else {
					JOptionPane.showMessageDialog(null, "Nowe adresy nie zosta³y umieszczone w bazie",
							"Pominiêcie insertu", JOptionPane.INFORMATION_MESSAGE);
				}

			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "B³¹d podczas wczytywania pliku z proxy", "B³¹d",
						JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}

		}
		return scrollPane;
	}

	public static List<Proxies> criteriaSelectProxy(EntityManagerFactory entityManagerFactory, JRadioButton all,
			JRadioButton random, JRadioButton recently, JRadioButton grader, JRadioButton less) {
		List<Proxies> selectedProxies = new ArrayList<Proxies>();
		if (all.isSelected()) {
			selectedProxies = AppMethods.loadProxiesFromDatabaseAll(entityManagerFactory);
		} else if (random.isSelected()) {
			selectedProxies = AppMethods.loadProxiesFromDatabaseRandom(entityManagerFactory, 10);
		} else if (recently.isSelected()) {
			selectedProxies = AppMethods.loadProxiesFromDataBaseRecently(entityManagerFactory);
		} else if (grader.isSelected()) {
			selectedProxies = AppMethods.loadProxiesFromDataBaseRank(entityManagerFactory, 0);
		} else if (less.isSelected()) {
			selectedProxies = AppMethods.loadProxiesFromDataBaseRank(entityManagerFactory, 0);
		} else {
			logger.error("Nie zostalo wybrane zande kryterium wyboru!");
		}

		return selectedProxies;
	}

	/**
	 * wczytuje istniejacy zbior proxy tak aby dodac tylko adresy, ktorych nie
	 * ma w bazie
	 * 
	 * @param entityManagerFactory
	 * @return
	 */
	public static List<Proxies> loadProxiesFromDatabaseAll(EntityManagerFactory entityManagerFactory) {
		EntityManager em = entityManagerFactory.createEntityManager();
		if (!em.getTransaction().isActive())
			em.getTransaction().begin();
		TypedQuery<Proxies> query = em.createQuery("SELECT p FROM Proxies p order by id", Proxies.class);
		List<Proxies> set = new ArrayList<Proxies>(new HashSet<Proxies>(query.getResultList()));
		if (em.getTransaction().isActive())
			em.getTransaction().commit();
		em.close();
		return set;
	}

	public static List<Proxies> loadProxiesFromDatabaseRandom(EntityManagerFactory entityManagerFactory,
			int numberOfRecords) {
		EntityManager em = entityManagerFactory.createEntityManager();
		if (!em.getTransaction().isActive())
			em.getTransaction().begin();
		TypedQuery<Proxies> query = em.createQuery("Select p FROM Proxies p order by rand()", Proxies.class)
				.setMaxResults(numberOfRecords);
		List<Proxies> set = new ArrayList<Proxies>(new HashSet<Proxies>(query.getResultList()));
		if (em.getTransaction().isActive())
			em.getTransaction().commit();
		em.close();
		return set;
	}

	public static List<Proxies> loadProxiesFromDataBaseRecently(EntityManagerFactory entityManagerFactory) {
		EntityManager em = entityManagerFactory.createEntityManager();
		if (!em.getTransaction().isActive())
			em.getTransaction().begin();
		TypedQuery<Proxies> query = em.createQuery("SELECT p FROM Proxies p order by dataDodania desc", Proxies.class);
		List<Proxies> set = new ArrayList<Proxies>(new HashSet<Proxies>(query.getResultList()));
		if (em.getTransaction().isActive())
			em.getTransaction().commit();
		em.close();
		return set;
	}

	public static List<Proxies> loadProxiesFromDataBaseRank(EntityManagerFactory entityManagerFactory, double rank) {
		EntityManager em = entityManagerFactory.createEntityManager();
		if (!em.getTransaction().isActive())
			em.getTransaction().begin();
		TypedQuery<Proxies> query = em.createQuery("SELECT p FROM Proxies p WHERE rank <=:rank", Proxies.class);
		query.setParameter("rank", rank);
		List<Proxies> set = new ArrayList<Proxies>(new HashSet<Proxies>(query.getResultList()));
		if (em.getTransaction().isActive())
			em.getTransaction().commit();
		em.close();
		return set;
	}

}
