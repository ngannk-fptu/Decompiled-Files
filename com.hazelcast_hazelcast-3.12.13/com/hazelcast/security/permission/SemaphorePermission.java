/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security.permission;

import com.hazelcast.security.permission.InstancePermission;

public class SemaphorePermission
extends InstancePermission {
    private static final int ACQUIRE = 4;
    private static final int RELEASE = 8;
    private static final int READ = 16;
    private static final int ALL = 31;

    public SemaphorePermission(String name, String ... actions) {
        super(name, actions);
    }

    @Override
    protected int initMask(String[] actions) {
        int mask = 0;
        for (String action : actions) {
            if ("all".equals(action)) {
                return 31;
            }
            if ("create".equals(action)) {
                mask |= 1;
                continue;
            }
            if ("acquire".equals(action)) {
                mask |= 4;
                continue;
            }
            if ("release".equals(action)) {
                mask |= 8;
                continue;
            }
            if ("destroy".equals(action)) {
                mask |= 2;
                continue;
            }
            if (!"read".equals(action)) continue;
            mask |= 0x10;
        }
        return mask;
    }
}

