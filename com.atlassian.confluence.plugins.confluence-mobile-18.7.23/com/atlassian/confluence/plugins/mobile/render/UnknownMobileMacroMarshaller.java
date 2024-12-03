/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Marshaller
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.Streamables
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.xhtml.MacroManager
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 */
package com.atlassian.confluence.plugins.mobile.render;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.plugins.mobile.render.NonMobileMacroPlaceholder;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.util.HashSet;

public class UnknownMobileMacroMarshaller
implements Marshaller<MacroDefinition> {
    public static final String UNKNOWN_MOBILE_MACROS_PROPERTY = "confluence.mobile.unknown.macro.names";
    private final MacroManager macroManager;
    private final Marshaller<MacroDefinition> unknownMacroMarshaller;
    private final NonMobileMacroPlaceholder nonMobileMacroPlaceholder;

    public UnknownMobileMacroMarshaller(MacroManager macroManager, Marshaller<MacroDefinition> unknownMacroMarshaller, NonMobileMacroPlaceholder nonMobileMacroPlaceholder) {
        this.macroManager = macroManager;
        this.unknownMacroMarshaller = unknownMacroMarshaller;
        this.nonMobileMacroPlaceholder = nonMobileMacroPlaceholder;
    }

    public Streamable marshal(MacroDefinition macroDefinition, ConversionContext conversionContext) throws XhtmlException {
        Macro macro = this.macroManager.getMacroByName(macroDefinition.getName());
        if (macro == null) {
            return this.unknownMacroMarshaller.marshal((Object)macroDefinition, conversionContext);
        }
        HashSet<String> macroNames = (HashSet<String>)conversionContext.getProperty(UNKNOWN_MOBILE_MACROS_PROPERTY);
        if (macroNames == null) {
            macroNames = new HashSet<String>();
        }
        macroNames.add(macroDefinition.getName());
        conversionContext.setProperty(UNKNOWN_MOBILE_MACROS_PROPERTY, macroNames);
        return Streamables.from((String)this.nonMobileMacroPlaceholder.create(macroDefinition.getName(), macro, conversionContext));
    }
}

