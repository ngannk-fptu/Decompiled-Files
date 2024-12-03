/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 */
package com.sun.jna.platform.unix.solaris;

import com.sun.jna.Native;
import com.sun.jna.platform.unix.solaris.Kstat2;

public class Kstat2StatusException
extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final int kstat2Status;

    public Kstat2StatusException(int ks) {
        this(ks, Kstat2StatusException.formatMessage(ks));
    }

    protected Kstat2StatusException(int ks, String msg) {
        super(msg);
        this.kstat2Status = ks;
    }

    public int getKstat2Status() {
        return this.kstat2Status;
    }

    private static String formatMessage(int ks) {
        String status = Kstat2.INSTANCE.kstat2_status_string(ks);
        if (ks == 10) {
            status = status + " (errno=" + Native.getLastError() + ")";
        }
        return "Kstat2Status error code " + ks + ": " + status;
    }
}

