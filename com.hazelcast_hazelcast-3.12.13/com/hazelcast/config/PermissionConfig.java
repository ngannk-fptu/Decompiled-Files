/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionConfig {
    private PermissionType type;
    private String name;
    private String principal;
    private Set<String> endpoints = Collections.newSetFromMap(new ConcurrentHashMap());
    private Set<String> actions = Collections.newSetFromMap(new ConcurrentHashMap());

    public PermissionConfig() {
    }

    public PermissionConfig(PermissionType type, String name, String principal) {
        this.type = type;
        this.name = name;
        this.principal = principal;
    }

    public PermissionConfig(PermissionConfig permissionConfig) {
        this.type = permissionConfig.type;
        this.name = permissionConfig.getName();
        this.principal = permissionConfig.getPrincipal();
        for (String endpoint : permissionConfig.getEndpoints()) {
            this.endpoints.add(endpoint);
        }
        for (String action : permissionConfig.getActions()) {
            this.actions.add(action);
        }
    }

    public PermissionConfig addEndpoint(String endpoint) {
        this.endpoints.add(endpoint);
        return this;
    }

    public PermissionConfig addAction(String action) {
        this.actions.add(action);
        return this;
    }

    public PermissionType getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public String getPrincipal() {
        return this.principal;
    }

    public Set<String> getEndpoints() {
        return this.endpoints;
    }

    public Set<String> getActions() {
        return this.actions;
    }

    public PermissionConfig setType(PermissionType type) {
        this.type = type;
        return this;
    }

    public PermissionConfig setName(String name) {
        this.name = name;
        return this;
    }

    public PermissionConfig setPrincipal(String principal) {
        this.principal = principal;
        return this;
    }

    public PermissionConfig setActions(Set<String> actions) {
        this.actions = actions;
        return this;
    }

    public PermissionConfig setEndpoints(Set<String> endpoints) {
        this.endpoints = endpoints;
        return this;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PermissionConfig)) {
            return false;
        }
        PermissionConfig that = (PermissionConfig)o;
        if (this.type != that.type) {
            return false;
        }
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        if (this.principal != null ? !this.principal.equals(that.principal) : that.principal != null) {
            return false;
        }
        if (this.endpoints != null ? !this.endpoints.equals(that.endpoints) : that.endpoints != null) {
            return false;
        }
        return this.actions != null ? this.actions.equals(that.actions) : that.actions == null;
    }

    public int hashCode() {
        int result = this.type.hashCode();
        result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
        result = 31 * result + (this.principal != null ? this.principal.hashCode() : 0);
        result = 31 * result + (this.endpoints != null ? this.endpoints.hashCode() : 0);
        result = 31 * result + (this.actions != null ? this.actions.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "PermissionConfig{type=" + (Object)((Object)this.type) + ", name='" + this.name + '\'' + ", principal='" + this.principal + '\'' + ", endpoints=" + this.endpoints + ", actions=" + this.actions + '}';
    }

    public static enum PermissionType {
        ALL("all-permissions"),
        MAP("map-permission"),
        QUEUE("queue-permission"),
        TOPIC("topic-permission"),
        MULTIMAP("multimap-permission"),
        LIST("list-permission"),
        SET("set-permission"),
        ID_GENERATOR("id-generator-permission"),
        FLAKE_ID_GENERATOR("flake-id-generator-permission"),
        LOCK("lock-permission"),
        ATOMIC_LONG("atomic-long-permission"),
        ATOMIC_REFERENCE("atomic-reference-permission"),
        COUNTDOWN_LATCH("countdown-latch-permission"),
        SEMAPHORE("semaphore-permission"),
        EXECUTOR_SERVICE("executor-service-permission"),
        TRANSACTION("transaction-permission"),
        DURABLE_EXECUTOR_SERVICE("durable-executor-service-permission"),
        CARDINALITY_ESTIMATOR("cardinality-estimator-permission"),
        SCHEDULED_EXECUTOR("scheduled-executor-permission"),
        CACHE("cache-permission"),
        USER_CODE_DEPLOYMENT("user-code-deployment-permission"),
        CONFIG("config-permission"),
        PN_COUNTER("pn-counter-permission"),
        RING_BUFFER("ring-buffer-permission"),
        RELIABLE_TOPIC("reliable-topic-permission");

        private final String nodeName;

        private PermissionType(String nodeName) {
            this.nodeName = nodeName;
        }

        public static PermissionType getType(String nodeName) {
            for (PermissionType type : PermissionType.values()) {
                if (!nodeName.equals(type.getNodeName())) continue;
                return type;
            }
            return null;
        }

        public String getNodeName() {
            return this.nodeName;
        }
    }
}

