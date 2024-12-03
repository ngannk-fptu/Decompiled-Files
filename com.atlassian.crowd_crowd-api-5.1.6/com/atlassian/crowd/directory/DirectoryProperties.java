/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.embedded.api.Directory;

public class DirectoryProperties {
    public static final String CACHE_ENABLED = "com.atlassian.crowd.directory.sync.cache.enabled";

    private DirectoryProperties() {
    }

    public static boolean cachesAllUsers(Directory directory) {
        return DirectoryProperties.cachesUsers(directory, true);
    }

    public static boolean cachesAnyUsers(Directory directory) {
        return DirectoryProperties.cachesUsers(directory, false);
    }

    private static boolean cachesUsers(Directory directory, boolean fullyCached) {
        switch (directory.getType()) {
            case CUSTOM: 
            case UNKNOWN: {
                return false;
            }
            case INTERNAL: 
            case AZURE_AD: {
                return true;
            }
            case CROWD: 
            case CONNECTOR: {
                return Boolean.parseBoolean((String)directory.getAttributes().get(CACHE_ENABLED));
            }
            case DELEGATING: {
                return !fullyCached;
            }
        }
        return false;
    }
}

