/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;

public class CannotResolveResourceIdentifierException
extends XhtmlException {
    private final ResourceIdentifier resourceIdentifier;

    public CannotResolveResourceIdentifierException(ResourceIdentifier resourceIdentifier, String message) {
        super(message);
        this.resourceIdentifier = resourceIdentifier;
    }

    public CannotResolveResourceIdentifierException(ResourceIdentifier resourceIdentifier, String message, Throwable cause) {
        super(message, cause);
        this.resourceIdentifier = resourceIdentifier;
    }

    public ResourceIdentifier getResourceIdentifier() {
        return this.resourceIdentifier;
    }
}

