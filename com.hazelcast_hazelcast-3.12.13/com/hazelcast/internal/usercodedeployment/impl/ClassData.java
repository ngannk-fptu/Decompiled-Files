/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.usercodedeployment.impl;

import com.hazelcast.internal.usercodedeployment.impl.UserCodeDeploymentSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClassData
implements IdentifiedDataSerializable,
Versioned {
    private Map<String, byte[]> innerClassDefinitions = Collections.emptyMap();
    private byte[] mainClassDefinition;

    Map<String, byte[]> getInnerClassDefinitions() {
        return this.innerClassDefinitions;
    }

    public void setInnerClassDefinitions(Map<String, byte[]> innerClassDefinitions) {
        this.innerClassDefinitions = innerClassDefinitions;
    }

    void setMainClassDefinition(byte[] mainClassDefinition) {
        this.mainClassDefinition = mainClassDefinition;
    }

    byte[] getMainClassDefinition() {
        return this.mainClassDefinition;
    }

    @Override
    public int getFactoryId() {
        return UserCodeDeploymentSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeByteArray(this.mainClassDefinition);
        out.writeInt(this.innerClassDefinitions.size());
        for (Map.Entry<String, byte[]> entry : this.innerClassDefinitions.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeByteArray(entry.getValue());
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.mainClassDefinition = in.readByteArray();
        int size = in.readInt();
        this.innerClassDefinitions = new HashMap<String, byte[]>();
        for (int i = 0; i < size; ++i) {
            this.innerClassDefinitions.put(in.readUTF(), in.readByteArray());
        }
    }
}

