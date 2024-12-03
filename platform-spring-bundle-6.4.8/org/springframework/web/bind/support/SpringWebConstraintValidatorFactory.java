/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorFactory
 */
package org.springframework.web.bind.support;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public class SpringWebConstraintValidatorFactory
implements ConstraintValidatorFactory {
    public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        return (T)((ConstraintValidator)this.getWebApplicationContext().getAutowireCapableBeanFactory().createBean(key));
    }

    public void releaseInstance(ConstraintValidator<?, ?> instance) {
        this.getWebApplicationContext().getAutowireCapableBeanFactory().destroyBean(instance);
    }

    protected WebApplicationContext getWebApplicationContext() {
        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        if (wac == null) {
            throw new IllegalStateException("No WebApplicationContext registered for current thread - consider overriding SpringWebConstraintValidatorFactory.getWebApplicationContext()");
        }
        return wac;
    }
}

