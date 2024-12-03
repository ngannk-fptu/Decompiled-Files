/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.upload;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public final class UploadDateCalculator {
    private static final Random RANDOM = new Random();
    private static final int MORNING_HOURS = 6;

    private UploadDateCalculator() {
        throw new UnsupportedOperationException();
    }

    static Instant calculateUploadTime(Instant instant) {
        ZonedDateTime datetime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        if (datetime.getHour() >= 6) {
            return UploadDateCalculator.nextMidnight(datetime).plus((long)RANDOM.nextInt(60), ChronoUnit.MINUTES);
        }
        return UploadDateCalculator.nextMorning(datetime).plus((long)RANDOM.nextInt(60), ChronoUnit.MINUTES);
    }

    private static Instant nextMorning(ZonedDateTime dateTime) {
        return dateTime.with(ChronoField.HOUR_OF_DAY, 6L).with(ChronoField.MINUTE_OF_HOUR, 0L).with(ChronoField.SECOND_OF_MINUTE, 0L).with(ChronoField.MILLI_OF_SECOND, 0L).toInstant();
    }

    private static Instant nextMidnight(ZonedDateTime dateTime) {
        return dateTime.with(ChronoField.HOUR_OF_DAY, 0L).with(ChronoField.MINUTE_OF_HOUR, 0L).with(ChronoField.SECOND_OF_MINUTE, 0L).with(ChronoField.MILLI_OF_SECOND, 0L).toInstant().plus(1L, ChronoUnit.DAYS);
    }
}

