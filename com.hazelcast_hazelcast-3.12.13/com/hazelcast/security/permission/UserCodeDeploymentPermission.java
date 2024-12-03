/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security.permission;

import com.hazelcast.security.permission.InstancePermission;

public class UserCodeDeploymentPermission
extends InstancePermission {
    private static final int DEPLOY = 4;
    private static final int ALL = 4;

    public UserCodeDeploymentPermission(String ... actions) {
        super("user-code-deployment-service", actions);
    }

    @Override
    protected int initMask(String[] actions) {
        int mask = 0;
        for (String action : actions) {
            if ("all".equals(action)) {
                return 4;
            }
            if (!"deploy".equals(action)) continue;
            mask |= 4;
        }
        return mask;
    }
}

