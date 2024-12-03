/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.search.v2.extractor;

import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import java.util.Collection;
import java.util.function.BiConsumer;
import javax.annotation.Nonnull;

public interface BulkExtractor<T> {
    public boolean canHandle(@Nonnull Class<?> var1);

    public void extractAll(@Nonnull Collection<T> var1, @Nonnull Class<? extends T> var2, @Nonnull BiConsumer<T, FieldDescriptor> var3);
}

