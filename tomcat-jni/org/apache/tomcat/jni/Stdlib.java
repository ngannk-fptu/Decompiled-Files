/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

@Deprecated
public class Stdlib {
    public static native boolean memread(byte[] var0, long var1, int var3);

    public static native boolean memwrite(long var0, byte[] var2, int var3);

    public static native boolean memset(long var0, int var2, int var3);

    public static native long malloc(int var0);

    public static native long realloc(long var0, int var2);

    public static native long calloc(int var0, int var1);

    public static native void free(long var0);

    public static native int getpid();

    public static native int getppid();
}

