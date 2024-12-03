/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;

public final class AnnotationInstance {
    private static final AnnotationValue[] ANNOTATION_VALUES_TYPE = new AnnotationValue[0];
    static final InstanceNameComparator NAME_COMPARATOR = new InstanceNameComparator();
    static final AnnotationInstance[] EMPTY_ARRAY = new AnnotationInstance[0];
    private final DotName name;
    private AnnotationTarget target;
    private final AnnotationValue[] values;

    AnnotationInstance(AnnotationInstance instance, AnnotationTarget target) {
        this.name = instance.name;
        this.values = instance.values;
        this.target = target;
    }

    AnnotationInstance(DotName name, AnnotationTarget target, AnnotationValue[] values) {
        this.name = name;
        this.target = target;
        this.values = values != null && values.length > 0 ? values : AnnotationValue.EMPTY_VALUE_ARRAY;
    }

    public static final AnnotationInstance create(DotName name, AnnotationTarget target, AnnotationValue[] values) {
        if (name == null) {
            throw new IllegalArgumentException("Name can't be null");
        }
        if (values == null) {
            throw new IllegalArgumentException("Values can't be null");
        }
        values = (AnnotationValue[])values.clone();
        Arrays.sort(values, new Comparator<AnnotationValue>(){

            @Override
            public int compare(AnnotationValue o1, AnnotationValue o2) {
                return o1.name().compareTo(o2.name());
            }
        });
        return new AnnotationInstance(name, target, values);
    }

    public static final AnnotationInstance create(DotName name, AnnotationTarget target, List<AnnotationValue> values) {
        if (name == null) {
            throw new IllegalArgumentException("Name can't be null");
        }
        if (values == null) {
            throw new IllegalArgumentException("Values can't be null");
        }
        return AnnotationInstance.create(name, target, values.toArray(ANNOTATION_VALUES_TYPE));
    }

    public DotName name() {
        return this.name;
    }

    public AnnotationTarget target() {
        return this.target;
    }

    public AnnotationValue value(final String name) {
        int result = Arrays.binarySearch(this.values, name, new Comparator<Object>(){

            @Override
            public int compare(Object o1, Object o2) {
                return ((AnnotationValue)o1).name().compareTo(name);
            }
        });
        return result >= 0 ? this.values[result] : null;
    }

    public AnnotationValue value() {
        return this.value("value");
    }

    public AnnotationValue valueWithDefault(IndexView index, String name) {
        ClassInfo definition = index.getClassByName(this.name);
        if (definition == null) {
            throw new IllegalArgumentException("Index did not contain annotation definition: " + this.name);
        }
        AnnotationValue result = this.value(name);
        if (result != null) {
            return result;
        }
        MethodInfo method = definition.method(name, new Type[0]);
        return method == null ? null : method.defaultValue();
    }

    public AnnotationValue valueWithDefault(IndexView index) {
        return this.valueWithDefault(index, "value");
    }

    public List<AnnotationValue> valuesWithDefaults(IndexView index) {
        ClassInfo definition = index.getClassByName(this.name);
        if (definition == null) {
            throw new IllegalArgumentException("Index did not contain annotation definition: " + this.name);
        }
        List<MethodInfo> methods = definition.methods();
        ArrayList<AnnotationValue> result = new ArrayList<AnnotationValue>(methods.size());
        for (MethodInfo method : methods) {
            AnnotationValue value = this.value(method.name());
            if (value == null) {
                value = method.defaultValue();
            }
            if (value == null) continue;
            result.add(value);
        }
        return Collections.unmodifiableList(result);
    }

    public List<AnnotationValue> values() {
        return Collections.unmodifiableList(Arrays.asList(this.values));
    }

    AnnotationValue[] valueArray() {
        return this.values;
    }

    public String toString(boolean simple) {
        StringBuilder builder = new StringBuilder("@").append(simple ? this.name.local() : this.name);
        if (this.values.length > 0) {
            builder.append("(");
            for (int i = 0; i < this.values.length; ++i) {
                builder.append(this.values[i]);
                if (i >= this.values.length - 1) continue;
                builder.append(",");
            }
            builder.append(')');
        }
        return builder.toString();
    }

    public String toString() {
        return this.toString(true);
    }

    void setTarget(AnnotationTarget target) {
        if (this.target != null) {
            throw new IllegalStateException("Attempt to modify target post-initialization");
        }
        this.target = target;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AnnotationInstance instance = (AnnotationInstance)o;
        return this.target == instance.target && this.name.equals(instance.name) && Arrays.equals(this.values, instance.values);
    }

    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + Arrays.hashCode(this.values);
        return result;
    }

    static class InstanceNameComparator
    implements Comparator<AnnotationInstance> {
        InstanceNameComparator() {
        }

        @Override
        public int compare(AnnotationInstance instance, AnnotationInstance instance2) {
            return instance.name().compareTo(instance2.name());
        }
    }
}

