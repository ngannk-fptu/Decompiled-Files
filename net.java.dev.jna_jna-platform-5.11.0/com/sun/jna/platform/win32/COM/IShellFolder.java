/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Function
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.ptr.IntByReference
 *  com.sun.jna.ptr.PointerByReference
 */
package com.sun.jna.platform.win32.COM;

import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.ShTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public interface IShellFolder {
    public static final Guid.IID IID_ISHELLFOLDER = new Guid.IID("{000214E6-0000-0000-C000-000000000046}");

    public WinNT.HRESULT QueryInterface(Guid.REFIID var1, PointerByReference var2);

    public int AddRef();

    public int Release();

    public WinNT.HRESULT ParseDisplayName(WinDef.HWND var1, Pointer var2, String var3, IntByReference var4, PointerByReference var5, IntByReference var6);

    public WinNT.HRESULT EnumObjects(WinDef.HWND var1, int var2, PointerByReference var3);

    public WinNT.HRESULT BindToObject(Pointer var1, Pointer var2, Guid.REFIID var3, PointerByReference var4);

    public WinNT.HRESULT BindToStorage(Pointer var1, Pointer var2, Guid.REFIID var3, PointerByReference var4);

    public WinNT.HRESULT CompareIDs(WinDef.LPARAM var1, Pointer var2, Pointer var3);

    public WinNT.HRESULT CreateViewObject(WinDef.HWND var1, Guid.REFIID var2, PointerByReference var3);

    public WinNT.HRESULT GetAttributesOf(int var1, Pointer var2, IntByReference var3);

    public WinNT.HRESULT GetUIObjectOf(WinDef.HWND var1, int var2, Pointer var3, Guid.REFIID var4, IntByReference var5, PointerByReference var6);

    public WinNT.HRESULT GetDisplayNameOf(Pointer var1, int var2, ShTypes.STRRET var3);

    public WinNT.HRESULT SetNameOf(WinDef.HWND var1, Pointer var2, String var3, int var4, PointerByReference var5);

    public static class Converter {
        public static IShellFolder PointerToIShellFolder(PointerByReference ptr) {
            final Pointer interfacePointer = ptr.getValue();
            Pointer vTablePointer = interfacePointer.getPointer(0L);
            final Pointer[] vTable = new Pointer[13];
            vTablePointer.read(0L, vTable, 0, 13);
            return new IShellFolder(){

                @Override
                public WinNT.HRESULT QueryInterface(Guid.REFIID byValue, PointerByReference pointerByReference) {
                    Function f = Function.getFunction((Pointer)vTable[0], (int)63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, byValue, pointerByReference}));
                }

                @Override
                public int AddRef() {
                    Function f = Function.getFunction((Pointer)vTable[1], (int)63);
                    return f.invokeInt(new Object[]{interfacePointer});
                }

                @Override
                public int Release() {
                    Function f = Function.getFunction((Pointer)vTable[2], (int)63);
                    return f.invokeInt(new Object[]{interfacePointer});
                }

                @Override
                public WinNT.HRESULT ParseDisplayName(WinDef.HWND hwnd, Pointer pbc, String pszDisplayName, IntByReference pchEaten, PointerByReference ppidl, IntByReference pdwAttributes) {
                    Function f = Function.getFunction((Pointer)vTable[3], (int)63);
                    char[] pszDisplayNameNative = Native.toCharArray((String)pszDisplayName);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, hwnd, pbc, pszDisplayNameNative, pchEaten, ppidl, pdwAttributes}));
                }

                @Override
                public WinNT.HRESULT EnumObjects(WinDef.HWND hwnd, int grfFlags, PointerByReference ppenumIDList) {
                    Function f = Function.getFunction((Pointer)vTable[4], (int)63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, hwnd, grfFlags, ppenumIDList}));
                }

                @Override
                public WinNT.HRESULT BindToObject(Pointer pidl, Pointer pbc, Guid.REFIID riid, PointerByReference ppv) {
                    Function f = Function.getFunction((Pointer)vTable[5], (int)63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, pidl, pbc, riid, ppv}));
                }

                @Override
                public WinNT.HRESULT BindToStorage(Pointer pidl, Pointer pbc, Guid.REFIID riid, PointerByReference ppv) {
                    Function f = Function.getFunction((Pointer)vTable[6], (int)63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, pidl, pbc, riid, ppv}));
                }

                @Override
                public WinNT.HRESULT CompareIDs(WinDef.LPARAM lParam, Pointer pidl1, Pointer pidl2) {
                    Function f = Function.getFunction((Pointer)vTable[7], (int)63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, lParam, pidl1, pidl2}));
                }

                @Override
                public WinNT.HRESULT CreateViewObject(WinDef.HWND hwndOwner, Guid.REFIID riid, PointerByReference ppv) {
                    Function f = Function.getFunction((Pointer)vTable[8], (int)63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, hwndOwner, riid, ppv}));
                }

                @Override
                public WinNT.HRESULT GetAttributesOf(int cidl, Pointer apidl, IntByReference rgfInOut) {
                    Function f = Function.getFunction((Pointer)vTable[9], (int)63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, cidl, apidl, rgfInOut}));
                }

                @Override
                public WinNT.HRESULT GetUIObjectOf(WinDef.HWND hwndOwner, int cidl, Pointer apidl, Guid.REFIID riid, IntByReference rgfReserved, PointerByReference ppv) {
                    Function f = Function.getFunction((Pointer)vTable[10], (int)63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, hwndOwner, cidl, apidl, riid, rgfReserved, ppv}));
                }

                @Override
                public WinNT.HRESULT GetDisplayNameOf(Pointer pidl, int flags, ShTypes.STRRET pName) {
                    Function f = Function.getFunction((Pointer)vTable[11], (int)63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, pidl, flags, pName}));
                }

                @Override
                public WinNT.HRESULT SetNameOf(WinDef.HWND hwnd, Pointer pidl, String pszName, int uFlags, PointerByReference ppidlOut) {
                    Function f = Function.getFunction((Pointer)vTable[12], (int)63);
                    return new WinNT.HRESULT(f.invokeInt(new Object[]{interfacePointer, hwnd, pidl, pszName, uFlags, ppidlOut}));
                }
            };
        }
    }
}

