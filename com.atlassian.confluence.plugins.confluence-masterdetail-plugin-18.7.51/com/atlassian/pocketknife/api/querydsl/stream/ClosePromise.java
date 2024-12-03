/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.concurrent.NotThreadSafe
 */
package com.atlassian.pocketknife.api.querydsl.stream;

import com.google.common.base.Preconditions;
import java.io.Closeable;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class ClosePromise
implements Closeable {
    private static final Runnable NOOP_RUNNABLE = () -> {};
    private boolean closed = false;
    private final ClosePromise parentPromise;
    private final Runnable closeEffect;

    public static ClosePromise NOOP() {
        return new ClosePromise();
    }

    private ClosePromise() {
        this(null, NOOP_RUNNABLE);
    }

    public ClosePromise(Runnable closeEffect) {
        this(ClosePromise.NOOP(), closeEffect);
    }

    public ClosePromise(ClosePromise parentPromise, Runnable closeEffect) {
        this.parentPromise = parentPromise;
        this.closeEffect = (Runnable)Preconditions.checkNotNull((Object)closeEffect);
    }

    @Override
    public void close() {
        if (!this.closed) {
            this.closed = true;
            try {
                this.closeEffect.run();
            }
            finally {
                if (this.parentPromise != null) {
                    this.parentPromise.close();
                }
            }
        }
    }

    public boolean isClosed() {
        return this.closed;
    }
}

