/*
 * Decompiled with CFR 0.152.
 */
package io.github.bucket4j.local;

import io.github.bucket4j.AbstractBucketBuilder;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.BucketExceptions;
import io.github.bucket4j.TimeMeter;
import io.github.bucket4j.local.FakeLock;
import io.github.bucket4j.local.LocalBucket;
import io.github.bucket4j.local.LockFreeBucket;
import io.github.bucket4j.local.SynchronizationStrategy;
import io.github.bucket4j.local.SynchronizedBucket;

public class LocalBucketBuilder
extends AbstractBucketBuilder<LocalBucketBuilder> {
    private TimeMeter timeMeter = TimeMeter.SYSTEM_MILLISECONDS;
    private SynchronizationStrategy synchronizationStrategy = SynchronizationStrategy.LOCK_FREE;

    public LocalBucketBuilder withNanosecondPrecision() {
        this.timeMeter = TimeMeter.SYSTEM_NANOTIME;
        return this;
    }

    public LocalBucketBuilder withMillisecondPrecision() {
        this.timeMeter = TimeMeter.SYSTEM_MILLISECONDS;
        return this;
    }

    public LocalBucketBuilder withCustomTimePrecision(TimeMeter customTimeMeter) {
        if (customTimeMeter == null) {
            throw BucketExceptions.nullTimeMeter();
        }
        this.timeMeter = customTimeMeter;
        return this;
    }

    public LocalBucketBuilder withSynchronizationStrategy(SynchronizationStrategy synchronizationStrategy) {
        if (synchronizationStrategy == null) {
            throw BucketExceptions.nullSynchronizationStrategy();
        }
        this.synchronizationStrategy = synchronizationStrategy;
        return this;
    }

    public LocalBucket build() {
        BucketConfiguration configuration = this.buildConfiguration();
        switch (this.synchronizationStrategy) {
            case LOCK_FREE: {
                return new LockFreeBucket(configuration, this.timeMeter);
            }
            case SYNCHRONIZED: {
                return new SynchronizedBucket(configuration, this.timeMeter);
            }
            case NONE: {
                return new SynchronizedBucket(configuration, this.timeMeter, FakeLock.INSTANCE);
            }
        }
        throw new IllegalStateException();
    }
}

