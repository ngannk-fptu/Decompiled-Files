/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.Aware
 *  org.springframework.core.type.AnnotationMetadata
 */
package org.springframework.context.annotation;

import org.springframework.beans.factory.Aware;
import org.springframework.core.type.AnnotationMetadata;

public interface ImportAware
extends Aware {
    public void setImportMetadata(AnnotationMetadata var1);
}

