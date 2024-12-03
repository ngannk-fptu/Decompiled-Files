/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.config;

import io.micrometer.core.instrument.config.MeterRegistryConfig;
import io.micrometer.core.instrument.config.validate.Validated;
import io.micrometer.core.instrument.config.validate.ValidationException;
import java.util.Arrays;
import java.util.function.Function;

public class MeterRegistryConfigValidator {
    private MeterRegistryConfigValidator() {
    }

    @SafeVarargs
    public static <M extends MeterRegistryConfig> Validated<?> checkAll(M config, Function<M, ? extends Validated<?>> ... validation) {
        return Arrays.stream(validation).map(v -> (Validated)v.apply(config)).map(v -> v).reduce(Validated.none(), (v1, v2) -> v1.and((Validated<?>)v2));
    }

    public static <M extends MeterRegistryConfig, T> Function<M, Validated<T>> check(String property, Function<M, T> getter) {
        return config -> {
            try {
                return Validated.valid(config.prefix() + '.' + property, getter.apply(config));
            }
            catch (ValidationException e) {
                return e.getValidation().failures().iterator().next();
            }
        };
    }

    public static <M extends MeterRegistryConfig, T> Function<M, Validated<T>> checkRequired(String property, Function<M, T> getter) {
        return MeterRegistryConfigValidator.check(property, getter).andThen(Validated::required);
    }
}

