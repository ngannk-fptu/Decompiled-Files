/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.internal.RequestContext
 *  com.atlassian.workcontext.api.ImmutableWorkContextReference
 */
package com.atlassian.vcache.internal.core;

import com.atlassian.vcache.internal.RequestContext;
import com.atlassian.vcache.internal.core.DefaultRequestContext;
import com.atlassian.workcontext.api.ImmutableWorkContextReference;
import java.util.Objects;
import java.util.function.Supplier;

public class WorkContextRequestContextSupplier
implements Supplier<RequestContext> {
    private ImmutableWorkContextReference<RequestContext> requestContextSupplier;

    public WorkContextRequestContextSupplier(Supplier<String> partitionIdentifier) {
        Objects.requireNonNull(partitionIdentifier);
        this.requestContextSupplier = new ImmutableWorkContextReference(() -> new DefaultRequestContext(partitionIdentifier));
    }

    @Override
    public RequestContext get() {
        return (RequestContext)this.requestContextSupplier.get();
    }
}

