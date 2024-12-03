/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.ldap.cache.CacheRefresherFactory
 *  com.atlassian.crowd.directory.ldap.cache.DirectoryCacheFactory
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.InternalDirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 *  com.atlassian.crowd.manager.audit.AuditService
 *  com.atlassian.crowd.manager.audit.mapper.AuditLogGroupMapper
 *  com.atlassian.crowd.manager.audit.mapper.AuditLogUserMapper
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.crowd.directory.loader;

import com.atlassian.crowd.directory.AuditingDirectoryDecorator;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.ldap.cache.CacheRefresherFactory;
import com.atlassian.crowd.directory.ldap.cache.DirectoryCacheFactory;
import com.atlassian.crowd.directory.loader.DbCachingRemoteDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.InternalDirectoryInstanceLoader;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import com.atlassian.crowd.manager.audit.AuditService;
import com.atlassian.crowd.manager.audit.mapper.AuditLogGroupMapper;
import com.atlassian.crowd.manager.audit.mapper.AuditLogUserMapper;
import com.atlassian.event.api.EventPublisher;

public class ConfigurableDbCachingRemoteDirectoryInstanceLoader
extends DbCachingRemoteDirectoryInstanceLoader {
    public ConfigurableDbCachingRemoteDirectoryInstanceLoader(DirectoryInstanceLoader remoteDirectoryInstanceLoader, InternalDirectoryInstanceLoader internalDirectoryInstanceLoader, DirectoryCacheFactory directoryCacheFactory, CacheRefresherFactory cacheRefresherFactory, AuditService auditService, AuditLogUserMapper auditLogUserMapper, AuditLogGroupMapper auditLogGroupMapper, EventPublisher eventPublisher, DirectoryDao directoryDao) {
        super(remoteDirectoryInstanceLoader, internalDirectoryInstanceLoader, directoryCacheFactory, cacheRefresherFactory, auditService, auditLogUserMapper, auditLogGroupMapper, eventPublisher, directoryDao);
    }

    @Override
    public RemoteDirectory getDirectory(Directory directory) throws DirectoryInstantiationException {
        if (Boolean.parseBoolean((String)directory.getAttributes().get("com.atlassian.crowd.directory.sync.cache.enabled"))) {
            return super.getDirectory(directory);
        }
        RemoteDirectory uncachedDirectory = this.getRawDirectory(directory.getId(), directory.getImplementationClass(), directory.getAttributes());
        if (this.auditService.isEnabled()) {
            return new AuditingDirectoryDecorator(uncachedDirectory, this.auditService, this.auditLogUserMapper, this.auditLogGroupMapper, directory.getName());
        }
        return uncachedDirectory;
    }
}

