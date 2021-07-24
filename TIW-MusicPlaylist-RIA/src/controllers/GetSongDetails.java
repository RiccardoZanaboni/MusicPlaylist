package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import beans.Playlist;
import beans.Song;
import dao.PlaylistDAO;
import dao.SongDAO;

@WebServlet("/GetSongDetails")
public class GetSongDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetSongDetails() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void init() throws ServletException{
    	ServletContext servletContext = getServletContext();
    	try {
    		ServletContext context = getServletContext();
    		String driver = context.getInitParameter("dbDriver");
    		String url = context.getInitParameter("dbUrl");
    		String user = context.getInitParameter("dbUser");
    		String password = context.getInitParameter("dbPassword");
    		Class.forName(driver);
    		connection = DriverManager.getConnection(url, user, password);
    	}catch(SQLException e) {
    		throw new UnavailableException("Couldn't get the Database connection");
    	}catch(ClassNotFoundException e) {
    		throw new UnavailableException("Error in the load of Database driver");
    	}
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sId = request.getParameter("songId");
		if(sId == null || sId.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing song id value");
		}
		Integer songId = null;
		try {
			songId = Integer.parseInt(sId);
		} catch (NumberFormatException | NullPointerException e) {
			// only for debugging e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect param values");
			return;
		}
		SongDAO sDao= new SongDAO(connection);
		Song song = null;
		try {
			song = sDao.findSongById(songId);
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Failure in the song's  extraction");
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
