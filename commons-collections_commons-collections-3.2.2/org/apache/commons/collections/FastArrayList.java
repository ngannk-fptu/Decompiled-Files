/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class FastArrayList
extends ArrayList {
    protected ArrayList list = null;
    protected boolean fast = false;

    public FastArrayList() {
        this.list = new ArrayList();
    }

    public FastArrayList(int capacity) {
        this.list = new ArrayList(capacity);
    }

    public FastArrayList(Collection collection) {
        this.list = new ArrayList(collection);
    }

    public boolean getFast() {
        return this.fast;
    }

    public void setFast(boolean fast) {
        this.fast = fast;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean add(Object element) {
        if (this.fast) {
            FastArrayList fastArrayList = this;
            synchronized (fastArrayList) {
                ArrayList temp = (ArrayList)this.list.clone();
                boolean result = temp.add(element);
                this.list = temp;
                return result;
            }
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            return this.list.add(element);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(int index, Object element) {
        if (this.fast) {
            FastArrayList fastArrayList = this;
            synchronized (fastArrayList) {
                ArrayList temp = (ArrayList)this.list.clone();
                temp.add(index, element);
                this.list = temp;
            }
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            this.list.add(index, element);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean addAll(Collection collection) {
        if (this.fast) {
            FastArrayList fastArrayList = this;
            synchronized (fastArrayList) {
                ArrayList temp = (ArrayList)this.list.clone();
                boolean result = temp.addAll(collection);
                this.list = temp;
                return result;
            }
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            return this.list.addAll(collection);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean addAll(int index, Collection collection) {
        if (this.fast) {
            FastArrayList fastArrayList = this;
            synchronized (fastArrayList) {
                ArrayList temp = (ArrayList)this.list.clone();
                boolean result = temp.addAll(index, collection);
                this.list = temp;
                return result;
            }
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            return this.list.addAll(index, collection);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clear() {
        if (this.fast) {
            FastArrayList fastArrayList = this;
            synchronized (fastArrayList) {
                ArrayList temp = (ArrayList)this.list.clone();
                temp.clear();
                this.list = temp;
            }
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            this.list.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object clone() {
        FastArrayList results = null;
        if (this.fast) {
            results = new FastArrayList((Collection)this.list);
        } else {
            ArrayList arrayList = this.list;
            synchronized (arrayList) {
                results = new FastArrayList((Collection)this.list);
            }
        }
        results.setFast(this.getFast());
        return results;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean contains(Object element) {
        if (this.fast) {
            return this.list.contains(element);
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            return this.list.contains(element);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean containsAll(Collection collection) {
        if (this.fast) {
            return this.list.containsAll(collection);
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            return this.list.containsAll(collection);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void ensureCapacity(int capacity) {
        if (this.fast) {
            FastArrayList fastArrayList = this;
            synchronized (fastArrayList) {
                ArrayList temp = (ArrayList)this.list.clone();
                temp.ensureCapacity(capacity);
                this.list = temp;
            }
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            this.list.ensureCapacity(capacity);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof List)) {
            return false;
        }
        List lo = (List)o;
        if (this.fast) {
            ListIterator li1 = this.list.listIterator();
            ListIterator li2 = lo.listIterator();
            while (li1.hasNext() && li2.hasNext()) {
                Object o1 = li1.next();
                Object o2 = li2.next();
                if (o1 != null ? o1.equals(o2) : o2 == null) continue;
                return false;
            }
            return !li1.hasNext() && !li2.hasNext();
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            ListIterator li1 = this.list.listIterator();
            ListIterator li2 = lo.listIterator();
            while (li1.hasNext() && li2.hasNext()) {
                Object o1 = li1.next();
                Object o2 = li2.next();
                if (o1 != null ? o1.equals(o2) : o2 == null) continue;
                return false;
            }
            return !li1.hasNext() && !li2.hasNext();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object get(int index) {
        if (this.fast) {
            return this.list.get(index);
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            return this.list.get(index);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int hashCode() {
        if (this.fast) {
            int hashCode = 1;
            Iterator i = this.list.iterator();
            while (i.hasNext()) {
                Object o = i.next();
                hashCode = 31 * hashCode + (o == null ? 0 : o.hashCode());
            }
            return hashCode;
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            int hashCode = 1;
            Iterator i = this.list.iterator();
            while (i.hasNext()) {
                Object o = i.next();
                hashCode = 31 * hashCode + (o == null ? 0 : o.hashCode());
            }
            return hashCode;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int indexOf(Object element) {
        if (this.fast) {
            return this.list.indexOf(element);
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            return this.list.indexOf(element);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isEmpty() {
        if (this.fast) {
            return this.list.isEmpty();
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            return this.list.isEmpty();
        }
    }

    public Iterator iterator() {
        if (this.fast) {
            return new ListIter(0);
        }
        return this.list.iterator();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int lastIndexOf(Object element) {
        if (this.fast) {
            return this.list.lastIndexOf(element);
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            return this.list.lastIndexOf(element);
        }
    }

    public ListIterator listIterator() {
        if (this.fast) {
            return new ListIter(0);
        }
        return this.list.listIterator();
    }

    public ListIterator listIterator(int index) {
        if (this.fast) {
            return new ListIter(index);
        }
        return this.list.listIterator(index);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object remove(int index) {
        if (this.fast) {
            FastArrayList fastArrayList = this;
            synchronized (fastArrayList) {
                ArrayList temp = (ArrayList)this.list.clone();
                Object result = temp.remove(index);
                this.list = temp;
                return result;
            }
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            return this.list.remove(index);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean remove(Object element) {
        if (this.fast) {
            FastArrayList fastArrayList = this;
            synchronized (fastArrayList) {
                ArrayList temp = (ArrayList)this.list.clone();
                boolean result = temp.remove(element);
                this.list = temp;
                return result;
            }
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            return this.list.remove(element);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean removeAll(Collection collection) {
        if (this.fast) {
            FastArrayList fastArrayList = this;
            synchronized (fastArrayList) {
                ArrayList temp = (ArrayList)this.list.clone();
                boolean result = temp.removeAll(collection);
                this.list = temp;
                return result;
            }
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            return this.list.removeAll(collection);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean retainAll(Collection collection) {
        if (this.fast) {
            FastArrayList fastArrayList = this;
            synchronized (fastArrayList) {
                ArrayList temp = (ArrayList)this.list.clone();
                boolean result = temp.retainAll(collection);
                this.list = temp;
                return result;
            }
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            return this.list.retainAll(collection);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object set(int index, Object element) {
        if (this.fast) {
            return this.list.set(index, element);
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            return this.list.set(index, element);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int size() {
        if (this.fast) {
            return this.list.size();
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            return this.list.size();
        }
    }

    public List subList(int fromIndex, int toIndex) {
        if (this.fast) {
            return new SubList(fromIndex, toIndex);
        }
        return this.list.subList(fromIndex, toIndex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object[] toArray() {
        if (this.fast) {
            return this.list.toArray();
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            return this.list.toArray();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object[] toArray(Object[] array) {
        if (this.fast) {
            return this.list.toArray(array);
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            return this.list.toArray(array);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("FastArrayList[");
        sb.append(this.list.toString());
        sb.append("]");
        return sb.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void trimToSize() {
        if (this.fast) {
            FastArrayList fastArrayList = this;
            synchronized (fastArrayList) {
                ArrayList temp = (ArrayList)this.list.clone();
                temp.trimToSize();
                this.list = temp;
            }
        }
        ArrayList arrayList = this.list;
        synchronized (arrayList) {
            this.list.trimToSize();
        }
    }

    private class ListIter
    implements ListIterator {
        private List expected;
        private ListIterator iter;
        private int lastReturnedIndex = -1;

        public ListIter(int i) {
            this.expected = FastArrayList.this.list;
            this.iter = this.get().listIterator(i);
        }

        private void checkMod() {
            if (FastArrayList.this.list != this.expected) {
                throw new ConcurrentModificationException();
            }
        }

        List get() {
            return this.expected;
        }

        public boolean hasNext() {
            return this.iter.hasNext();
        }

        public Object next() {
            this.lastReturnedIndex = this.iter.nextIndex();
            return this.iter.next();
        }

        public boolean hasPrevious() {
            return this.iter.hasPrevious();
        }

        public Object previous() {
            this.lastReturnedIndex = this.iter.previousIndex();
            return this.iter.previous();
        }

        public int previousIndex() {
            return this.iter.previousIndex();
        }

        public int nextIndex() {
            return this.iter.nextIndex();
        }

        public void remove() {
            this.checkMod();
            if (this.lastReturnedIndex < 0) {
                throw new IllegalStateException();
            }
            this.get().remove(this.lastReturnedIndex);
            this.expected = FastArrayList.this.list;
            this.iter = this.get().listIterator(this.lastReturnedIndex);
            this.lastReturnedIndex = -1;
        }

        public void set(Object o) {
            this.checkMod();
            if (this.lastReturnedIndex < 0) {
                throw new IllegalStateException();
            }
            this.get().set(this.lastReturnedIndex, o);
            this.expected = FastArrayList.this.list;
            this.iter = this.get().listIterator(this.previousIndex() + 1);
        }

        public void add(Object o) {
            this.checkMod();
            int i = this.nextIndex();
            this.get().add(i, o);
            this.expected = FastArrayList.this.list;
            this.iter = this.get().listIterator(i + 1);
            this.lastReturnedIndex = -1;
        }
    }

    private class SubList
    implements List {
        private int first;
        private int last;
        private List expected;

        public SubList(int first, int last) {
            this.first = first;
            this.last = last;
            this.expected = FastArrayList.this.list;
        }

        private List get(List l) {
            if (FastArrayList.this.list != this.expected) {
                throw new ConcurrentModificationException();
            }
            return l.subList(this.first, this.last);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void clear() {
            if (FastArrayList.this.fast) {
                FastArrayList fastArrayList = FastArrayList.this;
                synchronized (fastArrayList) {
                    ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
                    this.get(temp).clear();
                    this.last = this.first;
                    FastArrayList.this.list = temp;
                    this.expected = temp;
                }
            }
            ArrayList arrayList = FastArrayList.this.list;
            synchronized (arrayList) {
                this.get(this.expected).clear();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean remove(Object o) {
            if (FastArrayList.this.fast) {
                FastArrayList fastArrayList = FastArrayList.this;
                synchronized (fastArrayList) {
                    ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
                    boolean r = this.get(temp).remove(o);
                    if (r) {
                        --this.last;
                    }
                    FastArrayList.this.list = temp;
                    this.expected = temp;
                    return r;
                }
            }
            ArrayList arrayList = FastArrayList.this.list;
            synchronized (arrayList) {
                return this.get(this.expected).remove(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean removeAll(Collection o) {
            if (FastArrayList.this.fast) {
                FastArrayList fastArrayList = FastArrayList.this;
                synchronized (fastArrayList) {
                    ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
                    List sub = this.get(temp);
                    boolean r = sub.removeAll(o);
                    if (r) {
                        this.last = this.first + sub.size();
                    }
                    FastArrayList.this.list = temp;
                    this.expected = temp;
                    return r;
                }
            }
            ArrayList arrayList = FastArrayList.this.list;
            synchronized (arrayList) {
                return this.get(this.expected).removeAll(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean retainAll(Collection o) {
            if (FastArrayList.this.fast) {
                FastArrayList fastArrayList = FastArrayList.this;
                synchronized (fastArrayList) {
                    ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
                    List sub = this.get(temp);
                    boolean r = sub.retainAll(o);
                    if (r) {
                        this.last = this.first + sub.size();
                    }
                    FastArrayList.this.list = temp;
                    this.expected = temp;
                    return r;
                }
            }
            ArrayList arrayList = FastArrayList.this.list;
            synchronized (arrayList) {
                return this.get(this.expected).retainAll(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int size() {
            if (FastArrayList.this.fast) {
                return this.get(this.expected).size();
            }
            ArrayList arrayList = FastArrayList.this.list;
            synchronized (arrayList) {
                return this.get(this.expected).size();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean isEmpty() {
            if (FastArrayList.this.fast) {
                return this.get(this.expected).isEmpty();
            }
            ArrayList arrayList = FastArrayList.this.list;
            synchronized (arrayList) {
                return this.get(this.expected).isEmpty();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean contains(Object o) {
            if (FastArrayList.this.fast) {
                return this.get(this.expected).contains(o);
            }
            ArrayList arrayList = FastArrayList.this.list;
            synchronized (arrayList) {
                return this.get(this.expected).contains(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean containsAll(Collection o) {
            if (FastArrayList.this.fast) {
                return this.get(this.expected).containsAll(o);
            }
            ArrayList arrayList = FastArrayList.this.list;
            synchronized (arrayList) {
                return this.get(this.expected).containsAll(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Object[] toArray(Object[] o) {
            if (FastArrayList.this.fast) {
                return this.get(this.expected).toArray(o);
            }
            ArrayList arrayList = FastArrayList.this.list;
            synchronized (arrayList) {
                return this.get(this.expected).toArray(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Object[] toArray() {
            if (FastArrayList.this.fast) {
                return this.get(this.expected).toArray();
            }
            ArrayList arrayList = FastArrayList.this.list;
            synchronized (arrayList) {
                return this.get(this.expected).toArray();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (FastArrayList.this.fast) {
                return this.get(this.expected).equals(o);
            }
            ArrayList arrayList = FastArrayList.this.list;
            synchronized (arrayList) {
                return this.get(this.expected).equals(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int hashCode() {
            if (FastArrayList.this.fast) {
                return this.get(this.expected).hashCode();
            }
            ArrayList arrayList = FastArrayList.this.list;
            synchronized (arrayList) {
                return this.get(this.expected).hashCode();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean add(Object o) {
            if (FastArrayList.this.fast) {
                FastArrayList fastArrayList = FastArrayList.this;
                synchronized (fastArrayList) {
                    ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
                    boolean r = this.get(temp).add(o);
                    if (r) {
                        ++this.last;
                    }
                    FastArrayList.this.list = temp;
                    this.expected = temp;
                    return r;
                }
            }
            ArrayList arrayList = FastArrayList.this.list;
            synchronized (arrayList) {
                return this.get(this.expected).add(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean addAll(Collection o) {
            if (FastArrayList.this.fast) {
                FastArrayList fastArrayList = FastArrayList.this;
                synchronized (fastArrayList) {
                    ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
                    boolean r = this.get(temp).addAll(o);
                    if (r) {
                        this.last += o.size();
                    }
                    FastArrayList.this.list = temp;
                    this.expected = temp;
                    return r;
                }
            }
            ArrayList arrayList = FastArrayList.this.list;
            synchronized (arrayList) {
                return this.get(this.expected).addAll(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void add(int i, Object o) {
            if (FastArrayList.this.fast) {
                FastArrayList fastArrayList = FastArrayList.this;
                synchronized (fastArrayList) {
                    ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
                    this.get(temp).add(i, o);
                    ++this.last;
                    FastArrayList.this.list = temp;
                    this.expected = temp;
                }
            }
            ArrayList arrayList = FastArrayList.this.list;
            synchronized (arrayList) {
                this.get(this.expected).add(i, o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean addAll(int i, Collection o) {
            if (FastArrayList.this.fast) {
                FastArrayList fastArrayList = FastArrayList.this;
                synchronized (fastArrayList) {
                    ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
                    boolean r = this.get(temp).addAll(i, o);
                    FastArrayList.this.list = temp;
                    if (r) {
                        this.last += o.size();
                    }
                    this.expected = temp;
                    return r;
                }
            }
            ArrayList arrayList = FastArrayList.this.list;
            synchronized (arrayList) {
                return this.get(this.expected).addAll(i, o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Object remove(int i) {
            if (FastArrayList.this.fast) {
                FastArrayList fastArrayList = FastArrayList.this;
                synchronized (fastArrayList) {
                    ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
                    Object o = this.get(temp).remove(i);
                    --this.last;
                    FastArrayList.this.list = temp;
                    this.expected = temp;
                    return o;
                }
            }
            ArrayList arrayList = FastArrayList.this.list;
            synchronized (arrayList) {
                return this.get(this.expected).remove(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Object set(int i, Object a) {
            if (FastArrayList.this.fast) {
                FastArrayList fastArrayList = FastArrayList.this;
                synchronized (fastArrayList) {
                    ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
                    Object o = this.get(temp).set(i, a);
                    FastArrayList.this.list = temp;
                    this.expected = temp;
                    return o;
                }
            }
            ArrayList arrayList = FastArrayList.this.list;
            synchronized (arrayList) {
                return this.get(this.expected).set(i, a);
            }
        }

        public Iterator iterator() {
            return new SubListIter(0);
        }

        public ListIterator listIterator() {
            return new SubListIter(0);
        }

        public ListIterator listIterator(int i) {
            return new SubListIter(i);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Object get(int i) {
            if (FastArrayList.this.fast) {
                return this.get(this.expected).get(i);
            }
            ArrayList arrayList = FastArrayList.this.list;
            synchronized (arrayList) {
                return this.get(this.expected).get(i);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int indexOf(Object o) {
            if (FastArrayList.this.fast) {
                return this.get(this.expected).indexOf(o);
            }
            ArrayList arrayList = FastArrayList.this.list;
            synchronized (arrayList) {
                return this.get(this.expected).indexOf(o);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public int lastIndexOf(Object o) {
            if (FastArrayList.this.fast) {
                return this.get(this.expected).lastIndexOf(o);
            }
            ArrayList arrayList = FastArrayList.this.list;
            synchronized (arrayList) {
                return this.get(this.expected).lastIndexOf(o);
            }
        }

        public List subList(int f, int l) {
            if (FastArrayList.this.list != this.expected) {
                throw new ConcurrentModificationException();
            }
            return new SubList(this.first + f, f + l);
        }

        private class SubListIter
        implements ListIterator {
            private List expected;
            private ListIterator iter;
            private int lastReturnedIndex = -1;

            public SubListIter(int i) {
                this.expected = ((SubList)SubList.this).FastArrayList.this.list;
                this.iter = SubList.this.get(this.expected).listIterator(i);
            }

            private void checkMod() {
                if (((SubList)SubList.this).FastArrayList.this.list != this.expected) {
                    throw new ConcurrentModificationException();
                }
            }

            List get() {
                return SubList.this.get(this.expected);
            }

            public boolean hasNext() {
                this.checkMod();
                return this.iter.hasNext();
            }

            public Object next() {
                this.checkMod();
                this.lastReturnedIndex = this.iter.nextIndex();
                return this.iter.next();
            }

            public boolean hasPrevious() {
                this.checkMod();
                return this.iter.hasPrevious();
            }

            public Object previous() {
                this.checkMod();
                this.lastReturnedIndex = this.iter.previousIndex();
                return this.iter.previous();
            }

            public int previousIndex() {
                this.checkMod();
                return this.iter.previousIndex();
            }

            public int nextIndex() {
                this.checkMod();
                return this.iter.nextIndex();
            }

            public void remove() {
                this.checkMod();
                if (this.lastReturnedIndex < 0) {
                    throw new IllegalStateException();
                }
                this.get().remove(this.lastReturnedIndex);
                SubList.this.last--;
                this.expected = ((SubList)SubList.this).FastArrayList.this.list;
                this.iter = this.get().listIterator(this.lastReturnedIndex);
                this.lastReturnedIndex = -1;
            }

            public void set(Object o) {
                this.checkMod();
                if (this.lastReturnedIndex < 0) {
                    throw new IllegalStateException();
                }
                this.get().set(this.lastReturnedIndex, o);
                this.expected = ((SubList)SubList.this).FastArrayList.this.list;
                this.iter = this.get().listIterator(this.previousIndex() + 1);
            }

            public void add(Object o) {
                this.checkMod();
                int i = this.nextIndex();
                this.get().add(i, o);
                SubList.this.last++;
                this.expected = ((SubList)SubList.this).FastArrayList.this.list;
                this.iter = this.get().listIterator(i + 1);
                this.lastReturnedIndex = -1;
            }
        }
    }
}

