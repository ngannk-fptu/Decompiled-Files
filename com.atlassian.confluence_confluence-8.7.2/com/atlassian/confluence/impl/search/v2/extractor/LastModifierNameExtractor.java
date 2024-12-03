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

public class LastModifierNameExtractor
implements Extractor2 {
    @Deprecated
    public static final String ANONYMOUS_LAST_MODIFIER_ID = "";

    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        ImmutableList.Builder resultBuilder = ImmutableList.builder();
        if (searchable instanceof ConfluenceEntityObject) {
            ConfluenceEntityObject ceo = (ConfluenceEntityObject)searchable;
            String lastModifier = ceo.getLastModifier() == null ? ANONYMOUS_LAST_MODIFIER_ID : ceo.getLastModifier().getKey().getStringValue();
            resultBuilder.add((Object)SearchFieldMappings.LAST_MODIFIER.createField(lastModifier));
        }
        return resultBuilder.build();
    }
}

