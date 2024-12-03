/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.seraph.auth.AuthenticationContext
 *  com.atlassian.seraph.auth.AuthenticationContextImpl
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.ObjectDeletedException
 *  org.hibernate.SessionFactory
 *  org.hibernate.query.Query
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.support.DataAccessUtils
 *  org.springframework.orm.hibernate5.HibernateTemplate
 */
package com.atlassian.confluence.user.persistence.dao.hibernate;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.seraph.auth.AuthenticationContext;
import com.atlassian.seraph.auth.AuthenticationContextImpl;
import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.ObjectDeletedException;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

@ParametersAreNonnullByDefault
public class HibernateConfluenceUserDao
implements ConfluenceUserDao {
    private static final AuthenticationContext authenticationContext = new AuthenticationContextImpl();
    private final HibernateTemplate hibernateTemplate;

    public HibernateConfluenceUserDao(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @Override
    public void create(ConfluenceUser user) {
        this.hibernateTemplate.save((Object)user);
    }

    @Override
    public void update(ConfluenceUser user) {
        this.hibernateTemplate.update((Object)user);
    }

    @Override
    public @Nullable ConfluenceUser findByKey(@Nullable UserKey key) {
        if (key == null) {
            return null;
        }
        try {
            return (ConfluenceUser)this.hibernateTemplate.get(ConfluenceUserImpl.class, (Serializable)key);
        }
        catch (DataAccessException ex) {
            if (ex.getCause() instanceof ObjectDeletedException) {
                return null;
            }
            throw ex;
        }
    }

    @Override
    public @Nullable ConfluenceUser findByUsername(String username) {
        if (StringUtils.isBlank((CharSequence)username)) {
            return null;
        }
        ConfluenceUser user = (ConfluenceUser)this.hibernateTemplate.execute(session -> {
            Principal authenticatedUser = authenticationContext.getUser();
            if (authenticatedUser instanceof ConfluenceUser && username.equals(authenticatedUser.getName()) && session.contains((Object)authenticatedUser)) {
                return (ConfluenceUser)((Object)authenticatedUser);
            }
            Query queryObject = session.createNamedQuery("confluence.confluenceuser_findByLowerUserName", ConfluenceUser.class);
            queryObject.setParameter("lowerUsername", (Object)IdentifierUtils.toLowerCase((String)username));
            return (ConfluenceUser)queryObject.uniqueResult();
        });
        if (user != null) {
            this.hibernateTemplate.initialize((Object)user);
        }
        return user;
    }

    @Override
    public Set<ConfluenceUser> getAll() {
        List allUsers = this.hibernateTemplate.loadAll(ConfluenceUserImpl.class);
        return new HashSet<ConfluenceUser>(allUsers);
    }

    @Override
    public Map<String, UserKey> findUserKeysByLowerNames(Iterable<String> names) {
        return this.findConfluenceUsersByLowerNames(names).stream().collect(Collectors.toMap(user -> user.getLowerName(), user -> user.getKey()));
    }

    @Override
    public List<ConfluenceUser> findConfluenceUsersByLowerNames(Iterable<String> lowerNames) {
        if (!lowerNames.iterator().hasNext()) {
            return Collections.emptyList();
        }
        Collection params = StreamSupport.stream(lowerNames.spliterator(), false).collect(Collectors.toList());
        List users = Objects.requireNonNull((List)this.hibernateTemplate.execute(session -> {
            Query queryObject = session.getNamedQuery("confluence.confluenceuser_findByLowerNames");
            queryObject.setParameterList("lowerNames", params);
            return queryObject.list();
        }));
        return users.stream().map(user -> user).collect(Collectors.toList());
    }

    @Override
    public List<UserKey> getAllUserKeys() {
        List result = (List)this.hibernateTemplate.execute(session -> {
            List allUsers = this.hibernateTemplate.loadAll(ConfluenceUserImpl.class);
            return allUsers.stream().map(ConfluenceUserImpl::getKey).collect(Collectors.toList());
        });
        return result;
    }

    @Override
    public Map<UserKey, String> findLowerNamesByKeys(Iterable<UserKey> keys) {
        Collection params = StreamSupport.stream(keys.spliterator(), false).collect(Collectors.toList());
        Map result = (Map)this.hibernateTemplate.execute(session -> {
            Query queryObject = session.getNamedQuery("confluence.confluenceuser_findLowerNamesByKeys");
            queryObject.setParameterList("userKeys", params);
            List users = queryObject.list();
            return users.stream().collect(Collectors.toMap(ConfluenceUserImpl::getKey, ConfluenceUserImpl::getLowerName));
        });
        return result;
    }

    private Optional<ConfluenceUser> getUnsyncedUser(UserKey key) {
        return Optional.ofNullable((ConfluenceUser)this.hibernateTemplate.execute(session -> session.getNamedQuery("confluence.confluenceuser_getUnsyncedUser").setParameter("userKey", (Object)key).uniqueResult()));
    }

    private Optional<ConfluenceUser> getDeletedUser(UserKey key) {
        return Optional.ofNullable((ConfluenceUser)this.hibernateTemplate.execute(session -> session.getNamedQuery("confluence.confluenceuser_getDeletedUser").setParameter("userKey", (Object)key).setCacheable(true).uniqueResult()));
    }

    @Override
    public boolean isDeletedUser(ConfluenceUser user) {
        return this.getDeletedUser(user.getKey()).isPresent();
    }

    @Override
    public boolean isUnsyncedUser(ConfluenceUser user) {
        return this.getUnsyncedUser(user.getKey()).isPresent();
    }

    @Override
    public List<ConfluenceUser> searchUnsyncedUsers(String searchParam) {
        return (List)this.hibernateTemplate.execute(session -> {
            Query queryObject = session.getNamedQuery("confluence.confluenceuser_searchUnsyncedUsers");
            queryObject.setParameter("lowerUsername", (Object)"%".concat(GeneralUtil.specialToLowerCase(searchParam).concat("%")));
            List users = queryObject.list();
            return users;
        });
    }

    @Override
    public int countUnsyncedUsers() {
        return DataAccessUtils.intResult((Collection)((Collection)this.hibernateTemplate.execute(session -> {
            Query query = session.getNamedQuery("confluence.confluenceuser_countUnsyncedUsers");
            return query.list();
        })));
    }

    @Override
    public Map<UserKey, Optional<ConfluenceUser>> findByKeys(Set<UserKey> userkeys) {
        if (userkeys.isEmpty()) {
            return Collections.emptyMap();
        }
        Stream<ConfluenceUser> users = this.hibernateTemplate.findByNamedQueryAndNamedParam("confluence.confluenceuser_bulkFindByUserKeys", "userKeys", this.keysToStrings(userkeys)).stream().map(ConfluenceUser.class::cast);
        Map<UserKey, Optional<ConfluenceUser>> usersByKey = users.collect(Collectors.toMap(ConfluenceUser::getKey, Optional::of));
        userkeys.forEach(userKey -> usersByKey.putIfAbsent((UserKey)userKey, Optional.empty()));
        return usersByKey;
    }

    @Override
    public void remove(ConfluenceUser user) {
        this.hibernateTemplate.delete((Object)user);
    }

    @Override
    public ConfluenceUser rename(String oldUsername, String newUsername, boolean overrideExisting) {
        ConfluenceUser user = Objects.requireNonNull(this.findByUsername(oldUsername));
        return this.doRename(user, newUsername, overrideExisting);
    }

    @Override
    public ConfluenceUser rename(ConfluenceUser userToRename, String newUsername, boolean overrideExisting) {
        @NonNull ConfluenceUser user = Objects.requireNonNull(this.findByKey(userToRename.getKey()));
        return this.doRename(user, newUsername, overrideExisting);
    }

    private ConfluenceUser doRename(ConfluenceUser userToRename, String newUsername, boolean overrideExisting) {
        ConfluenceUserImpl user = (ConfluenceUserImpl)userToRename;
        String oldUsername = user.getName();
        user.setName(newUsername);
        if (!IdentifierUtils.equalsInLowerCase((String)oldUsername, (String)newUsername)) {
            this.updateLowerName(user, newUsername, overrideExisting);
        }
        this.hibernateTemplate.update((Object)user);
        this.hibernateTemplate.flush();
        return user;
    }

    @Override
    public void deactivateUser(String username) {
        ConfluenceUserImpl user = (ConfluenceUserImpl)this.findByUsername(username);
        if (user == null) {
            return;
        }
        user.setLowerName(null);
        this.hibernateTemplate.update((Object)user);
        this.hibernateTemplate.flush();
    }

    private void updateLowerName(ConfluenceUserImpl user, String newUsername, boolean overrideExisting) {
        if (overrideExisting) {
            this.deactivateUser(newUsername);
            user.setLowerName(IdentifierUtils.toLowerCase((String)newUsername));
        } else {
            user.setLowerName(null);
        }
    }

    private Set<String> keysToStrings(Set<UserKey> userkeys) {
        return userkeys.stream().map(UserKey::getStringValue).collect(Collectors.toSet());
    }
}

