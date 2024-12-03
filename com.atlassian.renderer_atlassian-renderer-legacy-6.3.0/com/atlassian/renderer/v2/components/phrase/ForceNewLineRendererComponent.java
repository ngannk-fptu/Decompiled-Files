/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components.phrase;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.components.AbstractRegexRendererComponent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForceNewLineRendererComponent
extends AbstractRegexRendererComponent {
    private static final Pattern FORCE_NEWLINE = Pattern.compile("(?<!\\\\)\\\\{2}(?!\\S*\\\\)");
    public static final String FORCED_NEWLINE_CLASS = "atl-forced-newline";
    public static final String FORCED_NEWLINE_TAG = "<br class=\"atl-forced-newline\" />";

    @Override
    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.renderLinebreaks();
    }

    @Override
    public String render(String wiki, RenderContext context) {
        if (wiki.indexOf("\\\\") == -1) {
            return wiki;
        }
        return this.regexRender(wiki, context, FORCE_NEWLINE);
    }

    @Override
    public void appendSubstitution(StringBuffer buffer, RenderContext context, Matcher matcher) {
        buffer.append(FORCED_NEWLINE_TAG);
    }
}

