/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.vcache;

import com.atlassian.annotations.PublicApi;
import com.atlassian.vcache.VCache;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;

@PublicApi
public interface ExternalCache<V>
extends VCache {
    public CompletionStage<Optional<V>> get(String var1);

    public CompletionStage<V> get(String var1, Supplier<V> var2);

    default public CompletionStage<Map<String, Optional<V>>> getBulk(String ... keys) {
        return this.getBulk(Arrays.asList(keys));
    }

    public CompletionStage<Map<String, Optional<V>>> getBulk(Iterable<String> var1);

    default public CompletionStage<Map<String, V>> getBulk(Function<Set<String>, Map<String, V>> factory, String ... keys) {
        return this.getBulk(factory, Arrays.asList(keys));
    }

    public CompletionStage<Map<String, V>> getBulk(Function<Set<String>, Map<String, V>> var1, Iterable<String> var2);
}

