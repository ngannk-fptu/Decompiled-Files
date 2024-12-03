/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.business.insights.core.rest.model;

import com.atlassian.business.insights.core.rest.exception.InvalidDayException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public enum Weekdays {
    MONDAY("mon"),
    TUESDAY("tue"),
    WEDNESDAY("wed"),
    THURSDAY("thu"),
    FRIDAY("fri"),
    SATURDAY("sat"),
    SUNDAY("sun");

    private static final Map<String, Weekdays> DAYS_MAP;
    private final String dayAbbreviation;

    private Weekdays(String dayAbbreviation) {
        this.dayAbbreviation = dayAbbreviation;
    }

    @JsonCreator
    public static Weekdays fromString(@Nonnull String dayStr) {
        try {
            return Optional.ofNullable(DAYS_MAP.get(dayStr.toLowerCase())).orElseGet(() -> Weekdays.valueOf(dayStr.toUpperCase()));
        }
        catch (Exception exception) {
            throw new InvalidDayException(dayStr);
        }
    }

    @JsonValue
    public String getAbbreviation() {
        return this.dayAbbreviation;
    }

    static {
        DAYS_MAP = Stream.of(Weekdays.values()).collect(Collectors.toMap(Weekdays::getAbbreviation, Function.identity()));
    }
}

