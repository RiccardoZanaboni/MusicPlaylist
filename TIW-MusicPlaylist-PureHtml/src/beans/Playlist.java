package beans;
import java.util.Date;

public class Playlist{
	private int id;
	private String title;
	private Date creation_date;
	private User creator;
	
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public Date getCreation_date() {
		return creation_date;
	}
	
	public void setCreation_date(Date creation_date) {
		this.creation_date = creation_date;
	}
	
	public User getCreator() {
		return creator;
	}
	
	public void setCreator(User creator) {
		this.creator = creator;
	}
	
	
}