/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.sort;

import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchSort;
import java.util.Objects;

@SearchPrimitive
public final class UserAttributeSort
implements SearchSort {
    public static final String KEY = "userAttributeSort";
    private final String fieldName;
    private final UserAttribute attribute;
    private final SearchSort.Order order;

    public UserAttributeSort(UserAttribute attribute, SearchSort.Order order, String fieldName) {
        this.order = Objects.requireNonNull(order);
        this.fieldName = Objects.requireNonNull(fieldName);
        this.attribute = Objects.requireNonNull(attribute);
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public SearchSort.Order getOrder() {
        return this.order;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public UserAttribute getAttribute() {
        return this.attribute;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserAttributeSort)) {
            return false;
        }
        UserAttributeSort that = (UserAttributeSort)o;
        return this.getFieldName().equals(that.getFieldName()) && this.getAttribute() == that.getAttribute() && this.getOrder() == that.getOrder();
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.getFieldName(), this.getAttribute(), this.getOrder()});
    }

    public static enum UserAttribute {
        USERKEY,
        USERNAME,
        FULLNAME,
        EMAIL;

    }
}

