package containers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.User;
import dao.PlaylistDAO;
import utils.ConnectionHandler;


@WebServlet("/CreatePlaylist")
public class CreatePlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
       
    
    public CreatePlaylist() {
        super();
    }
    
    public void init() throws ServletException{
    	ServletContext servletContext = getServletContext();
    	ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
    	templateResolver.setTemplateMode(TemplateMode.HTML);
    	this.templateEngine = new TemplateEngine();
    	this.templateEngine.setTemplateResolver(templateResolver);
    	templateResolver.setSuffix(".html");
    	connection = ConnectionHandler.getConnection(getServletContext());
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession  s = request.getSession();
		User u = (User) s.getAttribute("user");
		String playlist_title = request.getParameter("playlist_title");
		if (playlist_title == null || playlist_title.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing the name of the playlist");
			return;
		}
		int userid = u.getId();
		Date today = new Date();
        Date date = new java.sql.Date(today.getTime());
		PlaylistDAO pDao = new PlaylistDAO(connection);
		try {
			pDao.createPlaylist(playlist_title, date, userid);
		}catch(SQLException e1) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Error in the creation of the playlist in the DB");
			return;
		}
		String path = getServletContext().getContextPath() + "/GoToHomePage"; 
		response.sendRedirect(path);
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
