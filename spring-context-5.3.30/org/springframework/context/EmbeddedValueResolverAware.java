/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.Aware
 *  org.springframework.util.StringValueResolver
 */
package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.util.StringValueResolver;

public interface EmbeddedValueResolverAware
extends Aware {
    public void setEmbeddedValueResolver(StringValueResolver var1);
}

