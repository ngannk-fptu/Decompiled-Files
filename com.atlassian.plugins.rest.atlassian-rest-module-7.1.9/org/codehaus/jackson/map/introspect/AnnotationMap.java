/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.introspect;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import org.codehaus.jackson.map.util.Annotations;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class AnnotationMap
implements Annotations {
    protected HashMap<Class<? extends Annotation>, Annotation> _annotations;

    public AnnotationMap() {
    }

    private AnnotationMap(HashMap<Class<? extends Annotation>, Annotation> a) {
        this._annotations = a;
    }

    @Override
    public <A extends Annotation> A get(Class<A> cls) {
        if (this._annotations == null) {
            return null;
        }
        return (A)this._annotations.get(cls);
    }

    public static AnnotationMap merge(AnnotationMap primary, AnnotationMap secondary) {
        if (primary == null || primary._annotations == null || primary._annotations.isEmpty()) {
            return secondary;
        }
        if (secondary == null || secondary._annotations == null || secondary._annotations.isEmpty()) {
            return primary;
        }
        HashMap<Class<? extends Annotation>, Annotation> annotations = new HashMap<Class<? extends Annotation>, Annotation>();
        for (Annotation ann : secondary._annotations.values()) {
            annotations.put(ann.annotationType(), ann);
        }
        for (Annotation ann : primary._annotations.values()) {
            annotations.put(ann.annotationType(), ann);
        }
        return new AnnotationMap(annotations);
    }

    @Override
    public int size() {
        return this._annotations == null ? 0 : this._annotations.size();
    }

    public void addIfNotPresent(Annotation ann) {
        if (this._annotations == null || !this._annotations.containsKey(ann.annotationType())) {
            this._add(ann);
        }
    }

    public void add(Annotation ann) {
        this._add(ann);
    }

    public String toString() {
        if (this._annotations == null) {
            return "[null]";
        }
        return this._annotations.toString();
    }

    protected final void _add(Annotation ann) {
        if (this._annotations == null) {
            this._annotations = new HashMap();
        }
        this._annotations.put(ann.annotationType(), ann);
    }
}

