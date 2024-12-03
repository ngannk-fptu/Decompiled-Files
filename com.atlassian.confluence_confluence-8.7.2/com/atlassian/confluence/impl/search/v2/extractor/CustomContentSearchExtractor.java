/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import java.util.Collection;
import java.util.Collections;

public class CustomContentSearchExtractor
implements Extractor2 {
    @Deprecated
    public static final String FIELD_CONTENT_PLUGIN_KEY = SearchFieldMappings.CONTENT_PLUGIN_KEY.getName();

    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        if (!(searchable instanceof CustomContentEntityObject)) {
            return Collections.emptyList();
        }
        CustomContentEntityObject ceo = (CustomContentEntityObject)searchable;
        return Collections.singletonList(SearchFieldMappings.CONTENT_PLUGIN_KEY.createField(ceo.getPluginModuleKey()));
    }
}

