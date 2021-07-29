/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MessageParser;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import myclient.Pages.ChatUserInterface;
import static myclient.Pages.ChatUserInterface.msgarea;
import static myclient.Pages.ChatUserInterface.userTitle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sun.misc.BASE64Decoder;

/**
 *
 * @author PRIYARANJAN
 */
public class ParseMsg {

    JSONObject jsonObj;
    JSONArray data;
    JSONObject userdata;
    private DefaultListModel<String> userListModel = new DefaultListModel<>();

    public ParseMsg() {

    }

    public void ParseUserMsg(String msg) {
        try {
            jsonObj = new JSONObject(msg);
            data = jsonObj.getJSONArray("data");
            userdata = data.getJSONObject(0);

            String remark = (String) userdata.get("remark");

            switch (remark) {
                case "textmsg":
                    parseTextMsg();
                    break;
                case "emoji":
                    parseEmoji();
                    break;
                case "users_name":
                    parseUserName();
                    break;
            }

        } catch (Exception e) {
            System.out.println("Error client parse msg : " + e.getMessage());
        }
    }

    public void parseUserName() {
        try {
            String myname = userTitle.getText();
            JSONArray users_name = (JSONArray) userdata.get("usernames");
            ArrayList<String> users = new ArrayList<>();
            for (int i = 0; i < users_name.length(); i++) {
                users.add(users_name.getString(i));
            }
            userListModel.clear();
            for (String user : users) {
                userListModel.addElement(user);
            }
            if (!userListModel.isEmpty()) {
                userListModel.removeElement(myname);
            }

            ChatUserInterface.ListUser.setModel(userListModel);
        } catch (JSONException ex) {
            System.out.println("error parse users name : " + ex.getMessage());
        }
    }

    public void parseTextMsg() {
        try {
            String myname = userTitle.getText();
            String sender_name = (String) userdata.get("sender");
            String user_type = (String) userdata.get("type");
            String msg = (String) userdata.get("message");

            JSONArray recevers = (JSONArray) userdata.get("receivers");
            ArrayList<String> receivers = new ArrayList<>();
            for (int i = 0; i < recevers.length(); i++) {
                receivers.add(recevers.getString(i));
            }

            if (sender_name.equals(myname)) {
                sender_name = "Me";
            }

            int noofrcvers = receivers.size();
            if (user_type.equals("All")) {
                insertDoc("\n" + sender_name + " : \n", styleTextSender());
            } else if (noofrcvers == 2) {
                insertDoc("\n" + sender_name + " : \n", styleTextSenderPersonal());
            } else {
                insertDoc("\n" + sender_name + " : \n", styleTextSenderSelected());
            }
            
            if (sender_name.equals("Me")) {
                insertDoc(msg + "\n", styleTextMsgMe());
            } else {
                insertDoc(msg + "\n", styleTextMsg());
            }
        } catch (JSONException ex) {
            System.out.println("error text msg parse : " + ex.getMessage());
        }
    }

    public void parseEmoji() {
        try {
            String myname = userTitle.getText();
            String sender_name = (String) userdata.get("sender");
            String user_type = (String) userdata.get("type");
            String imgfile = (String) userdata.get("message");

            BASE64Decoder decoder = new BASE64Decoder();
            byte[] imgBytes = decoder.decodeBuffer(imgfile.trim());
            ByteArrayInputStream bis = new ByteArrayInputStream(imgBytes);
            BufferedImage bufImg = ImageIO.read(bis);

            if (sender_name.equals(myname)) {
                sender_name = "Me";
            }

            insertDoc("\n" + sender_name + " : \n", styleTextSender());

            ImageIcon img = new ImageIcon(bufImg);
            insertImg(img);
            bis.close();
        } catch (Exception ex) {
            System.out.println("parsemsg client error " + ex.getMessage());
        }
    }

    public SimpleAttributeSet styleTextSenderSelected() {
        SimpleAttributeSet sender = new SimpleAttributeSet();
        StyleConstants.setBold(sender, true);
        StyleConstants.setFontSize(sender, 16);
        StyleConstants.setBackground(sender, Color.yellow);
        StyleConstants.setForeground(sender, Color.black);

        return sender;
    }

    public SimpleAttributeSet styleTextSenderPersonal() {
        SimpleAttributeSet sender = new SimpleAttributeSet();
        StyleConstants.setBold(sender, true);
        StyleConstants.setFontSize(sender, 16);
        StyleConstants.setBackground(sender, Color.green);
        StyleConstants.setForeground(sender, Color.black);

        return sender;
    }

    public SimpleAttributeSet styleTextSender() {
        SimpleAttributeSet sender = new SimpleAttributeSet();
        StyleConstants.setBold(sender, true);
        StyleConstants.setFontSize(sender, 16);
        StyleConstants.setBackground(sender, Color.orange);
        StyleConstants.setForeground(sender, Color.black);

        return sender;
    }
    
    public SimpleAttributeSet styleTextMsgMe() {
        SimpleAttributeSet message = new SimpleAttributeSet();
        StyleConstants.setForeground(message, Color.blue);

        return message;
    }

    public SimpleAttributeSet styleTextMsg() {
        SimpleAttributeSet message = new SimpleAttributeSet();
        StyleConstants.setForeground(message, Color.blue);

        return message;
    }

    public void setInsertionDocEnd() {
        Document doc = msgarea.getDocument();
        int currentDocLength = doc.getLength();
        msgarea.setSelectionStart(currentDocLength);
        msgarea.setSelectionEnd(currentDocLength);
    }

    public void insertImg(ImageIcon img) {
        setInsertionDocEnd();
        msgarea.insertIcon(img);
    }

    public void insertDoc(String txt, SimpleAttributeSet style) {
        setInsertionDocEnd();
        Document doc = msgarea.getDocument();
        try {
            doc.insertString(doc.getLength(), txt, style);
        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
        }
    }
}
