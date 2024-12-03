/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Library
 *  com.sun.jna.Native
 */
package com.sun.jna.platform.linux;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface LibRT
extends Library {
    public static final LibRT INSTANCE = (LibRT)Native.load((String)"rt", LibRT.class);

    public int shm_open(String var1, int var2, int var3);

    public int shm_unlink(String var1);
}

