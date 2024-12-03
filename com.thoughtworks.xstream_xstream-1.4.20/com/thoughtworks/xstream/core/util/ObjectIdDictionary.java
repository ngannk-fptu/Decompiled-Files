/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class ObjectIdDictionary {
    private final Map map = new HashMap();
    private final ReferenceQueue queue = new ReferenceQueue();

    public void associateId(Object obj, Object id) {
        this.map.put(new WeakIdWrapper(obj), id);
        this.cleanup();
    }

    public Object lookupId(Object obj) {
        Object id = this.map.get(new IdWrapper(obj));
        return id;
    }

    public boolean containsId(Object item) {
        boolean b = this.map.containsKey(new IdWrapper(item));
        return b;
    }

    public void removeId(Object item) {
        this.map.remove(new IdWrapper(item));
        this.cleanup();
    }

    public int size() {
        this.cleanup();
        return this.map.size();
    }

    private void cleanup() {
        WeakIdWrapper wrapper;
        while ((wrapper = (WeakIdWrapper)this.queue.poll()) != null) {
            this.map.remove(wrapper);
        }
    }

    private class WeakIdWrapper
    extends WeakReference
    implements Wrapper {
        private final int hashCode;

        public WeakIdWrapper(Object obj) {
            super(obj, ObjectIdDictionary.this.queue);
            this.hashCode = System.identityHashCode(obj);
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object other) {
            return this.get() == ((Wrapper)other).get();
        }

        public String toString() {
            Object obj = this.get();
            return obj == null ? "(null)" : obj.toString();
        }
    }

    private static class IdWrapper
    implements Wrapper {
        private final Object obj;
        private final int hashCode;

        public IdWrapper(Object obj) {
            this.hashCode = System.identityHashCode(obj);
            this.obj = obj;
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object other) {
            return this.obj == ((Wrapper)other).get();
        }

        public String toString() {
            return this.obj.toString();
        }

        public Object get() {
            return this.obj;
        }
    }

    private static interface Wrapper {
        public int hashCode();

        public boolean equals(Object var1);

        public String toString();

        public Object get();
    }
}

