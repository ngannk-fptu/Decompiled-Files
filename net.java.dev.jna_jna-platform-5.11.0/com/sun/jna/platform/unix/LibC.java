/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Library
 *  com.sun.jna.Native
 */
package com.sun.jna.platform.unix;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.unix.LibCAPI;

public interface LibC
extends LibCAPI,
Library {
    public static final String NAME = "c";
    public static final LibC INSTANCE = (LibC)Native.load((String)"c", LibC.class);
}

