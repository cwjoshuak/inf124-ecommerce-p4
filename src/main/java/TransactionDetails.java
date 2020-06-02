import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionDetails {
	public int transactionId;
	public String shoeId;
	public String shoeName;
	public String colorName;
	public String imageFile;
	public int quantity;
	public int shoeSize;
	public double basePrice;
	public double stateTax;

	public TransactionDetails(ResultSet rs) {
		try {
			this.transactionId = rs.getInt("transaction_id");
			this.shoeId = rs.getString("shoe_id");
			this.shoeName = rs.getString("name");
			this.colorName = rs.getString("color_name");
			this.imageFile = rs.getString("file_name");
			this.quantity = rs.getInt("quantity");
			this.shoeSize = rs.getInt("shoe_size");
			this.basePrice = rs.getDouble("base_price");
			this.stateTax = rs.getDouble("state_tax");

		} catch (SQLException e) {
			System.out.println(e);
		}
	}
}
