/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messageparser;

import controller.ClientList;
import static controller.ClientList.clients;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import myserver.models.ClientModel;
import org.json.*;
import static service.AcceptClients.*;

/**
 *
 * @author PRIYARANJAN
 */
public class MessageParser {

    JSONObject jsonObj;
    JSONArray data;
    JSONObject userdata;

    public void forSetUsername(ClientModel model, String msg) {
        try {
            jsonObj = new JSONObject(msg);
            data = jsonObj.getJSONArray("data");
            userdata = data.getJSONObject(0);

            String uname = (String) userdata.get("username");
            model.setUsername(uname);

            sendClientsList();
        } catch (JSONException ex) {
            System.out.println("for set Username in side parser :" + ex.getMessage());
        }
    }

    public void sendClientsList() {
        ArrayList<String> users = new ArrayList<>();
        for (ClientModel client : ClientList.clients) {
            users.add(client.getUsername());
        }
        JSONArray usernames = new JSONArray(users);
        String clients_name = "{"
                + "  \"data\": ["
                + "    {"
                + "      \"remark\" : \"users_name\","
                + "      \"type\" : \"names\","
                + "      \"usernames\" : " + usernames + ","
                + "    },"
                + "  ]"
                + "}";
        sendToAll(clients_name);
    }

    public void removeUserOnLogout(ClientModel clientmodel) {
        ClientList.clients.remove(clientmodel);
        sendClientsList();
    }

    public void ParseMessage(String msgData) {
        try {
            jsonObj = new JSONObject(msgData);
            data = jsonObj.getJSONArray("data");
            userdata = data.getJSONObject(0);
            
            String userType = (String) userdata.get("type");
            
            if (userType.equals("All")) {
                sendToAll(msgData);
            } else {
                JSONArray arr = (JSONArray) userdata.get("receivers");
                String msg = (String) userdata.get("message");

                ArrayList<String> users = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    users.add(arr.getString(i));
                }

                sendToSome(users, msgData);
            }
        } catch (JSONException ex) {
            System.out.println("Error in Message parse retrive :" + ex.getMessage());
        }
    }
}
