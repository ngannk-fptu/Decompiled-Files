/*
 * Decompiled with CFR 0.152.
 */
package javax.validation.spi;

import javax.validation.Configuration;
import javax.validation.ValidatorFactory;
import javax.validation.spi.BootstrapState;
import javax.validation.spi.ConfigurationState;

public interface ValidationProvider<T extends Configuration<T>> {
    public T createSpecializedConfiguration(BootstrapState var1);

    public Configuration<?> createGenericConfiguration(BootstrapState var1);

    public ValidatorFactory buildValidatorFactory(ConfigurationState var1);
}

