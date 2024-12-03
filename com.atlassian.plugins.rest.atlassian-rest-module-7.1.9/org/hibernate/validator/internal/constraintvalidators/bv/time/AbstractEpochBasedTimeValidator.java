/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidatorContext
 *  javax.validation.metadata.ConstraintDescriptor
 */
package org.hibernate.validator.internal.constraintvalidators.bv.time;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.time.Clock;
import java.time.Duration;
import javax.validation.ConstraintValidatorContext;
import javax.validation.metadata.ConstraintDescriptor;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidator;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorInitializationContext;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public abstract class AbstractEpochBasedTimeValidator<C extends Annotation, T>
implements HibernateConstraintValidator<C, T> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    protected Clock referenceClock;

    @Override
    public void initialize(ConstraintDescriptor<C> constraintDescriptor, HibernateConstraintValidatorInitializationContext initializationContext) {
        try {
            this.referenceClock = Clock.offset(initializationContext.getClockProvider().getClock(), this.getEffectiveTemporalValidationTolerance(initializationContext.getTemporalValidationTolerance()));
        }
        catch (Exception e) {
            throw LOG.getUnableToGetCurrentTimeFromClockProvider(e);
        }
    }

    public boolean isValid(T value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        int result = Long.compare(this.getEpochMillis(value, this.referenceClock), this.referenceClock.millis());
        return this.isValid(result);
    }

    protected abstract Duration getEffectiveTemporalValidationTolerance(Duration var1);

    protected abstract long getEpochMillis(T var1, Clock var2);

    protected abstract boolean isValid(int var1);
}

