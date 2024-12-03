/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.usercodedeployment.impl.operation;

import com.hazelcast.internal.usercodedeployment.UserCodeDeploymentService;
import com.hazelcast.internal.usercodedeployment.impl.ClassData;
import com.hazelcast.internal.usercodedeployment.impl.UserCodeDeploymentSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.UrgentSystemOperation;
import java.io.IOException;

public final class ClassDataFinderOperation
extends Operation
implements UrgentSystemOperation,
IdentifiedDataSerializable {
    private String className;
    private ClassData response;

    public ClassDataFinderOperation(String className) {
        this.className = className;
    }

    public ClassDataFinderOperation() {
    }

    @Override
    public ClassData getResponse() {
        return this.response;
    }

    @Override
    public void run() throws Exception {
        UserCodeDeploymentService service = (UserCodeDeploymentService)this.getService();
        this.response = service.getClassDataOrNull(this.className);
    }

    @Override
    public String getServiceName() {
        return "user-code-deployment-service";
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.className);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.className = in.readUTF();
    }

    @Override
    public int getFactoryId() {
        return UserCodeDeploymentSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }
}

