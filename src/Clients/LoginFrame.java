package Clients;

import Clients.ChatFrame;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout.*;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.awt.*;

/**
 * view
 * @Created by DELL - StudentID: 18120652
 * @Date Jul 16, 2020 - 8:54:34 PM
 * @Description ...
 */
public class LoginFrame extends JFrame {

    private JPanel contentPane;
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    private String host = "localhost";
    private int port = 9999;
    private Socket socket;

    private DataInputStream dis;
    private DataOutputStream dos;

    private String username;

    /**
     * Launch the application.
     */
   

    /**
     * Create the frame.
     */
    public LoginFrame() {
        setTitle("24/7 Chat");

        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(225,204,236));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(201,182,228));

        JLabel lbUsername = new JLabel("Username");
        lbUsername.setFont(new Font("Noto Sans", Font.BOLD, 16));

        JLabel lbPassword = new JLabel("Password");
        lbPassword.setFont(new Font("Noto Sans", Font.BOLD, 16));

        txtUsername = new JTextField();
        txtUsername.setColumns(10);

        txtPassword = new JPasswordField();

        JPanel buttons = new JPanel();
        buttons.setBackground(new Color(225,204,236));
        JPanel notificationContainer = new JPanel();
        notificationContainer.setBackground(new Color(225,204,236));
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addComponent(headerPanel, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addGap(69)
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(lbPassword)
                                                .addGap(18)
                                                .addComponent(txtPassword, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addComponent(lbUsername)
                                                .addGap(18)
                                                .addComponent(txtUsername, GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)))
                                .addGap(81))
                        .addComponent(notificationContainer, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                        .addComponent(buttons, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
        );
        gl_contentPane.setVerticalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addComponent(headerPanel, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
                                .addGap(33)
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(lbUsername)
                                        .addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(30)
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lbPassword))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(notificationContainer, GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(buttons, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
                                .addGap(22))
        );

        JLabel notification = new JLabel("");
        notification.setForeground(Color.RED);
        notification.setFont(new Font("Roboto", Font.PLAIN, 14));
        notificationContainer.add(notification);

        JButton login = new JButton("Log in");
        JButton signup = new JButton("Sign up");

        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String response = Login(txtUsername.getText(), String.copyValueOf(txtPassword.getPassword()));

                // ????ng nh???p th??nh c??ng th?? server s??? tr??? v???  chu???i "Log in successful"
                if ( response.equals("Log in successful") ) {
                    username = txtUsername.getText();
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            try {
                                ChatFrame frame = new ChatFrame(username, dis, dos);
                                frame.setVisible(true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    dispose();
                } else {
                    txtPassword.setText("");
                    notification.setText(response);
                }
            }
        });
        login.setEnabled(false);
        buttons.add(login);

        signup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                JPasswordField confirm = new JPasswordField();

                // Hi???n th??? h???p tho???i x??c nh???n password

                int action = JOptionPane.showConfirmDialog(null, confirm,"Comfirm your password",JOptionPane.OK_CANCEL_OPTION);
                if (action == JOptionPane.OK_OPTION) {
                    if (String.copyValueOf(confirm.getPassword()).equals(String.copyValueOf(txtPassword.getPassword()))) {
                        String response = Signup(txtUsername.getText(), String.copyValueOf(txtPassword.getPassword()));

                        // ????ng k?? th??nh c??ng th?? server s??? tr??? v???  chu???i "Log in successful"
                        if ( response.equals("Sign up successful") ) {
                            username = txtUsername.getText();
                            EventQueue.invokeLater(new Runnable() {
                                public void run() {
                                    try {
                                        // In ra th??ng b??o ????ng k?? th??nh c??ng
                                        int confirm = JOptionPane.showConfirmDialog(null, "Sign up successful\nWelcome to 24/7 Chat", "Sign up successful", JOptionPane.DEFAULT_OPTION);

                                        ChatFrame frame = new ChatFrame(username, dis, dos);
                                        frame.setVisible(true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            dispose();
                        } else {
                            login.setEnabled(false);
                            signup.setEnabled(false);
                            txtPassword.setText("");
                            notification.setText(response);
                        }
                    } else {
                        notification.setText("Confirm password does not match");
                    }
                }
            }
        });
        signup.setEnabled(false);
        
        buttons.add(signup);

        JLabel headerContent = new JLabel("LOG IN");
        headerContent.setFont(new Font("Roboto", Font.BOLD, 24));
        headerPanel.add(headerContent);
        contentPane.setLayout(gl_contentPane);

        txtUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (txtUsername.getText().isEmpty() || String.copyValueOf(txtPassword.getPassword()).isEmpty()) {
                    login.setEnabled(false);
                    signup.setEnabled(false);
                } else {
                    login.setEnabled(true);
                    signup.setEnabled(true);
                }
            }
        });

        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (txtUsername.getText().isEmpty()|| String.copyValueOf(txtPassword.getPassword()).isEmpty()) {
                    login.setEnabled(false);
                    signup.setEnabled(false);
                } else {
                    login.setEnabled(true);
                    signup.setEnabled(true);
                }
            }
        });

        this.getRootPane().setDefaultButton(login);
    }

   
    public String Login(String username, String password) {
        try {
            Connect();

            dos.writeUTF("Log in");
            dos.writeUTF(username);
            dos.writeUTF(password);
            dos.flush();

            String response = dis.readUTF();
            return response;

        } catch (IOException e) {
            e.printStackTrace();
            return "Network error: Log in fail";
        }
    }

    
    public String Signup(String username, String password) {
        try {
            Connect();

            dos.writeUTF("Sign up");
            dos.writeUTF(username);
            dos.writeUTF(password);
            dos.flush();

            String response = dis.readUTF();
            return response;

        } catch (IOException e) {
            e.printStackTrace();
            return "Network error: Sign up fail";
        }
    }

    
    public void Connect() {
        try {
            if (socket != null) {
                socket.close();
            }
            socket = new Socket(host, port);
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getUsername() {
        return this.username;
    }
     public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoginFrame frame = new LoginFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
