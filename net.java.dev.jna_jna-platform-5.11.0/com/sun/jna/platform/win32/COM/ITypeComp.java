/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.WString
 *  com.sun.jna.ptr.PointerByReference
 */
package com.sun.jna.platform.win32.COM;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public interface ITypeComp
extends IUnknown {
    public WinNT.HRESULT Bind(WString var1, WinDef.ULONG var2, WinDef.WORD var3, PointerByReference var4, OaIdl.DESCKIND.ByReference var5, OaIdl.BINDPTR.ByReference var6);

    public WinNT.HRESULT BindType(WString var1, WinDef.ULONG var2, PointerByReference var3, PointerByReference var4);
}

