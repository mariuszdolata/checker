package proxy_checker.application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.persistence.EntityManagerFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

import proxy_checker.db.Proxies;

public class ProxyCheckerFrame {
	public Logger logger = Logger.getLogger(ProxyCheckerFrame.class);
	private EntityManagerFactory entityManagerFactory;
	private AddProxy addProxy;

	private File openSelectedFile;
	private JFrame mainFrame;
	private int x, y, width, height;
	private String frameName;
	private JMenuBar menuBar = new JMenuBar();
	private JButton addButton, testButton, statsButton, stopButton, selectButton;
	private int widthButton = 120, heightButton = 50;
	private JTabbedPane tabbedPane;
	private JPanel proxyListPanel, optionsPanel, resultsPanel;
	private JTable proxyListTable;
	private JScrollPane scrollTable;
	private ButtonGroup browsers, methodLoading;
	private JRadioButton htmlUnitBrowser, seleniumBrowser;
	private JRadioButton allProxies, randomProxies, recentlyProxies, graderProxies, lessProxies;
	private JLabel chooseBrowser, chooseMethod;
	private JTextField urlToScrapeField, xPathField, successField, failField, threadsField, timeOutField, retryField;
	private JCheckBox nonStopCheck, jsCheck, imageCheck;

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return entityManagerFactory;
	}

	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public AddProxy getAddProxy() {
		return addProxy;
	}

	public void setAddProxy(AddProxy addProxy) {
		this.addProxy = addProxy;
	}

	public File getOpenSelectedFile() {
		return openSelectedFile;
	}

	public void setOpenSelectedFile(File openSelectedFile) {
		this.openSelectedFile = openSelectedFile;
	}

	public JFrame getMainFrame() {
		return mainFrame;
	}

	public void setMainFrame(JFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getFrameName() {
		return frameName;
	}

	public void setFrameName(String frameName) {
		this.frameName = frameName;
	}

	public JMenuBar getMenuBar() {
		return menuBar;
	}

	public void setMenuBar(JMenuBar menuBar) {
		this.menuBar = menuBar;
	}

	public JButton getAddButton() {
		return addButton;
	}

	public void setAddButton(JButton addButton) {
		this.addButton = addButton;
	}

	public JButton getTestButton() {
		return testButton;
	}

	public void setTestButton(JButton testButton) {
		this.testButton = testButton;
	}

	public JButton getStatsButton() {
		return statsButton;
	}

	public void setStatsButton(JButton statsButton) {
		this.statsButton = statsButton;
	}

	public JButton getStopButton() {
		return stopButton;
	}

	public void setStopButton(JButton stopButton) {
		this.stopButton = stopButton;
	}

	public JButton getSelectButton() {
		return selectButton;
	}

	public void setSelectButton(JButton selectButton) {
		this.selectButton = selectButton;
	}

	public int getWidthButton() {
		return widthButton;
	}

	public void setWidthButton(int widthButton) {
		this.widthButton = widthButton;
	}

	public int getHeightButton() {
		return heightButton;
	}

	public void setHeightButton(int heightButton) {
		this.heightButton = heightButton;
	}

	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	public void setTabbedPane(JTabbedPane tabbedPane) {
		this.tabbedPane = tabbedPane;
	}

	public JPanel getProxyListPanel() {
		return proxyListPanel;
	}

	public void setProxyListPanel(JPanel proxyListPanel) {
		this.proxyListPanel = proxyListPanel;
	}

	public JPanel getOptionsPanel() {
		return optionsPanel;
	}

	public void setOptionsPanel(JPanel optionsPanel) {
		this.optionsPanel = optionsPanel;
	}

	public JPanel getResultsPanel() {
		return resultsPanel;
	}

	public void setResultsPanel(JPanel resultsPanel) {
		this.resultsPanel = resultsPanel;
	}

	public JTable getProxyListTable() {
		return proxyListTable;
	}

	public void setProxyListTable(JTable proxyListTable) {
		this.proxyListTable = proxyListTable;
	}

	public JScrollPane getScrollTable() {
		return scrollTable;
	}

	public void setScrollTable(JScrollPane scrollTable) {
		this.scrollTable = scrollTable;
	}

	public ButtonGroup getBrowsers() {
		return browsers;
	}

	public void setBrowsers(ButtonGroup browsers) {
		this.browsers = browsers;
	}

	public ButtonGroup getMethodLoading() {
		return methodLoading;
	}

	public void setMethodLoading(ButtonGroup methodLoading) {
		this.methodLoading = methodLoading;
	}

	public JRadioButton getHtmlUnitBrowser() {
		return htmlUnitBrowser;
	}

	public void setHtmlUnitBrowser(JRadioButton htmlUnitBrowser) {
		this.htmlUnitBrowser = htmlUnitBrowser;
	}

	public JRadioButton getSeleniumBrowser() {
		return seleniumBrowser;
	}

	public void setSeleniumBrowser(JRadioButton seleniumBrowser) {
		this.seleniumBrowser = seleniumBrowser;
	}

	public JRadioButton getAllProxies() {
		return allProxies;
	}

	public void setAllProxies(JRadioButton allProxies) {
		this.allProxies = allProxies;
	}

	public JRadioButton getRandomProxies() {
		return randomProxies;
	}

	public void setRandomProxies(JRadioButton randomProxies) {
		this.randomProxies = randomProxies;
	}

	public JRadioButton getRecentlyProxies() {
		return recentlyProxies;
	}

	public void setRecentlyProxies(JRadioButton recentlyProxies) {
		this.recentlyProxies = recentlyProxies;
	}

	public JRadioButton getGraderProxies() {
		return graderProxies;
	}

	public void setGraderProxies(JRadioButton graderProxies) {
		this.graderProxies = graderProxies;
	}

	public JRadioButton getLessProxies() {
		return lessProxies;
	}

	public void setLessProxies(JRadioButton lessProxies) {
		this.lessProxies = lessProxies;
	}

	public JLabel getChooseBrowser() {
		return chooseBrowser;
	}

	public void setChooseBrowser(JLabel chooseBrowser) {
		this.chooseBrowser = chooseBrowser;
	}

	public JLabel getChooseMethod() {
		return chooseMethod;
	}

	public void setChooseMethod(JLabel chooseMethod) {
		this.chooseMethod = chooseMethod;
	}

	public JTextField getUrlToScrapeField() {
		return urlToScrapeField;
	}

	public void setUrlToScrapeField(JTextField urlToScrapeField) {
		this.urlToScrapeField = urlToScrapeField;
	}

	public JTextField getxPathField() {
		return xPathField;
	}

	public void setxPathField(JTextField xPathField) {
		this.xPathField = xPathField;
	}

	public JTextField getSuccessField() {
		return successField;
	}

	public void setSuccessField(JTextField successField) {
		this.successField = successField;
	}

	public JTextField getFailField() {
		return failField;
	}

	public void setFailField(JTextField failField) {
		this.failField = failField;
	}

	public JTextField getThreadsField() {
		return threadsField;
	}

	public void setThreadsField(JTextField threadsField) {
		this.threadsField = threadsField;
	}

	public JTextField getTimeOutField() {
		return timeOutField;
	}

	public void setTimeOutField(JTextField timeOutField) {
		this.timeOutField = timeOutField;
	}

	public JTextField getRetryField() {
		return retryField;
	}

	public void setRetryField(JTextField retryField) {
		this.retryField = retryField;
	}

	public JCheckBox getNonStopCheck() {
		return nonStopCheck;
	}

	public void setNonStopCheck(JCheckBox nonStopCheck) {
		this.nonStopCheck = nonStopCheck;
	}

	public JCheckBox getJsCheck() {
		return jsCheck;
	}

	public void setJsCheck(JCheckBox jsCheck) {
		this.jsCheck = jsCheck;
	}

	public JCheckBox getImageCheck() {
		return imageCheck;
	}

	public void setImageCheck(JCheckBox imageCheck) {
		this.imageCheck = imageCheck;
	}

	public ProxyCheckerFrame(int x, int y, int width, int height, String frameName,
			EntityManagerFactory entityManagerFactory) {
		super();
		this.entityManagerFactory = entityManagerFactory;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.frameName = frameName;
		this.mainFrame = new JFrame(frameName);
		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mainFrame.setBounds(x, y, width, height);
		createMenuBar();
		createButtons();
		createTabbedPane();
		mainFrame.add(this.getMenuBar(), BorderLayout.NORTH);
		mainFrame.setVisible(true);
	}

	/**
	 * metoda tworzaca menuBar - miejsce zarezerwowane na przyszly rozwoj
	 * programu
	 */
	private void createMenuBar() {
		this.setMenuBar(new JMenuBar());
		this.menuBar.setBounds(this.getX(), this.getY(), 150, 200);
		JMenu menu = new JMenu("Plik");
		menu.setBounds(this.getX(), this.getY(), 150, 200);
		JMenuItem otworzItem = new JMenuItem("Otwórz");
		menu.add(otworzItem);
		this.getMenuBar().add(menu);
	}

	private void createButtons() {
		this.setAddButton(new JButton("Dodaj proxy"));
		this.getAddButton().setBounds(10, 30, this.getWidthButton(), this.getHeightButton());
		this.getAddButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectOpenFile();

			}
		});
		this.getMainFrame().add(this.getAddButton(), BorderLayout.NORTH);

		this.setSelectButton(new JButton("Pobierz proxy"));
		this.getSelectButton().setBounds(150, 30, this.getWidthButton(), this.getHeightButton());
		this.getSelectButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectButtonAction();

			}
		});
		this.getMainFrame().add(this.getSelectButton(), BorderLayout.NORTH);

		this.setTestButton(new JButton("Testuj proxy"));
		this.getTestButton().setBounds(290, 30, this.getWidthButton(), this.getHeightButton());
		this.getTestButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				testButtonAction();

			}
		});
		this.getMainFrame().add(this.getTestButton(), BorderLayout.NORTH);

		this.setStopButton(new JButton("Zatrzymaj"));
		this.getStopButton().setBounds(430, 30, this.getWidthButton(), this.getHeightButton());
		this.setStatsButton(new JButton("Statystyka"));
		this.getMainFrame().add(this.getStopButton(), BorderLayout.NORTH);
		this.getStopButton().setEnabled(false);
		this.getStatsButton().setBounds(570, 30, this.getWidthButton(), this.getHeightButton());
		this.getStatsButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				statsButtonAction();

			}
		});
		this.getMainFrame().add(this.getStatsButton(), BorderLayout.NORTH);
	}

	private void createTabbedPane() {
		this.setTabbedPane(new JTabbedPane());
		this.setProxyListPanel(new JPanel());
		this.setOptionsPanel(new JPanel());
		createOptionTable();
		this.setResultsPanel(new JPanel());

		this.getTabbedPane().addTab("Lista proxy", this.getProxyListPanel());
		this.getTabbedPane().addTab("opcje", this.getOptionsPanel());
		this.getTabbedPane().addTab("rezultaty", this.getResultsPanel());
		this.getTabbedPane().setBounds(10, 100, this.getWidth() - 40, this.getHeight() - 150);

		this.getMainFrame().add(this.getTabbedPane(), BorderLayout.NORTH);
	}

	/**
	 * Metoda tworzaca zakladke opcji
	 */
	private void createOptionTable() {
		int startFirst = 20, startSecond = 250, length = 130, height = 30;

		this.setChooseBrowser(new JLabel("Wybierz przegl¹darkê do testowania", SwingConstants.RIGHT));
		this.getChooseBrowser().setBounds(startFirst, 0, 220, height);
		this.getOptionsPanel().add(this.getChooseBrowser(), BorderLayout.WEST);
		this.setHtmlUnitBrowser(new JRadioButton("HtmlUnit", true));
		this.setSeleniumBrowser(new JRadioButton("Selenium", false));
		this.setBrowsers(new ButtonGroup());
		this.getBrowsers().add(this.getHtmlUnitBrowser());
		this.getSeleniumBrowser().setBounds(startSecond, 0, length, height);
		this.getHtmlUnitBrowser().setBounds(startSecond, height, length, height);
		JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
		separator.setBounds(0, 60, 6000, 1);
		JSeparator separator3 = new JSeparator(SwingConstants.HORIZONTAL);
		separator3.setBounds(0, 220, 6000, 1);
		JSeparator separator2 = new JSeparator(SwingConstants.VERTICAL);
		separator2.setBounds(400, 0, 4, 1000);
		this.getOptionsPanel().add(separator);
		this.getOptionsPanel().add(separator2);
		this.getOptionsPanel().add(separator3);
		this.getBrowsers().add(this.getSeleniumBrowser());
		this.getOptionsPanel().add(this.getSeleniumBrowser());
		this.getOptionsPanel().add(this.getHtmlUnitBrowser(), BorderLayout.NORTH);
		this.getOptionsPanel().setLayout(null);
		// this.getOptionsPanel().setLayout(new FlowLayout(FlowLayout.LEFT));

		this.setAllProxies(new JRadioButton("wszystkie", true));
		this.getAllProxies().setBounds(startSecond, height * 2 + 10, length, height);
		this.setRandomProxies(new JRadioButton("losowe", false));
		this.getRandomProxies().setBounds(startSecond, height * 3 + 10, length, height);
		this.setRecentlyProxies(new JRadioButton("dzisiaj dodane", false));
		this.getRecentlyProxies().setBounds(startSecond, height * 4 + 10, length, height);
		this.setGraderProxies(new JRadioButton("lepsze ni¿", false));
		this.getGraderProxies().setBounds(startSecond, height * 5 + 10, length, height);
		this.setLessProxies(new JRadioButton("gorsze ni¿", false));
		this.getLessProxies().setBounds(startSecond, height * 6 + 10, length, height);
		this.setMethodLoading(new ButtonGroup());
		this.getMethodLoading().add(this.getAllProxies());
		this.getMethodLoading().add(this.getRandomProxies());
		this.getMethodLoading().add(this.getRecentlyProxies());
		this.getMethodLoading().add(this.getGraderProxies());
		this.getMethodLoading().add(this.getLessProxies());
		this.setChooseMethod(new JLabel("Które proxy pobraæ?", SwingConstants.RIGHT));
		this.getChooseMethod().setBounds(20, 60 + 10, 220, 30);
		this.getOptionsPanel().add(this.getChooseMethod());
		this.getOptionsPanel().add(this.getChooseMethod());
		this.getOptionsPanel().add(this.getAllProxies());
		this.getOptionsPanel().add(this.getRecentlyProxies());
		this.getOptionsPanel().add(this.getRandomProxies());
		this.getOptionsPanel().add(this.getGraderProxies());
		this.getOptionsPanel().add(this.getLessProxies());
		JLabel urlToScrapeLabel = new JLabel("URL do testowania proxy", SwingConstants.CENTER);
		urlToScrapeLabel.setBounds(10, 230, 380, 30);
		this.setUrlToScrapeField(new JTextField("http://www.moje-ip.eu"));
		this.getUrlToScrapeField().setBounds(10, 260, 380, 30);
		this.getOptionsPanel().add(this.getUrlToScrapeField());
		this.getOptionsPanel().add(urlToScrapeLabel);
		JLabel xPathLabel = new JLabel("XPath dla IP", SwingConstants.CENTER);
		xPathLabel.setBounds(10, 290, 380, 30);
		this.getOptionsPanel().add(xPathLabel);
		this.setxPathField(new JTextField("xpath"));
		this.getxPathField().setBounds(10, 320, 380, 30);
		this.getOptionsPanel().add(this.getxPathField());
		JLabel testowanieLbl = new JLabel("Testowanie non-stop", SwingConstants.RIGHT);
		testowanieLbl.setBounds(410, 0, 230, 30);
		this.getOptionsPanel().add(testowanieLbl);
		JLabel liczbaWatkowLbl = new JLabel("Liczba w¹tków", SwingConstants.RIGHT);
		liczbaWatkowLbl.setBounds(410, 30, 230, 30);
		this.getOptionsPanel().add(liczbaWatkowLbl);

		JLabel timeOutLbl = new JLabel("TimeOut [ms]", SwingConstants.RIGHT);
		timeOutLbl.setBounds(410, 70, 230, 30);
		this.getOptionsPanel().add(timeOutLbl);

		JLabel jsLbl = new JLabel("Wy³¹czenie javaScript", SwingConstants.RIGHT);
		jsLbl.setBounds(410, 100, 230, 30);
		this.getOptionsPanel().add(jsLbl);

		JLabel imageLbl = new JLabel("Wy³¹czenie obrazków", SwingConstants.RIGHT);
		imageLbl.setBounds(410, 130, 230, 30);
		this.getOptionsPanel().add(imageLbl);

		JLabel retryLbl = new JLabel("Liczba powtórzeñ", SwingConstants.RIGHT);
		retryLbl.setBounds(410, 160, 230, 30);
		this.getOptionsPanel().add(retryLbl);

		JLabel successLbl = new JLabel("Sukces (String)", SwingConstants.RIGHT);
		successLbl.setBounds(410, 230, 230, 30);
		this.getOptionsPanel().add(successLbl);

		this.setSuccessField(new JTextField("Podaj ci¹g znaków.", SwingConstants.CENTER));
		this.getSuccessField().setBounds(410, 260, 460, 30);
		this.getOptionsPanel().add(this.getSuccessField());

		JLabel failLbl = new JLabel("Pora¿ka (String)", SwingConstants.RIGHT);
		failLbl.setBounds(410, 290, 230, 30);
		this.getOptionsPanel().add(failLbl);

		this.setFailField(new JTextField("Podaj ci¹g znaków.", SwingConstants.CENTER));
		this.getFailField().setBounds(410, 320, 460, 30);
		this.getOptionsPanel().add(this.getFailField());

		this.setNonStopCheck(new JCheckBox());
		this.getNonStopCheck().setBounds(660, 0, 30, 30);
		this.getOptionsPanel().add(this.getNonStopCheck());

		this.setThreadsField(new JTextField("10"));
		this.getThreadsField().setBounds(660, 30, 40, 25);
		this.getOptionsPanel().add(this.getThreadsField());

		this.setTimeOutField(new JTextField("30000"));
		this.getTimeOutField().setBounds(660, 70, 40, 25);
		this.getOptionsPanel().add(this.getTimeOutField());

		this.setJsCheck(new JCheckBox());
		this.getJsCheck().setBounds(660, 100, 30, 30);
		this.getOptionsPanel().add(this.getJsCheck());

		this.setImageCheck(new JCheckBox());
		this.getImageCheck().setBounds(660, 130, 30, 30);
		this.getOptionsPanel().add(this.getImageCheck());

		this.setRetryField(new JTextField("3"));
		this.getRetryField().setBounds(660, 160, 40, 25);
		this.getOptionsPanel().add(this.getRetryField());
	}

	/**
	 * metoda otwierajaca plik z lista proxy do dodania
	 */
	public void selectOpenFile() {
		JFileChooser fileChooser = new JFileChooser("C://crawlers//proxies");

		FileNameExtensionFilter filter = new FileNameExtensionFilter("text files", "txt");
		fileChooser.setFileFilter(filter);
		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			this.setOpenSelectedFile(fileChooser.getSelectedFile());
			try {
				logger.info("Wczytano plik: " + openSelectedFile.getCanonicalPath().toString());
				this.setAddProxy(
						new AddProxy(openSelectedFile.getCanonicalPath().toString(), this.getEntityManagerFactory()));
				// Wypelnienie tabeli nowymi adresami proxy
				fillTable();
				int insert = JOptionPane.showConfirmDialog(null, "Czy umieœciæ nowe adresy proxy w bazie", "INSERT",
						JOptionPane.YES_NO_OPTION);
				if (insert == JOptionPane.YES_OPTION) {
					try {
						this.getAddProxy().insertProxies(this.getEntityManagerFactory());
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
	}

	public void fillTable() {
		Object[] col = { "id", "data dodania", "adres", "port", "ranking" };
		Object[][] proxies = new Object[this.getAddProxy().getNewProxies().size()][5];
		Iterator<Proxies> iterateNewProxies = this.getAddProxy().getNewProxies().iterator();
		int iter = 0;
		while (iterateNewProxies.hasNext()) {
			Proxies proxy = iterateNewProxies.next();
			try {
				proxies[iter][0] = proxy.getId();
				proxies[iter][1] = proxy.getDataDodania();
				proxies[iter][2] = proxy.getAdres();
				proxies[iter][3] = proxy.getPort();
				proxies[iter][4] = proxy.getRank();
			} catch (Exception e) {
				logger.error("Blad podczas wype³niania tabeli danymi nowych proxy");
			}
			iter++;
		}

		this.setProxyListTable(new JTable(proxies, col));
		this.setScrollTable(new JScrollPane(this.getProxyListTable()));
		this.getScrollTable().setPreferredSize(new Dimension(500, 400));
		this.getProxyListPanel().add(this.getScrollTable());
		this.getMainFrame().revalidate();

	}

	private void selectButtonAction() {
		logger.info("SELECT button");
	}

	private void testButtonAction() {
		logger.info("TEST button");
	}

	private void statsButtonAction() {
		logger.info("STATS button");
	}

}
