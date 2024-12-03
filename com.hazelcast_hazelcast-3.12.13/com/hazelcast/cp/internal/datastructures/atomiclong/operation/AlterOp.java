/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomiclong.operation;

import com.hazelcast.core.IFunction;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.datastructures.atomiclong.RaftAtomicLong;
import com.hazelcast.cp.internal.datastructures.atomiclong.operation.AbstractAtomicLongOp;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public class AlterOp
extends AbstractAtomicLongOp {
    private IFunction<Long, Long> function;
    private AlterResultType alterResultType;

    public AlterOp() {
    }

    public AlterOp(String name, IFunction<Long, Long> function, AlterResultType alterResultType) {
        super(name);
        Preconditions.checkNotNull(alterResultType);
        this.function = function;
        this.alterResultType = alterResultType;
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        RaftAtomicLong atomic = this.getAtomicLong(groupId);
        long before = atomic.getAndAdd(0L);
        long after = this.function.apply(before);
        atomic.getAndSet(after);
        return this.alterResultType == AlterResultType.OLD_VALUE ? before : after;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeObject(this.function);
        out.writeUTF(this.alterResultType.name());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.function = (IFunction)in.readObject();
        this.alterResultType = AlterResultType.valueOf(in.readUTF());
    }

    public static enum AlterResultType {
        OLD_VALUE(0),
        NEW_VALUE(1);

        private final int value;

        private AlterResultType(int value) {
            this.value = value;
        }

        public static AlterResultType fromValue(int value) {
            switch (value) {
                case 0: {
                    return OLD_VALUE;
                }
                case 1: {
                    return NEW_VALUE;
                }
            }
            throw new IllegalArgumentException("No " + AlterResultType.class + " for value: " + value);
        }

        public int value() {
            return this.value;
        }
    }
}

