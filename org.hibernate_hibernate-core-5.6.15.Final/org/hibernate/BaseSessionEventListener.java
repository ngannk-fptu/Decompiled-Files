/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.SessionEventListener;

public class BaseSessionEventListener
implements SessionEventListener {
    @Override
    public void transactionCompletion(boolean successful) {
    }

    @Override
    public void jdbcConnectionAcquisitionStart() {
    }

    @Override
    public void jdbcConnectionAcquisitionEnd() {
    }

    @Override
    public void jdbcConnectionReleaseStart() {
    }

    @Override
    public void jdbcConnectionReleaseEnd() {
    }

    @Override
    public void jdbcPrepareStatementStart() {
    }

    @Override
    public void jdbcPrepareStatementEnd() {
    }

    @Override
    public void jdbcExecuteStatementStart() {
    }

    @Override
    public void jdbcExecuteStatementEnd() {
    }

    @Override
    public void jdbcExecuteBatchStart() {
    }

    @Override
    public void jdbcExecuteBatchEnd() {
    }

    @Override
    public void cachePutStart() {
    }

    @Override
    public void cachePutEnd() {
    }

    @Override
    public void cacheGetStart() {
    }

    @Override
    public void cacheGetEnd(boolean hit) {
    }

    @Override
    public void flushStart() {
    }

    @Override
    public void flushEnd(int numberOfEntities, int numberOfCollections) {
    }

    @Override
    public void partialFlushStart() {
    }

    @Override
    public void partialFlushEnd(int numberOfEntities, int numberOfCollections) {
    }

    @Override
    public void dirtyCalculationStart() {
    }

    @Override
    public void dirtyCalculationEnd(boolean dirty) {
    }

    @Override
    public void end() {
    }
}

