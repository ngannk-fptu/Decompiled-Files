/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.editor.macro.InvalidMacroParameterException;
import java.util.Map;

public interface MacroParameterTypeParser {
    public Map<String, Object> parseMacroParameters(String var1, Map<String, String> var2, ConversionContext var3) throws InvalidMacroParameterException;

    public Object parseMacroParameter(String var1, String var2, String var3, Map<String, String> var4, ConversionContext var5) throws InvalidMacroParameterException;
}

