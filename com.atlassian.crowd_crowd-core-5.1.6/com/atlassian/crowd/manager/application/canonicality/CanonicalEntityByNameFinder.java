/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.ObjectNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.exception.runtime.OperationFailedException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.search.Entity
 *  com.google.common.collect.ImmutableList
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.manager.application.canonicality;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.exception.runtime.OperationFailedException;
import com.atlassian.crowd.manager.application.filtering.AccessFilter;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.search.Entity;
import com.google.common.collect.ImmutableList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CanonicalEntityByNameFinder {
    private static final Logger logger = LoggerFactory.getLogger(CanonicalEntityByNameFinder.class);
    private static final String LOCATED_USER_IN_DIRECTORY_WITH_ACCESS_LOG = "Located user '{}' in directory {} '{}'";
    private static final String LOCATED_USER_IN_DIRECTORY_WITHOUT_ACCESS_LOG = "Located user '{}' in directory {} '{}', but null returned as the user does not have access to the application.";
    private final DirectoryManager directoryManager;
    private final List<Directory> directories;
    private final AccessFilter accessFilter;

    public CanonicalEntityByNameFinder(DirectoryManager directoryManager, Iterable<Directory> directories) {
        this(directoryManager, directories, AccessFilter.UNFILTERED);
    }

    public CanonicalEntityByNameFinder(DirectoryManager directoryManager, Iterable<Directory> directories, AccessFilter accessFilter) {
        this.directoryManager = directoryManager;
        this.directories = ImmutableList.copyOf(directories);
        this.accessFilter = accessFilter;
    }

    public Group fastFailingFindGroupByName(String name) throws GroupNotFoundException, com.atlassian.crowd.exception.OperationFailedException {
        return this.fastFailingFindOptionalGroupByName(name).orElseThrow(() -> new GroupNotFoundException(name));
    }

    public User fastFailingFindUserByName(String name) throws UserNotFoundException, com.atlassian.crowd.exception.OperationFailedException {
        return this.fastFailingFindOptionalUserByName(name).orElseThrow(() -> new UserNotFoundException(name));
    }

    public Optional<Group> fastFailingFindOptionalGroupByName(String name) throws com.atlassian.crowd.exception.OperationFailedException {
        return this.fastFailFindByName((arg_0, arg_1) -> ((DirectoryManager)this.directoryManager).findGroupByName(arg_0, arg_1), name);
    }

    public Optional<User> fastFailingFindOptionalUserByName(String name) throws com.atlassian.crowd.exception.OperationFailedException {
        return this.fastFailFindByName((arg_0, arg_1) -> ((DirectoryManager)this.directoryManager).findUserByName(arg_0, arg_1), name);
    }

    public Group findGroupByName(String name) throws GroupNotFoundException {
        return this.findByName((arg_0, arg_1) -> ((DirectoryManager)this.directoryManager).findGroupByName(arg_0, arg_1), name).orElseThrow(() -> new GroupNotFoundException(name));
    }

    public GroupWithAttributes findGroupWithAttributesByName(String name) throws GroupNotFoundException {
        return this.findByName((arg_0, arg_1) -> ((DirectoryManager)this.directoryManager).findGroupWithAttributesByName(arg_0, arg_1), name).orElseThrow(() -> new GroupNotFoundException(name));
    }

    public User findUserByName(String name) throws UserNotFoundException {
        return this.findByName((arg_0, arg_1) -> ((DirectoryManager)this.directoryManager).findUserByName(arg_0, arg_1), name).orElseThrow(() -> new UserNotFoundException(name));
    }

    public User findRemoteUserByName(String name) throws UserNotFoundException {
        return this.findByName((arg_0, arg_1) -> ((DirectoryManager)this.directoryManager).findRemoteUserByName(arg_0, arg_1), name).orElseThrow(() -> new UserNotFoundException(name));
    }

    public UserWithAttributes findUserWithAttributesByName(String name) throws UserNotFoundException {
        return this.findByName((arg_0, arg_1) -> ((DirectoryManager)this.directoryManager).findUserWithAttributesByName(arg_0, arg_1), name).orElseThrow(() -> new UserNotFoundException(name));
    }

    private <T extends DirectoryEntity> Optional<T> findByName(Searcher<T> searcher, String name) {
        try {
            return this.findByName(searcher, name, false);
        }
        catch (com.atlassian.crowd.exception.OperationFailedException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    private <T extends DirectoryEntity> Optional<T> fastFailFindByName(Searcher<T> searcher, String name) throws com.atlassian.crowd.exception.OperationFailedException {
        return this.findByName(searcher, name, true);
    }

    private <T extends DirectoryEntity> Optional<T> findByName(Searcher<T> searcher, String name, boolean failFast) throws com.atlassian.crowd.exception.OperationFailedException {
        for (Directory directory : this.directories) {
            try {
                T entity = searcher.findByName(directory.getId(), name);
                if (entity instanceof User) {
                    Optional userWithAccess = this.accessFilter.hasAccess(entity.getDirectoryId(), Entity.USER, entity.getName()) ? Optional.of(entity) : Optional.empty();
                    logger.trace(userWithAccess.isPresent() ? LOCATED_USER_IN_DIRECTORY_WITH_ACCESS_LOG : LOCATED_USER_IN_DIRECTORY_WITHOUT_ACCESS_LOG, new Object[]{entity.getName(), directory.getId(), directory.getName()});
                    return userWithAccess;
                }
                if (!this.accessFilter.hasAccess(entity.getDirectoryId(), Entity.GROUP, entity.getName())) continue;
                return Optional.of(entity);
            }
            catch (ObjectNotFoundException entity) {
            }
            catch (com.atlassian.crowd.exception.OperationFailedException e) {
                if (failFast) {
                    throw e;
                }
                logger.error(e.getMessage(), (Throwable)e);
            }
            catch (DirectoryNotFoundException e) {
                throw CanonicalEntityByNameFinder.concurrentModificationExceptionForDirectoryIteration(e);
            }
        }
        return Optional.empty();
    }

    private static ConcurrentModificationException concurrentModificationExceptionForDirectoryIteration(DirectoryNotFoundException e) {
        return new ConcurrentModificationException("Directory mapping was removed while iterating through directories", e);
    }

    private static interface Searcher<T extends DirectoryEntity> {
        public T findByName(long var1, String var3) throws DirectoryNotFoundException, com.atlassian.crowd.exception.OperationFailedException, ObjectNotFoundException;
    }
}

