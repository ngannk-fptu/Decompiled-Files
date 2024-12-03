/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ValidationException
 */
package org.springframework.validation.beanvalidation;

import javax.validation.ValidationException;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class OptionalValidatorFactoryBean
extends LocalValidatorFactoryBean {
    @Override
    public void afterPropertiesSet() {
        try {
            super.afterPropertiesSet();
        }
        catch (ValidationException ex) {
            LogFactory.getLog(this.getClass()).debug("Failed to set up a Bean Validation provider", ex);
        }
    }
}

