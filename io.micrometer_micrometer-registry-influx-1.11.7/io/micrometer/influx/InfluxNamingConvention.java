/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 *  io.micrometer.core.instrument.Meter$Type
 *  io.micrometer.core.instrument.config.NamingConvention
 */
package io.micrometer.influx;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.NamingConvention;
import java.util.regex.Pattern;

public class InfluxNamingConvention
implements NamingConvention {
    private static final Pattern PATTERN_SPECIAL_CHARACTERS = Pattern.compile("([, =\"])");
    private final NamingConvention delegate;

    public InfluxNamingConvention() {
        this(NamingConvention.snakeCase);
    }

    public InfluxNamingConvention(NamingConvention delegate) {
        this.delegate = delegate;
    }

    public String name(String name, Meter.Type type, @Nullable String baseUnit) {
        return this.escape(this.delegate.name(name, type, baseUnit).replace("=", "_"));
    }

    public String tagKey(String key) {
        if (key.equals("time")) {
            throw new IllegalArgumentException("'time' is an invalid tag key in InfluxDB");
        }
        return this.escape(this.delegate.tagKey(key));
    }

    public String tagValue(String value) {
        return this.escape(this.delegate.tagValue(value).replace('\n', ' '));
    }

    private String escape(String string) {
        return PATTERN_SPECIAL_CHARACTERS.matcher(string).replaceAll("\\\\$1");
    }
}

