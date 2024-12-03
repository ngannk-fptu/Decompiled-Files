/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.util.PerformanceSensitive
 *  org.apache.logging.log4j.util.StringBuilders
 */
package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.StringBuilders;

@Plugin(name="MarkerPatternConverter", category="Converter")
@ConverterKeys(value={"marker"})
@PerformanceSensitive(value={"allocation"})
public final class MarkerPatternConverter
extends LogEventPatternConverter {
    private MarkerPatternConverter(String[] options) {
        super("Marker", "marker");
    }

    public static MarkerPatternConverter newInstance(String[] options) {
        return new MarkerPatternConverter(options);
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        Marker marker = event.getMarker();
        if (marker != null) {
            StringBuilders.appendValue((StringBuilder)toAppendTo, (Object)marker);
        }
    }
}

