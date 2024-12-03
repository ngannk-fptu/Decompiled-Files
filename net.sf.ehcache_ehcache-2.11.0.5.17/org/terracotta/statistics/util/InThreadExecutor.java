/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics.util;

import java.util.concurrent.Executor;

public final class InThreadExecutor
implements Executor {
    public static final Executor INSTANCE = new InThreadExecutor();

    private InThreadExecutor() {
    }

    @Override
    public void execute(Runnable r) {
        r.run();
    }
}

