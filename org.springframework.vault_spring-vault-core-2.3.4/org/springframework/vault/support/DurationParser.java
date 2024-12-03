/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package org.springframework.vault.support;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class DurationParser {
    private static final Pattern PARSE_PATTERN = Pattern.compile("([0-9]+)(ns|us|ms|s|m|h|d)");
    private static final Pattern VERIFY_PATTERN = Pattern.compile("(([0-9]+)(ns|us|ms|s|m|h|d))+");

    @Nullable
    public static Duration parseDuration(String duration) {
        if (StringUtils.isEmpty((Object)duration)) {
            return null;
        }
        if ("0".equals(duration)) {
            return Duration.ZERO;
        }
        if (!VERIFY_PATTERN.matcher(duration.toLowerCase(Locale.ENGLISH)).matches()) {
            throw new IllegalArgumentException(String.format("Cannot parse '%s' into a Duration", duration));
        }
        Matcher matcher = PARSE_PATTERN.matcher(duration.toLowerCase(Locale.ENGLISH));
        Duration result = Duration.ZERO;
        while (matcher.find()) {
            String typ;
            int num = Integer.parseInt(matcher.group(1));
            switch (typ = matcher.group(2)) {
                case "ns": {
                    result = result.plus(Duration.ofNanos(num));
                    break;
                }
                case "us": {
                    result = result.plus(Duration.ofNanos(num * 1000));
                    break;
                }
                case "ms": {
                    result = result.plus(Duration.ofMillis(num));
                    break;
                }
                case "s": {
                    result = result.plus(Duration.ofSeconds(num));
                    break;
                }
                case "m": {
                    result = result.plus(Duration.ofMinutes(num));
                    break;
                }
                case "h": {
                    result = result.plus(Duration.ofHours(num));
                    break;
                }
                case "d": {
                    result = result.plus(Duration.ofDays(num));
                    break;
                }
                case "w": {
                    result = result.plus(Duration.ofDays(num * 7));
                }
            }
        }
        return result;
    }

    public static String formatDuration(Duration duration) {
        StringBuilder builder = new StringBuilder();
        for (TemporalUnit unit : duration.getUnits()) {
            if (unit == ChronoUnit.MINUTES) {
                builder.append(duration.get(unit)).append('m');
            }
            if (unit == ChronoUnit.HOURS) {
                builder.append(duration.get(unit)).append('h');
            }
            if (unit == ChronoUnit.SECONDS) {
                builder.append(duration.get(unit)).append('s');
            }
            if (unit == ChronoUnit.MILLIS) {
                builder.append(duration.get(unit)).append("ms");
            }
            if (unit != ChronoUnit.NANOS) continue;
            builder.append(duration.get(unit)).append("ns");
        }
        return builder.toString();
    }
}

