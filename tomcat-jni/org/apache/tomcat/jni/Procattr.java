/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

import org.apache.tomcat.jni.Error;

@Deprecated
public class Procattr {
    public static native long create(long var0) throws Error;

    public static native int ioSet(long var0, int var2, int var3, int var4);

    public static native int childInSet(long var0, long var2, long var4);

    public static native int childOutSet(long var0, long var2, long var4);

    public static native int childErrSet(long var0, long var2, long var4);

    public static native int dirSet(long var0, String var2);

    public static native int cmdtypeSet(long var0, int var2);

    public static native int detachSet(long var0, int var2);

    public static native int errorCheckSet(long var0, int var2);

    public static native int addrspaceSet(long var0, int var2);

    public static native void errfnSet(long var0, long var2, Object var4);

    public static native int userSet(long var0, String var2, String var3);

    public static native int groupSet(long var0, String var2);
}

