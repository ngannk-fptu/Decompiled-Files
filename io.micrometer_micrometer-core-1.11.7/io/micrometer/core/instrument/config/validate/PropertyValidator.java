/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.config.validate;

import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.config.MeterRegistryConfig;
import io.micrometer.core.instrument.config.validate.DurationValidator;
import io.micrometer.core.instrument.config.validate.InvalidReason;
import io.micrometer.core.instrument.config.validate.Validated;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Incubating(since="1.5.0")
public class PropertyValidator {
    private PropertyValidator() {
    }

    public static Validated<Duration> getDuration(MeterRegistryConfig config, String property) {
        String prefixedProperty = PropertyValidator.prefixedProperty(config, property);
        return DurationValidator.validate(prefixedProperty, config.get(prefixedProperty));
    }

    public static Validated<TimeUnit> getTimeUnit(MeterRegistryConfig config, String property) {
        String prefixedProperty = PropertyValidator.prefixedProperty(config, property);
        return DurationValidator.validateTimeUnit(prefixedProperty, config.get(prefixedProperty));
    }

    public static Validated<Integer> getInteger(MeterRegistryConfig config, String property) {
        String prefixedProperty = PropertyValidator.prefixedProperty(config, property);
        String value = config.get(prefixedProperty);
        try {
            return Validated.valid(prefixedProperty, value == null ? null : Integer.valueOf(value));
        }
        catch (NumberFormatException e) {
            return Validated.invalid(prefixedProperty, value, "must be an integer", InvalidReason.MALFORMED, e);
        }
    }

    public static <E extends Enum<E>> Validated<E> getEnum(MeterRegistryConfig config, Class<E> enumClass, String property) {
        String prefixedProperty = PropertyValidator.prefixedProperty(config, property);
        String value = config.get(prefixedProperty);
        if (value == null) {
            return Validated.valid(prefixedProperty, null);
        }
        try {
            Enum[] values;
            for (Enum enumValue : values = (Enum[])enumClass.getDeclaredMethod("values", new Class[0]).invoke(enumClass, new Object[0])) {
                if (!enumValue.name().equalsIgnoreCase(value)) continue;
                return Validated.valid(prefixedProperty, enumValue);
            }
            return Validated.invalid(prefixedProperty, value, "should be one of " + Arrays.stream(values).map(v -> '\'' + v.name() + '\'').collect(Collectors.joining(", ")), InvalidReason.MALFORMED);
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Validated<Boolean> getBoolean(MeterRegistryConfig config, String property) {
        String prefixedProperty;
        String value = config.get(prefixedProperty = PropertyValidator.prefixedProperty(config, property));
        return Validated.valid(prefixedProperty, value == null ? null : Boolean.valueOf(value));
    }

    public static Validated<String> getSecret(MeterRegistryConfig config, String property) {
        String prefixedProperty = PropertyValidator.prefixedProperty(config, property);
        return Validated.validSecret(prefixedProperty, config.get(prefixedProperty));
    }

    public static Validated<String> getString(MeterRegistryConfig config, String property) {
        String prefixedProperty = PropertyValidator.prefixedProperty(config, property);
        return Validated.valid(prefixedProperty, config.get(prefixedProperty));
    }

    public static Validated<String> getUrlString(MeterRegistryConfig config, String property) {
        String prefixedProperty = PropertyValidator.prefixedProperty(config, property);
        String value = config.get(prefixedProperty);
        try {
            return Validated.valid(prefixedProperty, value == null ? null : URI.create(value).toURL()).map(url -> value);
        }
        catch (IllegalArgumentException | MalformedURLException ex) {
            return Validated.invalid(prefixedProperty, value, "must be a valid URL", InvalidReason.MALFORMED, ex);
        }
    }

    public static Validated<String> getUriString(MeterRegistryConfig config, String property) {
        String prefixedProperty = PropertyValidator.prefixedProperty(config, property);
        String value = config.get(prefixedProperty);
        try {
            return Validated.valid(prefixedProperty, value == null ? null : URI.create(value)).map(uri -> value);
        }
        catch (IllegalArgumentException ex) {
            return Validated.invalid(prefixedProperty, value, "must be a valid URI", InvalidReason.MALFORMED, ex);
        }
    }

    private static String prefixedProperty(MeterRegistryConfig config, String property) {
        return config.prefix() + '.' + property;
    }
}

