/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

import java.nio.ByteBuffer;

public class Buffer {
    @Deprecated
    public static native ByteBuffer malloc(int var0);

    @Deprecated
    public static native ByteBuffer calloc(int var0, int var1);

    @Deprecated
    public static native ByteBuffer palloc(long var0, int var2);

    @Deprecated
    public static native ByteBuffer pcalloc(long var0, int var2);

    @Deprecated
    public static native ByteBuffer create(long var0, int var2);

    @Deprecated
    public static native void free(ByteBuffer var0);

    public static native long address(ByteBuffer var0);

    @Deprecated
    public static native long size(ByteBuffer var0);
}

