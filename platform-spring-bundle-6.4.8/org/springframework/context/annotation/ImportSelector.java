/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import java.util.function.Predicate;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;

public interface ImportSelector {
    public String[] selectImports(AnnotationMetadata var1);

    @Nullable
    default public Predicate<String> getExclusionFilter() {
        return null;
    }
}

