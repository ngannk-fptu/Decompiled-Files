/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id.uuid;

import java.net.InetAddress;
import org.hibernate.internal.util.BytesHelper;

public final class Helper {
    private static final byte[] ADDRESS_BYTES;
    private static final int ADDRESS_INT;
    private static final String ADDRESS_HEX_STRING;
    private static final byte[] JVM_IDENTIFIER_BYTES;
    private static final int JVM_IDENTIFIER_INT;
    private static final String JVM_IDENTIFIER_HEX_STRING;
    private static short counter;

    private Helper() {
    }

    public static byte[] getAddressBytes() {
        return ADDRESS_BYTES;
    }

    public static int getAddressInt() {
        return ADDRESS_INT;
    }

    public static String getAddressHexString() {
        return ADDRESS_HEX_STRING;
    }

    public static byte[] getJvmIdentifierBytes() {
        return JVM_IDENTIFIER_BYTES;
    }

    public static int getJvmIdentifierInt() {
        return JVM_IDENTIFIER_INT;
    }

    public static String getJvmIdentifierHexString() {
        return JVM_IDENTIFIER_HEX_STRING;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static short getCountShort() {
        Class<Helper> clazz = Helper.class;
        synchronized (Helper.class) {
            if (counter < 0) {
                counter = 0;
            }
            short s = counter;
            counter = (short)(s + 1);
            // ** MonitorExit[var0] (shouldn't be in output)
            return s;
        }
    }

    public static byte[] getCountBytes() {
        return BytesHelper.fromShort(Helper.getCountShort());
    }

    public static String format(int value) {
        String formatted = Integer.toHexString(value);
        StringBuilder buf = new StringBuilder("00000000");
        buf.replace(8 - formatted.length(), 8, formatted);
        return buf.toString();
    }

    public static String format(short value) {
        String formatted = Integer.toHexString(value);
        StringBuilder buf = new StringBuilder("0000");
        buf.replace(4 - formatted.length(), 4, formatted);
        return buf.toString();
    }

    static {
        byte[] address;
        try {
            address = InetAddress.getLocalHost().getAddress();
        }
        catch (Exception e) {
            address = new byte[4];
        }
        ADDRESS_BYTES = address;
        ADDRESS_INT = BytesHelper.toInt(ADDRESS_BYTES);
        ADDRESS_HEX_STRING = Helper.format(ADDRESS_INT);
        JVM_IDENTIFIER_INT = (int)(System.currentTimeMillis() >>> 8);
        JVM_IDENTIFIER_BYTES = BytesHelper.fromInt(JVM_IDENTIFIER_INT);
        JVM_IDENTIFIER_HEX_STRING = Helper.format(JVM_IDENTIFIER_INT);
        counter = 0;
    }
}

