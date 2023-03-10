/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clients;

/**
 *
 * @author Admin
 */
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;

/**
 * view
 * @Created by DELL - StudentID: 18120652
 * @Date Jul 14, 2020 - 8:36:36 PM
 * @Description ...
 */
public class ChatFrame extends JFrame {

    private JButton btnFile;
    private JButton btnSend;
    private JScrollPane chatPanel;
    private JLabel lbReceiver = new JLabel(" ");
    private JPanel contentPane;
    private JTextField txtMessage;
    private JTextPane chatWindow;
    JComboBox<String> onlineUsers = new JComboBox<String>();

    private String username;
    private DataInputStream dis;
    private DataOutputStream dos;

    private HashMap<String, JTextPane> chatWindows = new HashMap<String, JTextPane>();

    Thread receiver;

    private void autoScroll() {
        chatPanel.getVerticalScrollBar().setValue(chatPanel.getVerticalScrollBar().getMaximum());
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Insert a emoji into chat pane.
     */
    private void newEmoji(String username, String emoji, Boolean yourMessage) {

        StyledDocument doc;
        if (username.equals(this.username)) {
            doc = chatWindows.get(lbReceiver.getText()).getStyledDocument();
        } else {
            doc = chatWindows.get(username).getStyledDocument();
        }

        Style userStyle = doc.getStyle("User style");
        if (userStyle == null) {
            userStyle = doc.addStyle("User style", null);
            StyleConstants.setBold(userStyle, true);
        }

        if (yourMessage == true) {
            StyleConstants.setForeground(userStyle, Color.red);
        } else {
            StyleConstants.setForeground(userStyle, Color.BLUE);
        }

        // In ra m??n h??nh t??n ng?????i g???i
        try { doc.insertString(doc.getLength(), username + ": ", userStyle); }
        catch (BadLocationException e){}

        Style iconStyle = doc.getStyle("Icon style");
        if (iconStyle == null) {
            iconStyle = doc.addStyle("Icon style", null);
        }

        StyleConstants.setIcon(iconStyle, new ImageIcon(emoji));

        // In ra m??n h??nh Emoji
        try { doc.insertString(doc.getLength(), "invisible text", iconStyle); }
        catch (BadLocationException e){}

        // Xu???ng d??ng
        try { doc.insertString(doc.getLength(), "\n", userStyle); }
        catch (BadLocationException e){}

        autoScroll();
    }

    /**
     * Insert a file into chat pane.
     */
    private void newFile(String username, String filename, byte[] file, Boolean yourMessage) {

        StyledDocument doc;
        String window = null;
        if (username.equals(this.username)) {
            window = lbReceiver.getText();
        } else {
            window = username;
        }
        doc = chatWindows.get(window).getStyledDocument();

        Style userStyle = doc.getStyle("User style");
        if (userStyle == null) {
            userStyle = doc.addStyle("User style", null);
            StyleConstants.setBold(userStyle, true);
        }

        if (yourMessage == true) {
            StyleConstants.setForeground(userStyle, Color.red);
        } else {
            StyleConstants.setForeground(userStyle, Color.BLUE);
        }

        try { doc.insertString(doc.getLength(), username + ": ", userStyle); }
        catch (BadLocationException e){}

        Style linkStyle = doc.getStyle("Link style");
        if (linkStyle == null) {
            linkStyle = doc.addStyle("Link style", null);
            StyleConstants.setForeground(linkStyle, Color.BLUE);
            StyleConstants.setUnderline(linkStyle, true);
            StyleConstants.setBold(linkStyle, true);
            linkStyle.addAttribute("link", new HyberlinkListener(filename, file));
        }

        if (chatWindows.get(window).getMouseListeners() != null) {
            // T???o MouseListener cho c??c ???????ng d???n t???i v??? file
            chatWindows.get(window).addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e)
                {
                    Element ele = doc.getCharacterElement(chatWindow.viewToModel(e.getPoint()));
                    AttributeSet as = ele.getAttributes();
                    HyberlinkListener listener = (HyberlinkListener)as.getAttribute("link");
                    if(listener != null)
                    {
                        listener.execute();
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void mouseExited(MouseEvent e) {
                    // TODO Auto-generated method stub

                }

            });
        }

        // In ra ???????ng d???n t???i file
        try {
            doc.insertString(doc.getLength(),"<" + filename + ">", linkStyle);
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }

        // Xu???ng d??ng
        try {
            doc.insertString(doc.getLength(), "\n", userStyle);
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }

        autoScroll();
    }

