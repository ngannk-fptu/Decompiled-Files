/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.confluence.internal.search.contentnames.v2;

import com.atlassian.confluence.internal.search.contentnames.v2.AttachmentCategorySectionSpec;
import com.atlassian.confluence.internal.search.contentnames.v2.BlogCategorySectionSpec;
import com.atlassian.confluence.internal.search.contentnames.v2.ContentCategorySectionSpec;
import com.atlassian.confluence.internal.search.contentnames.v2.CustomCategorySectionSpec;
import com.atlassian.confluence.internal.search.contentnames.v2.PageCategorySectionSpec;
import com.atlassian.confluence.internal.search.contentnames.v2.PeopleCategorySectionSpec;
import com.atlassian.confluence.internal.search.contentnames.v2.PersonalSpaceCategorySectionSpec;
import com.atlassian.confluence.internal.search.contentnames.v2.SpaceCategorySectionSpec;
import com.atlassian.confluence.search.actions.json.ContentNameMatch;
import com.atlassian.confluence.search.contentnames.Category;
import com.atlassian.confluence.search.contentnames.ContentNameSearchSectionSpec;
import com.atlassian.confluence.search.contentnames.SearchResult;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class CoreContentNameSearchSectionSpecsProvider
implements Supplier<Map<Category, ContentNameSearchSectionSpec>> {
    @VisibleForTesting
    private Map<Category, ContentNameSearchSectionSpec> sectionSpecs;

    public CoreContentNameSearchSectionSpecsProvider(Function<SearchResult, ContentNameMatch> defaultSearchResultTransformer) {
        Objects.requireNonNull(defaultSearchResultTransformer);
        this.sectionSpecs = ImmutableMap.builder().put((Object)Category.ATTACHMENTS, (Object)new AttachmentCategorySectionSpec(defaultSearchResultTransformer)).put((Object)Category.PEOPLE, (Object)new PeopleCategorySectionSpec(defaultSearchResultTransformer)).put((Object)Category.CONTENT, (Object)new ContentCategorySectionSpec(defaultSearchResultTransformer)).put((Object)Category.BLOGS, (Object)new BlogCategorySectionSpec(defaultSearchResultTransformer)).put((Object)Category.PAGES, (Object)new PageCategorySectionSpec(defaultSearchResultTransformer)).put((Object)Category.SPACES, (Object)new SpaceCategorySectionSpec(defaultSearchResultTransformer)).put((Object)Category.PERSONAL_SPACE, (Object)new PersonalSpaceCategorySectionSpec(defaultSearchResultTransformer)).put((Object)Category.CUSTOM, (Object)new CustomCategorySectionSpec(defaultSearchResultTransformer)).build();
    }

    @Override
    public Map<Category, ContentNameSearchSectionSpec> get() {
        return this.sectionSpecs;
    }
}

