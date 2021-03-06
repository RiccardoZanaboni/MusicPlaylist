package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import dao.SongDAO;
import utils.ConnectionHandler;


@WebServlet("/AddSong")
public class AddSong extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    
    public AddSong() {
        super();
        
    }
    
    public void init() throws ServletException{ 
    	connection = ConnectionHandler.getConnection(getServletContext());
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}


	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pId = request.getParameter("playlistid");
		if (pId == null || pId.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing playlistId parameter");
			return;
		}
		
		int playlistId = -1;
		try {
			playlistId = Integer.parseInt(pId);
		}catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad number format for playlist id");
			return;
		}
		
		String sId = StringEscapeUtils.escapeJava(request.getParameter("songid"));
		if (sId == null || sId.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing song id parameter");
			return;
		}
		int songId = -1;
		try {
			songId = Integer.parseInt(sId);
			
		}catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad number format for song id");
			return;
		}
		
		SongDAO sDao = new SongDAO(connection);
		try {
			sDao.setPlaylistId(playlistId, songId);
			
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Song id or playlist id don't exist in their DB tables or the selected song is already in the playlist");
			return;
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
	}

}
