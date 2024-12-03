/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.filter;

import org.radeox.filter.CacheFilter;
import org.radeox.filter.FilterPipe;
import org.radeox.filter.context.FilterContext;
import org.radeox.filter.regex.LocaleRegexTokenFilter;
import org.radeox.regex.MatchResult;
import org.radeox.util.Encoder;

public class EscapeFilter
extends LocaleRegexTokenFilter
implements CacheFilter {
    protected String getLocaleKey() {
        return "filter.escape";
    }

    public void handleMatch(StringBuffer buffer, MatchResult result, FilterContext context) {
        buffer.append(this.handleMatch(result, context));
    }

    public String handleMatch(MatchResult result, FilterContext context) {
        if (result.group(1) == null) {
            String match = result.group(2);
            if (match == null) {
                match = result.group(3);
            }
            if ("\\".equals(match)) {
                return "\\\\";
            }
            return Encoder.toEntity(match.charAt(0));
        }
        return "&#92;";
    }

    public String[] before() {
        return FilterPipe.FIRST_BEFORE;
    }
}

