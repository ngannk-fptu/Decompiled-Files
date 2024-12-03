/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.PreDestroy
 *  javax.validation.ConstraintViolationException
 *  javax.validation.Validator
 *  org.osgi.framework.BundleContext
 *  org.osgi.util.tracker.ServiceTracker
 */
package com.atlassian.webhooks.internal;

import com.atlassian.webhooks.internal.Validator;
import java.util.HashSet;
import javax.annotation.PreDestroy;
import javax.validation.ConstraintViolationException;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class DefaultValidator
implements Validator {
    private final ServiceTracker<Object, Object> validatorTracker;

    public DefaultValidator(BundleContext bundleContext) {
        this.validatorTracker = new ServiceTracker(bundleContext, "javax.validation.Validator", null);
        this.validatorTracker.open();
    }

    @PreDestroy
    public void destroy() {
        this.validatorTracker.close();
    }

    @Override
    public <T> T validate(T target) {
        Object validator = this.validatorTracker.getService();
        if (validator == null) {
            return target;
        }
        HashSet validationErrors = new HashSet(((javax.validation.Validator)validator).validate(target, new Class[0]));
        if (!validationErrors.isEmpty()) {
            throw new ConstraintViolationException(validationErrors);
        }
        return target;
    }
}

