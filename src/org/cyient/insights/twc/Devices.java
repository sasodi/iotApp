package org.cyient.insights.twc;

import java.util.ArrayList;
import java.util.List;

public class Devices {
	public static final int block_1_A = 240402;
	public static final int block_1_B = 250000;
	public static final int block_1_C = 240204;
	public static final int block_3_A = 250009;
	public static final int block_3_B = 250011;
	public static final int block_3_C = 250008;
	public static final int block_4_A = 250013;
	public static final int block_4_B = 250014;
	public static final int block_4_C = 250012;
	public static final int block_G_A = 240401;
	public static final int block_G_B = 240301;
	public static final int block_G_C = 241304;
	public static final int block_G_DC = 241303;
	public static final int block_base = 241302;
	
	public static List<String> getActiveNames() {
		List<String> activeObjectName = new ArrayList<String>();
		
		activeObjectName.add("chw_vlv_fdbk_1");
		activeObjectName.add("ra_co2_1");
		activeObjectName.add("ra_temp_1");
		activeObjectName.add("sa_temp_1");
		activeObjectName.add("fa_damper_1");
		activeObjectName.add("sf_vfd_output_1");
		activeObjectName.add("supplyfanss_1");
		activeObjectName.add("supply_flow_sp_1");
		
		return activeObjectName;
	}
	
	public static List<Integer> getAsList(){
		List<Integer> devices = new ArrayList<Integer>();
		
		devices.add(block_1_A);
		devices.add(block_1_B);
		devices.add(block_1_C);
		devices.add(block_3_A);
		devices.add(block_3_B);
		devices.add(block_3_C);
		devices.add(block_4_A);
		devices.add(block_4_B);
		devices.add(block_4_C);
		devices.add(block_G_A);
		devices.add(block_G_B);
		devices.add(block_G_C);
		devices.add(block_G_DC);
		devices.add(block_base);
		
		return devices;
	}
	
	public static String getDeviceName(int devId){
		
		String devName = null;
		switch (devId) {
			case block_1_A:  devName = "block1A";
					 		 break;
			case block_1_B:  devName = "block1B";
	 		 				 break;
			case block_1_C:  devName = "block1C";
	 		 				 break;
			case block_3_A:  devName = "block3A";
	 		 				 break;
			case block_3_B:  devName = "block3B";
	 		 				 break;
			case block_3_C:  devName = "block3C";
	 		 				 break;
			case block_4_A:  devName = "block4A";
	 		 				 break;
			case block_4_B:  devName = "block4B";
	 		 				 break;
			case block_4_C:  devName = "block4C";
	 		 				 break;
			case block_G_A:  devName = "blockGA";
	 		 				 break;
			case block_G_B:  devName = "blockGB";
	 		 				 break;
			case block_G_C:  devName = "blockGC";
	 		 				 break;
			case block_G_DC:  devName = "blockGDC";
	 		 			      break;
			case block_base:  devName = "blockGbase";
	 		 				  break;
			default : devName = "unknown";
	 		
		}
		
		return devName;
	}
}