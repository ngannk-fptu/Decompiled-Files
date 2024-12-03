/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.macro;

import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.macro.MacroMarshallingFactory;
import com.atlassian.confluence.xhtml.api.MacroDefinition;

public class MacroMarshallingFactoryImpl
implements MacroMarshallingFactory {
    private final Marshaller<MacroDefinition> viewMarshaller;
    private final Marshaller<MacroDefinition> editorMarshaller;
    private final Marshaller<MacroDefinition> storageMarshaller;
    private final Unmarshaller<MacroDefinition> editorUnmarshaller;
    private final Unmarshaller<MacroDefinition> storageUnmarshaller;

    public MacroMarshallingFactoryImpl(Marshaller<MacroDefinition> viewMarshaller, Marshaller<MacroDefinition> editorMarshaller, Marshaller<MacroDefinition> storageMarshaller, Unmarshaller<MacroDefinition> editorUnmarshaller, Unmarshaller<MacroDefinition> storageUnmarshaller) {
        this.viewMarshaller = viewMarshaller;
        this.editorMarshaller = editorMarshaller;
        this.storageMarshaller = storageMarshaller;
        this.editorUnmarshaller = editorUnmarshaller;
        this.storageUnmarshaller = storageUnmarshaller;
    }

    @Override
    public Marshaller<MacroDefinition> getViewMarshaller() {
        return this.viewMarshaller;
    }

    @Override
    public Marshaller<MacroDefinition> getEditorMarshaller() {
        return this.editorMarshaller;
    }

    @Override
    public Marshaller<MacroDefinition> getStorageMarshaller() {
        return this.storageMarshaller;
    }

    @Override
    public Unmarshaller<MacroDefinition> getEditorUnmarshaller() {
        return this.editorUnmarshaller;
    }

    @Override
    public Unmarshaller<MacroDefinition> getStorageUnmarshaller() {
        return this.storageUnmarshaller;
    }
}