    /**
     * Insert a new message into chat pane.
     */
    private void newMessage(String username, String message, Boolean yourMessage) {

        StyledDocument doc;
        if (username.equals(this.username)) {
            doc = chatWindows.get(lbReceiver.getText()).getStyledDocument();
        } else {
            doc = chatWindows.get(username).getStyledDocument();
        }

        Style userStyle = doc.getStyle("User style");
        if (userStyle == null) {
            userStyle = doc.addStyle("User style", null);
            StyleConstants.setBold(userStyle, true);
        }

        if (yourMessage == true) {
            StyleConstants.setForeground(userStyle, Color.red);
        } else {
            StyleConstants.setForeground(userStyle, Color.BLUE);
        }

        // In ra t??n ng?????i g???i
        try { doc.insertString(doc.getLength(), username + ": ", userStyle); }
        catch (BadLocationException e){}

        Style messageStyle = doc.getStyle("Message style");
        if (messageStyle == null) {
            messageStyle = doc.addStyle("Message style", null);
            StyleConstants.setForeground(messageStyle, Color.BLACK);
            StyleConstants.setBold(messageStyle, false);
        }

        // In ra n???i dung tin nh???n
        try { doc.insertString(doc.getLength(), message + "\n",messageStyle); }
        catch (BadLocationException e){}

        autoScroll();
    }

    /**
     * Create the frame.
     */
    public ChatFrame(String username, DataInputStream dis, DataOutputStream dos) {
        setTitle("24/7 Chat");
        this.username = username;
        this.dis = dis;
        this.dos = dos;
        receiver = new Thread(new Receiver(dis));
        receiver.start();

        setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 586, 450);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setBackground(new Color(201,182,228));
        setContentPane(contentPane);

        JPanel header = new JPanel();
        header.setBackground(new Color(190,159,225));

        txtMessage = new JTextField();
        txtMessage.setEnabled(false);
        txtMessage.setColumns(10);

        btnSend = new JButton("");
        btnSend.setEnabled(false);
        btnSend.setIcon(new ImageIcon("src\\common\\icon\\component\\send.png"));

