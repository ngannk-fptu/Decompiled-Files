/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.SynchronisableDirectoryProperties$SyncGroupMembershipsAfterAuth
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.crowd.console.value.directory;

import com.atlassian.crowd.directory.SynchronisableDirectoryProperties;
import com.google.common.collect.ImmutableMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class SyncGroupMembershipsUtils {
    private SyncGroupMembershipsUtils() {
    }

    public static Map<String, String> syncGroupMembershipsAfterAuthOptions() {
        return ImmutableMap.of((Object)SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.WHEN_AUTHENTICATION_CREATED_THE_USER.name(), (Object)"directoryconnector.update.groups.on.auth.whencreated.label", (Object)SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.ALWAYS.name(), (Object)"directoryconnector.update.groups.on.auth.always.label", (Object)SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.NEVER.name(), (Object)"directoryconnector.update.groups.on.auth.never.label");
    }

    public static Map<String, String> syncGroupMembershipsAfterAuthOptionsNames() {
        HashMap<String, String> options = new HashMap<String, String>();
        EnumSet.allOf(SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.class).forEach(option -> options.put(option.name(), option.name()));
        return options;
    }
}

