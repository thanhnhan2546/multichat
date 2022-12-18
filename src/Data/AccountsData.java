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
                p.name = rs.getString(3);
                
                dsPD.add(p);
            }
        }catch(SQLException e){
                 JOptionPane.showMessageDialog(null, "Error show");
        }
        return dsPD;
    }
}
