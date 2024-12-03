/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.editor.macro.EditorMacroMarshaller;
import com.atlassian.confluence.impl.macro.schema.MacroSchemaMigrator;
import com.atlassian.confluence.xhtml.api.MacroDefinition;

public class DelegatingEditorMacroMarshaller
implements Marshaller<MacroDefinition> {
    private final EditorMacroMarshaller macroMarshaller;
    private final MacroSchemaMigrator macroSchemaMigrator;

    public DelegatingEditorMacroMarshaller(EditorMacroMarshaller macroMarshaller, MacroSchemaMigrator macroSchemaMigrator) {
        this.macroMarshaller = macroMarshaller;
        this.macroSchemaMigrator = macroSchemaMigrator;
    }

    @Override
    public Streamable marshal(MacroDefinition macroDefinition, ConversionContext conversionContext) throws XhtmlException {
        MacroDefinition migratedDefinition = this.macroSchemaMigrator.migrateSchemaIfNecessary(macroDefinition, conversionContext);
        return this.macroMarshaller.marshal(migratedDefinition, conversionContext);
    }
}

