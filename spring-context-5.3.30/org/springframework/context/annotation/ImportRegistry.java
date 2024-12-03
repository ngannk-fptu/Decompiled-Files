/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.lang.Nullable
 */
package org.springframework.context.annotation;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;

interface ImportRegistry {
    @Nullable
    public AnnotationMetadata getImportingClassFor(String var1);

    public void removeImportingClass(String var1);
}

