/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

import org.apache.tomcat.jni.Error;

@Deprecated
public class Registry {
    public static final int HKEY_CLASSES_ROOT = 1;
    public static final int HKEY_CURRENT_CONFIG = 2;
    public static final int HKEY_CURRENT_USER = 3;
    public static final int HKEY_LOCAL_MACHINE = 4;
    public static final int HKEY_USERS = 5;
    public static final int KEY_ALL_ACCESS = 1;
    public static final int KEY_CREATE_LINK = 2;
    public static final int KEY_CREATE_SUB_KEY = 4;
    public static final int KEY_ENUMERATE_SUB_KEYS = 8;
    public static final int KEY_EXECUTE = 16;
    public static final int KEY_NOTIFY = 32;
    public static final int KEY_QUERY_VALUE = 64;
    public static final int KEY_READ = 128;
    public static final int KEY_SET_VALUE = 256;
    public static final int KEY_WOW64_64KEY = 512;
    public static final int KEY_WOW64_32KEY = 1024;
    public static final int KEY_WRITE = 2048;
    public static final int REG_BINARY = 1;
    public static final int REG_DWORD = 2;
    public static final int REG_EXPAND_SZ = 3;
    public static final int REG_MULTI_SZ = 4;
    public static final int REG_QWORD = 5;
    public static final int REG_SZ = 6;

    public static native long create(int var0, String var1, int var2, long var3) throws Error;

    public static native long open(int var0, String var1, int var2, long var3) throws Error;

    public static native int close(long var0);

    public static native int getType(long var0, String var2);

    public static native int getValueI(long var0, String var2) throws Error;

    public static native long getValueJ(long var0, String var2) throws Error;

    public static native int getSize(long var0, String var2);

    public static native String getValueS(long var0, String var2) throws Error;

    public static native String[] getValueA(long var0, String var2) throws Error;

    public static native byte[] getValueB(long var0, String var2) throws Error;

    public static native int setValueI(long var0, String var2, int var3);

    public static native int setValueJ(long var0, String var2, long var3);

    public static native int setValueS(long var0, String var2, String var3);

    public static native int setValueE(long var0, String var2, String var3);

    public static native int setValueA(long var0, String var2, String[] var3);

    public static native int setValueB(long var0, String var2, byte[] var3);

    public static native String[] enumKeys(long var0) throws Error;

    public static native String[] enumValues(long var0) throws Error;

    public static native int deleteValue(long var0, String var2);

    public static native int deleteKey(int var0, String var1, boolean var2);
}

