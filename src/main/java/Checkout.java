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

@WebServlet(name="Checkout", value="/checkout")
public class Checkout extends HttpServlet {
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
    protected void insertIntoDb(ArrayList<Item> items, int transactionId) {
    	if (items != null) {
    		String query = "INSERT INTO `transaction_details` (`transaction_id`, `shoe_id`, `color_name`, `quantity`, `shoe_size`, `base_price`, `state_tax`) VALUES (?,?,?,?,?,?,?)";
			try {
				Connection con = DriverManager.getConnection(url, dbUsername, dbPassword);

				for(int i = 0; i < items.size(); i++){
					PreparedStatement st = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
					st.setInt(1, transactionId);
					st.setString(2, items.get(i).shoe.id);
					st.setString(3, items.get(i).color);
					st.setInt(4, items.get(i).quantity);
					st.setInt(5, items.get(i).size);
					st.setDouble(6, items.get(i).shoe.price);
					st.setDouble(7, 0.0);
					st.execute();
					st.close();
				}

				con.close();
			} catch (SQLException e) {
				System.out.println(e);
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
	}

	protected int insertTransaction(HttpServletRequest request) {

		String name = request.getParameter("fname");
		String phoneNumber = request.getParameter("phone_number");
		String email = request.getParameter("email");
		String address = request.getParameter("address");
		String zip = request.getParameter("zip");
		String shipping_method = request.getParameter("shipping");
		String city = request.getParameter("city");
		String state = request.getParameter("state");
		String cardName = request.getParameter("cardname");
		String cardNumber = request.getParameter("cardnumber");
		String expMonth = request.getParameter("expmonth");
		String expYear = request.getParameter("expyear");


		String query = "INSERT INTO `transactions` (billing_full_name, billing_phone_number, billing_email, billing_addr_1, billing_city, billing_state, billing_zip, shipping_method, payment_name, payment_card, payment_exp_month, payment_exp_year) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			Connection con = DriverManager.getConnection(url, dbUsername, dbPassword);
			PreparedStatement st = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			st.setString(1, name);
			st.setString(2, phoneNumber);
			st.setString(3, email);
			st.setString(4, address);
			st.setString(5, city);
			st.setString(6, state);
			st.setString(7, zip);
			st.setString(8, shipping_method);
			st.setString(9, cardName);
			st.setString(10, cardNumber);
			st.setString(11, expMonth);
			st.setString(12, expYear);

			st.execute();
			int autoIncKeyFromApi = -1;

			ResultSet rs = st.getGeneratedKeys();

			if (rs.next()) {
				autoIncKeyFromApi = rs.getInt(1);
			}
			rs.close();
			st.close();
			con.close();
			return autoIncKeyFromApi;
		} catch (SQLException e) {
			System.out.println(e);
			return -1;
		}
	}
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
    	HttpSession session = request.getSession(false);

    	int id = insertTransaction(request);
		insertIntoDb((ArrayList<Item>)session.getAttribute("cart"), id);

		session.setAttribute("cart", new ArrayList<Item>());
		RequestDispatcher rd = request.getRequestDispatcher("/order_confirmation?id="+id);
		rd.forward(request, response);
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

//        ArrayList<Item> cart = new ArrayList<Item>();
//        cart.add(new Item(getShoe("10001"), "Pool Blue", "3", 7,1));
//        cart.add(new Item(getShoe("205392"), "Black / White ", "2", 8,2));

        try (PrintWriter writer = response.getWriter()) {
            writer.println("<!DOCTYPE html><html lang='en'>");
            writer.println("<head>");
            writer.println("<title>eCrocs | Checkout</title>");
            writer.println("<meta name=\"description\" charset=\"UTF-8\" content=\"A site for INF 124 ecommerce project - selling eCrocs\" />");
            writer.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />");
            writer.println("<link href=\"http://db.onlinewebfonts.com/c/158a997f8a01e5bd6f96844ae5739add?family=AG+Book+Rounded\" rel=\"stylesheet\" type=\"text/css\"/>");
            writer.println("<link href=\"https://fonts.googleapis.com/css2?family=Open+Sans+Condensed:wght@300;700&display=swap\" rel=\"stylesheet\"/>");
            writer.println("<link rel=\"stylesheet\" href=\"css/styles.css?v=1.0\" />");
            writer.println("</head>");


            writer.println("<body class=\"checkout\" style=\"margin: 0;\">");
            writer.println("<div class='confirmationheader'><div class=\"logo\"><a href=\"../inf124-ecommerce-p3/products\">ecrocs</a></div></div>");

            writer.println("<div class='confirmation'>");
            writer.println("<div class='confirmation-left'>");
            writer.println("<h3>Your Cart</h3>");

			HttpSession session = request.getSession(false);
			if(session != null) {
				ArrayList<Item> cart = (ArrayList<Item>) session.getAttribute("cart");
				if (cart == null) {
					session.setAttribute("cart", new ArrayList<Item>());
					cart = (ArrayList<Item>) session.getAttribute("cart");
				}
				double totalPrice = 0;

				for (int i = 0;i<cart.size();i++) {
					writer.println("<div class='cartitem'>");
					writer.println("<h4>"+ cart.get(i).shoe.name +"</h4>");
					writer.println("<div class='cartrows'>");
					writer.println("<div class='cartleft'>");
					writer.println("<img src=\"./assets/"+ cart.get(i).shoe.id+"/product_"+cart.get(i).colorIndex+".jpg\" class='cartpic' />");
					writer.println("</div>");
					writer.println("<div class='cartright'>");
					writer.println("<br />");
					writer.println("<div><h5>Price: $" + cart.get(i).shoe.price +"</h5></div>");
					writer.println("<div><h5>Size:  " + cart.get(i).size +"</h5></div>");
					writer.println("<div><h5>Quantity: " + cart.get(i).quantity +"</h5></div>");
					writer.println("</div>");
					writer.println("</div>");
					writer.println("</div>");
					totalPrice += (cart.get(i).shoe.price * cart.get(i).quantity);
				}

				writer.println("<div><h5>Price: $<span id='baseprice'>" +  Math.round(totalPrice * 100.0) / 100.0+"</span></h5></div>");
                writer.println("<h5>+ $<span id='tax'>0.00</span> <span id='taxfrom'></span> tax (<span id='taxpercentage'>0</span>%)</h5>");
                writer.println("<div><h5>Total Price: $<span id='totalprice'>" +  Math.round(totalPrice * 100.0) / 100.0+"</span></h5></div>");

            }





//            writer.println("<div><h5>Total Price w/ Tax: $" + (totalPrice + tax) +"</h5></div>");


            writer.println("</div>");

            writer.println("<div class='confirmation-right'>");
            writer.println("<div class='order-form' id='odForm'>");

            writer.println("</div>");
            writer.println("</div>");
            writer.println("</div>");


            writer.println("<script src=\"./js/new-checkout.js\"></script>");
            writer.println("</body>");
            writer.println("</html>");
        }
    }
}
