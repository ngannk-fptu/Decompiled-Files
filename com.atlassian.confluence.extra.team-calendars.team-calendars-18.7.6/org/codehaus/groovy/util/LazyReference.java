/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.util;

import org.codehaus.groovy.util.LockableObject;
import org.codehaus.groovy.util.ManagedReference;
import org.codehaus.groovy.util.ReferenceBundle;
import org.codehaus.groovy.util.ReferenceType;

public abstract class LazyReference<T>
extends LockableObject {
    private static final ManagedReference INIT = new ManagedReference(ReferenceType.HARD, null, (Object)null){};
    private static final ManagedReference NULL_REFERENCE = new ManagedReference(ReferenceType.HARD, null, (Object)null){};
    private ManagedReference<T> reference = INIT;
    private final ReferenceBundle bundle;

    public LazyReference(ReferenceBundle bundle) {
        this.bundle = bundle;
    }

    public T get() {
        ManagedReference<T> resRef = this.reference;
        if (resRef == INIT) {
            return this.getLocked(false);
        }
        if (resRef == NULL_REFERENCE) {
            return null;
        }
        T res = resRef.get();
        if (res == null) {
            return this.getLocked(true);
        }
        return res;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private T getLocked(boolean force) {
        this.lock();
        try {
            ManagedReference<T> resRef = this.reference;
            if (!force && resRef != INIT) {
                T t = resRef.get();
                return t;
            }
            T res = this.initValue();
            this.reference = res == null ? NULL_REFERENCE : new ManagedReference<T>(this.bundle, res);
            T t = res;
            return t;
        }
        finally {
            this.unlock();
        }
    }

    public void clear() {
        this.reference = INIT;
    }

    public abstract T initValue();

    @Override
    public String toString() {
        T res = this.reference.get();
        if (res == null) {
            return "<null>";
        }
        return res.toString();
    }
}

