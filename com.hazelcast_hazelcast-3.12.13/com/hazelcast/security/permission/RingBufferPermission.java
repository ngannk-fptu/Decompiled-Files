/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security.permission;

import com.hazelcast.security.permission.InstancePermission;

public class RingBufferPermission
extends InstancePermission {
    private static final int PUT = 4;
    private static final int READ = 8;
    private static final int ALL = 12;

    public RingBufferPermission(String name, String ... actions) {
        super(name, actions);
    }

    @Override
    protected int initMask(String[] actions) {
        int mask = 0;
        for (String action : actions) {
            if ("all".equals(action)) {
                return 12;
            }
            if ("create".equals(action)) {
                mask |= 1;
                continue;
            }
            if ("destroy".equals(action)) {
                mask |= 2;
                continue;
            }
            if ("put".equals(action)) {
                mask |= 4;
                continue;
            }
            if (!"read".equals(action)) continue;
            mask |= 8;
        }
        return mask;
    }
}

