/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.crowd.CrowdUserDirectoryHelper
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.periodic.event.provider;

import com.atlassian.confluence.user.crowd.CrowdUserDirectoryHelper;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.lang.constant.Constable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDirectoryStatisticsProvider {
    private final Logger logger = LoggerFactory.getLogger(UserDirectoryStatisticsProvider.class);
    private final CrowdDirectoryService crowdDirectoryService;
    private final CrowdUserDirectoryHelper crowdUserDirectoryHelper;
    private static final Predicate<Directory> SHOULD_COUNT = directory -> !directory.getType().equals((Object)DirectoryType.CUSTOM) && !directory.getType().equals((Object)DirectoryType.UNKNOWN);
    private static final int FAILED_COUNT = -1;

    @Autowired
    public UserDirectoryStatisticsProvider(@ComponentImport CrowdDirectoryService crowdDirectoryService, @ComponentImport CrowdUserDirectoryHelper crowdUserDirectoryHelper) {
        this.crowdDirectoryService = crowdDirectoryService;
        this.crowdUserDirectoryHelper = crowdUserDirectoryHelper;
    }

    public Map<Long, Map<String, Object>> getUserDirectoryStatistics() {
        HashMap<Long, Map<String, Object>> directories = new HashMap<Long, Map<String, Object>>();
        this.crowdDirectoryService.findAllDirectories().stream().forEach(directoryMapping -> {
            HashMap<String, Constable> attributes = new HashMap<String, Constable>();
            long directoryId = directoryMapping.getId();
            Directory directory = this.crowdDirectoryService.findDirectoryById(directoryId);
            this.logger.debug("Gathering statistics for directory [ {} ]", (Object)directoryId);
            if (SHOULD_COUNT.test(directory)) {
                long startUsers = System.currentTimeMillis();
                attributes.put("users", this.crowdUserDirectoryHelper.getUserCount(directoryId).orElse(-1));
                this.logger.debug("Finished counting users in {} ms", (Object)(System.currentTimeMillis() - startUsers));
                long startGroups = System.currentTimeMillis();
                attributes.put("groups", this.crowdUserDirectoryHelper.getGroupCount(directoryId).orElse(-1));
                this.logger.debug("Finished counting groups in {} ms", (Object)(System.currentTimeMillis() - startGroups));
                long startMemberships = System.currentTimeMillis();
                attributes.put("memberships", this.crowdUserDirectoryHelper.getMembershipCount(directoryId).orElse(-1));
                this.logger.debug("Finished counting memberships in {} ms", (Object)(System.currentTimeMillis() - startMemberships));
            }
            long startDirectoryConfig = System.currentTimeMillis();
            attributes.put("impl", (Constable)this.crowdUserDirectoryHelper.getUserDirectoryImplementation(directoryId));
            attributes.put("type", (Constable)directory.getType());
            attributes.put("active", Boolean.valueOf(directory.isActive()));
            Optional mode = this.crowdUserDirectoryHelper.getSynchronisationMode(directoryId);
            if (mode.isPresent()) {
                attributes.put("sync", (Constable)mode.get());
            }
            this.logger.debug("Finished fetching directory config info in {} ms", (Object)(System.currentTimeMillis() - startDirectoryConfig));
            directories.put(directoryId, attributes);
        });
        return directories;
    }
}

