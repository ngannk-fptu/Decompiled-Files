/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@SearchPrimitive
public class FieldExistsQuery
implements SearchQuery {
    public static final String KEY = "fieldExists";
    private final String fieldName;
    private final boolean negate;

    public FieldExistsQuery(String fieldName, boolean isNegate) {
        this.fieldName = fieldName;
        this.negate = isNegate;
    }

    public FieldExistsQuery(String fieldName) {
        this(fieldName, false);
    }

    public static FieldExistsQuery fieldNotExistsQuery(String fieldName) {
        return new FieldExistsQuery(fieldName, true);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Collections.EMPTY_LIST;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public boolean isNegate() {
        return this.negate;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        FieldExistsQuery that = (FieldExistsQuery)obj;
        return this.fieldName.equals(that.fieldName) && this.negate == that.negate;
    }

    public int hashCode() {
        return Objects.hash(this.fieldName, this.negate);
    }
}

