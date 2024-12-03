/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.ptr.IntByReference
 *  com.sun.jna.ptr.PointerByReference
 *  com.sun.jna.win32.StdCallLibrary
 *  com.sun.jna.win32.W32APIOptions
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import java.util.Map;

public interface Version
extends StdCallLibrary {
    public static final Version INSTANCE = (Version)Native.load((String)"version", Version.class, (Map)W32APIOptions.DEFAULT_OPTIONS);

    public int GetFileVersionInfoSize(String var1, IntByReference var2);

    public boolean GetFileVersionInfo(String var1, int var2, int var3, Pointer var4);

    public boolean VerQueryValue(Pointer var1, String var2, PointerByReference var3, IntByReference var4);
}

