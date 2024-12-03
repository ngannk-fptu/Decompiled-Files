/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang.reflect;

import java.lang.annotation.Annotation;
import org.aspectj.lang.reflect.AjType;
import org.aspectj.lang.reflect.SignaturePattern;
import org.aspectj.lang.reflect.TypePattern;

public interface DeclareAnnotation {
    public AjType<?> getDeclaringType();

    public Kind getKind();

    public SignaturePattern getSignaturePattern();

    public TypePattern getTypePattern();

    public Annotation getAnnotation();

    public String getAnnotationAsText();

    public static enum Kind {
        Field,
        Method,
        Constructor,
        Type;

    }
}

