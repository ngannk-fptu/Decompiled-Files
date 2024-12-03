/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

import org.apache.tomcat.jni.Error;

@Deprecated
public class Poll {
    public static final int APR_POLLIN = 1;
    public static final int APR_POLLPRI = 2;
    public static final int APR_POLLOUT = 4;
    public static final int APR_POLLERR = 16;
    public static final int APR_POLLHUP = 32;
    public static final int APR_POLLNVAL = 64;
    public static final int APR_POLLSET_THREADSAFE = 1;
    public static final int APR_NO_DESC = 0;
    public static final int APR_POLL_SOCKET = 1;
    public static final int APR_POLL_FILE = 2;
    public static final int APR_POLL_LASTDESC = 3;

    public static native long create(int var0, long var1, int var3, long var4) throws Error;

    public static native int destroy(long var0);

    public static native int add(long var0, long var2, int var4);

    public static native int addWithTimeout(long var0, long var2, int var4, long var5);

    public static native int remove(long var0, long var2);

    public static native int poll(long var0, long var2, long[] var4, boolean var5);

    public static native int maintain(long var0, long[] var2, boolean var3);

    public static native void setTtl(long var0, long var2);

    public static native long getTtl(long var0);

    public static native int pollset(long var0, long[] var2);

    public static native int interrupt(long var0);

    public static native boolean wakeable(long var0);
}

