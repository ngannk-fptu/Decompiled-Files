/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.identicator;

import com.mchange.v1.identicator.IdHashKey;
import com.mchange.v1.identicator.Identicator;
import com.mchange.v1.identicator.StrongIdHashKey;
import com.mchange.v1.util.WrapperIterator;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class IdHashSet
extends AbstractSet
implements Set {
    HashSet inner;
    Identicator id;

    private IdHashSet(HashSet hashSet, Identicator identicator) {
        this.inner = hashSet;
        this.id = identicator;
    }

    public IdHashSet(Identicator identicator) {
        this(new HashSet(), identicator);
    }

    public IdHashSet(Collection collection, Identicator identicator) {
        this(new HashSet(2 * collection.size()), identicator);
    }

    public IdHashSet(int n, float f, Identicator identicator) {
        this(new HashSet(n, f), identicator);
    }

    public IdHashSet(int n, Identicator identicator) {
        this(new HashSet(n, 0.75f), identicator);
    }

    @Override
    public Iterator iterator() {
        return new WrapperIterator(this.inner.iterator(), true){

            @Override
            protected Object transformObject(Object object) {
                IdHashKey idHashKey = (IdHashKey)object;
                return idHashKey.getKeyObj();
            }
        };
    }

    @Override
    public int size() {
        return this.inner.size();
    }

    @Override
    public boolean contains(Object object) {
        return this.inner.contains(this.createKey(object));
    }

    @Override
    public boolean add(Object object) {
        return this.inner.add(this.createKey(object));
    }

    @Override
    public boolean remove(Object object) {
        return this.inner.remove(this.createKey(object));
    }

    @Override
    public void clear() {
        this.inner.clear();
    }

    private IdHashKey createKey(Object object) {
        return new StrongIdHashKey(object, this.id);
    }
}

