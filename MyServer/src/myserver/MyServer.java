/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import pages.ServerUI;
import service.AcceptClients;

/**
 *
 * @author PRIYARANJAN
 */
public class MyServer {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        AcceptClients ac = new AcceptClients("localhost", 1234);
        ServerUI.MonotorArea.append("Server Stared at ip: 127.0.0.1 and port: 1234");
        ac.start();
    }
}
