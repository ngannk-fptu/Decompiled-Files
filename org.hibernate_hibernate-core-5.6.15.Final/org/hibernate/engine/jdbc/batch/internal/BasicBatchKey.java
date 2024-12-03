/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.batch.internal;

import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.jdbc.Expectation;

public class BasicBatchKey
implements BatchKey {
    private final String comparison;
    private final int statementCount;
    private final Expectation expectation;

    public BasicBatchKey(String comparison, Expectation expectation) {
        this.comparison = comparison;
        this.statementCount = 1;
        this.expectation = expectation;
    }

    @Override
    public Expectation getExpectation() {
        return this.expectation;
    }

    @Override
    public int getBatchedStatementCount() {
        return this.statementCount;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BasicBatchKey that = (BasicBatchKey)o;
        return this.comparison.equals(that.comparison);
    }

    public int hashCode() {
        return this.comparison.hashCode();
    }
}

