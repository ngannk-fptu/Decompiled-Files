/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Attributes
 *  javax.annotation.Nonnull
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.embedded.api.Attributes;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class SynchronisableDirectoryProperties {
    @Deprecated
    public static final String CURRENT_START_SYNC_TIME = "com.atlassian.crowd.directory.sync.currentstartsynctime";
    @Deprecated
    public static final String LAST_START_SYNC_TIME = "com.atlassian.crowd.directory.sync.laststartsynctime";
    @Deprecated
    public static final String LAST_SYNC_DURATION_MS = "com.atlassian.crowd.directory.sync.lastdurationms";
    public static final String CACHE_SYNCHRONISE_INTERVAL = "directory.cache.synchronise.interval";
    public static final String CACHE_SYNCHRONISE_CRON = "directory.cache.synchronise.cron";
    public static final String CACHE_SYNCHRONISATION_TYPE = "directory.cache.synchronise.type";
    @Deprecated
    public static final String IS_SYNCHRONISING = "com.atlassian.crowd.directory.sync.issynchronising";
    public static final String INCREMENTAL_SYNC_ENABLED = "crowd.sync.incremental.enabled";
    public static final String SYNC_USER_FILTER_CQL = "com.atlassian.crowd.sync.user.filter.cql";
    public static final String SYNC_GROUP_MEMBERSHIP_AFTER_SUCCESSFUL_USER_AUTH_ENABLED = "crowd.sync.group.membership.after.successful.user.auth.enabled";
    public static final String CONNECTION_TIMEOUT_IN_MILLISECONDS = "ldap.connection.timeout";
    public static final String READ_TIMEOUT_IN_MILLISECONDS = "ldap.read.timeout";

    private SynchronisableDirectoryProperties() {
    }

    public static enum SyncGroupMembershipsAfterAuth {
        NEVER("false"),
        ALWAYS("true"),
        WHEN_AUTHENTICATION_CREATED_THE_USER("only_when_first_created");

        private final String value;
        public static final SyncGroupMembershipsAfterAuth DEFAULT;
        private static final SyncGroupMembershipsAfterAuth LEGACY_DEFAULT;

        private SyncGroupMembershipsAfterAuth(String value) {
            this.value = value;
        }

        public static SyncGroupMembershipsAfterAuth forDirectory(@Nonnull Attributes directoryWithAttributes) {
            return SyncGroupMembershipsAfterAuth.forValue(directoryWithAttributes.getValue(SynchronisableDirectoryProperties.SYNC_GROUP_MEMBERSHIP_AFTER_SUCCESSFUL_USER_AUTH_ENABLED));
        }

        public static SyncGroupMembershipsAfterAuth forValue(String attributeValue) {
            return Arrays.stream(SyncGroupMembershipsAfterAuth.values()).filter(item -> item.value.equals(attributeValue)).findFirst().orElse(LEGACY_DEFAULT);
        }

        public String getValue() {
            return this.value;
        }

        static {
            DEFAULT = WHEN_AUTHENTICATION_CREATED_THE_USER;
            LEGACY_DEFAULT = ALWAYS;
        }
    }
}

