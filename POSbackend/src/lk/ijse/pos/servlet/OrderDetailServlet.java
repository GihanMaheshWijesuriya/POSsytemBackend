package lk.ijse.pos.servlet;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/orderdetails")
public class OrderDetailServlet extends HttpServlet {

    @Resource(name = "java:comp/env/jdbc/pool")
    private DataSource ds;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonReader reader = Json.createReader(req.getReader());
        resp.setContentType("application/json");

        PrintWriter out = resp.getWriter();
        Connection connections = null;

        try {
            JsonArray item = reader.readArray();
            connections = ds.getConnection();
            PreparedStatement pstm = connections.prepareStatement("INSERT INTO orderdetail VALUES (ordetailid,?,?,?,?,?)");
            boolean result = false;
            for (int i = 0; i < item.size(); i++) {
                JsonObject itemobj = (JsonObject) item.get(i);
                System.out.println(itemobj);
                int itemid = itemobj.getInt("itemid");
                String itemname = itemobj.getString("itemname");
                double unitprice = itemobj.getInt("unitprice");
                int qty = 5;
                double totalprice = itemobj.getInt("totalprice");


                pstm.setObject(1, itemid);
                pstm.setObject(2, itemname);
                pstm.setObject(3, unitprice);
                pstm.setObject(4, qty);
                pstm.setObject(5, totalprice);
                result = pstm.executeUpdate() > 0;
            }

            if (result) {
                out.println("true");
            } else {
                out.println("false");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            out.println("false");
        } finally {
            try {
                connections.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            out.close();
        }
    }
}
