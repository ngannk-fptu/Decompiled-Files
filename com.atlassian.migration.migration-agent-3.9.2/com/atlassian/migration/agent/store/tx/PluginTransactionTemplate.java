/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.tx;

import java.util.function.Supplier;

public interface PluginTransactionTemplate {
    public <T> T on(boolean var1, Supplier<T> var2);

    default public void on(boolean readonly, Runnable action) {
        this.on(readonly, () -> {
            action.run();
            return null;
        });
    }

    default public <T> T read(Supplier<T> action) {
        return this.on(true, action);
    }

    default public void read(Runnable action) {
        this.on(true, action);
    }

    default public <T> T write(Supplier<T> action) {
        return this.on(false, action);
    }

    default public void write(Runnable action) {
        this.on(false, action);
    }
}

