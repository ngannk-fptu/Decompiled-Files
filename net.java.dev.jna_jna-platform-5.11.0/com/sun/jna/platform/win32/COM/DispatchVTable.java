/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Pointer
 *  com.sun.jna.Structure
 *  com.sun.jna.Structure$ByReference
 *  com.sun.jna.Structure$FieldOrder
 *  com.sun.jna.WString
 *  com.sun.jna.ptr.IntByReference
 *  com.sun.jna.ptr.PointerByReference
 *  com.sun.jna.win32.StdCallLibrary$StdCallCallback
 */
package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

@Structure.FieldOrder(value={"QueryInterfaceCallback", "AddRefCallback", "ReleaseCallback", "GetTypeInfoCountCallback", "GetTypeInfoCallback", "GetIDsOfNamesCallback", "InvokeCallback"})
public class DispatchVTable
extends Structure {
    public QueryInterfaceCallback QueryInterfaceCallback;
    public AddRefCallback AddRefCallback;
    public ReleaseCallback ReleaseCallback;
    public GetTypeInfoCountCallback GetTypeInfoCountCallback;
    public GetTypeInfoCallback GetTypeInfoCallback;
    public GetIDsOfNamesCallback GetIDsOfNamesCallback;
    public InvokeCallback InvokeCallback;

    public static interface InvokeCallback
    extends StdCallLibrary.StdCallCallback {
        public WinNT.HRESULT invoke(Pointer var1, OaIdl.DISPID var2, Guid.REFIID var3, WinDef.LCID var4, WinDef.WORD var5, OleAuto.DISPPARAMS.ByReference var6, Variant.VARIANT.ByReference var7, OaIdl.EXCEPINFO.ByReference var8, IntByReference var9);
    }

    public static interface GetIDsOfNamesCallback
    extends StdCallLibrary.StdCallCallback {
        public WinNT.HRESULT invoke(Pointer var1, Guid.REFIID var2, WString[] var3, int var4, WinDef.LCID var5, OaIdl.DISPIDByReference var6);
    }

    public static interface GetTypeInfoCallback
    extends StdCallLibrary.StdCallCallback {
        public WinNT.HRESULT invoke(Pointer var1, WinDef.UINT var2, WinDef.LCID var3, PointerByReference var4);
    }

    public static interface GetTypeInfoCountCallback
    extends StdCallLibrary.StdCallCallback {
        public WinNT.HRESULT invoke(Pointer var1, WinDef.UINTByReference var2);
    }

    public static interface ReleaseCallback
    extends StdCallLibrary.StdCallCallback {
        public int invoke(Pointer var1);
    }

    public static interface AddRefCallback
    extends StdCallLibrary.StdCallCallback {
        public int invoke(Pointer var1);
    }

    public static interface QueryInterfaceCallback
    extends StdCallLibrary.StdCallCallback {
        public WinNT.HRESULT invoke(Pointer var1, Guid.REFIID var2, PointerByReference var3);
    }

    public static class ByReference
    extends DispatchVTable
    implements Structure.ByReference {
    }
}

