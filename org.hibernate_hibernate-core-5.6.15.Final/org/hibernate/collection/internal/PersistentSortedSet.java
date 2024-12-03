/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.collection.internal;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.BasicCollectionPersister;

public class PersistentSortedSet
extends PersistentSet
implements SortedSet {
    protected Comparator comparator;

    public PersistentSortedSet() {
    }

    public PersistentSortedSet(SharedSessionContractImplementor session) {
        super(session);
    }

    @Deprecated
    public PersistentSortedSet(SessionImplementor session) {
        this((SharedSessionContractImplementor)session);
    }

    public PersistentSortedSet(SharedSessionContractImplementor session, SortedSet set) {
        super(session, (Set)set);
        this.comparator = set.comparator();
    }

    @Deprecated
    public PersistentSortedSet(SessionImplementor session, SortedSet set) {
        this((SharedSessionContractImplementor)session, set);
    }

    protected Serializable snapshot(BasicCollectionPersister persister, EntityMode entityMode) throws HibernateException {
        TreeMap<Object, Object> clonedSet = new TreeMap<Object, Object>(this.comparator);
        for (Object setElement : this.set) {
            Object copy = persister.getElementType().deepCopy(setElement, persister.getFactory());
            clonedSet.put(copy, copy);
        }
        return clonedSet;
    }

    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    public Comparator comparator() {
        return this.comparator;
    }

    public SortedSet subSet(Object fromElement, Object toElement) {
        this.read();
        SortedSet<Object> subSet = ((SortedSet)this.set).subSet(fromElement, toElement);
        return new SubSetProxy(subSet);
    }

    public SortedSet headSet(Object toElement) {
        this.read();
        SortedSet<Object> headSet = ((SortedSet)this.set).headSet(toElement);
        return new SubSetProxy(headSet);
    }

    public SortedSet tailSet(Object fromElement) {
        this.read();
        SortedSet<Object> tailSet = ((SortedSet)this.set).tailSet(fromElement);
        return new SubSetProxy(tailSet);
    }

    public Object first() {
        this.read();
        return ((SortedSet)this.set).first();
    }

    public Object last() {
        this.read();
        return ((SortedSet)this.set).last();
    }

    class SubSetProxy
    extends AbstractPersistentCollection.SetProxy
    implements SortedSet {
        SubSetProxy(SortedSet s) {
            super(s);
        }

        public Comparator comparator() {
            return ((SortedSet)this.set).comparator();
        }

        public Object first() {
            return ((SortedSet)this.set).first();
        }

        public SortedSet headSet(Object toValue) {
            return new SubSetProxy(((SortedSet)this.set).headSet(toValue));
        }

        public Object last() {
            return ((SortedSet)this.set).last();
        }

        public SortedSet subSet(Object fromValue, Object toValue) {
            return new SubSetProxy(((SortedSet)this.set).subSet(fromValue, toValue));
        }

        public SortedSet tailSet(Object fromValue) {
            return new SubSetProxy(((SortedSet)this.set).tailSet(fromValue));
        }
    }
}

