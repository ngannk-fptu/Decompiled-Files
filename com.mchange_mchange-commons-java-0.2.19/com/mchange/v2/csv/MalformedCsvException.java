/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.csv;

import com.mchange.lang.PotentiallySecondaryException;

public class MalformedCsvException
extends PotentiallySecondaryException {
    public MalformedCsvException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public MalformedCsvException(Throwable throwable) {
        super(throwable);
    }

    public MalformedCsvException(String string) {
        super(string);
    }

    public MalformedCsvException() {
    }
}

