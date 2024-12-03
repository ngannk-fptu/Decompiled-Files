/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorFactory
 */
package org.hibernate.validator.internal.engine.constraintvalidation;

import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import org.hibernate.validator.internal.util.privilegedactions.NewInstance;

public class ConstraintValidatorFactoryImpl
implements ConstraintValidatorFactory {
    public final <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
        return (T)((ConstraintValidator)this.run(NewInstance.action(key, "ConstraintValidator")));
    }

    public void releaseInstance(ConstraintValidator<?, ?> instance) {
    }

    private <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }
}

