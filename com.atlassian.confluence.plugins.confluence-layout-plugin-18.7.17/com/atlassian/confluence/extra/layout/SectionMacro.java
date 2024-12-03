/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.confluence.extra.layout;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

public class SectionMacro
extends BaseMacro {
    private static final String MACRO_NAME = "section";

    public boolean suppressSurroundingTagDuringWysiwygRendering() {
        return false;
    }

    public boolean suppressMacroRenderingDuringWysiwyg() {
        return true;
    }

    public String getName() {
        return MACRO_NAME;
    }

    public boolean isInline() {
        return false;
    }

    public boolean hasBody() {
        return true;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.ALL;
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        int firstDivIndex;
        String cssClass;
        String string = cssClass = Boolean.valueOf(StringUtils.defaultString((String)((String)parameters.get("border")))) != false ? " class=\"sectionMacroWithBorder\"" : " class=\"sectionMacro\"";
        if (renderContext.getOutputType().equals("preview")) {
            Pattern emptyParagraphPattern = Pattern.compile("</div><p>(&nbsp;|&#160;|\\s|\\u00a0|)</p>");
            body = emptyParagraphPattern.matcher(body).replaceAll("</div>");
        }
        if ((firstDivIndex = body.indexOf("<div")) > -1) {
            return new StringBuffer("<div class=\"sectionColumnWrapper\"><div").append(cssClass).append(">").append(body.substring(0, firstDivIndex)).append("<div class=\"sectionMacroRow\">").append(body.substring(firstDivIndex, body.length())).append("</div></div></div>").toString();
        }
        return new StringBuffer("<div").append(cssClass).append(">").append("<div class=\"sectionMacroRow\">").append(body).append("</div></div>").toString();
    }
}

