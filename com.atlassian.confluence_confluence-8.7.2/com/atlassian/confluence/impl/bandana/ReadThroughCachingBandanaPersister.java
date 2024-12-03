/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaPersister
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.fugue.Option
 *  com.google.common.annotations.VisibleForTesting
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.bandana;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaPersister;
import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.impl.cache.ReadThroughAtlassianCache;
import com.atlassian.confluence.impl.cache.ReadThroughCache;
import com.atlassian.confluence.setup.bandana.KeyedBandanaContext;
import com.atlassian.fugue.Option;
import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class ReadThroughCachingBandanaPersister
implements BandanaPersister {
    private static final Logger log = LoggerFactory.getLogger(ReadThroughCachingBandanaPersister.class);
    private static final Class<?>[] KNOWN_IMMUTABLE_TYPES = new Class[]{String.class, Enum.class, BigDecimal.class, BigInteger.class, Boolean.TYPE, Boolean.class, Byte.TYPE, Byte.class, Character.TYPE, Character.class, Double.TYPE, Double.class, Float.TYPE, Float.class, Integer.TYPE, Integer.class, Long.TYPE, Long.class, Short.TYPE, Short.class};
    private final BandanaPersister persister;
    private final ReadThroughCache<String, Option<Object>> cache;

    ReadThroughCachingBandanaPersister(BandanaPersister persister, ReadThroughCache<String, Option<Object>> cache) {
        this.persister = Objects.requireNonNull(persister);
        this.cache = cache;
    }

    public static ReadThroughCachingBandanaPersister create(BandanaPersister persister, CacheFactory cacheFactory) {
        return new ReadThroughCachingBandanaPersister(persister, ReadThroughAtlassianCache.create(cacheFactory, CoreCache.BANDANA_VALUE_BY_CONTEXT_AND_KEY));
    }

    public @Nullable Object retrieve(BandanaContext context, String key) {
        return this.retrieve((KeyedBandanaContext)context, key);
    }

    private @Nullable Object retrieve(KeyedBandanaContext context, String key) {
        String cacheKey = ReadThroughCachingBandanaPersister.cacheKey(context, key);
        return this.cache.get(cacheKey, () -> {
            log.debug("Retrieving entry for key '{}'", (Object)key);
            Object persistedResult = this.persister.retrieve((BandanaContext)context, key);
            log.debug("Retrieved entry for key '{}', '{}'", (Object)key, persistedResult);
            return Option.option((Object)persistedResult);
        }).getOrNull();
    }

    public Map<String, Object> retrieve(BandanaContext context) {
        Set keys = this.persister.retrieve(context).keySet();
        HashMap<String, Object> result = new HashMap<String, Object>();
        for (Object obj : keys) {
            String key = (String)obj;
            result.put(key, this.retrieve(context, key));
        }
        return result;
    }

    public Iterable<String> retrieveKeys(BandanaContext bandanaContext) {
        return this.persister.retrieveKeys(bandanaContext);
    }

    public void store(BandanaContext context, String key, @Nullable Object value) {
        this.store((KeyedBandanaContext)context, key, value);
    }

    private void store(KeyedBandanaContext context, String key, @Nullable Object value) {
        ReadThroughCachingBandanaPersister.warnIfValueNotSerializable(value);
        String cacheKey = ReadThroughCachingBandanaPersister.cacheKey(context, key);
        Option cachedValue = this.cache.get(cacheKey, () -> null);
        if (cachedValue != null) {
            if (value == null && !cachedValue.isDefined()) {
                log.debug("Skipping replacement of null entry for key '{}'", (Object)key);
                return;
            }
            if (value != null && cachedValue.isDefined() && cachedValue.get().equals(value) && ReadThroughCachingBandanaPersister.isImmutableType(value.getClass())) {
                log.debug("Skipping replacement of immutable entry for key '{}', value '{}'", (Object)key, value);
                return;
            }
        }
        log.debug("Storing entry for key '{}', value '{}'", (Object)key, value);
        this.persister.store((BandanaContext)context, key, value);
        log.debug("Stored entry successfully for key '{}', value '{}'", (Object)key, value);
        this.cache.remove(cacheKey);
        this.retrieve(context, key);
    }

    private static void warnIfValueNotSerializable(@Nullable Object value) {
        Optional.ofNullable(value).filter(x -> !(x instanceof Serializable)).ifPresent(x -> log.warn("Non-serializable type {} is not compatible with remote Bandana cache", (Object)x.getClass().getName()));
    }

    @VisibleForTesting
    static String cacheKey(KeyedBandanaContext context, String key) {
        return context.getContextKey() + "-" + key;
    }

    private static boolean isImmutableType(Class<?> type) {
        return Stream.of(KNOWN_IMMUTABLE_TYPES).anyMatch(type::isAssignableFrom);
    }

    public void flushCaches() {
        this.cache.removeAll();
    }

    public void remove(BandanaContext context) {
        this.persister.remove(context);
        this.cache.removeAll();
    }

    public void remove(BandanaContext context, String key) {
        this.remove((KeyedBandanaContext)context, key);
    }

    private void remove(KeyedBandanaContext context, String key) {
        this.persister.remove((BandanaContext)context, key);
        String cacheKey = ReadThroughCachingBandanaPersister.cacheKey(context, key);
        this.cache.remove(cacheKey);
        this.cache.get(cacheKey, Option::none);
    }
}

