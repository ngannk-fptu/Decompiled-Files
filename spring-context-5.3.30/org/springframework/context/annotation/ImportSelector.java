/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.lang.Nullable
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

