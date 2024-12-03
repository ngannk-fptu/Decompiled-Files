/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.aspectj.weaver.ResolvedType;

public final class Iterators {
    private Iterators() {
    }

    public static <T> Filter<T> dupFilter() {
        return new Filter<T>(){
            final Set<T> seen = new HashSet();

            @Override
            public Iterator<T> filter(final Iterator<T> in) {
                return new Iterator<T>(){
                    boolean fresh = false;
                    T peek;

                    @Override
                    public boolean hasNext() {
                        if (this.fresh) {
                            return true;
                        }
                        while (in.hasNext()) {
                            this.peek = in.next();
                            if (!seen.contains(this.peek)) {
                                this.fresh = true;
                                return true;
                            }
                            this.peek = null;
                        }
                        return false;
                    }

                    @Override
                    public T next() {
                        if (!this.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        Object ret = this.peek;
                        seen.add(this.peek);
                        this.peek = null;
                        this.fresh = false;
                        return ret;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public static <T> Iterator<T> array(final T[] o) {
        return new Iterator<T>(){
            int i = 0;
            int len = o == null ? 0 : o.length;

            @Override
            public boolean hasNext() {
                return this.i < this.len;
            }

            @Override
            public T next() {
                if (this.i < this.len) {
                    return o[this.i++];
                }
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static Iterator<ResolvedType> array(final ResolvedType[] o, final boolean genericsAware) {
        return new Iterator<ResolvedType>(){
            int i = 0;
            int len = o == null ? 0 : o.length;

            @Override
            public boolean hasNext() {
                return this.i < this.len;
            }

            @Override
            public ResolvedType next() {
                if (this.i < this.len) {
                    ResolvedType oo = o[this.i++];
                    if (!genericsAware && (oo.isParameterizedType() || oo.isGenericType())) {
                        return oo.getRawType();
                    }
                    return oo;
                }
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static <A, B> Iterator<B> mapOver(final Iterator<A> a, final Getter<A, B> g) {
        return new Iterator<B>(){
            Iterator<B> delegate = new Iterator<B>(){

                @Override
                public boolean hasNext() {
                    if (!a.hasNext()) {
                        return false;
                    }
                    Object o = a.next();
                    delegate = Iterators.append1(g.get(o), this);
                    return delegate.hasNext();
                }

                @Override
                public B next() {
                    if (!this.hasNext()) {
                        throw new UnsupportedOperationException();
                    }
                    return delegate.next();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };

            @Override
            public boolean hasNext() {
                return this.delegate.hasNext();
            }

            @Override
            public B next() {
                return this.delegate.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static <A> Iterator<A> recur(final A a, final Getter<A, A> g) {
        return new Iterator<A>(){
            Iterator<A> delegate;
            {
                this.delegate = Iterators.one(a);
            }

            @Override
            public boolean hasNext() {
                return this.delegate.hasNext();
            }

            @Override
            public A next() {
                Object next = this.delegate.next();
                this.delegate = Iterators.append(g.get(next), this.delegate);
                return next;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static <T> Iterator<T> append(Iterator<T> a, Iterator<T> b) {
        if (!b.hasNext()) {
            return a;
        }
        return Iterators.append1(a, b);
    }

    public static <T> Iterator<T> append1(final Iterator<T> a, final Iterator<T> b) {
        if (!a.hasNext()) {
            return b;
        }
        return new Iterator<T>(){

            @Override
            public boolean hasNext() {
                return a.hasNext() || b.hasNext();
            }

            @Override
            public T next() {
                if (a.hasNext()) {
                    return a.next();
                }
                if (b.hasNext()) {
                    return b.next();
                }
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static <T> Iterator<T> snoc(final Iterator<T> first, final T last) {
        return new Iterator<T>(){
            T last1;
            {
                this.last1 = last;
            }

            @Override
            public boolean hasNext() {
                return first.hasNext() || this.last1 != null;
            }

            @Override
            public T next() {
                if (first.hasNext()) {
                    return first.next();
                }
                if (this.last1 == null) {
                    throw new NoSuchElementException();
                }
                Object ret = this.last1;
                this.last1 = null;
                return ret;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static <T> Iterator<T> one(final T it) {
        return new Iterator<T>(){
            boolean avail = true;

            @Override
            public boolean hasNext() {
                return this.avail;
            }

            @Override
            public T next() {
                if (!this.avail) {
                    throw new NoSuchElementException();
                }
                this.avail = false;
                return it;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static class ResolvedTypeArrayIterator
    implements Iterator<ResolvedType> {
        private ResolvedType[] array;
        private int index;
        private int len;
        private boolean wantGenerics;
        private List<String> alreadySeen;

        public ResolvedTypeArrayIterator(ResolvedType[] array, List<String> alreadySeen, boolean wantGenerics) {
            assert (array != null);
            this.array = array;
            this.wantGenerics = wantGenerics;
            this.len = array.length;
            this.index = 0;
            this.alreadySeen = alreadySeen;
            this.moveToNextNewOne();
        }

        private void moveToNextNewOne() {
            while (this.index < this.len) {
                String signature;
                ResolvedType interfaceType = this.array[this.index];
                if (!this.wantGenerics && interfaceType.isParameterizedOrGenericType()) {
                    interfaceType = interfaceType.getRawType();
                }
                if (!this.alreadySeen.contains(signature = interfaceType.getSignature())) break;
                ++this.index;
            }
        }

        @Override
        public boolean hasNext() {
            return this.index < this.len;
        }

        @Override
        public ResolvedType next() {
            if (this.index < this.len) {
                ResolvedType oo = this.array[this.index++];
                if (!this.wantGenerics && (oo.isParameterizedType() || oo.isGenericType())) {
                    oo = oo.getRawType();
                }
                this.alreadySeen.add(oo.getSignature());
                this.moveToNextNewOne();
                return oo;
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static interface Filter<T> {
        public Iterator<T> filter(Iterator<T> var1);
    }

    public static interface Getter<A, B> {
        public Iterator<B> get(A var1);
    }
}

