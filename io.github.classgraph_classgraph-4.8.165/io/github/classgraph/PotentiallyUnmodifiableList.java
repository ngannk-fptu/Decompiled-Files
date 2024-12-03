/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;

class PotentiallyUnmodifiableList<T>
extends ArrayList<T> {
    static final long serialVersionUID = 1L;
    boolean modifiable = true;

    PotentiallyUnmodifiableList() {
    }

    PotentiallyUnmodifiableList(int sizeHint) {
        super(sizeHint);
    }

    PotentiallyUnmodifiableList(Collection<T> collection) {
        super(collection);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    void makeUnmodifiable() {
        this.modifiable = false;
    }

    @Override
    public boolean add(T element) {
        if (!this.modifiable) {
            throw new IllegalArgumentException("List is immutable");
        }
        return super.add(element);
    }

    @Override
    public void add(int index, T element) {
        if (!this.modifiable) {
            throw new IllegalArgumentException("List is immutable");
        }
        super.add(index, element);
    }

    @Override
    public boolean remove(Object o) {
        if (!this.modifiable) {
            throw new IllegalArgumentException("List is immutable");
        }
        return super.remove(o);
    }

    @Override
    public T remove(int index) {
        if (!this.modifiable) {
            throw new IllegalArgumentException("List is immutable");
        }
        return (T)super.remove(index);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (!this.modifiable && !c.isEmpty()) {
            throw new IllegalArgumentException("List is immutable");
        }
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        if (!this.modifiable && !c.isEmpty()) {
            throw new IllegalArgumentException("List is immutable");
        }
        return super.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (!this.modifiable && !c.isEmpty()) {
            throw new IllegalArgumentException("List is immutable");
        }
        return super.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (!this.modifiable && !this.isEmpty()) {
            throw new IllegalArgumentException("List is immutable");
        }
        return super.retainAll(c);
    }

    @Override
    public void clear() {
        if (!this.modifiable && !this.isEmpty()) {
            throw new IllegalArgumentException("List is immutable");
        }
        super.clear();
    }

    @Override
    public T set(int index, T element) {
        if (!this.modifiable) {
            throw new IllegalArgumentException("List is immutable");
        }
        return super.set(index, element);
    }

    @Override
    public Iterator<T> iterator() {
        final Iterator iterator = super.iterator();
        return new Iterator<T>(){

            @Override
            public boolean hasNext() {
                if (PotentiallyUnmodifiableList.this.isEmpty()) {
                    return false;
                }
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                if (!PotentiallyUnmodifiableList.this.modifiable) {
                    throw new IllegalArgumentException("List is immutable");
                }
                iterator.remove();
            }
        };
    }

    @Override
    public ListIterator<T> listIterator() {
        final ListIterator iterator = super.listIterator();
        return new ListIterator<T>(){

            @Override
            public boolean hasNext() {
                if (PotentiallyUnmodifiableList.this.isEmpty()) {
                    return false;
                }
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next();
            }

            @Override
            public boolean hasPrevious() {
                if (PotentiallyUnmodifiableList.this.isEmpty()) {
                    return false;
                }
                return iterator.hasPrevious();
            }

            @Override
            public T previous() {
                return iterator.previous();
            }

            @Override
            public int nextIndex() {
                if (PotentiallyUnmodifiableList.this.isEmpty()) {
                    return 0;
                }
                return iterator.nextIndex();
            }

            @Override
            public int previousIndex() {
                if (PotentiallyUnmodifiableList.this.isEmpty()) {
                    return -1;
                }
                return iterator.previousIndex();
            }

            @Override
            public void remove() {
                if (!PotentiallyUnmodifiableList.this.modifiable) {
                    throw new IllegalArgumentException("List is immutable");
                }
                iterator.remove();
            }

            @Override
            public void set(T e) {
                if (!PotentiallyUnmodifiableList.this.modifiable) {
                    throw new IllegalArgumentException("List is immutable");
                }
                iterator.set(e);
            }

            @Override
            public void add(T e) {
                if (!PotentiallyUnmodifiableList.this.modifiable) {
                    throw new IllegalArgumentException("List is immutable");
                }
                iterator.add(e);
            }
        };
    }
}

