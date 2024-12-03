/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.ptr.PointerByReference
 *  com.sun.jna.win32.StdCallLibrary
 *  com.sun.jna.win32.W32APIOptions
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import java.util.Map;

public interface GDI32
extends StdCallLibrary {
    public static final GDI32 INSTANCE = (GDI32)Native.load((String)"gdi32", GDI32.class, (Map)W32APIOptions.DEFAULT_OPTIONS);
    public static final int SRCCOPY = 0xCC0020;

    public WinDef.HRGN ExtCreateRegion(Pointer var1, int var2, WinGDI.RGNDATA var3);

    public int CombineRgn(WinDef.HRGN var1, WinDef.HRGN var2, WinDef.HRGN var3, int var4);

    public WinDef.HRGN CreateRectRgn(int var1, int var2, int var3, int var4);

    public WinDef.HRGN CreateRoundRectRgn(int var1, int var2, int var3, int var4, int var5, int var6);

    public WinDef.HRGN CreatePolyPolygonRgn(WinDef.POINT[] var1, int[] var2, int var3, int var4);

    public boolean SetRectRgn(WinDef.HRGN var1, int var2, int var3, int var4, int var5);

    public int SetPixel(WinDef.HDC var1, int var2, int var3, int var4);

    public WinDef.HDC CreateCompatibleDC(WinDef.HDC var1);

    public boolean DeleteDC(WinDef.HDC var1);

    public WinDef.HBITMAP CreateDIBitmap(WinDef.HDC var1, WinGDI.BITMAPINFOHEADER var2, int var3, Pointer var4, WinGDI.BITMAPINFO var5, int var6);

    public WinDef.HBITMAP CreateDIBSection(WinDef.HDC var1, WinGDI.BITMAPINFO var2, int var3, PointerByReference var4, Pointer var5, int var6);

    public WinDef.HBITMAP CreateCompatibleBitmap(WinDef.HDC var1, int var2, int var3);

    public WinNT.HANDLE SelectObject(WinDef.HDC var1, WinNT.HANDLE var2);

    public boolean DeleteObject(WinNT.HANDLE var1);

    public int GetDeviceCaps(WinDef.HDC var1, int var2);

    public int GetDIBits(WinDef.HDC var1, WinDef.HBITMAP var2, int var3, int var4, Pointer var5, WinGDI.BITMAPINFO var6, int var7);

    public int ChoosePixelFormat(WinDef.HDC var1, WinGDI.PIXELFORMATDESCRIPTOR.ByReference var2);

    public boolean SetPixelFormat(WinDef.HDC var1, int var2, WinGDI.PIXELFORMATDESCRIPTOR.ByReference var3);

    public int GetObject(WinNT.HANDLE var1, int var2, Pointer var3);

    public boolean BitBlt(WinDef.HDC var1, int var2, int var3, int var4, int var5, WinDef.HDC var6, int var7, int var8, int var9);
}

