/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.xhtml.api.Link
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 */
package com.atlassian.confluence.plugins.macros.advanced;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.util.Optional;

public class IncludeMacroUtils {
    public static Optional<Link> getLink(ConversionContext conversionContext) {
        return conversionContext.hasProperty("macroDefinition") ? IncludeMacroUtils.getLinkOptionalFromMacro(conversionContext) : Optional.empty();
    }

    private static Optional<Link> getLinkOptionalFromMacro(ConversionContext conversionContext) {
        return Optional.ofNullable((Link)((MacroDefinition)conversionContext.getProperty("macroDefinition")).getTypedParameter("", Link.class));
    }
}

