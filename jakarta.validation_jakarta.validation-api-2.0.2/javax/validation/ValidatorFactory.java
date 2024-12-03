/*
 * Decompiled with CFR 0.152.
 */
package javax.validation;

import javax.validation.ClockProvider;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.Validator;
import javax.validation.ValidatorContext;

public interface ValidatorFactory
extends AutoCloseable {
    public Validator getValidator();

    public ValidatorContext usingContext();

    public MessageInterpolator getMessageInterpolator();

    public TraversableResolver getTraversableResolver();

    public ConstraintValidatorFactory getConstraintValidatorFactory();

    public ParameterNameProvider getParameterNameProvider();

    public ClockProvider getClockProvider();

    public <T> T unwrap(Class<T> var1);

    @Override
    public void close();
}

