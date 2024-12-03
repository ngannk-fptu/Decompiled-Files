/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.core.Addressable;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import java.util.Collection;
import java.util.Collections;

public class AddressableExtractor
implements Extractor2 {
    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        boolean acceptableType = searchable instanceof Addressable && searchable instanceof Searchable && ((Searchable)searchable).isIndexable();
        return acceptableType ? Collections.singletonList(SearchFieldMappings.CHANGE_URL_PATH.createField(((Addressable)searchable).getUrlPath())) : Collections.emptyList();
    }
}

