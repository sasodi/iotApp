package org.cyient.insights.twc;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.PropertyValueException;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyAck;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyRequest;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.util.DiscoveryUtils;
import com.serotonin.bacnet4j.util.PropertyValues;
import com.serotonin.bacnet4j.util.RequestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sriky on 29/3/16.
 */
public class Node {
    private final float ERROR_PRESENT_VALUE = (float)-999.0;
    /* stores device ID corresponding to blocks which is already decided in BACnet Design */
    private int deviceID;
    /* stores name of the block this node is referring to */
    private String blockName;
    /* Remote Device id of this device that can be used to query it overtime */
    private RemoteDevice remoteDevice;
    /* stores list of object identifiers corresponding to data points to be collected*/
    private List<ObjectIdentifier> oids;
    /* List of object names of this block from which data will be collected */
    private List<String> activeObjectNames;
    /* Map of object Name to object identifier */
    private Map<String,ObjectIdentifier> nameToOid;
    /* Method that gets list of object identifiers attached to a block*/
    private SequenceOf<ObjectIdentifier> getObjectList(LocalDevice localDevice, RemoteDevice remoteDevice){
		/* declare an empty sequence as a place holder */
        SequenceOf<ObjectIdentifier> oidw = new SequenceOf<ObjectIdentifier>();
        try{
			/* get the object list*/
            oidw = RequestUtils.getObjectList(localDevice, remoteDevice);
            System.out.println("requesting objectList....");
        }catch(Exception e){
            System.out.println("requesting objectList failed: "+ e.getMessage());
        }
		/* Will be null */
        return oidw;
    }
    private boolean isThere(List<String> l, String s){
        for(String the: l){
            if(the.equalsIgnoreCase(s))return true;
        }
        return false;
    }

    public Node(LocalDevice localDevice, int deviceID, List<String> ObjectNames){

		/* store corresponding device ID */
        this.deviceID = deviceID;
		/* get the remote device object reference from device ID using discoveryUtils*/
        this.remoteDevice = DiscoveryUtils.discoverDevice(localDevice, deviceID);
		/* get the list of data points to be collected */
        this.activeObjectNames = ObjectNames;
		/**/
        this.oids = new ArrayList<ObjectIdentifier>();
		/* build the list of Oid corresponding to Object Names if remote device is successfully obtained*/
        if(remoteDevice !=null){
            this.nameToOid = new HashMap<String,ObjectIdentifier>();
            for(ObjectIdentifier objectIdentifier: getObjectList(localDevice, remoteDevice)){
				/*stores name of the object*/
                String objName=null;
				/* Read object name property */
                try{
                    ReadPropertyAck ack = (ReadPropertyAck) localDevice.send(remoteDevice,
                            new ReadPropertyRequest(objectIdentifier, PropertyIdentifier.objectName)).get();
                    objName = ack.getValue().toString();
                    //System.out.println(objName);
                }catch(Exception e){
                    //System.out.println("Time Out : "+ e.getMessage());
                }
				/* if object name is not read properly, it wont be added to the Map*/
                if(objName != null){
                    this.nameToOid.put(objName, objectIdentifier);
                    //System.out.println("Done addingt To full Lis "+objName);
                }
				/* if the name is marked for reading , then it will be added to the list of object identifiers to be scanned */
                if(isThere(ObjectNames,objName)){
                    //System.out.println("adding to Active List "+objName);
                    this.oids.add(objectIdentifier);
                    //System.out.println("Done adding to Active List "+objName);
                }
            }
        }
		/* get the name of the device block */
        this.blockName = Devices.getDeviceName(deviceID);
    }

    /* Method to get present values of the block */
    public Map<String,Float> getPresentValues(LocalDevice localDevice) throws BACnetException {

		/* initialize  object name to present value store */
        Map<String,Float> nameToPresentValue = new HashMap<String,Float>();
        //System.out.println("created name to present value");
		/* get present values of all the active oid */
        PropertyValues pvs = RequestUtils.readOidPresentValues(localDevice, this.remoteDevice, this.oids, null);
        //System.out.println("done requesting propperty values"+ pvs);
		/* place holder for present values */
        Float presentValue;
		/* for each of the requested objects id of a block get the present values */
        for(String name: this.activeObjectNames){
			/* get the corresponding object identifier*/
            ObjectIdentifier oid = nameToOid.get(name);
			/* read the present value*/
            try {
                if( oid != null){
                    presentValue = Float.parseFloat(pvs.get(oid, PropertyIdentifier.presentValue).toString());
                    //System.out.println("read present value "+ presentValue);
                }else{
                    presentValue = ERROR_PRESENT_VALUE;
                }
            } catch (PropertyValueException e) {
                presentValue = (float) ERROR_PRESENT_VALUE;
            }catch(NullPointerException e){
                presentValue = (float) ERROR_PRESENT_VALUE;
            }catch(NumberFormatException e){
                presentValue = (float) ERROR_PRESENT_VALUE;
            }
            nameToPresentValue.put(name, presentValue);
            System.out.println(name+":"+presentValue);
        }
        presentValue = ERROR_PRESENT_VALUE;
        try {
            if(nameToOid.get("supplyfanss_1")!=null){
                if( pvs.get(nameToOid.get("supplyfanss_1"), PropertyIdentifier.presentValue).toString().contains("Active")){ presentValue =(float)1.0;}else{
                    presentValue=(float)0.0;
                }
            }else {presentValue=(float)-1.0;}
        } catch (PropertyValueException e) {
            presentValue = ERROR_PRESENT_VALUE;
            System.out.println("Error: "+e.getMessage());
        }catch(NullPointerException e){
            presentValue = ERROR_PRESENT_VALUE;
            System.out.println("Error: "+e.getMessage());
        }catch(NumberFormatException e){
            System.out.println("Error: "+e.getMessage());
            presentValue = ERROR_PRESENT_VALUE;
        }
        nameToPresentValue.put("supplyfanss_1",presentValue);

        return nameToPresentValue;
    }
    public String getBlockName(){
        return this.blockName;
    }
}
