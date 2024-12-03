/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection.java;

import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.Map;
import org.hibernate.annotations.common.reflection.AnnotationReader;
import org.hibernate.annotations.common.reflection.MetadataProvider;
import org.hibernate.annotations.common.reflection.java.JavaAnnotationReader;

public final class JavaMetadataProvider
implements MetadataProvider {
    @Override
    public Map<Object, Object> getDefaults() {
        return Collections.emptyMap();
    }

    @Override
    public AnnotationReader getAnnotationReader(AnnotatedElement annotatedElement) {
        return new JavaAnnotationReader(annotatedElement);
    }

    @Override
    public void reset() {
    }
}

