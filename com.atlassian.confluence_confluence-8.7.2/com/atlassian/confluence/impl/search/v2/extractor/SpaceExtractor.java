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
import com.atlassian.confluence.plugins.index.api.StringFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.TextFieldDescriptor;
import com.atlassian.confluence.spaces.Space;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;

public class SpaceExtractor
implements Extractor2 {
    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        if (!(searchable instanceof Space)) {
            return Collections.emptyList();
        }
        ImmutableList.Builder resultBuilder = ImmutableList.builder();
        Space space = (Space)searchable;
        if (StringUtils.isNotBlank((CharSequence)space.getKey())) {
            resultBuilder.add((Object)new StringFieldDescriptor("key", space.getKey(), FieldDescriptor.Store.YES));
        }
        if (StringUtils.isNotBlank((CharSequence)space.getName())) {
            resultBuilder.add((Object)new TextFieldDescriptor("name", space.getName(), FieldDescriptor.Store.YES));
        }
        return resultBuilder.build();
    }
}

