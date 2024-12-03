/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

@Deprecated
public class SSLSocket {
    public static native int attach(long var0, long var2) throws Exception;

    public static native int handshake(long var0);

    public static native int renegotiate(long var0);

    public static native void setVerify(long var0, int var2, int var3);

    public static native byte[] getInfoB(long var0, int var2) throws Exception;

    public static native String getInfoS(long var0, int var2) throws Exception;

    public static native int getInfoI(long var0, int var2) throws Exception;

    public static native int getALPN(long var0, byte[] var2);
}

