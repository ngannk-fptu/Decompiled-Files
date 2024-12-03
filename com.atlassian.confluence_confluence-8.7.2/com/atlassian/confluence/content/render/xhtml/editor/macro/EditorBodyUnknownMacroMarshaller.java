/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.editor.macro.CommonMacroAttributeWriter;
import com.atlassian.confluence.content.render.xhtml.editor.macro.EditorBodyMacroMarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.macro.PlaceholderUrlFactory;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.util.Map;
import javax.xml.stream.XMLOutputFactory;

final class EditorBodyUnknownMacroMarshaller
extends EditorBodyMacroMarshaller {
    EditorBodyUnknownMacroMarshaller(CommonMacroAttributeWriter commonAttributeWriter, PlaceholderUrlFactory placeholderUrlFactory, XMLOutputFactory xmlOutputFactory) {
        super(commonAttributeWriter, placeholderUrlFactory, xmlOutputFactory);
    }

    @Override
    public boolean handles(Macro macro) {
        throw new UnsupportedOperationException("This should only be used by ImprovedEditorUnknownMacroMarshaller");
    }

    @Override
    public Streamable marshal(Macro macro, final MacroDefinition macroDefinition, ConversionContext conversionContext) throws XhtmlException {
        Macro fakeBlockBodyMacro = new Macro(){

            @Override
            public String execute(Map<String, String> parameters, String body, ConversionContext context) {
                throw new UnsupportedOperationException("mock macro does not support this");
            }

            @Override
            public Macro.BodyType getBodyType() {
                return macroDefinition.getBodyType();
            }

            @Override
            public Macro.OutputType getOutputType() {
                return Macro.OutputType.BLOCK;
            }
        };
        return super.marshal(fakeBlockBodyMacro, macroDefinition, conversionContext);
    }
}

