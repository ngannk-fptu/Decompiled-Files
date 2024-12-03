/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Pointer
 *  com.sun.jna.Structure$ByReference
 *  com.sun.jna.ptr.PointerByReference
 */
package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.COM.IRunningObjectTable;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public class RunningObjectTable
extends Unknown
implements IRunningObjectTable {
    public RunningObjectTable() {
    }

    public RunningObjectTable(Pointer pointer) {
        super(pointer);
    }

    @Override
    public WinNT.HRESULT Register(WinDef.DWORD grfFlags, Pointer punkObject, Pointer pmkObjectName, WinDef.DWORDByReference pdwRegister) {
        int vTableId = 3;
        WinNT.HRESULT hr = (WinNT.HRESULT)((Object)this._invokeNativeObject(3, new Object[]{this.getPointer(), grfFlags, punkObject, pmkObjectName, pdwRegister}, WinNT.HRESULT.class));
        return hr;
    }

    @Override
    public WinNT.HRESULT Revoke(WinDef.DWORD dwRegister) {
        int vTableId = 4;
        WinNT.HRESULT hr = (WinNT.HRESULT)((Object)this._invokeNativeObject(4, new Object[]{this.getPointer(), dwRegister}, WinNT.HRESULT.class));
        return hr;
    }

    @Override
    public WinNT.HRESULT IsRunning(Pointer pmkObjectName) {
        int vTableId = 5;
        WinNT.HRESULT hr = (WinNT.HRESULT)((Object)this._invokeNativeObject(5, new Object[]{this.getPointer(), pmkObjectName}, WinNT.HRESULT.class));
        return hr;
    }

    @Override
    public WinNT.HRESULT GetObject(Pointer pmkObjectName, PointerByReference ppunkObject) {
        int vTableId = 6;
        WinNT.HRESULT hr = (WinNT.HRESULT)((Object)this._invokeNativeObject(6, new Object[]{this.getPointer(), pmkObjectName, ppunkObject}, WinNT.HRESULT.class));
        return hr;
    }

    @Override
    public WinNT.HRESULT NoteChangeTime(WinDef.DWORD dwRegister, WinBase.FILETIME pfiletime) {
        int vTableId = 7;
        WinNT.HRESULT hr = (WinNT.HRESULT)((Object)this._invokeNativeObject(7, new Object[]{this.getPointer(), dwRegister, pfiletime}, WinNT.HRESULT.class));
        return hr;
    }

    @Override
    public WinNT.HRESULT GetTimeOfLastChange(Pointer pmkObjectName, WinBase.FILETIME.ByReference pfiletime) {
        int vTableId = 8;
        WinNT.HRESULT hr = (WinNT.HRESULT)((Object)this._invokeNativeObject(8, new Object[]{this.getPointer(), pmkObjectName, pfiletime}, WinNT.HRESULT.class));
        return hr;
    }

    @Override
    public WinNT.HRESULT EnumRunning(PointerByReference ppenumMoniker) {
        int vTableId = 9;
        WinNT.HRESULT hr = (WinNT.HRESULT)((Object)this._invokeNativeObject(9, new Object[]{this.getPointer(), ppenumMoniker}, WinNT.HRESULT.class));
        return hr;
    }

    public static class ByReference
    extends RunningObjectTable
    implements Structure.ByReference {
    }
}

