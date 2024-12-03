/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.format.Formatter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.geo.format;

import java.text.ParseException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metric;
import org.springframework.data.geo.Metrics;
import org.springframework.format.Formatter;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public enum DistanceFormatter implements Converter<String, Distance>,
Formatter<Distance>
{
    INSTANCE;

    private static final Map<String, Metric> SUPPORTED_METRICS;
    private static final String INVALID_DISTANCE = "Expected double amount optionally followed by a metrics abbreviation (%s) but got '%s'!";

    @Nullable
    public final Distance convert(String source) {
        return source == null ? null : DistanceFormatter.doConvert(source.trim().toLowerCase(Locale.US));
    }

    public String print(Distance distance, Locale locale) {
        return distance == null ? null : String.format("%s%s", distance.getValue(), distance.getUnit().toLowerCase(locale));
    }

    public Distance parse(String text, Locale locale) throws ParseException {
        return DistanceFormatter.doConvert(text.trim().toLowerCase(locale));
    }

    private static Distance doConvert(String source) {
        for (Map.Entry<String, Metric> metric : SUPPORTED_METRICS.entrySet()) {
            if (!source.endsWith(metric.getKey())) continue;
            return DistanceFormatter.fromString(source, metric);
        }
        try {
            return new Distance(Double.parseDouble(source));
        }
        catch (NumberFormatException o_O) {
            throw new IllegalArgumentException(String.format(INVALID_DISTANCE, StringUtils.collectionToCommaDelimitedString(SUPPORTED_METRICS.keySet()), source));
        }
    }

    private static Distance fromString(String source, Map.Entry<String, Metric> metric) {
        String amountString = source.substring(0, source.indexOf(metric.getKey()));
        try {
            return new Distance(Double.parseDouble(amountString), metric.getValue());
        }
        catch (NumberFormatException o_O) {
            throw new IllegalArgumentException(String.format(INVALID_DISTANCE, StringUtils.collectionToCommaDelimitedString(SUPPORTED_METRICS.keySet()), source));
        }
    }

    static {
        LinkedHashMap<String, Metrics> metrics = new LinkedHashMap<String, Metrics>();
        for (Metrics metric : Metrics.values()) {
            metrics.put(metric.getAbbreviation(), metric);
            metrics.put(metric.toString().toLowerCase(Locale.US), metric);
        }
        SUPPORTED_METRICS = Collections.unmodifiableMap(metrics);
    }
}

