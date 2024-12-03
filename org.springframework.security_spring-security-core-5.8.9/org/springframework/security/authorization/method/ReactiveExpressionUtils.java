/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.EvaluationException
 *  org.springframework.expression.Expression
 *  reactor.core.publisher.Mono
 */
package org.springframework.security.authorization.method;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import reactor.core.publisher.Mono;

final class ReactiveExpressionUtils {
    static Mono<Boolean> evaluateAsBoolean(Expression expr, EvaluationContext ctx) {
        return Mono.defer(() -> {
            Object value;
            try {
                value = expr.getValue(ctx);
            }
            catch (EvaluationException ex) {
                return Mono.error(() -> new IllegalArgumentException("Failed to evaluate expression '" + expr.getExpressionString() + "'", ex));
            }
            if (value instanceof Boolean) {
                return Mono.just((Object)((Boolean)value));
            }
            if (value instanceof Mono) {
                Mono monoValue = (Mono)value;
                return monoValue.filter(Boolean.class::isInstance).map(Boolean.class::cast).switchIfEmpty(ReactiveExpressionUtils.createInvalidReturnTypeMono(expr));
            }
            return ReactiveExpressionUtils.createInvalidReturnTypeMono(expr);
        });
    }

    private static Mono<Boolean> createInvalidReturnTypeMono(Expression expr) {
        return Mono.error(() -> new IllegalStateException("Expression: '" + expr.getExpressionString() + "' must return boolean or Mono<Boolean>"));
    }

    private ReactiveExpressionUtils() {
    }
}

