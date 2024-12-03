/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 */
package com.atlassian.confluence.setup;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.cluster.shareddata.SharedDataManager;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SharedConfigurationMap
implements Serializable {
    private Map attributes = new HashMap();
    private static final String PUBLISHED_INSTANCE_NAME = "instance";

    public SharedConfigurationMap(ApplicationConfiguration config) {
        this.attributes.putAll(config.getProperties());
    }

    public Serializable get(Serializable key) {
        return (Serializable)this.attributes.get(key);
    }

    public void put(Serializable key, Serializable value) {
        this.attributes.put(key, value);
    }

    public Collection keySet() {
        return this.attributes.keySet();
    }

    public void publish(SharedDataManager clusterSharedDataManager) {
        SharedConfigurationMap.getPublishMap(clusterSharedDataManager).put(PUBLISHED_INSTANCE_NAME, this);
    }

    public static SharedConfigurationMap getPublished(SharedDataManager clusterSharedDataManager) {
        return SharedConfigurationMap.getPublishMap(clusterSharedDataManager).get(PUBLISHED_INSTANCE_NAME);
    }

    private static Map<String, SharedConfigurationMap> getPublishMap(SharedDataManager clusterSharedDataManager) {
        return clusterSharedDataManager.getSharedData(SharedConfigurationMap.class.getName()).getMap();
    }
}

