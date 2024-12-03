/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.SynchronisableDirectoryProperties$SyncGroupMembershipsAfterAuth
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.crowd.embedded.admin.crowd;

import com.atlassian.crowd.directory.SynchronisableDirectoryProperties;
import com.atlassian.crowd.embedded.admin.crowd.CrowdPermissionOption;
import com.atlassian.crowd.embedded.admin.util.ConfigurationWithPassword;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public final class CrowdDirectoryConfiguration
implements ConfigurationWithPassword {
    private static final String CROWD_APPLICATION_PASSWORD_KEY = "application.password";
    private long directoryId;
    private boolean active = true;
    private String name;
    private String crowdServerUrl;
    private String applicationName;
    private String applicationPassword;
    private CrowdPermissionOption crowdPermissionOption = CrowdPermissionOption.READ_ONLY;
    private boolean nestedGroupsEnabled;
    private boolean incrementalSyncEnabled = true;
    private long crowdServerSynchroniseIntervalInMin = 60L;
    private String httpTimeout;
    private String httpMaxConnections;
    private String httpProxyHost;
    private String httpProxyPort;
    private String httpProxyUsername;
    private String httpProxyPassword;
    private SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth groupSyncOnAuthMode = SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.ALWAYS;

    @Override
    public long getDirectoryId() {
        return this.directoryId;
    }

    public void setDirectoryId(long directoryId) {
        this.directoryId = directoryId;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCrowdServerUrl() {
        return this.crowdServerUrl;
    }

    public void setCrowdServerUrl(String crowdServerUrl) {
        this.crowdServerUrl = crowdServerUrl;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationPassword() {
        return this.applicationPassword;
    }

    public void setApplicationPassword(String applicationPassword) {
        this.applicationPassword = applicationPassword;
    }

    public CrowdPermissionOption getCrowdPermissionOption() {
        return this.crowdPermissionOption;
    }

    public void setCrowdPermissionOption(CrowdPermissionOption crowdPermissionOption) {
        this.crowdPermissionOption = crowdPermissionOption;
    }

    public boolean isNestedGroupsEnabled() {
        return this.nestedGroupsEnabled;
    }

    public void setNestedGroupsEnabled(boolean nestedGroupsEnabled) {
        this.nestedGroupsEnabled = nestedGroupsEnabled;
    }

    public boolean isIncrementalSyncEnabled() {
        return this.incrementalSyncEnabled;
    }

    public void setIncrementalSyncEnabled(boolean incrementalSyncEnabled) {
        this.incrementalSyncEnabled = incrementalSyncEnabled;
    }

    public long getCrowdServerSynchroniseIntervalInMin() {
        return this.crowdServerSynchroniseIntervalInMin;
    }

    public void setCrowdServerSynchroniseIntervalInMin(long crowdServerSynchroniseIntervalInMin) {
        this.crowdServerSynchroniseIntervalInMin = crowdServerSynchroniseIntervalInMin;
    }

    @Override
    public void setPassword(String password) {
        this.setApplicationPassword(password);
    }

    @Override
    public String getPassword() {
        return this.getApplicationPassword();
    }

    @Override
    public String getPasswordAttributeKey() {
        return CROWD_APPLICATION_PASSWORD_KEY;
    }

    public String getHttpProxyPort() {
        return this.httpProxyPort;
    }

    public void setHttpProxyPort(String httpProxyPort) {
        this.httpProxyPort = httpProxyPort;
    }

    public String getHttpProxyHost() {
        return this.httpProxyHost;
    }

    public void setHttpProxyHost(String httpProxyHost) {
        this.httpProxyHost = httpProxyHost;
    }

    public String getHttpProxyUsername() {
        return this.httpProxyUsername;
    }

    public void setHttpProxyUsername(String httpProxyUsername) {
        this.httpProxyUsername = httpProxyUsername;
    }

    public String getHttpProxyPassword() {
        return this.httpProxyPassword;
    }

    public void setHttpProxyPassword(String httpProxyPassword) {
        this.httpProxyPassword = httpProxyPassword;
    }

    public String getHttpMaxConnections() {
        return this.httpMaxConnections;
    }

    public void setHttpMaxConnections(String httpMaxConnections) {
        this.httpMaxConnections = httpMaxConnections;
    }

    public String getHttpTimeout() {
        return this.httpTimeout;
    }

    public void setHttpTimeout(String httpTimeout) {
        this.httpTimeout = httpTimeout;
    }

    public void setGroupSyncOnAuthMode(SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth groupSyncOnAuthMode) {
        this.groupSyncOnAuthMode = groupSyncOnAuthMode;
    }

    public SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth getGroupSyncOnAuthMode() {
        return this.groupSyncOnAuthMode;
    }
}

