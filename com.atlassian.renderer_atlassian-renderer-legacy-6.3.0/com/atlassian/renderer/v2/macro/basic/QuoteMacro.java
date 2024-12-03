/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.macro.basic;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.Map;

public class QuoteMacro
extends BaseMacro {
    @Override
    public boolean isInline() {
        return false;
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    @Override
    public RenderMode getBodyRenderMode() {
        return RenderMode.ALL;
    }

    @Override
    public boolean suppressMacroRenderingDuringWysiwyg() {
        return false;
    }

    @Override
    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        return "<blockquote>" + body + "</blockquote>";
    }
}

