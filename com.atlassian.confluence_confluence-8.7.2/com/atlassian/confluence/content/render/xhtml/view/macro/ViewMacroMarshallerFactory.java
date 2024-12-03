/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.view.macro;

import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.view.macro.ViewMacroErrorPlaceholder;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.xhtml.api.MacroDefinition;

public interface ViewMacroMarshallerFactory {
    public Marshaller<MacroDefinition> newMacroMarshaller();

    public Marshaller<MacroDefinition> newMacroMarshaller(MacroManager var1, Marshaller<MacroDefinition> var2, ViewMacroErrorPlaceholder var3);

    public Marshaller<MacroDefinition> newUnknownMacroMarshaller();
}

