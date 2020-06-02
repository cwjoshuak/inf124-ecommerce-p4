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
import java.net.*;

@WebServlet(name="Product", value="/product")
public class ProductServlet extends HttpServlet {
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
		HttpSession session = request.getSession(false);
		if(session != null){
			ArrayList<Item> cart = (ArrayList<Item>)session.getAttribute("cart");
			if (cart == null) {
				session.setAttribute("cart", new ArrayList<Item>());
				cart = (ArrayList<Item>)session.getAttribute("cart");
			}
			String qty = request.getParameter("qty");
			String size = request.getParameter("size");
			String id = request.getParameter("sid");
			String color = request.getParameter("color");
			boolean shouldAdd = true;
			for(int i = 0; i < cart.size(); i++) {
				Item it = cart.get(i);
				if (it.shoe.id.equals(id) && Integer.parseInt(size) == it.size && it.color.equals(color)){
					it.quantity += Integer.parseInt(qty);
					shouldAdd = false;
				}
			}
			if(shouldAdd) {
				Item i = new Item(getShoe(id), request.getParameter("color"), getShoeColor(id,color), Integer.parseInt(size), Integer.parseInt(qty));
				cart.add(i);
				session.setAttribute("cart", cart);
			}
		}

		response.sendRedirect("./checkout");
		return;
    }

    protected void toCart(HttpServletResponse response) throws IOException {
        response.sendRedirect("./checkout");
    }

    public static String getCurrentID(HttpServletRequest request){
        return request.getParameter("id");
    }

    public static String getCurrentColor(HttpServletRequest request){
        return request.getParameter("color");
    }


    protected String getShoeColor(String id, String color){
        String query = "SELECT * FROM `shoe_colors` WHERE shoe_id=" + id + " AND color_name=\"" + color +"\"";

        String fileName = "";
        try {
            Connection con = DriverManager.getConnection(url, dbUsername, dbPassword);

            // 3. Create a statement
            Statement st = con.createStatement();

            // 4. Create a ResultSet
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                fileName = rs.getString("file_name");
            }

            // 5. Close all connections
            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return fileName.substring(fileName.length() -1);
    }

    protected Shoe getShoe(String sid) {
        String query = "SELECT * FROM `shoes` WHERE id=" + sid;
        Shoe shoe = null;

        try {
            Connection con = DriverManager.getConnection(url, dbUsername, dbPassword);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                shoe = new Shoe(rs, new String[]{"type", "name", "id", "price", "desc1", "desc2"});
            }
            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return shoe;
    }

    protected ArrayList<String> getShoeDetails(Shoe s) {
        String query = "SELECT `details` FROM `shoe_details` WHERE shoe_id=" + s.id;
        ArrayList<String> deets = new ArrayList<String>();

        try {
            Connection con = DriverManager.getConnection(url, dbUsername, dbPassword);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                deets.add(rs.getString("details"));
            }

            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return deets;
    }

    protected ArrayList<String> getShoeSizes(Shoe s) {
        String query = "SELECT `size` FROM `shoe_sizes` WHERE shoe_id=" + s.id;
        ArrayList<String> sizes = new ArrayList<String>();

        try {
            Connection con = DriverManager.getConnection(url, dbUsername, dbPassword);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                sizes.add(rs.getString("size"));
            }

            // 5. Close all connections
            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return sizes;
    }

    protected ArrayList<ShoeColor> getShoeColors(Shoe s) {
        String query = "SELECT DISTINCT `color_name`, `file_name` FROM `shoe_colors` WHERE shoe_id=" + s.id;
        ArrayList<ShoeColor> colors = new ArrayList<ShoeColor>();

        try {
            Connection con = DriverManager.getConnection(url, dbUsername, dbPassword);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                colors.add(new ShoeColor(rs));
            }

            // 5. Close all connections
            rs.close();
            st.close();
            con.close();
        } catch (SQLException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return colors;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession(false);
		if (session != null) {
			ArrayList<String[]> viewed = (ArrayList<String[]>)session.getAttribute(("viewed"));
			String id = request.getParameter("id");
			String color = request.getParameter("color");
			for(int i = 0; i < viewed.size(); i++){
				String[] arr = viewed.get(i);
				if (arr[0].equals(id) && arr[1].equals(color)) {
					viewed.remove(i);
				}
			}
			viewed.add(0, new String[]{id, color});

			if (viewed.size() > 5) {
				viewed.remove(5);
			}
			session.setAttribute("viewed", viewed);
		}

        try (PrintWriter writer = response.getWriter()) {
            writer.println("<!DOCTYPE html><html lang='en'>");
            writer.println("<head>");
            writer.println("<title>eCrocs | Product</title>");
            writer.println("<meta name=\"description\" charset=\"UTF-8\" content=\"A site for INF 124 ecommerce project - selling eCrocs\" />");
            writer.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />");
            writer.println("<link href=\"http://db.onlinewebfonts.com/c/158a997f8a01e5bd6f96844ae5739add?family=AG+Book+Rounded\" rel=\"stylesheet\" type=\"text/css\"/>");
            writer.println("<link href=\"https://fonts.googleapis.com/css2?family=Open+Sans+Condensed:wght@300;700&display=swap\" rel=\"stylesheet\"/>");
            writer.println("<link rel=\"stylesheet\" href=\"css/styles.css?v=1.0\" />");
            writer.println("</head>");


            writer.println("<body style=\"margin: 0;\">");
            writer.println("<div class=\"productheader\"><div class=\"logo\"><a href=\"./products\">ecrocs</a></div>" +
                    "<div><a class='cartt' href=\"./checkout\">My Cart</a></div></div>");

            Shoe currShoe = getShoe(getCurrentID(request));
            String currColor = getCurrentColor(request);
            ArrayList<String> deets = getShoeDetails(currShoe);
            ArrayList<String> sizes = getShoeSizes(currShoe);
            ArrayList<ShoeColor> colors = getShoeColors(currShoe);
            String queryID = getCurrentID(request);
			String queryColor = getCurrentColor(request);
            String currColorIndex = getShoeColor(queryID, queryColor);

            writer.println("<div class=\"product\">");
            writer.println("<div class=\"product-left\">");
            writer.println("<img src=\"./assets/"+ currShoe.id +"/product_"+currColorIndex+".jpg\" class='main' />");
            writer.println("<div class= 'selector center'>");
            writer.println("<img src=\"./assets/"+ currShoe.id +"/product_"+currColorIndex+".jpg\" class='active' id='img1' />");
            writer.println("<img src='./assets/"+ currShoe.id +"/color_"+currColorIndex+".jpg' id='img2' />");
            writer.println("</div>");
            writer.println("<script src=\"./js/np2.js\"></script>");
            writer.println("<p>" + currShoe.desc1 + "<br />" + currShoe.desc2 + "</p>");
            writer.println("<span class='bold'>" + currShoe.name + " Details</span>");
            writer.println("<ul>");

            for (int i =0; i <deets.size();i++) {
                writer.println("<li>" + deets.get(i) +"</li>");
            }

            writer.println("</ul>");
            writer.println("</div>");

            writer.println("<div class='product-right'>");
            writer.println("<h2>"+currShoe.name+"</h2>");
            writer.println("<h4 id='baseprice'>$"+currShoe.price+"</h4>");
            writer.println("<hr />");
            writer.println("<span class='color-text bold'>Color: </span>");
            writer.println("<span class='color-text bold'>"+currColor+"</span>");
            writer.println("<br /><br />");
            writer.println("<div class='selector'>");

            for (int i =0; i < colors.size();i++) {
                if (currColor.equals(colors.get(i).colorName)) {
                    writer.println("<a href='./product?id="+currShoe.id+"&color="+colors.get(i).colorName+"'><img src='./assets/"+currShoe.id+"/color_"+colors.get(i).fileName.substring(colors.get(i).fileName.length() -1)+".jpg' class='small active'></a>");
                } else {
                    writer.println("<a href='./product?id="+currShoe.id+"&color="+colors.get(i).colorName+"'><img src='./assets/"+currShoe.id+"/color_"+colors.get(i).fileName.substring(colors.get(i).fileName.length() -1)+".jpg' class='small'></a>");
                }
            }

            writer.println("</div>");
            writer.println("<br />");
			writer.println("<span class='size-text bold'>Shoe Size:</span>");
			writer.println("<select name='size' id='size-selector' form='orderForm'>");

			for (int i =0; i <sizes.size();i++) {
				writer.println("<option value='"+sizes.get(i)+"'>"+sizes.get(i)+"</option>");
			}

			writer.println("</select>");
            writer.println("<br />");

            writer.println("<div class='order-form' id='odForm'>");
			writer.println(" <input type=\"hidden\" name=\"sid\" form='orderForm' value=\""+request.getParameter("id")+"\">");
			writer.println(" <input type=\"hidden\" name=\"color\" form='orderForm' value=\""+request.getParameter("color")+"\">");

            writer.println("</div>");
            writer.println("</div>");
            writer.println("</div>");


            writer.println("<script src=\"./js/newnew-product.js\"></script>");





            writer.println("</body>");
            writer.println("</html>");
        }
    }
}
