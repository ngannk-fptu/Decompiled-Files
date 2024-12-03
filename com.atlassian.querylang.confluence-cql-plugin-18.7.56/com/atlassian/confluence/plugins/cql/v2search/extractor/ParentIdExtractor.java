/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.plugins.index.api.Extractor2
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor$Store
 *  com.atlassian.confluence.plugins.index.api.StringFieldDescriptor
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.cql.v2search.extractor;

import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.StringFieldDescriptor;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.springframework.stereotype.Component;

@ExportAsService(value={Extractor2.class})
@Component
public class ParentIdExtractor
implements Extractor2 {
    public static final String PAGE_OR_COMMENT_PARENT_ID = "page-or-comment-parentId";

    public StringBuilder extractText(Object o) {
        return new StringBuilder();
    }

    public Collection<FieldDescriptor> extractFields(Object searchable) {
        Comment parent;
        ImmutableList.Builder resultBuilder = ImmutableList.builder();
        long parentId = -1L;
        if (searchable == null) {
            return resultBuilder.build();
        }
        if (searchable instanceof Comment && (parent = ((Comment)searchable).getParent()) != null) {
            parentId = parent.getId();
        }
        if (searchable instanceof Page && (parent = ((Page)searchable).getParent()) != null) {
            parentId = parent.getId();
        }
        if (parentId != -1L) {
            resultBuilder.add((Object)new StringFieldDescriptor(PAGE_OR_COMMENT_PARENT_ID, String.valueOf(parentId), FieldDescriptor.Store.YES));
        }
        return resultBuilder.build();
    }
}

