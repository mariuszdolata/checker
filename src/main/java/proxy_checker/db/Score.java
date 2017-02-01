package proxy_checker.db;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name="score")
public class Score {

	private Long score_id;
	private Date dataTestu;
	private String potwierdzenie;
	private int ms;
	private int proby;
	private Proxies proxies;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long getScoreId() {
		return score_id;
	}
	public void setScoreId(Long id) {
		this.score_id = id;
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
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="proxy_id", nullable=false)
	public Proxies getProxies() {
		return proxies;
	}
	public void setProxies(Proxies proxies) {
		this.proxies = proxies;
	}
	public Score() {
		super();
	}
	@Override
	public String toString() {
		return "Score [id=" + score_id + ", dataTestu=" + dataTestu + ", potwierdzenie=" + potwierdzenie + ", ms=" + ms
				+ ", proby=" + proby + ", proxies=" + proxies + "]";
	}
	
	
}
