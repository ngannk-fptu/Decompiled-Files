/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

public class ResourceIdentifierCreationException
extends RuntimeException {
    public ResourceIdentifierCreationException(Object resource, String message) {
        super("Error creating resource identifier for resource " + resource + ": " + message);
    }
}

