/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.search.contentnames;

import com.atlassian.confluence.search.actions.json.ContentNameMatch;
import com.atlassian.confluence.search.contentnames.Category;
import com.atlassian.confluence.search.contentnames.ContentNameSearchContext;
import com.atlassian.confluence.search.contentnames.ContentNameSearchSection;
import com.atlassian.confluence.search.contentnames.ContentNameSearchSectionSpec;
import com.atlassian.confluence.search.contentnames.ContentNameSearchSectionsProvider;
import com.atlassian.confluence.search.contentnames.ContentNameSearcher;
import com.atlassian.confluence.search.contentnames.QueryToken;
import com.atlassian.confluence.search.contentnames.ResultTemplate;
import com.atlassian.confluence.search.contentnames.SearchResult;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class DefaultContentNameSearchSectionsProvider
implements ContentNameSearchSectionsProvider {
    private static final String NEXT_UI_SEARCH = "next.ui.search";
    private ContentNameSearcher contentNameSearcher;
    private Supplier<Map<Category, ContentNameSearchSectionSpec>> searchSectionSpecsProvider;

    @Override
    public Collection<ContentNameSearchSection> getSections(List<QueryToken> queryTokens, ContentNameSearchContext context) {
        if (NEXT_UI_SEARCH.equalsIgnoreCase(context.getHttpServletRequest().getParameter("src"))) {
            return Collections.emptyList();
        }
        Map<Category, ContentNameSearchSectionSpec> sectionSpecs = this.searchSectionSpecsProvider.get();
        ResultTemplate resultTemplate = new ResultTemplate();
        if (Iterables.isEmpty(context.getTypes())) {
            sectionSpecs.forEach((category, spec) -> {
                if (spec.isDefault()) {
                    resultTemplate.addCategory(spec.getCategory(), context.getMaxPerCategory() > 0 ? context.getMaxPerCategory() : spec.getLimit());
                }
            });
        } else {
            int maxPerCategory = context.getMaxPerCategory() > 0 ? context.getMaxPerCategory() : 7;
            context.getTypes().forEach(type -> {
                if (StringUtils.isNotBlank((CharSequence)type)) {
                    resultTemplate.addCategory(Category.getCategory(type), maxPerCategory);
                }
            });
        }
        return this.createSections(sectionSpecs, this.contentNameSearcher.search(queryTokens, resultTemplate, context.getSpaceKey()));
    }

    private List<ContentNameSearchSection> createSections(Map<Category, ContentNameSearchSectionSpec> sectionSpecs, Map<Category, List<SearchResult>> rawResults) {
        ArrayList<ContentNameSearchSection> contentNameSearchSections = new ArrayList<ContentNameSearchSection>();
        rawResults.forEach((category, searchResultList) -> {
            Function<SearchResult, ContentNameMatch> transformer = ((ContentNameSearchSectionSpec)sectionSpecs.get(category)).getSearchResultTransformer();
            contentNameSearchSections.add(new ContentNameSearchSection(((ContentNameSearchSectionSpec)sectionSpecs.get(category)).getWeight(), searchResultList.stream().map(transformer).collect(Collectors.toList())));
        });
        contentNameSearchSections.sort(ContentNameSearchSection.COMPARATOR);
        return contentNameSearchSections;
    }

    public void setContentNameSearcher(ContentNameSearcher contentNameSearcher) {
        this.contentNameSearcher = contentNameSearcher;
    }

    public void setContentNameSearchSectionSpecsProvider(Supplier<Map<Category, ContentNameSearchSectionSpec>> contentNameSearchSectionSpecsProvider) {
        this.searchSectionSpecsProvider = contentNameSearchSectionSpecsProvider;
    }
}

