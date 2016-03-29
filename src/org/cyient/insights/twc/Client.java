package org.cyient.insights.twc;

import com.thingworx.communications.client.ClientConfigurator;
import com.thingworx.communications.client.ConnectedThingClient;

/**
 * Created by sriky on 29/3/16.
 */
public class Client extends ConnectedThingClient {
    public Client(ClientConfigurator config) throws Exception {
        super(config);
    }

    public static void main(String[] args) throws Exception {
        // Set the required configuration information
        ClientConfigurator config = new ClientConfigurator();
        //set uri of thingworx server

    }
}
