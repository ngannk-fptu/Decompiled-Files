/*
 * Decompiled with CFR 0.152.
 */
package javax.validation;

import java.io.InputStream;
import javax.validation.BootstrapConfiguration;
import javax.validation.ClockProvider;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.ValidatorFactory;
import javax.validation.valueextraction.ValueExtractor;

public interface Configuration<T extends Configuration<T>> {
    public T ignoreXmlConfiguration();

    public T messageInterpolator(MessageInterpolator var1);

    public T traversableResolver(TraversableResolver var1);

    public T constraintValidatorFactory(ConstraintValidatorFactory var1);

    public T parameterNameProvider(ParameterNameProvider var1);

    public T clockProvider(ClockProvider var1);

    public T addValueExtractor(ValueExtractor<?> var1);

    public T addMapping(InputStream var1);

    public T addProperty(String var1, String var2);

    public MessageInterpolator getDefaultMessageInterpolator();

    public TraversableResolver getDefaultTraversableResolver();

    public ConstraintValidatorFactory getDefaultConstraintValidatorFactory();

    public ParameterNameProvider getDefaultParameterNameProvider();

    public ClockProvider getDefaultClockProvider();

    public BootstrapConfiguration getBootstrapConfiguration();

    public ValidatorFactory buildValidatorFactory();
}

