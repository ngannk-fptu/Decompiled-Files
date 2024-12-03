/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.batch.internal;

import org.hibernate.engine.jdbc.batch.internal.BatchingBatch;
import org.hibernate.engine.jdbc.batch.internal.NonBatchingBatch;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;

final class SharedBatchBuildingCode {
    SharedBatchBuildingCode() {
    }

    static Batch buildBatch(int defaultJdbcBatchSize, BatchKey key, JdbcCoordinator jdbcCoordinator) {
        Integer sessionJdbcBatchSize = jdbcCoordinator.getJdbcSessionOwner().getJdbcBatchSize();
        int jdbcBatchSizeToUse = sessionJdbcBatchSize == null ? defaultJdbcBatchSize : sessionJdbcBatchSize;
        return jdbcBatchSizeToUse > 1 ? new BatchingBatch(key, jdbcCoordinator, jdbcBatchSizeToUse) : new NonBatchingBatch(key, jdbcCoordinator);
    }
}

