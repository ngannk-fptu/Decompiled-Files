/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.introspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import org.codehaus.jackson.map.introspect.AnnotationMap;
import org.codehaus.jackson.map.type.TypeBindings;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class Annotated {
    protected Annotated() {
    }

    public abstract <A extends Annotation> A getAnnotation(Class<A> var1);

    public final <A extends Annotation> boolean hasAnnotation(Class<A> acls) {
        return this.getAnnotation(acls) != null;
    }

    public abstract Annotated withAnnotations(AnnotationMap var1);

    public final Annotated withFallBackAnnotationsFrom(Annotated annotated) {
        return this.withAnnotations(AnnotationMap.merge(this.getAllAnnotations(), annotated.getAllAnnotations()));
    }

    public abstract AnnotatedElement getAnnotated();

    protected abstract int getModifiers();

    public final boolean isPublic() {
        return Modifier.isPublic(this.getModifiers());
    }

    public abstract String getName();

    public JavaType getType(TypeBindings context) {
        return context.resolveType(this.getGenericType());
    }

    public abstract Type getGenericType();

    public abstract Class<?> getRawType();

    protected abstract AnnotationMap getAllAnnotations();
}

