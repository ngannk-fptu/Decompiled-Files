/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Library
 *  com.sun.jna.Native
 *  com.sun.jna.win32.W32APIOptions
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;
import java.util.Map;

public interface Winsock2
extends Library {
    public static final Winsock2 INSTANCE = (Winsock2)Native.load((String)"ws2_32", Winsock2.class, (Map)W32APIOptions.ASCII_OPTIONS);

    public int gethostname(byte[] var1, int var2);
}

