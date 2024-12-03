/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ValidatorFactory
 */
package org.hibernate.validator;

import java.time.Duration;
import javax.validation.ValidatorFactory;
import org.hibernate.validator.HibernateValidatorContext;
import org.hibernate.validator.Incubating;
import org.hibernate.validator.spi.scripting.ScriptEvaluatorFactory;

public interface HibernateValidatorFactory
extends ValidatorFactory {
    @Incubating
    public ScriptEvaluatorFactory getScriptEvaluatorFactory();

    @Incubating
    public Duration getTemporalValidationTolerance();

    public HibernateValidatorContext usingContext();
}

