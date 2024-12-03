/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import com.atlassian.confluence.util.GeneralUtil;
import com.google.common.collect.ImmutableList;
import java.util.Collection;

public class CommentExtractor
implements Extractor2 {
    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        ImmutableList.Builder resultBuilder = ImmutableList.builder();
        if (searchable instanceof Comment) {
            Comment comment = (Comment)searchable;
            ContentEntityObject owner = comment.getContainer();
            if (owner instanceof AbstractPage) {
                AbstractPage page = (AbstractPage)owner;
                resultBuilder.add((Object)SearchFieldMappings.PAGE_URL_PATH.createField(GeneralUtil.getIdBasedPageUrl(page)));
            }
            if (owner != null) {
                resultBuilder.add((Object)SearchFieldMappings.CONTAINER_CONTENT_TYPE.createField(owner.getType()));
                resultBuilder.add((Object)SearchFieldMappings.PAGE_DISPLAY_TITLE.createField(owner.getDisplayTitle()));
            }
        }
        return resultBuilder.build();
    }
}

