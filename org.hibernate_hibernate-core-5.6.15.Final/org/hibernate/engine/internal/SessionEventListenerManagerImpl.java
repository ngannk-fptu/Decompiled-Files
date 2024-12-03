/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.internal;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import org.hibernate.SessionEventListener;
import org.hibernate.engine.spi.SessionEventListenerManager;

public class SessionEventListenerManagerImpl
implements SessionEventListenerManager,
Serializable {
    private SessionEventListener[] listeners;

    public SessionEventListenerManagerImpl(SessionEventListener ... initialListener) {
        this.listeners = initialListener;
    }

    @Override
    public void addListener(SessionEventListener ... additionalListeners) {
        Objects.requireNonNull(additionalListeners);
        SessionEventListener[] existing = this.listeners;
        if (existing == null) {
            this.listeners = Arrays.copyOf(additionalListeners, additionalListeners.length);
        } else {
            SessionEventListener[] newlist = new SessionEventListener[existing.length + additionalListeners.length];
            System.arraycopy(existing, 0, newlist, 0, existing.length);
            System.arraycopy(additionalListeners, 0, newlist, existing.length, additionalListeners.length);
            this.listeners = newlist;
        }
    }

    @Override
    public void transactionCompletion(boolean successful) {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.transactionCompletion(successful);
        }
    }

    @Override
    public void jdbcConnectionAcquisitionStart() {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.jdbcConnectionAcquisitionStart();
        }
    }

    @Override
    public void jdbcConnectionAcquisitionEnd() {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.jdbcConnectionAcquisitionEnd();
        }
    }

    @Override
    public void jdbcConnectionReleaseStart() {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.jdbcConnectionReleaseStart();
        }
    }

    @Override
    public void jdbcConnectionReleaseEnd() {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.jdbcConnectionReleaseEnd();
        }
    }

    @Override
    public void jdbcPrepareStatementStart() {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.jdbcPrepareStatementStart();
        }
    }

    @Override
    public void jdbcPrepareStatementEnd() {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.jdbcPrepareStatementEnd();
        }
    }

    @Override
    public void jdbcExecuteStatementStart() {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.jdbcExecuteStatementStart();
        }
    }

    @Override
    public void jdbcExecuteStatementEnd() {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.jdbcExecuteStatementEnd();
        }
    }

    @Override
    public void jdbcExecuteBatchStart() {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.jdbcExecuteBatchStart();
        }
    }

    @Override
    public void jdbcExecuteBatchEnd() {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.jdbcExecuteBatchEnd();
        }
    }

    @Override
    public void cachePutStart() {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.cachePutStart();
        }
    }

    @Override
    public void cachePutEnd() {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.cachePutEnd();
        }
    }

    @Override
    public void cacheGetStart() {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.cacheGetStart();
        }
    }

    @Override
    public void cacheGetEnd(boolean hit) {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.cacheGetEnd(hit);
        }
    }

    @Override
    public void flushStart() {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.flushStart();
        }
    }

    @Override
    public void flushEnd(int numberOfEntities, int numberOfCollections) {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.flushEnd(numberOfEntities, numberOfCollections);
        }
    }

    @Override
    public void partialFlushStart() {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.partialFlushStart();
        }
    }

    @Override
    public void partialFlushEnd(int numberOfEntities, int numberOfCollections) {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.partialFlushEnd(numberOfEntities, numberOfCollections);
        }
    }

    @Override
    public void dirtyCalculationStart() {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.dirtyCalculationStart();
        }
    }

    @Override
    public void dirtyCalculationEnd(boolean dirty) {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.dirtyCalculationEnd(dirty);
        }
    }

    @Override
    public void end() {
        if (this.listeners == null) {
            return;
        }
        for (SessionEventListener listener : this.listeners) {
            listener.end();
        }
    }
}

