/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.lang.ref.WeakReference;

@Deprecated
public class WeakishReference {
    private WeakReference<Object> weakref;

    WeakishReference(Object reference) {
        this.weakref = new WeakReference<Object>(reference);
    }

    public Object get() {
        return this.weakref.get();
    }

    public static WeakishReference createReference(Object object) {
        return new WeakishReference(object);
    }

    public static class HardReference
    extends WeakishReference {
        public HardReference(Object object) {
            super(object);
        }
    }
}

