/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.access.annotation;

import java.lang.annotation.Annotation;
import java.util.Collection;
import org.springframework.security.access.ConfigAttribute;

@Deprecated
public interface AnnotationMetadataExtractor<A extends Annotation> {
    public Collection<? extends ConfigAttribute> extractAttributes(A var1);
}

