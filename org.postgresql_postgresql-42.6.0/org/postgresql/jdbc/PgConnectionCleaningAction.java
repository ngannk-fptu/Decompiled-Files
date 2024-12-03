/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.jdbc;

import java.io.Closeable;
import java.io.IOException;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.postgresql.Driver;
import org.postgresql.jdbc.PgConnection;
import org.postgresql.jdbc.ResourceLock;
import org.postgresql.util.GT;
import org.postgresql.util.LazyCleaner;

class PgConnectionCleaningAction
implements LazyCleaner.CleaningAction<IOException> {
    private static final Logger LOGGER = Logger.getLogger(PgConnection.class.getName());
    private final ResourceLock lock;
    private @Nullable Throwable openStackTrace;
    private final Closeable queryExecutorCloseAction;
    private @Nullable Timer cancelTimer;

    PgConnectionCleaningAction(ResourceLock lock, @Nullable Throwable openStackTrace, Closeable queryExecutorCloseAction) {
        this.lock = lock;
        this.openStackTrace = openStackTrace;
        this.queryExecutorCloseAction = queryExecutorCloseAction;
    }

    public Timer getTimer() {
        try (ResourceLock ignore = this.lock.obtain();){
            Timer cancelTimer = this.cancelTimer;
            if (cancelTimer == null) {
                this.cancelTimer = cancelTimer = Driver.getSharedTimer().getTimer();
            }
            Timer timer = cancelTimer;
            return timer;
        }
    }

    public void releaseTimer() {
        try (ResourceLock ignore = this.lock.obtain();){
            if (this.cancelTimer != null) {
                this.cancelTimer = null;
                Driver.getSharedTimer().releaseTimer();
            }
        }
    }

    public void purgeTimerTasks() {
        try (ResourceLock ignore = this.lock.obtain();){
            Timer timer = this.cancelTimer;
            if (timer != null) {
                timer.purge();
            }
        }
    }

    @Override
    public void onClean(boolean leak) throws IOException {
        if (leak && this.openStackTrace != null) {
            LOGGER.log(Level.WARNING, GT.tr("Leak detected: Connection.close() was not called", new Object[0]), this.openStackTrace);
        }
        this.openStackTrace = null;
        this.releaseTimer();
        this.queryExecutorCloseAction.close();
    }
}

