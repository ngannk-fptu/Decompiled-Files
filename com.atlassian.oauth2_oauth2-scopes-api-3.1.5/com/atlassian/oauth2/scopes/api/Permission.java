/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.scopes.api;

import java.util.Objects;

public class Permission {
    private final String key;
    private final Object target;
    private final Object targetExcluded;

    public static Permission permission(String permission) {
        return new Permission(permission, null, null);
    }

    public static Permission permissionWithTarget(String permission, Object target) {
        return new Permission(permission, target, null);
    }

    public static Permission permissionWithTargetExcluded(String permission, Object notTarget) {
        return new Permission(permission, null, notTarget);
    }

    private Permission(String key, Object target, Object targetExcluded) {
        this.key = key;
        this.target = target;
        this.targetExcluded = targetExcluded;
    }

    public boolean hasPermission(String key) {
        return key.equals(this.key);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Permission that = (Permission)o;
        boolean isSameKey = Objects.equals(this.key, that.key);
        boolean isNullTarget = Objects.isNull(this.target) || Objects.isNull(that.target);
        boolean isNullNotTarget = Objects.isNull(this.targetExcluded) && Objects.isNull(that.targetExcluded);
        return !(!isSameKey || !isNullTarget && !Objects.equals(this.target, that.target) || !isNullNotTarget && Objects.equals(this.targetExcluded, that.target));
    }

    public int hashCode() {
        return Objects.hash(this.key, this.target);
    }

    public String toString() {
        if (this.target != null) {
            return this.key + ":" + this.target;
        }
        if (this.targetExcluded != null) {
            return this.key + ":!" + this.targetExcluded;
        }
        return this.key;
    }
}

