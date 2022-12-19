package Data;

import DTO.Accounts;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Admin
 */
public class AccountsData {
    static Accounts pd = new Accounts();
    Connection conn=null;
    Statement st = null;
    ResultSet rs = null;
    
    public AccountsData(){
        Connect cn = new Connect();
        conn = cn.getConnect();
    }
      public ArrayList<Accounts> list(){
        ArrayList dsPD = new ArrayList<Accounts>();
        try {
            String qry = "SELECT * FROM accounts";
            System.out.println(qry);
            st = conn.createStatement();
            rs = st.executeQuery(qry);
            
            while (rs.next()){
                Accounts p = new Accounts();
                
                p.username = rs.getString(1);
                p.password = rs.getString(2);
                
                dsPD.add(p);
            }
        }catch(SQLException e){
                 JOptionPane.showMessageDialog(null, "Error show");
        }
        return dsPD;
    }
      public void create (String username, String password){
          try{
              String qry = "INSERT INTO accounts VALUES ('" + username + "','" + password + "')";
               System.out.println("qry: " + qry);
            st = conn.createStatement();
            st.execute(qry);
          }catch (SQLException e){
              JOptionPane.showMessageDialog(null, "Error show");
          }
      }
      public String checkTable (String sender, String receiver){
          String flag = "";
          try{
              String qry = "SELECT * " +
"FROM information_schema.tables " +
"WHERE table_schema = 'multichat' " +
"    AND table_name = '"+ sender + "_"+ receiver +"' OR table_name = '"+ receiver + "_"+ sender +"' " +
"LIMIT 1;";
               System.out.println("qry: " + qry);
               
            st = conn.createStatement();
           rs = st.executeQuery(qry);
              if(rs.next()){
                  flag = rs.getString(3);
              }else {
                  flag = "";
              }
          }catch (SQLException e){
              JOptionPane.showMessageDialog(null, "null");
          }
          return flag;
      }
      public void saveHistoryMessage(String sender, String receiver, String message){
//          Kiểm tra table trong database đã tồn tại hay chưa
          String flag = checkTable(sender, receiver);
        if(flag.isEmpty()){
            try{
              String qry = "CREATE TABLE "+ sender + "_"+ receiver + " (sender varchar(255), message varchar(255))";
               System.out.println("qry: " + qry);
            st = conn.createStatement();
            st.execute(qry);
            saveMessage(sender + "_"+ receiver, sender, message );
          }catch (SQLException e){
              JOptionPane.showMessageDialog(null, "Error show");
          }
System.out.println("null");
          }else{
//            lưu tin nhắn vào database
            saveMessage(flag, sender, message);
      }
        }
      public void saveMessage(String tableName, String sender, String message){
          try{
              String qry = "INSERT INTO "+ tableName + " VALUES ('" + sender + "','" + message + "')";
               System.out.println("qry: " + qry);
            st = conn.createStatement();
            st.execute(qry);
          }catch (SQLException e){
              JOptionPane.showMessageDialog(null, "Error show");
          }
      }
      
}
