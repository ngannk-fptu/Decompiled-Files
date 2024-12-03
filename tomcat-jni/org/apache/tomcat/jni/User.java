/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jni;

import org.apache.tomcat.jni.Error;

@Deprecated
public class User {
    public static native long uidCurrent(long var0) throws Error;

    public static native long gidCurrent(long var0) throws Error;

    public static native long uid(String var0, long var1) throws Error;

    public static native long usergid(String var0, long var1) throws Error;

    public static native long gid(String var0, long var1) throws Error;

    public static native String username(long var0, long var2) throws Error;

    public static native String groupname(long var0, long var2) throws Error;

    public static native int uidcompare(long var0, long var2);

    public static native int gidcompare(long var0, long var2);

    public static native String homepath(String var0, long var1) throws Error;
}

