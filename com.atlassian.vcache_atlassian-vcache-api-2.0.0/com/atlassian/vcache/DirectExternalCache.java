/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.vcache;

import com.atlassian.annotations.PublicApi;
import com.atlassian.vcache.CasIdentifier;
import com.atlassian.vcache.ExternalCache;
import com.atlassian.vcache.ExternalWriteOperationsUnbuffered;
import com.atlassian.vcache.IdentifiedValue;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;

@PublicApi
public interface DirectExternalCache<V>
extends ExternalCache<V>,
ExternalWriteOperationsUnbuffered<V> {
    public CompletionStage<Optional<IdentifiedValue<V>>> getIdentified(String var1);

    public CompletionStage<IdentifiedValue<V>> getIdentified(String var1, Supplier<V> var2);

    default public CompletionStage<Map<String, Optional<IdentifiedValue<V>>>> getBulkIdentified(String ... keys) {
        return this.getBulkIdentified(Arrays.asList(keys));
    }

    public CompletionStage<Map<String, Optional<IdentifiedValue<V>>>> getBulkIdentified(Iterable<String> var1);

    public CompletionStage<Boolean> removeIf(String var1, CasIdentifier var2);

    public CompletionStage<Boolean> replaceIf(String var1, CasIdentifier var2, V var3);
}

