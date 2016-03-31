package org.cyient.insights.twc;

/**
 * Created by sriky on 30/3/16.
 */

import com.serotonin.bacnet4j.LocalDevice;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinitions;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.metadata.collections.FieldDefinitionCollection;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.constants.CommonPropertyNames;
import com.thingworx.types.primitives.structs.Location;
import org.joda.time.DateTime;

import java.util.Map;

// Property Definitions
@SuppressWarnings("serial")
@ThingworxPropertyDefinitions(properties = {
        @ThingworxPropertyDefinition(name="ratemp", description="Return air Temperature", baseType="NUMBER", category="block", aspects={"isReadOnly:true"}),
        @ThingworxPropertyDefinition(name="satemp", description="Supply air Pressure", baseType="NUMBER", category="block", aspects={"isReadOnly:true"}),
        @ThingworxPropertyDefinition(name="raco2", description="return air co2", baseType="NUMBER", category="block", aspects={"isReadOnly:true"}),
        @ThingworxPropertyDefinition(name="occur_ts", description="time occured", baseType="DATETIME", category="block", aspects={"isReadOnly:true"}),
        @ThingworxPropertyDefinition(name="location", description="Where is this block located", baseType="LOCATION", category="block", aspects={"isReadOnly:true"}),

})
public class Thing extends VirtualThing implements Runnable  {
    private Map<String,Float> values = null;
    private Thread _shutdownThread = null;
    private Node node = null;
    private LocalDevice localDevice =null;
    private Location  locate=  new Location(17.4188203,78.3369446,19.96);
    public Thing(String name, String description, String identifier, ConnectedThingClient client, Node node, LocalDevice localDevice) {
        super(name,description,identifier,client);
        super.initializeFromAnnotations();
        this.init();
        this.node = node;
        this.localDevice = localDevice;
    }
    private void init()
    {
        initializeFromAnnotations();
    }
    @Override
    public void processScanRequest() throws Exception {
        // Be sure to call the base classes scan request
        super.processScanRequest();
        // Execute the code for this simulation every scan
        this.scanDevice();
    }
    public void scanDevice() throws Exception {
        DateTime now = DateTime.now();
        values = node.getPresentValues(localDevice);
        super.setProperty("satemp", values.get("sa_temp_1"));
        super.setProperty("ratemp", values.get("ra_temp_1"));
        super.setProperty("raco2", values.get("ra_co2_1"));
        super.setProperty("occur_ts",now);
        super.setProperty("location",locate);
        System.out.println("Done settign property");

        // Update the subscribed properties and events to send any updates to Thingworx
        // Without calling these methods, the property and event updates will not be sent
        // The numbers are timeouts in milliseconds.
        super.updateSubscribedProperties(10000);
        super.updateSubscribedEvents(10000);
    }

    @ThingworxServiceDefinition( name="Shutdown", description="Shutdown the client")
    @ThingworxServiceResult( name=CommonPropertyNames.PROP_RESULT, description="", baseType="NOTHING")
    public synchronized void Shutdown() throws Exception {
        // Should not have to do this, but guard against this method being called more than once.
        if(this._shutdownThread == null) {
            // Create a thread for shutting down and start the thread
            this._shutdownThread = new Thread(this);
            this._shutdownThread.start();
        }
    }
    @Override
    public void run() {
        try {
            // Delay for a period to verify that the Shutdown service will return
            Thread.sleep(1000);
            // Shutdown the client
            this.getClient().shutdown();
        } catch (Exception x) {
            // Not much can be done if there is an exception here
            // In the case of production code should at least log the error
        }
    }
}
