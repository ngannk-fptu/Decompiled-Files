/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.filter;

import java.util.Map;
import org.radeox.filter.context.FilterContext;
import org.radeox.filter.regex.LocaleRegexTokenFilter;
import org.radeox.regex.MatchResult;

public class ParamFilter
extends LocaleRegexTokenFilter {
    public void handleMatch(StringBuffer buffer, MatchResult result, FilterContext context) {
        String name;
        Map param = context.getRenderContext().getParameters();
        if (param.containsKey(name = result.group(1))) {
            Object value = param.get(name);
            if (value instanceof String[]) {
                buffer.append(((String[])value)[0]);
            } else {
                buffer.append(value);
            }
        } else {
            buffer.append("<");
            buffer.append(name);
            buffer.append(">");
        }
    }

    protected String getLocaleKey() {
        return "filter.param";
    }

    protected boolean isSingleLine() {
        return true;
    }
}

