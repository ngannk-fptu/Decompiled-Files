/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.runtime.OperationFailedException
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.UserQuery
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.diagnostics.ipd.userdirectory.service;

import com.atlassian.confluence.internal.diagnostics.ipd.userdirectory.service.UserDirectoryConnectionService;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.UserQuery;
import com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDirectoryConnectionServiceImpl
implements UserDirectoryConnectionService {
    private static final Logger LOG = LoggerFactory.getLogger(UserDirectoryConnectionServiceImpl.class);
    private final CrowdDirectoryService crowdDirectoryService;
    private final DirectoryInstanceLoader directoryInstanceLoader;
    private final Clock clock;

    public UserDirectoryConnectionServiceImpl(CrowdDirectoryService crowdDirectoryService, DirectoryInstanceLoader directoryInstanceLoader, Clock clock) {
        this.crowdDirectoryService = crowdDirectoryService;
        this.directoryInstanceLoader = directoryInstanceLoader;
        this.clock = clock;
    }

    @Override
    public boolean getConnectionState(Directory directory) {
        try {
            this.crowdDirectoryService.testConnection(directory);
            return true;
        }
        catch (com.atlassian.crowd.exception.runtime.OperationFailedException e) {
            LOG.debug("Failed to establish connection for directory: {}", (Object)directory.getName(), (Object)e);
            return false;
        }
    }

    @Override
    public Optional<Duration> getLatency(Directory directory) {
        try {
            RemoteDirectory rawDirectory = this.directoryInstanceLoader.getRawDirectory(directory.getId(), directory.getImplementationClass(), directory.getAttributes());
            Instant startTime = this.clock.instant();
            rawDirectory.searchUsers((EntityQuery)new UserQuery(User.class, (SearchRestriction)NullRestrictionImpl.INSTANCE, 0, 1));
            Instant endTime = this.clock.instant();
            return Optional.of(Duration.between(startTime, endTime));
        }
        catch (DirectoryInstantiationException e) {
            LOG.debug("Failed to load implementation of directory: {}", (Object)directory.getName(), (Object)e);
            return Optional.empty();
        }
        catch (OperationFailedException e) {
            LOG.debug("Failed to establish connection for directory: {}", (Object)directory.getName(), (Object)e);
            return Optional.empty();
        }
    }

    @Override
    public Stream<Directory> findAllActiveExternalDirectories() {
        return this.crowdDirectoryService.findAllDirectories().stream().filter(Directory::isActive).filter(directory -> directory.getType() != DirectoryType.INTERNAL);
    }
}

