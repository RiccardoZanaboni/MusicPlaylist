package containers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Playlist;
import beans.Song;
import beans.User;
import dao.PlaylistDAO;
import dao.SongDAO;
import utils.ConnectionHandler;

@WebServlet("/GetPlaylist")
public class GetPlaylist extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public GetPlaylist() {
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
		String pId = request.getParameter("playlistId");
		String sId = request.getParameter("songId");
		
		if(pId == null || pId.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing the playlist id");
			return;
		}
		
		Integer playlistId = null;
		try {
			playlistId = Integer.parseInt(pId);
		} catch (NumberFormatException | NullPointerException e) {
			// only for debugging e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect playlistId value");
			return;
		}
		
		if(sId == null || sId.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing the starting songid");
			return;
		}
		Integer startingSongId = null;
		try {
			startingSongId = Integer.parseInt(sId);
		} catch (NumberFormatException | NullPointerException e) {
			// only for debugging e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect SongId value");
			return;
		}
		
		PlaylistDAO pDao= new PlaylistDAO(connection);
		Playlist playlist = null;
		try {
			playlist = pDao.findPlaylistById(playlistId);
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in the playlist's extraction");
			return;
		}
		SongDAO sDao = new SongDAO(connection);
		List<Song> songs = null;
		List<Song> songsOfUser = null;
		List<Song> songToView = null;
		HttpSession s = request.getSession();
		User u = (User) s.getAttribute("user");
		int userId = u.getId();
		try {
			songs = sDao.findSongByPlaylistId(playlistId,userId);
			songsOfUser = sDao.findSongByUserId(userId,playlistId);
		}catch(SQLException e) {
			System.out.println(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in the playlist's songs extraction");
			return;
		}
		
		songToView = songs;
		Integer songsSize = songs.size();
		if(startingSongId >songsSize) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "SongId value is too large");
			return;
		}
		startingSongId = (startingSongId/5) * 5;
		System.out.println(startingSongId);
		if ((songsSize-startingSongId) > 0) {
			if((songsSize-startingSongId)>5) {
				songToView = songs.subList(startingSongId, (startingSongId+5));
			}else {
				songToView = songs.subList(startingSongId, (startingSongId+(songs.size()-startingSongId)));
			}
		}
	
		Integer nextSongId = startingSongId+5;
		Integer previousSongId = startingSongId-5;
		
		String path = "WEB-INF/PlaylistPage.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("playlist",playlist);
		ctx.setVariable("songToView", songToView);
		ctx.setVariable("songsOfUser", songsOfUser);
		ctx.setVariable("nextSongId",nextSongId);
		ctx.setVariable("previousSongId",previousSongId);
		ctx.setVariable("songsSize", songsSize);
		ctx.setVariable("songsUsersSize", songsOfUser.size());

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
