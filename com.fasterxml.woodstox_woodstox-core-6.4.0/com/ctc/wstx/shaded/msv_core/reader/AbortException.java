/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader;

public class AbortException
extends Exception {
    public static final AbortException theInstance = new AbortException();

    private AbortException() {
        super("aborted. Errors should have been reported");
    }
}

