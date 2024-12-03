/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.internal.search.contentnames.v2;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.search.contentnames.SearchResult;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.lucene.LuceneUtils;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableList;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;

@Internal
class FieldValuesMapper
implements Function<Function<String, String>, Optional<SearchResult>> {
    private final List<BiFunction<Function<String, String>, SearchResult, SearchResult>> decorators;

    FieldValuesMapper() {
        this.decorators = Collections.emptyList();
    }

    FieldValuesMapper(BiFunction<Function<String, String>, SearchResult, SearchResult> one) {
        this.decorators = ImmutableList.of(one);
    }

    FieldValuesMapper(BiFunction<Function<String, String>, SearchResult, SearchResult> one, BiFunction<Function<String, String>, SearchResult, SearchResult> other) {
        this.decorators = ImmutableList.of(one, other);
    }

    @Override
    public Optional<SearchResult> apply(Function<String, String> getFieldValue) {
        String modificationDate;
        Long id;
        String contentName = getFieldValue.apply(SearchFieldNames.CONTENT_NAME_UNSTEMMED);
        String urlPath = getFieldValue.apply(SearchFieldNames.URL_PATH);
        String contentType = getFieldValue.apply(SearchFieldNames.TYPE);
        String handle = getFieldValue.apply(SearchFieldNames.HANDLE);
        try {
            id = new HibernateHandle(handle).getId();
        }
        catch (ParseException e) {
            return Optional.empty();
        }
        SearchResult result = new SearchResult(id, contentName, urlPath, contentType);
        String creationDate = getFieldValue.apply(SearchFieldNames.CREATION_DATE);
        if (creationDate != null) {
            result.setCreatedDate(LuceneUtils.stringToDate(creationDate));
        }
        if ((modificationDate = getFieldValue.apply(SearchFieldNames.LAST_MODIFICATION_DATE)) != null) {
            result.setLastModifiedDate(LuceneUtils.stringToDate(modificationDate));
        }
        result.setLastModifierKey(FieldValuesMapper.userKey(getFieldValue.apply(SearchFieldNames.LAST_MODIFIER)));
        result.setCreatorKey(FieldValuesMapper.userKey(getFieldValue.apply(SearchFieldNames.CREATOR)));
        for (BiFunction<Function<String, String>, SearchResult, SearchResult> decorator : this.decorators) {
            result = decorator.apply(getFieldValue, result);
        }
        return Optional.of(result);
    }

    private static UserKey userKey(String value) {
        return StringUtils.isBlank((CharSequence)value) ? null : new UserKey(value);
    }
}

