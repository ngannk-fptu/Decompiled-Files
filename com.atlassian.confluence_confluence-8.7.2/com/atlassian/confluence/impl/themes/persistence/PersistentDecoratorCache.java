/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.google.common.annotations.VisibleForTesting
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.themes.persistence;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.core.PersistentDecorator;
import com.atlassian.confluence.impl.cache.ReadThroughAtlassianCache;
import com.atlassian.confluence.impl.cache.ReadThroughCache;
import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

class PersistentDecoratorCache {
    @VisibleForTesting
    static final String SINGLETON_KEY = "SingletonKey";
    private final ReadThroughCache<String, Decorators> spaceDecoratorsCache;
    private final ReadThroughCache<String, Boolean> hasDecoratorsCache;

    public static PersistentDecoratorCache create(CacheFactory cacheFactory) {
        return new PersistentDecoratorCache(ReadThroughAtlassianCache.create(cacheFactory, CoreCache.DECORATORS_BY_SPACE_KEY), ReadThroughAtlassianCache.create(cacheFactory, CoreCache.DECORATORS_EXIST_BY_SPACE_KEY));
    }

    PersistentDecoratorCache(ReadThroughCache<String, Decorators> spaceDecoratorsCache, ReadThroughCache<String, Boolean> hasDecoratorsCache) {
        this.spaceDecoratorsCache = spaceDecoratorsCache;
        this.hasDecoratorsCache = hasDecoratorsCache;
    }

    public @NonNull Optional<PersistentDecorator> getDecoratorByName(@Nullable String spaceKey, String decoratorName, Function<String, Collection<PersistentDecorator>> spaceKeyToDecoratorsLookup) {
        return this.spaceDecoratorsCache.get(PersistentDecoratorCache.cacheKey(spaceKey), () -> Decorators.create(spaceKey, spaceKeyToDecoratorsLookup)).getDecorator(decoratorName);
    }

    public boolean hasDecorators(Supplier<Boolean> hasAnyDecoratorsSupplier) {
        return this.hasDecoratorsCache.get(SINGLETON_KEY, hasAnyDecoratorsSupplier);
    }

    public void remove(PersistentDecorator decorator) {
        this.spaceDecoratorsCache.remove(PersistentDecoratorCache.cacheKey(decorator));
        this.hasDecoratorsCache.removeAll();
    }

    private static String cacheKey(PersistentDecorator decorator) {
        return PersistentDecoratorCache.cacheKey(decorator.getSpaceKey());
    }

    private static String cacheKey(@Nullable String spaceKey) {
        return Optional.ofNullable(spaceKey).orElse("_GLOBAL_");
    }

    @VisibleForTesting
    static class Decorators
    implements Serializable {
        private final Map<String, PersistentDecorator> decoratorsByName;

        static Decorators create(@Nullable String spaceKey, Function<String, Collection<PersistentDecorator>> spaceKeyToDecoratorsLookup) {
            return new Decorators(spaceKeyToDecoratorsLookup.apply(spaceKey));
        }

        public Decorators(Collection<PersistentDecorator> decorators) {
            this.decoratorsByName = decorators.stream().collect(Collectors.toMap(PersistentDecorator::getName, Function.identity()));
        }

        public Optional<PersistentDecorator> getDecorator(String decoratorName) {
            return Optional.ofNullable(this.decoratorsByName.get(decoratorName));
        }
    }
}

