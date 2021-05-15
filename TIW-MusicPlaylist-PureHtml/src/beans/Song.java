package beans;
import java.io.InputStream;
import java.util.Date;

public class Song{
	private int id;
	private String title;
	private String singer;
	private String musical_genre;
	private Date release_date;
	private int creator;
	private Playlist playlist;
	private String file;  
	private String image;
	
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
	
	public Date getRelease_date() {
		return release_date;
	}
	
	public void setRelease_date(Date release_date) {
		this.release_date = release_date;
	}
	
	public String getSinger() {
		return singer;
	}
	
	public void setSinger(String singer) {
		this.singer = singer;
	}
	
	public String getMusical_genre() {
		return musical_genre;
	}
	
	public void setMusical_genre(String musical_genre) {
		this.musical_genre = musical_genre;
	}
	
	public int getCreator() {
		return creator;
	}
	
	public void setCreator(int creator) {
		this.creator = creator;
	}
	
	public Playlist getPlaylist() {
		return playlist;
	}
	
	public void setPlaylist(Playlist playlist) {
		this.playlist = playlist ;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String encoded) {
		this.file = encoded;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String encoded) {
		this.image = encoded;
	}
	
	
}