/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;

public class SpaceKeyAndNameExtractor
implements Extractor2 {
    @Deprecated
    public static final String IN_SPACE_FIELD = SearchFieldMappings.IN_SPACE.getName();

    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        ImmutableList.Builder resultBuilder = ImmutableList.builder();
        Space space = null;
        if (searchable instanceof Spaced) {
            space = ((Spaced)searchable).getSpace();
        }
        if (space != null) {
            if (StringUtils.isNotBlank((CharSequence)space.getKey())) {
                resultBuilder.add((Object)SearchFieldMappings.SPACE_KEY.createField(space.getKey()));
            }
            if (StringUtils.isNotBlank((CharSequence)space.getName())) {
                resultBuilder.add((Object)SearchFieldMappings.SPACE_NAME.createField(space.getName()));
            }
        } else {
            resultBuilder.add((Object)SearchFieldMappings.IN_SPACE.createField(false));
        }
        return resultBuilder.build();
    }
}

