/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.dao.application.ApplicationDAO
 *  com.atlassian.crowd.directory.InternalDirectory
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.impl.DefaultConnectionPoolProperties
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ApplicationType
 *  com.atlassian.crowd.model.application.ImmutableApplication
 *  com.atlassian.crowd.model.directory.ImmutableDirectory
 *  com.google.common.collect.Sets
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.crowd;

import com.atlassian.confluence.user.crowd.DirectoryState;
import com.atlassian.crowd.dao.application.ApplicationDAO;
import com.atlassian.crowd.directory.InternalDirectory;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.impl.DefaultConnectionPoolProperties;
import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ApplicationType;
import com.atlassian.crowd.model.application.ImmutableApplication;
import com.atlassian.crowd.model.directory.ImmutableDirectory;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EmbeddedCrowdBootstrap {
    public static final String INTERNAL_DIRECTORY_NAME = "Confluence Internal Directory";
    public static final String APPLICATION_NAME = "crowd-embedded";
    private static final Logger log = LoggerFactory.getLogger(EmbeddedCrowdBootstrap.class);
    private CrowdDirectoryService crowdDirectoryService;
    private ApplicationDAO applicationDao;

    public void bootstrap() {
        this.ensureApplicationExists();
        this.ensureInternalDirectoryExists();
    }

    public boolean ensureApplicationExists() {
        try {
            this.applicationDao.findByName(APPLICATION_NAME);
            return false;
        }
        catch (ApplicationNotFoundException e) {
            ImmutableApplication application = ImmutableApplication.builder((String)APPLICATION_NAME, (ApplicationType)ApplicationType.GENERIC_APPLICATION).setActive(true).setAttributes(new DefaultConnectionPoolProperties().toPropertiesMap()).setMembershipAggregationEnabled(true).build();
            this.applicationDao.add((Application)application, PasswordCredential.NONE);
            return true;
        }
    }

    public void ensureInternalDirectoryExists() {
        this.ensureInternalDirectoryExists(DirectoryState.ENABLED);
    }

    public void ensureInternalDirectoryExists(DirectoryState state) {
        if (this.hasInternalDirectory(this.crowdDirectoryService.findAllDirectories())) {
            return;
        }
        ImmutableDirectory defaultDirectory = ImmutableDirectory.builder((String)INTERNAL_DIRECTORY_NAME, (DirectoryType)DirectoryType.INTERNAL, (String)InternalDirectory.class.getName()).setActive(state == DirectoryState.ENABLED).setAllowedOperations((Set)Sets.newHashSet((Object[])OperationType.values())).setDescription("Confluence default internal directory").setAttribute("user_encryption_method", "atlassian-security").build();
        Directory directory = this.crowdDirectoryService.addDirectory((Directory)defaultDirectory);
        log.info("Created default internal directory: {}, isActive: {}", (Object)directory, (Object)(directory != null && directory.isActive() ? 1 : 0));
    }

    private boolean hasInternalDirectory(List<Directory> allDirectories) {
        boolean found = false;
        for (Directory directory : allDirectories) {
            if (!directory.getImplementationClass().equals(InternalDirectory.class.getName())) continue;
            found = true;
            break;
        }
        return found;
    }

    public void setCrowdDirectoryService(CrowdDirectoryService crowdDirectoryService) {
        this.crowdDirectoryService = crowdDirectoryService;
    }

    public void setApplicationDao(ApplicationDAO applicationDao) {
        this.applicationDao = applicationDao;
    }
}

