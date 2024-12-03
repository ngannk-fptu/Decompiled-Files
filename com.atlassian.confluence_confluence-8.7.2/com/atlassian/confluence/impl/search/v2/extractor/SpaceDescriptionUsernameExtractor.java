/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.user.ConfluenceUser;
import com.google.common.collect.ImmutableList;
import java.util.Collection;

public class SpaceDescriptionUsernameExtractor
implements Extractor2 {
    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        ConfluenceUser user;
        ImmutableList.Builder resultBuilder = ImmutableList.builder();
        if (searchable instanceof SpaceDescription && (user = ((SpaceDescription)searchable).getSpace().getCreator()) != null) {
            resultBuilder.add((Object)SearchFieldMappings.USER_NAME.createField(user.getName()));
            if (user.getEmail() != null) {
                resultBuilder.add((Object)SearchFieldMappings.EMAIL.createField(user.getEmail()));
            }
            if (user.getFullName() != null) {
                resultBuilder.add((Object)SearchFieldMappings.FULL_NAME.createField(user.getFullName()));
                resultBuilder.add((Object)SearchFieldMappings.FULL_NAME_SORTABLE.createField(user.getFullName()));
            }
        }
        return resultBuilder.build();
    }
}

