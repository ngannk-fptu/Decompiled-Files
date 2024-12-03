/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

@Deprecated
public class Local {
    public static native long create(String var0, long var1) throws Exception;

    public static native int bind(long var0, long var2);

    public static native int listen(long var0, int var2);

    public static native long accept(long var0) throws Exception;

    public static native int connect(long var0, long var2);
}

