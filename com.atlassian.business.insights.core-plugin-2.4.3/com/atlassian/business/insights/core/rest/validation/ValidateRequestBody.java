/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.interceptor.MethodInvocation
 *  com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor
 *  io.swagger.v3.oas.annotations.parameters.RequestBody
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.business.insights.core.rest.validation;

import com.atlassian.business.insights.core.rest.exception.InvalidRequestBodyException;
import com.atlassian.business.insights.core.rest.validation.RequestBodyValidator;
import com.atlassian.business.insights.core.rest.validation.ValidationResult;
import com.atlassian.business.insights.core.rest.validation.Validator;
import com.atlassian.plugins.rest.common.interceptor.MethodInvocation;
import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import java.lang.reflect.InvocationTargetException;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidateRequestBody
implements ResourceInterceptor {
    public void intercept(MethodInvocation methodInvocation) throws IllegalAccessException, InvocationTargetException {
        Object[] bodyContent = methodInvocation.getParameters();
        ValidationResult validationResult = new ValidationResult();
        methodInvocation.getMethod().getParameters().stream().filter(p -> p.isAnnotationPresent(RequestBody.class)).filter(p -> p.isAnnotationPresent(Validator.class)).forEach(parameter -> {
            RequestBodyValidator requestBodyValidator;
            Validator validatorAnnotation = (Validator)parameter.getAnnotation(Validator.class);
            Class validatorClass = validatorAnnotation.value();
            try {
                requestBodyValidator = (RequestBodyValidator)validatorClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException("The class " + validatorClass + " should have default constructor");
            }
            requestBodyValidator.validate(bodyContent, validationResult);
        });
        if (validationResult.hasErrors()) {
            throw new InvalidRequestBodyException(validationResult);
        }
        methodInvocation.invoke();
    }
}

