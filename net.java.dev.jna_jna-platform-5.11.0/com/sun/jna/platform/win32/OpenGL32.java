/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.win32.StdCallLibrary
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.StdCallLibrary;

public interface OpenGL32
extends StdCallLibrary {
    public static final OpenGL32 INSTANCE = (OpenGL32)Native.load((String)"opengl32", OpenGL32.class);

    public String glGetString(int var1);

    public WinDef.HGLRC wglCreateContext(WinDef.HDC var1);

    public WinDef.HGLRC wglGetCurrentContext();

    public boolean wglMakeCurrent(WinDef.HDC var1, WinDef.HGLRC var2);

    public boolean wglDeleteContext(WinDef.HGLRC var1);

    public Pointer wglGetProcAddress(String var1);
}

