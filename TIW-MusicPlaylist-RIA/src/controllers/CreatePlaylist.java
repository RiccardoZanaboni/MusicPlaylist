package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import beans.User;
import dao.PlaylistDAO;
import utils.ConnectionHandler;


@WebServlet("/CreatePlaylist")
@MultipartConfig
public class CreatePlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    
    public CreatePlaylist() {
        super();
    }
    
    public void init() throws ServletException{ 
    	connection = ConnectionHandler.getConnection(getServletContext());
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession  s = request.getSession();
		User u = (User) s.getAttribute("user");
		String playlist_title = StringEscapeUtils.escapeJava(request.getParameter("title"));
		if (playlist_title == null || playlist_title.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing the name of the playlist");
			return;
		}
		int userid = u.getId();
		Date today = new Date();
        Date date = new java.sql.Date(today.getTime());
		PlaylistDAO pDao = new PlaylistDAO(connection);
		try {
			pDao.createPlaylist(playlist_title, date, userid);
		}catch(SQLException e1) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error in the creation of the playlist in the DB");
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
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
