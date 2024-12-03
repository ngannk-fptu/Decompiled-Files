/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.core.Member;
import com.hazelcast.core.MemberSelector;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class MemberSelectingCollection<M extends Member>
implements Collection<M> {
    private final Collection<M> members;
    private final MemberSelector selector;

    public MemberSelectingCollection(Collection<M> members, MemberSelector selector) {
        this.members = members;
        this.selector = selector;
    }

    @Override
    public int size() {
        return MemberSelectingCollection.count(this.members, this.selector);
    }

    public static <M extends Member> int count(Collection<M> members, MemberSelector memberSelector) {
        int size = 0;
        for (Member member : members) {
            if (!memberSelector.select(member)) continue;
            ++size;
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        return !this.iterator().hasNext();
    }

    @Override
    public boolean contains(Object o) {
        for (Member member : this.members) {
            if (!this.selector.select(member) || !o.equals(member)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Iterator<M> iterator() {
        return new MemberSelectingIterator();
    }

    @Override
    public Object[] toArray() {
        ArrayList<Member> result = new ArrayList<Member>();
        for (Member member : this.members) {
            if (!this.selector.select(member)) continue;
            result.add(member);
        }
        return result.toArray(new Object[0]);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        ArrayList<Member> result = new ArrayList<Member>();
        for (Member member : this.members) {
            if (!this.selector.select(member)) continue;
            result.add(member);
        }
        if (a.length != result.size()) {
            a = (Object[])Array.newInstance(a.getClass().getComponentType(), result.size());
        }
        for (int i = 0; i < a.length; ++i) {
            a[i] = result.get(i);
        }
        return a;
    }

    @Override
    public boolean add(M member) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (this.contains(o)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends M> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    class MemberSelectingIterator
    implements Iterator<M> {
        private final Iterator<M> iterator;
        private M member;

        MemberSelectingIterator() {
            this.iterator = MemberSelectingCollection.this.members.iterator();
        }

        @Override
        public boolean hasNext() {
            while (this.member == null && this.iterator.hasNext()) {
                Member nextMember = (Member)this.iterator.next();
                if (!MemberSelectingCollection.this.selector.select(nextMember)) continue;
                this.member = nextMember;
            }
            return this.member != null;
        }

        @Override
        public M next() {
            if (this.member == null && !this.hasNext()) {
                throw new NoSuchElementException();
            }
            Object nextMember = this.member;
            this.member = null;
            return nextMember;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

