/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

@Deprecated
public class OS {
    private static final int UNIX = 1;
    private static final int WIN32 = 3;
    private static final int WIN64 = 4;
    private static final int LINUX = 5;
    private static final int SOLARIS = 6;
    private static final int BSD = 7;
    private static final int MACOSX = 8;
    public static final int LOG_EMERG = 1;
    public static final int LOG_ERROR = 2;
    public static final int LOG_NOTICE = 3;
    public static final int LOG_WARN = 4;
    public static final int LOG_INFO = 5;
    public static final int LOG_DEBUG = 6;
    public static final boolean IS_UNIX = OS.is(1);
    @Deprecated
    public static final boolean IS_NETWARE = false;
    public static final boolean IS_WIN32 = OS.is(3);
    public static final boolean IS_WIN64 = OS.is(4);
    public static final boolean IS_LINUX = OS.is(5);
    public static final boolean IS_SOLARIS = OS.is(6);
    public static final boolean IS_BSD = OS.is(7);
    public static final boolean IS_MACOSX = OS.is(8);

    private static native boolean is(int var0);

    public static native String defaultEncoding(long var0);

    public static native String localeEncoding(long var0);

    public static native int random(byte[] var0, int var1);

    public static native int info(long[] var0);

    public static native String expand(String var0);

    public static native void sysloginit(String var0);

    public static native void syslog(int var0, String var1);
}

