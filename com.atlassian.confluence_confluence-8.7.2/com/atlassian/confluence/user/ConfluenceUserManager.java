/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.Entity
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.atlassian.user.UserManager
 *  com.atlassian.user.impl.DefaultUser
 *  com.atlassian.user.repository.RepositoryIdentifier
 *  com.atlassian.user.search.page.Pager
 *  com.atlassian.user.security.password.Credential
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.event.events.security.ChangePasswordEvent;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.confluence.user.UserExistenceChecker;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.atlassian.user.impl.DefaultUser;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.security.password.Credential;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceUserManager
implements UserManager,
UserExistenceChecker {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceUserManager.class);
    private final UserManager delegate;
    private final ConfluenceUserDao dao;
    private final EventPublisher eventPublisher;

    public ConfluenceUserManager(UserManager delegate, ConfluenceUserDao dao, EventPublisher eventPublisher) {
        this.delegate = delegate;
        this.dao = dao;
        this.eventPublisher = eventPublisher;
    }

    public RepositoryIdentifier getIdentifier() {
        return this.delegate.getIdentifier();
    }

    public RepositoryIdentifier getRepository(Entity entity) throws EntityException {
        return this.delegate.getRepository(entity);
    }

    public boolean isCreative() {
        return this.delegate.isCreative();
    }

    public Pager<User> getUsers() throws EntityException {
        return this.delegate.getUsers();
    }

    public Pager<String> getUserNames() throws EntityException {
        return this.delegate.getUserNames();
    }

    public User getUser(String username) throws EntityException {
        User backingUser = this.delegate.getUser(username);
        if (backingUser == null) {
            return null;
        }
        ConfluenceUserImpl user = (ConfluenceUserImpl)this.dao.findByUsername(username);
        if (user != null) {
            user.setBackingUser(backingUser);
        }
        return user;
    }

    public User createUser(String username) throws EntityException {
        DefaultUser user = new DefaultUser(username);
        return this.createUser((User)user, Credential.NONE);
    }

    public User createUser(User userTemplate, Credential credential) throws EntityException, UnsupportedOperationException, IllegalArgumentException {
        this.delegate.createUser(userTemplate, credential);
        return this.dao.findByUsername(userTemplate.getName());
    }

    public void saveUser(User user) throws EntityException, IllegalArgumentException {
        ConfluenceUser confluenceUser = null;
        if (user instanceof ConfluenceUser) {
            confluenceUser = (ConfluenceUser)user;
        } else {
            confluenceUser = this.dao.findByUsername(user.getName());
            ConfluenceUserImpl updatedUser = Objects.requireNonNull((ConfluenceUserImpl)confluenceUser);
            updatedUser.setBackingUser(user);
        }
        this.dao.update(confluenceUser);
        this.delegate.saveUser(user);
    }

    public void removeUser(User user) throws EntityException, IllegalArgumentException {
        this.delegate.removeUser(user);
    }

    public void alterPassword(User user, String plainTextPass) throws EntityException {
        this.delegate.alterPassword(user, plainTextPass);
        this.eventPublisher.publish((Object)new ChangePasswordEvent(user));
    }

    public boolean isReadOnly(User user) throws EntityException {
        return this.delegate.isReadOnly(user);
    }

    @Override
    public boolean exists(String name) {
        try {
            if (name != null) {
                return this.delegate.getUser(name) != null;
            }
        }
        catch (EntityException e) {
            log.error("Error in getUser():" + e.getMessage(), (Throwable)e);
        }
        return false;
    }
}

