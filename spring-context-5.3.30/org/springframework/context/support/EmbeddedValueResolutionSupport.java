/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringValueResolver
 */
package org.springframework.context.support;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

public class EmbeddedValueResolutionSupport
implements EmbeddedValueResolverAware {
    @Nullable
    private StringValueResolver embeddedValueResolver;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    @Nullable
    protected String resolveEmbeddedValue(String value) {
        return this.embeddedValueResolver != null ? this.embeddedValueResolver.resolveStringValue(value) : value;
    }
}

