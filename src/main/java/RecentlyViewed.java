import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.*;

@WebServlet(name="RecentlyViewed", value="/api/recentlyviewed")
public class RecentlyViewed extends HttpServlet {
	private String url = "jdbc:mysql://localhost:3306/ecrocs?serverTimezone=UTC";
	private String dbUsername = "root";
	private String dbPassword = "rxpost123";
	public void init() {
		// 1. Load JDBC driver
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try (PrintWriter writer = response.getWriter()) {

			HttpSession session = request.getSession(false);
			if(session != null) {
				ArrayList<String[]> viewed = (ArrayList<String[]>)session.getAttribute(("viewed"));

				if (viewed != null && viewed.size() > 0) {
					System.out.println(viewed.size());
					writer.println("<table id=\"recent\"><thead><tr>");
					writer.println("<th colspan="+viewed.size() +">Recently Viewed</th>");
					writer.println("</tr></thead><tbody><tr>");
					for(int i = 0; i < viewed.size(); i++) {
						String[] arr = viewed.get(i);
						String id = arr[0];
						String color = arr[1];
						String query = "SELECT `file_name` FROM `shoe_colors` WHERE shoe_colors.shoe_id=\""+id+"\" AND shoe_colors.color_name=\""+color+"\" GROUP BY file_name";
						try {
							Connection con = DriverManager.getConnection(url, dbUsername, dbPassword);

							// 3. Create a statement
							Statement st = con.createStatement();

							// 4. Create a ResultSet
							ResultSet rs = st.executeQuery(query);
							if (rs.next()){
								String fn = rs.getString("file_name");
								writer.println("<td><a href=\"./product?id="+id+"&color="+color+"\"><div class=\"recent-card\">");
								writer.println("<img src=\"./assets/"+id +"/"+fn+".jpg\">");
								writer.println("</div></a></td>");
							}

						}catch (SQLException e) {
							System.out.println(e);
						}

					}
				}
			}
			writer.println("<script src=\"./js/new-products.js\"></script>");
		}
	}

}
