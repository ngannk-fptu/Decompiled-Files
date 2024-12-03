/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.delegation;

import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.atlassian.user.impl.DuplicateEntityException;
import com.atlassian.user.impl.delegation.repository.DelegatingRepository;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.PagerFactory;
import com.atlassian.user.security.password.Credential;
import com.atlassian.user.util.Assert;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DelegatingUserManager
implements UserManager {
    private final List<UserManager> userManagers;

    public DelegatingUserManager(List<UserManager> userManagers) {
        this.userManagers = userManagers;
    }

    @Override
    public Pager<User> getUsers() throws EntityException {
        ArrayList pagers = new ArrayList();
        for (UserManager userManager : this.userManagers) {
            pagers.add(userManager.getUsers());
        }
        return PagerFactory.getPager(pagers);
    }

    @Override
    public Pager<String> getUserNames() throws EntityException {
        ArrayList pagers = new ArrayList();
        for (UserManager userManager : this.userManagers) {
            pagers.add(userManager.getUserNames());
        }
        return PagerFactory.getPager(pagers);
    }

    @Override
    public User getUser(String username) throws EntityException {
        UserManager userManager;
        User foundUser = null;
        Iterator<UserManager> i$ = this.userManagers.iterator();
        while (i$.hasNext() && (foundUser = (userManager = i$.next()).getUser(username)) == null) {
        }
        return foundUser;
    }

    @Override
    public User createUser(String username) throws EntityException {
        User preexistingUser;
        try {
            preexistingUser = this.getUser(username);
        }
        catch (EntityException e) {
            throw new EntityException("Couldn't check whether user already exists", e);
        }
        if (preexistingUser != null) {
            UserManager manager = this.getMatchingUserManager(preexistingUser);
            if (manager == null) {
                throw new DuplicateEntityException("User [" + username + "] reported to exist by unknown manager");
            }
            RepositoryIdentifier repository = manager.getRepository(preexistingUser);
            if (repository == null) {
                throw new DuplicateEntityException("User [" + username + "] reported to exist in unknown repository by manager: " + manager);
            }
            throw new DuplicateEntityException("User [" + username + "] already exists in: " + repository.getName());
        }
        User createdUser = null;
        for (UserManager userManager : this.userManagers) {
            if (userManager.isCreative()) {
                createdUser = userManager.createUser(username);
            }
            if (createdUser == null) continue;
            break;
        }
        if (createdUser == null) {
            throw new EntityException("Could not create user: " + username + ". " + "Ensure you have a read-write repository configured.");
        }
        return createdUser;
    }

    @Override
    public User createUser(User userTemplate, Credential credential) throws EntityException, UnsupportedOperationException {
        User preexistingUser;
        String username = userTemplate.getName();
        try {
            preexistingUser = this.getUser(username);
        }
        catch (EntityException e) {
            throw new EntityException("Couldn't check whether user already exists", e);
        }
        if (preexistingUser != null) {
            UserManager manager = this.getMatchingUserManager(preexistingUser);
            if (manager == null) {
                throw new DuplicateEntityException("User [" + username + "] reported to exist by unknown manager");
            }
            RepositoryIdentifier repository = manager.getRepository(preexistingUser);
            if (repository == null) {
                throw new DuplicateEntityException("User [" + username + "] reported to exist in unknown repository by manager: " + manager);
            }
            throw new DuplicateEntityException("User [" + username + "] already exists in: " + repository.getName());
        }
        User createdUser = null;
        for (UserManager userManager : this.userManagers) {
            if (userManager.isCreative()) {
                createdUser = userManager.createUser(userTemplate, credential);
            }
            if (createdUser == null) continue;
            break;
        }
        if (createdUser == null) {
            throw new EntityException("Could not create user: " + username + ". " + "Ensure you have a read-write repository configured.");
        }
        return createdUser;
    }

    @Override
    public void alterPassword(User user, String plainTextPass) throws EntityException {
        UserManager userManager = this.getMatchingUserManager(user);
        if (userManager == null) {
            throw new EntityException("Cannot find a userManager responsible for user [" + user.getName() + "]");
        }
        userManager.alterPassword(user, plainTextPass);
    }

    @Override
    public void saveUser(User user) throws EntityException {
        UserManager userManager = this.getMatchingUserManager(user);
        if (userManager == null) {
            throw new EntityException("Cannot find a userManager responsible for user [" + user.getName() + "]");
        }
        userManager.saveUser(user);
    }

    @Override
    public void removeUser(User user) throws EntityException {
        UserManager userManager = this.getMatchingUserManager(user);
        if (userManager == null) {
            throw new IllegalArgumentException("Cannot find a userManager responsible for user [" + user.getName() + "]");
        }
        userManager.removeUser(user);
    }

    @Override
    public boolean isReadOnly(User user) throws EntityException {
        UserManager userManager = this.getMatchingUserManager(user);
        if (userManager != null) {
            return userManager.isReadOnly(user);
        }
        throw new EntityException("Cannot find a userManager responsible for user [" + user.getName() + "]");
    }

    @Override
    public RepositoryIdentifier getIdentifier() {
        Iterator<UserManager> iter = this.userManagers.iterator();
        ArrayList<RepositoryIdentifier> repositories = new ArrayList<RepositoryIdentifier>();
        while (iter.hasNext()) {
            UserManager userManager = iter.next();
            repositories.add(userManager.getIdentifier());
        }
        return new DelegatingRepository(repositories);
    }

    @Override
    public RepositoryIdentifier getRepository(Entity entity) throws EntityException {
        for (UserManager userManager : this.userManagers) {
            RepositoryIdentifier repo = userManager.getRepository(entity);
            if (repo == null) continue;
            return repo;
        }
        return null;
    }

    @Override
    public boolean isCreative() {
        for (UserManager userManager : this.userManagers) {
            if (!userManager.isCreative()) continue;
            return true;
        }
        return false;
    }

    protected UserManager getMatchingUserManager(User user) throws EntityException {
        Assert.notNull(user, "User must not be null");
        for (UserManager userManager : this.userManagers) {
            User foundUser = userManager.getUser(user.getName());
            if (foundUser == null) continue;
            return userManager;
        }
        return null;
    }

    public List getUserManagers() {
        return Collections.unmodifiableList(this.userManagers);
    }
}

