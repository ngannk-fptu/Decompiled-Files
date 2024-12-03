/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.servlet.mvc.condition;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.mvc.condition.AbstractNameValueExpression;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;
import org.springframework.web.util.WebUtils;

public final class ParamsRequestCondition
extends AbstractRequestCondition<ParamsRequestCondition> {
    private final Set<ParamExpression> expressions;

    public ParamsRequestCondition(String ... params) {
        this.expressions = ParamsRequestCondition.parseExpressions(params);
    }

    private static Set<ParamExpression> parseExpressions(String ... params) {
        if (ObjectUtils.isEmpty(params)) {
            return Collections.emptySet();
        }
        LinkedHashSet<ParamExpression> expressions = new LinkedHashSet<ParamExpression>(params.length);
        for (String param : params) {
            expressions.add(new ParamExpression(param));
        }
        return expressions;
    }

    private ParamsRequestCondition(Set<ParamExpression> conditions) {
        this.expressions = conditions;
    }

    public Set<NameValueExpression<String>> getExpressions() {
        return new LinkedHashSet<NameValueExpression<String>>(this.expressions);
    }

    @Override
    protected Collection<ParamExpression> getContent() {
        return this.expressions;
    }

    @Override
    protected String getToStringInfix() {
        return " && ";
    }

    @Override
    public ParamsRequestCondition combine(ParamsRequestCondition other) {
        if (this.isEmpty() && other.isEmpty()) {
            return this;
        }
        if (other.isEmpty()) {
            return this;
        }
        if (this.isEmpty()) {
            return other;
        }
        LinkedHashSet<ParamExpression> set = new LinkedHashSet<ParamExpression>(this.expressions);
        set.addAll(other.expressions);
        return new ParamsRequestCondition(set);
    }

    @Override
    @Nullable
    public ParamsRequestCondition getMatchingCondition(HttpServletRequest request) {
        for (ParamExpression expression : this.expressions) {
            if (expression.match(request)) continue;
            return null;
        }
        return this;
    }

    @Override
    public int compareTo(ParamsRequestCondition other, HttpServletRequest request) {
        int result = other.expressions.size() - this.expressions.size();
        if (result != 0) {
            return result;
        }
        return (int)(this.getValueMatchCount(other.expressions) - this.getValueMatchCount(this.expressions));
    }

    private long getValueMatchCount(Set<ParamExpression> expressions) {
        long count = 0L;
        for (ParamExpression e : expressions) {
            if (e.getValue() == null || e.isNegated()) continue;
            ++count;
        }
        return count;
    }

    static class ParamExpression
    extends AbstractNameValueExpression<String> {
        private final Set<String> namesToMatch = new HashSet<String>(WebUtils.SUBMIT_IMAGE_SUFFIXES.length + 1);

        ParamExpression(String expression) {
            super(expression);
            this.namesToMatch.add(this.getName());
            for (String suffix : WebUtils.SUBMIT_IMAGE_SUFFIXES) {
                this.namesToMatch.add(this.getName() + suffix);
            }
        }

        @Override
        protected boolean isCaseSensitiveName() {
            return true;
        }

        @Override
        protected String parseValue(String valueExpression) {
            return valueExpression;
        }

        @Override
        protected boolean matchName(HttpServletRequest request) {
            for (String current : this.namesToMatch) {
                if (request.getParameterMap().get(current) == null) continue;
                return true;
            }
            return request.getParameterMap().containsKey(this.name);
        }

        @Override
        protected boolean matchValue(HttpServletRequest request) {
            return ObjectUtils.nullSafeEquals(this.value, request.getParameter(this.name));
        }
    }
}

