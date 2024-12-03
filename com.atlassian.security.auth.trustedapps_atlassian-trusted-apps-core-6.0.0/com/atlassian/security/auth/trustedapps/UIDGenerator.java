/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class UIDGenerator {
    public static String generateUID() {
        try {
            String strRetVal = "";
            String strTemp = "";
            InetAddress addr = InetAddress.getLocalHost();
            byte[] ipaddr = addr.getAddress();
            for (int i = 0; i < ipaddr.length; ++i) {
                Byte b = new Byte(ipaddr[i]);
                strTemp = Integer.toHexString(b.intValue() & 0xFF);
                while (strTemp.length() < 2) {
                    strTemp = '0' + strTemp;
                }
                strRetVal = strRetVal + strTemp;
            }
            strTemp = Long.toHexString(System.currentTimeMillis());
            while (strTemp.length() < 12) {
                strTemp = '0' + strTemp;
            }
            strRetVal = strRetVal + strTemp;
            SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
            strTemp = Integer.toHexString(prng.nextInt());
            while (strTemp.length() < 8) {
                strTemp = '0' + strTemp;
            }
            strRetVal = strRetVal + strTemp.substring(4);
            strTemp = Long.toHexString(System.identityHashCode(new Object()));
            while (strTemp.length() < 8) {
                strTemp = '0' + strTemp;
            }
            strRetVal = strRetVal + strTemp;
            return strRetVal.toUpperCase();
        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

