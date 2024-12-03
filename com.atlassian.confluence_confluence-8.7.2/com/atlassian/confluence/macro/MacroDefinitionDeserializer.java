/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.xhtml.api.MacroDefinition;

public interface MacroDefinitionDeserializer {
    public MacroDefinition deserialize(String var1);

    public MacroDefinition deserializeWithTypedParameters(String var1, ConversionContext var2);
}

