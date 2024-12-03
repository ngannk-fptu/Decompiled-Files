/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.links.linktypes.AbstractPageLink
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.macros.basic;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.links.linktypes.AbstractPageLink;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import java.util.Map;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class AnchorMacro
extends BaseMacro
implements Macro {
    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        return TokenType.INLINE;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public String execute(Map parameters, String body, RenderContext renderContext) {
        return this.execute((Map<String, String>)parameters, body, (ConversionContext)new DefaultConversionContext(renderContext));
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) {
        String anchorName = StringUtils.defaultString((String)parameters.get("0"));
        return StringUtils.isNotBlank((CharSequence)anchorName) ? "<span class=\"confluence-anchor-link\" id=\"" + AbstractPageLink.generateAnchor((PageContext)conversionContext.getPageContext(), (String)anchorName) + "\">" + StringEscapeUtils.escapeHtml4((String)body) + "</span>" : "";
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.INLINE;
    }
}

