/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;
import org.jboss.jandex.Utils;

final class RecordComponentInternal {
    static final RecordComponentInternal[] EMPTY_ARRAY = new RecordComponentInternal[0];
    private final byte[] name;
    private Type type;
    private AnnotationInstance[] annotations;
    static final NameComparator NAME_COMPARATOR = new NameComparator();

    RecordComponentInternal(byte[] name, Type type) {
        this(name, type, AnnotationInstance.EMPTY_ARRAY);
    }

    RecordComponentInternal(byte[] name, Type type, AnnotationInstance[] annotations) {
        this.name = name;
        this.type = type;
        this.annotations = annotations;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RecordComponentInternal that = (RecordComponentInternal)o;
        if (!Arrays.equals(this.annotations, that.annotations)) {
            return false;
        }
        if (!Arrays.equals(this.name, that.name)) {
            return false;
        }
        return this.type.equals(that.type);
    }

    public int hashCode() {
        int result = Arrays.hashCode(this.name);
        result = 31 * result + this.type.hashCode();
        result = 31 * result + Arrays.hashCode(this.annotations);
        return result;
    }

    final String name() {
        return Utils.fromUTF8(this.name);
    }

    final byte[] nameBytes() {
        return this.name;
    }

    final Type type() {
        return this.type;
    }

    final List<AnnotationInstance> annotations() {
        return Collections.unmodifiableList(Arrays.asList(this.annotations));
    }

    final AnnotationInstance[] annotationArray() {
        return this.annotations;
    }

    final AnnotationInstance annotation(DotName name) {
        AnnotationInstance key = new AnnotationInstance(name, null, null);
        int i = Arrays.binarySearch(this.annotations, key, AnnotationInstance.NAME_COMPARATOR);
        return i >= 0 ? this.annotations[i] : null;
    }

    final boolean hasAnnotation(DotName name) {
        return this.annotation(name) != null;
    }

    public String toString() {
        return this.type + " " + this.name();
    }

    public String toString(ClassInfo clazz) {
        return this.type + " " + clazz.name() + "." + this.name();
    }

    void setType(Type type) {
        this.type = type;
    }

    void setAnnotations(List<AnnotationInstance> annotations) {
        if (annotations.size() > 0) {
            this.annotations = annotations.toArray(new AnnotationInstance[annotations.size()]);
            Arrays.sort(this.annotations, AnnotationInstance.NAME_COMPARATOR);
        }
    }

    static class NameComparator
    implements Comparator<RecordComponentInternal> {
        NameComparator() {
        }

        @Override
        private int compare(byte[] left, byte[] right) {
            int i = 0;
            for (int j = 0; i < left.length && j < right.length; ++i, ++j) {
                int a = left[i] & 0xFF;
                int b = right[j] & 0xFF;
                if (a == b) continue;
                return a - b;
            }
            return left.length - right.length;
        }

        @Override
        public int compare(RecordComponentInternal instance, RecordComponentInternal instance2) {
            return this.compare(instance.name, instance2.name);
        }
    }
}

