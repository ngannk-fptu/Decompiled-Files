/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.operation;

import com.hazelcast.instance.DefaultNodeExtension;
import com.hazelcast.internal.management.operation.AbstractManagementOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.impl.NodeEngineImpl;
import java.io.IOException;

public class SetLicenseOperation
extends AbstractManagementOperation {
    private String licenseKey;

    public SetLicenseOperation() {
    }

    public SetLicenseOperation(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    @Override
    public void run() throws Exception {
        DefaultNodeExtension nodeExtension = (DefaultNodeExtension)((NodeEngineImpl)this.getNodeEngine()).getNode().getNodeExtension();
        nodeExtension.setLicenseKey(this.licenseKey);
    }

    @Override
    public int getId() {
        return 7;
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.licenseKey = in.readUTF();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.licenseKey);
    }
}

