/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.model.permission;

import com.atlassian.confluence.plugins.gatekeeper.model.owner.OwnerType;

public class Source {
    private OwnerType type;
    private boolean isGlobalAnonymousAccessEnabled;
    private boolean canLogin;
    private String name;

    public Source(OwnerType type, boolean isGlobalAnonymousAccessEnabled, boolean canLogin) {
        this.type = OwnerType.TYPE_ANONYMOUS;
        this.isGlobalAnonymousAccessEnabled = isGlobalAnonymousAccessEnabled;
        this.canLogin = canLogin;
        this.name = "<anonymous>";
    }

    public Source(OwnerType type, String name) {
        this.type = type;
        this.name = name;
    }

    public boolean isGlobalAnonymousAccessEnabled() {
        return this.isGlobalAnonymousAccessEnabled;
    }

    public boolean canLogin() {
        return this.canLogin;
    }

    public String getName() {
        return this.name;
    }

    public boolean isAnonymous() {
        return this.type == OwnerType.TYPE_ANONYMOUS;
    }

    public boolean isUser() {
        return this.type == OwnerType.TYPE_USER;
    }

    public boolean isGroup() {
        return this.type == OwnerType.TYPE_GROUP;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Source that = (Source)o;
        if (this.type != that.type) {
            return false;
        }
        if (this.name == null && that.name == null) {
            return true;
        }
        if (this.name == null || that.name == null) {
            return false;
        }
        return this.name.equals(that.name);
    }

    public int hashCode() {
        int result = this.type.hashCode();
        result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "Source{type=" + this.type + ", name=" + this.name + "}";
    }
}

