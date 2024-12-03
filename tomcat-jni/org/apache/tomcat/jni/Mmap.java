/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

import org.apache.tomcat.jni.Error;

@Deprecated
public class Mmap {
    public static final int APR_MMAP_READ = 1;
    public static final int APR_MMAP_WRITE = 2;

    public static native long create(long var0, long var2, long var4, int var6, long var7) throws Error;

    public static native long dup(long var0, long var2) throws Error;

    public static native int delete(long var0);

    public static native long offset(long var0, long var2) throws Error;
}

