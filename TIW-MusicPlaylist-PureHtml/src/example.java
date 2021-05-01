import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Date;
public class example {
   public static void main(String args[]) throws Exception{
      //Registering the Driver
      DriverManager.registerDriver(new com.mysql.jdbc.Driver());
      //Getting the connection
      String mysqlUrl = "jdbc:mysql://localhost/dbmusicPlaylist";
      Connection con = DriverManager.getConnection(mysqlUrl, "root", "ilrichi10");
      System.out.println("Connection established......");
      PreparedStatement pstmt = con.prepareStatement("INSERT INTO song VALUES(?,?,?,?,?,?,?,?,?)");
      pstmt.setInt(1, 1);
      pstmt.setInt(9, 1);
      pstmt.setInt(8, 1);
      pstmt.setString(2, "sample image");
      pstmt.setString(4, "sample image");
      pstmt.setString(6, "sample image");
      Date today = new Date();
      Date date = new java.sql.Date(today.getTime());
	  pstmt.setObject(5, date);

      //Inserting Blob type
      InputStream in = new FileInputStream("C:\\Users\\Admin\\Pictures\\ciro.png");
      pstmt.setBlob(3, in);
      pstmt.setBlob(7, in);
      //Executing the statement
      pstmt.execute();
      System.out.println("Record inserted......");
   }
}