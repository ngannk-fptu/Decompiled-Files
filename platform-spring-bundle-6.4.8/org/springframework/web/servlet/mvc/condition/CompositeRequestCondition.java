/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.servlet.mvc.condition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

public class CompositeRequestCondition
extends AbstractRequestCondition<CompositeRequestCondition> {
    private final RequestConditionHolder[] requestConditions;

    public CompositeRequestCondition(RequestCondition<?> ... requestConditions) {
        this.requestConditions = this.wrap(requestConditions);
    }

    private CompositeRequestCondition(RequestConditionHolder[] requestConditions) {
        this.requestConditions = requestConditions;
    }

    private RequestConditionHolder[] wrap(RequestCondition<?> ... rawConditions) {
        RequestConditionHolder[] wrappedConditions = new RequestConditionHolder[rawConditions.length];
        for (int i2 = 0; i2 < rawConditions.length; ++i2) {
            wrappedConditions[i2] = new RequestConditionHolder(rawConditions[i2]);
        }
        return wrappedConditions;
    }

    @Override
    public boolean isEmpty() {
        return ObjectUtils.isEmpty(this.requestConditions);
    }

    public List<RequestCondition<?>> getConditions() {
        return this.unwrap();
    }

    private List<RequestCondition<?>> unwrap() {
        ArrayList result = new ArrayList();
        for (RequestConditionHolder holder : this.requestConditions) {
            result.add(holder.getCondition());
        }
        return result;
    }

    @Override
    protected Collection<?> getContent() {
        return !this.isEmpty() ? this.getConditions() : Collections.emptyList();
    }

    @Override
    protected String getToStringInfix() {
        return " && ";
    }

    private int getLength() {
        return this.requestConditions.length;
    }

    @Override
    public CompositeRequestCondition combine(CompositeRequestCondition other) {
        if (this.isEmpty() && other.isEmpty()) {
            return this;
        }
        if (other.isEmpty()) {
            return this;
        }
        if (this.isEmpty()) {
            return other;
        }
        this.assertNumberOfConditions(other);
        RequestConditionHolder[] combinedConditions = new RequestConditionHolder[this.getLength()];
        for (int i2 = 0; i2 < this.getLength(); ++i2) {
            combinedConditions[i2] = this.requestConditions[i2].combine(other.requestConditions[i2]);
        }
        return new CompositeRequestCondition(combinedConditions);
    }

    private void assertNumberOfConditions(CompositeRequestCondition other) {
        Assert.isTrue(this.getLength() == other.getLength(), () -> "Cannot combine CompositeRequestConditions with a different number of conditions. " + ObjectUtils.nullSafeToString(this.requestConditions) + " and " + ObjectUtils.nullSafeToString(other.requestConditions));
    }

    @Override
    @Nullable
    public CompositeRequestCondition getMatchingCondition(HttpServletRequest request) {
        if (this.isEmpty()) {
            return this;
        }
        RequestConditionHolder[] matchingConditions = new RequestConditionHolder[this.getLength()];
        for (int i2 = 0; i2 < this.getLength(); ++i2) {
            matchingConditions[i2] = this.requestConditions[i2].getMatchingCondition(request);
            if (matchingConditions[i2] != null) continue;
            return null;
        }
        return new CompositeRequestCondition(matchingConditions);
    }

    @Override
    public int compareTo(CompositeRequestCondition other, HttpServletRequest request) {
        if (this.isEmpty() && other.isEmpty()) {
            return 0;
        }
        if (this.isEmpty()) {
            return 1;
        }
        if (other.isEmpty()) {
            return -1;
        }
        this.assertNumberOfConditions(other);
        for (int i2 = 0; i2 < this.getLength(); ++i2) {
            int result = this.requestConditions[i2].compareTo(other.requestConditions[i2], request);
            if (result == 0) continue;
            return result;
        }
        return 0;
    }
}

