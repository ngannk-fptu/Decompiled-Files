/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.embed;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.transformers.TransformationException;

public class CannotUnmarshalEmbeddedResourceException
extends TransformationException {
    private final ResourceIdentifier resourceIdentifier;
    private final String title;

    public CannotUnmarshalEmbeddedResourceException(ResourceIdentifier resourceIdentifier, String title, Throwable cause) {
        super("Could not unmarshal an embedded image in the editor.", cause);
        this.resourceIdentifier = resourceIdentifier;
        this.title = title;
    }

    public ResourceIdentifier getResourceIdentifier() {
        return this.resourceIdentifier;
    }

    public String getTitle() {
        return this.title;
    }
}

