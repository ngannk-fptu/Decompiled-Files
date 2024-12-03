/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.ptr.ByReference
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.ByReference;

public interface WinReg {
    public static final HKEY HKEY_CLASSES_ROOT = new HKEY(Integer.MIN_VALUE);
    public static final HKEY HKEY_CURRENT_USER = new HKEY(-2147483647);
    public static final HKEY HKEY_LOCAL_MACHINE = new HKEY(-2147483646);
    public static final HKEY HKEY_USERS = new HKEY(-2147483645);
    public static final HKEY HKEY_PERFORMANCE_DATA = new HKEY(-2147483644);
    public static final HKEY HKEY_PERFORMANCE_TEXT = new HKEY(-2147483568);
    public static final HKEY HKEY_PERFORMANCE_NLSTEXT = new HKEY(-2147483552);
    public static final HKEY HKEY_CURRENT_CONFIG = new HKEY(-2147483643);
    public static final HKEY HKEY_DYN_DATA = new HKEY(-2147483642);
    public static final HKEY HKEY_CURRENT_USER_LOCAL_SETTINGS = new HKEY(-2147483641);

    public static class HKEYByReference
    extends ByReference {
        public HKEYByReference() {
            this(null);
        }

        public HKEYByReference(HKEY h) {
            super(Native.POINTER_SIZE);
            this.setValue(h);
        }

        public void setValue(HKEY h) {
            this.getPointer().setPointer(0L, h != null ? h.getPointer() : null);
        }

        public HKEY getValue() {
            Pointer p = this.getPointer().getPointer(0L);
            if (p == null) {
                return null;
            }
            if (WinBase.INVALID_HANDLE_VALUE.getPointer().equals((Object)p)) {
                return (HKEY)WinBase.INVALID_HANDLE_VALUE;
            }
            HKEY h = new HKEY();
            h.setPointer(p);
            return h;
        }
    }

    public static class HKEY
    extends WinNT.HANDLE {
        public HKEY() {
        }

        public HKEY(Pointer p) {
            super(p);
        }

        public HKEY(int value) {
            super(new Pointer((long)value));
        }
    }
}

