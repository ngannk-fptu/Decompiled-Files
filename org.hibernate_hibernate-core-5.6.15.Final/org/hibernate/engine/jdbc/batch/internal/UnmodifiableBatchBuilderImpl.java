/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.batch.internal;

import org.hibernate.engine.jdbc.batch.internal.SharedBatchBuildingCode;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.hibernate.engine.jdbc.batch.spi.BatchBuilder;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;

final class UnmodifiableBatchBuilderImpl
implements BatchBuilder {
    private final int jdbcBatchSize;

    public UnmodifiableBatchBuilderImpl(int jdbcBatchSize) {
        this.jdbcBatchSize = jdbcBatchSize;
    }

    @Override
    public Batch buildBatch(BatchKey key, JdbcCoordinator jdbcCoordinator) {
        return SharedBatchBuildingCode.buildBatch(this.jdbcBatchSize, key, jdbcCoordinator);
    }
}

