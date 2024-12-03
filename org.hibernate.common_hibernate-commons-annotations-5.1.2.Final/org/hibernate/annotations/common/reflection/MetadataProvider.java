/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import org.hibernate.annotations.common.reflection.AnnotationReader;

public interface MetadataProvider {
    public Map<Object, Object> getDefaults();

    public AnnotationReader getAnnotationReader(AnnotatedElement var1);

    default public void reset() {
    }
}

