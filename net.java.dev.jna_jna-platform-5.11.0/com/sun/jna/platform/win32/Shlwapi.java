/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.ptr.PointerByReference
 *  com.sun.jna.win32.StdCallLibrary
 *  com.sun.jna.win32.W32APIOptions
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.ShTypes;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import java.util.Map;

public interface Shlwapi
extends StdCallLibrary {
    public static final Shlwapi INSTANCE = (Shlwapi)Native.load((String)"Shlwapi", Shlwapi.class, (Map)W32APIOptions.DEFAULT_OPTIONS);

    public WinNT.HRESULT StrRetToStr(ShTypes.STRRET var1, Pointer var2, PointerByReference var3);

    public boolean PathIsUNC(String var1);
}

