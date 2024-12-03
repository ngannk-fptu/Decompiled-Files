/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.event.user.UserAuthenticatedEvent
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.MembershipAlreadyExistsException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.OperationNotSupportedException
 *  com.atlassian.crowd.exception.ReadOnlyGroupException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.manager.directory.DirectoryPermissionException
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Preconditions
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.core.event.listener;

import com.atlassian.crowd.core.event.listener.DefaultGroupMembershipResolver;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.user.UserAuthenticatedEvent;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.MembershipAlreadyExistsException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.OperationNotSupportedException;
import com.atlassian.crowd.exception.ReadOnlyGroupException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.manager.directory.DirectoryPermissionException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoGroupAdderListener {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DirectoryManager directoryManager;
    private final List<DefaultGroupMembershipResolver> defaultMembershipResolvers;
    private final EventPublisher eventPublisher;

    public AutoGroupAdderListener(DirectoryManager directoryManager, List<DefaultGroupMembershipResolver> defaultMembershipResolvers, EventPublisher eventPublisher) {
        this.directoryManager = (DirectoryManager)Preconditions.checkNotNull((Object)directoryManager, (Object)"directoryManager");
        this.defaultMembershipResolvers = (List)Preconditions.checkNotNull(defaultMembershipResolvers);
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void register() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void unregister() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void handleEvent(UserAuthenticatedEvent event) {
        Directory directory = event.getDirectory();
        User user = event.getUser();
        Application application = event.getApplication();
        this.handleEvent(directory, user, application);
    }

    private void handleEvent(Directory directory, User user, Application application) {
        try {
            UserWithAttributes userWithAttributes = this.directoryManager.findUserWithAttributesByName(directory.getId().longValue(), user.getName());
            this.defaultMembershipResolvers.forEach(resolver -> {
                Collection<String> defaultGroupNames = resolver.getDefaultGroupNames(application, directory, userWithAttributes);
                if (!defaultGroupNames.isEmpty()) {
                    defaultGroupNames.forEach(group -> this.addUserToGroupSafely(directory, (User)userWithAttributes, (String)group, application));
                    try {
                        resolver.onDefaultGroupsAdded(application, directory, userWithAttributes);
                    }
                    catch (OperationFailedException e) {
                        this.logger.error("Could not call back resolver", (Throwable)e);
                        throw new RuntimeException(e);
                    }
                }
            });
        }
        catch (DirectoryInstantiationException e) {
            this.logger.error("Could not instantiate directory: {}", (Object)e.getMessage(), (Object)e);
        }
        catch (OperationFailedException e) {
            this.logger.error("Could not access directory: {}", (Object)e.getMessage(), (Object)e);
        }
        catch (UserNotFoundException e) {
            this.logger.error("Could not access user: {}", (Object)e.getMessage(), (Object)e);
        }
        catch (DirectoryNotFoundException e) {
            this.logger.error("Could not find directory {}", (Object)directory.getId(), (Object)e);
        }
    }

    private void addUserToGroupSafely(Directory directory, User user, String groupName, Application application) {
        try {
            this.directoryManager.addUserToGroup(directory.getId().longValue(), user.getName(), groupName);
        }
        catch (GroupNotFoundException e) {
            this.logger.warn("Could not auto add user '{}' to group '{}', because the group does not exist. User is authenticatingto directory '{}' from application '{}'", new Object[]{user.getName(), groupName, directory.getName(), application.getName()});
            this.logger.debug("Underlying exception", (Throwable)e);
        }
        catch (ReadOnlyGroupException e) {
            this.logger.error("Could not auto add user '{}' to group '{}', because the group is read only. User is authenticatingto directory '{}' from application '{}'", new Object[]{user.getName(), groupName, directory.getName(), application.getName()});
            this.logger.debug("Underlying exception", (Throwable)e);
        }
        catch (UserNotFoundException e) {
            this.logger.error("Could not auto add user to group: {}", (Object)e.getMessage(), (Object)e);
        }
        catch (OperationNotSupportedException e) {
            this.logger.warn("Could not add user to default groups because directory '{}' [{}] doesn't support such operation", (Object)directory.getName(), (Object)directory.getId());
            this.logger.debug("Underlying exception", (Throwable)e);
        }
        catch (OperationFailedException e) {
            this.logger.error("Could not access directory: {}", (Object)e.getMessage(), (Object)e);
        }
        catch (DirectoryNotFoundException e) {
            this.logger.error("Could not find directory {}", (Object)directory.getId(), (Object)e);
        }
        catch (DirectoryPermissionException e) {
            this.logger.error("You have group '{}' to be auto-added for the user '{}', but the directory does not have permission for Group updates.", (Object)groupName, (Object)user.getName());
        }
        catch (MembershipAlreadyExistsException e) {
            this.logger.debug("Could not auto add user to group because membership already exists", (Throwable)e);
        }
    }
}

