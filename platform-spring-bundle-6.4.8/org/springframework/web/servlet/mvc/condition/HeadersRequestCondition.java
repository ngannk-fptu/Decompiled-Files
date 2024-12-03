/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.servlet.mvc.condition;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.mvc.condition.AbstractNameValueExpression;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;

public final class HeadersRequestCondition
extends AbstractRequestCondition<HeadersRequestCondition> {
    private static final HeadersRequestCondition PRE_FLIGHT_MATCH = new HeadersRequestCondition(new String[0]);
    private final Set<HeaderExpression> expressions;

    public HeadersRequestCondition(String ... headers) {
        this.expressions = HeadersRequestCondition.parseExpressions(headers);
    }

    private static Set<HeaderExpression> parseExpressions(String ... headers) {
        LinkedHashSet<HeaderExpression> result = null;
        if (!ObjectUtils.isEmpty(headers)) {
            for (String header : headers) {
                HeaderExpression expr = new HeaderExpression(header);
                if ("Accept".equalsIgnoreCase(expr.name) || "Content-Type".equalsIgnoreCase(expr.name)) continue;
                result = result != null ? result : new LinkedHashSet<HeaderExpression>(headers.length);
                result.add(expr);
            }
        }
        return result != null ? result : Collections.emptySet();
    }

    private HeadersRequestCondition(Set<HeaderExpression> conditions) {
        this.expressions = conditions;
    }

    public Set<NameValueExpression<String>> getExpressions() {
        return new LinkedHashSet<NameValueExpression<String>>(this.expressions);
    }

    @Override
    protected Collection<HeaderExpression> getContent() {
        return this.expressions;
    }

    @Override
    protected String getToStringInfix() {
        return " && ";
    }

    @Override
    public HeadersRequestCondition combine(HeadersRequestCondition other) {
        if (this.isEmpty() && other.isEmpty()) {
            return this;
        }
        if (other.isEmpty()) {
            return this;
        }
        if (this.isEmpty()) {
            return other;
        }
        LinkedHashSet<HeaderExpression> set = new LinkedHashSet<HeaderExpression>(this.expressions);
        set.addAll(other.expressions);
        return new HeadersRequestCondition(set);
    }

    @Override
    @Nullable
    public HeadersRequestCondition getMatchingCondition(HttpServletRequest request) {
        if (CorsUtils.isPreFlightRequest(request)) {
            return PRE_FLIGHT_MATCH;
        }
        for (HeaderExpression expression : this.expressions) {
            if (expression.match(request)) continue;
            return null;
        }
        return this;
    }

    @Override
    public int compareTo(HeadersRequestCondition other, HttpServletRequest request) {
        int result = other.expressions.size() - this.expressions.size();
        if (result != 0) {
            return result;
        }
        return (int)(this.getValueMatchCount(other.expressions) - this.getValueMatchCount(this.expressions));
    }

    private long getValueMatchCount(Set<HeaderExpression> expressions) {
        long count = 0L;
        for (HeaderExpression e : expressions) {
            if (e.getValue() == null || e.isNegated()) continue;
            ++count;
        }
        return count;
    }

    static class HeaderExpression
    extends AbstractNameValueExpression<String> {
        HeaderExpression(String expression) {
            super(expression);
        }

        @Override
        protected boolean isCaseSensitiveName() {
            return false;
        }

        @Override
        protected String parseValue(String valueExpression) {
            return valueExpression;
        }

        @Override
        protected boolean matchName(HttpServletRequest request) {
            return request.getHeader(this.name) != null;
        }

        @Override
        protected boolean matchValue(HttpServletRequest request) {
            return ObjectUtils.nullSafeEquals(this.value, request.getHeader(this.name));
        }
    }
}

