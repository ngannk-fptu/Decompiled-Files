/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.utils;

import java.util.Random;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

public class SessionUtils {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$utils$SessionUtils == null ? (class$org$apache$axis$utils$SessionUtils = SessionUtils.class$("org.apache.axis.utils.SessionUtils")) : class$org$apache$axis$utils$SessionUtils).getName());
    protected static final int SESSION_ID_BYTES = 16;
    protected static Random random = null;
    protected static String randomClass = "java.security.SecureRandom";
    private static String thisHost = null;
    static /* synthetic */ Class class$org$apache$axis$utils$SessionUtils;

    public static synchronized String generateSessionId() {
        byte[] bytes = new byte[16];
        SessionUtils.getRandom().nextBytes(bytes);
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < bytes.length; ++i) {
            byte b1 = (byte)((bytes[i] & 0xF0) >> 4);
            byte b2 = (byte)(bytes[i] & 0xF);
            if (b1 < 10) {
                result.append((char)(48 + b1));
            } else {
                result.append((char)(65 + (b1 - 10)));
            }
            if (b2 < 10) {
                result.append((char)(48 + b2));
                continue;
            }
            result.append((char)(65 + (b2 - 10)));
        }
        return result.toString();
    }

    public static synchronized Long generateSession() {
        return new Long(SessionUtils.getRandom().nextLong());
    }

    private static synchronized Random getRandom() {
        if (random == null) {
            try {
                Class<?> clazz = Class.forName(randomClass);
                random = (Random)clazz.newInstance();
            }
            catch (Exception e) {
                random = new Random();
            }
        }
        return random;
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

