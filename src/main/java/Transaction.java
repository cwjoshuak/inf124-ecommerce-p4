import java.sql.ResultSet;
import java.sql.SQLException;

public class Transaction {
	String billingName;
	String billingPhone;
	String billingEmail;
	String address;
	String city;
	String state;
	String zip;

	String shippingMethod;

	String paymentName;
	String paymentCard;
	String paymentMonth;
	String paymentYear;

	public Transaction(ResultSet rs){
		try {
			this.billingName = rs.getString("billing_full_name");
			this.billingPhone = rs.getString("billing_phone_number");
			this.billingEmail = rs.getString("billing_email");
			this.address = rs.getString("billing_addr_1");
			this.city = rs.getString("billing_city");
			this.state = rs.getString("billing_state");
			this.zip = rs.getString("billing_zip");
			this.shippingMethod = rs.getString("shipping_method");
			this.paymentName = rs.getString("payment_name");
			this.paymentCard = rs.getString("payment_card");
			this.paymentMonth = rs.getString("payment_exp_month");
			this.paymentYear = rs.getString("payment_exp_year");

		} catch (SQLException e) {}

	}
}
