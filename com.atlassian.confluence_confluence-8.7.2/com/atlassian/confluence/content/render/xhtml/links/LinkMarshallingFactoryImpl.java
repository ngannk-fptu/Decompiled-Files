/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.links;

import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.links.LinkMarshallingFactory;
import com.atlassian.confluence.xhtml.api.Link;

public class LinkMarshallingFactoryImpl
implements LinkMarshallingFactory {
    private final Marshaller<Link> viewMarshaller;
    private final Marshaller<Link> editorMarshaller;
    private final Marshaller<Link> storageMarshaller;
    private final Unmarshaller<Link> editorUnmarshaller;
    private final Unmarshaller<Link> storageUnmarshaller;

    public LinkMarshallingFactoryImpl(Marshaller<Link> viewMarshaller, Marshaller<Link> editorMarshaller, Marshaller<Link> storageMarshaller, Unmarshaller<Link> editorUnmarshaller, Unmarshaller<Link> storageUnmarshaller) {
        this.viewMarshaller = viewMarshaller;
        this.editorMarshaller = editorMarshaller;
        this.storageMarshaller = storageMarshaller;
        this.editorUnmarshaller = editorUnmarshaller;
        this.storageUnmarshaller = storageUnmarshaller;
    }

    @Override
    public Marshaller<Link> getViewMarshaller() {
        return this.viewMarshaller;
    }

    @Override
    public Marshaller<Link> getEditorMarshaller() {
        return this.editorMarshaller;
    }

    @Override
    public Marshaller<Link> getStorageMarshaller() {
        return this.storageMarshaller;
    }

    @Override
    public Unmarshaller<Link> getEditorUnmarshaller() {
        return this.editorUnmarshaller;
    }

    @Override
    public Unmarshaller<Link> getStorageUnmarshaller() {
        return this.storageUnmarshaller;
    }
}

