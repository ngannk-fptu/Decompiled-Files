/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.filter;

import java.text.MessageFormat;
import org.radeox.api.engine.context.InitialRenderContext;
import org.radeox.filter.CacheFilter;
import org.radeox.filter.context.FilterContext;
import org.radeox.filter.regex.LocaleRegexTokenFilter;
import org.radeox.regex.MatchResult;

public class HeadingFilter
extends LocaleRegexTokenFilter
implements CacheFilter {
    private MessageFormat formatter;

    protected String getLocaleKey() {
        return "filter.heading";
    }

    public void handleMatch(StringBuffer buffer, MatchResult result, FilterContext context) {
        buffer.append(this.handleMatch(result, context));
    }

    public void setInitialContext(InitialRenderContext context) {
        super.setInitialContext(context);
        String outputTemplate = this.outputMessages.getString(this.getLocaleKey() + ".print");
        this.formatter = new MessageFormat("");
        this.formatter.applyPattern(outputTemplate);
    }

    public String handleMatch(MatchResult result, FilterContext context) {
        return this.formatter.format(new Object[]{result.group(1).replace('.', '-'), result.group(3)});
    }
}

