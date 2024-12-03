/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;

public interface ResourceIdentifierResolver<X extends ResourceIdentifier, Y> {
    public Y resolve(X var1, ConversionContext var2) throws CannotResolveResourceIdentifierException;
}

