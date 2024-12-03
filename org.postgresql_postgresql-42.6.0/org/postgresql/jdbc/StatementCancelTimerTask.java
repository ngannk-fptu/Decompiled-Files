/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.jdbc;

import java.util.TimerTask;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.postgresql.jdbc.PgStatement;

class StatementCancelTimerTask
extends TimerTask {
    private @Nullable PgStatement statement;

    StatementCancelTimerTask(PgStatement statement) {
        this.statement = statement;
    }

    @Override
    public boolean cancel() {
        boolean result = super.cancel();
        this.statement = null;
        return result;
    }

    @Override
    public void run() {
        PgStatement statement = this.statement;
        if (statement != null) {
            statement.cancelIfStillNeeded(this);
        }
        this.statement = null;
    }
}

