/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ClassFilter;

public class JavaSerializationFilterConfig {
    private volatile ClassFilter blacklist;
    private volatile ClassFilter whitelist;
    private volatile boolean defaultsDisabled;

    public JavaSerializationFilterConfig() {
    }

    public JavaSerializationFilterConfig(JavaSerializationFilterConfig javaSerializationFilterConfig) {
        this.blacklist = new ClassFilter(javaSerializationFilterConfig.blacklist);
        this.whitelist = new ClassFilter(javaSerializationFilterConfig.whitelist);
        this.defaultsDisabled = javaSerializationFilterConfig.defaultsDisabled;
    }

    public ClassFilter getBlacklist() {
        if (this.blacklist == null) {
            this.blacklist = new ClassFilter();
        }
        return this.blacklist;
    }

    public JavaSerializationFilterConfig setBlacklist(ClassFilter blackList) {
        this.blacklist = blackList;
        return this;
    }

    public ClassFilter getWhitelist() {
        if (this.whitelist == null) {
            this.whitelist = new ClassFilter();
        }
        return this.whitelist;
    }

    public JavaSerializationFilterConfig setWhitelist(ClassFilter whiteList) {
        this.whitelist = whiteList;
        return this;
    }

    public boolean isDefaultsDisabled() {
        return this.defaultsDisabled;
    }

    public JavaSerializationFilterConfig setDefaultsDisabled(boolean defaultsDisabled) {
        this.defaultsDisabled = defaultsDisabled;
        return this;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.blacklist == null ? 0 : this.blacklist.hashCode());
        result = 31 * result + (this.whitelist == null ? 0 : this.whitelist.hashCode());
        result = 31 * result + (this.defaultsDisabled ? 0 : 1);
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        JavaSerializationFilterConfig other = (JavaSerializationFilterConfig)obj;
        return (this.blacklist == null && other.blacklist == null || this.blacklist != null && this.blacklist.equals(other.blacklist)) && (this.whitelist == null && other.whitelist == null || this.whitelist != null && this.whitelist.equals(other.whitelist)) && this.defaultsDisabled == other.defaultsDisabled;
    }

    public String toString() {
        return "JavaSerializationFilterConfig{defaultsDisabled=" + this.defaultsDisabled + ", blacklist=" + this.blacklist + ", whitelist=" + this.whitelist + "}";
    }
}

