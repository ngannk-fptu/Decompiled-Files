/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.user.User;

public class NeverPermittedContentPermission
extends ContentPermission {
    private final String type;

    public NeverPermittedContentPermission(String type) {
        this.type = type;
    }

    @Override
    public boolean isPermitted(User user) {
        return false;
    }

    @Override
    public boolean isGroupPermission() {
        return false;
    }

    @Override
    public boolean isUserPermission() {
        return false;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        NeverPermittedContentPermission that = (NeverPermittedContentPermission)o;
        return this.type != null ? this.type.equals(that.type) : that.type == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.type != null ? this.type.hashCode() : 0);
        return result;
    }
}

