/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.util;

import org.codehaus.groovy.util.Finalizable;
import org.codehaus.groovy.util.Reference;
import org.codehaus.groovy.util.ReferenceBundle;
import org.codehaus.groovy.util.ReferenceManager;
import org.codehaus.groovy.util.ReferenceType;

public class ManagedReference<T>
implements Finalizable {
    private static final ReferenceManager NULL_MANAGER = new ReferenceManager(null){};
    private final Reference<T, ManagedReference<T>> ref;
    private final ReferenceManager manager;

    public ManagedReference(ReferenceType type, ReferenceManager rmanager, T value) {
        if (rmanager == null) {
            rmanager = NULL_MANAGER;
        }
        this.manager = rmanager;
        this.ref = type.createReference(value, this, rmanager.getReferenceQueue());
        rmanager.afterReferenceCreation(this.ref);
    }

    public ManagedReference(ReferenceBundle bundle, T value) {
        this(bundle.getType(), bundle.getManager(), value);
    }

    public final T get() {
        return this.ref.get();
    }

    public final void clear() {
        this.ref.clear();
        this.manager.removeStallEntries();
    }

    @Override
    public void finalizeReference() {
        this.ref.clear();
    }
}

