/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security.permission;

import com.hazelcast.security.permission.ClusterPermission;
import java.security.Permission;

public class TransactionPermission
extends ClusterPermission {
    public TransactionPermission() {
        super("<transaction>");
    }

    @Override
    public boolean implies(Permission permission) {
        return this.getClass() == permission.getClass();
    }

    @Override
    public String getActions() {
        return "transaction";
    }
}

