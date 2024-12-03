/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.Structure
 *  com.sun.jna.ptr.IntByReference
 *  com.sun.jna.win32.StdCallLibrary
 *  com.sun.jna.win32.W32APIOptions
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import java.util.Map;

public interface NtDll
extends StdCallLibrary {
    public static final NtDll INSTANCE = (NtDll)Native.load((String)"NtDll", NtDll.class, (Map)W32APIOptions.DEFAULT_OPTIONS);

    public int ZwQueryKey(WinNT.HANDLE var1, int var2, Structure var3, int var4, IntByReference var5);

    public int NtSetSecurityObject(WinNT.HANDLE var1, int var2, Pointer var3);

    public int NtQuerySecurityObject(WinNT.HANDLE var1, int var2, Pointer var3, int var4, IntByReference var5);

    public int RtlNtStatusToDosError(int var1);
}

