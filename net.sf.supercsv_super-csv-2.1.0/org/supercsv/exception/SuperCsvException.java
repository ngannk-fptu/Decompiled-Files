/*
 * Decompiled with CFR 0.152.
 */
package org.supercsv.exception;

import org.supercsv.util.CsvContext;

public class SuperCsvException
extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private CsvContext csvContext;

    public SuperCsvException(String msg) {
        super(msg);
    }

    public SuperCsvException(String msg, CsvContext context) {
        super(msg);
        this.csvContext = context;
    }

    public SuperCsvException(String msg, CsvContext context, Throwable t) {
        super(msg, t);
        this.csvContext = context;
    }

    public CsvContext getCsvContext() {
        return this.csvContext;
    }

    public String toString() {
        return String.format("%s: %s%ncontext=%s", this.getClass().getName(), this.getMessage(), this.csvContext);
    }
}

