/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.SynchronisableDirectoryProperties$SyncGroupMembershipsAfterAuth
 *  com.atlassian.crowd.directory.monitor.poller.PollerConfig
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.directory.DirectoryImpl
 */
package com.atlassian.crowd.console.value.directory;

import com.atlassian.crowd.console.value.directory.SyncGroupMembershipsUtils;
import com.atlassian.crowd.directory.SynchronisableDirectoryProperties;
import com.atlassian.crowd.directory.monitor.poller.PollerConfig;
import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import java.util.Map;
import java.util.TimeZone;

public abstract class SynchronisableDirectoryConnection {
    private final String serverTimeZone = TimeZone.getDefault().getID();
    private SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth syncGroupMembershipsAfterAuth = SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.DEFAULT;
    private boolean incrementalSyncEnabled = false;
    private PollerConfig pollerConfig = new PollerConfig();

    public void loadFromDirectory(Directory directory) {
        this.incrementalSyncEnabled = Boolean.parseBoolean(directory.getValue("crowd.sync.incremental.enabled"));
        this.pollerConfig.copyFrom(directory.getAttributes());
        this.syncGroupMembershipsAfterAuth = SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.forDirectory((Attributes)directory);
    }

    public void updateDirectory(DirectoryImpl directory) {
        directory.setAttribute("crowd.sync.incremental.enabled", Boolean.toString(this.incrementalSyncEnabled));
        directory.setAttribute("crowd.sync.group.membership.after.successful.user.auth.enabled", this.syncGroupMembershipsAfterAuth.getValue());
        this.pollerConfig.copyTo(directory.getAttributes());
    }

    protected long getAttributeValueAsLong(Directory directory, String attributeName) {
        String value = directory.getValue(attributeName);
        return value == null ? 0L : Long.parseLong(value);
    }

    public boolean isIncrementalSyncEnabled() {
        return this.incrementalSyncEnabled;
    }

    public void setIncrementalSyncEnabled(boolean incrementalSyncEnabled) {
        this.incrementalSyncEnabled = incrementalSyncEnabled;
    }

    public long getPollingIntervalInMin() {
        return this.pollerConfig.getPollingIntervalInMin();
    }

    public void setPollingIntervalInMin(long pollingIntervalInMin) {
        this.pollerConfig.setPollingIntervalInMin(pollingIntervalInMin);
    }

    public String getSyncGroupMembershipsAfterAuth() {
        return this.syncGroupMembershipsAfterAuth.name();
    }

    public void setSyncGroupMembershipsAfterAuth(String syncGroupMembershipsAfterAuth) {
        this.syncGroupMembershipsAfterAuth = SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.valueOf((String)syncGroupMembershipsAfterAuth);
    }

    public String getCronExpression() {
        return this.pollerConfig.getCronExpression();
    }

    public void setCronExpression(String cronExpression) {
        this.pollerConfig.setCronExpression(cronExpression);
    }

    public String getSynchronisationType() {
        return this.pollerConfig.getSynchronisationType();
    }

    public void setSynchronisationType(String synchronisationType) {
        this.pollerConfig.setSynchronisationType(synchronisationType);
    }

    public Map<String, String> getSyncGroupMembershipsAfterAuthValues() {
        return SyncGroupMembershipsUtils.syncGroupMembershipsAfterAuthOptions();
    }

    public String getServerTimeZone() {
        return this.serverTimeZone;
    }
}

