/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import DTO.Accounts;
import Data.AccountsData;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Admin
 */
public class Server {
    private Object lock;

    private ServerSocket s;
    private Socket socket;
    static ArrayList<Handler> clients = new ArrayList<Handler>();
    static ArrayList<Accounts> listAccounts = new ArrayList<Accounts>();
    private String dataFile = "src\\data\\accounts.txt";

   
    private void loadAccounts() {
        try {
//            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile), "utf8"));
//            System.out.println("br: "+ br);
             AccountsData acc = new AccountsData();
        listAccounts = acc.list();
        listAccounts.forEach((account) -> {
            clients.add(new Handler(account.username, account.password, false, lock));
        });

//            String info = br.readLine();
//            while (info != null && !(info.isEmpty())) {
////                clients.add(new Handler(info.split(",")[0], info.split(",")[1], false, lock));
//                    clients.add(new Handler(info.split(",")[0], info.split(",")[1], false, lock));
//                info = br.readLine();
//            }
//
//            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    private void saveAccounts() {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File(dataFile), "utf8");
        } catch (Exception ex ) {
            System.out.println(ex.getMessage());
        }
        for (Handler client : clients) {
            pw.print(client.getUsername() + "," + client.getPassword() + "\n");
        }
        pw.println("");
        if (pw != null) {
            pw.close();
        }
    }

    public Server() throws IOException {
        try {
            // Object dùng để synchronize cho việc giao tiếp với các người dùng
            lock = new Object();

            // Đọc danh sách tài khoản đã đăng ký
            this.loadAccounts();
            // Socket dùng để xử lý các yêu cầu đăng nhập/đăng ký từ user
            s = new ServerSocket(9999);

            while (true) {
                // Đợi request đăng nhập/đăng xuất từ client
                socket = s.accept();

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                // Đọc yêu cầu đăng nhập/đăng xuất
                String request = dis.readUTF();

                if (request.equals("Sign up")) {
                    // Yêu cầu đăng ký từ user

                    String username = dis.readUTF();
                    String password = dis.readUTF();

                    // Kiểm tra tên đăng nhập đã tồn tại hay chưa
                    if (isExisted(username) == false) {

                        // Tạo một Handler để giải quyết các request từ user này
                        Handler newHandler = new Handler(socket, username, password, true, lock);
                        clients.add(newHandler);

                        // Lưu danh sách tài khoản xuống file và gửi thông báo đăng nhập thành công cho user
                        this.saveAccounts();
                        dos.writeUTF("Sign up successful");
                        dos.flush();

                        // Tạo một Thread để giao tiếp với user này
                        Thread t = new Thread(newHandler);
                        t.start();

                        // Gửi thông báo cho các client đang online cập nhật danh sách người dùng trực tuyến
                        updateOnlineUsers();
                    } else {

                        // Thông báo đăng nhập thất bại
                        dos.writeUTF("This username is being used");
                        dos.flush();
                    }
                } else if (request.equals("Log in")) {
                    // Yêu cầu đăng nhập từ user

                    String username = dis.readUTF();
                    String password = dis.readUTF();

                    // Kiểm tra tên đăng nhập có tồn tại hay không
                    if (isExisted(username) == true) {
                        for (Handler client : clients) {
                            if (client.getUsername().equals(username)) {
                                // Kiểm tra mật khẩu có trùng khớp không
                                if (password.equals(client.getPassword())) {

                                    // Tạo Handler mới để giải quyết các request từ user này
                                    Handler newHandler = client;
                                    newHandler.setSocket(socket);
                                    newHandler.setIsLoggedIn(true);

                                    // Thông báo đăng nhập thành công cho người dùng
                                    dos.writeUTF("Log in successful");
                                    dos.flush();

                                    // Tạo một Thread để giao tiếp với user này
                                    Thread t = new Thread(newHandler);
                                    t.start();

                                    // Gửi thông báo cho các client đang online cập nhật danh sách người dùng trực tuyến
                                    updateOnlineUsers();
                                } else {
                                    dos.writeUTF("Password is not correct");
                                    dos.flush();
                                }
                                break;
                            }
                        }

                    } else {
                        dos.writeUTF("This username is not exist");
                        dos.flush();
                    }
                }

            }

        } catch (Exception ex){
            System.err.println(ex);
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }

    public boolean isExisted(String name) {
        for (Handler client:clients) {
            if (client.getUsername().equals(name)) {
                return true;
            }
        }
        return false;
    }

    
    public static void updateOnlineUsers() {
        String message = " ";
        for (Handler client:clients) {
            if (client.getIsLoggedIn() == true) {
                message += ",";
                message += client.getUsername();
            }
        }
        for (Handler client:clients) {
            if (client.getIsLoggedIn() == true) {
                try {
                    client.getDos().writeUTF("Online users");
                    client.getDos().writeUTF(message);
                    client.getDos().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}