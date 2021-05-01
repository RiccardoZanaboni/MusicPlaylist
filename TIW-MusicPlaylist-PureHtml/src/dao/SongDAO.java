package dao;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import beans.Song;

public class SongDAO {
	private Connection con;
	
	public SongDAO(Connection connection) {
		this.con = connection;
	}
	
	public List<Song> findSongById(int playlistId) throws SQLException{
		List<Song> songs = new ArrayList<Song>();
		String query = "SELECT id, title, image, singer, release_date, musical_genre, file FROM song where playlist = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, playlistId);
			Blob image_blob = null;
			Blob file_blob = null;
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Song song = new Song();
					song.setId(result.getInt("id"));
					song.setTitle(result.getString("title"));
					
					image_blob= result.getBlob("image");
					byte[] ba = image_blob.getBytes(1, (int) image_blob.length()); 
					byte[] img64 =  Base64.getEncoder().encode(ba);
					String image = new String(img64);
					song.setImage(image);
	
					song.setSinger(result.getString("singer"));
					song.setRelease_date(result.getDate("release_date"));
					song.setMusical_genre(result.getString("musical_genre"));
					
					file_blob= result.getBlob("file");
					InputStream file= file_blob.getBinaryStream();
					song.setFile(file);
					songs.add(song);
				}
			}
		}
		return songs;
	}
}
