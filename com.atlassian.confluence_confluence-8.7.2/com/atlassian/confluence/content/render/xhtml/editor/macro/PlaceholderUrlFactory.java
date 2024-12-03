/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.xhtml.api.MacroDefinition;

public interface PlaceholderUrlFactory {
    public String getUrlForMacro(MacroDefinition var1);

    public String getUrlForMacroHeading(MacroDefinition var1);

    public String getUrlForUnknownAttachment();

    public String getUrlForUnknownMacro(String var1);

    public String getUrlForErrorPlaceholder(String var1);
}

