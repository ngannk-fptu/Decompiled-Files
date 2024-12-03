/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

import java.nio.ByteBuffer;
import org.apache.tomcat.jni.Error;

@Deprecated
public class Shm {
    public static native long create(long var0, String var2, long var3) throws Error;

    public static native int remove(String var0, long var1);

    public static native int destroy(long var0);

    public static native long attach(String var0, long var1) throws Error;

    public static native int detach(long var0);

    public static native long baseaddr(long var0);

    public static native long size(long var0);

    public static native ByteBuffer buffer(long var0);
}

