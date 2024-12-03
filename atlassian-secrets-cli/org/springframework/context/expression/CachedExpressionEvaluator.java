/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.expression;

import java.util.Map;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public abstract class CachedExpressionEvaluator {
    private final SpelExpressionParser parser;
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    protected CachedExpressionEvaluator(SpelExpressionParser parser) {
        Assert.notNull((Object)parser, "SpelExpressionParser must not be null");
        this.parser = parser;
    }

    protected CachedExpressionEvaluator() {
        this(new SpelExpressionParser());
    }

    protected SpelExpressionParser getParser() {
        return this.parser;
    }

    protected ParameterNameDiscoverer getParameterNameDiscoverer() {
        return this.parameterNameDiscoverer;
    }

    protected Expression getExpression(Map<ExpressionKey, Expression> cache, AnnotatedElementKey elementKey, String expression) {
        ExpressionKey expressionKey = this.createKey(elementKey, expression);
        Expression expr = cache.get(expressionKey);
        if (expr == null) {
            expr = this.getParser().parseExpression(expression);
            cache.put(expressionKey, expr);
        }
        return expr;
    }

    private ExpressionKey createKey(AnnotatedElementKey elementKey, String expression) {
        return new ExpressionKey(elementKey, expression);
    }

    protected static class ExpressionKey
    implements Comparable<ExpressionKey> {
        private final AnnotatedElementKey element;
        private final String expression;

        protected ExpressionKey(AnnotatedElementKey element, String expression) {
            Assert.notNull((Object)element, "AnnotatedElementKey must not be null");
            Assert.notNull((Object)expression, "Expression must not be null");
            this.element = element;
            this.expression = expression;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ExpressionKey)) {
                return false;
            }
            ExpressionKey otherKey = (ExpressionKey)other;
            return this.element.equals(otherKey.element) && ObjectUtils.nullSafeEquals(this.expression, otherKey.expression);
        }

        public int hashCode() {
            return this.element.hashCode() * 29 + this.expression.hashCode();
        }

        public String toString() {
            return this.element + " with expression \"" + this.expression + "\"";
        }

        @Override
        public int compareTo(ExpressionKey other) {
            int result = this.element.toString().compareTo(other.element.toString());
            if (result == 0) {
                result = this.expression.compareTo(other.expression);
            }
            return result;
        }
    }
}

