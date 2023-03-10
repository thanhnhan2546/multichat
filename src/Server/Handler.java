/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import DTO.Message;
import Data.MessageData;
import static Server.Server.data;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Admin
 */
class Handler implements Runnable{
    // Object để synchronize các hàm cần thiết
    // Các client đều có chung object này được thừa hưởng từ chính server
    private Object lock;

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private String username;
    private String password;
    private boolean isLoggedIn;
    static MessageData msg = new MessageData();

    public Handler(Socket socket, String username, String password, boolean isLoggedIn, Object lock) throws IOException {
        this.socket = socket;
        this.username = username;
        this.password = password;
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
        this.isLoggedIn = isLoggedIn;
        this.lock = lock;
    }

    public Handler(String username, String password, boolean isLoggedIn, Object lock) {
        this.username = username;
        this.password = password;
        this.isLoggedIn = isLoggedIn;
        this.lock = lock;
    }


    public void setIsLoggedIn(boolean IsLoggedIn) {
        this.isLoggedIn = IsLoggedIn;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        try {
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   
    public void closeSocket() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean getIsLoggedIn() {
        return this.isLoggedIn;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public DataOutputStream getDos() {
        return this.dos;
    }

    @Override
    public void run() {

        while (true) {
            try {
                String message = null;

                // Đọc yêu cầu từ user
                message = dis.readUTF();

                // Yêu cầu đăng xuất từ user
                if (message.equals("Log out")) {

                    // Thông báo cho user có thể đăng xuất
                    dos.writeUTF("Safe to leave");
                    dos.flush();

                    // Đóng socket và chuyển trạng thái thành offline
                    socket.close();
                    this.isLoggedIn = false;

                    
                    Server.updateOnlineUsers();
                    break;
                }
                else if(message.equals("selectUser"))   {
                    String receiver = dis.readUTF();
                    String sender = dis.readUTF();
                    System.out.println("receiver: "+ receiver );
                    ArrayList<Message> mess = msg.list(sender, receiver);
                    if(!mess.isEmpty()){
                       for(int i = mess.size() - 1; i >=0; i--){
                        System.out.println("i: " + mess.get(i).message);
                    }
System.out.println("not null");
                    }else{
                        System.out.println(" null");
                    }
                } 
                // Yêu cầu gửi tin nhắn dạng văn bản
                else if (message.equals("Text")){
                    String receiver = dis.readUTF();
                    String content = dis.readUTF();
                    String sender = dis.readUTF();
                    
                    data.saveHistoryMessage(sender, receiver, content);
                    for (Handler client: Server.clients) {
                        if (client.getUsername().equals(receiver)) {
                            synchronized (lock) {
                                client.getDos().writeUTF("Text");
                                client.getDos().writeUTF(this.username);
                                client.getDos().writeUTF(content);
                                client.getDos().flush();
                                break;
                            }
                        }
                    }
                }

                // Yêu cầu gửi tin nhắn dạng Emoji
                else if (message.equals("Emoji")) {
                    String receiver = dis.readUTF();
                    String emoji = dis.readUTF();
                    String sender = dis.readUTF();
                    System.out.println("emoji: "+ emoji);
                    String urlEmoji = emoji.replace("\\", "\\\\\\");
                    System.out.println("url: " + urlEmoji);
                    data.saveHistoryMessage(sender, receiver, urlEmoji);
                    for (Handler client: Server.clients) {
                        if (client.getUsername().equals(receiver)) {
                            synchronized (lock) {
                                client.getDos().writeUTF("Emoji");
                                client.getDos().writeUTF(this.username);
                                client.getDos().writeUTF(emoji);
                                client.getDos().flush();
                                break;
                            }
                        }
                    }
                }

                // Yêu cầu gửi File
                else if (message.equals("File")) {

                    // Đọc các header của tin nhắn gửi file
                    String receiver = dis.readUTF();
                    String filename = dis.readUTF();
                    int size = Integer.parseInt(dis.readUTF());
                    int bufferSize = 2048;
                    byte[] buffer = new byte[bufferSize];

                    for (Handler client: Server.clients) {
                        if (client.getUsername().equals(receiver)) {
                            synchronized (lock) {
                                client.getDos().writeUTF("File");
                                client.getDos().writeUTF(this.username);
                                client.getDos().writeUTF(filename);
                                client.getDos().writeUTF(String.valueOf(size));
                                while (size > 0) {
                                    // Gửi lần lượt từng buffer cho người nhận cho đến khi hết file
                                    dis.read(buffer, 0, Math.min(size, bufferSize));
                                    client.getDos().write(buffer, 0, Math.min(size, bufferSize));
                                    size -= bufferSize;
                                }
                                client.getDos().flush();
                                break;
                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }}
