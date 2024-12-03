/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.directory.DirectoryWrapper;
import com.atlassian.crowd.embedded.api.Directory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;

public class LazyAttributesEvaluationDirectory
extends DirectoryWrapper
implements Directory {
    private final Set<String> lazyEvaluatedKeys;
    private final UnaryOperator<String> transformer;
    private final Cache<String, Optional<String>> evaluatedAttributes;

    public LazyAttributesEvaluationDirectory(Directory delegate, Set<String> lazyEvaluatedKeys, UnaryOperator<String> transformer) {
        super(delegate);
        this.lazyEvaluatedKeys = ImmutableSet.copyOf(lazyEvaluatedKeys);
        this.transformer = transformer;
        this.evaluatedAttributes = CacheBuilder.newBuilder().build();
    }

    @Override
    @Nullable
    public Set<String> getValues(String key) {
        if (this.lazyEvaluatedKeys.contains(key)) {
            try {
                return ((Optional)this.evaluatedAttributes.get((Object)key, this.extractFromDelegateAndTransform(key))).map(Collections::singleton).orElse(null);
            }
            catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return this.delegate.getValues(key);
    }

    @Override
    @Nullable
    public String getValue(String key) {
        if (this.lazyEvaluatedKeys.contains(key)) {
            try {
                return ((Optional)this.evaluatedAttributes.get((Object)key, this.extractFromDelegateAndTransform(key))).orElse(null);
            }
            catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return this.delegate.getValue(key);
    }

    @Override
    public Map<String, String> getAttributes() {
        HashMap<String, String> attributes = new HashMap<String, String>(this.delegate.getAttributes());
        this.lazyEvaluatedKeys.stream().filter(attributes::containsKey).forEach(k -> attributes.put((String)k, this.getValue((String)k)));
        return attributes;
    }

    private Callable<Optional<String>> extractFromDelegateAndTransform(String key) {
        return () -> {
            Set originalValues = this.delegate.getValues(key);
            if (originalValues == null || originalValues.isEmpty()) {
                return Optional.empty();
            }
            String value = (String)Iterables.getOnlyElement((Iterable)originalValues);
            return Optional.of(this.transformer.apply(value));
        };
    }
}

