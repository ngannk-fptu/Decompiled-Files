/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.xhtml.editor.macro.CommonMacroAttributeWriter;
import com.atlassian.confluence.content.render.xhtml.editor.macro.EditorBodilessMacroMarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.macro.PlaceholderUrlFactory;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import javax.xml.stream.XMLOutputFactory;

final class EditorBodilessUnknownMacroMarshaller
extends EditorBodilessMacroMarshaller {
    EditorBodilessUnknownMacroMarshaller(CommonMacroAttributeWriter commonAttributeWriter, PlaceholderUrlFactory placeholderUrlFactory, XMLOutputFactory xmlOutputFactory) {
        super(commonAttributeWriter, placeholderUrlFactory, xmlOutputFactory);
    }

    @Override
    public boolean handles(Macro macro) {
        throw new UnsupportedOperationException("This should only be used by ImprovedEditorUnknownMacroMarshaller");
    }

    @Override
    protected String getCssClass(MacroDefinition definition) {
        return "wysiwyg-unknown-macro";
    }

    @Override
    protected String getImageSource(MacroDefinition definition) {
        return this.placeholderUrlFactory.getUrlForUnknownMacro(definition.getName());
    }
}

