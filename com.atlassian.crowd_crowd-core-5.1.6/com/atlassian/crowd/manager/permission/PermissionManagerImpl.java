/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.dao.application.ApplicationDAO
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.event.application.ApplicationUpdatedEvent
 *  com.atlassian.crowd.event.directory.DirectoryUpdatedEvent
 *  com.atlassian.crowd.exception.ApplicationNotFoundException
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.manager.permission.PermissionManager
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ApplicationDirectoryMapping
 *  com.atlassian.crowd.model.application.DirectoryMapping
 *  com.atlassian.crowd.model.application.ImmutableApplication
 *  com.atlassian.crowd.model.directory.ImmutableDirectory
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.crowd.manager.permission;

import com.atlassian.crowd.dao.application.ApplicationDAO;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.event.application.ApplicationUpdatedEvent;
import com.atlassian.crowd.event.directory.DirectoryUpdatedEvent;
import com.atlassian.crowd.exception.ApplicationNotFoundException;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.manager.permission.PermissionManager;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ApplicationDirectoryMapping;
import com.atlassian.crowd.model.application.DirectoryMapping;
import com.atlassian.crowd.model.application.ImmutableApplication;
import com.atlassian.crowd.model.directory.ImmutableDirectory;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PermissionManagerImpl
implements PermissionManager {
    private static final Logger logger = LoggerFactory.getLogger(PermissionManagerImpl.class);
    private final ApplicationDAO applicationDao;
    private final DirectoryDao directoryDao;
    private final EventPublisher eventPublisher;

    public PermissionManagerImpl(ApplicationDAO applicationDao, DirectoryDao directoryDao, EventPublisher eventPublisher) {
        this.applicationDao = (ApplicationDAO)Preconditions.checkNotNull((Object)applicationDao);
        this.directoryDao = (DirectoryDao)Preconditions.checkNotNull((Object)directoryDao);
        this.eventPublisher = eventPublisher;
    }

    public boolean hasPermission(Directory directory, OperationType operationType) {
        Validate.notNull((Object)directory, (String)"directory cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)operationType, (String)"operationType cannot be null", (Object[])new Object[0]);
        boolean permission = directory.getAllowedOperations().contains(operationType);
        if (!permission && logger.isDebugEnabled()) {
            logger.debug("Directory " + directory.getName() + " : Permission " + operationType.name() + " has been denied");
        }
        return permission;
    }

    public boolean hasPermission(Application application, Directory directory, OperationType operationType) {
        ApplicationDirectoryMapping mapping;
        Validate.notNull((Object)application, (String)"application cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)directory, (String)"directory cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)operationType, (String)"operationType cannot be null", (Object[])new Object[0]);
        boolean hasPermission = false;
        if (this.hasPermission(directory, operationType) && (mapping = application.getApplicationDirectoryMapping(directory.getId().longValue())) != null) {
            hasPermission = mapping.getAllowedOperations().contains(operationType);
        }
        return hasPermission;
    }

    public void removePermission(Directory directory, OperationType operationType) throws DirectoryNotFoundException {
        Validate.notNull((Object)directory, (String)"directory cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)operationType, (String)"operationType cannot be null", (Object[])new Object[0]);
        ImmutableDirectory oldDirectory = ImmutableDirectory.from((Directory)this.directoryDao.findById(directory.getId().longValue()));
        directory.getAllowedOperations().remove(operationType);
        this.directoryDao.update(directory);
        ImmutableDirectory newDirectory = ImmutableDirectory.from((Directory)this.directoryDao.findById(directory.getId().longValue()));
        this.eventPublisher.publish((Object)new DirectoryUpdatedEvent((Object)this, (Directory)oldDirectory, (Directory)newDirectory));
    }

    public void removePermission(Application application, Directory directory, OperationType operationType) throws ApplicationNotFoundException {
        Validate.notNull((Object)application, (String)"application cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)directory, (String)"directory cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)operationType, (String)"operationType cannot be null", (Object[])new Object[0]);
        ApplicationDirectoryMapping mapping = application.getApplicationDirectoryMapping(directory.getId().longValue());
        if (mapping != null) {
            ImmutableApplication oldApplication = ImmutableApplication.from((Application)this.applicationDao.findById(application.getId().longValue()));
            mapping.getAllowedOperations().remove(operationType);
            this.applicationDao.update(application);
            ImmutableApplication newApplication = ImmutableApplication.from((Application)this.applicationDao.findById(application.getId().longValue()));
            this.eventPublisher.publish((Object)new ApplicationUpdatedEvent((Application)oldApplication, (Application)newApplication));
        }
    }

    public void addPermission(Directory directory, OperationType operationType) throws DirectoryNotFoundException {
        Validate.notNull((Object)directory, (String)"directory cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)operationType, (String)"operationType cannot be null", (Object[])new Object[0]);
        ImmutableDirectory oldDirectory = ImmutableDirectory.from((Directory)this.directoryDao.findById(directory.getId().longValue()));
        directory.getAllowedOperations().add(operationType);
        this.directoryDao.update(directory);
        ImmutableDirectory newDirectory = ImmutableDirectory.from((Directory)this.directoryDao.findById(directory.getId().longValue()));
        this.eventPublisher.publish((Object)new DirectoryUpdatedEvent((Object)this, (Directory)oldDirectory, (Directory)newDirectory));
    }

    public void addPermission(Application application, Directory directory, OperationType operationType) throws ApplicationNotFoundException {
        Validate.notNull((Object)application, (String)"application cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)directory, (String)"directory cannot be null", (Object[])new Object[0]);
        Validate.notNull((Object)operationType, (String)"operationType cannot be null", (Object[])new Object[0]);
        DirectoryMapping mapping = application.getDirectoryMapping(directory.getId().longValue());
        if (mapping != null) {
            ImmutableApplication oldApplication = ImmutableApplication.from((Application)this.applicationDao.findById(application.getId().longValue()));
            mapping.getAllowedOperations().add(operationType);
            this.applicationDao.update(application);
            ImmutableApplication newApplication = ImmutableApplication.from((Application)this.applicationDao.findById(application.getId().longValue()));
            this.eventPublisher.publish((Object)new ApplicationUpdatedEvent((Application)oldApplication, (Application)newApplication));
        }
    }
}

