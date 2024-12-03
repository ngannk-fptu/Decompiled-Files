/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context;

import org.springframework.context.Lifecycle;
import org.springframework.context.Phased;

public interface SmartLifecycle
extends Lifecycle,
Phased {
    public static final int DEFAULT_PHASE = Integer.MAX_VALUE;

    default public boolean isAutoStartup() {
        return true;
    }

    default public void stop(Runnable callback) {
        this.stop();
        callback.run();
    }

    @Override
    default public int getPhase() {
        return Integer.MAX_VALUE;
    }
}

