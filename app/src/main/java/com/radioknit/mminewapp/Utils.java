package com.radioknit.mminewapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.widget.Toast;


/**

 */
public class Utils {

    private static final String TAG = "Utils";
    /**

     */
    public static void log(String message) {
        if (BuildConfig.DEBUG) {
            if (message != null) Log.i(Const.TAG, message);
        }
    }
    // ============================================================================


    /**
     *
     */
    public static String printHex(String hex) {
        StringBuilder sb = new StringBuilder();
        int len = hex.length();
        String a=(toHex1(hex));
        try {
            for (int i = 0; i < len; i += 2) {
                sb.append("0x").append(hex.substring(i, i + 2)).append(" ");
            }
        } catch (NumberFormatException e) {
            log("printHex NumberFormatException: " + e.getMessage());

        } catch (StringIndexOutOfBoundsException e) {
            log("printHex StringIndexOutOfBoundsException: " + e.getMessage());
        }
        return sb.toString();
    }
    // ============================================================================


    /**

     * @param hex -
     * @return -
     */
    public static byte[] toHex(String hex) {
        int len = hex.length();
        byte[] result = new byte[len];
        try {
            int index = 0;
            for (int i = 0; i < len; i += 2) {
                result[index] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
                index++;
            }
        } catch (NumberFormatException e) {
            log("toHex NumberFormatException: " + e.getMessage());

        } catch (StringIndexOutOfBoundsException e) {
            log("toHex StringIndexOutOfBoundsException: " + e.getMessage());
        }
        return result;
    }
    // ============================================================================
    public static String toHex11(String hex) {

        StringBuffer buffer=new StringBuffer();

        String s1="49";
        String s2="49";

        String s3="00";
        String s4="00";

        int d=13;
        String a81= String.valueOf(d);

        int x1 = Integer.parseInt(s1,16);
        int x2 = Integer.parseInt(s2,16);
        int x3 = Integer.parseInt(s3,16);
        int x4 = Integer.parseInt(s4,16);

        int len = hex.length();
        byte[] result = new byte[len];

        int index = 0;
        for (int i = 0; i < len; i += 2) {
            result[index] = (byte) Integer.parseInt(hex.substring(i, i + 2),16);
            index++;

        }


        String str1= Integer.toHexString(result[0]);

        int a1 = Integer.parseInt(str1,16);
        //System.out.println("RES="+a1);


        int a=x1+x2+a1+x3+x4;
        int a6 =a & 15;

        System.out.println("a6="+a6);

        int a7 = (a & 240)>>4 ;

        System.out.println("a7="+a7);
        buffer.append(s1).append(s2).append(a1).
                append(s3).append(s4).append(a7).append(a6).append(a81);

        return  buffer.toString();
    }
    // ============================================================================

    public static String toHex1(String hex) {

        StringBuffer buffer=new StringBuffer();
        int len = hex.length();
        byte[] result = new byte[len];

            int index = 0;
            for (int i = 0; i < len; i += 2) {
                result[index] = (byte) Integer.parseInt(hex.substring(i, i + 2),16);
                index++;

            }
        // System.out.println("RES="+result[0]);

        String str1= Integer.toHexString(result[0]);
        String str2= Integer.toHexString(result[1]);
        String str3= Integer.toHexString(result[2]);
        String str4= Integer.toHexString(result[3]);
        String str5= Integer.toHexString(result[4]);

        int a1 = Integer.parseInt(str1,16);
        int a2 = Integer.parseInt(str2,16);
        int a3 = Integer.parseInt(str3,16);
        int a4 = Integer.parseInt(str4,16);
        int a5 = Integer.parseInt(str5,16);


        int a=a1+a2+a3+a4+a5;
        String z= Integer.toHexString(a);
        //System.out.println("RES="+z);


        int a6 =a & 15;

        String a61=decimal_value(a6);
       // System.out.println("a61="+a61);

        int a7 = (a & 240)>>4 ;
        String a71=decimal_value(a7);
        //System.out.println("a7="+a71);


        hex=hex.concat(a61);
        hex=hex.concat(a71);

        int d=13;
        String a81= String.valueOf(d);

        hex=hex.concat(a81);



       // System.out.println("STRING="+hex);

       // return z.getBytes();

         buffer.append(hex).append("\n").append(z);
        //buffer.append(hex);
        return  buffer.toString();
    }

