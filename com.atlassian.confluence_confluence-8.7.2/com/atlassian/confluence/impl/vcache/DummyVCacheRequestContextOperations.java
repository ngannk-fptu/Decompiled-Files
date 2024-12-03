/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.vcache;

import com.atlassian.confluence.vcache.VCacheRequestContextOperations;

public class DummyVCacheRequestContextOperations
implements VCacheRequestContextOperations {
    @Override
    public <T, X extends Throwable> T doInRequestContext(VCacheRequestContextOperations.Action<T, X> action) throws X {
        return action.perform();
    }

    @Override
    public <T, X extends Throwable> T doInRequestContext(String partitionIdentifier, VCacheRequestContextOperations.Action<T, X> action) throws X {
        return action.perform();
    }
}

