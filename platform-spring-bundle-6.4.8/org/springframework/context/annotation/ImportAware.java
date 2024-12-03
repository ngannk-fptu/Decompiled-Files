/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import org.springframework.beans.factory.Aware;
import org.springframework.core.type.AnnotationMetadata;

public interface ImportAware
extends Aware {
    public void setImportMetadata(AnnotationMetadata var1);
}

