/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.commons.collections4.CollectionUtils
 *  org.apache.commons.collections4.SetUtils
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.query.MultiTextFieldQuery;
import com.google.common.annotations.VisibleForTesting;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;

public class TextQuery
extends MultiTextFieldQuery {
    @VisibleForTesting
    static final Set<String> DEFAULT_SEARCH_FIELDS = Set.of(SearchFieldNames.TITLE, SearchFieldNames.LABEL_TEXT, SearchFieldNames.CONTENT, SearchFieldNames.ATTACHMENT_FILE_NAME, SearchFieldNames.PERSONAL_INFORMATION_USERNAME, SearchFieldNames.PERSONAL_INFORMATION_FULL_NAME, SearchFieldNames.PERSONAL_INFORMATION_EMAIL, SearchFieldNames.CONTENT_NAME_UNSTEMMED);

    public TextQuery(String query) {
        super(query, DEFAULT_SEARCH_FIELDS, BooleanOperator.AND);
    }

    public TextQuery(String query, Set<String> extraFields) {
        super(query, (Set<String>)(CollectionUtils.isEmpty(extraFields) ? DEFAULT_SEARCH_FIELDS : SetUtils.union(DEFAULT_SEARCH_FIELDS, extraFields)), BooleanOperator.AND);
    }
}

