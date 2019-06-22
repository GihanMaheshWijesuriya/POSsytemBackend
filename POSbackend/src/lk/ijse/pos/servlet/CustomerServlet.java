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

@WebServlet(urlPatterns = "/customers")
public class CustomerServlet extends HttpServlet {

    @Resource(name = "java:comp/env/jdbc/pool")
    private DataSource ds;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Do Get Workionh");
        try (PrintWriter out = resp.getWriter()) {

            resp.setContentType("application/json");

            try {
                Connection connection = ds.getConnection();

                Statement stm = connection.createStatement();
                ResultSet rst = stm.executeQuery("SELECT * FROM Customer");

                JsonArrayBuilder customers = Json.createArrayBuilder();

                while (rst.next()) {
                    int id = rst.getInt("cusid");
                    String firstname = rst.getString("cusfirstname");
                    String lastname = rst.getString("cuslastname");
                    String email = rst.getString("cusemail");
                    String password = rst.getString("cuspassword");
                    String phonenumber = rst.getString("cusphonenum");

                    JsonObject customer = Json.createObjectBuilder().add("cusid", id)
                            .add("cusfirstname", firstname)
                            .add("cuslastname", lastname)
                            .add("cusemail", email)
                            .add("cuspassword", password)
                            .add("cusphonenum", phonenumber)
                            .build();
                    customers.add(customer);
                }

                out.println(customers.build().toString());

                connection.close();
            } catch (Exception ex) {
                resp.sendError(500, ex.getMessage());
                ex.printStackTrace();
            }

        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JsonReader reader = Json.createReader(req.getReader());
        resp.setContentType("application/json");

        PrintWriter out = resp.getWriter();

        Connection connection = null;

        try {
            JsonObject customer = reader.readObject();
            String firstname = customer.getString("cusfirstname");
            String lastname = customer.getString("cuslastname");
            String email = customer.getString("cusemail");
            String password = customer.getString("cuspassword");
            String phonenumber = customer.getString("cusphonenum");
            connection = ds.getConnection();

            PreparedStatement pstm = connection.prepareStatement("INSERT INTO Customer VALUES (cusid,?,?,?,?,?)");
            pstm.setObject(1, firstname);
            pstm.setObject(2, lastname);
            pstm.setObject(3, email);
            pstm.setObject(4, password);
            pstm.setObject(5, phonenumber);
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
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            out.close();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doDelete(req, resp);
        PrintWriter out = resp.getWriter();
        System.out.println("Delete working");
        String id = req.getParameter("cusid");
        int cusid = Integer.parseInt(id);

        if (cusid > 0) {
            try {
                Connection connection = ds.getConnection();
                PreparedStatement pstm = connection.prepareStatement("DELETE FROM Customer WHERE cusid=?");
                pstm.setObject(1, cusid);
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
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPut(req, resp);
        PrintWriter out = resp.getWriter();
        System.out.println("Update working...");

        JsonReader reader = Json.createReader(req.getReader());
        JsonObject cus = reader.readObject();
        System.out.println(cus);
//        System.out.println(cus.getString("cusid"));
//        int cusID = Integer.parseInt(cus.getString("cusid"));

        if (cus.getInt("cusid") > 0) {
            try {
                String firstnamecus = cus.getString("cusfirstname");
                String lastnamecus = cus.getString("cuslastname");
                String cusemailcus = cus.getString("cusemail");
                String cuspasswordcus = cus.getString("cuspassword");
                String cusphonecus = cus.getString("cusphonenum");
                int idcus = cus.getInt("cusid");

                System.out.println(idcus + "id");
                System.out.println(firstnamecus + "first");
                System.out.println(lastnamecus + "last");
                System.out.println(cusemailcus + "email");
                System.out.println(cuspasswordcus + "pass");
                System.out.println(cusphonecus + "phone");

                Connection connection = ds.getConnection();
                PreparedStatement pstm = connection.prepareStatement("UPDATE customer set cusfirstname =?,cuslastname =?,cusemail =?" +
                        ",cuspassword =?,cusphonenum =? WHERE cusid=?");
                pstm.setObject(1, firstnamecus);
                pstm.setObject(2, lastnamecus);
                pstm.setObject(3, cusemailcus);
                pstm.setObject(4, cuspasswordcus);
                pstm.setObject(5, cusphonecus);
                pstm.setObject(6, idcus);

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
