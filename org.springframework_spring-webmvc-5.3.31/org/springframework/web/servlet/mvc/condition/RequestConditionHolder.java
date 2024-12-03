/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.servlet.mvc.condition;

import java.util.Collection;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

public final class RequestConditionHolder
extends AbstractRequestCondition<RequestConditionHolder> {
    @Nullable
    private final RequestCondition<Object> condition;

    public RequestConditionHolder(@Nullable RequestCondition<?> requestCondition) {
        this.condition = requestCondition;
    }

    @Nullable
    public RequestCondition<?> getCondition() {
        return this.condition;
    }

    @Override
    protected Collection<?> getContent() {
        return this.condition != null ? Collections.singleton(this.condition) : Collections.emptyList();
    }

    @Override
    protected String getToStringInfix() {
        return " ";
    }

    @Override
    public RequestConditionHolder combine(RequestConditionHolder other) {
        if (this.condition == null && other.condition == null) {
            return this;
        }
        if (this.condition == null) {
            return other;
        }
        if (other.condition == null) {
            return this;
        }
        this.assertEqualConditionTypes(this.condition, other.condition);
        RequestCondition<Object> combined = this.condition.combine(other.condition);
        return new RequestConditionHolder(combined);
    }

    private void assertEqualConditionTypes(RequestCondition<?> thisCondition, RequestCondition<?> otherCondition) {
        Class<?> otherClazz;
        Class<?> clazz = thisCondition.getClass();
        if (!clazz.equals(otherClazz = otherCondition.getClass())) {
            throw new ClassCastException("Incompatible request conditions: " + clazz + " and " + otherClazz);
        }
    }

    @Override
    @Nullable
    public RequestConditionHolder getMatchingCondition(HttpServletRequest request) {
        if (this.condition == null) {
            return this;
        }
        RequestCondition match = (RequestCondition)this.condition.getMatchingCondition(request);
        return match != null ? new RequestConditionHolder(match) : null;
    }

    @Override
    public int compareTo(RequestConditionHolder other, HttpServletRequest request) {
        if (this.condition == null && other.condition == null) {
            return 0;
        }
        if (this.condition == null) {
            return 1;
        }
        if (other.condition == null) {
            return -1;
        }
        this.assertEqualConditionTypes(this.condition, other.condition);
        return this.condition.compareTo(other.condition, request);
    }
}

