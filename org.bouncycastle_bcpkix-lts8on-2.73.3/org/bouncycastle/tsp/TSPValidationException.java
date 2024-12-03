/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp;

import org.bouncycastle.tsp.TSPException;

public class TSPValidationException
extends TSPException {
    private int failureCode = -1;

    public TSPValidationException(String message) {
        super(message);
    }

    public TSPValidationException(String message, int failureCode) {
        super(message);
        this.failureCode = failureCode;
    }

    public int getFailureCode() {
        return this.failureCode;
    }
}

