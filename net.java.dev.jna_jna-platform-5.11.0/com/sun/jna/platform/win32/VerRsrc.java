/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Pointer
 *  com.sun.jna.Structure
 *  com.sun.jna.Structure$ByReference
 *  com.sun.jna.Structure$FieldOrder
 */
package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef;

public interface VerRsrc {

    @Structure.FieldOrder(value={"dwSignature", "dwStrucVersion", "dwFileVersionMS", "dwFileVersionLS", "dwProductVersionMS", "dwProductVersionLS", "dwFileFlagsMask", "dwFileFlags", "dwFileOS", "dwFileType", "dwFileSubtype", "dwFileDateMS", "dwFileDateLS"})
    public static class VS_FIXEDFILEINFO
    extends Structure {
        public WinDef.DWORD dwSignature;
        public WinDef.DWORD dwStrucVersion;
        public WinDef.DWORD dwFileVersionMS;
        public WinDef.DWORD dwFileVersionLS;
        public WinDef.DWORD dwProductVersionMS;
        public WinDef.DWORD dwProductVersionLS;
        public WinDef.DWORD dwFileFlagsMask;
        public WinDef.DWORD dwFileFlags;
        public WinDef.DWORD dwFileOS;
        public WinDef.DWORD dwFileType;
        public WinDef.DWORD dwFileSubtype;
        public WinDef.DWORD dwFileDateMS;
        public WinDef.DWORD dwFileDateLS;

        public VS_FIXEDFILEINFO() {
        }

        public VS_FIXEDFILEINFO(Pointer memory) {
            super(memory);
            this.read();
        }

        public int getFileVersionMajor() {
            return this.dwFileVersionMS.intValue() >>> 16;
        }

        public int getFileVersionMinor() {
            return this.dwFileVersionMS.intValue() & 0xFFFF;
        }

        public int getFileVersionRevision() {
            return this.dwFileVersionLS.intValue() >>> 16;
        }

        public int getFileVersionBuild() {
            return this.dwFileVersionLS.intValue() & 0xFFFF;
        }

        public int getProductVersionMajor() {
            return this.dwProductVersionMS.intValue() >>> 16;
        }

        public int getProductVersionMinor() {
            return this.dwProductVersionMS.intValue() & 0xFFFF;
        }

        public int getProductVersionRevision() {
            return this.dwProductVersionLS.intValue() >>> 16;
        }

        public int getProductVersionBuild() {
            return this.dwProductVersionLS.intValue() & 0xFFFF;
        }

        public static class ByReference
        extends VS_FIXEDFILEINFO
        implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }
    }
}

