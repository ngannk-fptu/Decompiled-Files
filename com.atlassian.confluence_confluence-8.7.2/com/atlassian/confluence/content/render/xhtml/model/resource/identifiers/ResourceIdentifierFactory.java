/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;

public interface ResourceIdentifierFactory {
    public ResourceIdentifier getResourceIdentifier(Object var1, ConversionContext var2);
}

