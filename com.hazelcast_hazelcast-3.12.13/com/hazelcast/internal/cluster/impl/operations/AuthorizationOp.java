/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.config.GroupConfig;
import com.hazelcast.internal.cluster.impl.operations.AbstractJoinOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class AuthorizationOp
extends AbstractJoinOperation {
    private String groupName;
    private String groupPassword;
    private Boolean response = Boolean.TRUE;

    public AuthorizationOp() {
    }

    public AuthorizationOp(String groupName, String groupPassword) {
        this.groupName = groupName;
        this.groupPassword = groupPassword;
    }

    @Override
    public void run() {
        GroupConfig groupConfig = this.getNodeEngine().getConfig().getGroupConfig();
        if (!this.groupName.equals(groupConfig.getName())) {
            this.response = Boolean.FALSE;
        } else if (!this.groupPassword.equals(groupConfig.getPassword())) {
            this.response = Boolean.FALSE;
        }
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.groupName = in.readUTF();
        this.groupPassword = in.readUTF();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.groupName);
        out.writeUTF(this.groupPassword);
    }

    @Override
    public int getId() {
        return 8;
    }
}

