/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Throwables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XhtmlTimeoutException;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroMarshaller;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.google.common.base.Throwables;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditorMacroMarshaller
implements Marshaller<MacroDefinition> {
    private static final Logger log = LoggerFactory.getLogger(EditorMacroMarshaller.class);
    private final MacroManager macroManager;
    private final List<MacroMarshaller> macroMarshallers;

    public EditorMacroMarshaller(MacroManager macroManager, List<MacroMarshaller> macroMarshallers) {
        this.macroManager = macroManager;
        this.macroMarshallers = macroMarshallers;
    }

    @Override
    public Streamable marshal(MacroDefinition macroDefinition, ConversionContext conversionContext) throws XhtmlException {
        Macro macro = this.macroManager.getMacroByName(macroDefinition.getName());
        for (MacroMarshaller macroMarshaller : this.macroMarshallers) {
            if (!macroMarshaller.handles(macro)) continue;
            try {
                Streamable streamable = macroMarshaller.marshal(macro, macroDefinition, conversionContext);
                conversionContext.checkTimeout();
                return streamable;
            }
            catch (Exception ex) {
                Throwables.propagateIfInstanceOf((Throwable)ex, XhtmlTimeoutException.class);
                log.warn("The macro Marshaller " + macroMarshaller.getClass() + " threw an exception while rendering a placeholder for the macro " + macroDefinition, (Throwable)ex);
            }
        }
        throw new XhtmlException("No MacroMarshaller is available for the macro with name " + macroDefinition.getName());
    }
}

