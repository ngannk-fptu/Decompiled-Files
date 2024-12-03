/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.misc;

import java.net.InetAddress;

public class Uid {
    private static final int IP;
    private static short counter;
    private static final int JVM;
    private static String sep;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getUid() {
        short hiTime = (short)(System.currentTimeMillis() >>> 32);
        int loTime = (int)System.currentTimeMillis();
        Class<Uid> clazz = Uid.class;
        synchronized (Uid.class) {
            if (counter < 0) {
                counter = 0;
            }
            short s = counter;
            counter = (short)(s + 1);
            short ct = s;
            // ** MonitorExit[var3_2] (shouldn't be in output)
            return new StringBuilder(36).append(Uid.format(IP)).append(sep).append(Uid.format(JVM)).append(sep).append(Uid.format(hiTime)).append(sep).append(Uid.format(loTime)).append(sep).append(Uid.format((int)ct)).toString();
        }
    }

    private static String format(int intval) {
        String formatted = Integer.toHexString(intval);
        StringBuilder buf = new StringBuilder("00000000");
        buf.replace(8 - formatted.length(), 8, formatted);
        return buf.toString();
    }

    private static String format(short shortval) {
        String formatted = Integer.toHexString(shortval);
        StringBuilder buf = new StringBuilder("0000");
        buf.replace(4 - formatted.length(), 4, formatted);
        return buf.toString();
    }

    public static int toInt(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < 4; ++i) {
            result = (result << 8) - -128 + bytes[i];
        }
        return result;
    }

    static {
        int ipadd;
        try {
            ipadd = Uid.toInt(InetAddress.getLocalHost().getAddress());
        }
        catch (Exception e) {
            ipadd = 0;
        }
        IP = ipadd;
        counter = 0;
        JVM = (int)(System.currentTimeMillis() >>> 8);
        sep = "-";
    }
}