    public static byte[] toHex2(String hex) {

        int len = hex.length();
        byte[] result = new byte[len];
        try {
            int index = 0;
            for (int i = 0; i < len; i += 2) {
                result[index] = (byte) Integer.parseInt(hex.substring(i, i + 2),16);
                index++;

            }
        } catch (NumberFormatException e) {


        } catch (StringIndexOutOfBoundsException e) {

        }
        // System.out.println("RES="+result[0]);

        String str1= Integer.toHexString(result[0]);
        String str2= Integer.toHexString(result[1]);
        String str3= Integer.toHexString(result[2]);
        String str4= Integer.toHexString(result[3]);
        String str5= Integer.toHexString(result[4]);

        int a1 = Integer.parseInt(str1,16);
        int a2 = Integer.parseInt(str2,16);
        int a3 = Integer.parseInt(str3,16);
        int a4 = Integer.parseInt(str4, 16);
        int a5 = Integer.parseInt(str5,16);


        int a=a1+a2+a3+a4+a5;
        String z= Integer.toHexString(a);
        System.out.println("RES="+z);


        int a6 =a & 15;

        String a61=decimal_value(a6);
        System.out.println("a61="+a61);

        int a7 = (a & 240)>>4 ;
        String a71=decimal_value(a7);
        System.out.println("a7="+a71);


        hex=hex.concat(a61);
        hex=hex.concat(a71);

        int d=13;
        String a81= String.valueOf(d);

        hex=hex.concat(a81);

        System.out.println("STRING="+hex);


        return z.getBytes();
    }

    public static byte[] toHex3(String hex) {
        int a =0;
        int len = hex.length();
        byte[] result = new byte[len];
        byte[] buffer = new byte[a];
        try {
            int index = 0;
            for (int i = 0; i < len; i += 2) {
                result[index] = (byte) Integer.parseInt(hex.substring(i, i + 2),16);
                index++;

            }
        } catch (NumberFormatException e) {


        } catch (StringIndexOutOfBoundsException e) {

        }
        // System.out.println("RES="+result[0]);

        String str1= Integer.toHexString(result[0]);
        String str2= Integer.toHexString(result[1]);
        String str3= Integer.toHexString(result[2]);
        String str4= Integer.toHexString(result[3]);
        String str5= Integer.toHexString(result[4]);

        int a1 = Integer.parseInt(str1,16);
        int a2 = Integer.parseInt(str2,16);
        int a3 = Integer.parseInt(str3,16);
        int a4 = Integer.parseInt(str4,16);
        int a5 = Integer.parseInt(str5,16);


        a=a1+a2+a3+a4+a5;
        String z= Integer.toHexString(a);
        // System.out.println("RES="+z);


        int a6 =a & 15;

        String a61=decimal_value(a6);
        // System.out.println("a61="+a61);

        int a7 = (a & 240)>>4 ;
        String a71=decimal_value(a7);
        //  System.out.println("a7="+a71);


        hex=hex.concat(a61);
        hex=hex.concat(a71);

        int d=13;
        String a81= String.valueOf(d);

        hex=hex.concat(a81);

        //System.out.println("STRING="+hex);


        return buffer;
    }

    public static String decimal_value(int val) {
        String va = null;
        if (val == 10) {
            va = "A";
        } else if (val == 11) {
            va = "A";
        } else if (val ==12 ) {
            va = "C";
        } else if (val ==13 ) {
            va ="D";
        } else if (val ==14 ) {
            va = "E";
        } else if (val == 15) {
            va = "F";
        } else if (val == 0) {
            va = "0";
        } else if (val == 1) {
            va = "1";
        } else if (val == 2) {
            va = "2";
        } else if (val == 3) {
            va = "3";
        } else if (val == 4) {
            va = "4";
        } else if (val == 5) {
            va = "5";
        } else if (val == 6) {
            va = "6";
        } else if (val == 7) {
            va = "7";
        } else if (val == 8) {
            va = "8";
        } else if (val == 9) {
            va = "9";
        }
        return va;
    }

    /**

     */
    public static byte[] concat(byte[]A, byte[] B) {
        byte[] C = new byte[A.length + B.length];
        System.arraycopy(A, 0, C, 0, A.length);
        System.arraycopy(B, 0, C, A.length, B.length);
        return C;
    }
    // ============================================================================


