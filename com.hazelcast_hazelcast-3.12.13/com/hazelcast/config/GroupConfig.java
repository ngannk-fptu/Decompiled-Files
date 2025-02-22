/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.util.Preconditions;

public final class GroupConfig {
    public static final String DEFAULT_GROUP_PASSWORD = "dev-pass";
    public static final String DEFAULT_GROUP_NAME = "dev";
    private String name = "dev";
    private String password = "dev-pass";

    public GroupConfig() {
    }

    public GroupConfig(String name) {
        this.setName(name);
    }

    public GroupConfig(String name, String password) {
        this.setName(name);
        this.setPassword(password);
    }

    public GroupConfig(GroupConfig groupConfig) {
        this.name = groupConfig.name;
        this.password = groupConfig.password;
    }

    public String getName() {
        return this.name;
    }

    public GroupConfig setName(String name) {
        this.name = Preconditions.isNotNull(name, "name");
        return this;
    }

    @Deprecated
    public String getPassword() {
        return this.password;
    }

    @Deprecated
    public GroupConfig setPassword(String password) {
        this.password = Preconditions.isNotNull(password, "group password");
        return this;
    }

    public int hashCode() {
        return (this.name != null ? this.name.hashCode() : 0) + 31 * (this.password != null ? this.password.hashCode() : 0);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GroupConfig)) {
            return false;
        }
        GroupConfig other = (GroupConfig)obj;
        return (this.name == null ? other.name == null : this.name.equals(other.name)) && (this.password == null ? other.password == null : this.password.equals(other.password));
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GroupConfig [name=").append(this.name).append(", password=");
        int len = this.password.length();
        for (int i = 0; i < len; ++i) {
            builder.append('*');
        }
        builder.append("]");
        return builder.toString();
    }
}

