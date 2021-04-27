package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import beans.Playlist;
import beans.User;

public class PlaylistDAO{
	private Connection con;
	
	public PlaylistDAO(Connection connection) {
		this.con = connection;
	}
	
	
	public List<Playlist> findPlaylistByUser(int creatorid) throws SQLException{
		List<Playlist> playlists = new ArrayList<Playlist>();
		String query = "SELECT id, title, creation_date FROM playlist where creator = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, creatorid);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Playlist playlist = new Playlist();
					playlist.setId(result.getInt("id"));
					playlist.setTitle(result.getString("title"));
					playlist.setCreation_date(result.getDate("creation_date"));;
					playlists.add(playlist);
				}
			}
		}
		return playlists;
	}
}