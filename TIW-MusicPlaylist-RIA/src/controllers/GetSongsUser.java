package controllers;

import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import beans.Song;
import beans.User;
import dao.SongDAO;

@WebServlet("/GetSongsUser")
public class GetSongsUser extends HttpServlet{
    private static final long serialVersionUID = 1L;
	private Connection connection;       
   
    public GetSongsUser() {
        super();
    }

    public void init() throws ServletException{
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	Integer playlistId = null;
		try {
			playlistId = Integer.parseInt(request.getParameter("playlistid"));
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect param values");
			return;
		}
    	
		HttpSession s = request.getSession();
		User u = (User)s.getAttribute("user");
		SongDAO sDao = new SongDAO(connection);
		List<Song> songs = null;
		try {
			songs = sDao.findSongByUserId(u.getId(), playlistId);
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover ss");
			return;
		}
		
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(songs);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	
	}

}
