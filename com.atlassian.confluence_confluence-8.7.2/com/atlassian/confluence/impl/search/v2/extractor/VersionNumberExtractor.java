/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import java.util.ArrayList;
import java.util.Collection;

public class VersionNumberExtractor
implements Extractor2 {
    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        ArrayList<FieldDescriptor> fieldDescriptors = new ArrayList<FieldDescriptor>();
        if (searchable instanceof ContentEntityObject) {
            ContentEntityObject content = (ContentEntityObject)searchable;
            fieldDescriptors.add(SearchFieldMappings.CONTENT_VERSION.createField(String.valueOf(content.getVersion())));
        }
        return fieldDescriptors;
    }
}

