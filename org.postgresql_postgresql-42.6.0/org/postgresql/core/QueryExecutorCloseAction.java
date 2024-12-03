/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.core;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.postgresql.core.PGStream;
import org.postgresql.core.QueryExecutorBase;

public class QueryExecutorCloseAction
implements Closeable {
    private static final Logger LOGGER = Logger.getLogger(QueryExecutorBase.class.getName());
    private static final AtomicReferenceFieldUpdater<QueryExecutorCloseAction, @Nullable PGStream> PG_STREAM_UPDATER = AtomicReferenceFieldUpdater.newUpdater(QueryExecutorCloseAction.class, PGStream.class, "pgStream");
    private volatile @Nullable PGStream pgStream;

    public QueryExecutorCloseAction(PGStream pgStream) {
        this.pgStream = pgStream;
    }

    public boolean isClosed() {
        PGStream pgStream = this.pgStream;
        return pgStream == null || pgStream.isClosed();
    }

    public void abort() {
        PGStream pgStream = this.pgStream;
        if (pgStream == null || !PG_STREAM_UPDATER.compareAndSet(this, pgStream, null)) {
            return;
        }
        try {
            LOGGER.log(Level.FINEST, " FE=> close socket");
            pgStream.getSocket().close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    @Override
    public void close() throws IOException {
        LOGGER.log(Level.FINEST, " FE=> Terminate");
        PGStream pgStream = this.pgStream;
        if (pgStream == null || !PG_STREAM_UPDATER.compareAndSet(this, pgStream, null)) {
            return;
        }
        this.sendCloseMessage(pgStream);
        if (pgStream.isClosed()) {
            return;
        }
        pgStream.flush();
        pgStream.close();
    }

    public void sendCloseMessage(PGStream pgStream) throws IOException {
        if (pgStream.isClosed()) {
            return;
        }
        int timeout = pgStream.getNetworkTimeout();
        if (timeout == 0 || timeout > 1000) {
            pgStream.setNetworkTimeout(1000);
        }
        pgStream.sendChar(88);
        pgStream.sendInteger4(4);
    }
}

