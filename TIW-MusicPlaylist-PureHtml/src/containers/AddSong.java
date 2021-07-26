package containers;

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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import dao.SongDAO;


@WebServlet("/AddSong")
public class AddSong extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
       
    
    public AddSong() {
        super();
        
    }
    
    public void init() throws ServletException{
    	ServletContext servletContext = getServletContext();
    	ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
    	templateResolver.setTemplateMode(TemplateMode.HTML);
    	this.templateEngine = new TemplateEngine();
    	this.templateEngine.setTemplateResolver(templateResolver);
    	templateResolver.setSuffix(".html");
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
		doPost(request, response);
	}


	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pId = request.getParameter("playlistId");
		if (pId == null || pId.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing playlistId parameter");
			return;
		}
		
		int playlistId = -1;
		try {
			playlistId = Integer.parseInt(pId);
		
		}catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad number format for playlist id");
			return;
		}
		
		String sId = request.getParameter("songId");
		
		if (sId == null || sId.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing song id parameter");
			return;
		}
		
		int songId = -1;
		
		try {
			songId = Integer.parseInt(sId);
			
		}catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad number format for song id");
			return;
		}
		
		SongDAO sDao = new SongDAO(connection);
		try {
			sDao.setPlaylistId(playlistId, songId);
			
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Song id or playlist id don't exist in their DB tables");
			return;
		}
		
		String path = getServletContext().getContextPath() + "/GetPlaylist?playlistId="+playlistId+"&songId=0";	//Al momento dopo aver aggiunto una canzone ritorno alla prima pagina
		response.sendRedirect(path);
	
	}

}
