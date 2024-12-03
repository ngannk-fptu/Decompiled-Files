/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans.factory.parsing;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

@FunctionalInterface
public interface SourceExtractor {
    @Nullable
    public Object extractSource(Object var1, @Nullable Resource var2);
}

