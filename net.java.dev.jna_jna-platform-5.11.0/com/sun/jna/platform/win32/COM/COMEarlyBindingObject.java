/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.WString
 *  com.sun.jna.ptr.IntByReference
 *  com.sun.jna.ptr.PointerByReference
 */
package com.sun.jna.platform.win32.COM;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.COM.COMBindingBaseObject;
import com.sun.jna.platform.win32.COM.IDispatch;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class COMEarlyBindingObject
extends COMBindingBaseObject
implements IDispatch {
    public COMEarlyBindingObject(Guid.CLSID clsid, boolean useActiveInstance, int dwClsContext) {
        super(clsid, useActiveInstance, dwClsContext);
    }

    protected String getStringProperty(OaIdl.DISPID dispId) {
        Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
        this.oleMethod(2, result, dispId);
        return result.getValue().toString();
    }

    protected void setProperty(OaIdl.DISPID dispId, boolean value) {
        this.oleMethod(4, null, dispId, new Variant.VARIANT(value));
    }

    @Override
    public WinNT.HRESULT QueryInterface(Guid.REFIID riid, PointerByReference ppvObject) {
        return this.getIDispatch().QueryInterface(riid, ppvObject);
    }

    @Override
    public int AddRef() {
        return this.getIDispatch().AddRef();
    }

    @Override
    public int Release() {
        return this.getIDispatch().Release();
    }

    @Override
    public WinNT.HRESULT GetTypeInfoCount(WinDef.UINTByReference pctinfo) {
        return this.getIDispatch().GetTypeInfoCount(pctinfo);
    }

    @Override
    public WinNT.HRESULT GetTypeInfo(WinDef.UINT iTInfo, WinDef.LCID lcid, PointerByReference ppTInfo) {
        return this.getIDispatch().GetTypeInfo(iTInfo, lcid, ppTInfo);
    }

    @Override
    public WinNT.HRESULT GetIDsOfNames(Guid.REFIID riid, WString[] rgszNames, int cNames, WinDef.LCID lcid, OaIdl.DISPIDByReference rgDispId) {
        return this.getIDispatch().GetIDsOfNames(riid, rgszNames, cNames, lcid, rgDispId);
    }

    @Override
    public WinNT.HRESULT Invoke(OaIdl.DISPID dispIdMember, Guid.REFIID riid, WinDef.LCID lcid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams, Variant.VARIANT.ByReference pVarResult, OaIdl.EXCEPINFO.ByReference pExcepInfo, IntByReference puArgErr) {
        return this.getIDispatch().Invoke(dispIdMember, riid, lcid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr);
    }
}

