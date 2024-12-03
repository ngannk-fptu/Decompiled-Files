/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.annotation.SdkInternalApi;

@SdkInternalApi
public enum TimestampFormat {
    ISO_8601("iso8601"),
    UNIX_TIMESTAMP("unixTimestamp"),
    UNIX_TIMESTAMP_IN_MILLIS("unixTimestampInMillis"),
    RFC_822("rfc822"),
    UNKNOWN("unknown");

    private final String format;

    private TimestampFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return this.format;
    }

    public static TimestampFormat fromValue(String format) {
        if (format == null) {
            return null;
        }
        for (TimestampFormat timestampFormat : TimestampFormat.values()) {
            if (!timestampFormat.format.equals(format)) continue;
            return timestampFormat;
        }
        throw new IllegalArgumentException("Unknown enum value for TimestampFormat : " + format);
    }
}

