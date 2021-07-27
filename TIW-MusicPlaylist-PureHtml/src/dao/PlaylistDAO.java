package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import beans.Playlist;
import beans.User;
import java.time.LocalDate;

public class PlaylistDAO{
	private Connection con;
	
	public PlaylistDAO(Connection connection) {
		this.con = connection;
	}
	
	
	//Metodo per il recupero di tutte le playlist di un determinato utente dal db
	public List<Playlist> findPlaylistByUser(int creatorid) throws SQLException{
		List<Playlist> playlists = new ArrayList<Playlist>();
		String query = "SELECT id, title, creation_date FROM playlist where creator = ? order by creation_date desc";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, creatorid);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Playlist playlist = new Playlist();
					playlist.setId(result.getInt("id"));
					playlist.setTitle(result.getString("title"));
					playlist.setCreation_date(result.getDate("creation_date"));
					playlists.add(playlist);
				}
			}
		}
		return playlists;
	}
	
	public Playlist findPlaylistById(int playlistId, int creatorId) throws SQLException{
		Playlist playlist = new Playlist();
		String query = "SELECT title, creation_date FROM playlist where id = ? and creator= ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, playlistId);
			pstatement.setInt(2, creatorId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					playlist.setId(playlistId);
					playlist.setTitle(result.getString("title"));
					playlist.setCreation_date(result.getDate("creation_date"));
				}
			}
		}
		return playlist;
	}
	
	
	//Metodo utilizzato per inserire una nuova playlist creata dall utente con la form della Home.html nel db
	public int createPlaylist(String title, Date date, int creatorid) throws SQLException {
		String query = "INSERT into playlist (title, creation_date, creator) VALUES (?, ?, ?)";
		int code = 0;
		PreparedStatement pStatement = null;
		try {
			pStatement = con.prepareStatement(query);
			pStatement.setString(1, title);
			pStatement.setObject(2, date);
			pStatement.setInt(3, creatorid);
			code = pStatement.executeUpdate();
		}catch(SQLException e) {
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
	
}