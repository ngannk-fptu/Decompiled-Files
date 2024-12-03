/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 */
package com.atlassian.confluence.plugins.macros.advanced;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.Map;

public class ExcerptMacro
extends BaseMacro {
    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        return TokenType.INLINE;
    }

    public boolean isInline() {
        return true;
    }

    public boolean hasBody() {
        return true;
    }

    public RenderMode getBodyRenderMode() {
        return null;
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        return this.shouldHideExcerpt(parameters) ? "" : body;
    }

    private boolean shouldHideExcerpt(Map parameters) {
        return "true".equalsIgnoreCase((String)parameters.get("hidden"));
    }
}

