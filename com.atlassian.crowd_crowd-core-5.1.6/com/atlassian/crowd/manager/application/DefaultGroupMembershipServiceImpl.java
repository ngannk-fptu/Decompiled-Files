/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.dao.application.ApplicationDefaultGroupMembershipConfigurationDao
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.exception.DirectoryMappingNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.manager.application.DefaultGroupMembershipService
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ApplicationDefaultGroupMembershipConfiguration
 *  com.atlassian.crowd.model.application.ApplicationDirectoryMapping
 *  com.google.common.collect.ImmutableList
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.crowd.manager.application;

import com.atlassian.crowd.dao.application.ApplicationDefaultGroupMembershipConfigurationDao;
import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.exception.DirectoryMappingNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.manager.application.DefaultGroupMembershipService;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ApplicationDefaultGroupMembershipConfiguration;
import com.atlassian.crowd.model.application.ApplicationDirectoryMapping;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultGroupMembershipServiceImpl
implements DefaultGroupMembershipService {
    private final ApplicationDefaultGroupMembershipConfigurationDao applicationDefaultGroupMembershipConfigurationDao;

    public DefaultGroupMembershipServiceImpl(ApplicationDefaultGroupMembershipConfigurationDao applicationDefaultGroupMembershipConfigurationDao) {
        this.applicationDefaultGroupMembershipConfigurationDao = applicationDefaultGroupMembershipConfigurationDao;
    }

    public void add(Application application, ApplicationDirectoryMapping directoryMapping, String groupName) throws OperationFailedException {
        try {
            this.applicationDefaultGroupMembershipConfigurationDao.add(application, directoryMapping, groupName);
        }
        catch (ApplicationNotFoundException | DirectoryMappingNotFoundException e) {
            throw new OperationFailedException(e);
        }
    }

    public void remove(Application application, ApplicationDirectoryMapping directoryMapping, String groupName) throws OperationFailedException {
        try {
            this.applicationDefaultGroupMembershipConfigurationDao.remove(application, directoryMapping, groupName);
        }
        catch (ApplicationNotFoundException | DirectoryMappingNotFoundException e) {
            throw new OperationFailedException(e);
        }
    }

    public List<String> listAll(Application application, ApplicationDirectoryMapping directoryMapping) throws OperationFailedException {
        try {
            return ImmutableList.copyOf((Collection)this.applicationDefaultGroupMembershipConfigurationDao.listAll(application, directoryMapping).stream().map(ApplicationDefaultGroupMembershipConfiguration::getGroupName).collect(Collectors.toList()));
        }
        catch (ApplicationNotFoundException | DirectoryMappingNotFoundException e) {
            throw new OperationFailedException(e);
        }
    }
}

