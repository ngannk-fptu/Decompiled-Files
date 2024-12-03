/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorFactory
 */
package org.springframework.validation.beanvalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;

public class SpringConstraintValidatorFactory
implements ConstraintValidatorFactory {
    private final AutowireCapableBeanFactory beanFactory;

    public SpringConstraintValidatorFactory(AutowireCapableBeanFactory beanFactory) {
        Assert.notNull((Object)beanFactory, "BeanFactory must not be null");
        this.beanFactory = beanFactory;
    }

    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        return (T)((ConstraintValidator)this.beanFactory.createBean(key));
    }

    public void releaseInstance(ConstraintValidator<?, ?> instance) {
        this.beanFactory.destroyBean(instance);
    }
}

