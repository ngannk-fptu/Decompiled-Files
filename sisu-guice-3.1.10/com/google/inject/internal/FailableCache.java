/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 */
package com.google.inject.internal;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class FailableCache<K, V> {
    private final LoadingCache<K, Object> delegate = CacheBuilder.newBuilder().build(new CacheLoader<K, Object>(){

        public Object load(K key) {
            Errors errors = new Errors();
            Object result = null;
            try {
                result = FailableCache.this.create(key, errors);
            }
            catch (ErrorsException e) {
                errors.merge(e.getErrors());
            }
            return errors.hasErrors() ? errors : result;
        }
    });

    protected abstract V create(K var1, Errors var2) throws ErrorsException;

    public V get(K key, Errors errors) throws ErrorsException {
        Object resultOrError = this.delegate.getUnchecked(key);
        if (resultOrError instanceof Errors) {
            errors.merge((Errors)resultOrError);
            throw errors.toException();
        }
        Object result = resultOrError;
        return (V)result;
    }

    boolean remove(K key) {
        return this.delegate.asMap().remove(key) != null;
    }
}

