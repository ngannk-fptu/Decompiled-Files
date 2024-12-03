/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.validation.ConstraintViolation
 *  javax.validation.MessageInterpolator
 *  javax.validation.Validation
 *  javax.validation.Validator
 *  javax.validation.ValidatorFactory
 */
package com.atlassian.plugins.rest.common.validation;

import com.atlassian.plugins.rest.common.interceptor.MethodInvocation;
import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import com.atlassian.plugins.rest.common.validation.SalMessageInterpolator;
import com.atlassian.plugins.rest.common.validation.ValidationError;
import com.atlassian.plugins.rest.common.validation.ValidationErrors;
import com.atlassian.sal.api.message.I18nResolver;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.api.model.Parameter;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.core.Response;

public class ValidationInterceptor
implements ResourceInterceptor {
    private final ValidatorFactory factory;

    public ValidationInterceptor(I18nResolver i18nResolver) {
        this(new SalMessageInterpolator(i18nResolver));
    }

    public ValidationInterceptor(MessageInterpolator messageInterpolator) {
        this.factory = Validation.byDefaultProvider().configure().messageInterpolator(messageInterpolator).buildValidatorFactory();
    }

    @Override
    public void intercept(MethodInvocation invocation) throws IllegalAccessException, InvocationTargetException {
        Set constraintViolations;
        Validator validator = this.factory.getValidator();
        int entityIndex = -1;
        AbstractResourceMethod method = invocation.getMethod();
        for (int i = 0; i < method.getParameters().size(); ++i) {
            Parameter parameter = method.getParameters().get(i);
            if (Parameter.Source.ENTITY != parameter.getSource()) continue;
            entityIndex = i;
            break;
        }
        if (entityIndex > -1 && !(constraintViolations = validator.validate(invocation.getParameters()[entityIndex], new Class[0])).isEmpty()) {
            ValidationErrors errors = new ValidationErrors();
            for (ConstraintViolation violation : constraintViolations) {
                ValidationError error = new ValidationError();
                error.setMessage(violation.getMessage());
                error.setPath(violation.getPropertyPath().toString());
                errors.addError(error);
            }
            invocation.getHttpContext().getResponse().setResponse(Response.status(400).entity(errors).build());
            return;
        }
        invocation.invoke();
    }
}

