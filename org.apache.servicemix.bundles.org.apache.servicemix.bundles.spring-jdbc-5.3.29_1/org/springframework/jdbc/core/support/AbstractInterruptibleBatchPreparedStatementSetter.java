/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.core.support;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.jdbc.core.InterruptibleBatchPreparedStatementSetter;

public abstract class AbstractInterruptibleBatchPreparedStatementSetter
implements InterruptibleBatchPreparedStatementSetter {
    private boolean exhausted;

    @Override
    public final void setValues(PreparedStatement ps, int i) throws SQLException {
        this.exhausted = !this.setValuesIfAvailable(ps, i);
    }

    @Override
    public final boolean isBatchExhausted(int i) {
        return this.exhausted;
    }

    @Override
    public int getBatchSize() {
        return Integer.MAX_VALUE;
    }

    protected abstract boolean setValuesIfAvailable(PreparedStatement var1, int var2) throws SQLException;
}

