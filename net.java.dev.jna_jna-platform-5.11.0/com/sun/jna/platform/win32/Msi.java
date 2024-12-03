/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.ptr.IntByReference
 *  com.sun.jna.win32.StdCallLibrary
 *  com.sun.jna.win32.W32APIOptions
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import java.util.Map;

public interface Msi
extends StdCallLibrary {
    public static final Msi INSTANCE = (Msi)Native.load((String)"msi", Msi.class, (Map)W32APIOptions.DEFAULT_OPTIONS);
    public static final int INSTALLSTATE_NOTUSED = -7;
    public static final int INSTALLSTATE_BADCONFIG = -6;
    public static final int INSTALLSTATE_INCOMPLETE = -5;
    public static final int INSTALLSTATE_SOURCEABSENT = -4;
    public static final int INSTALLSTATE_MOREDATA = -3;
    public static final int INSTALLSTATE_INVALIDARG = -2;
    public static final int INSTALLSTATE_UNKNOWN = -1;
    public static final int INSTALLSTATE_BROKEN = 0;
    public static final int INSTALLSTATE_ADVERTISED = 1;
    public static final int INSTALLSTATE_REMOVED = 1;
    public static final int INSTALLSTATE_ABSENT = 2;
    public static final int INSTALLSTATE_LOCAL = 3;
    public static final int INSTALLSTATE_SOURCE = 4;
    public static final int INSTALLSTATE_DEFAULT = 5;

    public int MsiGetComponentPath(String var1, String var2, char[] var3, IntByReference var4);

    public int MsiLocateComponent(String var1, char[] var2, IntByReference var3);

    public int MsiGetProductCode(String var1, char[] var2);

    public int MsiEnumComponents(WinDef.DWORD var1, char[] var2);
}

