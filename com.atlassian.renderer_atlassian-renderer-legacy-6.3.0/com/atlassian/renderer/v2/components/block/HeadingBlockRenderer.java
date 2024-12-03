/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components.block;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.components.block.BlockRenderer;
import com.atlassian.renderer.v2.components.block.LineWalker;
import com.atlassian.renderer.v2.macro.basic.BasicAnchorMacro;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeadingBlockRenderer
implements BlockRenderer {
    private static final Pattern HEADER_PATTERN = Pattern.compile("\\s*h([1-6])\\.\\s*(.*)");
    private static final String REPLACE = "<h{0}><a name=\"{1}\"></a>{2}</h{0}>";

    @Override
    public String renderNextBlock(String thisLine, LineWalker nextLines, RenderContext context, SubRenderer subRenderer) {
        Matcher matcher = HEADER_PATTERN.matcher(thisLine);
        if (matcher.matches()) {
            String headingLevel = matcher.group(1);
            String body = matcher.group(2);
            return this.renderHeading(headingLevel, body, context, subRenderer);
        }
        return null;
    }

    protected String renderHeading(String headingLevel, String body, RenderContext context, SubRenderer subRenderer) {
        return this.renderHeading(headingLevel, body, context, subRenderer, REPLACE);
    }

    protected String renderHeading(String headingLevel, String body, RenderContext context, SubRenderer subRenderer, String renderFormat) {
        String anchor = this.getAnchor(context, body);
        String renderedBody = subRenderer.render(body, context, context.getRenderMode().and(RenderMode.INLINE));
        if (renderedBody.equals("") && context.isRenderingForWysiwyg()) {
            renderedBody = "<br />";
        }
        return MessageFormat.format(renderFormat, headingLevel, anchor, renderedBody);
    }

    protected String getAnchor(RenderContext context, String body) {
        return BasicAnchorMacro.getAnchor(context, body);
    }
}

