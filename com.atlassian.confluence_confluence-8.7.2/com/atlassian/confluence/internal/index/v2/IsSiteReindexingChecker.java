/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.google.common.base.Supplier
 */
package com.atlassian.confluence.internal.index.v2;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.internal.index.lucene.FullReindexManager;
import com.google.common.base.Supplier;
import java.util.Objects;

@Internal
public class IsSiteReindexingChecker
implements java.util.function.Supplier<Boolean> {
    private final Supplier<FullReindexManager> indexManagerSupplier;

    public IsSiteReindexingChecker(Supplier<FullReindexManager> indexManagerSupplier) {
        this.indexManagerSupplier = Objects.requireNonNull(indexManagerSupplier);
    }

    @Override
    public Boolean get() {
        return ((FullReindexManager)this.indexManagerSupplier.get()).isReIndexing();
    }
}

