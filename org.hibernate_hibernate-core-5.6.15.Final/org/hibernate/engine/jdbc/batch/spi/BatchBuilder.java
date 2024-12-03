/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.batch.spi;

import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.service.Service;

public interface BatchBuilder
extends Service {
    public Batch buildBatch(BatchKey var1, JdbcCoordinator var2);
}

