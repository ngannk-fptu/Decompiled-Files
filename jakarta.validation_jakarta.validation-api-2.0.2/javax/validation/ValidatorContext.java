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
import javax.validation.valueextraction.ValueExtractor;

public interface ValidatorContext {
    public ValidatorContext messageInterpolator(MessageInterpolator var1);

    public ValidatorContext traversableResolver(TraversableResolver var1);

    public ValidatorContext constraintValidatorFactory(ConstraintValidatorFactory var1);

    public ValidatorContext parameterNameProvider(ParameterNameProvider var1);

    public ValidatorContext clockProvider(ClockProvider var1);

    public ValidatorContext addValueExtractor(ValueExtractor<?> var1);

    public Validator getValidator();
}

