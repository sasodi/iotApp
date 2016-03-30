package org.cyient.insights.twc;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.transport.Transport;
import com.thingworx.communications.client.ClientConfigurator;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.communications.common.SecurityClaims;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sriky on 29/3/16.
 *
 * Main file that defines client and connects to thingworks.
 *
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

        Client client = new Client(config);

        IpNetwork network = new IpNetwork("192.168.168.255", 47808, "192.168.168.120");
        Transport transport = new DefaultTransport(network);
        LocalDevice localDevice = new LocalDevice(23472, transport);
        List<Node> listOfNodes = new ArrayList<Node>();
        try {
            localDevice.initialize();
        /* create a list of blocks initialize and bind them. need to use the node and device classes.*/
        /*for each device in the list initiate the node objects*/
        for(int deviceID: Devices.getAsList()) {
            listOfNodes.add(new Node(localDevice, deviceID, Devices.getActiveNames()));
            System.out.println("processing device: " + deviceID);
        }

        for(Node node: listOfNodes){
            client.bindThing(new Thing(node.getBlockName(),node.getBlockName(),node.getBlockName(),client,node,localDevice));
        }


            // Start the client
            client.start();

        }
        catch(Exception eStart) {
            System.out.println("Initial Start Failed : " + eStart.getMessage());
        }

        /* loop over all the blocks and collect and set data.*/
        // As long as the client has not been shutdown, continue
        while(!client.isShutdown()) {
            // Only process the Virtual Things if the client is connected
            if(client.isConnected()) {
                // Loop over all the Virtual Things and process them
                for(VirtualThing thing : client.getThings().values()) {
                    try {
                        thing.processScanRequest();
                    }
                    catch(Exception eProcessing) {
                        System.out.println("Error Processing Scan Request for [" + thing.getName() + "] : " + eProcessing.getMessage());
                    }
                }
            }
            // Suspend processing at the scan rate interval
            Thread.sleep(1000);
        }

    }
}
