/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.QueryUtil;
import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

@SearchPrimitive
public class WildcardTextFieldQuery
implements SearchQuery {
    public static final String KEY = "wildcardTextField";
    private String fieldName;
    private String rawQuery;
    private BooleanOperator operator;

    public WildcardTextFieldQuery(String fieldName, String rawQuery, BooleanOperator operator) {
        if (StringUtils.isBlank((CharSequence)fieldName)) {
            throw new IllegalArgumentException("Fieldname is required.");
        }
        if (StringUtils.isBlank((CharSequence)rawQuery)) {
            throw new IllegalArgumentException("Raw query is required.");
        }
        if (operator == null) {
            throw new IllegalArgumentException("Operator is required.");
        }
        this.rawQuery = rawQuery;
        this.fieldName = fieldName;
        this.operator = operator;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Arrays.asList(this.fieldName, this.rawQuery);
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getRawQuery() {
        return QueryUtil.escape(this.rawQuery);
    }

    public BooleanOperator getOperator() {
        return this.operator;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        WildcardTextFieldQuery other = (WildcardTextFieldQuery)obj;
        return this.fieldName.equals(other.fieldName) && this.operator.equals((Object)other.operator) && this.rawQuery.equals(other.rawQuery);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{1255, 337, this.fieldName, this.operator, this.rawQuery});
    }

    @Override
    public SearchQuery expand() {
        return this;
    }
}

