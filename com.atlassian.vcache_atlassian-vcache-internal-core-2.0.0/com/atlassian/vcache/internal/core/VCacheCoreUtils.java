/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.marshalling.api.MarshallingException
 *  com.atlassian.marshalling.api.MarshallingPair
 *  com.atlassian.vcache.ExternalCacheException
 *  com.atlassian.vcache.ExternalCacheException$Reason
 *  javax.annotation.Nullable
 */
package com.atlassian.vcache.internal.core;

import com.atlassian.marshalling.api.MarshallingException;
import com.atlassian.marshalling.api.MarshallingPair;
import com.atlassian.vcache.ExternalCacheException;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.LongConsumer;
import javax.annotation.Nullable;

public class VCacheCoreUtils {
    public static <T> CompletionStage<T> successful(T value) {
        return CompletableFuture.completedFuture(value);
    }

    public static <T> CompletionStage<T> failed(CompletionStage<T> result, Throwable cause) {
        result.toCompletableFuture().completeExceptionally(cause);
        return result;
    }

    public static int roundUpToSeconds(Duration time) {
        long result;
        if (time.isNegative()) {
            throw new IllegalArgumentException("duration cannot be negative: " + time);
        }
        long l = result = time.getNano() > 0 ? time.getSeconds() + 1L : time.getSeconds();
        if (result > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("duration exceeds maximum number that can be held in an int");
        }
        return (int)result;
    }

    public static void whenPositive(long number, LongConsumer handler) {
        if (number > 0L) {
            handler.accept(number);
        }
    }

    public static <V> byte[] marshall(V data, MarshallingPair<V> valueMarshalling) throws ExternalCacheException {
        try {
            return valueMarshalling.getMarshaller().marshallToBytes(Objects.requireNonNull(data));
        }
        catch (MarshallingException e) {
            throw new ExternalCacheException(ExternalCacheException.Reason.MARSHALLER_FAILURE, (Throwable)e);
        }
    }

    public static <V> Optional<V> unmarshall(@Nullable byte[] data, MarshallingPair<V> valueMarshalling) throws ExternalCacheException {
        if (data == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(valueMarshalling.getUnmarshaller().unmarshallFrom(data));
        }
        catch (MarshallingException ex) {
            throw new ExternalCacheException(ExternalCacheException.Reason.MARSHALLER_FAILURE, (Throwable)ex);
        }
    }

    public static <K> boolean isEmpty(Iterable<K> iter) {
        return !iter.iterator().hasNext();
    }
}

