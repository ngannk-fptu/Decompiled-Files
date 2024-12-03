/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Encryptor
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Throwables
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.util.concurrent.UncheckedExecutionException
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.crypto;

import com.atlassian.crowd.crypto.SaltingEncryptor;
import com.atlassian.crowd.embedded.api.Encryptor;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.time.Duration;
import java.util.function.BooleanSupplier;
import java.util.function.UnaryOperator;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachedEncryptor
implements Encryptor {
    private static final Logger logger = LoggerFactory.getLogger(CachedEncryptor.class);
    private static final String NULL_REPLACEMENT = "";
    private static final String NON_NULL_PREFIX = "p";
    private final LoadingCache<String, String> encryptionCache;
    private final LoadingCache<String, String> decryptionCache;
    private final BooleanSupplier cacheEnabledSupplier;
    private final Encryptor delegate;

    public CachedEncryptor(Encryptor delegate, long maxCacheSize, Duration expireAfterAccess, BooleanSupplier cacheEnabledSupplier) {
        Preconditions.checkArgument((!(delegate instanceof SaltingEncryptor) ? 1 : 0) != 0, (Object)"SaltingEncryptor should not be cached");
        this.delegate = delegate;
        this.encryptionCache = CachedEncryptor.createCache(maxCacheSize, expireAfterAccess, password -> {
            logger.debug("Encrypted password not found in encryption cache. Encrypting.");
            return delegate.encrypt(password);
        });
        this.decryptionCache = CachedEncryptor.createCache(maxCacheSize, expireAfterAccess, encryptedPassword -> {
            logger.debug("Decrypted password not found in decryption cache. Decrypting.");
            return delegate.decrypt(encryptedPassword);
        });
        this.cacheEnabledSupplier = cacheEnabledSupplier;
    }

    private static LoadingCache<String, String> createCache(long maxCacheSize, Duration expireAfterAccess, final UnaryOperator<String> loader) {
        return CacheBuilder.newBuilder().maximumSize(maxCacheSize).expireAfterAccess(expireAfterAccess).build((CacheLoader)new CacheLoader<String, String>(){

            @Nonnull
            public String load(@Nonnull String sanitizedKey) {
                return CachedEncryptor.wrapNull((String)loader.apply(CachedEncryptor.unwrapNull(sanitizedKey)));
            }
        });
    }

    public String encrypt(String password) {
        if (!this.cacheEnabledSupplier.getAsBoolean()) {
            return this.delegate.encrypt(password);
        }
        String encrypted = this.get(this.encryptionCache, password);
        this.decryptionCache.put((Object)CachedEncryptor.wrapNull(encrypted), (Object)CachedEncryptor.wrapNull(password));
        return encrypted;
    }

    public String decrypt(String encryptedPassword) {
        if (!this.cacheEnabledSupplier.getAsBoolean()) {
            return this.delegate.decrypt(encryptedPassword);
        }
        return this.get(this.decryptionCache, encryptedPassword);
    }

    private String get(LoadingCache<String, String> cache, String original) {
        try {
            String resultSanitized = (String)cache.getUnchecked((Object)CachedEncryptor.wrapNull(original));
            return CachedEncryptor.unwrapNull(resultSanitized);
        }
        catch (UncheckedExecutionException e) {
            Throwables.throwIfUnchecked((Throwable)e.getCause());
            throw e;
        }
    }

    private static String wrapNull(String data) {
        return data == null ? NULL_REPLACEMENT : NON_NULL_PREFIX + data;
    }

    private static String unwrapNull(String data) {
        return NULL_REPLACEMENT.equals(data) ? null : data.substring(NON_NULL_PREFIX.length());
    }

    public boolean changeEncryptionKey() {
        try {
            boolean bl = this.delegate.changeEncryptionKey();
            return bl;
        }
        finally {
            logger.debug("Clearing the encryption cache.");
            this.clearCache();
        }
    }

    @VisibleForTesting
    void clearCache() {
        this.encryptionCache.invalidateAll();
        this.decryptionCache.invalidateAll();
    }
}

