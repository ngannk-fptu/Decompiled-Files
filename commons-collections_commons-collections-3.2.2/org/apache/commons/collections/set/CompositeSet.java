/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.set;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.collection.CompositeCollection;

public class CompositeSet
extends CompositeCollection
implements Set {
    public CompositeSet() {
    }

    public CompositeSet(Set set) {
        super(set);
    }

    public CompositeSet(Set[] sets) {
        super(sets);
    }

    public synchronized void addComposited(Collection c) {
        if (!(c instanceof Set)) {
            throw new IllegalArgumentException("Collections added must implement java.util.Set");
        }
        Iterator i = this.getCollections().iterator();
        while (i.hasNext()) {
            Set set = (Set)i.next();
            Collection intersects = CollectionUtils.intersection(set, c);
            if (intersects.size() <= 0) continue;
            if (this.mutator == null) {
                throw new UnsupportedOperationException("Collision adding composited collection with no SetMutator set");
            }
            if (!(this.mutator instanceof SetMutator)) {
                throw new UnsupportedOperationException("Collision adding composited collection to a CompositeSet with a CollectionMutator instead of a SetMutator");
            }
            ((SetMutator)this.mutator).resolveCollision(this, set, (Set)c, intersects);
            if (CollectionUtils.intersection(set, c).size() <= 0) continue;
            throw new IllegalArgumentException("Attempt to add illegal entry unresolved by SetMutator.resolveCollision()");
        }
        super.addComposited(new Collection[]{c});
    }

    public synchronized void addComposited(Collection c, Collection d) {
        if (!(c instanceof Set)) {
            throw new IllegalArgumentException("Argument must implement java.util.Set");
        }
        if (!(d instanceof Set)) {
            throw new IllegalArgumentException("Argument must implement java.util.Set");
        }
        this.addComposited(new Set[]{(Set)c, (Set)d});
    }

    public synchronized void addComposited(Collection[] comps) {
        for (int i = comps.length - 1; i >= 0; --i) {
            this.addComposited(comps[i]);
        }
    }

    public void setMutator(CompositeCollection.CollectionMutator mutator) {
        super.setMutator(mutator);
    }

    public boolean remove(Object obj) {
        Iterator i = this.getCollections().iterator();
        while (i.hasNext()) {
            Set set = (Set)i.next();
            if (!set.contains(obj)) continue;
            return set.remove(obj);
        }
        return false;
    }

    public boolean equals(Object obj) {
        Set set;
        return obj instanceof Set && (set = (Set)obj).containsAll(this) && set.size() == this.size();
    }

    public int hashCode() {
        int code = 0;
        Iterator i = this.iterator();
        while (i.hasNext()) {
            Object next = i.next();
            code += next != null ? next.hashCode() : 0;
        }
        return code;
    }

    public static interface SetMutator
    extends CompositeCollection.CollectionMutator {
        public void resolveCollision(CompositeSet var1, Set var2, Set var3, Collection var4);
    }
}

