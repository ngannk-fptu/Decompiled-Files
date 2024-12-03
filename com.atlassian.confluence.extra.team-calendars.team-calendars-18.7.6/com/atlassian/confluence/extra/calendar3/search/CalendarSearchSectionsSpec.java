/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.search.actions.json.ContentNameMatch
 *  com.atlassian.confluence.search.contentnames.Category
 *  com.atlassian.confluence.search.contentnames.ContentNameSearchSectionSpec
 *  com.atlassian.confluence.search.contentnames.SearchResult
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.CustomContentTypeQuery
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.extra.calendar3.search;

import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.search.actions.json.ContentNameMatch;
import com.atlassian.confluence.search.contentnames.Category;
import com.atlassian.confluence.search.contentnames.ContentNameSearchSectionSpec;
import com.atlassian.confluence.search.contentnames.SearchResult;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.CustomContentTypeQuery;
import com.atlassian.confluence.util.GeneralUtil;
import com.google.common.collect.ImmutableSet;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CalendarSearchSectionsSpec
implements ContentNameSearchSectionSpec {
    private static final Category CALENDAR_CATEGORY = new Category("calendar");
    private static final int MAX_RESULTS = 3;
    private static final Set<String> FIELD_NAMES = ImmutableSet.of((Object)SearchFieldNames.TITLE, (Object)SearchFieldNames.TYPE, (Object)SearchFieldNames.URL_PATH, (Object)SearchFieldNames.HANDLE);
    private final ContextPathHolder contextPathHolder;
    private Function<List<Map<String, String>>, List<SearchResult>> fieldValueTransformer = list -> list.stream().map(fieldValues -> FieldValuesMapper.instance.apply(fieldValues::get)).filter(Optional::isPresent).map(Optional::get).limit(3L).collect(Collectors.toList());

    public CalendarSearchSectionsSpec(ContextPathHolder contextPathHolder) {
        this.contextPathHolder = Objects.requireNonNull(contextPathHolder);
    }

    public Category getCategory() {
        return CALENDAR_CATEGORY;
    }

    public boolean isDefault() {
        return true;
    }

    public SearchQuery getFilter() {
        return new CustomContentTypeQuery(new String[]{"com.atlassian.confluence.extra.team-calendars:calendar-content-type"});
    }

    public int getWeight() {
        return 50;
    }

    public int getLimit() {
        return 3;
    }

    public Set<String> getFields() {
        return FIELD_NAMES;
    }

    public Function<List<Map<String, String>>, List<SearchResult>> getFieldValuesTransformer() {
        return this.fieldValueTransformer;
    }

    public Function<SearchResult, ContentNameMatch> getSearchResultTransformer() {
        return x -> new ContentNameMatch("calendar-item", GeneralUtil.htmlEncode((String)x.getName()), this.contextPathHolder.getContextPath() + x.getUrl() + "?src=search");
    }

    private static class FieldValuesMapper
    implements Function<Function<String, String>, Optional<SearchResult>> {
        private static Function<Function<String, String>, Optional<SearchResult>> instance = new FieldValuesMapper();

        private FieldValuesMapper() {
        }

        @Override
        public Optional<SearchResult> apply(Function<String, String> getFieldValue) {
            long id;
            String contentName = getFieldValue.apply(SearchFieldNames.TITLE);
            String urlPath = getFieldValue.apply(SearchFieldNames.URL_PATH);
            String contentType = getFieldValue.apply(SearchFieldNames.TYPE);
            String handle = getFieldValue.apply(SearchFieldNames.HANDLE);
            try {
                id = FieldValuesMapper.getHandleId(handle);
            }
            catch (ParseException e) {
                return Optional.empty();
            }
            SearchResult result = new SearchResult(Long.valueOf(id), contentName, urlPath, contentType);
            return Optional.of(result);
        }

        private static long getHandleId(String handleString) throws ParseException {
            int idx = handleString.indexOf("-");
            if (idx < 0) {
                throw new ParseException("Handle separator not found in " + handleString, 0);
            }
            if (idx == 0) {
                throw new ParseException("Handle starts with separator in " + handleString, 0);
            }
            if (idx == handleString.length() - 1) {
                throw new ParseException("Handle without an id in " + handleString, idx);
            }
            try {
                return Long.parseLong(handleString.substring(idx + 1));
            }
            catch (NumberFormatException e) {
                throw new ParseException("Handle with an invalid id in " + handleString, idx);
            }
        }
    }
}

