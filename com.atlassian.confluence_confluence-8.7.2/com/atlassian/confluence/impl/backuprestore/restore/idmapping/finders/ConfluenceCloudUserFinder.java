/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobSource
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.spi.UserDao
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.atlassian.sal.api.user.UserKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.idmapping.finders;

import com.atlassian.confluence.api.model.backuprestore.JobSource;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.finders.AbstractUserEntityFinder;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.spi.UserDao;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.atlassian.sal.api.user.UserKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceCloudUserFinder
extends AbstractUserEntityFinder {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceCloudUserFinder.class);
    private final UserDao embeddedCrowdUserDao;
    private final CrowdDirectoryService directoryService;

    public ConfluenceCloudUserFinder(ConfluenceUserDao confluenceUserDao, CrowdDirectoryService directoryService, UserDao embeddedCrowdUserDao) {
        super(confluenceUserDao);
        this.directoryService = directoryService;
        this.embeddedCrowdUserDao = embeddedCrowdUserDao;
    }

    @Override
    public Map<ImportedObjectV2, UserKey> doSecondStageFind(List<ImportedObjectV2> importedObjectPartition) {
        return this.getUsersByEmails(importedObjectPartition);
    }

    public Map<ImportedObjectV2, UserKey> getUsersByEmails(List<ImportedObjectV2> importedObjects) {
        HashMap<ImportedObjectV2, UserKey> usersByImportedObject = new HashMap<ImportedObjectV2, UserKey>();
        for (ImportedObjectV2 importedObject : importedObjects) {
            ConfluenceUser user;
            String username = (String)importedObject.getFieldValue("name");
            EntityQuery query = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user()).with((SearchRestriction)Restriction.on((Property)UserTermKeys.EMAIL).exactlyMatching((Object)username)).returningAtMost(2);
            List matchedUsernames = this.directoryService.findAllDirectories().stream().flatMap(dir -> this.embeddedCrowdUserDao.search(dir.getId().longValue(), query).stream()).map(String::toLowerCase).distinct().collect(Collectors.toList());
            if (matchedUsernames.size() == 1) {
                user = this.confluenceUserDao.findByUsername((String)matchedUsernames.get(0));
                if (user == null) continue;
                usersByImportedObject.put(importedObject, user.getKey());
                continue;
            }
            if (matchedUsernames.isEmpty()) {
                user = this.confluenceUserDao.findByUsername(username);
                if (user == null) continue;
                usersByImportedObject.put(importedObject, user.getKey());
                continue;
            }
            log.debug("Ambiguous result while trying to match user by email address. An Unknown User will be created for email [{}] entry.", (Object)username);
        }
        return usersByImportedObject;
    }

    @Override
    public Class<?> getSupportedClass() {
        return ConfluenceUserImpl.class;
    }

    @Override
    public boolean isSupportedJobSource(JobSource jobSource) {
        return JobSource.CLOUD.equals((Object)jobSource);
    }
}

