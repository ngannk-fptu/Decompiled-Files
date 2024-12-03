/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.Supplier
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.impl.ImmutableAttributes
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.UserAlreadyExistsException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.user.DelegatingUserWithAttributes
 *  com.atlassian.crowd.model.user.InternalUser
 *  com.atlassian.crowd.model.user.TimestampedUser
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.TermRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.atlassian.crowd.util.BatchResult
 *  com.atlassian.event.api.EventPublisher
 *  io.atlassian.fugue.Option
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.Supplier;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.event.events.user.DirectoryUserRenamedEvent;
import com.atlassian.confluence.impl.cache.tx.TransactionAwareCache;
import com.atlassian.confluence.impl.cache.tx.TransactionAwareCacheFactory;
import com.atlassian.confluence.impl.content.render.prefetch.UserPrefetcher;
import com.atlassian.confluence.impl.user.crowd.CachedCrowdEntityCacheKey;
import com.atlassian.confluence.impl.user.crowd.CachedCrowdUser;
import com.atlassian.confluence.impl.user.crowd.CachedCrowdUserEmailSearchQuery;
import com.atlassian.confluence.impl.user.crowd.CrowdUserCache;
import com.atlassian.confluence.impl.user.crowd.hibernate.InternalUserDao;
import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.impl.ImmutableAttributes;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.UserAlreadyExistsException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.user.DelegatingUserWithAttributes;
import com.atlassian.crowd.model.user.InternalUser;
import com.atlassian.crowd.model.user.TimestampedUser;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.TermRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.atlassian.crowd.util.BatchResult;
import com.atlassian.event.api.EventPublisher;
import io.atlassian.fugue.Option;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class CachedCrowdUserDao
implements InternalUserDao<InternalUser>,
InitializingBean,
UserPrefetcher.PrefetchDao {
    private static final Logger log = LoggerFactory.getLogger(CachedCrowdUserDao.class);
    private final InternalUserDao<InternalUser> delegate;
    private final TransactionAwareCacheFactory txCacheFactory;
    private final CacheFactory nonTxCacheFactory;
    private final EventPublisher eventPublisher;

    public CachedCrowdUserDao(InternalUserDao<InternalUser> delegate, TransactionAwareCacheFactory txCacheFactory, CacheFactory nonTxCacheFactory, EventPublisher eventPublisher) {
        this.delegate = delegate;
        this.txCacheFactory = txCacheFactory;
        this.nonTxCacheFactory = nonTxCacheFactory;
        this.eventPublisher = eventPublisher;
    }

    public void afterPropertiesSet() throws Exception {
        this.getUserCache();
        this.getUserEmailCache();
        this.getUserAttributesCache();
    }

    private TransactionAwareCache<CachedCrowdEntityCacheKey, Option<TimestampedUser>> getUserCache() {
        return CoreCache.CROWD_USERS_BY_NAME.resolve(this.txCacheFactory::getTxCache);
    }

    private Option<TimestampedUser> findUserInternal(CachedCrowdEntityCacheKey key) {
        try {
            return Option.some((Object)new CachedCrowdUser(this.delegate.findByName(key.getDirectoryId(), key.getName())));
        }
        catch (UserNotFoundException e) {
            return Option.none();
        }
    }

    private List<TimestampedUser> toCachedCrowdUserList(List<TimestampedUser> users) {
        return Collections.unmodifiableList(users.stream().map(CachedCrowdUser::new).collect(Collectors.toList()));
    }

    private TransactionAwareCache<CachedCrowdUserEmailSearchQuery, List<TimestampedUser>> getUserEmailCache() {
        return CoreCache.CROWD_USERS_BY_EMAIL.resolve(this.txCacheFactory::getTxCache);
    }

    private List<TimestampedUser> searchByEmailInternal(CachedCrowdUserEmailSearchQuery key) {
        return this.toCachedCrowdUserList(this.delegate.search(key.getDirectoryId(), key.toEmailQuery()));
    }

    private TransactionAwareCache<CachedCrowdEntityCacheKey, Option<ImmutableAttributes>> getUserAttributesCache() {
        return CoreCache.CROWD_USER_ATTRIBUTES_BY_NAME.resolve(this.txCacheFactory::getTxCache);
    }

    private Option<ImmutableAttributes> findAttributesInternal(CachedCrowdEntityCacheKey key) {
        try {
            return Option.some((Object)new ImmutableAttributes((Attributes)this.delegate.findByNameWithAttributes(key.getDirectoryId(), key.getName())));
        }
        catch (UserNotFoundException e) {
            return Option.none();
        }
    }

    private TimestampedUser findUser(@NonNull CachedCrowdEntityCacheKey key) throws UserNotFoundException {
        return (TimestampedUser)Objects.requireNonNull(this.getUserCache().get(key, (Supplier<Option<TimestampedUser>>)((Supplier)() -> this.findUserInternal(key)))).getOrThrow(() -> new UserNotFoundException(key.getName()));
    }

    private Attributes findAttributes(@NonNull CachedCrowdEntityCacheKey key) throws UserNotFoundException {
        return (Attributes)Objects.requireNonNull(this.getUserAttributesCache().get(key, (Supplier<Option<ImmutableAttributes>>)((Supplier)() -> this.findAttributesInternal(key)))).getOrThrow(() -> new UserNotFoundException(key.getName()));
    }

    public TimestampedUser findByName(long directoryId, String userName) throws UserNotFoundException {
        return this.findUser(new CachedCrowdEntityCacheKey(directoryId, userName));
    }

    public TimestampedUser findByExternalId(long directoryId, String externalId) throws UserNotFoundException {
        return this.delegate.findByExternalId(directoryId, externalId);
    }

    public UserWithAttributes findByNameWithAttributes(long directoryId, String userName) throws UserNotFoundException {
        CachedCrowdEntityCacheKey key = new CachedCrowdEntityCacheKey(directoryId, userName);
        TimestampedUser user = this.findUser(key);
        Attributes attributes = this.findAttributes(key);
        return new DelegatingUserWithAttributes((User)user, attributes);
    }

    public PasswordCredential getCredential(long directoryId, String userName) throws UserNotFoundException {
        return this.delegate.getCredential(directoryId, userName);
    }

    public List<PasswordCredential> getCredentialHistory(long directoryId, String userName) throws UserNotFoundException {
        return this.delegate.getCredentialHistory(directoryId, userName);
    }

    public BatchResult<User> addAll(Set<UserTemplateWithCredentialAndAttributes> users) {
        if (log.isDebugEnabled()) {
            log.debug("adding [ {} ] users", (Object)users.size());
        }
        for (UserTemplateWithCredentialAndAttributes user : users) {
            this.removeFromCaches(new CachedCrowdEntityCacheKey((User)user));
        }
        return this.delegate.addAll(users);
    }

    @Override
    public InternalUser add(User user, PasswordCredential credential) throws UserAlreadyExistsException, IllegalArgumentException, DirectoryNotFoundException {
        log.debug("adding single user [ {} ]", (Object)user);
        CachedCrowdEntityCacheKey key = new CachedCrowdEntityCacheKey(user);
        this.removeFromCaches(key);
        InternalUser createdUser = this.delegate.add(user, credential);
        this.getUserCache().remove(key);
        return createdUser;
    }

    public void storeAttributes(User user, Map<String, Set<String>> attributes, boolean updateTimestamp) throws UserNotFoundException {
        this.getUserAttributesCache().remove(new CachedCrowdEntityCacheKey(user));
        this.delegate.storeAttributes(user, attributes, updateTimestamp);
    }

    public User update(User user) throws UserNotFoundException, IllegalArgumentException {
        CachedCrowdEntityCacheKey key = new CachedCrowdEntityCacheKey(user);
        this.getUserCache().remove(key);
        this.getUserEmailCache().removeAll();
        User updatedUser = this.delegate.update(user);
        this.getUserCache().remove(key);
        return updatedUser;
    }

    public void updateCredential(User user, PasswordCredential credential, int maxCredentialHistory) throws UserNotFoundException, IllegalArgumentException {
        this.delegate.updateCredential(user, credential, maxCredentialHistory);
    }

    public User rename(User user, String newName) throws UserNotFoundException, UserAlreadyExistsException, IllegalArgumentException {
        String oldName = user.getName();
        this.removeFromCaches(new CachedCrowdEntityCacheKey(user));
        CachedCrowdEntityCacheKey newKey = new CachedCrowdEntityCacheKey(user.getDirectoryId(), newName);
        this.removeFromCaches(newKey);
        User renamedUser = this.delegate.rename(user, newName);
        this.getUserCache().remove(newKey);
        this.eventPublisher.publish((Object)new DirectoryUserRenamedEvent(this, oldName, renamedUser));
        return renamedUser;
    }

    public void removeAttribute(User user, String attributeName) throws UserNotFoundException {
        this.getUserAttributesCache().remove(new CachedCrowdEntityCacheKey(user));
        this.delegate.removeAttribute(user, attributeName);
    }

    public void remove(User user) throws UserNotFoundException {
        this.removeFromCaches(new CachedCrowdEntityCacheKey(user));
        this.delegate.remove(user);
    }

    private <T> boolean isSimpleUserEmailQuery(EntityQuery<T> query) {
        if (!query.getReturnType().isAssignableFrom(TimestampedUser.class)) {
            return false;
        }
        SearchRestriction searchRestriction = query.getSearchRestriction();
        if (!(searchRestriction instanceof TermRestriction)) {
            return false;
        }
        TermRestriction term = (TermRestriction)searchRestriction;
        return UserTermKeys.EMAIL.equals(term.getProperty());
    }

    public <T> List<T> search(long directoryId, EntityQuery<T> query) {
        if (this.isSimpleUserEmailQuery(query)) {
            CachedCrowdUserEmailSearchQuery key = new CachedCrowdUserEmailSearchQuery(directoryId, query);
            return this.getUserEmailCache().get(key, (Supplier<List<TimestampedUser>>)((Supplier)() -> this.searchByEmailInternal(key)));
        }
        return this.delegate.search(directoryId, query);
    }

    @Override
    public InternalUser internalFindByName(long directoryId, String userName) throws UserNotFoundException {
        return this.delegate.internalFindByName(directoryId, userName);
    }

    @Override
    public InternalUser internalFindByUser(User user) throws UserNotFoundException {
        return this.delegate.internalFindByUser(user);
    }

    @Override
    public void removeAllUsers(long directoryId) {
        this.getUserCache().removeAll();
        this.getUserEmailCache().removeAll();
        this.getUserAttributesCache().removeAll();
        this.delegate.removeAllUsers(directoryId);
    }

    @Override
    public Collection<InternalUser> findByNames(long directoryId, Collection<String> userNames) {
        return this.delegate.findByNames(directoryId, userNames);
    }

    @Override
    public int prefetchAndCacheUsers(long directoryId, Collection<String> usernames) {
        return new CrowdUserCache(CoreCache.CROWD_USERS_BY_NAME.getCache(this.nonTxCacheFactory)).findByNames(directoryId, usernames, this.delegate::findByNames).size();
    }

    public BatchResult<String> removeAllUsers(long directoryId, Set<String> userNames) {
        for (String userName : userNames) {
            this.removeFromCaches(new CachedCrowdEntityCacheKey(directoryId, userName));
        }
        return this.delegate.removeAllUsers(directoryId, userNames);
    }

    public void setAttributeForAllInDirectory(long directoryId, String attrName, String attrValue) {
        this.getUserAttributesCache().removeAll();
        this.delegate.setAttributeForAllInDirectory(directoryId, attrName, attrValue);
    }

    public Set<String> getAllExternalIds(long directoryId) throws DirectoryNotFoundException {
        return this.delegate.getAllExternalIds(directoryId);
    }

    public long getUserCount(long directoryId) throws DirectoryNotFoundException {
        return this.delegate.getUserCount(directoryId);
    }

    public Set<Long> findDirectoryIdsContainingUserName(String username) {
        return this.delegate.findDirectoryIdsContainingUserName(username);
    }

    public Map<String, String> findByExternalIds(long directoryId, Set<String> externalIds) {
        return this.delegate.findByExternalIds(directoryId, externalIds);
    }

    private void removeFromCaches(CachedCrowdEntityCacheKey key) {
        this.getUserCache().remove(key);
        this.getUserAttributesCache().remove(key);
        this.getUserEmailCache().removeAll();
    }
}

