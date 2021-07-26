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
import utils.ConnectionHandler;

@WebServlet("/GetSongsUser")
public class GetSongsUser extends HttpServlet{
    private static final long serialVersionUID = 1L;
	private Connection connection;       
   
    public GetSongsUser() {
        super();
    }

    public void init() throws ServletException{
    	connection = ConnectionHandler.getConnection(getServletContext());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	Integer playlistId = null;
		try {
			playlistId = Integer.parseInt(request.getParameter("playlistid"));
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect playlistId value");
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
			response.getWriter().println("Not possible to recover songs");
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
