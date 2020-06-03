package com.shoeclasses.shoe;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NewShoe {
    public String type = null;
    public String id = null;
    public String name = null;
    public String desc1 = null;
    public String desc2 = null;
    public double price = 0.0;

    public NewShoe(ResultSet rs, String[] colNames) {
        try {
            for(int i = 0; i < colNames.length; i++) {
                switch(colNames[i]) {
                    case "type":
                        this.type = rs.getString("type");
                        break;
                    case "id":
                        this.id = rs.getString("id");
                        break;
                    case "desc1":
                        this.desc1 = rs.getString("desc1");
                        break;
                    case "desc2":
                        this.desc2 = rs.getString("desc2");
                        break;
                    case "name":
                        this.name = rs.getString("name");
                        break;
                    case "price":
                        this.price = rs.getDouble("price");
                        break;
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    @Override
    public String toString() {
        return "Shoe{" +
                "type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", desc1='" + desc1 + '\'' +
                ", desc2='" + desc2 + '\'' +
                ", price=" + price +
                '}';
    }
}
