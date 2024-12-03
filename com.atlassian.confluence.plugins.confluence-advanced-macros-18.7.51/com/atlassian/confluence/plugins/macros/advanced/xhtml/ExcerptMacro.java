/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.macros.advanced.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class ExcerptMacro
implements Macro {
    protected static Pattern stripPattern = Pattern.compile("<p>(.*?)</p>", 34);

    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        Matcher matcher;
        if (StringUtils.isNotEmpty((CharSequence)body) && (matcher = stripPattern.matcher(body)).matches()) {
            body = matcher.group(1);
        }
        return this.shouldHideExcerpt(parameters) ? "" : body;
    }

    private boolean shouldHideExcerpt(Map parameters) {
        return "true".equalsIgnoreCase((String)parameters.get("hidden"));
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.RICH_TEXT;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.INLINE;
    }
}

