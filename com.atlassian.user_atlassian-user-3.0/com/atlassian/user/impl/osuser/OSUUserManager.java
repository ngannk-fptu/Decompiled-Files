/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.PropertySet
 *  com.opensymphony.user.ImmutableException
 *  com.opensymphony.user.ManagerAccessor
 *  com.opensymphony.user.User
 */
package com.atlassian.user.impl.osuser;

import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.atlassian.user.impl.DuplicateEntityException;
import com.atlassian.user.impl.RepositoryException;
import com.atlassian.user.impl.osuser.OSUAccessor;
import com.atlassian.user.impl.osuser.OSUEntityManager;
import com.atlassian.user.impl.osuser.OSUUser;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.EntityNameAlphaComparator;
import com.atlassian.user.search.page.DefaultPager;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.security.password.Credential;
import com.atlassian.user.util.Assert;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.ImmutableException;
import com.opensymphony.user.ManagerAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class OSUUserManager
extends OSUEntityManager
implements UserManager {
    private final OSUAccessor accessor;

    public OSUUserManager(RepositoryIdentifier repository, OSUAccessor accessor) {
        super(repository);
        this.accessor = accessor;
    }

    @Override
    public Pager<User> getUsers() {
        List<String> userNames = this.getUserNamesInternal();
        TreeSet<Entity> atlassianUsers = new TreeSet<Entity>(new EntityNameAlphaComparator());
        for (String username : userNames) {
            OSUUser user = this.getWrappedOSUUser(username);
            if (user == null) continue;
            atlassianUsers.add(user);
        }
        return new DefaultPager<Entity>(atlassianUsers);
    }

    @Override
    public Pager<String> getUserNames() throws EntityException {
        return new DefaultPager<String>(this.getUserNamesInternal());
    }

    @Override
    public User getUser(String username) {
        return this.getWrappedOSUUser(username);
    }

    private OSUUser getWrappedOSUUser(String username) {
        com.opensymphony.user.User opensymphonyUser = this.getOpenSymphonyUser(username);
        return opensymphonyUser == null ? null : new OSUUser(opensymphonyUser);
    }

    private com.opensymphony.user.User getOpenSymphonyUser(String username) {
        String lcUsername = username.toLowerCase();
        if (this.accessor.getCredentialsProvider().handles(lcUsername)) {
            return new com.opensymphony.user.User(username.toLowerCase(), (ManagerAccessor)this.accessor);
        }
        return null;
    }

    @Override
    public User createUser(String username) throws EntityException {
        this.validateNewUserName(username);
        if (this.accessor.getCredentialsProvider().create(username)) {
            this.accessor.getProfileProvider().create(username);
            return this.getWrappedOSUUser(username);
        }
        throw new EntityException("Was unable to create user [" + username + "] but the credentials provider [" + this.accessor.getCredentialsProvider().toString() + "] did not say why.");
    }

    @Override
    public User createUser(User userTemplate, Credential credential) throws EntityException, UnsupportedOperationException {
        String username = userTemplate.getName();
        this.validateNewUserName(username);
        if (credential.isEncrypted() && credential != Credential.NONE) {
            throw new IllegalArgumentException("OSUser passwords must not be encrypted");
        }
        boolean created = this.accessor.getCredentialsProvider().create(username);
        if (!created) {
            throw new RepositoryException("Couldn't create user [" + username + "] in credentials provider");
        }
        this.accessor.getProfileProvider().create(username);
        com.opensymphony.user.User user = new com.opensymphony.user.User(username.toLowerCase(), (ManagerAccessor)this.accessor);
        user.setFullName(userTemplate.getFullName());
        user.setEmail(userTemplate.getEmail());
        try {
            if (credential != Credential.NONE && credential.getValue() != null) {
                user.setPassword(credential.getValue());
            }
            user.store();
        }
        catch (ImmutableException e) {
            throw new RepositoryException(e);
        }
        return new OSUUser(user);
    }

    private void validateNewUserName(String username) throws DuplicateEntityException {
        boolean usernameHandled = this.accessor.getCredentialsProvider().handles(username);
        if (usernameHandled) {
            throw new DuplicateEntityException("User [" + username + "] already exists in credentialsProvider [" + this.accessor.getCredentialsProvider().toString() + "]");
        }
    }

    @Override
    public void alterPassword(User user, String plainTextPass) throws EntityException {
        if (!(user instanceof OSUUser)) {
            throw new EntityException("Unsupported user type: " + user);
        }
        OSUUser osUser = (OSUUser)user;
        osUser.setPassword(plainTextPass);
        this.saveUser(osUser);
    }

    @Override
    public void removeUser(User user) throws EntityException {
        Assert.notNull(user, "User must not be null");
        Assert.isInstanceOf(OSUUser.class, user);
        Assert.isTrue(this.getUser(user.getName()) != null, "User is not managed by this user manager: [" + user.getName() + "]");
        String userName = user.getName();
        PropertySet propertySet = this.accessor.getProfileProvider().getPropertySet(userName);
        for (Object o : propertySet.getKeys()) {
            String key = (String)o;
            propertySet.remove(key);
        }
        ArrayList<String> groupsOfUser = new ArrayList<String>(this.getGroupsContainingUserInternal(userName));
        for (String groupName : groupsOfUser) {
            this.accessor.getAccessProvider().removeFromGroup(userName, groupName);
        }
        boolean result = this.accessor.getCredentialsProvider().remove(userName);
        if (!result) {
            throw new EntityException("Could not remove user!");
        }
        this.accessor.getProfileProvider().remove(userName);
    }

    @Override
    public boolean isReadOnly(User user) {
        return this.getUser(user.getName()) == null;
    }

    @Override
    public void saveUser(User user) throws EntityException {
        Assert.notNull(user, "User must not be null");
        com.opensymphony.user.User osUser = this.getOpenSymphonyUser(user.getName());
        osUser.setFullName(user.getFullName());
        osUser.setEmail(user.getEmail());
        try {
            osUser.store();
        }
        catch (ImmutableException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public RepositoryIdentifier getRepository(Entity entity) throws EntityException {
        return this.getUser(entity.getName()) == null ? null : this.repository;
    }

    public OSUAccessor getAccessor() {
        return this.accessor;
    }

    private List<String> getUserNamesInternal() {
        return this.accessor.getCredentialsProvider().list();
    }

    private List<String> getGroupsContainingUserInternal(String userName) {
        return this.accessor.getAccessProvider().listGroupsContainingUser(userName);
    }
}

