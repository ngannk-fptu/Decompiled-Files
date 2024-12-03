/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

import org.apache.tomcat.jni.Error;
import org.apache.tomcat.jni.FileInfo;

@Deprecated
public class Directory {
    public static native int make(String var0, int var1, long var2);

    public static native int makeRecursive(String var0, int var1, long var2);

    public static native int remove(String var0, long var1);

    public static native String tempGet(long var0);

    public static native long open(String var0, long var1) throws Error;

    public static native int close(long var0);

    public static native int rewind(long var0);

    public static native int read(FileInfo var0, int var1, long var2);
}

