/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.common.util.StringUtils
 */
package io.micrometer.core.instrument.config.validate;

import io.micrometer.common.lang.Nullable;
import io.micrometer.common.util.StringUtils;
import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.config.validate.InvalidReason;
import io.micrometer.core.instrument.config.validate.Validated;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Incubating(since="1.5.0")
public enum DurationValidator {
    SIMPLE(new String[]{"^\\s*(([\\+]?\\d+)(\\.\\d*)?)\\s*([a-zA-Z]{0,2})\\s*", "^\\s*([\\+]?\\d{0,3}([_,]?\\d{3})*(\\.\\d*)?)\\s*([a-zA-Z]{0,2})\\s*"}){

        @Override
        protected Validated<Duration> doParse(String property, String value) {
            int e;
            Matcher matcher = ((Pattern)this.patterns.get(0)).matcher(value.toLowerCase().replaceAll("[,_\\s]", ""));
            if (!matcher.matches()) {
                return Validated.invalid(property, value, "must be a valid duration", InvalidReason.MALFORMED);
            }
            String unit = matcher.group(4);
            if (StringUtils.isBlank((String)unit)) {
                return Validated.invalid(property, value, "must have a valid duration unit", InvalidReason.MALFORMED);
            }
            Double amount = Double.valueOf(matcher.group(1));
            for (e = 0; e < 18 && Math.abs(amount - (double)amount.longValue()) > 1.0E-10; ++e) {
                amount = amount * 10.0;
            }
            long multipliedResult = amount.longValue();
            long multipliedFactor = (long)Math.pow(10.0, e);
            return 1.validateChronoUnit(property, value, unit).map(cu -> Duration.of(multipliedResult, cu).dividedBy(multipliedFactor));
        }
    }
    ,
    ISO8601(new String[]{"^[\\+\\-]?P.*$"}){

        @Override
        protected Validated<Duration> doParse(String property, String value) {
            try {
                return Validated.valid(property, Duration.parse(value));
            }
            catch (Exception ex) {
                return Validated.invalid(property, value, "must be a valid ISO-8601 duration like 'PT10S'", InvalidReason.MALFORMED, ex);
            }
        }
    };

    protected final List<Pattern> patterns;

    private DurationValidator(String ... patterns) {
        this.patterns = Arrays.stream(patterns).map(Pattern::compile).collect(Collectors.toList());
    }

    public static Validated<Duration> validate(String property, @Nullable String value) {
        return value == null ? Validated.valid(property, null) : DurationValidator.detect(property, value).flatMap(validator -> validator.doParse(property, value));
    }

    protected abstract Validated<Duration> doParse(String var1, String var2);

    private static Validated<DurationValidator> detect(String property, @Nullable String value) {
        if (value == null || StringUtils.isBlank((String)value)) {
            return Validated.invalid(property, value, "must be a valid duration value", value == null ? InvalidReason.MISSING : InvalidReason.MALFORMED);
        }
        for (DurationValidator candidate : DurationValidator.values()) {
            if (!candidate.patterns.stream().anyMatch(p -> p.matcher(value).matches())) continue;
            return Validated.valid(property, candidate);
        }
        return Validated.invalid(property, value, "must be a valid duration value", InvalidReason.MALFORMED);
    }

    public static Validated<TimeUnit> validateTimeUnit(String property, @Nullable String unit) {
        return DurationValidator.validateChronoUnit(property, unit, unit).flatMap(cu -> DurationValidator.toTimeUnit(property, cu));
    }

    public static Validated<ChronoUnit> validateChronoUnit(String property, @Nullable String value, @Nullable String unit) {
        if (unit == null) {
            return Validated.valid(property, null);
        }
        switch (unit.toLowerCase()) {
            case "ns": 
            case "nanoseconds": 
            case "nanosecond": 
            case "nanos": {
                return Validated.valid(property, ChronoUnit.NANOS);
            }
            case "us": 
            case "microseconds": 
            case "microsecond": 
            case "micros": {
                return Validated.valid(property, ChronoUnit.MICROS);
            }
            case "ms": 
            case "milliseconds": 
            case "millisecond": 
            case "millis": {
                return Validated.valid(property, ChronoUnit.MILLIS);
            }
            case "s": 
            case "seconds": 
            case "second": 
            case "secs": 
            case "sec": {
                return Validated.valid(property, ChronoUnit.SECONDS);
            }
            case "m": 
            case "minutes": 
            case "minute": 
            case "mins": 
            case "min": {
                return Validated.valid(property, ChronoUnit.MINUTES);
            }
            case "h": 
            case "hours": 
            case "hour": {
                return Validated.valid(property, ChronoUnit.HOURS);
            }
            case "d": 
            case "days": 
            case "day": {
                return Validated.valid(property, ChronoUnit.DAYS);
            }
        }
        return Validated.invalid(property, value, "must contain a valid time unit", InvalidReason.MALFORMED);
    }

    private static Validated<TimeUnit> toTimeUnit(String property, @Nullable ChronoUnit chronoUnit) {
        if (chronoUnit == null) {
            return Validated.valid(property, null);
        }
        switch (chronoUnit) {
            case NANOS: {
                return Validated.valid(property, TimeUnit.NANOSECONDS);
            }
            case MICROS: {
                return Validated.valid(property, TimeUnit.MICROSECONDS);
            }
            case MILLIS: {
                return Validated.valid(property, TimeUnit.MILLISECONDS);
            }
            case SECONDS: {
                return Validated.valid(property, TimeUnit.SECONDS);
            }
            case MINUTES: {
                return Validated.valid(property, TimeUnit.MINUTES);
            }
            case HOURS: {
                return Validated.valid(property, TimeUnit.HOURS);
            }
            case DAYS: {
                return Validated.valid(property, TimeUnit.DAYS);
            }
        }
        return Validated.invalid(property, chronoUnit.toString(), "must be a valid time unit", InvalidReason.MALFORMED);
    }
}

