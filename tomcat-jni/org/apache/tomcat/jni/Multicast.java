/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

@Deprecated
public class Multicast {
    public static native int join(long var0, long var2, long var4, long var6);

    public static native int leave(long var0, long var2, long var4, long var6);

    public static native int hops(long var0, int var2);

    public static native int loopback(long var0, boolean var2);

    public static native int ointerface(long var0, long var2);
}

