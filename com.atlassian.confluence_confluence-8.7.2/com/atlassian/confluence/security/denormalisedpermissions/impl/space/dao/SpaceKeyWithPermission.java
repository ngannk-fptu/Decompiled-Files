/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.space.dao;

import com.atlassian.annotations.Internal;

@Internal
public class SpaceKeyWithPermission {
    private final String spaceKey;
    private final boolean hasPermission;

    public SpaceKeyWithPermission(String spaceKey, boolean hasPermission) {
        this.spaceKey = spaceKey;
        this.hasPermission = hasPermission;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public boolean isHasPermission() {
        return this.hasPermission;
    }
}

