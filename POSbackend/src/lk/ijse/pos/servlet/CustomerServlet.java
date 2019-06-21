package lk.ijse.absd.servlet;

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
import javax.servlet.http.HttpSession;
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

        try (PrintWriter out = resp.getWriter()) {

            resp.setContentType("application/json");

            try {
                Connection connection = ds.getConnection();

                Statement stm = connection.createStatement();
                ResultSet rst = stm.executeQuery("SELECT * FROM Customer");

                JsonArrayBuilder customers = Json.createArrayBuilder();

                while (rst.next()){
                    String id = rst.getInteger("cusid");
                    String firstname = rst.getString("cusfirstname");
                    String lastname = rst.getString("cuslastname");
                    String email = rst.getString("cusemail");
                    String password = rst.getString("cuspassword");
                    String phonenumber = rst.getString("cusphonenum");

                    JsonObject customer = Json.createObjectBuilder().add("cusid", id)
                            .add("cusfirstname", firstname)
                            .add("cuslastname", lastname)
                            .add("email", cusemail)
                            .add("password", cuspassword)
                            .add("phonenumber", cusphonenum)
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
            boolean result = pstm.executeUpdate()>0;

            if (result){
                out.println("true");
            }else{
                out.println("false");
            }

        }catch (Exception ex){
            ex.printStackTrace();
            out.println("false");
        }finally {
            try {
                connection.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            out.close();
        }
    }
}
