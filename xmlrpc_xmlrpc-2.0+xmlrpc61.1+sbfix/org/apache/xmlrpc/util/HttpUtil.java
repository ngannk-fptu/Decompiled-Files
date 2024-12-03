/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.EncoderException
 *  org.apache.commons.codec.binary.Base64
 */
package org.apache.xmlrpc.util;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;

public class HttpUtil {
    private static final Base64 base64 = new Base64();
    static /* synthetic */ Class class$org$apache$commons$codec$binary$Base64;

    private HttpUtil() {
    }

    /*
     * WARNING - void declaration
     */
    public static String encodeBasicAuthentication(String user, String password) {
        void var2_2;
        String auth;
        if (user == null || password == null) {
            auth = null;
        } else {
            try {
                byte[] bytes = (user + ':' + password).getBytes();
                auth = new String((byte[])base64.encode((Object)bytes)).trim();
            }
            catch (EncoderException e) {
                throw new RuntimeException("Possibly incompatible version of '" + (class$org$apache$commons$codec$binary$Base64 == null ? (class$org$apache$commons$codec$binary$Base64 = HttpUtil.class$("org.apache.commons.codec.binary.Base64")) : class$org$apache$commons$codec$binary$Base64).getName() + "' used: " + (Object)((Object)e));
            }
        }
        return var2_2;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

