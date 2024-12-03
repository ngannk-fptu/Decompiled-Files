/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintViolationException
 *  javax.validation.Validation
 *  javax.validation.Validator
 *  javax.validation.ValidatorFactory
 *  javax.validation.executable.ExecutableValidator
 */
package org.springframework.validation.beanvalidation;

import java.lang.reflect.Method;
import java.util.Set;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.validation.annotation.Validated;

public class MethodValidationInterceptor
implements MethodInterceptor {
    private final Validator validator;

    public MethodValidationInterceptor() {
        this(Validation.buildDefaultValidatorFactory());
    }

    public MethodValidationInterceptor(ValidatorFactory validatorFactory) {
        this(validatorFactory.getValidator());
    }

    public MethodValidationInterceptor(Validator validator) {
        this.validator = validator;
    }

    @Override
    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Set result;
        if (this.isFactoryBeanMetadataMethod(invocation.getMethod())) {
            return invocation.proceed();
        }
        Class[] groups = this.determineValidationGroups(invocation);
        ExecutableValidator execVal = this.validator.forExecutables();
        Method methodToValidate = invocation.getMethod();
        Object target = invocation.getThis();
        Assert.state(target != null, "Target must not be null");
        try {
            result = execVal.validateParameters(target, methodToValidate, invocation.getArguments(), groups);
        }
        catch (IllegalArgumentException ex) {
            methodToValidate = BridgeMethodResolver.findBridgedMethod(ClassUtils.getMostSpecificMethod(invocation.getMethod(), target.getClass()));
            result = execVal.validateParameters(target, methodToValidate, invocation.getArguments(), groups);
        }
        if (!result.isEmpty()) {
            throw new ConstraintViolationException(result);
        }
        Object returnValue = invocation.proceed();
        result = execVal.validateReturnValue(target, methodToValidate, returnValue, groups);
        if (!result.isEmpty()) {
            throw new ConstraintViolationException(result);
        }
        return returnValue;
    }

    private boolean isFactoryBeanMetadataMethod(Method method) {
        Class<?> clazz = method.getDeclaringClass();
        if (clazz.isInterface()) {
            return (clazz == FactoryBean.class || clazz == SmartFactoryBean.class) && !method.getName().equals("getObject");
        }
        Class factoryBeanType = null;
        if (SmartFactoryBean.class.isAssignableFrom(clazz)) {
            factoryBeanType = SmartFactoryBean.class;
        } else if (FactoryBean.class.isAssignableFrom(clazz)) {
            factoryBeanType = FactoryBean.class;
        }
        return factoryBeanType != null && !method.getName().equals("getObject") && ClassUtils.hasMethod(factoryBeanType, method);
    }

    protected Class<?>[] determineValidationGroups(MethodInvocation invocation) {
        Validated validatedAnn = AnnotationUtils.findAnnotation(invocation.getMethod(), Validated.class);
        if (validatedAnn == null) {
            Object target = invocation.getThis();
            Assert.state(target != null, "Target must not be null");
            validatedAnn = AnnotationUtils.findAnnotation(target.getClass(), Validated.class);
        }
        return validatedAnn != null ? validatedAnn.value() : new Class[]{};
    }
}

