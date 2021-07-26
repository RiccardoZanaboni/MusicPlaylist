package dao;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import beans.Song;

public class SongDAO {
	private Connection con;
	
	public SongDAO(Connection connection) {
		this.con = connection;
	}
	
	public List<Song> findSongByPlaylistId(int playlistId,int creatorId) throws SQLException{
		boolean test=false;
		List<Song> songs = new ArrayList<Song>();
		String query_test = "SELECT ordering FROM song join association on songid=id where playlistid=? and ordering=0 and creator=? ";
		try (PreparedStatement pstatement = con.prepareStatement(query_test);) {
			pstatement.setInt(1, playlistId);
			pstatement.setInt(2, creatorId);
			try (ResultSet result = pstatement.executeQuery();) {
				 test = result.next();
			}
		}
		String query="";
		if(test) {
			query = "SELECT id, title, image FROM song join association on songid=id where playlistid=? and creator=? order by ordering asc";
		} else {
			query = "SELECT id, title, image FROM song join association on songid=id where playlistid=? and creator=? order by release_date desc";
		}
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, playlistId);
			pstatement.setInt(2, creatorId);
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
					songs.add(song);
				}
			}
		}
		return songs;
	}
	
	public int createSong(String songT,InputStream imageContent, String singer, Date date, String genre, InputStream fileContent, int creatorid ) throws SQLException{
		String query = "INSERT into song (title, image, singer, release_date, musical_genre, file, creator) VALUES (?, ?, ?, ?, ?, ?, ?)";
		int code = 0;
		PreparedStatement pStatement = null;
		try {
			pStatement = con.prepareStatement(query);
			pStatement.setString(1, songT);
			pStatement.setBlob(2, imageContent);
			pStatement.setString(3, singer);
			pStatement.setObject(4, date);
			pStatement.setString(5, genre);
			pStatement.setBlob(6, fileContent);
			pStatement.setInt(7, creatorid);
			code = pStatement.executeUpdate();
		}catch(SQLException e) {
			System.out.println(e);
			throw new SQLException(e);
		}finally {
			try {
				if (pStatement != null) {
					pStatement.close();
				}
			} catch (Exception e1) {

			}
		}
		return code;
	}
	
	
	
	
	public List<Song> findSongByUserId(int creatorid, int playlistid) throws SQLException{
		List<Song> songs = new ArrayList<Song>();
		String query = "SELECT id,title FROM song where creator = ? and id not in (select songid from association where playlistid = ?) ";
		try(PreparedStatement pstatement = con.prepareStatement(query);){
			pstatement.setInt(1, creatorid);
			pstatement.setInt(2, playlistid);
			try(ResultSet result = pstatement.executeQuery();){
				while(result.next()) {
					Song song = new Song();
					song.setTitle(result.getString("title"));
					song.setId(result.getInt("id"));
					songs.add(song);
				}
			}
		}
		return songs;
	}
	
	
	
	public void setPlaylistId(int playlistid, int songid) throws SQLException {
		String query = "INSERT into association (songid,playlistid) values (?,?)";
		PreparedStatement pStatement = null;
		try{
			pStatement = con.prepareStatement(query);
			pStatement.setInt(1, songid);
			pStatement.setInt(2, playlistid);
			pStatement.executeUpdate();
			
		}catch(SQLException e) {
			System.out.println(e);
			throw new SQLException(e);
		}finally {
			try {
				if (pStatement != null) {
					pStatement.close();
				}
			} catch (Exception e1) {

			}
		}
	}
	
	public Song findSongById(int songId,int creatorId) throws SQLException{
		Song song = new Song();
		String query = "SELECT id, title, image, singer, release_date, musical_genre, file FROM song where id = ? and creator= ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, songId);
			pstatement.setInt(2, creatorId);
			Blob image_blob = null;
			Blob file_blob = null;
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					song.setId(result.getInt("id"));
					song.setTitle(result.getString("title"));
					
					image_blob= result.getBlob("image");
					byte[] image_ba = image_blob.getBytes(1, (int) image_blob.length()); 
					byte[] img64 =  Base64.getEncoder().encode(image_ba);
					String image = new String(img64);
					song.setImage(image);
	
					song.setSinger(result.getString("singer"));
					song.setRelease_date(result.getDate("release_date"));
					song.setMusical_genre(result.getString("musical_genre"));
					
					file_blob= result.getBlob("file");
					byte[] file_ba = file_blob.getBytes(1, (int) file_blob.length()); 
					byte[] file64 =  Base64.getEncoder().encode(file_ba);
					String file = new String(file64);
					song.setFile(file);
				}
			}
		}
		return song;
	}
	
	public void setOrder(int[] Ids,int playlistid) throws SQLException {
		con.setAutoCommit(false);
		String query = "UPDATE association SET ordering = ? WHERE songid = ? AND playlistid= ? ";
		PreparedStatement pStatement = null;
		try {
			pStatement = con.prepareStatement(query);
			for(int i=0; i<Ids.length; i++) {
				pStatement.setInt(1, i);
				pStatement.setInt(2, Ids[i]);
				pStatement.setInt(3, playlistid);
				pStatement.executeUpdate();
				con.commit();
			}
		}catch(SQLException e) {
			con.rollback();
			System.out.println(e);
			throw new SQLException(e);
		}finally {
			con.setAutoCommit(true);
			try {
				if (pStatement != null) {
					pStatement.close();
				}
			} catch (Exception e1) {

			}		
		}
		
	}
	
}
