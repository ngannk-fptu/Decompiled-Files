/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.ReflectionUtils$FieldCallback
 */
package org.springframework.data.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class AnnotationDetectionFieldCallback
implements ReflectionUtils.FieldCallback {
    private final Class<? extends Annotation> annotationType;
    @Nullable
    private Field field;

    public AnnotationDetectionFieldCallback(Class<? extends Annotation> annotationType) {
        Assert.notNull(annotationType, (String)"AnnotationType must not be null!");
        this.annotationType = annotationType;
    }

    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
        if (this.field != null) {
            return;
        }
        if (AnnotatedElementUtils.findMergedAnnotation((AnnotatedElement)field, this.annotationType) != null) {
            ReflectionUtils.makeAccessible((Field)field);
            this.field = field;
        }
    }

    @Nullable
    public Field getField() {
        return this.field;
    }

    public Field getRequiredField() {
        Field field = this.field;
        if (field == null) {
            throw new IllegalStateException(String.format("No field found for annotation %s!", this.annotationType));
        }
        return field;
    }

    @Nullable
    public Class<?> getType() {
        Field field = this.field;
        return field == null ? null : field.getType();
    }

    public Class<?> getRequiredType() {
        return this.getRequiredField().getType();
    }

    @Nullable
    public <T> T getValue(Object source) {
        Assert.notNull((Object)source, (String)"Source object must not be null!");
        Field field = this.field;
        if (field == null) {
            return null;
        }
        return (T)ReflectionUtils.getField((Field)field, (Object)source);
    }
}