    /**

     */
    public static String getPrefence(Context context, String item) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(item, Const.TAG);
    }
    // ============================================================================


    /**

     */
    public static boolean getBooleanPrefence(Context context, String tag) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(tag, true);
    }
    // ============================================================================


    /**

     */
    // ============================================================================
    public static class InputFilterHex implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (!Character.isDigit(source.charAt(i))
                        && source.charAt(i) != 'A' && source.charAt(i) != 'D'
                        && source.charAt(i) != 'B' && source.charAt(i) != 'E'
                        && source.charAt(i) != 'C' && source.charAt(i) != 'F'
                        ) {
                    return "";
                }
            }
            return null;
        }
    }
    // ============================================================================

    public static String calculateChecksumValue(String value){

        int sum  = 0;
        String sumHex;
        for(int i =0 ; i < value.length(); i++){
            char ch = value.charAt(i);
            String hex = String.format("%04x", (int) ch);
            Log.e(TAG, "Hex = "+hex);
            sum = sum + Integer.parseInt(hex, 16);
        }
        sumHex = String.format("%04x", sum);
        Log.e(TAG, "sumHex  = "+sumHex);
        Log.e(TAG, "SunHex new = "+ Integer.toHexString(sum));
        return sumHex;

    }


    public static boolean isObjNotNull(Object object) {
        boolean isValide = true;
        try {

            if (object == null) {
                isValide = false;
                Log.d(TAG, "In isObjNull()---1st");
            } else {
                if (object.equals("")) {
                    isValide = false;
                    Log.d(TAG, "In isObjNull()---2nd");
                }

            }

        } catch (Exception e) {
            Log.d(TAG, "Error In isObjNull()---" + e.getMessage());
        }
        Log.d(TAG, "In isObjNull()---3th");
        return isValide;

    }

    public static boolean isStringNotNull(String object) {
        boolean isValide = true;
        try {

            if (object == null) {
                isValide = false;
                Log.d(TAG, "In isStringNull()---1st");
            } else {
                if (object.trim().equals("")) {
                    isValide = false;
                    Log.d(TAG, "In isStringNull()---2nd");
                }
                if (object.trim().equalsIgnoreCase("null")) {
                    isValide = false;
                    Log.d(TAG, "In isStringNull()---3rd");
                }
            }

        } catch (Exception e) {
            Log.d(TAG, "Error In isStringNull()---" + e.getMessage());
        }
        Log.d(TAG, "In isStringNull()---4th");
        return isValide;
    }


    public static void showToastMsg(Context context, String msg) {
        if (Utils.isObjNotNull(context)) {
            if (Utils.isStringNotNull(msg)) {
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }
        }
    }


    public static String hexToBin (String hex){
        int i = Integer.parseInt(hex, 16);
        String bin = Integer.toBinaryString(i);
        while (bin.length()<8){
            bin="0"+bin;
        }
        return bin;

    }


    public static boolean checkRecivedStringValid(String recivedString) {
        String sum = Utils.calculateChecksumValue(recivedString.substring(0, 6));
//            Log.e(TAG, "Sum  = "+ sum);
        String lsb = sum.substring(2, 4);
        String strMsb = sum.substring(0, 2);
        int intMsb = (Integer.parseInt(strMsb, 16) | 50);
//            Log.e(TAG, "lsb = "+lsb +" intMsb = "+intMsb);
        String lsb1 = String.format("%04x", (int) recivedString.charAt(6));
        String msb1 = String.format("%04x", (int) recivedString.charAt(7));

//            Log.e(TAG, "lsb1 = "+lsb1 +" msb1 = "+msb1);

        if (lsb.equals(lsb1.substring(2, 4)) && String.valueOf(intMsb).equals(msb1.substring(2, 4))) {
            return true;
        } else {
            return false;
        }
    }

    public static String calculateHexaValue(String value){

        int sum  = 0;
        String sumHex;
        int mm = 2;
        if(value.length() > 7)
            mm = 3;

        for(int i =0 ; i < value.length()-1; i++){
            char ch = value.charAt(i);
            String hex = String.format("%04x", (int) ch);
            Log.e(TAG, "Hex = "+hex);
            if(Integer.parseInt(hex, 16) > 255)
                hex = String.format("%04x", 255);

            sum = sum + Integer.parseInt(hex, 16);
        }
        sumHex = String.format("%04x", sum);
        //Log.e(TAG, "sumHex  = "+sumHex);
        return sumHex;
    }


    public static String calculateChecksumValueNew(String value){

        int sum  = 0;
        String sumHex;
        for(int i =0 ; i < value.length()-2; i++){
            char ch = value.charAt(i);
            String hex = String.format("%04x", (int) ch);
            //Log.e(TAG, "Hex = "+hex);
            sum = sum + Integer.parseInt(hex, 16);
        }
        sumHex = String.format("%04x", sum);
        Log.e(TAG, "sumHex  = "+sumHex);
        Log.e(TAG, "SunHex new = "+ Integer.toHexString(sum));
        return sumHex;

    }


}
