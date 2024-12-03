/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.CasIdentifier
 *  com.atlassian.vcache.DirectExternalCache
 *  com.atlassian.vcache.IdentifiedValue
 *  com.atlassian.vcache.PutPolicy
 *  com.atlassian.vcache.VCacheUtils
 *  io.atlassian.fugue.Either
 */
package com.atlassian.confluence.core;

import com.atlassian.vcache.CasIdentifier;
import com.atlassian.vcache.DirectExternalCache;
import com.atlassian.vcache.IdentifiedValue;
import com.atlassian.vcache.PutPolicy;
import com.atlassian.vcache.VCacheUtils;
import io.atlassian.fugue.Either;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;

@Deprecated
class VCacheCasUtils {
    private static final Clock clock = Clock.systemDefaultZone();

    VCacheCasUtils() {
    }

    static <V> Either<Throwable, V> atomicReplace(DirectExternalCache<V> vcache, String key, Function<V, V> updater, Supplier<V> defaultSupplier, Duration maxDuration) {
        return VCacheCasUtils.internalAtomicLoop(maxDuration, () -> (Optional)VCacheUtils.fold((CompletionStage)vcache.getIdentified(key), arg_0 -> VCacheCasUtils.lambda$atomicReplace$4(updater, vcache, key, (Supplier)defaultSupplier, arg_0), err -> Optional.of(Either.left((Object)err))));
    }

    private static <T> Either<Throwable, T> internalAtomicLoop(Duration maxDuration, Supplier<Optional<Either<Throwable, T>>> cacheOperation) {
        Instant completeBy = clock.instant().plus(maxDuration);
        do {
            Optional<Either<Throwable, T>> result;
            if (!(result = cacheOperation.get()).isPresent()) continue;
            return result.get();
        } while (clock.instant().isBefore(completeBy));
        return Either.left((Object)new CompletionException(new RuntimeException("Timed out")));
    }

    private static /* synthetic */ Optional lambda$atomicReplace$4(Function updater, DirectExternalCache vcache, String key, Supplier defaultSupplier, Optional identifiedValue) {
        if (identifiedValue.isPresent()) {
            CasIdentifier identifier = ((IdentifiedValue)identifiedValue.get()).identifier();
            Object value = ((IdentifiedValue)identifiedValue.get()).value();
            Object replacement = updater.apply(value);
            return (Optional)VCacheUtils.fold((CompletionStage)vcache.replaceIf(key, identifier, replacement), res -> res != false ? Optional.of(Either.right((Object)replacement)) : Optional.empty(), err -> Optional.of(Either.left((Object)err)));
        }
        Object item = defaultSupplier.get();
        return (Optional)VCacheUtils.fold((CompletionStage)vcache.put(key, item, PutPolicy.ADD_ONLY), res -> res != false ? Optional.of(Either.right((Object)item)) : Optional.empty(), err -> Optional.of(Either.left((Object)err)));
    }
}

