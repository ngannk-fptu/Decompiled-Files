/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.macro;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.renderer.v2.macro.WysiwygBodyType;
import java.util.Map;

public interface Macro {
    @Deprecated
    public static final String RAW_PARAMS_KEY = ": = | RAW | = :";

    public TokenType getTokenType(Map var1, String var2, RenderContext var3);

    public boolean isInline();

    public boolean hasBody();

    public RenderMode getBodyRenderMode();

    public String execute(Map var1, String var2, RenderContext var3) throws MacroException;

    public boolean suppressSurroundingTagDuringWysiwygRendering();

    public boolean suppressMacroRenderingDuringWysiwyg();

    public WysiwygBodyType getWysiwygBodyType();
}

