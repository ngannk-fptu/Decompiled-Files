/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.util.PerformanceSensitive
 */
package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.NameAbbreviator;
import org.apache.logging.log4j.util.PerformanceSensitive;

@PerformanceSensitive(value={"allocation"})
public abstract class NamePatternConverter
extends LogEventPatternConverter {
    private final NameAbbreviator abbreviator;

    protected NamePatternConverter(String name, String style, String[] options) {
        super(name, style);
        this.abbreviator = options != null && options.length > 0 ? NameAbbreviator.getAbbreviator(options[0]) : NameAbbreviator.getDefaultAbbreviator();
    }

    protected final void abbreviate(String original, StringBuilder destination) {
        this.abbreviator.abbreviate(original, destination);
    }
}

