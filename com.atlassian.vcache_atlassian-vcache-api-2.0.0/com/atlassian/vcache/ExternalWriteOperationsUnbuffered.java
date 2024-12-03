/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.vcache;

import com.atlassian.annotations.PublicApi;
import com.atlassian.vcache.PutPolicy;
import java.util.Arrays;
import java.util.concurrent.CompletionStage;

@PublicApi
public interface ExternalWriteOperationsUnbuffered<V> {
    public CompletionStage<Boolean> put(String var1, V var2, PutPolicy var3);

    default public CompletionStage<Void> remove(String ... keys) {
        return this.remove(Arrays.asList(keys));
    }

    public CompletionStage<Void> remove(Iterable<String> var1);

    public CompletionStage<Void> removeAll();
}

