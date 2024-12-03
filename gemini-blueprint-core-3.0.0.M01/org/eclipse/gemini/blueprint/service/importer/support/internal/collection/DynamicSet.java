/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.DynamicCollection;

public class DynamicSet<E>
extends DynamicCollection<E>
implements Set<E> {
    public DynamicSet() {
    }

    public DynamicSet(Collection<? extends E> c) {
        super(c);
    }

    public DynamicSet(int size) {
        super(size);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean add(E o) {
        List list = this.storage;
        synchronized (list) {
            if (this.storage.contains(o)) {
                return false;
            }
            this.storage.add(o);
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c == null) {
            throw new NullPointerException();
        }
        boolean result = false;
        List list = this.storage;
        synchronized (list) {
            Iterator<E> iter = c.iterator();
            while (iter.hasNext()) {
                result |= this.add(iter.next());
            }
        }
        return result;
    }
}

