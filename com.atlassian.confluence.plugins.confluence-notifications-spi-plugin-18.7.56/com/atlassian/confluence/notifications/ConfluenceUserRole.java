/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.plugin.notifications.spi.UserRole
 */
package com.atlassian.confluence.notifications;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.plugin.notifications.spi.UserRole;

@ExperimentalApi
public class ConfluenceUserRole
implements UserRole {
    private final String id;

    public ConfluenceUserRole(String id) {
        this.id = id;
    }

    public String getID() {
        return this.id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ConfluenceUserRole that = (ConfluenceUserRole)o;
        return this.id.equals(that.id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }
}

