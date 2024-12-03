/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.marshalling.api.MarshallingPair
 *  com.atlassian.vcache.ExternalCacheException
 *  com.atlassian.vcache.ExternalCacheException$Reason
 *  com.atlassian.vcache.PutPolicy
 *  com.atlassian.vcache.internal.core.cas.IdentifiedData
 *  com.atlassian.vcache.internal.core.cas.IdentifiedUtils
 */
package com.atlassian.vcache.internal.legacy;

import com.atlassian.cache.Cache;
import com.atlassian.marshalling.api.MarshallingPair;
import com.atlassian.vcache.ExternalCacheException;
import com.atlassian.vcache.PutPolicy;
import com.atlassian.vcache.internal.core.cas.IdentifiedData;
import com.atlassian.vcache.internal.core.cas.IdentifiedUtils;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class LegacyUtils {
    LegacyUtils() {
    }

    static boolean directPut(String externalKey, IdentifiedData identifiedData, PutPolicy policy, Cache<String, IdentifiedData> delegate, boolean avoidCasOps) {
        PutPolicy convertedPolicy = avoidCasOps ? PutPolicy.PUT_ALWAYS : policy;
        switch (convertedPolicy) {
            case ADD_ONLY: {
                return delegate.putIfAbsent((Object)externalKey, (Object)identifiedData) == null;
            }
            case PUT_ALWAYS: {
                delegate.put((Object)externalKey, (Object)identifiedData);
                return true;
            }
            case REPLACE_ONLY: {
                IdentifiedData existingData = (IdentifiedData)delegate.get((Object)externalKey);
                return existingData != null && delegate.replace((Object)externalKey, (Object)existingData, (Object)identifiedData);
            }
        }
        throw new IllegalArgumentException("Unknown put policy: " + convertedPolicy);
    }

    static ExternalCacheException mapException(Exception ex) {
        return new ExternalCacheException(ExternalCacheException.Reason.UNCLASSIFIED_FAILURE, (Throwable)ex);
    }

    static <V> Map<String, Optional<V>> directGetBulk(Set<String> externalKeys, Cache<String, IdentifiedData> delegate, Optional<MarshallingPair<V>> valueMarshalling) {
        return StreamSupport.stream(externalKeys.spliterator(), false).distinct().collect(Collectors.toMap(Objects::requireNonNull, k -> IdentifiedUtils.unmarshall((IdentifiedData)((IdentifiedData)delegate.get(k)), (Optional)valueMarshalling)));
    }
}

