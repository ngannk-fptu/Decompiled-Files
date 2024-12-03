/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.crowd.exception.ObjectAlreadyExistsException
 *  com.atlassian.crowd.exception.ObjectNotFoundException
 *  com.atlassian.crowd.model.token.AuthenticationToken
 *  com.atlassian.crowd.model.token.Token
 *  com.atlassian.crowd.model.token.TokenLifetime
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.util.SearchResultsUtil
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Iterables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.dao.token;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.crowd.dao.token.TokenDAO;
import com.atlassian.crowd.dao.token.TokenDAOSearchUtils;
import com.atlassian.crowd.exception.ObjectAlreadyExistsException;
import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.model.token.AuthenticationToken;
import com.atlassian.crowd.model.token.Token;
import com.atlassian.crowd.model.token.TokenLifetime;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.util.SearchResultsUtil;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenDAOMemory
implements TokenDAO {
    public static final String RANDOM_HASH_CACHE = Token.class.getName() + ".random-hash-cache";
    public static final String IDENTIFIER_HASH_CACHE = Token.class.getName() + ".identifier-hash-cache";
    private static final Logger logger = LoggerFactory.getLogger(TokenDAOMemory.class);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Cache<String, Token> randomHashCache;
    private final Cache<String, Token> identifierHashCache;

    public TokenDAOMemory(CacheFactory cacheManager) {
        this((Cache<String, Token>)cacheManager.getCache(RANDOM_HASH_CACHE), (Cache<String, Token>)cacheManager.getCache(IDENTIFIER_HASH_CACHE));
    }

    public TokenDAOMemory(Cache<String, Token> randomHashCache, Cache<String, Token> identifierHashCache) {
        this.randomHashCache = (Cache)Preconditions.checkNotNull(randomHashCache);
        this.identifierHashCache = (Cache)Preconditions.checkNotNull(identifierHashCache);
    }

    @Override
    public Token findByRandomHash(String randomHash) throws ObjectNotFoundException {
        this.lock.readLock().lock();
        try {
            Token token = this.internalFindByRandomHash(randomHash);
            return token;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    private Token internalFindByRandomHash(String randomHash) throws ObjectNotFoundException {
        Token token = (Token)this.randomHashCache.get((Object)randomHash);
        if (token == null) {
            throw new ObjectNotFoundException(Token.class, (Object)randomHash);
        }
        return token;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Token findByIdentifierHash(String identifierHash) throws ObjectNotFoundException {
        this.lock.readLock().lock();
        try {
            Token token = (Token)this.identifierHashCache.get((Object)identifierHash);
            if (token == null) {
                throw new ObjectNotFoundException(Token.class, (Object)identifierHash);
            }
            Token token2 = token;
            return token2;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public Token add(Token token) throws ObjectAlreadyExistsException {
        this.lock.writeLock().lock();
        try {
            if (this.containsByIdentifierHash(token.getIdentifierHash())) {
                throw new ObjectAlreadyExistsException(token.getIdentifierHash());
            }
            this.randomHashCache.put((Object)token.getRandomHash(), (Object)token);
            this.identifierHashCache.put((Object)token.getIdentifierHash(), (Object)token);
        }
        finally {
            this.lock.writeLock().unlock();
        }
        return token;
    }

    private boolean containsByIdentifierHash(String identifierHash) {
        return this.identifierHashCache.containsKey((Object)identifierHash);
    }

    @Override
    public Token update(Token token) {
        this.lock.writeLock().lock();
        try {
            this.randomHashCache.put((Object)token.getRandomHash(), (Object)token);
            this.identifierHashCache.put((Object)token.getIdentifierHash(), (Object)token);
        }
        finally {
            this.lock.writeLock().unlock();
        }
        return token;
    }

    @Override
    public void remove(Token token) {
        this.lock.writeLock().lock();
        try {
            this.randomHashCache.remove((Object)token.getRandomHash());
            this.identifierHashCache.remove((Object)token.getIdentifierHash());
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<AuthenticationToken> search(EntityQuery<? extends AuthenticationToken> query) {
        Preconditions.checkArgument((query.getEntityDescriptor().getEntityType() == Entity.TOKEN ? 1 : 0) != 0, (Object)"TokenDAO can only evaluate EntityQueries for Entity.TOKEN");
        ImmutableList.Builder tokens = ImmutableList.builder();
        this.lock.readLock().lock();
        try {
            for (String key : this.findRandomHashKeys()) {
                try {
                    Token token = this.internalFindByRandomHash(key);
                    if (!TokenDAOSearchUtils.tokenMatchesSearchRestriction(token, query.getSearchRestriction())) continue;
                    tokens.add((Object)token);
                }
                catch (ObjectNotFoundException e) {
                    logger.error(e.getMessage(), (Throwable)e);
                }
            }
        }
        finally {
            this.lock.readLock().unlock();
        }
        return SearchResultsUtil.constrainResults((List)tokens.build(), (int)query.getStartIndex(), (int)query.getMaxResults());
    }

    private Iterable<String> findRandomHashKeys() {
        return this.randomHashCache.getKeys();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void remove(long directoryId, String name) {
        this.lock.writeLock().lock();
        try {
            Iterable<String> keys = this.findRandomHashKeys();
            this.remove(keys, directoryId, name);
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    private void remove(Iterable<String> keys, long directoryId, String name) {
        for (String key : keys) {
            try {
                Token token = this.internalFindByRandomHash(key);
                if (token.getDirectoryId() != directoryId || !token.getName().equals(name)) continue;
                this.remove(token);
            }
            catch (ObjectNotFoundException objectNotFoundException) {}
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeExcept(long directoryId, String name, String exclusionToken) {
        this.lock.writeLock().lock();
        try {
            Iterable keys = Iterables.filter(this.findRandomHashKeys(), (Predicate)Predicates.not((Predicate)Predicates.equalTo((Object)exclusionToken)));
            this.remove(keys, directoryId, name);
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void removeAll(long directoryId) {
        this.lock.writeLock().lock();
        try {
            for (String key : this.findRandomHashKeys()) {
                try {
                    Token token = this.internalFindByRandomHash(key);
                    if (token.getDirectoryId() != directoryId) continue;
                    this.remove(token);
                }
                catch (ObjectNotFoundException e) {
                    throw new IllegalStateException("Key already removed: " + key);
                    return;
                }
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void removeExpiredTokens(Date currentTime, long maxLifeInSeconds) {
        this.lock.writeLock().lock();
        try {
            for (String key : this.findRandomHashKeys()) {
                try {
                    Token token = this.internalFindByRandomHash(key);
                    long effectiveTokenSessionTime = TokenDAOMemory.getEffectiveTokenSessionTime(token, maxLifeInSeconds);
                    Date expiryTime = new Date(token.getLastAccessedTime() + TimeUnit.SECONDS.toMillis(effectiveTokenSessionTime));
                    if (!expiryTime.before(currentTime)) continue;
                    this.remove(token);
                }
                catch (ObjectNotFoundException e) {
                    throw new IllegalStateException("Key already removed: " + key);
                    return;
                }
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    private static long getEffectiveTokenSessionTime(Token token, long defaultMaxLifeInSeconds) {
        TokenLifetime tokenLifetime = token.getLifetime();
        if (tokenLifetime.isDefault()) {
            return defaultMaxLifeInSeconds;
        }
        return Math.min(tokenLifetime.getSeconds(), defaultMaxLifeInSeconds);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Collection<Token> loadAll() {
        this.lock.readLock().lock();
        try {
            ImmutableList.Builder tokens = ImmutableList.builder();
            for (String key : this.findRandomHashKeys()) {
                try {
                    Token token = this.internalFindByRandomHash(key);
                    tokens.add((Object)token);
                }
                catch (ObjectNotFoundException e) {
                    throw new IllegalStateException("Key already removed: " + key);
                }
            }
            ImmutableList immutableList = tokens.build();
            return immutableList;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void saveAll(Collection<Token> tokens) {
        Preconditions.checkNotNull(tokens);
        this.lock.writeLock().lock();
        try {
            for (Token token : tokens) {
                this.randomHashCache.put((Object)token.getRandomHash(), (Object)token);
                this.identifierHashCache.put((Object)token.getIdentifierHash(), (Object)token);
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    @Override
    public void removeAll() {
        this.lock.writeLock().lock();
        try {
            this.randomHashCache.removeAll();
            this.identifierHashCache.removeAll();
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }
}

