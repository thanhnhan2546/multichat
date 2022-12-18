/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Admin
 */
public class Connect {
      String host = "localhost";
    String user = "root";
    String pass = "";
    String dtb = "multichat";
    
    Connection conn = null;
    java.beans.Statement st = null;
    ResultSet rs = null;
    
    public Connect(){
        
    }
    public Connect(String Host, String user, String pass, String dtb){
        this.host = Host;
        this.user = user;
        this.pass = pass;
        this.dtb = dtb;
    }
   
    
    public Connection getConnect() {
        
        String url = "jdbc:mysql://"+host+":3306/"+dtb+"?useUnicode=yes&characterEncoding=UTF-8";
        try{
            this.conn = (Connection) DriverManager.getConnection(url,user,pass);
            System.out.println("kết nối thành công");
        }catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "KẾT NỐI CƠ SỞ DỮ LIỆU THẤT BẠI:" + e);
        }
        return this.conn;
    }
}
