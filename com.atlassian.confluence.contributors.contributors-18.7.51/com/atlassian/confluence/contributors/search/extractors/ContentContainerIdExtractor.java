/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Contained
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
import com.atlassian.confluence.pages.Contained;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.springframework.stereotype.Component;

@ExportAsService(value={Extractor2.class})
@Component
public class ContentContainerIdExtractor
extends AbstractContributionExtractor {
    public static final String CONTAINING_PAGE_ID = "containingPageId";

    public Collection<FieldDescriptor> extractFields(Object searchable) {
        ContentEntityObject container = null;
        if (searchable instanceof Contained) {
            container = ((Contained)searchable).getContainer();
        }
        if (container == null) {
            return null;
        }
        return ImmutableList.of((Object)new FieldDescriptor(CONTAINING_PAGE_ID, container.getIdAsString(), FieldDescriptor.Store.YES, FieldDescriptor.Index.NOT_ANALYZED));
    }
}

