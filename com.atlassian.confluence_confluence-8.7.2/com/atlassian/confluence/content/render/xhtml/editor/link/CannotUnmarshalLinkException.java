/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.link;

import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.transformers.TransformationException;

public class CannotUnmarshalLinkException
extends TransformationException {
    private final ResourceIdentifier resourceIdentifier;
    private final String linkAlias;

    public CannotUnmarshalLinkException(ResourceIdentifier resourceIdentifier, String linkAlias, Throwable cause) {
        super("Could not unmarshal a link in the editor.", cause);
        this.resourceIdentifier = resourceIdentifier;
        this.linkAlias = linkAlias;
    }

    public ResourceIdentifier getResourceIdentifier() {
        return this.resourceIdentifier;
    }

    public String getLinkAlias() {
        return this.linkAlias;
    }
}

