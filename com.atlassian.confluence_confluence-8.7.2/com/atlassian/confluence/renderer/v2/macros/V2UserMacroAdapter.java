/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 */
package com.atlassian.confluence.renderer.v2.macros;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.Map;

public class V2UserMacroAdapter
extends BaseMacro {
    private Macro xhtmlUserMacro;

    public V2UserMacroAdapter(Macro xhtmlUserMacro) {
        this.xhtmlUserMacro = xhtmlUserMacro;
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        try {
            return this.xhtmlUserMacro.execute(parameters, body, new DefaultConversionContext(renderContext));
        }
        catch (MacroExecutionException ex) {
            throw new MacroException("Exception while rendering the wrapped Xhtml macro", (Throwable)ex);
        }
    }

    public RenderMode getBodyRenderMode() {
        if (this.xhtmlUserMacro.getBodyType() == Macro.BodyType.RICH_TEXT) {
            return RenderMode.suppress((long)256L);
        }
        return RenderMode.NO_RENDER;
    }

    public boolean hasBody() {
        return this.xhtmlUserMacro.getBodyType() != Macro.BodyType.NONE;
    }

    public Macro getXhtmlMacro() {
        return this.xhtmlUserMacro;
    }
}

