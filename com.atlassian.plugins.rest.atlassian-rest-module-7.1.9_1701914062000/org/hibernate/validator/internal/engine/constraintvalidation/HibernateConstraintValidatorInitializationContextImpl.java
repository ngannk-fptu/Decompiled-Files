/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ClockProvider
 */
package org.hibernate.validator.internal.engine.constraintvalidation;

import java.time.Duration;
import javax.validation.ClockProvider;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorInitializationContext;
import org.hibernate.validator.spi.scripting.ScriptEvaluator;
import org.hibernate.validator.spi.scripting.ScriptEvaluatorFactory;

public class HibernateConstraintValidatorInitializationContextImpl
implements HibernateConstraintValidatorInitializationContext {
    private final ScriptEvaluatorFactory scriptEvaluatorFactory;
    private final ClockProvider clockProvider;
    private final Duration temporalValidationTolerance;
    private final int hashCode;

    public HibernateConstraintValidatorInitializationContextImpl(ScriptEvaluatorFactory scriptEvaluatorFactory, ClockProvider clockProvider, Duration temporalValidationTolerance) {
        this.scriptEvaluatorFactory = scriptEvaluatorFactory;
        this.clockProvider = clockProvider;
        this.temporalValidationTolerance = temporalValidationTolerance;
        this.hashCode = this.createHashCode();
    }

    public static HibernateConstraintValidatorInitializationContextImpl of(HibernateConstraintValidatorInitializationContextImpl defaultContext, ScriptEvaluatorFactory scriptEvaluatorFactory, ClockProvider clockProvider, Duration temporalValidationTolerance) {
        if (scriptEvaluatorFactory == defaultContext.scriptEvaluatorFactory && clockProvider == defaultContext.clockProvider && temporalValidationTolerance.equals(defaultContext.temporalValidationTolerance)) {
            return defaultContext;
        }
        return new HibernateConstraintValidatorInitializationContextImpl(scriptEvaluatorFactory, clockProvider, temporalValidationTolerance);
    }

    @Override
    public ScriptEvaluator getScriptEvaluatorForLanguage(String languageName) {
        return this.scriptEvaluatorFactory.getScriptEvaluatorByLanguageName(languageName);
    }

    @Override
    public ClockProvider getClockProvider() {
        return this.clockProvider;
    }

    @Override
    public Duration getTemporalValidationTolerance() {
        return this.temporalValidationTolerance;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        HibernateConstraintValidatorInitializationContextImpl hibernateConstraintValidatorInitializationContextImpl = (HibernateConstraintValidatorInitializationContextImpl)o;
        if (this.scriptEvaluatorFactory != hibernateConstraintValidatorInitializationContextImpl.scriptEvaluatorFactory) {
            return false;
        }
        if (this.clockProvider != hibernateConstraintValidatorInitializationContextImpl.clockProvider) {
            return false;
        }
        return this.temporalValidationTolerance.equals(hibernateConstraintValidatorInitializationContextImpl.temporalValidationTolerance);
    }

    public int hashCode() {
        return this.hashCode;
    }

    private int createHashCode() {
        int result = System.identityHashCode(this.scriptEvaluatorFactory);
        result = 31 * result + System.identityHashCode(this.clockProvider);
        result = 31 * result + this.temporalValidationTolerance.hashCode();
        return result;
    }
}

