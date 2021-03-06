package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import beans.Song;
import beans.User;
import dao.SongDAO;
import utils.ConnectionHandler;

@WebServlet("/GetSongDetails")
public class GetSongDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetSongDetails() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void init() throws ServletException{
		connection = ConnectionHandler.getConnection(getServletContext());
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession s = request.getSession();
		User u = (User)s.getAttribute("user");
		String sId = request.getParameter("songId");
		if(sId == null || sId.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing song id value");
			return;
		}
		Integer songId = null;
		try {
			songId = Integer.parseInt(sId);
		} catch (NumberFormatException | NullPointerException e) {
			// only for debugging e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect songId value");
			return;
		}
		SongDAO sDao= new SongDAO(connection);
		Song song = null;
		try {
			song = sDao.findSongById(songId,u.getId());
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Failure in the song's extraction or you don't have a song with that id");
			return;
		}
		if(song.getId()==0) { // songId starts from 1 in DB, 0 is the same of null
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Song id doesn't exist in the db");
			return;
		}
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(song);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
			}
	}
}
