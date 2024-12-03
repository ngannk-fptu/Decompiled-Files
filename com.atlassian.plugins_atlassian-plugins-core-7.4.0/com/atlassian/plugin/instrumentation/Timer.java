/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.instrumentation.operations.OpTimer
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.instrumentation;

import com.atlassian.instrumentation.operations.OpTimer;
import com.google.common.base.Preconditions;
import java.io.Closeable;
import java.util.Optional;
import javax.annotation.Nonnull;

public class Timer
implements Closeable {
    private final Optional<OpTimer> opTimer;

    Timer(@Nonnull Optional<OpTimer> opTimer) {
        this.opTimer = (Optional)Preconditions.checkNotNull(opTimer);
    }

    public Optional<OpTimer> getOpTimer() {
        return this.opTimer;
    }

    @Override
    public void close() {
        if (this.opTimer.isPresent()) {
            this.opTimer.get().end();
        }
    }
}

