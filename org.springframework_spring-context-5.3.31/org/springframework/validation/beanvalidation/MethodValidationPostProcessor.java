/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.Validator
 *  javax.validation.ValidatorFactory
 *  org.aopalliance.aop.Advice
 *  org.springframework.aop.Pointcut
 *  org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor
 *  org.springframework.aop.support.DefaultPointcutAdvisor
 *  org.springframework.aop.support.annotation.AnnotationMatchingPointcut
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.validation.beanvalidation;

import java.lang.annotation.Annotation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationInterceptor;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

public class MethodValidationPostProcessor
extends AbstractBeanFactoryAwareAdvisingPostProcessor
implements InitializingBean {
    private Class<? extends Annotation> validatedAnnotationType = Validated.class;
    @Nullable
    private Validator validator;

    public void setValidatedAnnotationType(Class<? extends Annotation> validatedAnnotationType) {
        Assert.notNull(validatedAnnotationType, (String)"'validatedAnnotationType' must not be null");
        this.validatedAnnotationType = validatedAnnotationType;
    }

    public void setValidator(Validator validator) {
        this.validator = validator instanceof LocalValidatorFactoryBean ? ((LocalValidatorFactoryBean)validator).getValidator() : (validator instanceof SpringValidatorAdapter ? (Validator)validator.unwrap(Validator.class) : validator);
    }

    public void setValidatorFactory(ValidatorFactory validatorFactory) {
        this.validator = validatorFactory.getValidator();
    }

    public void afterPropertiesSet() {
        AnnotationMatchingPointcut pointcut = new AnnotationMatchingPointcut(this.validatedAnnotationType, true);
        this.advisor = new DefaultPointcutAdvisor((Pointcut)pointcut, this.createMethodValidationAdvice(this.validator));
    }

    protected Advice createMethodValidationAdvice(@Nullable Validator validator) {
        return validator != null ? new MethodValidationInterceptor(validator) : new MethodValidationInterceptor();
    }
}

