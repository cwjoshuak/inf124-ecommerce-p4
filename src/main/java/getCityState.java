import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name="getCityState", value="/api/getCityState")
public class getCityState extends HttpServlet {
    private String url = "jdbc:mysql://localhost:3306/ecrocs?serverTimezone=UTC";
    private String dbUsername = "root";
    private String dbPassword = "rxpost123";

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String zip = request.getParameter("zip");
        String zip_query = "SELECT * FROM `zip_codes` WHERE zip=" + zip;
        String tax_query = "SELECT * FROM `tax_rates` WHERE ZipCode=" + zip;


        try {
            Connection con = DriverManager.getConnection(url, dbUsername, dbPassword);
            Statement st1 = con.createStatement();
            Statement st2 = con.createStatement();
            ResultSet rs1 = st1.executeQuery(zip_query);

            String city = "";
            String state = "";
            String rate = "";
            String regionName = "";

            System.out.println("AJAX ZIP CODES");
            while (rs1.next()) {
                city = rs1.getString("city");
                state = rs1.getString("state");
            }
            rs1.close();

            ResultSet rs2 = st2.executeQuery(tax_query);
            while (rs2.next()) {
                rate= rs2.getString("CombinedRate");
                regionName = rs2.getString("TaxRegionName");
            }
            try (PrintWriter writer = response.getWriter()) {
                writer.println(city + "," + state + "," + rate + "," + regionName);
            }
            rs2.close();
            st1.close();
            st2.close();
            con.close();
        } catch (SQLException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
