/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.identicator;

import com.mchange.v1.identicator.IdHashKey;
import com.mchange.v1.identicator.Identicator;
import com.mchange.v1.identicator.StrongIdHashKey;
import com.mchange.v1.util.IteratorUtils;
import com.mchange.v1.util.ListUtils;
import com.mchange.v1.util.WrapperIterator;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class IdList
implements List {
    Identicator id;
    List inner;

    public IdList(Identicator identicator, List list) {
        this.id = identicator;
        this.inner = list;
    }

    @Override
    public int size() {
        return this.inner.size();
    }

    @Override
    public boolean isEmpty() {
        return this.inner.isEmpty();
    }

    @Override
    public boolean contains(Object object) {
        StrongIdHashKey strongIdHashKey = new StrongIdHashKey(object, this.id);
        return this.inner.contains(object);
    }

    @Override
    public Iterator iterator() {
        return new WrapperIterator(this.inner.iterator(), true){

            @Override
            protected Object transformObject(Object object) {
                if (object instanceof IdHashKey) {
                    IdHashKey idHashKey = (IdHashKey)object;
                    return idHashKey.getKeyObj();
                }
                return object;
            }
        };
    }

    @Override
    public Object[] toArray() {
        return this.toArray(new Object[this.size()]);
    }

    @Override
    public Object[] toArray(Object[] objectArray) {
        return IteratorUtils.toArray(this.iterator(), this.size(), objectArray);
    }

    @Override
    public boolean add(Object object) {
        return this.inner.add(new StrongIdHashKey(object, this.id));
    }

    @Override
    public boolean remove(Object object) {
        return this.inner.remove(new StrongIdHashKey(object, this.id));
    }

    @Override
    public boolean containsAll(Collection collection) {
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            StrongIdHashKey strongIdHashKey = new StrongIdHashKey(iterator.next(), this.id);
            if (this.inner.contains(strongIdHashKey)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection collection) {
        Iterator iterator = collection.iterator();
        boolean bl = false;
        while (iterator.hasNext()) {
            StrongIdHashKey strongIdHashKey = new StrongIdHashKey(iterator.next(), this.id);
            bl |= this.inner.add(strongIdHashKey);
        }
        return bl;
    }

    public boolean addAll(int n, Collection collection) {
        Iterator iterator = collection.iterator();
        while (iterator.hasNext()) {
            StrongIdHashKey strongIdHashKey = new StrongIdHashKey(iterator.next(), this.id);
            this.inner.add(n, strongIdHashKey);
            ++n;
        }
        return collection.size() > 0;
    }

    @Override
    public boolean removeAll(Collection collection) {
        Iterator iterator = collection.iterator();
        boolean bl = false;
        while (iterator.hasNext()) {
            StrongIdHashKey strongIdHashKey = new StrongIdHashKey(iterator.next(), this.id);
            bl |= this.inner.remove(strongIdHashKey);
        }
        return bl;
    }

    @Override
    public boolean retainAll(Collection collection) {
        Iterator iterator = this.inner.iterator();
        boolean bl = false;
        while (iterator.hasNext()) {
            IdHashKey idHashKey = (IdHashKey)iterator.next();
            if (collection.contains(idHashKey.getKeyObj())) continue;
            this.inner.remove(idHashKey);
            bl = true;
        }
        return bl;
    }

    @Override
    public void clear() {
        this.inner.clear();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof List) {
            return ListUtils.equivalent(this, (List)object);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ListUtils.hashContents(this);
    }

    public Object get(int n) {
        return ((IdHashKey)this.inner.get(n)).getKeyObj();
    }

    public Object set(int n, Object object) {
        IdHashKey idHashKey = this.inner.set(n, new StrongIdHashKey(object, this.id));
        return idHashKey.getKeyObj();
    }

    public void add(int n, Object object) {
        this.inner.add(n, new StrongIdHashKey(object, this.id));
    }

    public Object remove(int n) {
        IdHashKey idHashKey = (IdHashKey)this.inner.remove(n);
        return idHashKey == null ? null : idHashKey.getKeyObj();
    }

    @Override
    public int indexOf(Object object) {
        return this.inner.indexOf(new StrongIdHashKey(object, this.id));
    }

    @Override
    public int lastIndexOf(Object object) {
        return this.inner.lastIndexOf(new StrongIdHashKey(object, this.id));
    }

    public ListIterator listIterator() {
        return new LinkedList(this).listIterator();
    }

    public ListIterator listIterator(int n) {
        return new LinkedList(this).listIterator(n);
    }

    public List subList(int n, int n2) {
        return new IdList(this.id, this.inner.subList(n, n2));
    }
}

