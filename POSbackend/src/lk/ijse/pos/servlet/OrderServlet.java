package lk.ijse.pos.servlet;

import javax.annotation.Resource;
import javax.json.Json;
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

@WebServlet(urlPatterns = "/orders")
public class OrderServlet extends HttpServlet {
    @Resource(name = "java:comp/env/jdbc/pool")
    private DataSource ds;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonReader reader = Json.createReader(req.getReader());
        resp.setContentType("application/json");

        PrintWriter out = resp.getWriter();
        Connection connections = null;

        try {
            JsonObject orders = reader.readObject();
            System.out.println(orders);
            String date = orders.getString("orderdate");
            String time = orders.getString("ordertime");
            int orid = orders.getInt("ordetailid");
            int cusid = orders.getInt("cusid");
            double totalprice = orders.getInt("totalprice");
            connections = ds.getConnection();

            PreparedStatement pstm = connections.prepareStatement("INSERT INTO orders VALUES (orderid,?,?,?,?,?)");
            pstm.setObject(1, date);
            pstm.setObject(2, time);
            pstm.setObject(3, orid);
            pstm.setObject(4, cusid);
            pstm.setObject(5, totalprice);
            boolean result = pstm.executeUpdate() > 0;

            if (result) {
                out.println("true");
            } else {
                out.println("false");
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
