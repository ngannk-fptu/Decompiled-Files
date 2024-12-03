/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security.permission;

import com.hazelcast.config.matcher.WildcardConfigPatternMatcher;
import com.hazelcast.security.permission.ClusterPermission;
import java.security.Permission;

public abstract class InstancePermission
extends ClusterPermission {
    protected static final int NONE = 0;
    protected static final int CREATE = 1;
    protected static final int DESTROY = 2;
    private static final WildcardConfigPatternMatcher CONFIG_PATTERN_MATCHER = new WildcardConfigPatternMatcher();
    protected final int mask;
    protected final String actions;

    public InstancePermission(String name, String ... actions) {
        super(name);
        if (name == null || "".equals(name)) {
            throw new IllegalArgumentException("Permission name is mandatory!");
        }
        this.mask = this.initMask(actions);
        StringBuilder s = new StringBuilder();
        for (String action : actions) {
            s.append(action).append(" ");
        }
        if (s.length() > 0) {
            s.setLength(s.length() - 1);
        }
        this.actions = s.toString();
    }

    protected abstract int initMask(String[] var1);

    @Override
    public boolean implies(Permission permission) {
        boolean maskTest;
        if (this.getClass() != permission.getClass()) {
            return false;
        }
        InstancePermission that = (InstancePermission)permission;
        boolean bl = maskTest = (this.mask & that.mask) == that.mask;
        if (!maskTest) {
            return false;
        }
        return CONFIG_PATTERN_MATCHER.matches(this.getName(), that.getName());
    }

    @Override
    public String getActions() {
        return this.actions;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.mask;
        result = 31 * result + this.actions.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        InstancePermission other = (InstancePermission)obj;
        if (this.getName() == null && other.getName() != null) {
            return false;
        }
        if (!this.getName().equals(other.getName())) {
            return false;
        }
        return this.mask == other.mask;
    }
}

