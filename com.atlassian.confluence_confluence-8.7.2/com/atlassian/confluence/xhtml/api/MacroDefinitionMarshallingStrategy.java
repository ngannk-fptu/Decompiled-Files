/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.xhtml.api;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.xhtml.api.MacroDefinition;

public enum MacroDefinitionMarshallingStrategy {
    DISCARD_MACRO{

        @Override
        public Streamable marshal(MacroDefinition macroDefinition, ConversionContext conversionContext, Marshaller<MacroDefinition> macroDefinitionMarshaller) {
            return Streamables.empty();
        }
    }
    ,
    MARSHALL_MACRO{

        @Override
        public Streamable marshal(MacroDefinition macroDefinition, ConversionContext conversionContext, Marshaller<MacroDefinition> macroDefinitionMarshaller) throws XhtmlException {
            return macroDefinition == null ? Streamables.empty() : macroDefinitionMarshaller.marshal(macroDefinition, conversionContext);
        }
    };


    public abstract Streamable marshal(MacroDefinition var1, ConversionContext var2, Marshaller<MacroDefinition> var3) throws XhtmlException;
}

