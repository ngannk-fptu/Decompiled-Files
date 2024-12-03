/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

import org.apache.tomcat.jni.Error;

@Deprecated
public class Lock {
    public static final int APR_LOCK_FCNTL = 0;
    public static final int APR_LOCK_FLOCK = 1;
    public static final int APR_LOCK_SYSVSEM = 2;
    public static final int APR_LOCK_PROC_PTHREAD = 3;
    public static final int APR_LOCK_POSIXSEM = 4;
    public static final int APR_LOCK_DEFAULT = 5;

    public static native long create(String var0, int var1, long var2) throws Error;

    public static native long childInit(String var0, long var1) throws Error;

    public static native int lock(long var0);

    public static native int trylock(long var0);

    public static native int unlock(long var0);

    public static native int destroy(long var0);

    public static native String lockfile(long var0);

    public static native String name(long var0);

    public static native String defname();
}

