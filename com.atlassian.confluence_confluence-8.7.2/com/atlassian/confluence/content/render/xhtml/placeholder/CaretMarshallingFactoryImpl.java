/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.placeholder;

import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.placeholder.CaretMarshallingFactory;
import com.atlassian.confluence.xhtml.api.Caret;

public class CaretMarshallingFactoryImpl
implements CaretMarshallingFactory {
    private final Marshaller<Caret> viewMarshaller;
    private final Marshaller<Caret> editorMarshaller;
    private final Marshaller<Caret> storageMarshaller;
    private final Unmarshaller<Caret> editorUnmarshaller;
    private final Unmarshaller<Caret> storageUnmarshaller;

    public CaretMarshallingFactoryImpl(Marshaller<Caret> viewMarshaller, Marshaller<Caret> editorMarshaller, Marshaller<Caret> storageMarshaller, Unmarshaller<Caret> editorUnmarshaller, Unmarshaller<Caret> storageUnmarshaller) {
        this.viewMarshaller = viewMarshaller;
        this.editorMarshaller = editorMarshaller;
        this.storageMarshaller = storageMarshaller;
        this.editorUnmarshaller = editorUnmarshaller;
        this.storageUnmarshaller = storageUnmarshaller;
    }

    @Override
    public Marshaller<Caret> getEditorMarshaller() {
        return this.editorMarshaller;
    }

    @Override
    public Unmarshaller<Caret> getEditorUnmarshaller() {
        return this.editorUnmarshaller;
    }

    @Override
    public Marshaller<Caret> getStorageMarshaller() {
        return this.storageMarshaller;
    }

    @Override
    public Unmarshaller<Caret> getStorageUnmarshaller() {
        return this.storageUnmarshaller;
    }

    @Override
    public Marshaller<Caret> getViewMarshaller() {
        return this.viewMarshaller;
    }
}

