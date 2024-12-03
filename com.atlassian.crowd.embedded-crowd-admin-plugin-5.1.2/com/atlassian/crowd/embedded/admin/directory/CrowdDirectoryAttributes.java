/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.SynchronisableDirectoryProperties$SyncGroupMembershipsAfterAuth
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.crowd.embedded.admin.directory;

import com.atlassian.crowd.directory.SynchronisableDirectoryProperties;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class CrowdDirectoryAttributes {
    public static final String APPLICATION_NAME = "application.name";
    public static final String APPLICATION_PASSWORD = "application.password";
    public static final String CROWD_SERVER_URL = "crowd.server.url";
    private String applicationName;
    private String applicationPassword;
    private String crowdServerUrl;
    private boolean nestedGroupsEnabled;
    private boolean incrementalSyncEnabled;
    private String crowdServerSynchroniseIntervalInSeconds;
    private String httpTimeout;
    private String httpMaxConnections;
    private String httpProxyHost;
    private String httpProxyPort;
    private String httpProxyUsername;
    private String httpProxyPassword;
    private SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth groupSyncOnAuthMode = SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.ALWAYS;

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

    public String getCrowdServerUrl() {
        return this.crowdServerUrl;
    }

    public void setCrowdServerUrl(String crowdServerUrl) {
        this.crowdServerUrl = crowdServerUrl;
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

    public String getCrowdServerSynchroniseIntervalInSeconds() {
        return this.crowdServerSynchroniseIntervalInSeconds;
    }

    public void setCrowdServerSynchroniseIntervalInSeconds(String crowdServerSynchroniseIntervalInSeconds) {
        this.crowdServerSynchroniseIntervalInSeconds = crowdServerSynchroniseIntervalInSeconds;
    }

    public String getHttpTimeout() {
        return this.httpTimeout;
    }

    public void setHttpTimeout(String httpTimeout) {
        this.httpTimeout = httpTimeout;
    }

    public String getHttpMaxConnections() {
        return this.httpMaxConnections;
    }

    public void setHttpMaxConnections(String httpMaxConnections) {
        this.httpMaxConnections = httpMaxConnections;
    }

    public String getHttpProxyHost() {
        return this.httpProxyHost;
    }

    public void setHttpProxyHost(String httpProxyHost) {
        this.httpProxyHost = httpProxyHost;
    }

    public String getHttpProxyPort() {
        return this.httpProxyPort;
    }

    public void setHttpProxyPort(String httpProxyPort) {
        this.httpProxyPort = httpProxyPort;
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

    public void setGroupSyncOnAuthMode(SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth membershipSyncOnAuth) {
        this.groupSyncOnAuthMode = membershipSyncOnAuth;
    }

    public SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth getGroupSyncOnAuthMode() {
        return this.groupSyncOnAuthMode;
    }

    public Map<String, String> toAttributesMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(APPLICATION_NAME, this.applicationName);
        map.put(APPLICATION_PASSWORD, this.applicationPassword);
        map.put(CROWD_SERVER_URL, this.crowdServerUrl);
        map.put("useNestedGroups", String.valueOf(this.nestedGroupsEnabled));
        map.put("crowd.sync.incremental.enabled", String.valueOf(this.incrementalSyncEnabled));
        map.put("directory.cache.synchronise.interval", this.crowdServerSynchroniseIntervalInSeconds);
        map.put("crowd.server.http.timeout", StringUtils.stripToNull((String)this.httpTimeout));
        map.put("crowd.server.http.max.connections", StringUtils.stripToNull((String)this.httpMaxConnections));
        map.put("crowd.server.http.proxy.host", StringUtils.stripToNull((String)this.httpProxyHost));
        map.put("crowd.server.http.proxy.port", StringUtils.stripToNull((String)this.httpProxyPort));
        map.put("crowd.server.http.proxy.username", StringUtils.stripToNull((String)this.httpProxyUsername));
        map.put("crowd.server.http.proxy.password", StringUtils.stripToNull((String)this.httpProxyPassword));
        map.put("crowd.sync.group.membership.after.successful.user.auth.enabled", this.groupSyncOnAuthMode.getValue());
        return map;
    }

    public static CrowdDirectoryAttributes fromAttributesMap(Map<String, String> map) {
        CrowdDirectoryAttributes attributes = new CrowdDirectoryAttributes();
        attributes.setApplicationName(map.get(APPLICATION_NAME));
        attributes.setApplicationPassword(map.get(APPLICATION_PASSWORD));
        attributes.setCrowdServerUrl(map.get(CROWD_SERVER_URL));
        attributes.setNestedGroupsEnabled(Boolean.valueOf(map.get("useNestedGroups")));
        attributes.setIncrementalSyncEnabled(Boolean.valueOf(map.get("crowd.sync.incremental.enabled")));
        attributes.setCrowdServerSynchroniseIntervalInSeconds(map.get("directory.cache.synchronise.interval"));
        attributes.setHttpTimeout(map.get("crowd.server.http.timeout"));
        attributes.setHttpMaxConnections(map.get("crowd.server.http.max.connections"));
        attributes.setHttpProxyHost(map.get("crowd.server.http.proxy.host"));
        attributes.setHttpProxyPort(map.get("crowd.server.http.proxy.port"));
        attributes.setHttpProxyUsername(map.get("crowd.server.http.proxy.username"));
        attributes.setHttpProxyPassword(map.get("crowd.server.http.proxy.password"));
        attributes.setGroupSyncOnAuthMode(SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.forValue((String)map.get("crowd.sync.group.membership.after.successful.user.auth.enabled")));
        return attributes;
    }
}

