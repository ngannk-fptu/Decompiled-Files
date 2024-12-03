/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.internal.index.v2;

import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.extractor.BulkExtractor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class BulkExtractor2Adapter
implements Extractor2 {
    private final BulkExtractor<Object> bulkExtractor;

    BulkExtractor2Adapter(BulkExtractor<Object> bulkExtractor) {
        this.bulkExtractor = bulkExtractor;
    }

    @Override
    @Nonnull
    public Collection<FieldDescriptor> extractFields(@Nonnull Object searchable) {
        if (this.bulkExtractor.canHandle(searchable.getClass())) {
            ArrayList<FieldDescriptor> fieldDescriptors = new ArrayList<FieldDescriptor>();
            this.bulkExtractor.extractAll(Collections.singleton(searchable), searchable.getClass(), (v, fieldDescriptor) -> fieldDescriptors.add((FieldDescriptor)fieldDescriptor));
            return fieldDescriptors;
        }
        return Collections.emptySet();
    }

    @Override
    @Nullable
    public StringBuilder extractText(Object searchable) {
        return null;
    }
}

