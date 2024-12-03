/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j.grid;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.grid.CommandResult;
import io.github.bucket4j.grid.GridCommand;
import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface GridProxy<K extends Serializable> {
    public <T extends Serializable> CommandResult<T> execute(K var1, GridCommand<T> var2);

    public void createInitialState(K var1, BucketConfiguration var2);

    public <T extends Serializable> T createInitialStateAndExecute(K var1, BucketConfiguration var2, GridCommand<T> var3);

    public <T extends Serializable> CompletableFuture<CommandResult<T>> executeAsync(K var1, GridCommand<T> var2);

    public <T extends Serializable> CompletableFuture<T> createInitialStateAndExecuteAsync(K var1, BucketConfiguration var2, GridCommand<T> var3);

    public Optional<BucketConfiguration> getConfiguration(K var1);

    public boolean isAsyncModeSupported();
}

