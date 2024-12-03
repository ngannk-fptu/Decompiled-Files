/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.conversion.impl;

public enum ConfigurationProperties {
    PROP_NUM_THREADS("confluence.document.conversion.threads"),
    PROP_NUM_THREADS_WAIT("confluence.document.conversion.threads.wait"),
    PROP_NUM_THUMBNAIL_THREADS_WAIT("confluence.document.thumbnail.conversion.threads.wait"),
    PROP_CAPABILITY("file.conversions"),
    PROP_CONVERSION_QUEUE_THRESHOLD("confluence.document.conversion.queue.threshold"),
    PROP_SYSTEM_LOAD_RATIO("confluence.document.conversion.system.load.ratio"),
    PROP_USED_MEMORY_RATIO("confluence.document.conversion.used.memory.ratio");

    private final String property;

    private ConfigurationProperties(String property) {
        this.property = property;
    }

    public static int getDefaultInt(ConfigurationProperties property) {
        switch (property) {
            case PROP_NUM_THREADS: {
                return Math.max(1, Math.floorDiv(Runtime.getRuntime().availableProcessors(), 2));
            }
            case PROP_NUM_THREADS_WAIT: {
                return 10000;
            }
            case PROP_NUM_THUMBNAIL_THREADS_WAIT: {
                return 1000;
            }
            case PROP_CONVERSION_QUEUE_THRESHOLD: {
                return 3;
            }
        }
        return -1;
    }

    public static double getDefaultDouble(ConfigurationProperties property) {
        switch (property) {
            case PROP_SYSTEM_LOAD_RATIO: {
                return 0.9;
            }
            case PROP_USED_MEMORY_RATIO: {
                return 0.8;
            }
        }
        return -1.0;
    }

    public static int getInt(ConfigurationProperties property) {
        switch (property) {
            case PROP_NUM_THREADS: 
            case PROP_NUM_THREADS_WAIT: 
            case PROP_NUM_THUMBNAIL_THREADS_WAIT: 
            case PROP_CONVERSION_QUEUE_THRESHOLD: {
                return Integer.getInteger(property.toString(), ConfigurationProperties.getDefaultInt(property));
            }
        }
        return -1;
    }

    public static double getDouble(ConfigurationProperties property) {
        switch (property) {
            case PROP_SYSTEM_LOAD_RATIO: 
            case PROP_USED_MEMORY_RATIO: {
                String value = System.getProperty(property.toString());
                if (value == null) {
                    return ConfigurationProperties.getDefaultDouble(property);
                }
                try {
                    return Double.valueOf(System.getProperty(property.toString()));
                }
                catch (NumberFormatException e) {
                    return ConfigurationProperties.getDefaultDouble(property);
                }
            }
        }
        return -1.0;
    }

    public String toString() {
        return this.property;
    }
}

