/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.apache.logging.log4j.core.LogEvent
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 *  org.apache.logging.log4j.core.pattern.ConverterKeys
 *  org.apache.logging.log4j.core.pattern.LogEventPatternConverter
 */
package com.atlassian.logging.log4j.layout.patterns;

import com.atlassian.logging.log4j.FqNameCollapser;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(name="CategoryCollapsingPatternConverter", category="Converter")
@ConverterKeys(value={"q", "Q"})
public class CategoryCollapsingPatternConverter
extends LogEventPatternConverter {
    Map<String, FqNameCollapser.Strategy> strategyOptionMap = ImmutableMap.of((Object)"length", (Object)((Object)FqNameCollapser.Strategy.PACKAGE_LENGTH), (Object)"segments", (Object)((Object)FqNameCollapser.Strategy.PACKAGE_SEGMENTS), (Object)"l", (Object)((Object)FqNameCollapser.Strategy.PACKAGE_LENGTH), (Object)"s", (Object)((Object)FqNameCollapser.Strategy.PACKAGE_SEGMENTS));
    private final FqNameCollapser fqNameCollapser;

    public CategoryCollapsingPatternConverter(String[] options) {
        super("CategoryCollapsingPatternConverter", "CategoryCollapsingPatternConverter");
        this.fqNameCollapser = new FqNameCollapser(this.extractPrecisionOption(options), this.extractStrategyOption(options));
    }

    public static CategoryCollapsingPatternConverter newInstance(String[] options) {
        return new CategoryCollapsingPatternConverter(options);
    }

    public void format(LogEvent event, StringBuilder toAppendTo) {
        toAppendTo.append(this.fqNameCollapser.collapse(event.getLoggerName()));
    }

    private int extractPrecisionOption(String[] options) {
        int collapsePrecision = -1;
        int optionsCount = options.length;
        if (optionsCount >= 1) {
            try {
                collapsePrecision = Integer.parseInt(options[0]);
            }
            catch (NumberFormatException exception) {
                return collapsePrecision;
            }
        }
        return collapsePrecision;
    }

    private FqNameCollapser.Strategy extractStrategyOption(String[] options) {
        FqNameCollapser.Strategy strategy = FqNameCollapser.Strategy.PACKAGE_SEGMENTS;
        int optionsCount = options.length;
        if (optionsCount >= 2) {
            String strategyString = options[1];
            strategy = this.strategyOptionMap.getOrDefault(strategyString, FqNameCollapser.Strategy.PACKAGE_SEGMENTS);
        }
        return strategy;
    }
}

