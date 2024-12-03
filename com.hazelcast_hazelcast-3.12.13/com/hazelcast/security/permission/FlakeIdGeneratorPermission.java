/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security.permission;

import com.hazelcast.security.permission.InstancePermission;

public class FlakeIdGeneratorPermission
extends InstancePermission {
    private static final int MODIFY = 4;
    private static final int ALL = 7;

    public FlakeIdGeneratorPermission(String name, String ... actions) {
        super(name, actions);
    }

    @Override
    protected int initMask(String[] actions) {
        int mask = 0;
        for (String action : actions) {
            if ("all".equals(action)) {
                return 7;
            }
            if ("create".equals(action)) {
                mask |= 1;
                continue;
            }
            if ("modify".equals(action)) {
                mask |= 4;
                continue;
            }
            if (!"destroy".equals(action)) continue;
            mask |= 2;
        }
        return mask;
    }
}

