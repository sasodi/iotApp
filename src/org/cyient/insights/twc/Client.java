package org.cyient.insights.twc;

import com.thingworx.communications.client.ClientConfigurator;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.common.SecurityClaims;

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
        //set uri of thingworx server by taking input from user
        config.setUri(args[0]);
        // Reconnect every 15 seconds if a disconnect occurs or if initial connection cannot be made
        config.setReconnectInterval(15);

        // Set the security using an Application Key from second argument
        SecurityClaims claims = SecurityClaims.fromAppKey(args[1]);
        config.setSecurityClaims(claims);

        // Set the name of the client
        config.setName("bms");
        // This client is a SDK
        config.setAsSDKType();


    }
}
