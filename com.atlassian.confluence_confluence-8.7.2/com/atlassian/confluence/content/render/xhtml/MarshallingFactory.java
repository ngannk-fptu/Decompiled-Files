/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;

public interface MarshallingFactory<T> {
    public Marshaller<T> getViewMarshaller();

    public Marshaller<T> getEditorMarshaller();

    public Marshaller<T> getStorageMarshaller();

    public Unmarshaller<T> getEditorUnmarshaller();

    public Unmarshaller<T> getStorageUnmarshaller();
}

