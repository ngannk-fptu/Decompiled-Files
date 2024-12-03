/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.salext.bundle;

import com.atlassian.troubleshooting.stp.salext.bundle.BundlePriority;
import java.util.EnumSet;
import javax.annotation.Nonnull;

@Deprecated
public enum BundleManifest {
    AUTH_CONFIG("auth-cfg", BundlePriority.RECOMMENDED),
    APPLICATION_CONFIG("application-config", BundlePriority.RECOMMENDED),
    APPLICATION_PROPERTIES("application-properties", BundlePriority.REQUIRED),
    CONF_CUSTOMISATIONS("confluence-customisations", BundlePriority.HIGHLY_RECOMMENDED),
    HEALTHCHECKS("healthchecks", BundlePriority.HIGHLY_RECOMMENDED),
    APPLICATION_LOGS("application-logs", BundlePriority.HIGHLY_RECOMMENDED),
    CACHE_CONFIG("cache-cfg", BundlePriority.DEFAULT),
    FECRU_OUT("fecru-out", BundlePriority.HIGHLY_RECOMMENDED),
    FECRU_PLUGIN_STATE("fecru-pluginstate-properties", BundlePriority.RECOMMENDED),
    MODZ("modz", BundlePriority.RECOMMENDED),
    PLUGIN_CONFIG("fecru-plugin-cfg", BundlePriority.DEFAULT),
    THREAD_DUMP("thread-dump", BundlePriority.RECOMMENDED),
    TOMCAT_CONFIG("tomcat-config", BundlePriority.DEFAULT),
    TOMCAT_LOGS("tomcat-logs", BundlePriority.RECOMMENDED),
    TOMCAT_ACCESS_LOGS("tomcat-access-logs", BundlePriority.RECOMMENDED),
    SYNCHRONY_CONFIG("synchrony-config", BundlePriority.RECOMMENDED),
    CLOUD_MIGRATION_LOGS("cloud-migration-logs", BundlePriority.RECOMMENDED),
    JFR_BUNDLE("jfr-bundle", BundlePriority.RECOMMENDED);

    private final String key;
    private final BundlePriority priority;

    private BundleManifest(String key, BundlePriority priority) {
        this.key = key;
        this.priority = priority;
    }

    @Nonnull
    public static EnumSet<BundleManifest> getDefaults() {
        EnumSet<BundleManifest> items = EnumSet.allOf(BundleManifest.class);
        items.remove((Object)THREAD_DUMP);
        items.remove((Object)TOMCAT_ACCESS_LOGS);
        return items;
    }

    public String getKey() {
        return this.key;
    }

    public BundlePriority getPriority() {
        return this.priority;
    }

    public boolean isRequired() {
        return this.priority == BundlePriority.REQUIRED;
    }
}

