/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.operation;

import com.hazelcast.config.PermissionConfig;
import com.hazelcast.instance.Node;
import com.hazelcast.internal.management.dto.PermissionConfigDTO;
import com.hazelcast.internal.management.operation.AbstractManagementOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.spi.impl.NodeEngineImpl;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UpdatePermissionConfigOperation
extends AbstractManagementOperation {
    private Set<PermissionConfig> permissionConfigs;

    public UpdatePermissionConfigOperation() {
    }

    public UpdatePermissionConfigOperation(Set<PermissionConfig> permissionConfigs) {
        this.permissionConfigs = permissionConfigs;
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    public void run() throws Exception {
        Node node = ((NodeEngineImpl)this.getNodeEngine()).getNode();
        node.securityContext.refreshPermissions(this.permissionConfigs);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.permissionConfigs.size());
        for (PermissionConfig permissionConfig : this.permissionConfigs) {
            new PermissionConfigDTO(permissionConfig).writeData(out);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int configSize = in.readInt();
        this.permissionConfigs = new HashSet<PermissionConfig>(configSize);
        for (int i = 0; i < configSize; ++i) {
            PermissionConfigDTO permissionConfigDTO = new PermissionConfigDTO();
            permissionConfigDTO.readData(in);
            this.permissionConfigs.add(permissionConfigDTO.getPermissionConfig());
        }
    }
}

