/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import org.springframework.core.type.AnnotationMetadata;

public interface ImportSelector {
    public String[] selectImports(AnnotationMetadata var1);
}

