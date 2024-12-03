/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.renderer.v2.macro.basic;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.renderer.v2.macro.basic.validator.ColorStyleValidator;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

public class ColorMacro
extends BaseMacro {
    @Override
    public boolean isInline() {
        return true;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    @Override
    public RenderMode getBodyRenderMode() {
        return RenderMode.INLINE.or(RenderMode.allow(4L));
    }

    @Override
    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        String color = StringUtils.trimToEmpty((String)((String)parameters.get("0")));
        ColorStyleValidator.getInstance().assertValid(color);
        return "<font color=\"" + color + "\">" + body + "</font>";
    }

    @Override
    public boolean suppressSurroundingTagDuringWysiwygRendering() {
        return true;
    }

    @Override
    public boolean suppressMacroRenderingDuringWysiwyg() {
        return false;
    }
}

