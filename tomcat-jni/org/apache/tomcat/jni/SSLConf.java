/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

public final class SSLConf {
    public static native long make(long var0, int var2) throws Exception;

    public static native void free(long var0);

    public static native int check(long var0, String var2, String var3) throws Exception;

    public static native void assign(long var0, long var2);

    public static native int apply(long var0, String var2, String var3) throws Exception;

    public static native int finish(long var0);
}

