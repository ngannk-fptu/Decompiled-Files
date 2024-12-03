/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jdbc;

import org.hibernate.jdbc.TooManyRowsAffectedException;

public class BatchedTooManyRowsAffectedException
extends TooManyRowsAffectedException {
    private final int batchPosition;

    public BatchedTooManyRowsAffectedException(String message, int expectedRowCount, int actualRowCount, int batchPosition) {
        super(message, expectedRowCount, actualRowCount);
        this.batchPosition = batchPosition;
    }

    public int getBatchPosition() {
        return this.batchPosition;
    }
}

