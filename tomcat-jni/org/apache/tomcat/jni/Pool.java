/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

import java.nio.ByteBuffer;

public class Pool {
    public static native long create(long var0);

    @Deprecated
    public static native void clear(long var0);

    public static native void destroy(long var0);

    @Deprecated
    public static native long parentGet(long var0);

    @Deprecated
    public static native boolean isAncestor(long var0, long var2);

    @Deprecated
    public static native long cleanupRegister(long var0, Object var2);

    @Deprecated
    public static native void cleanupKill(long var0, long var2);

    @Deprecated
    public static native void noteSubprocess(long var0, long var2, int var4);

    @Deprecated
    public static native ByteBuffer alloc(long var0, int var2);

    @Deprecated
    public static native ByteBuffer calloc(long var0, int var2);

    @Deprecated
    public static native int dataSet(long var0, String var2, Object var3);

    @Deprecated
    public static native Object dataGet(long var0, String var2);

    @Deprecated
    public static native void cleanupForExec();
}

