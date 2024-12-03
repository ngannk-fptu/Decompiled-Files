/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.batch.spi;

import org.hibernate.jdbc.Expectation;

public interface BatchKey {
    public int getBatchedStatementCount();

    public Expectation getExpectation();
}

