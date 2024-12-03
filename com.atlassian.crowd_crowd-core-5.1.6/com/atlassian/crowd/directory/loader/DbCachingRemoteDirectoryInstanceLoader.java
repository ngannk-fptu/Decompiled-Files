/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.CachingDirectory
 *  com.atlassian.crowd.directory.InternalRemoteDirectory
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.ldap.cache.CacheRefresherFactory
 *  com.atlassian.crowd.directory.ldap.cache.DirectoryCacheFactory
 *  com.atlassian.crowd.directory.loader.AbstractDirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.InternalDirectoryInstanceLoader
 *  com.atlassian.crowd.directory.loader.InternalHybridDirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 *  com.atlassian.crowd.manager.audit.AuditService
 *  com.atlassian.crowd.manager.audit.mapper.AuditLogGroupMapper
 *  com.atlassian.crowd.manager.audit.mapper.AuditLogUserMapper
 *  com.atlassian.crowd.model.directory.DirectoryImpl
 *  com.atlassian.crowd.util.persistence.hibernate.batch.BatchConfigParser
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.loader;

import com.atlassian.crowd.directory.CachingDirectory;
import com.atlassian.crowd.directory.DbCachingRemoteDirectory;
import com.atlassian.crowd.directory.InternalRemoteDirectory;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.ldap.cache.CacheRefresherFactory;
import com.atlassian.crowd.directory.ldap.cache.DirectoryCacheFactory;
import com.atlassian.crowd.directory.loader.AbstractDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.InternalDirectoryInstanceLoader;
import com.atlassian.crowd.directory.loader.InternalHybridDirectoryInstanceLoader;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import com.atlassian.crowd.manager.audit.AuditService;
import com.atlassian.crowd.manager.audit.mapper.AuditLogGroupMapper;
import com.atlassian.crowd.manager.audit.mapper.AuditLogUserMapper;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import com.atlassian.crowd.util.persistence.hibernate.batch.BatchConfigParser;
import com.atlassian.event.api.EventPublisher;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbCachingRemoteDirectoryInstanceLoader
extends AbstractDirectoryInstanceLoader
implements InternalHybridDirectoryInstanceLoader {
    private static final Logger log = LoggerFactory.getLogger(DbCachingRemoteDirectoryInstanceLoader.class);
    private final InternalDirectoryInstanceLoader internalDirectoryInstanceLoader;
    private final DirectoryInstanceLoader remoteDirectoryInstanceLoader;
    private final DirectoryCacheFactory directoryCacheFactory;
    private final CacheRefresherFactory cacheRefresherFactory;
    protected final AuditService auditService;
    protected final AuditLogUserMapper auditLogUserMapper;
    protected final AuditLogGroupMapper auditLogGroupMapper;
    private final EventPublisher eventPublisher;
    private final DirectoryDao directoryDao;

    public DbCachingRemoteDirectoryInstanceLoader(DirectoryInstanceLoader remoteDirectoryInstanceLoader, InternalDirectoryInstanceLoader internalDirectoryInstanceLoader, DirectoryCacheFactory directoryCacheFactory, CacheRefresherFactory cacheRefresherFactory, AuditService auditService, AuditLogUserMapper auditLogUserMapper, AuditLogGroupMapper auditLogGroupMapper, EventPublisher eventPublisher, DirectoryDao directoryDao) {
        this.remoteDirectoryInstanceLoader = remoteDirectoryInstanceLoader;
        this.internalDirectoryInstanceLoader = internalDirectoryInstanceLoader;
        this.directoryCacheFactory = directoryCacheFactory;
        this.cacheRefresherFactory = cacheRefresherFactory;
        this.auditService = auditService;
        this.auditLogUserMapper = auditLogUserMapper;
        this.auditLogGroupMapper = auditLogGroupMapper;
        this.eventPublisher = eventPublisher;
        this.directoryDao = directoryDao;
    }

    public RemoteDirectory getDirectory(Directory directory) throws DirectoryInstantiationException {
        RemoteDirectory remoteDirectory = this.getRawDirectory(directory.getId(), directory.getImplementationClass(), directory.getAttributes());
        InternalRemoteDirectory internalDirectory = this.getRawInternalDirectory(directory);
        return new DbCachingRemoteDirectory(remoteDirectory, internalDirectory, this.directoryCacheFactory, this.cacheRefresherFactory, this.auditService, this.auditLogUserMapper, directory.getName(), this.eventPublisher, this.directoryDao, new BatchConfigParser());
    }

    private InternalRemoteDirectory getRawInternalDirectory(Directory directory) throws DirectoryInstantiationException {
        DirectoryImpl internal = new DirectoryImpl(directory);
        HashMap<String, String> newAttributes = new HashMap<String, String>(internal.getAttributes());
        newAttributes.put("user_encryption_method", "atlassian-security");
        internal.setAttributes(newAttributes);
        return this.internalDirectoryInstanceLoader.getRawDirectory(directory.getId(), CachingDirectory.class.getName(), newAttributes);
    }

    public RemoteDirectory getRawDirectory(Long id, String className, Map<String, String> attributes) throws DirectoryInstantiationException {
        return this.remoteDirectoryInstanceLoader.getRawDirectory(id, className, attributes);
    }

    public boolean canLoad(String className) {
        return this.remoteDirectoryInstanceLoader.canLoad(className);
    }
}