        chatPanel = new JScrollPane();
        chatPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(241,241,246));

        btnFile = new JButton("");
        btnFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                // Hi???n th??? h???p tho???i cho ng?????i d??ng ch???n file ????? g???i
                JFileChooser fileChooser = new JFileChooser();
                int rVal = fileChooser.showOpenDialog(contentPane.getParent());
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    byte[] selectedFile = new byte[(int) fileChooser.getSelectedFile().length()];
                    BufferedInputStream bis;
                    try {
                        bis = new BufferedInputStream(new FileInputStream(fileChooser.getSelectedFile()));
                        // ?????c file v??o bi???n selectedFile
                        bis.read(selectedFile, 0, selectedFile.length);

                        dos.writeUTF("File");
                        dos.writeUTF(lbReceiver.getText());
                        dos.writeUTF(fileChooser.getSelectedFile().getName());
                        dos.writeUTF(String.valueOf(selectedFile.length));

                        int size = selectedFile.length;
                        int bufferSize = 2048;
                        int offset = 0;

                        // L???n l?????t g???i cho server t???ng buffer cho ?????n khi h???t file
                        while (size > 0) {
                            dos.write(selectedFile, offset, Math.min(size, bufferSize));
                            offset += Math.min(size, bufferSize);
                            size -= bufferSize;
                        }

                        dos.flush();

                        bis.close();

                        // In ra m??n h??nh file
                        newFile(username, fileChooser.getSelectedFile().getName(), selectedFile, true);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        btnFile.setEnabled(false);
        btnFile.setIcon(new ImageIcon("src\\common\\icon\\component\\attach.png"));

        JPanel emojis = new JPanel();
        emojis.setBackground(new Color(190,159,225));
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addComponent(header, GroupLayout.DEFAULT_SIZE, 560, Short.MAX_VALUE)
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addComponent(leftPanel, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                                        .addComponent(emojis, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addComponent(txtMessage, GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(btnFile, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(chatPanel, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)))
        );
        gl_contentPane.setVerticalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addComponent(header, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addComponent(chatPanel, GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(emojis, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
                                                        .addComponent(btnSend, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnFile, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtMessage, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)))
                                        .addComponent(leftPanel, GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)))
        );

        JLabel smileIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\smile.png"));
        smileIcon.addMouseListener(new IconListener(smileIcon.getIcon().toString()));
        emojis.add(smileIcon);

        JLabel bigSmileIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\big-smile.png"));
        bigSmileIcon.addMouseListener(new IconListener(bigSmileIcon.getIcon().toString()));
        emojis.add(bigSmileIcon);

        JLabel happyIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\happy.png"));
        happyIcon.addMouseListener(new IconListener(happyIcon.getIcon().toString()));
        emojis.add(happyIcon);

        JLabel loveIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\love.png"));
        loveIcon.addMouseListener(new IconListener(loveIcon.getIcon().toString()));
        emojis.add(loveIcon);

        JLabel sadIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\sad.png"));
        sadIcon.addMouseListener(new IconListener(sadIcon.getIcon().toString()));
        emojis.add(sadIcon);

        JLabel madIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\mad.png"));
        madIcon.addMouseListener(new IconListener(madIcon.getIcon().toString()));
        emojis.add(madIcon);

        JLabel suspiciousIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\suspicious.png"));
        suspiciousIcon.addMouseListener(new IconListener(suspiciousIcon.getIcon().toString()));
        emojis.add(suspiciousIcon);

        JLabel angryIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\angry.png"));
        angryIcon.addMouseListener(new IconListener(angryIcon.getIcon().toString()));
        emojis.add(angryIcon);

        JLabel confusedIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\confused.png"));
        confusedIcon.addMouseListener(new IconListener(confusedIcon.getIcon().toString()));
        emojis.add(confusedIcon);

        JLabel unhappyIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\unhappy.png"));
        unhappyIcon.addMouseListener(new IconListener(unhappyIcon.getIcon().toString()));
        emojis.add(unhappyIcon);

        JLabel appleIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\apple.png"));
        appleIcon.addMouseListener(new IconListener(appleIcon.getIcon().toString()));
        emojis.add(appleIcon);

        JLabel orangeIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\orange.png"));
        orangeIcon.addMouseListener(new IconListener(orangeIcon.getIcon().toString()));
        emojis.add(orangeIcon);

        JLabel cherryIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\cherry.png"));
        cherryIcon.addMouseListener(new IconListener(cherryIcon.getIcon().toString()));
        emojis.add(cherryIcon);

        JLabel cakeIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\cake.png"));
        cakeIcon.addMouseListener(new IconListener(cakeIcon.getIcon().toString()));
        emojis.add(cakeIcon);

        JLabel vietnamIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\vietnam.png"));
        vietnamIcon.addMouseListener(new IconListener(vietnamIcon.getIcon().toString()));
        emojis.add(vietnamIcon);

        JLabel usIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\us.png"));
        usIcon.addMouseListener(new IconListener(usIcon.getIcon().toString()));
        emojis.add(usIcon);

        JLabel ukIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\uk.png"));
        ukIcon.addMouseListener(new IconListener(ukIcon.getIcon().toString()));
        emojis.add(ukIcon);

        JLabel canadaIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\canadaIcon.png"));
        canadaIcon.addMouseListener(new IconListener(canadaIcon.getIcon().toString()));
        emojis.add(canadaIcon);

        JLabel italyIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\italy.png"));
        italyIcon.addMouseListener(new IconListener(italyIcon.getIcon().toString()));
        emojis.add(italyIcon);

        JLabel spainIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\spainIcon.png"));
        spainIcon.addMouseListener(new IconListener(spainIcon.getIcon().toString()));
        emojis.add(spainIcon);

        JLabel egyptIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\egyptIcon.png"));
        egyptIcon.addMouseListener(new IconListener(egyptIcon.getIcon().toString()));
        emojis.add(egyptIcon);

        JLabel swedenIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\sweden.png"));
        swedenIcon.addMouseListener(new IconListener(swedenIcon.getIcon().toString()));
        emojis.add(swedenIcon);

        JLabel australiaIcon = new JLabel(new ImageIcon("src\\common\\icon\\emoji\\australia.png"));
        australiaIcon.addMouseListener(new IconListener(australiaIcon.getIcon().toString()));
        emojis.add(australiaIcon);

        JLabel userImage = new JLabel(new ImageIcon("src\\common\\icon\\component\\user.png"));

        JPanel panel = new JPanel();
        panel.setBackground(new Color(201,182,228));
        JLabel lblNewLabel_1 = new JLabel("Online List");
        lblNewLabel_1.setFont(new Font("Noto Sans", Font.BOLD, 12));
        GroupLayout gl_leftPanel = new GroupLayout(leftPanel);
        gl_leftPanel.setHorizontalGroup(
                gl_leftPanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_leftPanel.createSequentialGroup()
                                .addGap(25)
                                .addComponent(userImage, GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                                .addGap(25))
                        .addGroup(gl_leftPanel.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panel, GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(gl_leftPanel.createSequentialGroup()
                                .addGap(28)
                                .addComponent(lblNewLabel_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(29))
                        .addGroup(Alignment.TRAILING, gl_leftPanel.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(onlineUsers, 0, 101, Short.MAX_VALUE)
                                .addContainerGap())
        );
        onlineUsers.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    lbReceiver.setText((String) onlineUsers.getSelectedItem());
                    if (chatWindow != chatWindows.get(lbReceiver.getText())) {
                        txtMessage.setText("");
                        chatWindow = chatWindows.get(lbReceiver.getText());
                        chatPanel.setViewportView(chatWindow);
                        chatPanel.validate();
                    }

                    if (lbReceiver.getText().isEmpty()) {
                        btnSend.setEnabled(false);
                        btnFile.setEnabled(false);
                        txtMessage.setEnabled(false);
                    } else {
                        btnSend.setEnabled(true);
                        
                        
                        btnFile.setEnabled(true);
                        txtMessage.setEnabled(true);
                    }
                }

            }
        });

        gl_leftPanel.setVerticalGroup(
                gl_leftPanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_leftPanel.createSequentialGroup()
                                .addGap(5)
                                .addComponent(userImage)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(panel, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
                                .addGap(41)
                                .addComponent(lblNewLabel_1)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(onlineUsers, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(104, Short.MAX_VALUE))
        );

        JLabel lbUsername = new JLabel(this.username);
        lbUsername.setFont(new Font("Noto Sans", Font.BOLD, 14));
        panel.add(lbUsername);
        leftPanel.setLayout(gl_leftPanel);

        JLabel headerContent = new JLabel("24/7 Chat");
        headerContent.setFont(new Font("Noto Sans", Font.BOLD, 24));
        header.add(headerContent);

        JPanel usernamePanel = new JPanel();
        usernamePanel.setBackground(new Color(225,204,236));
        chatPanel.setColumnHeaderView(usernamePanel);

        lbReceiver.setFont(new Font("Noto Sans", Font.BOLD, 16));
        usernamePanel.add(lbReceiver);

        chatWindows.put(" ", new JTextPane());
        chatWindow = chatWindows.get(" ");
        chatWindow.setFont(new Font("Noto Sans", Font.PLAIN, 14));
        chatWindow.setEditable(false);

        chatPanel.setViewportView(chatWindow);
        contentPane.setLayout(gl_contentPane);

        txtMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (txtMessage.getText().isEmpty() || lbReceiver.getText().isEmpty()) {
                    btnSend.setEnabled(false);
                } else {
                    btnSend.setEnabled(true);
                }
            }
        });

        onlineUsers.addActionListener (new ActionListener () {
    public void actionPerformed(ActionEvent e) {
//        System.out.println("combobox: "+ onlineUsers.getSelectedItem().toString());
       
//        System.out.println("selected: "+ selected);
        if(onlineUsers.getSelectedItem() != null){
             String selected = onlineUsers.getSelectedItem().toString();
            try{
                dos.writeUTF("selectUser");
            dos.writeUTF(onlineUsers.getSelectedItem().toString());
            dos.writeUTF(username);
            }catch (IOException e1){
             e1.printStackTrace();
                    newMessage("ERROR" , "Network error!" , true);
            }
        }
    }
});
        // Set action perform to send button.
        btnSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("username"+  username );
                try {
                    dos.writeUTF("Text");
                    dos.writeUTF(lbReceiver.getText());
                    dos.writeUTF(txtMessage.getText());
                    dos.writeUTF(username);
                    dos.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    newMessage("ERROR" , "Network error!" , true);
                }

                // In ra tin nh???n l??n m??n h??nh chat v???i ng?????i nh???n
                newMessage(username , txtMessage.getText() , true);
                txtMessage.setText("");
            }
        });

        this.getRootPane().setDefaultButton(btnSend);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {

                try {
                    dos.writeUTF("Log out");
                    dos.flush();

                    try {
                        receiver.join();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                    if (dos != null) {
                        dos.close();
                    }
                    if (dis != null) {
                        dis.close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

    }

    /**
     * Lu???ng nh???n tin nh???n t??? server c???a m???i client
     */
    class Receiver implements Runnable{

        private DataInputStream dis;

        public Receiver(DataInputStream dis) {
            this.dis = dis;
        }

        @Override
        public void run() {
            try {

                while (true) {
                    // Ch??? tin nh???n t??? server
                    String method = dis.readUTF();

                    if (method.equals("Text")) {
                        // Nh???n m???t tin nh???n v??n b???n
                        String sender =	dis.readUTF();
                        String message = dis.readUTF();

                        // In tin nh???n l??n m??n h??nh chat v???i ng?????i g???i
                        newMessage(sender, message, false);
                    }

                    else if (method.equals("Emoji")) {
                        // Nh???n m???t tin nh???n Emoji
                        String sender = dis.readUTF();
                        String emoji = dis.readUTF();

                        // In tin nh???n l??n m??n h??nh chat v???i ng?????i g???i
                        newEmoji(sender, emoji, false);
                    }

                    else if (method.equals("File")) {
                        // Nh???n m???t file
                        String sender = dis.readUTF();
                        String filename = dis.readUTF();
                        int size = Integer.parseInt(dis.readUTF());
                        int bufferSize = 2048;
                        byte[] buffer = new byte[bufferSize];
                        ByteArrayOutputStream file = new ByteArrayOutputStream();

                        while (size > 0) {
                            dis.read(buffer, 0, Math.min(bufferSize, size));
                            file.write(buffer, 0, Math.min(bufferSize, size));
                            size -= bufferSize;
                        }

                        // In ra m??n h??nh file ????
                        newFile(sender, filename, file.toByteArray(), false);

                    }

                    else if (method.equals("Online users")) {
                        // Nh???n y??u c???u c???p nh???t danh s??ch ng?????i d??ng tr???c tuy???n
                        String[] users = dis.readUTF().split(",");
                        onlineUsers.removeAllItems();

                        String chatting = lbReceiver.getText();

                        boolean isChattingOnline = false;

                        for (String user: users) {
                            if (user.equals(username) == false) {
                                // C???p nh???t danh s??ch c??c ng?????i d??ng tr???c tuy???n v??o ComboBox onlineUsers (tr??? b???n th??n)
                                onlineUsers.addItem(user);
                                if (chatWindows.get(user) == null) {
                                    JTextPane temp = new JTextPane();
                                    temp.setFont(new Font("Noto Sans", Font.PLAIN, 14));
                                    temp.setEditable(false);
                                    chatWindows.put(user, temp);
                                }
                            }
                            if (chatting.equals(user)) {
                                isChattingOnline = true;
                            }
                        }

                        if (isChattingOnline == false) {
                            // N???u ng?????i ??ang chat kh??ng online th?? chuy???n h?????ng v??? m??n h??nh m???c ?????nh v?? th??ng b??o cho ng?????i d??ng
                            onlineUsers.setSelectedItem(" ");
                            JOptionPane.showMessageDialog(null, chatting + " is offline!\nYou will be redirect to default chat window");
                        } else {
                            onlineUsers.setSelectedItem(chatting);
                        }

                        onlineUsers.validate();
                    }

                    else if (method.equals("Safe to leave")) {
                        // Th??ng b??o c?? th??? tho??t
                        break;
                    }

                }

            } catch(IOException ex) {
                System.err.println(ex);
            } finally {
                try {
                    if (dis != null) {
                        dis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * MouseListener cho c??c ???????ng d???n t???i file.
     */
    class HyberlinkListener extends AbstractAction {
        String filename;
        byte[] file;

        public HyberlinkListener(String filename, byte[] file) {
            this.filename = filename;
            this.file = Arrays.copyOf(file, file.length);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            execute();
        }

        public  void execute() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(filename));
            int rVal = fileChooser.showSaveDialog(contentPane.getParent());
            if (rVal == JFileChooser.APPROVE_OPTION) {

                // M??? file ???? ch???n sau ???? l??u th??ng tin xu???ng file ????
                File saveFile = fileChooser.getSelectedFile();
                BufferedOutputStream bos = null;
                try {
                    bos = new BufferedOutputStream(new FileOutputStream(saveFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                // Hi???n th??? JOptionPane cho ng?????i d??ng c?? mu???n m??? file v???a t???i v??? kh??ng
                int nextAction = JOptionPane.showConfirmDialog(null, "Saved file to " + saveFile.getAbsolutePath() + "\nDo you want to open this file?", "Successful", JOptionPane.YES_NO_OPTION);
                if (nextAction == JOptionPane.YES_OPTION) {
                    try {
                        Desktop.getDesktop().open(saveFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (bos != null) {
                    try {
                        bos.write(this.file);
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * MouseAdapter cho c??c Emoji.
     */
    class IconListener extends MouseAdapter {
        String emoji;

        public IconListener(String emoji) {
            this.emoji = emoji;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (txtMessage.isEnabled() == true) {

                try {
                    dos.writeUTF("Emoji");
                    dos.writeUTF(lbReceiver.getText());
                    dos.writeUTF(this.emoji);
                    dos.writeUTF(username);
                    dos.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    newMessage("ERROR" , "Network error!" , true);
                }

                // In Emoji l??n m??n h??nh chat v???i ng?????i nh???n
                newEmoji(username, this.emoji, true);
            }
        }
    }
}
