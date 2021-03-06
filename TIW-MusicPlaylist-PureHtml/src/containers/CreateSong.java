package containers;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.User;
import dao.PlaylistDAO;
import dao.SongDAO;
import utils.ConnectionHandler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@MultipartConfig
@WebServlet("/CreateSong")
public class CreateSong extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection	connection = null;
	private TemplateEngine templateEngine;
	
       
    public CreateSong() {
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
		HttpSession s = request.getSession();
		User u = (User) s.getAttribute("user");
		int userid = u.getId();
		String song_title = request.getParameter("song_title");
		String singer = request.getParameter("singer");
		String genre = request.getParameter("music_genre");
		String release_date = request.getParameter("release_date");
		Part image_part;
		try {
			image_part = request.getPart("song_image");
		}catch(Exception e){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing files in request!");
			return;
		}
		InputStream imageContent = image_part.getInputStream();
		Part file_part;
		try {
			file_part = request.getPart("song_file");
		}catch(Exception e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing files in request!");
			return;
		}
		InputStream fileContent = file_part.getInputStream();

		if(song_title == null || song_title.isEmpty() || singer == null || singer.isEmpty() || genre == null || genre.isEmpty() || release_date == null || release_date.isEmpty()){
			String error = "Missing parameters";
		}
		
		if(file_part == null || file_part.getSize() <= 0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing file in request!");
			return;
		}
		
		if(image_part == null || image_part.getSize() <= 0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing image in request!");
			return;
		}
		
		if((!image_part.getContentType().equals("image/png")) && (!image_part.getContentType().equals("image/jpeg"))) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Image file is not a jpeg/png file!");
			return;
		}
		
		if(! file_part.getContentType().equals("audio/mpeg")) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Song file is not a mp3 file!");
			return;
		}

		if(image_part.getSize() > 2000000 ) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Image file is too big!");
			return;
		}
		
		if(file_part.getSize() > 2000000) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Song file is too big!");
			return;
		}
		
		Date date = null;
		
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(release_date);
			
		}catch(ParseException e1) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Date format");
		}
		Date today = new Date();

        if(date.after(today)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Release_date cannot be in the future!");
			return;
        }
		
		SongDAO sDao = new SongDAO(connection);
		
		try {
			sDao.createSong(song_title, imageContent, singer, date , genre, fileContent, userid);
		}catch(SQLException e2) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ("Failure creating the song in the db"));
		}
		String path = getServletContext().getContextPath() + "/GoToHomePage"; 
		response.sendRedirect(path);
	}

}
