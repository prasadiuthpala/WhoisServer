package WhoisServer;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Prasadi_uthpala
 */
class ServerThread extends Thread {

    String line = null;
    BufferedReader br = null;
    PrintWriter pw = null;
    Socket client = null;
    Connection con;
    Statement stmt;
    ResultSet re;
    String cdate = "null";
    String edate = "null";
    String newLine = System.getProperty("line.separator");
    String keycode = "null";
    ServerSocket server = null;
    DataOutputStream os;
    String part1;
    String part2;
    String[] parts;
    String l_part;
    String o_b_l_part;

    public ServerThread(Socket s) {
        this.client = s;
    }

    @Override
    public void run() {
        try {

            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/20160214_lkdomains?zeroDateTimeBehavior=convertToNull", "root", "");
            stmt = con.createStatement();

            do {
                os = new DataOutputStream(client.getOutputStream());
                os.writeBytes(newLine);

                os.writeBytes("Enter the Domain Name ");
                os.writeBytes(newLine);
                os.writeBytes(newLine);

                InputStream is = client.getInputStream();
                br = new BufferedReader(new InputStreamReader(is));
                String value = br.readLine();
                if (value.contains(".")) {
                    parts = value.split("\\.");
                    l_part = parts[parts.length - 1];
                } else {
                    os.writeBytes(newLine);
                    os.writeBytes(newLine);
                    os.writeBytes("This is not a Registered Domain under LK Domain Registry");
                    os.writeBytes(newLine);
                    os.writeBytes(newLine);
                    os.writeBytes("-------------END------------");
                    break;
                }

                if (!"lk".equals(l_part)) {

                    os.writeBytes(newLine);
                    os.writeBytes(newLine);
                    os.writeBytes("This is not a Registered Domain under LK Domain Registry");
                    os.writeBytes(newLine);
                    os.writeBytes(newLine);
                    os.writeBytes("-------------END------------");
                    break;
                }
                if (parts.length != 2) {
                    o_b_l_part = parts[parts.length - 2];

                    if (("com".equals(o_b_l_part)) || ("hotel".equals(o_b_l_part)) || ("edu".equals(o_b_l_part)) || ("org".equals(o_b_l_part)) || ("web".equals(o_b_l_part))) {
                        part1 = parts[parts.length - 3];
                        part2 = o_b_l_part + ".lk";
                    } else {
                        part1 = parts[parts.length - 2];
                        part2 = "lk";
                    }
                } else {
                    part1 = parts[parts.length - 2];
                    part2 = "lk";
                }

                String query = "SELECT DomainCategory.Name "
                        + "FROM DomainName_Client, DomainNameType, DomainCategory, OrderDomain_Client "
                        + "WHERE DomainName_Client.Name='" + part1 + "'AND DomainNameType.Name='" + part2 + "' AND DomainName_Client.TypeID=DomainNameType.ID "
                        + "AND DomainCategory.ID=DomainName_Client.DomainCategoryID AND OrderDomain_Client.DomainNameID=DomainName_Client.ID";

                String query1 = "SELECT OrderItem_Client.Created,OrderItem_Client.LastEdited "
                        + "FROM DomainName_Client, DomainNameType, OrderDomain_Client, OrderItem_Client "
                        + "WHERE DomainName_Client.Name='" + part1 + "'AND DomainNameType.Name='" + part2 + "' AND DomainName_Client.TypeID=DomainNameType.ID "
                        + "AND OrderDomain_Client.DomainNameID=DomainName_Client.ID ";

                String queryR2 = "SELECT Customer_Client.FirstName "
                        + "FROM DomainName_Client, DomainNameType, OrderDomain_Client, OrderItem_Client, Order_Client,Customer_Client "
                        + "WHERE DomainName_Client.Name='" + part1 + "' AND DomainNameType.Name='" + part2 + "' AND DomainName_Client.TypeID=DomainNameType.ID "
                        + "AND OrderDomain_Client.OrderItemID=DomainName_Client.ID "
                        + "AND OrderDomain_Client.ID= OrderItem_Client.OrderID AND OrderItem_Client.ID= Order_Client.RegistrantID "
                        + "AND Customer_Client.ID= Order_Client.RegistrantID";

                String query3 = "SELECT ResourceRecord_Client.Type, ResourceRecord_Client.Parameter, ResourceRecord_Client.Value FROM DomainName_Client, DomainNameType, OrderDomain_Client, ResourceRecord_Client WHERE DomainName_Client.Name='" + part1 + "' AND DomainNameType.Name='" + part2 + "' AND DomainName_Client.TypeID=DomainNameType.ID AND OrderDomain_Client.DomainNameID=DomainName_Client.ID AND ResourceRecord_Client.OrderDomainID=OrderDomain_Client.ID ";

                String query4 = "SELECT NameServerRecord_Client.NameServer, NameServerRecord_Client.IPAddress, NameServerRecord_Client.TTL "
                        + "FROM DomainName_Client, DomainNameType, OrderDomain_Client, NameServerRecord_Client "
                        + "WHERE DomainName_Client.Name='" + part1 + "'AND DomainNameType.Name='" + part2 + "' AND DomainName_Client.TypeID=domainnametype.ID "
                        + "AND OrderDomain_Client.DomainNameID=DomainName_Client.ID AND NameServerRecord_Client.OrderDomainID=OrderDomain_Client.ID";

                String query5 = "SELECT Organization_Client.Name FROM DomainName_Client, DomainNameType, OrderDomain_Client,  OrderItem_Client, Order_Client,Organization_Client WHERE DomainName_Client.Name='" + part1 + "' AND DomainNameType.Name='" + part2 + "' AND DomainName_Client.TypeID=DomainNameType.ID AND OrderDomain_Client.DomainNameID=DomainName_Client.ID AND OrderDomain_Client.OrderItemID = OrderItem_Client.OrderID AND OrderItem_Client.ID= Order_Client.OrganizationID AND Organization_Client.ID= Order_Client.OrganizationID";

                try {
                    re = stmt.executeQuery(query);
                    if (!re.isBeforeFirst()) {
                        os.writeBytes(newLine);
                        os.writeBytes(newLine);
                        os.writeBytes("This is not a Registered Domain under LK Domain Registry");
                        os.writeBytes(newLine);
                    } else {

                        while (re.next()) {

                            String Category = re.getString("DomainCategory.Name");

                            os.writeBytes(newLine);
                            os.writeBytes("This Service is Provided by the LK Domain Registry- Sri Lanka. ");
                            os.writeBytes(newLine);
                            os.writeBytes("Visit LK Domain at www.nic.lk");
                            os.writeBytes(newLine);
                            os.writeBytes("Contact Us at +94(0)112-4216061 ");
                            os.writeBytes(newLine);
                            os.writeBytes(newLine);
                            if (Category == null) {
                                os.writeBytes(newLine);
                                os.writeBytes("Category     :   ");
                                os.writeBytes("No any Category");
                            } else {
                                os.writeBytes(newLine);
                                os.writeBytes("Category     :   ");
                                os.writeBytes(Category);
                            }

                            os.writeBytes(newLine);
                            os.writeBytes(newLine);

                            re = stmt.executeQuery(query1);
                            while (re.next()) {

                                Date Created = re.getDate("OrderItem_Client.Created");
                                Date LastEdited = re.getDate("OrderItem_Client.LastEdited");

                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                                if (Created == null) {
                                    cdate = "null";
                                } else {
                                    cdate = df.format(Created);
                                }
                                if (LastEdited == null) {
                                    edate = "null";
                                } else {
                                    edate = df.format(LastEdited);
                                }
                                os.writeBytes(newLine);
                                os.writeBytes("Created on   :  ");
                                os.writeBytes(cdate);
                                os.writeBytes(newLine);
                                os.writeBytes("Expires on   :  ");
                                os.writeBytes(edate);
                                os.writeBytes(newLine);
                                os.writeBytes(newLine);
                                os.writeBytes(newLine);
                                break;
                            }

                            re = stmt.executeQuery(queryR2);
                            while (re.next()) {

                                String FName = re.getString("Customer_Client.FirstName");
                                if ("".equals(FName)) {
                                    os.writeBytes(newLine);
                                    os.writeBytes("Registrant Contact    :  ");
                                    os.writeBytes("No any Registrant Contact ");
                                    os.writeBytes(newLine);
                                    os.writeBytes(newLine);
                                } else {
                                    os.writeBytes(newLine);
                                    os.writeBytes("Registered by    :   ");
                                    os.writeBytes(FName);
                                    os.writeBytes(newLine);
                                    os.writeBytes(newLine);
                                }
                                break;
                            }
                            re = stmt.executeQuery(query5);
                            while (re.next()) {
                                String C_Name = re.getString("Organization_Client.Name");

                                if (C_Name == null) {
                                    os.writeBytes(newLine);
                                    os.writeBytes("Company Name :   ");
                                    os.writeBytes("No any Company Name");
                                } else {
                                    os.writeBytes(newLine);
                                    os.writeBytes("Company Name     :   ");
                                    os.writeBytes(C_Name);
                                }
                                os.writeBytes(newLine);
                                os.writeBytes(newLine);
                                break;
                            }

                            re = stmt.executeQuery(query3);
                            if (re.isBeforeFirst()) {

                                os.writeBytes("Resource Record Information");
                                while (re.next()) {

                                    String Type = re.getString("ResourceRecord_Client.Type");
                                    String Parameter = re.getString("ResourceRecord_Client.Parameter");
                                    String Value = re.getString("ResourceRecord_Client.Value");
//                                    int Priority = re.getInt("ResourceRecord_Client.Priority");

                                    os.writeBytes(newLine);
                                    os.writeBytes(newLine);
                                    os.writeBytes("Type         :   ");
                                    os.writeBytes(Type);
                                    os.writeBytes(newLine);
                                    os.writeBytes("Parameter    :   ");
                                    os.writeBytes(Parameter);
                                    os.writeBytes(newLine);
                                    os.writeBytes("Value        :   ");
                                    os.writeBytes(Value);
                                    os.writeBytes(newLine);
//                                    os.writeBytes("Priority     :   ");
//                                    os.writeByte(Priority );

                                    os.writeBytes(newLine);
                                    os.writeBytes(newLine);
                                }
                            }

                            re = stmt.executeQuery(query4);
                            if (re.isBeforeFirst()) {

                                os.writeBytes("NameServer Record Information");
                                while (re.next()) {
                                    String NameServer = re.getString("NameServerRecord_Client.NameServer");
                                    String IPAddress = re.getString("NameServerRecord_Client.IPAddress");
                                    String TTL = re.getString("NameServerRecord_Client.TTL");

                                    os.writeBytes(newLine);
                                    os.writeBytes(newLine);
                                    os.writeBytes("NameServer   :   ");
                                    os.writeBytes(NameServer);
                                    os.writeBytes(newLine);
                                    if (IPAddress == null) {
                                        os.writeBytes("IPAddress    :   ");
                                        os.writeBytes("No any IP address");
                                    } else {
                                        os.writeBytes("IPAddress    :   ");
                                        os.writeBytes(IPAddress);
                                    }
                                    os.writeBytes(newLine);
                                    if (TTL == null) {
                                        os.writeBytes("TTL          :   ");
                                        os.writeBytes("no any TTL");
                                    } else {
                                        os.writeBytes("TTL          :   ");
                                        os.writeBytes(TTL);
                                    }
                                    os.writeBytes(newLine);

                                }
                            }
                            break;
                        }
                    }

                } catch (SQLException e) {
                } catch (IOException e) {
                }
                os.writeBytes(newLine);
                os.writeBytes("-------------END------------");
                os.writeBytes(newLine);
                break;
            } while ("yes".equals(keycode));
        } catch (IOException e) {
            System.out.println("IO error in server thread");
        } catch (NullPointerException e) {
            line = this.getName(); //reused String line for getting thread name
            System.out.println("Client " + line + " Closed");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                os.writeBytes(newLine);
                os.writeBytes(newLine);
                os.writeBytes("Connection Closing..");

                if (re != null) {
                    re.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
                if (br != null) {
                    br.close();
                    System.out.println("Socket Input Stream Closed");
                }
                if (pw != null) {
                    pw.close();
                    System.out.println("Socket Out Closed");
                }
                if (client != null) {
                    client.close();
                    System.out.println("Socket Closed");
                }
            } catch (IOException ie) {
                System.out.println("Socket Close Error");
            } catch (SQLException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
