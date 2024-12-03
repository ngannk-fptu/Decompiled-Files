/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.interceptor.MethodInvocation
 *  com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.MultivaluedMap
 */
package com.atlassian.audit.rest.v1.validation;

import com.atlassian.audit.rest.v1.validation.QueryParamValidator;
import com.atlassian.audit.rest.v1.validation.ValidationResult;
import com.atlassian.audit.rest.v1.validation.Validator;
import com.atlassian.audit.rest.v1.validation.exception.InvalidQueryException;
import com.atlassian.plugins.rest.common.interceptor.MethodInvocation;
import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import java.lang.reflect.InvocationTargetException;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MultivaluedMap;

public class ValidationInterceptor
implements ResourceInterceptor {
    public void intercept(MethodInvocation methodInvocation) throws InvocationTargetException, IllegalAccessException {
        MultivaluedMap queryParameters = methodInvocation.getHttpContext().getUriInfo().getQueryParameters();
        ValidationResult validationResult = new ValidationResult();
        methodInvocation.getMethod().getParameters().stream().filter(p -> p.isAnnotationPresent(QueryParam.class)).filter(p -> p.isAnnotationPresent(Validator.class)).forEach(parameter -> {
            QueryParamValidator parameterValidator;
            Class<? extends QueryParamValidator> validatorClass = ((Validator)parameter.getAnnotation(Validator.class)).value();
            try {
                parameterValidator = validatorClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException("The class " + validatorClass + " should have default constructor");
            }
            String key = ((QueryParam)parameter.getAnnotation(QueryParam.class)).value();
            String parameterValue = (String)queryParameters.getFirst((Object)key);
            if (parameterValue != null) {
                parameterValidator.validate(parameterValue, validationResult);
            }
        });
        if (validationResult.hasErrors()) {
            throw new InvalidQueryException(validationResult);
        }
        methodInvocation.invoke();
    }
}

