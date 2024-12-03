/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.operation;

import com.hazelcast.internal.management.ManagementCenterService;
import com.hazelcast.internal.management.operation.AbstractManagementOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.impl.NodeEngineImpl;
import java.io.IOException;

public class UpdateManagementCenterUrlOperation
extends AbstractManagementOperation {
    private static final int REDO_COUNT = 10;
    private static final int SLEEP_MILLIS = 1000;
    private String newUrl;

    public UpdateManagementCenterUrlOperation() {
    }

    public UpdateManagementCenterUrlOperation(String newUrl) {
        this.newUrl = newUrl;
    }

    @Override
    public void run() throws Exception {
        ManagementCenterService service = ((NodeEngineImpl)this.getNodeEngine()).getManagementCenterService();
        for (int count = 0; service == null && count < 10; ++count) {
            Thread.sleep(1000L);
            service = ((NodeEngineImpl)this.getNodeEngine()).getManagementCenterService();
        }
        if (service != null) {
            service.updateManagementCenterUrl(this.newUrl);
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.newUrl);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.newUrl = in.readUTF();
    }

    @Override
    public int getId() {
        return 1;
    }
}

