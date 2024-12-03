/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

import org.apache.tomcat.jni.Sockaddr;

@Deprecated
public class Address {
    public static final String APR_ANYADDR = "0.0.0.0";

    public static native boolean fill(Sockaddr var0, long var1);

    public static native Sockaddr getInfo(long var0);

    public static native long info(String var0, int var1, int var2, int var3, long var4) throws Exception;

    public static native String getnameinfo(long var0, int var2);

    public static native String getip(long var0);

    public static native int getservbyname(long var0, String var2);

    public static native long get(int var0, long var1) throws Exception;

    public static native boolean equal(long var0, long var2);
}

