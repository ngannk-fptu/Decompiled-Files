/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.Macro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.renderer.v2.macro.WysiwygBodyType
 *  io.atlassian.util.concurrent.LazyReference
 */
package com.atlassian.confluence.renderer;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.renderer.v2.macro.WysiwygBodyType;
import io.atlassian.util.concurrent.LazyReference;
import java.util.Map;
import java.util.function.Supplier;

class LazyLoadedMacroDecorator
implements Macro {
    private final String name;
    private final Supplier<Macro> delegate;

    public LazyLoadedMacroDecorator(String name, LazyReference<Macro> delegate) {
        this.name = name;
        this.delegate = delegate;
    }

    public boolean isInline() {
        return this.delegate.get().isInline();
    }

    public boolean hasBody() {
        return this.delegate.get().hasBody();
    }

    public RenderMode getBodyRenderMode() {
        return this.delegate.get().getBodyRenderMode();
    }

    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        return this.delegate.get().getTokenType(parameters, body, context);
    }

    public WysiwygBodyType getWysiwygBodyType() {
        return this.delegate.get().getWysiwygBodyType();
    }

    public boolean suppressSurroundingTagDuringWysiwygRendering() {
        return this.delegate.get().suppressSurroundingTagDuringWysiwygRendering();
    }

    public boolean suppressMacroRenderingDuringWysiwyg() {
        return this.delegate.get().suppressMacroRenderingDuringWysiwyg();
    }

    public String execute(Map params, String body, RenderContext renderContext) throws MacroException {
        return this.delegate.get().execute(params, body, renderContext);
    }

    public Macro getMacro() {
        return this.delegate.get();
    }

    public String toString() {
        return "LazyLoadedMacroDecorator{name=" + this.name + "}";
    }
}

