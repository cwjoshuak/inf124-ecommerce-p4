import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ShoeColor {
	public String colorName;
	public ArrayList<String> colorHex = new ArrayList<>();
	public String fileName;

	public ShoeColor(ResultSet rs) {
		try {
			this.colorName = rs.getString("color_name");
			addColorHex(rs);
			this.fileName = rs.getString("file_name");
		}catch(SQLException e) {

		}
	}
	public void addColorHex(ResultSet rs) {
		try {
			this.colorHex.add(rs.getString("color_hex"));
		} catch(SQLException e) {

		}
	}
	@Override
	public String toString() {
		return "ShoeColor{" +
				"color_name=" + colorName + '\'' +
				", file_name='" + fileName + '\'' +
				'}';
	}

}
