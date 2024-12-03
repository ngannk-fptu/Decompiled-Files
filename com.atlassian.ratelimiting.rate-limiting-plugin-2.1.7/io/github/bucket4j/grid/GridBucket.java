/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j.grid;

import io.github.bucket4j.AbstractBucket;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.BucketExceptions;
import io.github.bucket4j.BucketListener;
import io.github.bucket4j.BucketState;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.EstimationProbe;
import io.github.bucket4j.IncompatibleConfigurationException;
import io.github.bucket4j.Nothing;
import io.github.bucket4j.grid.AddTokensCommand;
import io.github.bucket4j.grid.BucketNotFoundException;
import io.github.bucket4j.grid.CommandResult;
import io.github.bucket4j.grid.ConsumeAsMuchAsPossibleCommand;
import io.github.bucket4j.grid.CreateSnapshotCommand;
import io.github.bucket4j.grid.EstimateAbilityToConsumeCommand;
import io.github.bucket4j.grid.GetAvailableTokensCommand;
import io.github.bucket4j.grid.GridCommand;
import io.github.bucket4j.grid.GridProxy;
import io.github.bucket4j.grid.RecoveryStrategy;
import io.github.bucket4j.grid.ReplaceConfigurationOrReturnPreviousCommand;
import io.github.bucket4j.grid.ReserveAndCalculateTimeToSleepCommand;
import io.github.bucket4j.grid.TryConsumeAndReturnRemainingTokensCommand;
import io.github.bucket4j.grid.TryConsumeCommand;
import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class GridBucket<K extends Serializable>
extends AbstractBucket {
    private final K key;
    private final GridProxy<K> gridProxy;
    private final RecoveryStrategy recoveryStrategy;
    private final Supplier<BucketConfiguration> configurationSupplier;

    public static <T extends Serializable> GridBucket<T> createLazyBucket(T key, Supplier<BucketConfiguration> configurationSupplier, GridProxy<T> gridProxy) {
        return new GridBucket<T>(BucketListener.NOPE, key, configurationSupplier, gridProxy, RecoveryStrategy.RECONSTRUCT, false);
    }

    public static <T extends Serializable> GridBucket<T> createInitializedBucket(T key, BucketConfiguration configuration, GridProxy<T> gridProxy, RecoveryStrategy recoveryStrategy) {
        return new GridBucket<T>(BucketListener.NOPE, key, () -> configuration, gridProxy, recoveryStrategy, true);
    }

    @Override
    public Bucket toListenable(BucketListener listener) {
        return new GridBucket<K>(listener, this.key, this.configurationSupplier, this.gridProxy, this.recoveryStrategy, false);
    }

    private GridBucket(BucketListener listener, K key, Supplier<BucketConfiguration> configurationSupplier, GridProxy<K> gridProxy, RecoveryStrategy recoveryStrategy, boolean initializeBucket) {
        super(listener);
        this.key = key;
        this.gridProxy = gridProxy;
        this.recoveryStrategy = recoveryStrategy;
        this.configurationSupplier = configurationSupplier;
        if (configurationSupplier == null) {
            throw BucketExceptions.nullConfigurationSupplier();
        }
        if (initializeBucket) {
            BucketConfiguration configuration = this.getConfiguration();
            gridProxy.createInitialState(key, configuration);
        }
    }

    @Override
    public boolean isAsyncModeSupported() {
        return this.gridProxy.isAsyncModeSupported();
    }

    @Override
    protected long consumeAsMuchAsPossibleImpl(long limit) {
        return this.execute(new ConsumeAsMuchAsPossibleCommand(limit));
    }

    @Override
    protected CompletableFuture<Long> tryConsumeAsMuchAsPossibleAsyncImpl(long limit) {
        return this.executeAsync(new ConsumeAsMuchAsPossibleCommand(limit));
    }

    @Override
    protected boolean tryConsumeImpl(long tokensToConsume) {
        return this.execute(new TryConsumeCommand(tokensToConsume));
    }

    @Override
    protected CompletableFuture<Boolean> tryConsumeAsyncImpl(long tokensToConsume) {
        return this.executeAsync(new TryConsumeCommand(tokensToConsume));
    }

    @Override
    protected ConsumptionProbe tryConsumeAndReturnRemainingTokensImpl(long tokensToConsume) {
        return this.execute(new TryConsumeAndReturnRemainingTokensCommand(tokensToConsume));
    }

    @Override
    protected EstimationProbe estimateAbilityToConsumeImpl(long numTokens) {
        return this.execute(new EstimateAbilityToConsumeCommand(numTokens));
    }

    @Override
    protected CompletableFuture<ConsumptionProbe> tryConsumeAndReturnRemainingTokensAsyncImpl(long tokensToConsume) {
        return this.executeAsync(new TryConsumeAndReturnRemainingTokensCommand(tokensToConsume));
    }

    @Override
    protected CompletableFuture<EstimationProbe> estimateAbilityToConsumeAsyncImpl(long tokensToEstimate) {
        return this.executeAsync(new EstimateAbilityToConsumeCommand(tokensToEstimate));
    }

    @Override
    protected long reserveAndCalculateTimeToSleepImpl(long tokensToConsume, long waitIfBusyNanosLimit) {
        ReserveAndCalculateTimeToSleepCommand consumeCommand = new ReserveAndCalculateTimeToSleepCommand(tokensToConsume, waitIfBusyNanosLimit);
        return this.execute(consumeCommand);
    }

    @Override
    protected CompletableFuture<Long> reserveAndCalculateTimeToSleepAsyncImpl(long tokensToConsume, long maxWaitTimeNanos) {
        ReserveAndCalculateTimeToSleepCommand consumeCommand = new ReserveAndCalculateTimeToSleepCommand(tokensToConsume, maxWaitTimeNanos);
        return this.executeAsync(consumeCommand);
    }

    @Override
    protected void addTokensImpl(long tokensToAdd) {
        this.execute(new AddTokensCommand(tokensToAdd));
    }

    @Override
    protected CompletableFuture<Void> addTokensAsyncImpl(long tokensToAdd) {
        CompletableFuture<Nothing> future = this.executeAsync(new AddTokensCommand(tokensToAdd));
        return future.thenApply(nothing -> null);
    }

    @Override
    protected void replaceConfigurationImpl(BucketConfiguration newConfiguration) {
        ReplaceConfigurationOrReturnPreviousCommand replaceConfigCommand = new ReplaceConfigurationOrReturnPreviousCommand(newConfiguration);
        BucketConfiguration previousConfiguration = this.execute(replaceConfigCommand);
        if (previousConfiguration != null) {
            throw new IncompatibleConfigurationException(previousConfiguration, newConfiguration);
        }
    }

    @Override
    protected CompletableFuture<Void> replaceConfigurationAsyncImpl(BucketConfiguration newConfiguration) {
        ReplaceConfigurationOrReturnPreviousCommand replaceConfigCommand = new ReplaceConfigurationOrReturnPreviousCommand(newConfiguration);
        CompletableFuture<BucketConfiguration> result = this.executeAsync(replaceConfigCommand);
        return result.thenCompose(previousConfiguration -> {
            if (previousConfiguration == null) {
                return CompletableFuture.completedFuture(null);
            }
            CompletableFuture future = new CompletableFuture();
            future.completeExceptionally(new IncompatibleConfigurationException((BucketConfiguration)previousConfiguration, newConfiguration));
            return future;
        });
    }

    @Override
    public long getAvailableTokens() {
        return this.execute(new GetAvailableTokensCommand());
    }

    @Override
    public BucketState createSnapshot() {
        return this.execute(new CreateSnapshotCommand());
    }

    private BucketConfiguration getConfiguration() {
        BucketConfiguration bucketConfiguration = this.configurationSupplier.get();
        if (bucketConfiguration == null) {
            throw BucketExceptions.nullConfiguration();
        }
        return bucketConfiguration;
    }

    private <T extends Serializable> T execute(GridCommand<T> command) {
        CommandResult<T> result = this.gridProxy.execute(this.key, command);
        if (!result.isBucketNotFound()) {
            return result.getData();
        }
        if (this.recoveryStrategy == RecoveryStrategy.THROW_BUCKET_NOT_FOUND_EXCEPTION) {
            throw new BucketNotFoundException((Serializable)this.key);
        }
        return this.gridProxy.createInitialStateAndExecute(this.key, this.getConfiguration(), command);
    }

    private <T extends Serializable> CompletableFuture<T> executeAsync(GridCommand<T> command) {
        CompletableFuture<CommandResult<T>> futureResult = this.gridProxy.executeAsync(this.key, command);
        return futureResult.thenCompose(cmdResult -> {
            if (!cmdResult.isBucketNotFound()) {
                Object resultDate = cmdResult.getData();
                return CompletableFuture.completedFuture(resultDate);
            }
            if (this.recoveryStrategy == RecoveryStrategy.THROW_BUCKET_NOT_FOUND_EXCEPTION) {
                CompletableFuture failedFuture = new CompletableFuture();
                failedFuture.completeExceptionally(new BucketNotFoundException((Serializable)this.key));
                return failedFuture;
            }
            return this.gridProxy.createInitialStateAndExecuteAsync(this.key, this.getConfiguration(), command);
        });
    }
}

