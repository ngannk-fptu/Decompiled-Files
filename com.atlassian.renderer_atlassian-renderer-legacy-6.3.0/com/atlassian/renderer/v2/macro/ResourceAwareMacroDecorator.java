/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.macro;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.renderer.v2.macro.ResourceAware;
import com.atlassian.renderer.v2.macro.WysiwygBodyType;
import java.util.Map;

public class ResourceAwareMacroDecorator
implements ResourceAware,
Macro {
    private Macro macro;
    private String resourcePath;

    public ResourceAwareMacroDecorator(Macro macro) {
        this.macro = macro;
    }

    @Override
    public String getResourcePath() {
        return this.resourcePath;
    }

    @Override
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        return this.macro.getTokenType(parameters, body, context);
    }

    @Override
    public boolean isInline() {
        return this.macro.isInline();
    }

    @Override
    public boolean hasBody() {
        return this.macro.hasBody();
    }

    @Override
    public RenderMode getBodyRenderMode() {
        return this.macro.getBodyRenderMode();
    }

    @Override
    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        return this.macro.execute(parameters, body, renderContext);
    }

    public Macro getMacro() {
        return this.macro;
    }

    @Override
    public boolean suppressSurroundingTagDuringWysiwygRendering() {
        return this.macro.suppressSurroundingTagDuringWysiwygRendering();
    }

    @Override
    public boolean suppressMacroRenderingDuringWysiwyg() {
        return this.macro.suppressMacroRenderingDuringWysiwyg();
    }

    @Override
    public WysiwygBodyType getWysiwygBodyType() {
        return this.macro.getWysiwygBodyType();
    }

    public String toString() {
        return "ResourceAwareMacroDecorator{macro=" + this.macro + "}";
    }
}

