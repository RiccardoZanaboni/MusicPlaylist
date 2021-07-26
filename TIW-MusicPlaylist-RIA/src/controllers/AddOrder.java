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


@WebServlet("/AddOrder")
public class AddOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    
    public AddOrder() {
        super();
        
    }
    
    public void init() throws ServletException{
    	connection = ConnectionHandler.getConnection(getServletContext());
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}


	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String arrayId = StringEscapeUtils.escapeJava(request.getParameter("arrayId"));
		String[] Ids = arrayId.split(",");
		if (Ids == null || Ids.length==0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("No songs to order!");
			return;
		}
		int[] songIds = new int [Ids.length];
	      for(int i=0; i<Ids.length; i++) {
	    	  songIds[i] = Integer.parseInt(Ids[i]);
	      }
		String pId = StringEscapeUtils.escapeJava(request.getParameter("playlistId"));
		int playlistId = -1;
		try {
			playlistId = Integer.parseInt(pId);
		
		}catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Bad number format for playlist id");
			return;
		}
		SongDAO sDao = new SongDAO(connection);
		try {
			sDao.setOrder(songIds,playlistId);
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Cannot set order of songs");
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
	
	}

}
