/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Library
 *  com.sun.jna.Native
 *  com.sun.jna.win32.W32APIOptions
 */
package com.sun.jna.platform.wince;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.W32APIOptions;
import java.util.Map;

public interface CoreDLL
extends WinNT,
Library {
    public static final CoreDLL INSTANCE = (CoreDLL)Native.load((String)"coredll", CoreDLL.class, (Map)W32APIOptions.UNICODE_OPTIONS);
}

