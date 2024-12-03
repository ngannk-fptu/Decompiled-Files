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

@PublicApi
public interface ExternalWriteOperationsBuffered<V> {
    public void put(String var1, V var2, PutPolicy var3);

    default public void remove(String ... keys) {
        this.remove(Arrays.asList(keys));
    }

    public void remove(Iterable<String> var1);

    public void removeAll();
}

