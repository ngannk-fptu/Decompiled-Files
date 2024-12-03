/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.common.properties;

import com.atlassian.crowd.common.properties.BooleanSystemProperty;
import com.atlassian.crowd.common.properties.DurationSystemProperty;
import com.atlassian.crowd.common.properties.IntegerSystemProperty;
import java.time.temporal.ChronoUnit;

public class SystemProperties {
    public static final BooleanSystemProperty INCLUDE_USERNAME_HEADER_IN_RESPONSES = new BooleanSystemProperty("crowd.username.header", false);
    public static final BooleanSystemProperty INCLUDE_APPNAME_HEADER_IN_RESPONSES = new BooleanSystemProperty("crowd.appname.header", false);
    public static final BooleanSystemProperty MEMBERSHIPS_SYNC_IMPROVEMENT_ENABLED = new BooleanSystemProperty("crowd.sync.memberships.improvement.enabled", true);
    public static final BooleanSystemProperty ALLOW_DUPLICATED_EXTERNAL_IDS_IN_SYNC = new BooleanSystemProperty("crowd.sync.allow.duplicated.external.ids", true);
    public static final DurationSystemProperty APPLICATION_STATUS_CACHE_DURATION = new DurationSystemProperty("crowd.application.status.cache.in.seconds", ChronoUnit.SECONDS, 10L);
    public static final BooleanSystemProperty ATLASSIAN_DEV_MODE = new BooleanSystemProperty("atlassian.dev.mode", false);
    public static final BooleanSystemProperty EMAIL_CHANGE_BY_EXTERNAL_APPS_ENABLED = new BooleanSystemProperty("crowd.email.change.by.external.apps", false);
    public static final BooleanSystemProperty SWALLOW_EXCEPTIONS_IN_DIRECTORY_SEARCH = new BooleanSystemProperty("crowd.directory.search.return.defaults.on.errors", false);
    public static final IntegerSystemProperty EVENT_TRANSFORMER_DIRECTORY_MANAGER_CACHE_SIZE = new IntegerSystemProperty("crowd.event.transformer.directory.manager.cache.size", 1000);
    public static final BooleanSystemProperty RECREATED_MEMBERSHIPS_BATCHING_ENABLED = SystemProperties.createBooleanSystemProperty("crowd.sync.recreated.memberships.batching.enabled", true, false);
    private static final String CROWD_SPECIFIC_CLASS_NAME = "com.atlassian.crowd.sso.saml.SsoSamlConfiguration";
    private static Boolean isCrowd;
    public static final BooleanSystemProperty AUDITLOG_SEARCH_ESCAPE_SPECIAL_CHARACTERS_ENABLED;

    private SystemProperties() {
    }

    public static BooleanSystemProperty createBooleanSystemProperty(String propertyName, boolean defaultForCrowd, boolean defaultForNonCrowd) {
        return new BooleanSystemProperty(propertyName, () -> SystemProperties.isCrowd() ? defaultForCrowd : defaultForNonCrowd);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static boolean isCrowd() {
        if (isCrowd != null) return isCrowd;
        Class<SystemProperties> clazz = SystemProperties.class;
        synchronized (SystemProperties.class) {
            if (isCrowd != null) return isCrowd;
            isCrowd = SystemProperties.isCrowdInternal();
            // ** MonitorExit[var0] (shouldn't be in output)
            return isCrowd;
        }
    }

    private static boolean isCrowdInternal() {
        try {
            return System.getProperty("crowd.home") != null && Class.forName(CROWD_SPECIFIC_CLASS_NAME) != null;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    static {
        AUDITLOG_SEARCH_ESCAPE_SPECIAL_CHARACTERS_ENABLED = new BooleanSystemProperty("crowd.audit.log.escape_special_characters", true);
    }
}

