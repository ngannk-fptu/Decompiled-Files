/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.common.annotation;

import java.lang.annotation.Annotation;

class AnnotatedParameter {
    final int parameterIndex;
    final Annotation annotation;
    final Object argument;

    AnnotatedParameter(int parameterIndex, Annotation annotation, Object argument) {
        this.parameterIndex = parameterIndex;
        this.annotation = annotation;
        this.argument = argument;
    }
}

