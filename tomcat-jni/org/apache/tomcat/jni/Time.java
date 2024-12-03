/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

@Deprecated
public class Time {
    public static final long APR_USEC_PER_SEC = 1000000L;
    public static final long APR_MSEC_PER_USEC = 1000L;

    public static long sec(long t) {
        return t / 1000000L;
    }

    public static long msec(long t) {
        return t / 1000L;
    }

    public static native long now();

    public static native String rfc822(long var0);

    public static native String ctime(long var0);

    public static native void sleep(long var0);
}

