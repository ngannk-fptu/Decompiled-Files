/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.renderer.v2.macro.basic.validator.WidthSizeValidator
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.confluence.extra.layout;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.renderer.v2.macro.basic.validator.WidthSizeValidator;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

public class ColumnMacro
extends BaseMacro {
    private static final String MACRO_NAME = "column";

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
        StringBuffer outputBuffer = new StringBuffer();
        String width = StringUtils.defaultString((String)((String)parameters.get("width")));
        if (StringUtils.isNotBlank((String)width)) {
            if (!width.endsWith("%") && !width.endsWith("px")) {
                width = outputBuffer.append(width).append("%").toString();
            }
            WidthSizeValidator.getInstance().assertValid(width);
        }
        outputBuffer.setLength(0);
        outputBuffer.append("<div class=\"columnMacro\"");
        if (StringUtils.isNotBlank((String)width)) {
            outputBuffer.append(" style=\"width:").append(width).append(";min-width:").append(width).append(";max-width:").append(width).append(";\"");
        }
        return outputBuffer.append(">").append(body).append("</div>").toString();
    }
}

