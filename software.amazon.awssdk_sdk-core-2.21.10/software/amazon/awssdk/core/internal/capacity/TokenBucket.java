/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.core.internal.capacity;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.retry.conditions.TokenBucketRetryCondition;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public class TokenBucket {
    private final int maxCapacity;
    private final AtomicInteger capacity;

    public TokenBucket(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.capacity = new AtomicInteger(maxCapacity);
    }

    public Optional<TokenBucketRetryCondition.Capacity> tryAcquire(int amountToAcquire) {
        int newCapacity;
        int currentCapacity;
        Validate.isTrue((amountToAcquire >= 0 ? 1 : 0) != 0, (String)"Amount must not be negative.", (Object[])new Object[0]);
        if (amountToAcquire == 0) {
            return Optional.of(TokenBucketRetryCondition.Capacity.builder().capacityAcquired(0).capacityRemaining(this.capacity.get()).build());
        }
        do {
            if ((newCapacity = (currentCapacity = this.capacity.get()) - amountToAcquire) >= 0) continue;
            return Optional.empty();
        } while (!this.capacity.compareAndSet(currentCapacity, newCapacity));
        return Optional.of(TokenBucketRetryCondition.Capacity.builder().capacityAcquired(amountToAcquire).capacityRemaining(newCapacity).build());
    }

    public void release(int amountToRelease) {
        int newCapacity;
        int currentCapacity;
        Validate.isTrue((amountToRelease >= 0 ? 1 : 0) != 0, (String)"Amount must not be negative.", (Object[])new Object[0]);
        if (amountToRelease == 0) {
            return;
        }
        do {
            if ((currentCapacity = this.capacity.get()) != this.maxCapacity) continue;
            return;
        } while (!this.capacity.compareAndSet(currentCapacity, newCapacity = Math.min(currentCapacity + amountToRelease, this.maxCapacity)));
    }

    public int currentCapacity() {
        return this.capacity.get();
    }

    public int maxCapacity() {
        return this.maxCapacity;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TokenBucket that = (TokenBucket)o;
        if (this.maxCapacity != that.maxCapacity) {
            return false;
        }
        return this.capacity.get() == that.capacity.get();
    }

    public int hashCode() {
        int result = this.maxCapacity;
        result = 31 * result + this.capacity.get();
        return result;
    }
}

