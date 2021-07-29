/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myclient.Sevices;

import MessageParser.ParseMsg;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.json.JSONArray;

/**
 *
 * @author PRIYARANJAN
 */
public class ClientService extends Thread {

    Socket socket;

    private InetAddress ipAddress;
    private int port;

    DataOutputStream writer;
    DataInputStream reader;

    ParseMsg parsemsg;

    public ClientService(String ip, int p) {
        try {
            socket = new Socket();
            this.ipAddress = InetAddress.getByName(ip);
            this.port = p;
            socket.connect(new InetSocketAddress(ipAddress, port));

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            reader = new DataInputStream(in);
            writer = new DataOutputStream(out);
            
            parsemsg = new ParseMsg();
        } catch (Exception e) {
            System.out.println("Client Service Error :" + e.getMessage());
        }
    }

    public void sendData(String msg) {
        try {
            writer.writeUTF(msg);
            writer.flush();
        } catch (IOException ex) {
            System.out.println("send message Error :" + ex.getMessage());
        }
    }

    public void sendLoginMsg(String username) {
        String loginMsg = "{"
                + "  \"data\": ["
                + "    {"
                + "      \"username\": \"" + username + "\","
                + "      \"type\" : \"login\","
                + "      \"receivers\" : \"[]\","
                + "      \"message\" : \"set username\""
                + "    },"
                + "  ]"
                + "}";
        sendData(loginMsg);
    }

    public void sendLogoutMsg(String username) throws IOException {
        String logoutMsg = "logout";
        sendData(logoutMsg);
        socket.close();
    }

    public void sendMessage(String message, ArrayList<String> arr, String username, String selected) {
        JSONArray rcvrs = new JSONArray(arr);
        String msgdata = "{"
                + "  \"data\": ["
                + "    {"
                + "      \"sender\": \"" + username + "\","
                + "      \"remark\": \"textmsg\","
                + "      \"type\" : \"" + selected + "\","
                + "      \"receivers\" : " + rcvrs + ","
                + "      \"message\" : \"" + message + "\""
                + "    },"
                + "  ]"
                + "}";
        sendData(msgdata);
    }

    public void sendEmoji(String type, ArrayList<String> arr, String username, File file) {
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fileInputStreamReader.read(bytes);
            String imgfile = new String(Base64.getEncoder().encodeToString(bytes));
            
            JSONArray rcvrs = new JSONArray(arr);
            String data = "{"
                    + "  \"data\": ["
                    + "    {"
                    + "      \"sender\": \"" + username + "\","
                    + "      \"remark\": \"emoji\","
                    + "      \"type\" : \"" + type + "\","
                    + "      \"receivers\" : " + rcvrs + ","
                    + "      \"message\" : \" " + imgfile + "\""
                    + "    },"
                    + "  ]"
                    + "}";
            sendData(data);
        } catch (Exception ex) {
            System.out.println("Send error emoji");
        }
    }

    public void run() {
        try {
            //receve message
            while (true) {
                String rcvdMsg = reader.readUTF();
                System.out.println("from server : " + rcvdMsg);
                parsemsg.ParseUserMsg(rcvdMsg);
            }
        } catch (IOException ex) {
            System.out.println("run read msg Error :" + ex.getMessage());
        }
    }
}
