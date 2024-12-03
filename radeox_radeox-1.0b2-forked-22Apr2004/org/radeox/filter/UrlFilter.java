/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.filter;

import java.text.MessageFormat;
import org.radeox.api.engine.ImageRenderEngine;
import org.radeox.api.engine.RenderEngine;
import org.radeox.api.engine.context.InitialRenderContext;
import org.radeox.filter.CacheFilter;
import org.radeox.filter.context.FilterContext;
import org.radeox.filter.regex.LocaleRegexTokenFilter;
import org.radeox.regex.MatchResult;
import org.radeox.util.Encoder;

public class UrlFilter
extends LocaleRegexTokenFilter
implements CacheFilter {
    private MessageFormat formatter;

    protected String getLocaleKey() {
        return "filter.url";
    }

    public void setInitialContext(InitialRenderContext context) {
        super.setInitialContext(context);
        String outputTemplate = this.outputMessages.getString(this.getLocaleKey() + ".print");
        this.formatter = new MessageFormat("");
        this.formatter.applyPattern(outputTemplate);
    }

    public void handleMatch(StringBuffer buffer, MatchResult result, FilterContext context) {
        buffer.append(result.group(1));
        RenderEngine engine = context.getRenderContext().getRenderEngine();
        String externalImage = "";
        if (engine instanceof ImageRenderEngine) {
            buffer.append(((ImageRenderEngine)((Object)engine)).getExternalImageLink(null));
        }
        buffer.append(this.formatter.format(new Object[]{externalImage, Encoder.escape(result.group(2)), Encoder.toEntity(result.group(2).charAt(0)) + result.group(2).substring(1)}));
    }
}

