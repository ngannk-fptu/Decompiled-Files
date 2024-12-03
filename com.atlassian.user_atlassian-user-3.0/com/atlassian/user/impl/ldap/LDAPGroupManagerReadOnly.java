/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.impl.ldap;

import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.user.impl.ReadOnlyGroupManager;
import com.atlassian.user.impl.ldap.LDAPValidator;
import com.atlassian.user.impl.ldap.adaptor.LDAPGroupAdaptor;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.page.DefaultPager;
import com.atlassian.user.search.page.Pager;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LDAPGroupManagerReadOnly
extends ReadOnlyGroupManager {
    private final RepositoryIdentifier repositoryIdentifier;
    private final LDAPGroupAdaptor groupAdaptor;

    public LDAPGroupManagerReadOnly(RepositoryIdentifier repositoryIdentifier, LDAPGroupAdaptor groupAdaptor) {
        this.repositoryIdentifier = repositoryIdentifier;
        this.groupAdaptor = groupAdaptor;
    }

    @Override
    public RepositoryIdentifier getIdentifier() {
        return this.repositoryIdentifier;
    }

    @Override
    public RepositoryIdentifier getRepository(Entity entity) throws EntityException {
        if (this.getGroup(entity.getName()) == null) {
            return null;
        }
        return this.repositoryIdentifier;
    }

    @Override
    public Pager<Group> getGroups() throws EntityException {
        return this.groupAdaptor.getGroups();
    }

    @Override
    public Group getGroup(String groupName) throws EntityException {
        return this.groupAdaptor.getGroup(groupName);
    }

    @Override
    public Pager<Group> getGroups(User user) throws EntityException {
        if (!LDAPValidator.validateLDAPEntity(user)) {
            return DefaultPager.emptyPager();
        }
        return this.groupAdaptor.getGroups(user);
    }

    @Override
    public Pager<String> getMemberNames(Group group) throws EntityException {
        if (!LDAPValidator.validateLDAPEntity(group)) {
            return DefaultPager.emptyPager();
        }
        return this.groupAdaptor.findMemberNames(group);
    }

    @Override
    public Pager<String> getLocalMemberNames(Group group) throws EntityException {
        if (!LDAPValidator.validateLDAPEntity(group)) {
            return DefaultPager.emptyPager();
        }
        return this.groupAdaptor.findMembers(group);
    }

    @Override
    public Pager<String> getExternalMemberNames(Group group) throws EntityException {
        throw new UnsupportedOperationException("External membership is not supported.");
    }

    @Override
    public boolean hasMembership(Group group, User user) throws EntityException {
        return this.groupAdaptor.hasMembership(group, user);
    }

    @Override
    public boolean supportsExternalMembership() throws EntityException {
        return false;
    }
}

