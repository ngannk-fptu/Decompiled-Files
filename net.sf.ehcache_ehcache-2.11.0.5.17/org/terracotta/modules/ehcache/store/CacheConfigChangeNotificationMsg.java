/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.store;

import java.io.Serializable;

public class CacheConfigChangeNotificationMsg
implements Serializable {
    private final String fullyQualifiedEhcacheName;
    private final String toolkitConfigName;
    private final Serializable newValue;

    public CacheConfigChangeNotificationMsg(String fullyQualifiedCacheName, String configName, Serializable newValue) {
        this.fullyQualifiedEhcacheName = fullyQualifiedCacheName;
        this.toolkitConfigName = configName;
        this.newValue = newValue;
    }

    public String getToolkitConfigName() {
        return this.toolkitConfigName;
    }

    public Serializable getNewValue() {
        return this.newValue;
    }

    public String getFullyQualifiedEhcacheName() {
        return this.fullyQualifiedEhcacheName;
    }

    public String toString() {
        return "CacheConfigChangeNotificationMsg [fullyQualifiedEhcacheName=" + this.fullyQualifiedEhcacheName + ", toolkitConfigName=" + this.toolkitConfigName + ", newValue=" + this.newValue + "]";
    }
}

