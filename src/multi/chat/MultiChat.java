/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multi.chat;

import DTO.Accounts;
import Data.AccountsData;
import java.util.ArrayList;
    
/**
 *
 * @author Admin
 */
public class MultiChat {
static ArrayList<Accounts> listAccounts = new ArrayList<Accounts>();
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        AccountsData acc = new AccountsData();
        listAccounts = acc.list();
        listAccounts.forEach((account) -> {
            System.out.println("acc: "+ account.username);
        });
    }
    
}
