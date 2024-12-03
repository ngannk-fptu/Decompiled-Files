/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.google.common.collect.ImmutableList;
import java.util.Collection;

public class LastModifierNameContentExtractor
implements Extractor2 {
    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        ConfluenceEntityObject ceo;
        ImmutableList.Builder resultBuilder = ImmutableList.builder();
        if (searchable instanceof ConfluenceEntityObject && (ceo = (ConfluenceEntityObject)searchable).getLastModifier() != null) {
            resultBuilder.add((Object)SearchFieldMappings.LAST_MODIFIER.createField(ceo.getLastModifier().getKey().getStringValue()));
        }
        return resultBuilder.build();
    }
}

