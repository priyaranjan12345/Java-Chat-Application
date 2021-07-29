/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import controller.ClientList;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import myserver.models.ClientModel;
import pages.ServerUI;

/**
 *
 * @author PRIYARANJAN
 */
public class AcceptClients extends Thread {

    public static boolean keepRunning = true;
    private static ServerSocket serverSocket;
    private String ip;
    private int port;

    public AcceptClients(String ip, int port) {
        System.out.println("Server Stared...");
        ServerUI.MonotorArea.append("\nServer Stared...");
        this.port = port;
        this.ip = ip;
    }

    public void run() {
        System.out.println("Now clients can connect to this server...");
        ServerUI.MonotorArea.append("\nNow clients can connect to this server...");
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));
            do {
                Socket socket = serverSocket.accept();
                ServerUI.MonotorArea.append("\n\nAccept a new client : "+socket.getRemoteSocketAddress());
                ClientModel model = new ClientModel(socket);
                ClientList.addClients(model);
                model.start();
                ClientList.display();
            } while (keepRunning);
        } catch (IOException ex) {
            System.out.println("My Server runServer Error : " + ex);
            ServerUI.MonotorArea.append(ex.getMessage());
        }
    }

    public static synchronized void sendToAll(String message) {
        ClientList.clients.stream().forEach((client) -> {
            client.sendMessage(message);
        });
    }

    public static synchronized void sendToSome(ArrayList<String> recipients, String message) {
        for (ClientModel client : ClientList.clients) {
            if (client.getUsername() != null && recipients.contains(client.getUsername())) {
                client.sendMessage(message);
            }
        }
    }
    
    public void finalize() {
        System.out.println("Object is garbage collected.");
    }

}
