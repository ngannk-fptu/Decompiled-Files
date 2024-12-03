/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.MessageInterpolator$Context
 */
package org.hibernate.validator.internal.engine.messageinterpolation;

import java.util.Arrays;
import javax.validation.MessageInterpolator;
import org.hibernate.validator.internal.engine.messageinterpolation.TermResolver;
import org.hibernate.validator.messageinterpolation.HibernateMessageInterpolatorContext;

public class ParameterTermResolver
implements TermResolver {
    @Override
    public String interpolate(MessageInterpolator.Context context, String expression) {
        Object variable = this.getVariable(context, this.removeCurlyBraces(expression));
        String resolvedExpression = variable != null ? this.resolveExpression(variable) : expression;
        return resolvedExpression;
    }

    private Object getVariable(MessageInterpolator.Context context, String parameter) {
        Object variable;
        if (context instanceof HibernateMessageInterpolatorContext && (variable = ((HibernateMessageInterpolatorContext)context).getMessageParameters().get(parameter)) != null) {
            return variable;
        }
        return context.getConstraintDescriptor().getAttributes().get(parameter);
    }

    private String removeCurlyBraces(String parameter) {
        return parameter.substring(1, parameter.length() - 1);
    }

    private String resolveExpression(Object variable) {
        String resolvedExpression = variable.getClass().isArray() ? (variable.getClass() == boolean[].class ? Arrays.toString((boolean[])variable) : (variable.getClass() == char[].class ? Arrays.toString((char[])variable) : (variable.getClass() == byte[].class ? Arrays.toString((byte[])variable) : (variable.getClass() == short[].class ? Arrays.toString((short[])variable) : (variable.getClass() == int[].class ? Arrays.toString((int[])variable) : (variable.getClass() == long[].class ? Arrays.toString((long[])variable) : (variable.getClass() == float[].class ? Arrays.toString((float[])variable) : (variable.getClass() == double[].class ? Arrays.toString((double[])variable) : Arrays.toString((Object[])variable))))))))) : variable.toString();
        return resolvedExpression;
    }
}

