/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.MessageInterpolator
 *  javax.validation.TraversableResolver
 *  javax.validation.Validation
 *  javax.validation.Validator
 *  javax.validation.ValidatorContext
 *  javax.validation.ValidatorFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 */
package org.springframework.validation.beanvalidation;

import javax.validation.MessageInterpolator;
import javax.validation.TraversableResolver;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.ValidatorFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.validation.beanvalidation.LocaleContextMessageInterpolator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

public class CustomValidatorBean
extends SpringValidatorAdapter
implements Validator,
InitializingBean {
    @Nullable
    private ValidatorFactory validatorFactory;
    @Nullable
    private MessageInterpolator messageInterpolator;
    @Nullable
    private TraversableResolver traversableResolver;

    public void setValidatorFactory(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    public void setMessageInterpolator(MessageInterpolator messageInterpolator) {
        this.messageInterpolator = messageInterpolator;
    }

    public void setTraversableResolver(TraversableResolver traversableResolver) {
        this.traversableResolver = traversableResolver;
    }

    public void afterPropertiesSet() {
        if (this.validatorFactory == null) {
            this.validatorFactory = Validation.buildDefaultValidatorFactory();
        }
        ValidatorContext validatorContext = this.validatorFactory.usingContext();
        MessageInterpolator targetInterpolator = this.messageInterpolator;
        if (targetInterpolator == null) {
            targetInterpolator = this.validatorFactory.getMessageInterpolator();
        }
        validatorContext.messageInterpolator((MessageInterpolator)new LocaleContextMessageInterpolator(targetInterpolator));
        if (this.traversableResolver != null) {
            validatorContext.traversableResolver(this.traversableResolver);
        }
        this.setTargetValidator(validatorContext.getValidator());
    }
}

