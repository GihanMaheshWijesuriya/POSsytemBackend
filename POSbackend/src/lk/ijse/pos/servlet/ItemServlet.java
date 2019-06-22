package lk.ijse.pos.servlet;

import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
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
import java.sql.*;

@WebServlet(urlPatterns = "/items")
public class ItemServlet extends HttpServlet {

    @Resource(name = "java:comp/env/jdbc/pool")
    private DataSource ds;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try (PrintWriter out = resp.getWriter()) {

            resp.setContentType("application/json");

            try {
                Connection connection = ds.getConnection();

                Statement stm = connection.createStatement();
                ResultSet rst = stm.executeQuery("SELECT * FROM Item");

                JsonArrayBuilder items = Json.createArrayBuilder();

                while (rst.next()){
                    int itemid = rst.getInt("itemid");
                    String itemname = rst.getString("itemname");
                    double itemPrice = rst.getDouble("itemprice");
                    int itemqty = rst.getInt("itemqty");

                    JsonObject item = Json.createObjectBuilder()
                            .add("itemid", itemid)
                            .add("itemname", itemname)
                            .add("itemprice", itemPrice)
                            .add("itemqty", itemqty)
                            .build();
                    System.out.println(item);
                    items.add(item);
                }

                out.println(items.build().toString());

                connection.close();
            } catch (Exception ex) {
                resp.sendError(500, ex.getMessage());
                ex.printStackTrace();
            }

        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);

        JsonReader reader = Json.createReader(req.getReader());
        System.out.println(reader + "rr");
        resp.setContentType("application/json");

        PrintWriter out = resp.getWriter();

        Connection connections = null;

        try {
            JsonObject item = reader.readObject();
            System.out.println(item);
            String itemname = item.getString("itemname");
            double price = Double.parseDouble(item.getString("itemprice"));
            int qty = item.getInt("itemqty");
            connections = ds.getConnection();

            PreparedStatement pstm = connections.prepareStatement("INSERT INTO item VALUES (itemid,?,?,?)");
            pstm.setObject(1, itemname);
            pstm.setObject(2, price);
            pstm.setObject(3, qty);
            boolean result = pstm.executeUpdate() > 0;

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

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        System.out.println("Update working...");

        JsonReader reader = Json.createReader(req.getReader());
        JsonObject item = reader.readObject();
        System.out.println(item);

        if (item.getInt("itemid") > 0) {
            try {
                String itmname = item.getString("itemname");
                double itmprc = item.getInt("itemprice");
                int itmqty = item.getInt("itemqty");
                int itmid = item.getInt("itemid");

                Connection connection = ds.getConnection();
                PreparedStatement pstm = connection.prepareStatement("UPDATE item set itemname =?,itemprice =?,itemqty =? WHERE itemid=?");
                pstm.setObject(1, itmname);
                pstm.setObject(2, itmprc);
                pstm.setObject(3, itmqty);
                pstm.setObject(4, itmid);

                int doExecute = pstm.executeUpdate();
                if (doExecute > 0) {
                    resp.setStatus(HttpServletResponse.SC_ACCEPTED);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }

            } catch (Exception ex) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                ex.printStackTrace();
            }
            out.println("true");
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            out.println("false");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        System.out.println("Delete working");
        String itid = req.getParameter("itemid");
        int itmid = Integer.parseInt(itid);

        if (itmid > 0) {
            try {
                Connection connection = ds.getConnection();
                PreparedStatement pstm = connection.prepareStatement("DELETE FROM item WHERE itemid=?");
                pstm.setObject(1, itmid);
                int doExecute = pstm.executeUpdate();
                if (doExecute > 0) {
                    resp.setStatus(HttpServletResponse.SC_ACCEPTED);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }

            } catch (Exception ex) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                ex.printStackTrace();
            }
            out.println("true");
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            out.println("false");
        }
    }
}
