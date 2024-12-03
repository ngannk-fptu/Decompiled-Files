/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.components.AbstractRendererComponent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractRegexRendererComponent
extends AbstractRendererComponent {
    @Override
    public abstract boolean shouldRender(RenderMode var1);

    @Override
    public abstract String render(String var1, RenderContext var2);

    protected String regexRender(String wiki, RenderContext context, Pattern pattern) {
        if (wiki == null || wiki.length() == 0) {
            return "";
        }
        Matcher matcher = pattern.matcher(wiki);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, "");
            this.appendSubstitution(buffer, context, matcher);
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    public abstract void appendSubstitution(StringBuffer var1, RenderContext var2, Matcher var3);
}

