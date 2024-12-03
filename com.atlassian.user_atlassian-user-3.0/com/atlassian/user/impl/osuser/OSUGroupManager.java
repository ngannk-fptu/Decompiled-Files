/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.user.Entity$Accessor
 *  com.opensymphony.user.Group
 *  com.opensymphony.user.ImmutableException
 *  com.opensymphony.user.ManagerAccessor
 *  com.opensymphony.user.provider.AccessProvider
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.impl.osuser;

import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import com.atlassian.user.impl.DuplicateEntityException;
import com.atlassian.user.impl.RepositoryException;
import com.atlassian.user.impl.osuser.OSUAccessor;
import com.atlassian.user.impl.osuser.OSUEntityManager;
import com.atlassian.user.impl.osuser.OSUGroup;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.search.EntityNameAlphaComparator;
import com.atlassian.user.search.page.DefaultPager;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.util.Assert;
import com.opensymphony.user.Entity;
import com.opensymphony.user.ImmutableException;
import com.opensymphony.user.ManagerAccessor;
import com.opensymphony.user.provider.AccessProvider;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class OSUGroupManager
extends OSUEntityManager
implements GroupManager {
    protected final Logger log = Logger.getLogger(this.getClass());
    private final OSUAccessor osuserAccessor;
    private final GenericAccessProviderWrapper accessProvider;

    public OSUGroupManager(RepositoryIdentifier repository, OSUAccessor accessor) {
        super(repository);
        this.osuserAccessor = accessor;
        this.accessProvider = new GenericAccessProviderWrapper(accessor.getAccessProvider());
    }

    @Override
    public Pager<Group> getGroups() {
        SortedSet<Group> atlassianGroups = this.getGroupsFromAccessProvider();
        return new DefaultPager<Group>(atlassianGroups);
    }

    private SortedSet<Group> getGroupsFromAccessProvider() {
        TreeSet<Entity> atlassianGroups = new TreeSet<Entity>(new EntityNameAlphaComparator());
        for (Object o : this.accessProvider.list()) {
            String groupName = (String)o;
            Group atlassianGroup = this.getGroup(groupName);
            if (atlassianGroup == null) continue;
            atlassianGroups.add(atlassianGroup);
        }
        return atlassianGroups;
    }

    @Override
    public Group createGroup(String groupName) throws EntityException {
        OSUGroup group = null;
        if (this.accessProvider.handles(groupName)) {
            throw new DuplicateEntityException("Group named [" + groupName + "] already exists in accessProvider [" + this.accessProvider.toString());
        }
        if (this.accessProvider.create(groupName)) {
            group = new OSUGroup(new com.opensymphony.user.Group(groupName, (ManagerAccessor)this.osuserAccessor));
        }
        return group;
    }

    @Override
    public void removeGroup(Group group) throws EntityException, IllegalArgumentException {
        if (group == null) {
            throw new IllegalArgumentException("Group is null.");
        }
        if (!(group instanceof OSUGroup)) {
            throw new IllegalArgumentException("User is not a OSUGroup [" + group.getClass().getName());
        }
        Group groupToRemove = this.getGroup(group.getName());
        List<String> users = this.accessProvider.listUsersInGroup(groupToRemove.getName());
        users = new ArrayList<String>(users);
        for (String username : users) {
            this.accessProvider.removeFromGroup(username, groupToRemove.getName());
        }
        this.accessProvider.remove(group.getName());
    }

    @Override
    public void addMembership(Group group, User user) {
        if (group == null || this.getGroup(group.getName()) == null) {
            throw new IllegalArgumentException("Cannot add membership for unknown group: [" + (group == null ? "null" : group.getName()) + "]");
        }
        this.accessProvider.addToGroup(user.getName(), group.getName());
    }

    @Override
    public boolean hasMembership(Group group, User user) {
        if (!(group instanceof OSUGroup)) {
            return false;
        }
        return this.accessProvider.inGroup(user.getName(), group.getName());
    }

    @Override
    public void removeMembership(Group group, User user) {
        if (group == null || this.getGroup(group.getName()) == null) {
            throw new IllegalArgumentException("Can't remove membership for unknown group: [" + (group == null ? "null" : group.getName()) + "]");
        }
        this.accessProvider.removeFromGroup(user.getName(), group.getName());
    }

    @Override
    public boolean isReadOnly(Group group) throws EntityException {
        return !this.accessProvider.handles(group.getName());
    }

    @Override
    public boolean supportsExternalMembership() throws EntityException {
        return false;
    }

    @Override
    public Pager<String> getMemberNames(Group group) throws EntityException {
        if (!(group instanceof OSUGroup)) {
            return DefaultPager.emptyPager();
        }
        ArrayList<String> memberNames = new ArrayList<String>(this.accessProvider.listUsersInGroup(group.getName()));
        memberNames.removeAll(Arrays.asList(new Object[]{null}));
        Collections.sort(memberNames, Collator.getInstance());
        return new DefaultPager<String>(memberNames);
    }

    @Override
    public Pager<String> getLocalMemberNames(Group group) throws EntityException {
        if (!(group instanceof OSUGroup)) {
            return DefaultPager.emptyPager();
        }
        ArrayList<String> memberNames = new ArrayList<String>(this.accessProvider.listUsersInGroup(group.getName()));
        Collections.sort(memberNames, Collator.getInstance());
        return new DefaultPager<String>(memberNames);
    }

    @Override
    public Pager<String> getExternalMemberNames(Group group) throws EntityException {
        throw new UnsupportedOperationException("External membership is not supported.");
    }

    public void saveGroup(Group group) throws EntityException {
        if (!this.accessProvider.handles(group.getName())) {
            return;
        }
        com.opensymphony.user.Group g = new com.opensymphony.user.Group(group.getName(), (ManagerAccessor)this.osuserAccessor);
        try {
            g.store();
        }
        catch (ImmutableException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Group getGroup(String groupName) {
        if (!this.accessProvider.handles(groupName)) {
            return null;
        }
        com.opensymphony.user.Group osgroup = new com.opensymphony.user.Group(groupName, (ManagerAccessor)this.osuserAccessor);
        return new OSUGroup(osgroup);
    }

    @Override
    public Pager<Group> getGroups(User user) throws RepositoryException {
        Assert.notNull(user, "User must not be null");
        if (!this.osuserAccessor.getCredentialsProvider().handles(user.getName())) {
            return DefaultPager.emptyPager();
        }
        TreeSet<Entity> groups = new TreeSet<Entity>(new EntityNameAlphaComparator());
        List<String> groupNames = this.accessProvider.listGroupsContainingUser(user.getName());
        for (String groupName : groupNames) {
            groups.add(this.getGroup(groupName));
        }
        return new DefaultPager<Entity>(groups);
    }

    @Override
    public List<Group> getWritableGroups() {
        return new ArrayList<Group>(this.getGroupsFromAccessProvider());
    }

    @Override
    public RepositoryIdentifier getIdentifier() {
        return this.repository;
    }

    @Override
    public RepositoryIdentifier getRepository(Entity entity) throws EntityException {
        if (this.getGroup(entity.getName()) != null) {
            return this.repository;
        }
        return null;
    }

    @Override
    public boolean isCreative() {
        List groupNames = this.accessProvider.list();
        if (groupNames.isEmpty()) {
            return true;
        }
        String groupName = (String)groupNames.get(0);
        return new com.opensymphony.user.Group(groupName, (ManagerAccessor)this.osuserAccessor).isMutable();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class GenericAccessProviderWrapper {
        private final AccessProvider provider;

        public GenericAccessProviderWrapper(AccessProvider provider) {
            this.provider = provider;
        }

        public boolean addToGroup(String s, String s1) {
            return this.provider.addToGroup(s, s1);
        }

        public boolean inGroup(String s, String s1) {
            return this.provider.inGroup(s, s1);
        }

        public List<String> listGroupsContainingUser(String s) {
            return this.provider.listGroupsContainingUser(s);
        }

        public List<String> listUsersInGroup(String s) {
            return this.provider.listUsersInGroup(s);
        }

        public boolean removeFromGroup(String s, String s1) {
            return this.provider.removeFromGroup(s, s1);
        }

        public boolean create(String s) {
            return this.provider.create(s);
        }

        public void flushCaches() {
            this.provider.flushCaches();
        }

        public boolean handles(String s) {
            return this.provider.handles(s);
        }

        public boolean init(Properties properties) {
            return this.provider.init(properties);
        }

        public List list() {
            return this.provider.list();
        }

        public boolean load(String s, Entity.Accessor accessor) {
            return this.provider.load(s, accessor);
        }

        public boolean remove(String s) {
            return this.provider.remove(s);
        }

        public boolean store(String s, Entity.Accessor accessor) {
            return this.provider.store(s, accessor);
        }
    }
}

