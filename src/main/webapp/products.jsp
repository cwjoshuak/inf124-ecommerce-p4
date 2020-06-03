<%--
  Created by IntelliJ IDEA.
  User: garry737
  Date: 6/2/20
  Time: 11:20 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang='en'>
<head>
    <title>eCrocs | Products</title>
    <meta name="description" charset="UTF-8" content="A site for INF 124 ecommerce project - selling eCrocs">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="http://db.onlinewebfonts.com/c/158a997f8a01e5bd6f96844ae5739add?family=AG+Book+Rounded" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css2?family=Open+Sans+Condensed:wght@300;700&amp;display=swap" rel="stylesheet">
    <link rel="stylesheet" href="css/styles.css?v=1.0">
</head>

<body class="products" style="margin: 0;">
<div><div class="logo"><a href="../inf124-ecommerce-p3">ecrocs</a></div></div>

<%@ page import="java.sql.*"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.TreeMap" %>
<%@ page import="com.shoeclasses.shoe.*"%>
<%@ page import="java.util.Iterator" %>

<%
    Class.forName("com.mysql.jdbc.Driver");
    String url = "jdbc:mysql://localhost:3306/ecrocs?serverTimezone=UTC";
    String dbUsername = "root";
    String dbPassword = "";
    Connection conn1 = DriverManager.getConnection(url,dbUsername,dbPassword);
    Statement stmt1 = conn1.createStatement();
    String sqlStr1 = "SELECT `type`, `id`, `name`, `price` FROM `shoes` ORDER BY `type`";
    ResultSet rs1 = stmt1.executeQuery(sqlStr1);
    Map<String, ArrayList<NewShoe>> shoes = new TreeMap<>();

    while (rs1.next()) {
        String shoeType = rs1.getString("type");
        if (!shoes.containsKey(shoeType)) {
            shoes.put(shoeType, new ArrayList<NewShoe>());
        }
        ArrayList<NewShoe> shoeList = shoes.get(shoeType);
        shoeList.add(new NewShoe(rs1, new String[]{"type", "name", "id", "price"}));
    }

    Iterator<String> ite = shoes.keySet().iterator();
    while(ite.hasNext()) {
        String shoeType = ite.next();
%>
<table>
    <thead>
    <tr>
        <th colspan=3>sMen's <%=shoeType%> </th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <%
            ArrayList<NewShoe> shoeList = shoes.get(shoeType);
            for (int i = 0; i < shoeList.size(); i++) {
                NewShoe shoe = shoeList.get(i);
                Connection conn2 = DriverManager.getConnection(url,dbUsername,dbPassword);
                Statement stmt2 = conn2.createStatement();
                String sqlStr2 = "SELECT `color_name`, `color_hex`, `file_name` FROM `shoe_colors` WHERE shoe_colors.shoe_id=\""+shoe.id +"\" ORDER BY `file_name`";
                ResultSet rs2 = stmt2.executeQuery(sqlStr2);
                Map<String, NewShoeColor> shoeColors = new TreeMap<>();

                while (rs2.next()) {
                    String fileName = rs2.getString("file_name");
                    if (!shoeColors.containsKey(fileName)) {
                        shoeColors.put(fileName, new NewShoeColor(rs2));
                    } else {
                        shoeColors.get(fileName).addColorHex(rs2);
                    }
                }

        %>
        <td>
            <a id="a-<%=shoe.id%>" href=" ">
                <div class="card">
                    <div class="title"><%=shoe.name%></div>
                    <img src="./assets/<%=shoe.id%>/product_0.jpg" id="img-<%=shoe.id%>">
                    <span class="price">$<%=shoe.price%></span>
                    <div class="colors" id="colors-<%=shoe.id%>"><%
                        Iterator<String> colorsIte = shoeColors.keySet().iterator();
                        while (colorsIte.hasNext()) {
                            String colorDivStyle = "";
                            NewShoeColor sc = shoeColors.get(colorsIte.next());
                            if (sc.colorHex.size() == 2) {
                                colorDivStyle = "\"background-image: -webkit-linear-gradient(-235deg, "+ sc.colorHex.get(0) +" 50%, "+ sc.colorHex.get(1) +" 50%);\"";
                            } else {
                                colorDivStyle = "\"background-color: " + sc.colorHex.get(0) + ";\"";
                            }
                    %><div class="circle" name="<%=sc.colorName%>" style=<%=colorDivStyle%>></div><%}%></div>
                </div>
            </a>
        </td>
        <%}%>
    </tr>
    </tbody>
</table>
<%}%>
<%--// recentlyviewed--%>
<%
    HttpSession sessi = request.getSession();
    if (sessi.getAttribute("viewed") == null) {

        ArrayList<String> test = new ArrayList<>();
        sessi.setAttribute("viewed", test);
    }
    sessi = request.getSession(false);
    if(sessi != null) {
        System.out.println("viewed");
        System.out.println(sessi);
        System.out.println(sessi.getAttributeNames());

        ArrayList<String[]> viewed = (ArrayList<String[]>) sessi.getAttribute(("viewed"));
        System.out.println(viewed);
        if (viewed != null && viewed.size() > 0) {
%>
<table id="recent"><thead><tr>
    <th colspan=<%=viewed.size()%>>Recently Viewed</th>
</tr></thead><tbody><tr>
        <%
            for(int i = 0; i < viewed.size(); i++) {
                String[] arr = viewed.get(i);
                String id = arr[0];
                String color = arr[1];
                Connection conn3 = DriverManager.getConnection(url,dbUsername,dbPassword);
                Statement stmt3 = conn3.createStatement();
                String sqlStr3 = "SELECT `file_name` FROM `shoe_colors` WHERE shoe_colors.shoe_id=\""+id+"\" AND shoe_colors.color_name=\""+color+"\" GROUP BY file_name";
                ResultSet rs3 = stmt3.executeQuery(sqlStr3);
                if (rs3.next()){
                    String fn = rs3.getString("file_name");
                    System.out.println(fn);
%>
    <td>
        <a href="./product?id=<%=id%>&color=<%=color%>">
            <div class="recent-card">
                <img src="./assets/<%=id%>/<%=fn%>.jpg">
            </div>
        </a>
    </td>
        <%
                }
            }
        }
    }
%>
    <script src="./js/new-products.js"></script>
</body>
</html>
