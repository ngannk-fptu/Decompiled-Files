/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.search.v2.extractor;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.index.api.Extractor2;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.SearchFieldMappings;
import java.util.Collection;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomePageExtractor
implements Extractor2 {
    @Deprecated
    public static final String HOME_PAGE_FIELD = SearchFieldMappings.HOME_PAGE.getName();
    private static final Logger log = LoggerFactory.getLogger(HomePageExtractor.class);

    @Override
    public StringBuilder extractText(Object searchable) {
        return new StringBuilder();
    }

    @Override
    public Collection<FieldDescriptor> extractFields(Object searchable) {
        if (!(searchable instanceof Page)) {
            return Collections.emptyList();
        }
        Page page = (Page)searchable;
        if (!page.isLatestVersion()) {
            log.error("Page '{}' ({}) is not the latest version. Cannot determine if it's the homepage of the space.", (Object)page.getTitle(), (Object)page.getContentId());
            return Collections.emptyList();
        }
        if (!page.isHomePage()) {
            return Collections.emptyList();
        }
        return Collections.singletonList(SearchFieldMappings.HOME_PAGE.createField(true));
    }
}

