/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.concurrent.CopyOnWriteArrayList;
import myserver.models.ClientModel;

/**
 *
 * @author PRIYARANJAN
 */
public class ClientList {
    public static CopyOnWriteArrayList<ClientModel> clients = new CopyOnWriteArrayList<>();

    public static void addClients(ClientModel model) {
        clients.add(model);
    }

    public static CopyOnWriteArrayList<ClientModel> getClients() {
        return clients;
    }
    
    public static void display(){
        System.out.println("Clients : "+clients);
        
        for(int i=0; i<clients.size(); i++){
            System.out.println(clients.get(i).getIPAddress());
            System.out.println(clients.get(i).getUsername()+"\n");
        }
    }
}
