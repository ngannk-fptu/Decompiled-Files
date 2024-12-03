/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Pointer
 *  com.sun.jna.ptr.IntByReference
 *  com.sun.jna.ptr.PointerByReference
 */
package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.IEnumVariant;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class EnumVariant
extends Unknown
implements IEnumVariant {
    public static final Guid.IID IID = new Guid.IID("{00020404-0000-0000-C000-000000000046}");
    public static final Guid.REFIID REFIID = new Guid.REFIID(IID);

    public EnumVariant() {
    }

    public EnumVariant(Pointer p) {
        this.setPointer(p);
    }

    @Override
    public Variant.VARIANT[] Next(int count) {
        Variant.VARIANT[] resultStaging = new Variant.VARIANT[count];
        IntByReference resultCount = new IntByReference();
        WinNT.HRESULT hresult = (WinNT.HRESULT)((Object)this._invokeNativeObject(3, new Object[]{this.getPointer(), resultStaging.length, resultStaging, resultCount}, WinNT.HRESULT.class));
        COMUtils.checkRC(hresult);
        Variant.VARIANT[] result = new Variant.VARIANT[resultCount.getValue()];
        System.arraycopy(resultStaging, 0, result, 0, resultCount.getValue());
        return result;
    }

    @Override
    public void Skip(int count) {
        WinNT.HRESULT hresult = (WinNT.HRESULT)((Object)this._invokeNativeObject(4, new Object[]{this.getPointer(), count}, WinNT.HRESULT.class));
        COMUtils.checkRC(hresult);
    }

    @Override
    public void Reset() {
        WinNT.HRESULT hresult = (WinNT.HRESULT)((Object)this._invokeNativeObject(5, new Object[]{this.getPointer()}, WinNT.HRESULT.class));
        COMUtils.checkRC(hresult);
    }

    @Override
    public EnumVariant Clone() {
        PointerByReference pbr = new PointerByReference();
        WinNT.HRESULT hresult = (WinNT.HRESULT)((Object)this._invokeNativeObject(6, new Object[]{this.getPointer(), pbr}, WinNT.HRESULT.class));
        COMUtils.checkRC(hresult);
        return new EnumVariant(pbr.getValue());
    }
}

