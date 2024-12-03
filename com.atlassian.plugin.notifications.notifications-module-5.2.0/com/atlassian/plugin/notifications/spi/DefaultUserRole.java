/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.spi;

import com.atlassian.plugin.notifications.spi.UserRole;

public class DefaultUserRole
implements UserRole {
    private final String id;

    public DefaultUserRole(String id) {
        this.id = id;
    }

    @Override
    public String getID() {
        return this.id;
    }

    public String toString() {
        return this.id;
    }
}

