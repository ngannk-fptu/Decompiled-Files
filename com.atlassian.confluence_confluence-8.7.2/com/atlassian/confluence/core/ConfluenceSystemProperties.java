/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.core;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ConfluenceSystemProperties {
    public static final String CONFLUENCE_DEV_MODE = "confluence.devmode";
    public static final String ATLASSIAN_DEV_MODE = "atlassian.dev.mode";
    private static boolean isDevMode;

    private static void refreshDevMode() {
        isDevMode = Boolean.getBoolean(CONFLUENCE_DEV_MODE) || Boolean.getBoolean(ATLASSIAN_DEV_MODE);
    }

    public static boolean isDevMode() {
        return isDevMode;
    }

    public static boolean isDisableCaches() {
        return Boolean.valueOf(System.getProperty("atlassian.disable.caches"));
    }

    public static boolean isBundledPluginsDisabled() {
        return Boolean.getBoolean("confluence.plugins.bundled.disable");
    }

    public static boolean isBackupPathSetAllowed() {
        return Boolean.getBoolean("confluence.backup.path.set.allowed");
    }

    public static boolean isEnableHazelcastJMX() {
        return Boolean.getBoolean("confluence.hazelcast.jmx.enable");
    }

    public static boolean isEnableHibernateJMX() {
        return Boolean.getBoolean("confluence.hibernate.jmx.enable");
    }

    public static boolean isAjsLogRendered() {
        return Boolean.getBoolean("ajs.log.rendered");
    }

    public static boolean isAjsLogVisible() {
        return Boolean.getBoolean("ajs.log.visible");
    }

    public static boolean isContextBatchingDisabled() {
        return Boolean.getBoolean("confluence.context.batching.disable");
    }

    public static @Nullable String getConfluenceFrontendServiceURL() {
        return System.getProperty("confluence.frontend.service.url");
    }

    public static @Nullable String getConfluenceFixedCDNPrefix() {
        return System.getProperty("confluence.fixed.cdn.prefix");
    }

    public static @Nullable String getConfluenceBaseUrlCDNPrefix() {
        return System.getProperty("confluence.baseurl.cdn.prefix");
    }

    public static @Nullable String getTenantTimezoneId() {
        return System.getProperty("confluence.tenant.timezone");
    }

    public static @Nullable String getHumanReadableClusterNodeName() {
        return System.getProperty("confluence.cluster.node.name");
    }

    public static boolean isUseHostnameAsHumanReadableClusterNodeName() {
        return Boolean.getBoolean("confluence.clusterNodeName.useHostname");
    }

    public static boolean isSynchronyDisabled() {
        return Boolean.getBoolean("synchrony.btf.disabled");
    }

    public static @Nullable String getHazelcastManagementCenterUrl() {
        return System.getProperty("confluence.hazelcast.managementcenter.url");
    }

    static {
        ConfluenceSystemProperties.refreshDevMode();
    }
}

