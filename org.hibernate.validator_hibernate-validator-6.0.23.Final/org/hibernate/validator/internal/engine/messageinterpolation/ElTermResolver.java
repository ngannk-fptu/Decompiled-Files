/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ELException
 *  javax.el.ExpressionFactory
 *  javax.el.PropertyNotFoundException
 *  javax.el.ValueExpression
 *  javax.validation.MessageInterpolator$Context
 */
package org.hibernate.validator.internal.engine.messageinterpolation;

import java.lang.invoke.MethodHandles;
import java.util.Locale;
import java.util.Map;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.PropertyNotFoundException;
import javax.el.ValueExpression;
import javax.validation.MessageInterpolator;
import org.hibernate.validator.internal.engine.messageinterpolation.FormatterWrapper;
import org.hibernate.validator.internal.engine.messageinterpolation.TermResolver;
import org.hibernate.validator.internal.engine.messageinterpolation.el.SimpleELContext;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.hibernate.validator.messageinterpolation.HibernateMessageInterpolatorContext;

public class ElTermResolver
implements TermResolver {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final String VALIDATED_VALUE_NAME = "validatedValue";
    private final Locale locale;
    private final ExpressionFactory expressionFactory;

    public ElTermResolver(Locale locale, ExpressionFactory expressionFactory) {
        this.locale = locale;
        this.expressionFactory = expressionFactory;
    }

    @Override
    public String interpolate(MessageInterpolator.Context context, String expression) {
        String resolvedExpression = expression;
        SimpleELContext elContext = new SimpleELContext(this.expressionFactory);
        try {
            ValueExpression valueExpression = this.bindContextValues(expression, context, elContext);
            resolvedExpression = (String)valueExpression.getValue((ELContext)elContext);
        }
        catch (PropertyNotFoundException pnfe) {
            LOG.unknownPropertyInExpressionLanguage(expression, (Exception)((Object)pnfe));
        }
        catch (ELException e) {
            LOG.errorInExpressionLanguage(expression, (Exception)((Object)e));
        }
        catch (Exception e) {
            LOG.evaluatingExpressionLanguageExpressionCausedException(expression, e);
        }
        return resolvedExpression;
    }

    private ValueExpression bindContextValues(String messageTemplate, MessageInterpolator.Context messageInterpolatorContext, SimpleELContext elContext) {
        ValueExpression valueExpression = this.expressionFactory.createValueExpression(messageInterpolatorContext.getValidatedValue(), Object.class);
        elContext.getVariableMapper().setVariable(VALIDATED_VALUE_NAME, valueExpression);
        valueExpression = this.expressionFactory.createValueExpression((Object)new FormatterWrapper(this.locale), FormatterWrapper.class);
        elContext.getVariableMapper().setVariable("formatter", valueExpression);
        this.addVariablesToElContext(elContext, messageInterpolatorContext.getConstraintDescriptor().getAttributes());
        if (messageInterpolatorContext instanceof HibernateMessageInterpolatorContext) {
            this.addVariablesToElContext(elContext, ((HibernateMessageInterpolatorContext)messageInterpolatorContext).getExpressionVariables());
        }
        return this.expressionFactory.createValueExpression((ELContext)elContext, messageTemplate, String.class);
    }

    private void addVariablesToElContext(SimpleELContext elContext, Map<String, Object> variables) {
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            ValueExpression valueExpression = this.expressionFactory.createValueExpression(entry.getValue(), Object.class);
            elContext.getVariableMapper().setVariable(entry.getKey(), valueExpression);
        }
    }
}

