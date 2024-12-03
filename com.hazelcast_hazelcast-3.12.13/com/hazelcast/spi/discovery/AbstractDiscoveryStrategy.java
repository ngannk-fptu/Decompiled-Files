/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.discovery;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.partitiongroup.PartitionGroupStrategy;
import com.hazelcast.util.StringUtil;
import java.util.Collections;
import java.util.Map;

public abstract class AbstractDiscoveryStrategy
implements DiscoveryStrategy {
    private final ILogger logger;
    private final Map<String, Comparable> properties;

    public AbstractDiscoveryStrategy(ILogger logger, Map<String, Comparable> properties) {
        this.logger = logger;
        this.properties = Collections.unmodifiableMap(properties);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void start() {
    }

    @Override
    public PartitionGroupStrategy getPartitionGroupStrategy() {
        return null;
    }

    @Override
    public Map<String, Object> discoverLocalMetadata() {
        return Collections.emptyMap();
    }

    protected Map<String, Comparable> getProperties() {
        return this.properties;
    }

    protected ILogger getLogger() {
        return this.logger;
    }

    protected <T extends Comparable> T getOrNull(PropertyDefinition property) {
        return this.getOrDefault(property, null);
    }

    protected <T extends Comparable> T getOrNull(String prefix, PropertyDefinition property) {
        return this.getOrDefault(prefix, property, null);
    }

    protected <T extends Comparable> T getOrDefault(PropertyDefinition property, T defaultValue) {
        return this.getOrDefault(null, property, defaultValue);
    }

    protected <T extends Comparable> T getOrDefault(String prefix, PropertyDefinition property, T defaultValue) {
        if (property == null) {
            return defaultValue;
        }
        Comparable value = this.readProperty(prefix, property);
        if (value == null) {
            value = this.properties.get(property.key());
        }
        if (value == null) {
            return defaultValue;
        }
        return (T)value;
    }

    private Comparable readProperty(String prefix, PropertyDefinition property) {
        if (prefix != null) {
            String p = this.getProperty(prefix, property);
            String v = System.getProperty(p);
            if (StringUtil.isNullOrEmpty(v)) {
                v = System.getenv(p);
            }
            if (!StringUtil.isNullOrEmpty(v)) {
                return property.typeConverter().convert((Comparable)((Object)v));
            }
        }
        return null;
    }

    private String getProperty(String prefix, PropertyDefinition property) {
        StringBuilder sb = new StringBuilder(prefix);
        if (prefix.charAt(prefix.length() - 1) != '.') {
            sb.append('.');
        }
        return sb.append(property.key()).toString();
    }
}

