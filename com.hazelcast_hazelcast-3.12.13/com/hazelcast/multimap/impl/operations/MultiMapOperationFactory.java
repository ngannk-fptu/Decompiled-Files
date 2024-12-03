/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.operations;

import com.hazelcast.multimap.impl.MultiMapDataSerializerHook;
import com.hazelcast.multimap.impl.operations.ClearOperation;
import com.hazelcast.multimap.impl.operations.ContainsEntryOperation;
import com.hazelcast.multimap.impl.operations.EntrySetOperation;
import com.hazelcast.multimap.impl.operations.KeySetOperation;
import com.hazelcast.multimap.impl.operations.SizeOperation;
import com.hazelcast.multimap.impl.operations.ValuesOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import java.io.IOException;

public class MultiMapOperationFactory
implements OperationFactory {
    private String name;
    private OperationFactoryType operationFactoryType;
    private Data key;
    private Data value;
    private long threadId;

    public MultiMapOperationFactory() {
    }

    public MultiMapOperationFactory(String name, OperationFactoryType operationFactoryType) {
        this.name = name;
        this.operationFactoryType = operationFactoryType;
    }

    public MultiMapOperationFactory(String name, OperationFactoryType operationFactoryType, Data key, Data value) {
        this(name, operationFactoryType);
        this.key = key;
        this.value = value;
    }

    public MultiMapOperationFactory(String name, OperationFactoryType operationFactoryType, Data key, Data value, long threadId) {
        this(name, operationFactoryType);
        this.key = key;
        this.value = value;
        this.threadId = threadId;
    }

    @Override
    public Operation createOperation() {
        switch (this.operationFactoryType) {
            case KEY_SET: {
                return new KeySetOperation(this.name);
            }
            case VALUES: {
                return new ValuesOperation(this.name);
            }
            case ENTRY_SET: {
                return new EntrySetOperation(this.name);
            }
            case CONTAINS: {
                return new ContainsEntryOperation(this.name, this.key, this.value, this.threadId);
            }
            case SIZE: {
                return new SizeOperation(this.name);
            }
            case CLEAR: {
                return new ClearOperation(this.name);
            }
        }
        return null;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.operationFactoryType.type);
        out.writeLong(this.threadId);
        out.writeData(this.key);
        out.writeData(this.value);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.operationFactoryType = OperationFactoryType.getByType(in.readInt());
        this.threadId = in.readLong();
        this.key = in.readData();
        this.value = in.readData();
    }

    @Override
    public int getFactoryId() {
        return MultiMapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 41;
    }

    public static enum OperationFactoryType {
        KEY_SET(1),
        VALUES(2),
        ENTRY_SET(3),
        CONTAINS(4),
        SIZE(5),
        CLEAR(6);

        final int type;

        private OperationFactoryType(int type) {
            this.type = type;
        }

        static OperationFactoryType getByType(int type) {
            for (OperationFactoryType factoryType : OperationFactoryType.values()) {
                if (factoryType.type != type) continue;
                return factoryType;
            }
            return null;
        }
    }
}

