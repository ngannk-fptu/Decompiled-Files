/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.usercodedeployment.impl.operation;

import com.hazelcast.internal.usercodedeployment.UserCodeDeploymentService;
import com.hazelcast.internal.usercodedeployment.impl.UserCodeDeploymentSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DeployClassesOperation
extends Operation
implements IdentifiedDataSerializable {
    private List<Map.Entry<String, byte[]>> classDefinitions;

    public DeployClassesOperation(List<Map.Entry<String, byte[]>> classDefinitions) {
        this.classDefinitions = classDefinitions;
    }

    public DeployClassesOperation() {
    }

    @Override
    public void run() throws Exception {
        UserCodeDeploymentService service = (UserCodeDeploymentService)this.getService();
        service.defineClasses(this.classDefinitions);
    }

    @Override
    public String getServiceName() {
        return "user-code-deployment-service";
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeInt(this.classDefinitions.size());
        for (Map.Entry<String, byte[]> classDefinition : this.classDefinitions) {
            out.writeUTF(classDefinition.getKey());
            out.writeByteArray(classDefinition.getValue());
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        int length = in.readInt();
        this.classDefinitions = new ArrayList<Map.Entry<String, byte[]>>(length);
        for (int i = 0; i < length; ++i) {
            String className = in.readUTF();
            byte[] classDefinition = in.readByteArray();
            this.classDefinitions.add(new AbstractMap.SimpleEntry<String, byte[]>(className, classDefinition));
        }
    }

    @Override
    public int getFactoryId() {
        return UserCodeDeploymentSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 2;
    }
}

