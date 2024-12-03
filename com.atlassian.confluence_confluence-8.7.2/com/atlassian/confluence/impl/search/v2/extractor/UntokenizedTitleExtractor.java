/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import java.util.Collection;
import java.util.Collections;

public class UntokenizedTitleExtractor
implements Extractor2 {
    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        if (!(searchable instanceof ContentEntityObject)) {
            return Collections.emptyList();
        }
        String displayTitle = ((ContentEntityObject)searchable).getDisplayTitle();
        if (displayTitle == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(SearchFieldMappings.CONTENT_NAME_UNTOKENIZED.createField(displayTitle));
    }
}

