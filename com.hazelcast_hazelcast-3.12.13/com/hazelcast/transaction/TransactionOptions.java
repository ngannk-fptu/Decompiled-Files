/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.SerializableByConvention;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@SerializableByConvention(value=SerializableByConvention.Reason.PUBLIC_API)
public final class TransactionOptions
implements DataSerializable {
    public static final long DEFAULT_TIMEOUT_MILLIS = TimeUnit.MINUTES.toMillis(2L);
    private long timeoutMillis;
    private int durability;
    private TransactionType transactionType;

    public TransactionOptions() {
        this.setDurability(1).setTransactionType(TransactionType.TWO_PHASE).setDefaultTimeout();
    }

    public TransactionType getTransactionType() {
        return this.transactionType;
    }

    public TransactionOptions setTransactionType(TransactionType transactionType) {
        if (transactionType == null) {
            throw new IllegalArgumentException("transactionType can't be null");
        }
        this.transactionType = transactionType;
        return this;
    }

    public long getTimeoutMillis() {
        return this.timeoutMillis;
    }

    public TransactionOptions setTimeout(long timeout, TimeUnit timeUnit) {
        if (timeout < 0L) {
            throw new IllegalArgumentException("Timeout can not be negative!");
        }
        if (timeUnit == null) {
            throw new IllegalArgumentException("timeunit can't be null");
        }
        if (timeout == 0L) {
            this.setDefaultTimeout();
        } else {
            this.timeoutMillis = timeUnit.toMillis(timeout);
        }
        return this;
    }

    public int getDurability() {
        return this.durability;
    }

    public TransactionOptions setDurability(int durability) {
        if (durability < 0) {
            throw new IllegalArgumentException("Durability cannot be negative!");
        }
        this.durability = durability;
        return this;
    }

    public static TransactionOptions getDefault() {
        return new TransactionOptions();
    }

    private void setDefaultTimeout() {
        this.timeoutMillis = DEFAULT_TIMEOUT_MILLIS;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeLong(this.timeoutMillis);
        out.writeInt(this.durability);
        out.writeInt(this.transactionType.value);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.timeoutMillis = in.readLong();
        this.durability = in.readInt();
        this.transactionType = TransactionType.getByValue(in.readInt());
    }

    public String toString() {
        return "TransactionOptions{timeoutMillis=" + this.timeoutMillis + ", durability=" + this.durability + ", txType=" + (Object)((Object)this.transactionType) + '}';
    }

    public static enum TransactionType {
        TWO_PHASE(1),
        LOCAL(2),
        ONE_PHASE(2);

        private final int value;

        private TransactionType(int value) {
            this.value = value;
        }

        public int id() {
            return this.value;
        }

        public static TransactionType getByValue(int value) {
            switch (value) {
                case 1: {
                    return TWO_PHASE;
                }
                case 2: {
                    return ONE_PHASE;
                }
            }
            throw new IllegalArgumentException("Unrecognized value:" + value);
        }
    }
}

