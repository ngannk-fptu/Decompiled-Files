/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Structure
 *  com.sun.jna.Structure$FieldOrder
 *  com.sun.jna.platform.win32.WinNT
 */
package oshi.jna.platform.windows;

import com.sun.jna.Structure;

public interface WinNT
extends com.sun.jna.platform.win32.WinNT {

    @Structure.FieldOrder(value={"TokenIsElevated"})
    public static class TOKEN_ELEVATION
    extends Structure {
        public int TokenIsElevated;
    }
}

