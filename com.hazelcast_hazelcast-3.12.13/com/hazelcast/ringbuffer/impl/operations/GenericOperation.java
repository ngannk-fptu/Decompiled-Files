/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.ringbuffer.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.ringbuffer.impl.RingbufferContainer;
import com.hazelcast.ringbuffer.impl.operations.AbstractRingBufferOperation;
import com.hazelcast.spi.ReadonlyOperation;
import java.io.IOException;

public class GenericOperation
extends AbstractRingBufferOperation
implements ReadonlyOperation {
    public static final byte OPERATION_SIZE = 0;
    public static final byte OPERATION_TAIL = 1;
    public static final byte OPERATION_HEAD = 2;
    public static final byte OPERATION_REMAINING_CAPACITY = 3;
    public static final byte OPERATION_CAPACITY = 4;
    byte operation;
    private transient long result;

    public GenericOperation() {
    }

    public GenericOperation(String name, byte operation) {
        super(name);
        this.operation = operation;
    }

    @Override
    public void run() throws Exception {
        RingbufferContainer ringbuffer = this.getRingBufferContainer();
        switch (this.operation) {
            case 0: {
                this.result = ringbuffer.size();
                break;
            }
            case 2: {
                this.result = ringbuffer.headSequence();
                break;
            }
            case 1: {
                this.result = ringbuffer.tailSequence();
                break;
            }
            case 3: {
                this.result = ringbuffer.remainingCapacity();
                break;
            }
            case 4: {
                this.result = ringbuffer.getCapacity();
                break;
            }
            default: {
                throw new IllegalStateException("Unrecognized operation:" + this.operation);
            }
        }
    }

    @Override
    public Long getResponse() {
        return this.result;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeByte(this.operation);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.operation = in.readByte();
    }
}

