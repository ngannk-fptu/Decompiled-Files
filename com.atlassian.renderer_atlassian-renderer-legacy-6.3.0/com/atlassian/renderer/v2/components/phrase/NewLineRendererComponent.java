/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components.phrase;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.components.AbstractRendererComponent;
import com.atlassian.renderer.v2.components.block.LineWalker;
import java.util.regex.Pattern;

public class NewLineRendererComponent
extends AbstractRendererComponent {
    private static final Pattern STARTS_WITH_BLOCK = Pattern.compile(TokenType.BLOCK.getTokenPatternString() + ".*");

    @Override
    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.renderLinebreaks();
    }

    @Override
    public String render(String wiki, RenderContext context) {
        if (wiki.indexOf("\n") < 0) {
            return wiki;
        }
        StringBuffer out = new StringBuffer(wiki.length());
        LineWalker walker = new LineWalker(wiki);
        while (walker.hasNext()) {
            String line = walker.next();
            out.append(line);
            if (!walker.hasNext()) continue;
            String nextLine = walker.peek();
            if (nextLine.trim().startsWith("<br") || line.trim().endsWith("<br class=\"atl-forced-newline\" />") || STARTS_WITH_BLOCK.matcher(nextLine).matches()) {
                out.append("\n");
                continue;
            }
            out.append("<br/>\n");
        }
        return out.toString();
    }
}

