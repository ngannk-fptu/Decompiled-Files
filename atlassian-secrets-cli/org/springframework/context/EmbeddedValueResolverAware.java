/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.util.StringValueResolver;

public interface EmbeddedValueResolverAware
extends Aware {
    public void setEmbeddedValueResolver(StringValueResolver var1);
}

