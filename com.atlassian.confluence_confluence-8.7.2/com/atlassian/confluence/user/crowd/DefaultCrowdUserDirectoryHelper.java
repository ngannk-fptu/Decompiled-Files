/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.manager.directory.SynchronisationMode
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.QueryBuilder$PartialEntityQuery
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.user.User
 *  com.google.common.base.Throwables
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.user.crowd;

import com.atlassian.confluence.user.crowd.CrowdUserDirectoryHelper;
import com.atlassian.confluence.user.crowd.CrowdUserDirectoryImplementation;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.manager.directory.SynchronisationMode;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.user.User;
import com.google.common.base.Throwables;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class DefaultCrowdUserDirectoryHelper
implements CrowdUserDirectoryHelper {
    private final Logger logger = LoggerFactory.getLogger(DefaultCrowdUserDirectoryHelper.class);
    private static final int MAX_COUNT = 5000;
    private final CrowdDirectoryService crowdDirectoryService;
    private final DirectoryManager directoryManager;
    private final SessionFactory sessionFactory;

    public DefaultCrowdUserDirectoryHelper(CrowdDirectoryService crowdDirectoryService, DirectoryManager directoryManager, SessionFactory sessionFactory) {
        this.directoryManager = Objects.requireNonNull(directoryManager);
        this.crowdDirectoryService = Objects.requireNonNull(crowdDirectoryService);
        this.sessionFactory = Objects.requireNonNull(sessionFactory);
    }

    @Override
    public Optional<SynchronisationMode> getSynchronisationMode(long directoryId) {
        try {
            return Optional.ofNullable(this.directoryManager.getSynchronisationMode(directoryId));
        }
        catch (DirectoryInstantiationException | DirectoryNotFoundException e) {
            this.logger.error("Unable to get synchronisation mode for directory [ {} ]", (Object)directoryId, (Object)e);
            return Optional.empty();
        }
    }

    @Override
    public CrowdUserDirectoryImplementation getUserDirectoryImplementation(long directoryId) {
        return CrowdUserDirectoryImplementation.getByImplementationClass(this.crowdDirectoryService.findDirectoryById(directoryId).getImplementationClass());
    }

    @Override
    public Optional<Integer> getUserCount(long directoryId) {
        return this.getCount(directoryId, EntityDescriptor.user(), (dir, query) -> {
            try {
                return this.directoryManager.searchUsers(dir.longValue(), query).size();
            }
            catch (DirectoryNotFoundException | OperationFailedException e) {
                throw Throwables.propagate((Throwable)e);
            }
        });
    }

    @Override
    public Optional<Integer> getGroupCount(long directoryId) {
        return this.getCount(directoryId, EntityDescriptor.group(), (dir, query) -> {
            try {
                return this.directoryManager.searchGroups(dir.longValue(), query).size();
            }
            catch (DirectoryNotFoundException | OperationFailedException e) {
                throw Throwables.propagate((Throwable)e);
            }
        });
    }

    @Override
    public Optional<Integer> getMembershipCount(long directoryId) {
        try {
            HibernateTemplate hibernateTemplate = new HibernateTemplate(this.sessionFactory);
            return Optional.of((Integer)hibernateTemplate.execute(session -> session.getNamedQuery("countMembershipsInDirectory").setParameter("directoryId", (Object)directoryId).uniqueResult()));
        }
        catch (RuntimeException e) {
            this.logger.error("Unable to get membership count for directory [ {} ]", (Object)directoryId, (Object)e);
            return Optional.empty();
        }
    }

    @Override
    public List<Directory> getDirectoriesForUser(User user) {
        return this.crowdDirectoryService.findAllDirectories().stream().filter(directory -> {
            try {
                return user != null && this.directoryManager.findUserByName(directory.getId().longValue(), user.getName()) != null;
            }
            catch (UserNotFoundException e) {
                return false;
            }
            catch (DirectoryNotFoundException | OperationFailedException e) {
                throw Throwables.propagate((Throwable)e);
            }
        }).collect(Collectors.toList());
    }

    private Optional<Integer> getCount(long directoryId, EntityDescriptor entity, BiFunction<Long, EntityQuery<?>, Integer> queryFunction) {
        try {
            int count = 0;
            int start = 0;
            long page = 5000L;
            while (page == 5000L) {
                QueryBuilder.PartialEntityQuery partialQuery = QueryBuilder.queryFor(String.class, (EntityDescriptor)entity);
                page = queryFunction.apply(directoryId, partialQuery.startingAt(start).returningAtMost(5000)).intValue();
                start = count = (int)((long)count + page);
            }
            return Optional.of(count);
        }
        catch (RuntimeException e) {
            this.logger.error("Unable to get {} count for directory [ {} ]", new Object[]{entity.getEntityType().toString(), directoryId, e});
            return Optional.empty();
        }
    }
}

