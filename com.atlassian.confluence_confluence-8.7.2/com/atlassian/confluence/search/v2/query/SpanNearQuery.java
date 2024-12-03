/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SearchPrimitive
public class SpanNearQuery
implements SearchQuery {
    public static final String KEY = "spanNear";
    private final String fieldName;
    private final List<String> fieldValues;
    private final int slop;
    private final boolean inOrder;
    private final float boost;

    public SpanNearQuery(String fieldName, List<String> fieldValues, int slop, boolean inOrder) {
        this(fieldName, fieldValues, slop, inOrder, 1.0f);
    }

    public SpanNearQuery(String fieldName, List<String> fieldValues, int slop, boolean inOrder, float boost) {
        this.fieldName = fieldName;
        this.fieldValues = fieldValues;
        this.slop = slop;
        this.inOrder = inOrder;
        this.boost = boost;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public List<String> getFieldValues() {
        return this.fieldValues;
    }

    public int getSlop() {
        return this.slop;
    }

    public boolean isInOrder() {
        return this.inOrder;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List<?> getParameters() {
        return Arrays.asList(this.fieldName, this.fieldValues, this.slop, this.inOrder);
    }

    @Override
    public float getBoost() {
        return this.boost;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SpanNearQuery)) {
            return false;
        }
        SpanNearQuery that = (SpanNearQuery)o;
        return this.getSlop() == that.getSlop() && this.isInOrder() == that.isInOrder() && Objects.equals(this.getFieldName(), that.getFieldName()) && Objects.equals(this.getFieldValues(), that.getFieldValues()) && this.getBoost() == that.getBoost();
    }

    public int hashCode() {
        return Objects.hash(this.getFieldName(), this.getFieldValues(), this.getSlop(), this.isInOrder(), Float.valueOf(this.getBoost()));
    }
}

