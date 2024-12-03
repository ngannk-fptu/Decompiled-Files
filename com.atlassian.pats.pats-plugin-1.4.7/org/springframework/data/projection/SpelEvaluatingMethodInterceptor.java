/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.context.expression.BeanFactoryResolver
 *  org.springframework.context.expression.MapAccessor
 *  org.springframework.expression.BeanResolver
 *  org.springframework.expression.EvaluationContext
 *  org.springframework.expression.Expression
 *  org.springframework.expression.ParserContext
 *  org.springframework.expression.PropertyAccessor
 *  org.springframework.expression.common.TemplateParserContext
 *  org.springframework.expression.spel.standard.SpelExpressionParser
 *  org.springframework.expression.spel.support.StandardEvaluationContext
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.projection;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

class SpelEvaluatingMethodInterceptor
implements MethodInterceptor {
    private static final ParserContext PARSER_CONTEXT = new TemplateParserContext();
    private final EvaluationContext evaluationContext;
    private final MethodInterceptor delegate;
    private final Map<Integer, Expression> expressions;
    private final Object target;

    public SpelEvaluatingMethodInterceptor(MethodInterceptor delegate, Object target, @Nullable BeanFactory beanFactory, SpelExpressionParser parser, Class<?> targetInterface) {
        Assert.notNull((Object)delegate, (String)"Delegate MethodInterceptor must not be null!");
        Assert.notNull((Object)target, (String)"Target object must not be null!");
        Assert.notNull((Object)parser, (String)"SpelExpressionParser must not be null!");
        Assert.notNull(targetInterface, (String)"Target interface must not be null!");
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        if (target instanceof Map) {
            evaluationContext.addPropertyAccessor((PropertyAccessor)new MapAccessor());
        }
        if (beanFactory != null) {
            evaluationContext.setBeanResolver((BeanResolver)new BeanFactoryResolver(beanFactory));
        }
        this.expressions = SpelEvaluatingMethodInterceptor.potentiallyCreateExpressionsForMethodsOnTargetInterface(parser, targetInterface);
        this.evaluationContext = evaluationContext;
        this.delegate = delegate;
        this.target = target;
    }

    private static Map<Integer, Expression> potentiallyCreateExpressionsForMethodsOnTargetInterface(SpelExpressionParser parser, Class<?> targetInterface) {
        HashMap<Integer, Expression> expressions = new HashMap<Integer, Expression>();
        for (Method method : targetInterface.getMethods()) {
            if (!method.isAnnotationPresent(Value.class)) continue;
            Value value = method.getAnnotation(Value.class);
            if (!StringUtils.hasText((String)value.value())) {
                throw new IllegalStateException(String.format("@Value annotation on %s contains empty expression!", method));
            }
            expressions.put(method.hashCode(), parser.parseExpression(value.value(), PARSER_CONTEXT));
        }
        return Collections.unmodifiableMap(expressions);
    }

    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Expression expression = this.expressions.get(invocation.getMethod().hashCode());
        if (expression == null) {
            return this.delegate.invoke(invocation);
        }
        return expression.getValue(this.evaluationContext, (Object)TargetWrapper.of(this.target, invocation.getArguments()));
    }

    static final class TargetWrapper {
        private final Object target;
        private final Object[] args;

        private TargetWrapper(Object target, Object[] args) {
            this.target = target;
            this.args = args;
        }

        public static TargetWrapper of(Object target, Object[] args) {
            return new TargetWrapper(target, args);
        }

        public Object getTarget() {
            return this.target;
        }

        public Object[] getArgs() {
            return this.args;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof TargetWrapper)) {
                return false;
            }
            TargetWrapper that = (TargetWrapper)o;
            if (!ObjectUtils.nullSafeEquals((Object)this.target, (Object)that.target)) {
                return false;
            }
            return ObjectUtils.nullSafeEquals((Object)this.args, (Object)that.args);
        }

        public int hashCode() {
            int result = ObjectUtils.nullSafeHashCode((Object)this.target);
            result = 31 * result + ObjectUtils.nullSafeHashCode((Object[])this.args);
            return result;
        }

        public String toString() {
            return "SpelEvaluatingMethodInterceptor.TargetWrapper(target=" + this.getTarget() + ", args=" + Arrays.deepToString(this.getArgs()) + ")";
        }
    }
}

