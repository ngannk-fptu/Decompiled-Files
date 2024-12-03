/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.QueryUtil;
import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@SearchPrimitive
public class TextSearchQuery
implements SearchQuery {
    public static final String KEY = "textSearch";
    private final String fieldName;
    private final String rawQuery;
    private final BooleanOperator operator;

    public TextSearchQuery(String fieldName, String query, BooleanOperator operator) {
        this.fieldName = fieldName;
        this.rawQuery = query;
        this.operator = operator;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Arrays.asList(new Serializable[]{this.fieldName, this.rawQuery, this.operator});
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getRawQuery() {
        return QueryUtil.escape(this.rawQuery);
    }

    public String getUnescapedQuery() {
        return this.rawQuery;
    }

    public BooleanOperator getOperator() {
        return this.operator;
    }
}

