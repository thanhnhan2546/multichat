package Data;

import DTO.Accounts;
import DTO.Message;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
public class MessageData {
    static Message msg = new Message();
     Connection conn=null;
    Statement st = null;
    ResultSet rs = null;
    
    public MessageData(){
         Connect cn = new Connect();
        conn = cn.getConnect();
    }
    
    public ArrayList<Message> list(String sender, String receiver){
        ArrayList listMsg = new ArrayList<Message>();
        AccountsData data = new AccountsData();
        String flag = data.checkTable(sender, receiver);
        System.out.println("flag: "+ flag);
       if(flag.isEmpty()){
           listMsg = new ArrayList<Message>();
       }
       else{
           try {
            String qry = "SELECT * FROM " + flag ;
            System.out.println("qry: "+ qry);
            st = conn.createStatement();
            rs = st.executeQuery(qry);
            
            while (rs.next()){
                Message p = new Message();
                
                p.sender = rs.getString(1);
                p.message = rs.getString(2);
                
                listMsg.add(p);
            }
        }catch(SQLException e){
                 JOptionPane.showMessageDialog(null, "Error show");
        }
       }
       return listMsg;
    }
}
