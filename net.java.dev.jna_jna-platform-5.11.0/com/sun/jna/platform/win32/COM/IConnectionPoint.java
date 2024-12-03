/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.COM.IUnknownCallback;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

public interface IConnectionPoint
extends IUnknown {
    public static final Guid.IID IID_IConnectionPoint = new Guid.IID("B196B286-BAB4-101A-B69C-00AA00341D07");

    public WinNT.HRESULT GetConnectionInterface(Guid.IID var1);

    public WinNT.HRESULT Advise(IUnknownCallback var1, WinDef.DWORDByReference var2);

    public WinNT.HRESULT Unadvise(WinDef.DWORD var1);
}

