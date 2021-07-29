/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myserver.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import messageparser.MessageParser;
import pages.ServerUI;

/**
 *
 * @author PRIYARANJAN
 */
public class ClientModel extends Thread {

    Socket socket;
    String username = "username";

    DataInputStream reader;
    DataOutputStream writer;
    
    boolean isNewUser = true;
    MessageParser parser;
    

    public ClientModel(Socket s) throws IOException {
        socket = s;
        writer = new DataOutputStream(socket.getOutputStream());
        reader = new DataInputStream(socket.getInputStream());
        parser = new MessageParser();
    }

    //Send message to client
    public void sendMessage(String msg) {
        try {
            writer.writeUTF(msg);
            writer.flush();
        } catch (IOException ex) {
            System.out.println("send message server :" + ex.getMessage());
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
    
    public String getIPAddress() {
        String ip = socket.getInetAddress().toString();
        int port = socket.getPort();
        
        return ip + " : " + port;
    }
    
    // Accept message from client
    public void run() {
        try {
            while (true) {
                String recvdMsg = reader.readUTF();
                System.out.println(getIPAddress() + " message: " +recvdMsg);
                ServerUI.MonotorArea.append("\n"+getIPAddress() + " message: " +recvdMsg);
                if(recvdMsg.equals("logout")){
                    parser.removeUserOnLogout(this);
                    break;
                }
                
                if(isNewUser){
                    parser.forSetUsername(this, recvdMsg);
                    isNewUser = false;
                }
                else{
                    parser.ParseMessage(recvdMsg);
                }
            }
        } catch (Exception e) {
            System.out.println("run client model " + e.getMessage());
        }
    }
}
