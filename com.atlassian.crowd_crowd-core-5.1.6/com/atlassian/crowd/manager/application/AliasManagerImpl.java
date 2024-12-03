/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.dao.alias.AliasDAO
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.manager.application.AliasAlreadyInUseException
 *  com.atlassian.crowd.manager.application.AliasAlreadyInUseRuntimeException
 *  com.atlassian.crowd.manager.application.AliasManager
 *  com.atlassian.crowd.manager.application.ApplicationManager
 *  com.atlassian.crowd.manager.application.ApplicationService
 *  com.atlassian.crowd.model.alias.Alias
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.event.AliasEvent
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.Validate
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.crowd.manager.application;

import com.atlassian.crowd.dao.alias.AliasDAO;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.application.AliasAlreadyInUseException;
import com.atlassian.crowd.manager.application.AliasAlreadyInUseRuntimeException;
import com.atlassian.crowd.manager.application.AliasManager;
import com.atlassian.crowd.manager.application.ApplicationManager;
import com.atlassian.crowd.manager.application.ApplicationService;
import com.atlassian.crowd.model.alias.Alias;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.event.AliasEvent;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.event.api.EventPublisher;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AliasManagerImpl
implements AliasManager {
    private final AliasDAO aliasDAO;
    private final ApplicationManager applicationManager;
    private final ApplicationService applicationService;
    private final EventPublisher eventPublisher;

    public AliasManagerImpl(AliasDAO aliasDAO, ApplicationManager applicationManager, ApplicationService applicationService, EventPublisher eventPublisher) {
        this.aliasDAO = aliasDAO;
        this.applicationManager = applicationManager;
        this.applicationService = applicationService;
        this.eventPublisher = eventPublisher;
    }

    public String findUsernameByAlias(Application application, String authenticatingUsername) {
        String realusername;
        Validate.notNull((Object)application);
        Validate.notNull((Object)authenticatingUsername);
        if (application.isAliasingEnabled() && (realusername = this.aliasDAO.findUsernameByAlias(application, authenticatingUsername)) != null) {
            return realusername;
        }
        return authenticatingUsername;
    }

    public String findAliasByUsername(Application application, String username) {
        String alias;
        Validate.notNull((Object)application);
        Validate.notNull((Object)username);
        if (application.isAliasingEnabled() && (alias = this.aliasDAO.findAliasByUsername(application, username)) != null) {
            return alias;
        }
        return username;
    }

    public void storeAlias(Application application, String username, String alias) throws AliasAlreadyInUseException {
        Validate.notNull((Object)username);
        Validate.notNull((Object)alias);
        String userWithAlias = this.aliasDAO.findUsernameByAlias(application, alias);
        if (userWithAlias != null) {
            if (IdentifierUtils.equalsInLowerCase((String)userWithAlias, (String)username)) {
                return;
            }
            throw new AliasAlreadyInUseException(application.getName(), alias, userWithAlias);
        }
        try {
            User user = this.applicationService.findUserByName(application, alias);
            String aliasForUserWithSameNameAsDesiredAlias = this.aliasDAO.findAliasByUsername(application, user.getName());
            if (aliasForUserWithSameNameAsDesiredAlias == null) {
                throw new AliasAlreadyInUseException(application.getName(), alias, user.getName());
            }
        }
        catch (UserNotFoundException user) {
            // empty catch block
        }
        String previousAlias = this.aliasDAO.findAliasByUsername(application, username);
        this.aliasDAO.storeAlias(application, username, alias);
        if (previousAlias == null) {
            this.eventPublisher.publish((Object)AliasEvent.created((long)application.getId(), (String)username, (String)alias));
        } else {
            this.eventPublisher.publish((Object)AliasEvent.updated((long)application.getId(), (String)username, (String)alias));
        }
    }

    public void removeAlias(Application application, String username) throws AliasAlreadyInUseException {
        String userWithUsernameAsAlias = this.aliasDAO.findUsernameByAlias(application, username);
        if (userWithUsernameAsAlias != null) {
            throw new AliasAlreadyInUseException(application.getName(), username, userWithUsernameAsAlias);
        }
        this.aliasDAO.removeAlias(application, username);
        this.eventPublisher.publish((Object)AliasEvent.deleted((String)username, (long)application.getId()));
    }

    public List<String> search(EntityQuery entityQuery) {
        return this.aliasDAO.search(entityQuery);
    }

    public void removeAliasesForUser(String username) {
        Validate.notNull((Object)username);
        List applications = this.applicationManager.findAll();
        for (Application app : applications) {
            String userWithGivenUsernameAsAlias = this.aliasDAO.findUsernameByAlias(app, username);
            if (userWithGivenUsernameAsAlias == null) continue;
            throw new AliasAlreadyInUseRuntimeException(app.getName(), username, userWithGivenUsernameAsAlias);
        }
        this.aliasDAO.removeAliasesForUser(username);
    }

    public Map<String, String> findAliasesByUsernames(Application application, Iterable<String> usernames) {
        if (application.isAliasingEnabled() && !Iterables.isEmpty(usernames)) {
            List aliasesForUsers = this.aliasDAO.findAliasesForUsers(application, usernames);
            return aliasesForUsers.stream().collect(Collectors.toMap(Alias::getName, Alias::getAlias));
        }
        return ImmutableMap.of();
    }

    public Map<String, String> findAllAliasesByUsernames(Application application) {
        return application.isAliasingEnabled() ? this.aliasDAO.findAllAliases(application) : ImmutableMap.of();
    }
}

