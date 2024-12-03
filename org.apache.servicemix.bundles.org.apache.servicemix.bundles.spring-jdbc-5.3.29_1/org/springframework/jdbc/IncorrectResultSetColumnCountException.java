/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataRetrievalFailureException
 */
package org.springframework.jdbc;

import org.springframework.dao.DataRetrievalFailureException;

public class IncorrectResultSetColumnCountException
extends DataRetrievalFailureException {
    private final int expectedCount;
    private final int actualCount;

    public IncorrectResultSetColumnCountException(int expectedCount, int actualCount) {
        super("Incorrect column count: expected " + expectedCount + ", actual " + actualCount);
        this.expectedCount = expectedCount;
        this.actualCount = actualCount;
    }

    public IncorrectResultSetColumnCountException(String msg, int expectedCount, int actualCount) {
        super(msg);
        this.expectedCount = expectedCount;
        this.actualCount = actualCount;
    }

    public int getExpectedCount() {
        return this.expectedCount;
    }

    public int getActualCount() {
        return this.actualCount;
    }
}

