/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aQute.bnd.annotation.spi.ServiceProvider
 *  javax.el.ELContext
 *  javax.el.ELResolver
 *  javax.el.ExpressionFactory
 *  javax.el.MethodExpression
 *  javax.el.ValueExpression
 */
package org.apache.el;

import aQute.bnd.annotation.spi.ServiceProvider;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import org.apache.el.ValueExpressionLiteral;
import org.apache.el.lang.ELSupport;
import org.apache.el.lang.ExpressionBuilder;
import org.apache.el.stream.StreamELResolverImpl;
import org.apache.el.util.ExceptionUtils;
import org.apache.el.util.MessageFactory;

@ServiceProvider(value=ExpressionFactory.class)
public class ExpressionFactoryImpl
extends ExpressionFactory {
    public Object coerceToType(Object obj, Class<?> type) {
        return ELSupport.coerceToType(null, obj, type);
    }

    public MethodExpression createMethodExpression(ELContext context, String expression, Class<?> expectedReturnType, Class<?>[] expectedParamTypes) {
        ExpressionBuilder builder = new ExpressionBuilder(expression, context);
        return builder.createMethodExpression(expectedReturnType, expectedParamTypes);
    }

    public ValueExpression createValueExpression(ELContext context, String expression, Class<?> expectedType) {
        if (expectedType == null) {
            throw new NullPointerException(MessageFactory.get("error.value.expectedType"));
        }
        ExpressionBuilder builder = new ExpressionBuilder(expression, context);
        return builder.createValueExpression(expectedType);
    }

    public ValueExpression createValueExpression(Object instance, Class<?> expectedType) {
        if (expectedType == null) {
            throw new NullPointerException(MessageFactory.get("error.value.expectedType"));
        }
        return new ValueExpressionLiteral(instance, expectedType);
    }

    public ELResolver getStreamELResolver() {
        return new StreamELResolverImpl();
    }

    static {
        ExceptionUtils.preload();
    }
}

