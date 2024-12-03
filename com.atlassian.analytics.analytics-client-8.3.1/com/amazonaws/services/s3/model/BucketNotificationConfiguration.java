/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.EventBridgeConfiguration;
import com.amazonaws.services.s3.model.NotificationConfiguration;
import com.amazonaws.util.json.Jackson;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BucketNotificationConfiguration
implements Serializable {
    private Map<String, NotificationConfiguration> configurations = new HashMap<String, NotificationConfiguration>();
    private EventBridgeConfiguration eventBridgeConfiguration;

    public BucketNotificationConfiguration() {
    }

    public BucketNotificationConfiguration(String name, NotificationConfiguration notificationConfiguration) {
        this.addConfiguration(name, notificationConfiguration);
    }

    public BucketNotificationConfiguration withNotificationConfiguration(Map<String, NotificationConfiguration> notificationConfiguration) {
        this.configurations.clear();
        this.configurations.putAll(notificationConfiguration);
        return this;
    }

    public BucketNotificationConfiguration addConfiguration(String name, NotificationConfiguration notificationConfiguration) {
        this.configurations.put(name, notificationConfiguration);
        return this;
    }

    public Map<String, NotificationConfiguration> getConfigurations() {
        return this.configurations;
    }

    public void setConfigurations(Map<String, NotificationConfiguration> configurations) {
        this.configurations = configurations;
    }

    public NotificationConfiguration getConfigurationByName(String name) {
        return this.configurations.get(name);
    }

    public NotificationConfiguration removeConfiguration(String name) {
        return this.configurations.remove(name);
    }

    public BucketNotificationConfiguration(Collection<TopicConfiguration> topicConfigurations) {
        if (topicConfigurations != null) {
            for (TopicConfiguration config : topicConfigurations) {
                this.addConfiguration(UUID.randomUUID().toString(), config);
            }
        }
    }

    public BucketNotificationConfiguration withTopicConfigurations(TopicConfiguration ... topicConfigurations) {
        this.setTopicConfigurations(Arrays.asList(topicConfigurations));
        return this;
    }

    public void setTopicConfigurations(Collection<TopicConfiguration> topicConfigurations) {
        this.configurations.clear();
        if (topicConfigurations != null) {
            for (TopicConfiguration topicConfiguration : topicConfigurations) {
                this.addConfiguration(UUID.randomUUID().toString(), topicConfiguration);
            }
        }
    }

    public List<TopicConfiguration> getTopicConfigurations() {
        ArrayList<TopicConfiguration> topicConfigs = new ArrayList<TopicConfiguration>();
        for (Map.Entry<String, NotificationConfiguration> entry : this.configurations.entrySet()) {
            if (!(entry.getValue() instanceof TopicConfiguration)) continue;
            topicConfigs.add((TopicConfiguration)entry.getValue());
        }
        return topicConfigs;
    }

    public EventBridgeConfiguration getEventBridgeConfiguration() {
        return this.eventBridgeConfiguration;
    }

    public void setEventBridgeConfiguration(EventBridgeConfiguration eventBridgeConfiguration) {
        this.eventBridgeConfiguration = eventBridgeConfiguration;
    }

    public BucketNotificationConfiguration withEventBridgeConfiguration(EventBridgeConfiguration eventBridgeConfiguration) {
        this.eventBridgeConfiguration = eventBridgeConfiguration;
        return this;
    }

    public String toString() {
        return Jackson.toJsonString(this.getConfigurations());
    }

    @Deprecated
    public static class TopicConfiguration
    extends com.amazonaws.services.s3.model.TopicConfiguration {
        public TopicConfiguration(String topic, String event) {
            super(topic, new String[]{event});
        }

        public String getTopic() {
            return this.getTopicARN();
        }

        public String getEvent() {
            Set<String> events = this.getEvents();
            String[] eventArray = events.toArray(new String[events.size()]);
            return eventArray[0];
        }

        public String toString() {
            return Jackson.toJsonString(this);
        }
    }
}

