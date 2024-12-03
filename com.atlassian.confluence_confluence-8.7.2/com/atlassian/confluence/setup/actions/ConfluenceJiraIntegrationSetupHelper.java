/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteCrowdDirectory
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException
 *  com.atlassian.crowd.model.application.ApplicationType
 *  com.atlassian.crowd.model.directory.DirectoryImpl
 *  com.atlassian.dragonfly.spi.JiraIntegrationSetupHelper
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.crowd.directory.RemoteCrowdDirectory;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException;
import com.atlassian.crowd.model.application.ApplicationType;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import com.atlassian.dragonfly.spi.JiraIntegrationSetupHelper;
import java.net.URI;
import java.util.EnumSet;

public class ConfluenceJiraIntegrationSetupHelper
implements JiraIntegrationSetupHelper {
    private static final String REMOTE_JIRA_DIRECTORY_NAME = "Remote Jira Directory";
    private CrowdDirectoryService crowdDirectoryService;
    private Directory jiraRemoteDirectory;

    public ConfluenceJiraIntegrationSetupHelper(CrowdDirectoryService crowdDirectoryService) {
        this.crowdDirectoryService = crowdDirectoryService;
    }

    public ApplicationType getApplicationType() {
        return ApplicationType.CONFLUENCE;
    }

    public void switchToCrowdAuthentication(URI remoteJiraUrl, String applicationName, String applicationPassword) {
        DirectoryImpl newRemoteDirectory = this.buildDirectoryConfiguration(remoteJiraUrl, applicationName, applicationPassword);
        this.jiraRemoteDirectory = this.crowdDirectoryService.addDirectory((Directory)newRemoteDirectory);
        this.crowdDirectoryService.setDirectoryPosition(this.jiraRemoteDirectory.getId().longValue(), 0);
    }

    public void switchToDefaultAuthentication() {
        Directory directory;
        if (this.jiraRemoteDirectory != null && (directory = this.crowdDirectoryService.findDirectoryById(this.jiraRemoteDirectory.getId().longValue())) != null) {
            try {
                this.crowdDirectoryService.removeDirectory(this.jiraRemoteDirectory.getId().longValue());
            }
            catch (DirectoryCurrentlySynchronisingException e) {
                throw new RuntimeException("Cannot undo the JAACS", e);
            }
        }
    }

    private String getDirectoryName() {
        return REMOTE_JIRA_DIRECTORY_NAME;
    }

    private DirectoryImpl buildDirectoryConfiguration(URI remoteJiraUrl, String applicationName, String applicationPassword) {
        String directoryName = this.getDirectoryName();
        DirectoryImpl directory = new DirectoryImpl(directoryName, DirectoryType.CROWD, RemoteCrowdDirectory.class.getName());
        directory.setActive(false);
        directory.setAttribute("crowd.server.url", remoteJiraUrl.toString());
        directory.setAttribute("application.name", applicationName);
        directory.setAttribute("application.password", applicationPassword);
        directory.setAttribute("directory.cache.synchronise.interval", Long.toString(3600L));
        directory.setAttribute("crowd.server.http.timeout", Long.toString(5000L));
        directory.setAttribute("crowd.server.http.max.connections", Long.toString(20L));
        directory.setAttribute("com.atlassian.crowd.directory.sync.cache.enabled", Boolean.toString(true));
        directory.setAttribute("useNestedGroups", Boolean.toString(false));
        directory.setAllowedOperations(EnumSet.allOf(OperationType.class));
        return directory;
    }

    public long getJiraRemoteDirectoryId() {
        return this.jiraRemoteDirectory.getId();
    }
}

