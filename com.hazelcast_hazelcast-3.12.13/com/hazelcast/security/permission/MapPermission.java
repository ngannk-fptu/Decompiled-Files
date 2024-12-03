/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security.permission;

import com.hazelcast.security.permission.InstancePermission;

public class MapPermission
extends InstancePermission {
    private static final int PUT = 4;
    private static final int REMOVE = 8;
    private static final int READ = 16;
    private static final int LISTEN = 32;
    private static final int LOCK = 64;
    private static final int INDEX = 128;
    private static final int INTERCEPT = 256;
    private static final int ALL = 511;

    public MapPermission(String name, String ... actions) {
        super(name, actions);
    }

    @Override
    protected int initMask(String[] actions) {
        int mask = 0;
        for (String action : actions) {
            if ("all".equals(action)) {
                return 511;
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
            if ("listen".equals(action)) {
                mask |= 0x20;
                continue;
            }
            if ("lock".equals(action)) {
                mask |= 0x40;
                continue;
            }
            if ("index".equals(action)) {
                mask |= 0x80;
                continue;
            }
            if (!"intercept".equals(action)) continue;
            mask |= 0x100;
        }
        return mask;
    }
}

