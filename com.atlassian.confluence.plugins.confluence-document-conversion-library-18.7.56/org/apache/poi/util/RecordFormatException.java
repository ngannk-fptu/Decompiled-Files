/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.util;

public class RecordFormatException
extends RuntimeException {
    public RecordFormatException(String exception) {
        super(exception);
    }

    public RecordFormatException(String exception, Throwable thr) {
        super(exception, thr);
    }

    public RecordFormatException(Throwable thr) {
        super(thr);
    }

    public static void check(boolean assertTrue, String message) {
        if (!assertTrue) {
            throw new RecordFormatException(message);
        }
    }
}

