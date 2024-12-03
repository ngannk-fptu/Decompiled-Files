/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.editor.macro.CommonMacroAttributeWriter;
import com.atlassian.confluence.content.render.xhtml.editor.macro.EditorBodilessUnknownMacroMarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.macro.EditorBodyUnknownMacroMarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroMarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.macro.PlaceholderUrlFactory;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import javax.xml.stream.XMLOutputFactory;

public class ImprovedEditorUnknownMacroMarshaller
implements MacroMarshaller {
    private final EditorBodilessUnknownMacroMarshaller editorBodilessMarshaller;
    private final EditorBodyUnknownMacroMarshaller editorBodyMarshaller;

    public ImprovedEditorUnknownMacroMarshaller(CommonMacroAttributeWriter commonAttributeWriter, PlaceholderUrlFactory placeholderUrlFactory, XMLOutputFactory xmlOutputFactory) {
        this.editorBodilessMarshaller = new EditorBodilessUnknownMacroMarshaller(commonAttributeWriter, placeholderUrlFactory, xmlOutputFactory);
        this.editorBodyMarshaller = new EditorBodyUnknownMacroMarshaller(commonAttributeWriter, placeholderUrlFactory, xmlOutputFactory);
    }

    @Override
    public boolean handles(Macro macro) {
        return macro == null;
    }

    @Override
    public Streamable marshal(Macro macro, MacroDefinition macroDefinition, ConversionContext conversionContext) throws XhtmlException {
        Macro.BodyType bodyType = macroDefinition.getBodyType();
        if (bodyType == Macro.BodyType.NONE) {
            return this.editorBodilessMarshaller.marshal(macro, macroDefinition, conversionContext);
        }
        return this.editorBodyMarshaller.marshal(macro, macroDefinition, conversionContext);
    }
}

