/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Pointer
 *  com.sun.jna.ptr.PointerByReference
 */
package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.IConnectionPointContainer;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public class ConnectionPointContainer
extends Unknown
implements IConnectionPointContainer {
    public ConnectionPointContainer(Pointer pointer) {
        super(pointer);
    }

    public WinNT.HRESULT EnumConnectionPoints() {
        int vTableId = 3;
        throw new UnsupportedOperationException();
    }

    @Override
    public WinNT.HRESULT FindConnectionPoint(Guid.REFIID riid, PointerByReference ppCP) {
        int vTableId = 4;
        return (WinNT.HRESULT)((Object)this._invokeNativeObject(4, new Object[]{this.getPointer(), riid, ppCP}, WinNT.HRESULT.class));
    }
}

