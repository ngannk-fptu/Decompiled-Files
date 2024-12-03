/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 */
package com.atlassian.confluence.plugins.macros.advanced;

import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.Map;

public class DocMacro
extends BaseMacro {
    public boolean isInline() {
        return true;
    }

    public boolean hasBody() {
        return true;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.INLINE.or(RenderMode.allow((long)4L));
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        String baseurl = "http://confluence.atlassian.com/";
        String relativeLink = (String)parameters.get("0");
        return "<a href=\"" + baseurl + HtmlUtil.htmlEncode((String)relativeLink) + "\" target=\"_blank\">" + body + "</a>";
    }

    public boolean suppressSurroundingTagDuringWysiwygRendering() {
        return true;
    }

    public boolean suppressMacroRenderingDuringWysiwyg() {
        return false;
    }
}

