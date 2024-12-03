/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.type;

import java.util.Set;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.MethodMetadata;

public interface AnnotationMetadata
extends ClassMetadata,
AnnotatedTypeMetadata {
    public Set<String> getAnnotationTypes();

    public Set<String> getMetaAnnotationTypes(String var1);

    public boolean hasAnnotation(String var1);

    public boolean hasMetaAnnotation(String var1);

    public boolean hasAnnotatedMethods(String var1);

    public Set<MethodMetadata> getAnnotatedMethods(String var1);
}

