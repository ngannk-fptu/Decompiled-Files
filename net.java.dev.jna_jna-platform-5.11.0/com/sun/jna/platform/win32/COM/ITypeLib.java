/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Pointer
 *  com.sun.jna.ptr.PointerByReference
 */
package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.IUnknown;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public interface ITypeLib
extends IUnknown {
    public WinDef.UINT GetTypeInfoCount();

    public WinNT.HRESULT GetTypeInfo(WinDef.UINT var1, PointerByReference var2);

    public WinNT.HRESULT GetTypeInfoType(WinDef.UINT var1, OaIdl.TYPEKIND.ByReference var2);

    public WinNT.HRESULT GetTypeInfoOfGuid(Guid.GUID var1, PointerByReference var2);

    public WinNT.HRESULT GetLibAttr(PointerByReference var1);

    public WinNT.HRESULT GetTypeComp(PointerByReference var1);

    public WinNT.HRESULT GetDocumentation(int var1, WTypes.BSTRByReference var2, WTypes.BSTRByReference var3, WinDef.DWORDByReference var4, WTypes.BSTRByReference var5);

    public WinNT.HRESULT IsName(WTypes.LPOLESTR var1, WinDef.ULONG var2, WinDef.BOOLByReference var3);

    public WinNT.HRESULT FindName(WTypes.LPOLESTR var1, WinDef.ULONG var2, Pointer[] var3, OaIdl.MEMBERID[] var4, WinDef.USHORTByReference var5);

    public void ReleaseTLibAttr(OaIdl.TLIBATTR var1);
}

