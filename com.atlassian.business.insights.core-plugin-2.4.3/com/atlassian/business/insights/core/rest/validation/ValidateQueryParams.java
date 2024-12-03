/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.interceptor.MethodInvocation
 *  com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor
 *  com.sun.jersey.api.model.Parameter
 *  javax.annotation.Nonnull
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.MultivaluedMap
 *  javax.ws.rs.ext.Provider
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.business.insights.core.rest.validation;

import com.atlassian.business.insights.core.rest.exception.InvalidQueryParamException;
import com.atlassian.business.insights.core.rest.validation.QueryParamValidator;
import com.atlassian.business.insights.core.rest.validation.ValidationResult;
import com.atlassian.business.insights.core.rest.validation.Validator;
import com.atlassian.plugins.rest.common.interceptor.MethodInvocation;
import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import com.sun.jersey.api.model.Parameter;
import java.lang.reflect.InvocationTargetException;
import javax.annotation.Nonnull;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import org.apache.commons.lang3.StringUtils;

@Provider
public class ValidateQueryParams
implements ResourceInterceptor {
    public static final String BAD_REQUEST_MISSING_PARAM = "data-pipeline.api.rest.queryparam.declared.parameter.missing.value";

    public void intercept(MethodInvocation methodInvocation) throws IllegalAccessException, InvocationTargetException {
        MultivaluedMap queryParameters = methodInvocation.getHttpContext().getUriInfo().getQueryParameters();
        ValidationResult validationResult = new ValidationResult();
        methodInvocation.getMethod().getParameters().stream().filter(p -> p.isAnnotationPresent(QueryParam.class)).filter(p -> p.isAnnotationPresent(Validator.class)).forEach(parameter -> {
            QueryParamValidator parameterValidator = this.getQueryParamValidator((Parameter)parameter);
            QueryParam queryParamAnnotation = (QueryParam)parameter.getAnnotation(QueryParam.class);
            String key = queryParamAnnotation.value();
            String parameterValue = (String)queryParameters.getFirst((Object)key);
            if (parameterValue != null && this.validateFieldIsNotBlank(parameterValue, key, validationResult)) {
                parameterValidator.validate(parameterValue, validationResult);
            }
        });
        if (validationResult.hasErrors()) {
            throw new InvalidQueryParamException(validationResult);
        }
        methodInvocation.invoke();
    }

    private QueryParamValidator getQueryParamValidator(Parameter parameter) {
        QueryParamValidator parameterValidator;
        Validator validatorAnnotation = (Validator)parameter.getAnnotation(Validator.class);
        Class validatorClass = validatorAnnotation.value();
        try {
            parameterValidator = (QueryParamValidator)validatorClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("The class " + validatorClass + " should have default constructor");
        }
        return parameterValidator;
    }

    private boolean validateFieldIsNotBlank(@Nonnull String parameterValue, @Nonnull String parameterName, @Nonnull ValidationResult validationResult) {
        if (StringUtils.isBlank((CharSequence)parameterValue)) {
            validationResult.add(BAD_REQUEST_MISSING_PARAM, parameterName);
            return false;
        }
        return true;
    }
}

