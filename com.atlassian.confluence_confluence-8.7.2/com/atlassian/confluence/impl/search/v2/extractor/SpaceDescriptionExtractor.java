/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Collections;

public class SpaceDescriptionExtractor
implements Extractor2 {
    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        SpaceDescription spaceDescription;
        if (searchable instanceof SpaceDescription && (spaceDescription = (SpaceDescription)searchable).getDisplayTitle() != null) {
            return ImmutableList.of((Object)SearchFieldMappings.CONTENT_NAME_UNSTEMMED.createField(spaceDescription.getDisplayTitle()));
        }
        return Collections.emptyList();
    }
}

