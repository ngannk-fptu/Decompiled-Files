/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.parsing;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

@FunctionalInterface
public interface SourceExtractor {
    @Nullable
    public Object extractSource(Object var1, @Nullable Resource var2);
}

