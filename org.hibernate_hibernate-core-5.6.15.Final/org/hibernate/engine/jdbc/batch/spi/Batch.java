/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.batch.spi;

import java.sql.PreparedStatement;
import org.hibernate.engine.jdbc.batch.spi.BatchKey;
import org.hibernate.engine.jdbc.batch.spi.BatchObserver;

public interface Batch {
    public BatchKey getKey();

    public void addObserver(BatchObserver var1);

    public PreparedStatement getBatchStatement(String var1, boolean var2);

    public void addToBatch();

    public void execute();

    public void release();
}

