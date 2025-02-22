/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.projection.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.projection.Projection;
import com.hazelcast.projection.impl.ProjectionDataSerializerHook;
import com.hazelcast.query.impl.Extractable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public final class MultiAttributeProjection<I>
extends Projection<I, Object[]>
implements IdentifiedDataSerializable {
    private String[] attributePaths;

    MultiAttributeProjection() {
    }

    public MultiAttributeProjection(String ... attributePath) {
        if (attributePath == null || attributePath.length == 0) {
            throw new IllegalArgumentException("You need to specify at least one attributePath");
        }
        for (String path : attributePath) {
            Preconditions.checkHasText(path, "attributePath must not be null or empty");
            Preconditions.checkFalse(path.contains("[any]"), "attributePath must not contain [any] operators");
        }
        this.attributePaths = attributePath;
    }

    @Override
    public Object[] transform(I input) {
        if (input instanceof Extractable) {
            Extractable extractable = (Extractable)input;
            Object[] result = new Object[this.attributePaths.length];
            for (int i = 0; i < this.attributePaths.length; ++i) {
                result[i] = extractable.getAttributeValue(this.attributePaths[i]);
            }
            return result;
        }
        throw new IllegalArgumentException("The given map entry is not extractable");
    }

    @Override
    public int getFactoryId() {
        return ProjectionDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTFArray(this.attributePaths);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.attributePaths = in.readUTFArray();
    }
}

