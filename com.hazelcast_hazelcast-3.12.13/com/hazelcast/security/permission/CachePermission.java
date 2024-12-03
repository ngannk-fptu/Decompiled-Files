/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security.permission;

import com.hazelcast.security.permission.InstancePermission;

public class CachePermission
extends InstancePermission {
    private static final int PUT = 4;
    private static final int REMOVE = 8;
    private static final int READ = 16;
    private static final int LISTEN = 32;
    private static final int ALL = 63;

    public CachePermission(String name, String ... actions) {
        super(name, actions);
    }

    @Override
    protected int initMask(String[] actions) {
        int mask = 0;
        for (String action : actions) {
            if ("all".equals(action)) {
                return 63;
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
            if ("remove".equals(action)) {
                mask |= 8;
                continue;
            }
            if ("read".equals(action)) {
                mask |= 0x10;
                continue;
            }
            if (!"listen".equals(action)) continue;
            mask |= 0x20;
        }
        return mask;
    }
}

