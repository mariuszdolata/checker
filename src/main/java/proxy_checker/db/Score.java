package proxy_checker.db;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.UpdateTimestamp;
//
@Entity
@Table(name="score")
public class Score {

	private Long score_id;
	private Date dataTestu;
	private String potwierdzenie;
	private int ms;
	private int proby;
	private double wynik;
	private int threadId;
	private Set<Proxies> proxies = new HashSet<Proxies>();
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	public Long getScore_id() {
		return score_id;
	}
	public void setScore_id(Long score_id) {
		this.score_id = score_id;
	}
	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataTestu() {
		return dataTestu;
	}
	
	public void setDataTestu(Date dataTestu) {
		this.dataTestu = dataTestu;
	}
	@Column(columnDefinition="mediumtext")
	public String getPotwierdzenie() {
		return potwierdzenie;
	}
	public void setPotwierdzenie(String potwierdzenie) {
		this.potwierdzenie = potwierdzenie;
	}
	public int getMs() {
		return ms;
	}
	public void setMs(int ms) {
		this.ms = ms;
	}
	public int getProby() {
		return proby;
	}
	public void setProby(int proby) {
		this.proby = proby;
	}
	
		@ManyToMany(fetch=FetchType.LAZY, mappedBy="scores")
	public Set<Proxies> getProxies() {
		return proxies;
	}
	public void setProxies(Set<Proxies> proxies) {
		this.proxies = proxies;
	}
	@Column(precision=2)
	public double getWynik() {
		return wynik;
	}
	public void setWynik(double wynik) {
		this.wynik = wynik;
	}
	
	public int getThreadId() {
		return threadId;
	}
	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}
	public Score() {
		super();
	}
	@Override
	public String toString() {
		return "Score [score_id=" + score_id + ", dataTestu=" + dataTestu + ", potwierdzenie=" + potwierdzenie + ", ms="
				+ ms + ", proby=" + proby + ", wynik=" + wynik + ", threadId=" + threadId + ", proxies=" + proxies
				+ "]";
	}
	
	
	
	
}
