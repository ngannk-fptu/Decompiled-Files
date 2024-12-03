/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.business.insights.core.writer.convert;

import com.atlassian.business.insights.core.writer.convert.ValueConverter;
import java.util.regex.Matcher;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EmbeddedLineBreakValueConverter
implements ValueConverter {
    private static final String ALL_EOL_CHARS = "\\r\\n|\\r|\\n";
    private final String escapeChar;

    public EmbeddedLineBreakValueConverter(@Nonnull String escapeChar) {
        this.escapeChar = escapeChar;
    }

    @Override
    @Nullable
    public Object convert(@Nullable Object value) {
        if (value instanceof String) {
            return ((String)value).replaceAll(ALL_EOL_CHARS, Matcher.quoteReplacement(this.escapeChar));
        }
        return value;
    }
}

