/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.util.$Function;
import com.google.inject.internal.util.$MapMaker;
import com.google.inject.internal.util.$Nullable;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class FailableCache<K, V> {
    private final Map<K, Object> delegate = new $MapMaker().makeComputingMap(new $Function<K, Object>(){

        @Override
        public Object apply(@$Nullable K key) {
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
        Object resultOrError = this.delegate.get(key);
        if (resultOrError instanceof Errors) {
            errors.merge((Errors)resultOrError);
            throw errors.toException();
        }
        Object result = resultOrError;
        return (V)result;
    }

    boolean remove(K key) {
        return this.delegate.remove(key) != null;
    }
}

