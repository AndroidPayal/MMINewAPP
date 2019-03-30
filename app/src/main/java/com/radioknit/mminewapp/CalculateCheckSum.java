package com.radioknit.mminewapp;

import android.util.Log;

/**
 * Created by soft on 11/12/17.
 */

public class CalculateCheckSum {

   public static String calculateChkSum(int[] calValue){
        String strCmd=String.format("%02x",calValue[0])+String.format("%02x",calValue[1])+String.format("%02x",calValue[2])+String.format("%02x",calValue[3])+String.format("%02x",calValue[4])+String.format("%02x",calValue[5]);
        int chkSum  = 0;
        for(int i = 0; i<strCmd.length(); i++){
            chkSum = chkSum + strCmd.charAt(i);

        }
       System.out.println("Checksum: "+chkSum);
        return Integer.toString(chkSum,16).substring(1,3);
    }
}
