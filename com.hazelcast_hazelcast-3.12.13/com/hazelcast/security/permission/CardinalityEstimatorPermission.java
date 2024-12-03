/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security.permission;

import com.hazelcast.security.permission.InstancePermission;

public class CardinalityEstimatorPermission
extends InstancePermission {
    private static final int READ = 4;
    private static final int MODIFY = 8;
    private static final int ALL = 15;

    public CardinalityEstimatorPermission(String name, String ... actions) {
        super(name, actions);
    }

    @Override
    protected int initMask(String[] actions) {
        int mask = 0;
        for (String action : actions) {
            if ("all".equals(action)) {
                return 15;
            }
            if ("create".equals(action)) {
                mask |= 1;
                continue;
            }
            if ("read".equals(action)) {
                mask |= 4;
                continue;
            }
            if ("modify".equals(action)) {
                mask |= 8;
                continue;
            }
            if (!"destroy".equals(action)) continue;
            mask |= 2;
        }
        return mask;
    }
}

