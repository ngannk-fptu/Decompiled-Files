/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.Structure$ByReference
 *  com.sun.jna.ptr.IntByReference
 *  com.sun.jna.win32.StdCallLibrary
 *  com.sun.jna.win32.W32APIOptions
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinRas;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import java.util.Map;

public interface Rasapi32
extends StdCallLibrary {
    public static final Rasapi32 INSTANCE = (Rasapi32)Native.load((String)"Rasapi32", Rasapi32.class, (Map)W32APIOptions.DEFAULT_OPTIONS);

    public int RasDial(WinRas.RASDIALEXTENSIONS.ByReference var1, String var2, WinRas.RASDIALPARAMS.ByReference var3, int var4, WinRas.RasDialFunc2 var5, WinNT.HANDLEByReference var6);

    public int RasEnumConnections(WinRas.RASCONN[] var1, IntByReference var2, IntByReference var3);

    public int RasGetConnectionStatistics(WinNT.HANDLE var1, Structure.ByReference var2);

    public int RasGetConnectStatus(WinNT.HANDLE var1, Structure.ByReference var2);

    public int RasGetCredentials(String var1, String var2, WinRas.RASCREDENTIALS.ByReference var3);

    public int RasGetEntryProperties(String var1, String var2, WinRas.RASENTRY.ByReference var3, IntByReference var4, Pointer var5, Pointer var6);

    public int RasGetProjectionInfo(WinNT.HANDLE var1, int var2, Pointer var3, IntByReference var4);

    public int RasHangUp(WinNT.HANDLE var1);

    public int RasSetEntryProperties(String var1, String var2, WinRas.RASENTRY.ByReference var3, int var4, byte[] var5, int var6);

    public int RasGetEntryDialParams(String var1, WinRas.RASDIALPARAMS.ByReference var2, WinDef.BOOLByReference var3);

    public int RasGetErrorString(int var1, char[] var2, int var3);
}

