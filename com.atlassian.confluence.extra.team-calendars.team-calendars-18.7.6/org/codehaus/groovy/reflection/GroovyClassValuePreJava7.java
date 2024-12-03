/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection;

import org.codehaus.groovy.reflection.GroovyClassValue;
import org.codehaus.groovy.util.Finalizable;
import org.codehaus.groovy.util.ManagedConcurrentMap;
import org.codehaus.groovy.util.ReferenceBundle;

class GroovyClassValuePreJava7<T>
implements GroovyClassValue<T> {
    private static final ReferenceBundle weakBundle = ReferenceBundle.getWeakBundle();
    private final GroovyClassValue.ComputeValue<T> computeValue;
    private final GroovyClassValuePreJava7Map map = new GroovyClassValuePreJava7Map();

    public GroovyClassValuePreJava7(GroovyClassValue.ComputeValue<T> computeValue) {
        this.computeValue = computeValue;
    }

    @Override
    public T get(Class<?> type) {
        Object value = ((EntryWithValue)this.map.getOrPut(type, null)).getValue();
        return (T)value;
    }

    @Override
    public void remove(Class<?> type) {
        this.map.remove(type);
    }

    private class GroovyClassValuePreJava7Map
    extends ManagedConcurrentMap<Class<?>, T> {
        public GroovyClassValuePreJava7Map() {
            super(weakBundle);
        }

        @Override
        protected GroovyClassValuePreJava7Segment createSegment(Object segmentInfo, int cap) {
            ReferenceBundle bundle = (ReferenceBundle)segmentInfo;
            if (bundle == null) {
                throw new IllegalArgumentException("bundle must not be null ");
            }
            return new GroovyClassValuePreJava7Segment(bundle, cap);
        }
    }

    private class GroovyClassValuePreJava7Segment
    extends ManagedConcurrentMap.Segment<Class<?>, T> {
        GroovyClassValuePreJava7Segment(ReferenceBundle bundle, int initialCapacity) {
            super(bundle, initialCapacity);
        }

        protected EntryWithValue createEntry(Class<?> key, int hash, T unused) {
            return new EntryWithValue(this, key, hash);
        }
    }

    private class EntryWithValue
    extends ManagedConcurrentMap.EntryWithValue<Class<?>, T> {
        public EntryWithValue(GroovyClassValuePreJava7Segment segment, Class<?> key, int hash) {
            super(weakBundle, segment, key, hash, GroovyClassValuePreJava7.this.computeValue.computeValue(key));
        }

        @Override
        public void setValue(T value) {
            if (value != null) {
                super.setValue(value);
            }
        }

        @Override
        public void finalizeReference() {
            Object value = this.getValue();
            if (value instanceof Finalizable) {
                ((Finalizable)value).finalizeReference();
            }
            super.finalizeReference();
        }
    }
}

