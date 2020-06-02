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

@WebServlet(name="OrderConfirmation", value="/order_confirmation")
public class OrderConfirmation extends HttpServlet {
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
	protected Transaction getTransaction(String id) {
		String query = "SELECT * from `transactions` WHERE transactions.id="+id;
		try {
			Connection con = DriverManager.getConnection(url, dbUsername, dbPassword);
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(query);
			Transaction t = null;
			if (rs.next()) {
				t = new Transaction(rs);
			}
			rs.close();
			st.close();
			con.close();
			return t;
		} catch (SQLException e) {}
		return null;
	}
	protected ArrayList<TransactionDetails> getTransactionDetails(String id){
		String query = "SELECT DISTINCT `transaction_id`, transaction_details.shoe_id, transaction_details.color_name, `quantity`, `shoe_size`, `base_price`, `state_tax`, `name`, `file_name` FROM `transaction_details` JOIN `shoes` ON transaction_details.shoe_id=shoes.id JOIN `shoe_colors` ON transaction_details.color_name=shoe_colors.color_name AND shoe_colors.shoe_id=transaction_details.shoe_id WHERE transaction_details.transaction_id="+id ;

		try {
			Connection con = DriverManager.getConnection(url, dbUsername, dbPassword);
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(query);
			ArrayList<TransactionDetails> details = new ArrayList<>();
			while (rs.next()) {
				TransactionDetails dt = new TransactionDetails(rs);
				details.add(dt);
			}

			rs.close();
			st.close();
			con.close();
			return details;
		} catch (SQLException e) {
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return null;
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String transactionId = request.getParameter("id");
		String tax = request.getParameter("taxPercent");

		System.out.println("TESTES TAX");
		System.out.println(tax);


		try (PrintWriter writer = response.getWriter()) {

			writer.println("<!DOCTYPE html><html lang='en'>");
			writer.println("<head>");
			writer.println("<title>eCrocs | Order Confirmation</title>");
			writer.println("<meta name=\"description\" charset=\"UTF-8\" content=\"A site for INF 124 ecommerce project - selling eCrocs\" />");
			writer.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />");
			writer.println("<link href=\"http://db.onlinewebfonts.com/c/158a997f8a01e5bd6f96844ae5739add?family=AG+Book+Rounded\" rel=\"stylesheet\" type=\"text/css\"/>");
			writer.println("<link href=\"https://fonts.googleapis.com/css2?family=Open+Sans+Condensed:wght@300;700&display=swap\" rel=\"stylesheet\"/>");
			writer.println("<link rel=\"stylesheet\" href=\"css/styles.css?v=1.0\" />");
			writer.println("</head>");


			writer.println("<body class=\"confirmation-bg\" style=\"margin: 0;\"");
			writer.println("<div><div class=\"confirmationheader\">");
			writer.println("<div class=\"logo\"><a href=\"../inf124-ecommerce-p3\">ecrocs</a></div></div></div>");

			Transaction tr = getTransaction(transactionId);
			ArrayList<TransactionDetails> details = getTransactionDetails(transactionId);

			double totalPrice = 0.0;
			writer.println("<div class='confirmation'>");
			writer.println("<div class='confirmation-left'>");
			writer.println("<h1>Order Confirmation</h1>");
			writer.println("<h2>Order Number: #"+transactionId+"</h2>");
			for (int i = 0; i < details.size(); i++) {
				TransactionDetails dt = details.get(i);
				System.out.println(dt.shoeName);
				writer.println("<div><h3>"+dt.shoeName+"</h3>");
				writer.println("<img src='./assets/"+dt.shoeId+"/"+dt.imageFile+".jpg'>");
				writer.println("<br/><h5>");
				writer.println("<b>Size: </b>"+dt.shoeSize +" <b>Color:</b> "+dt.colorName+"<br/>");
				writer.println("<b>Quantity:</b> "+dt.quantity +"<br/>");
				writer.println("<b>Price:</b> $"+dt.basePrice +"<br/>");
				double total = (dt.basePrice + dt.stateTax) * dt.quantity;
				totalPrice += total;
				writer.println("</h5></div>");
			}
//			writer.println("<h5><b>Price:</b> $"+String.format("%.2f", totalPrice)+ " + $" + Float.parseFloat(tax)/100*totalPrice + " (" + String.format("%.2f",tax) + "%) <br/>");
//			writer.println("<b>Total Price:</b> $" +String.format("%.2f", (totalPrice + (Float.parseFloat(tax)/100*totalPrice))) + "</h5><br/>");

			writer.println("</div>");

			writer.println("<div class='confirmation-right'>");
			writer.println("<h3>Billing Information</h3><h5>");
			writer.println(tr.billingName+"<br/>");
			writer.println(tr.address+"<br/>");
			writer.println(tr.city+"<br/>");
			writer.println(tr.state+", "+tr.zip +"</h5><br/>");

			writer.println("<h3>Price: $" + String.format("%.2f", totalPrice) + " + $" + String.format("%.2f", (Float.parseFloat(tax)/100*totalPrice)) + " (" + tax + "%)</h3>");
			writer.println("<h3>Total Price: $" + String.format("%.2f", totalPrice + (Float.parseFloat(tax)/100*totalPrice)) + "</h3>");



			writer.println("<div class='payment-info'>");
			writer.println("<br/><br/><br/><h3>Payment Information</h3><h5>");
			writer.println("<b>Name:</b> "+tr.paymentName +"<br/>");
			writer.println("<b>Credit Card Number:</b> **** **** **** "+tr.paymentCard.substring(tr.paymentCard.length()-4)+" <br/>");
			writer.println("<b>Credit Card Expiry:</b> "+String.format("%-2s", tr.paymentMonth).replace(' ', '0')+ "/" +tr.paymentYear+"</h5>");
			writer.println("</div></div></div>");
		}

	}
}
