/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security.jsm;

import java.security.BasicPermission;

public class HazelcastRuntimePermission
extends BasicPermission {
    private static final long serialVersionUID = -8927678876656102420L;

    public HazelcastRuntimePermission(String name) {
        super(name);
    }

    public HazelcastRuntimePermission(String name, String actions) {
        super(name, actions);
    }
}

