/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.util.PerformanceSensitive
 */
package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.DatePatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(name="FileDatePatternConverter", category="FileConverter")
@ConverterKeys(value={"d", "date"})
@PerformanceSensitive(value={"allocation"})
public final class FileDatePatternConverter {
    private FileDatePatternConverter() {
    }

    public static PatternConverter newInstance(String[] options) {
        if (options == null || options.length == 0) {
            return DatePatternConverter.newInstance(new String[]{"yyyy-MM-dd"});
        }
        return DatePatternConverter.newInstance(options);
    }
}

