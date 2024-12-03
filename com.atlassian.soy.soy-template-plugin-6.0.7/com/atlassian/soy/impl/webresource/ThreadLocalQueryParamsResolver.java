/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.soy.renderer.QueryParamsResolver
 *  com.google.common.base.Supplier
 *  javax.annotation.Nonnull
 */
package com.atlassian.soy.impl.webresource;

import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.soy.renderer.QueryParamsResolver;
import com.google.common.base.Supplier;
import javax.annotation.Nonnull;

public class ThreadLocalQueryParamsResolver
implements QueryParamsResolver {
    private final ThreadLocal<QueryParams> currentQueryParams = new ThreadLocal();

    @Nonnull
    public QueryParams get() {
        QueryParams queryParams = this.currentQueryParams.get();
        if (queryParams == null) {
            throw new IllegalThreadStateException(QueryParamsResolver.class.getName() + " cannot be invoked outside the context of a web resource transformation.");
        }
        return queryParams;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    <T> T withQueryParams(@Nonnull QueryParams value, Supplier<T> callback) {
        QueryParams original = this.currentQueryParams.get();
        this.currentQueryParams.set(value);
        try {
            Object object = callback.get();
            return (T)object;
        }
        finally {
            if (original == null) {
                this.currentQueryParams.remove();
            } else {
                this.currentQueryParams.set(original);
            }
        }
    }
}

