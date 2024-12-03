/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.macro;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.v2.macro.WysiwygBodyType;
import java.util.Map;

public abstract class BaseMacro
implements Macro {
    @Override
    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        return this.isInline() ? TokenType.INLINE : TokenType.INLINE_BLOCK;
    }

    @Override
    public boolean isInline() {
        return false;
    }

    @Override
    public boolean suppressSurroundingTagDuringWysiwygRendering() {
        return false;
    }

    @Override
    public boolean suppressMacroRenderingDuringWysiwyg() {
        return true;
    }

    @Override
    public WysiwygBodyType getWysiwygBodyType() {
        RenderMode bodyMode = this.getBodyRenderMode();
        return RenderMode.NO_RENDER.equals(bodyMode) ? WysiwygBodyType.PREFORMAT : WysiwygBodyType.WIKI_MARKUP;
    }
}

