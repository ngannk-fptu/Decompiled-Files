/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.ptr.PointerByReference
 */
package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public interface IConnectionPointContainer
extends IUnknown {
    public static final Guid.IID IID_IConnectionPointContainer = new Guid.IID("B196B284-BAB4-101A-B69C-00AA00341D07");

    public WinNT.HRESULT FindConnectionPoint(Guid.REFIID var1, PointerByReference var2);
}

