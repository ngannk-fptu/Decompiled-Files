/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.plugins.index.api.Extractor2
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor$Index
 *  com.atlassian.confluence.plugins.index.api.FieldDescriptor$Store
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.google.common.collect.ImmutableList
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.contributors.search.extractors;

import com.atlassian.confluence.contributors.search.extractors.AbstractContributionExtractor;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.springframework.stereotype.Component;

@ExportAsService(value={Extractor2.class})
@Component
public class CommentContributionExtractor
extends AbstractContributionExtractor {
    public static final String CONTAINING_PAGE_ID = "containingPageId";

    public Collection<FieldDescriptor> extractFields(Object searchable) {
        AbstractPage page;
        if (!(searchable instanceof Comment)) {
            return null;
        }
        try {
            ContentEntityObject owner = ((Comment)searchable).getContainer();
            if (!(owner instanceof AbstractPage)) {
                return null;
            }
            page = (AbstractPage)owner;
        }
        catch (IllegalStateException ise) {
            return null;
        }
        return ImmutableList.of((Object)new FieldDescriptor(CONTAINING_PAGE_ID, page.getIdAsString(), FieldDescriptor.Store.YES, FieldDescriptor.Index.NOT_ANALYZED));
    }
}

