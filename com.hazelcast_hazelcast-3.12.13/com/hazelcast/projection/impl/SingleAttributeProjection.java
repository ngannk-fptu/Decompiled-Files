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

public final class SingleAttributeProjection<I, O>
extends Projection<I, O>
implements IdentifiedDataSerializable {
    private String attributePath;

    SingleAttributeProjection() {
    }

    public SingleAttributeProjection(String attributePath) {
        Preconditions.checkHasText(attributePath, "attributePath must not be null or empty");
        Preconditions.checkFalse(attributePath.contains("[any]"), "attributePath must not contain [any] operators");
        this.attributePath = attributePath;
    }

    @Override
    public O transform(I input) {
        if (input instanceof Extractable) {
            return (O)((Extractable)input).getAttributeValue(this.attributePath);
        }
        throw new IllegalArgumentException("The given map entry is not extractable");
    }

    @Override
    public int getFactoryId() {
        return ProjectionDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.attributePath);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.attributePath = in.readUTF();
    }
}

