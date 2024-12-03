/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.placeholder;

import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.placeholder.PlaceholderMarshallingFactory;
import com.atlassian.confluence.xhtml.api.Placeholder;

public class PlaceholderMarshallingFactoryImpl
implements PlaceholderMarshallingFactory {
    private final Marshaller<Placeholder> viewMarshaller;
    private final Marshaller<Placeholder> editorMarshaller;
    private final Marshaller<Placeholder> storageMarshaller;
    private final Unmarshaller<Placeholder> editorUnmarshaller;
    private final Unmarshaller<Placeholder> storageUnmarshaller;

    public PlaceholderMarshallingFactoryImpl(Marshaller<Placeholder> viewMarshaller, Marshaller<Placeholder> editorMarshaller, Marshaller<Placeholder> storageMarshaller, Unmarshaller<Placeholder> editorUnmarshaller, Unmarshaller<Placeholder> storageUnmarshaller) {
        this.viewMarshaller = viewMarshaller;
        this.editorMarshaller = editorMarshaller;
        this.storageMarshaller = storageMarshaller;
        this.editorUnmarshaller = editorUnmarshaller;
        this.storageUnmarshaller = storageUnmarshaller;
    }

    @Override
    public Marshaller<Placeholder> getEditorMarshaller() {
        return this.editorMarshaller;
    }

    @Override
    public Unmarshaller<Placeholder> getEditorUnmarshaller() {
        return this.editorUnmarshaller;
    }

    @Override
    public Marshaller<Placeholder> getStorageMarshaller() {
        return this.storageMarshaller;
    }

    @Override
    public Unmarshaller<Placeholder> getStorageUnmarshaller() {
        return this.storageUnmarshaller;
    }

    @Override
    public Marshaller<Placeholder> getViewMarshaller() {
        return this.viewMarshaller;
    }
}

