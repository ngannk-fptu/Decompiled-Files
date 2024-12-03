/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.ptr.IntByReference
 *  com.sun.jna.win32.StdCallLibrary
 *  com.sun.jna.win32.W32APIOptions
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.Winnetwk;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import java.util.Map;

public interface Mpr
extends StdCallLibrary {
    public static final Mpr INSTANCE = (Mpr)Native.load((String)"Mpr", Mpr.class, (Map)W32APIOptions.DEFAULT_OPTIONS);

    public int WNetOpenEnum(int var1, int var2, int var3, Winnetwk.NETRESOURCE.ByReference var4, WinNT.HANDLEByReference var5);

    public int WNetEnumResource(WinNT.HANDLE var1, IntByReference var2, Pointer var3, IntByReference var4);

    public int WNetCloseEnum(WinNT.HANDLE var1);

    public int WNetGetUniversalName(String var1, int var2, Pointer var3, IntByReference var4);

    public int WNetUseConnection(WinDef.HWND var1, Winnetwk.NETRESOURCE var2, String var3, String var4, int var5, Pointer var6, IntByReference var7, IntByReference var8);

    public int WNetAddConnection3(WinDef.HWND var1, Winnetwk.NETRESOURCE var2, String var3, String var4, int var5);

    public int WNetCancelConnection2(String var1, int var2, boolean var3);
}

