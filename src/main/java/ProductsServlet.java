import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.*;
import java.util.*;

@WebServlet(name="Products", value="/products")
public class ProductsServlet extends HttpServlet {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }


    protected Map<String, ArrayList<Shoe>> getProducts() {

		String query = "SELECT `type`, `id`, `name`, `price` FROM `shoes` ORDER BY `type`";
		try {
			Connection con = DriverManager.getConnection(url, dbUsername, dbPassword);

			// 3. Create a statement
			Statement st = con.createStatement();

			// 4. Create a ResultSet
			ResultSet rs = st.executeQuery(query);
			Map<String, ArrayList<Shoe>> shoes = new TreeMap<>();

			while (rs.next()) {
				String shoeType = rs.getString("type");
				if (!shoes.containsKey(shoeType)) {
					shoes.put(shoeType, new ArrayList<Shoe>());
				}
				ArrayList<Shoe> shoeList = shoes.get(shoeType);

				shoeList.add(new Shoe(rs, new String[]{"type", "name", "id", "price"}));
			}
			// 5. Close all connections
			rs.close();
			st.close();
			con.close();
			return shoes;
		} catch (SQLException e) {
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}

	protected Map<String, ShoeColor> processShoe(Shoe s) {

		String query = "SELECT `color_name`, `color_hex`, `file_name` FROM `shoe_colors` WHERE shoe_colors.shoe_id=\""+s.id +"\" ORDER BY `file_name`";
		try {
			Connection con = DriverManager.getConnection(url, dbUsername, dbPassword);

			// 3. Create a statement
			Statement st = con.createStatement();

			// 4. Create a ResultSet
			ResultSet rs = st.executeQuery(query);
			Map<String, ShoeColor> shoeColors = new TreeMap<>();

			while (rs.next()) {
				String fileName = rs.getString("file_name");
				if (!shoeColors.containsKey(fileName)) {
					shoeColors.put(fileName, new ShoeColor(rs));
				} else {
					shoeColors.get(fileName).addColorHex(rs);
				}
			}
			// 5. Close all connections
			rs.close();
			st.close();
			con.close();
			return shoeColors;
		} catch (SQLException e) {
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		if (session.getAttribute("viewed") == null) {

			ArrayList<String> test = new ArrayList<>();
			session.setAttribute("viewed", test);
		}

		try (PrintWriter writer = response.getWriter()) {

			writer.println("<!DOCTYPE html><html lang='en'>");
			writer.println("<head>");
			writer.println("<title>eCrocs | Products</title>");
			writer.println("<meta name=\"description\" charset=\"UTF-8\" content=\"A site for INF 124 ecommerce project - selling eCrocs\" />");
			writer.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />");
			writer.println("<link href=\"http://db.onlinewebfonts.com/c/158a997f8a01e5bd6f96844ae5739add?family=AG+Book+Rounded\" rel=\"stylesheet\" type=\"text/css\"/>");
			writer.println("<link href=\"https://fonts.googleapis.com/css2?family=Open+Sans+Condensed:wght@300;700&display=swap\" rel=\"stylesheet\"/>");
			writer.println("<link rel=\"stylesheet\" href=\"css/styles.css?v=1.0\" />");
			writer.println("</head>");


			writer.println("<body class=\"products\" style=\"margin: 0;\">");
			writer.println("<div><div class=\"logo\"><a href=\"../inf124-ecommerce-p3\">ecrocs</a></div></div>");

			Map<String, ArrayList<Shoe>> shoes = getProducts();
			Iterator<String> ite = shoes.keySet().iterator();
			while(ite.hasNext()) {
				String shoeType = ite.next();
				writer.println("<table><thead><tr>");
				writer.println("<th colspan=3>Men's " + shoeType + "</th>");
				writer.println("</tr></thead><tbody><tr>");
				ArrayList<Shoe> shoeList = shoes.get(shoeType);
				for (int i = 0; i < shoeList.size(); i++) {
					Shoe shoe = shoeList.get(i);
					Map<String, ShoeColor> colors = processShoe(shoe);
					writer.println("<td>");
					String colorsDiv = "<div class=\"colors\" id=\"colors-" + shoe.id + "\">";

					Iterator<String> colorsIte = colors.keySet().iterator();
					while (colorsIte.hasNext()) {
						String colorDivStyle = "";
						ShoeColor sc = colors.get(colorsIte.next());
						if (sc.colorHex.size() == 2) {
							colorDivStyle = "\"background-image: " + dualGradient(sc.colorHex.get(0), sc.colorHex.get(1)) + ";\"";
						} else {
							colorDivStyle = "\"background-color: " + sc.colorHex.get(0) + ";\"";
						}
						colorsDiv += "<div class=\"circle\" name=\"" + sc.colorName + "\" style=" + colorDivStyle + "></div>";
					}
					colorsDiv += "</div>";
					String title = "<div class=\"title\">" + shoe.name + "</div>";

					String img = "<img src=\"./assets/" + shoe.id + "/product_0.jpg\" id=\"img-" + shoe.id + "\">";
					String price = "<span class=\"price\">$" + shoe.price + "</span>";

					String card = "<div class=\"card\">" + title + img + price + colorsDiv + "</div>";
					String anchor = "<a id=\"a-" + shoe.id + "\" href=\"\">" + card + "</a>";

					writer.println(anchor);
					writer.println("</td>");

				}
				writer.println("</tr></tbody></table>");
			}

			RequestDispatcher rd = request.getRequestDispatcher("/api/recentlyviewed");
			rd.include(request, response);
			writer.println("</body>");
			writer.println("</html>");
		}
    }
    private String dualGradient(String gradient1, String gradient2) {
		return "-webkit-linear-gradient(-235deg, "+gradient1 +" 50%, "+gradient2 +" 50%)";
	}
}
