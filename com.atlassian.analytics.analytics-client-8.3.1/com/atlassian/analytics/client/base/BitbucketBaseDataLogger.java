/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bitbucket.license.LicenseService
 *  com.atlassian.bitbucket.permission.Permission
 *  com.atlassian.bitbucket.project.Project
 *  com.atlassian.bitbucket.project.ProjectService
 *  com.atlassian.bitbucket.repository.RepositoryService
 *  com.atlassian.bitbucket.user.SecurityService
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.analytics.client.base;

import com.atlassian.analytics.client.base.BaseDataLogger;
import com.atlassian.analytics.client.base.BitbucketBaseDataEvent;
import com.atlassian.bitbucket.license.LicenseService;
import com.atlassian.bitbucket.permission.Permission;
import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.project.ProjectService;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.user.SecurityService;
import com.atlassian.event.api.EventPublisher;
import java.util.List;

public class BitbucketBaseDataLogger
implements BaseDataLogger {
    private final EventPublisher eventPublisher;
    private final ProjectService projectService;
    private final RepositoryService repositoryService;
    private final LicenseService licenseService;
    private final SecurityService securityService;

    public BitbucketBaseDataLogger(EventPublisher eventPublisher, ProjectService projectService, RepositoryService repositoryService, LicenseService licenseService, SecurityService securityService) {
        this.eventPublisher = eventPublisher;
        this.projectService = projectService;
        this.repositoryService = repositoryService;
        this.licenseService = licenseService;
        this.securityService = securityService;
    }

    @Override
    public void logBaseData() {
        this.securityService.withPermission(Permission.PROJECT_READ, "Logging base data for analytics").call(() -> {
            List projectKeys = this.projectService.findAllKeys();
            int projectCount = projectKeys.size();
            int repositoryCount = 0;
            for (String key : projectKeys) {
                Project project = this.projectService.getByKey(key);
                if (project == null) continue;
                repositoryCount += this.repositoryService.countByProject(project);
            }
            int userCount = this.licenseService.getLicensedUsersCount();
            this.eventPublisher.publish((Object)new BitbucketBaseDataEvent(userCount, projectCount, repositoryCount));
            return null;
        });
    }
}

