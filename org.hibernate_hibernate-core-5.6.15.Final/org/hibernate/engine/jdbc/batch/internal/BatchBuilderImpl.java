/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.batch.internal;

import org.hibernate.engine.jdbc.batch.internal.BatchBuilderMXBean;
import org.hibernate.engine.jdbc.batch.internal.SharedBatchBuildingCode;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.hibernate.engine.jdbc.batch.spi.BatchBuilder;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.service.spi.Manageable;

public class BatchBuilderImpl
implements BatchBuilder,
Manageable,
BatchBuilderMXBean {
    private volatile int jdbcBatchSize;

    public BatchBuilderImpl() {
    }

    public BatchBuilderImpl(int jdbcBatchSize) {
        this.jdbcBatchSize = jdbcBatchSize;
    }

    @Override
    public int getJdbcBatchSize() {
        return this.jdbcBatchSize;
    }

    @Override
    public void setJdbcBatchSize(int jdbcBatchSize) {
        this.jdbcBatchSize = jdbcBatchSize;
    }

    @Override
    public Batch buildBatch(BatchKey key, JdbcCoordinator jdbcCoordinator) {
        return SharedBatchBuildingCode.buildBatch(this.jdbcBatchSize, key, jdbcCoordinator);
    }
}

