/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

import org.apache.tomcat.jni.Error;

@Deprecated
public class Global {
    public static native long create(String var0, int var1, long var2) throws Error;

    public static native long childInit(String var0, long var1) throws Error;

    public static native int lock(long var0);

    public static native int trylock(long var0);

    public static native int unlock(long var0);

    public static native int destroy(long var0);
}

