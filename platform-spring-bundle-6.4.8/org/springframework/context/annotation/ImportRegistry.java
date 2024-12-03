/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;

interface ImportRegistry {
    @Nullable
    public AnnotationMetadata getImportingClassFor(String var1);

    public void removeImportingClass(String var1);
}

