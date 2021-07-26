package containers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Playlist;
import beans.Song;
import dao.PlaylistDAO;
import dao.SongDAO;
import utils.ConnectionHandler;

@WebServlet("/GetSongDetails")
public class GetSongDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GetSongDetails() {
		super();
		// TODO Auto-generated constructor stub
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
		String sId = request.getParameter("songId");
		if(sId == null || sId.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing song id value");
			return;
		}
		Integer songId = null;
		try {
			songId = Integer.parseInt(sId);
		} catch (NumberFormatException | NullPointerException e) {
			// only for debugging e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		SongDAO sDao= new SongDAO(connection);
		Song song = null;
		try {
			song = sDao.findSongById(songId);
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in the song's  extraction");
			return;
		}
		String path = "WEB-INF/PlayerPage.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("song", song);
		templateEngine.process(path, ctx, response.getWriter());
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
