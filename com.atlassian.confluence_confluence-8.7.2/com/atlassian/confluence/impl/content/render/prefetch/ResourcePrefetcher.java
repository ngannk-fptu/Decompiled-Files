/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.content.render.prefetch;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import java.util.Set;

public interface ResourcePrefetcher<T extends ResourceIdentifier> {
    public Class<T> getResourceItentifierType();

    public void prefetch(Set<T> var1, ConversionContext var2);
}

