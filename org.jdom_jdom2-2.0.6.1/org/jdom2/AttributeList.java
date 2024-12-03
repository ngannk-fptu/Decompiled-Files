/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.IllegalAddException;
import org.jdom2.Namespace;
import org.jdom2.Verifier;
import org.jdom2.internal.ArrayCopy;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class AttributeList
extends AbstractList<Attribute>
implements RandomAccess {
    private static final int INITIAL_ARRAY_SIZE = 4;
    private Attribute[] attributeData;
    private int size;
    private final Element parent;
    private static final Comparator<Attribute> ATTRIBUTE_NATURAL = new Comparator<Attribute>(){

        @Override
        public int compare(Attribute a1, Attribute a2) {
            int pcomp = a1.getNamespacePrefix().compareTo(a2.getNamespacePrefix());
            if (pcomp != 0) {
                return pcomp;
            }
            return a1.getName().compareTo(a2.getName());
        }
    };

    AttributeList(Element parent) {
        this.parent = parent;
    }

    final void uncheckedAddAttribute(Attribute a) {
        a.parent = this.parent;
        this.ensureCapacity(this.size + 1);
        this.attributeData[this.size++] = a;
        ++this.modCount;
    }

    @Override
    public boolean add(Attribute attribute) {
        if (attribute.getParent() != null) {
            throw new IllegalAddException("The attribute already has an existing parent \"" + attribute.getParent().getQualifiedName() + "\"");
        }
        if (Verifier.checkNamespaceCollision(attribute, this.parent) != null) {
            throw new IllegalAddException(this.parent, attribute, Verifier.checkNamespaceCollision(attribute, this.parent));
        }
        int duplicate = this.indexOfDuplicate(attribute);
        if (duplicate < 0) {
            attribute.setParent(this.parent);
            this.ensureCapacity(this.size + 1);
            this.attributeData[this.size++] = attribute;
            ++this.modCount;
        } else {
            Attribute old = this.attributeData[duplicate];
            old.setParent(null);
            this.attributeData[duplicate] = attribute;
            attribute.setParent(this.parent);
        }
        return true;
    }

    @Override
    public void add(int index, Attribute attribute) {
        if (index < 0 || index > this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        if (attribute.getParent() != null) {
            throw new IllegalAddException("The attribute already has an existing parent \"" + attribute.getParent().getQualifiedName() + "\"");
        }
        int duplicate = this.indexOfDuplicate(attribute);
        if (duplicate >= 0) {
            throw new IllegalAddException("Cannot add duplicate attribute");
        }
        String reason = Verifier.checkNamespaceCollision(attribute, this.parent);
        if (reason != null) {
            throw new IllegalAddException(this.parent, attribute, reason);
        }
        attribute.setParent(this.parent);
        this.ensureCapacity(this.size + 1);
        if (index == this.size) {
            this.attributeData[this.size++] = attribute;
        } else {
            System.arraycopy(this.attributeData, index, this.attributeData, index + 1, this.size - index);
            this.attributeData[index] = attribute;
            ++this.size;
        }
        ++this.modCount;
    }

    @Override
    public boolean addAll(Collection<? extends Attribute> collection) {
        return this.addAll(this.size(), collection);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean addAll(int index, Collection<? extends Attribute> collection) {
        if (index < 0 || index > this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        if (collection == null) {
            throw new NullPointerException("Can not add a null Collection to AttributeList");
        }
        int addcnt = collection.size();
        if (addcnt == 0) {
            return false;
        }
        if (addcnt == 1) {
            this.add(index, collection.iterator().next());
            return true;
        }
        this.ensureCapacity(this.size() + addcnt);
        int tmpmodcount = this.modCount;
        boolean ok = false;
        int count = 0;
        try {
            for (Attribute attribute : collection) {
                this.add(index + count, attribute);
                ++count;
            }
            ok = true;
        }
        finally {
            if (!ok) {
                while (--count >= 0) {
                    this.remove(index + count);
                }
                this.modCount = tmpmodcount;
            }
        }
        return true;
    }

    @Override
    public void clear() {
        if (this.attributeData != null) {
            while (this.size > 0) {
                --this.size;
                this.attributeData[this.size].setParent(null);
                this.attributeData[this.size] = null;
            }
        }
        ++this.modCount;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void clearAndSet(Collection<? extends Attribute> collection) {
        if (collection == null || collection.isEmpty()) {
            this.clear();
            return;
        }
        Attribute[] old = this.attributeData;
        int oldSize = this.size;
        int oldModCount = this.modCount;
        while (this.size > 0) {
            old[--this.size].setParent(null);
        }
        this.size = 0;
        this.attributeData = null;
        boolean ok = false;
        try {
            this.addAll(0, collection);
            ok = true;
        }
        finally {
            if (!ok) {
                this.attributeData = old;
                while (this.size < oldSize) {
                    this.attributeData[this.size++].setParent(this.parent);
                }
                this.modCount = oldModCount;
            }
        }
    }

    private void ensureCapacity(int minCapacity) {
        if (this.attributeData == null) {
            this.attributeData = new Attribute[Math.max(minCapacity, 4)];
            return;
        }
        if (minCapacity < this.attributeData.length) {
            return;
        }
        this.attributeData = ArrayCopy.copyOf(this.attributeData, minCapacity + 4 >>> 1 << 1);
    }

    @Override
    public Attribute get(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        return this.attributeData[index];
    }

    Attribute get(String name, Namespace namespace) {
        int index = this.indexOf(name, namespace);
        if (index < 0) {
            return null;
        }
        return this.attributeData[index];
    }

    int indexOf(String name, Namespace namespace) {
        if (this.attributeData != null) {
            if (namespace == null) {
                return this.indexOf(name, Namespace.NO_NAMESPACE);
            }
            String uri = namespace.getURI();
            for (int i = 0; i < this.size; ++i) {
                Attribute att = this.attributeData[i];
                if (!att.getNamespaceURI().equals(uri) || !att.getName().equals(name)) continue;
                return i;
            }
        }
        return -1;
    }

    @Override
    public Attribute remove(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        Attribute old = this.attributeData[index];
        old.setParent(null);
        System.arraycopy(this.attributeData, index + 1, this.attributeData, index, this.size - index - 1);
        this.attributeData[--this.size] = null;
        ++this.modCount;
        return old;
    }

    boolean remove(String name, Namespace namespace) {
        int index = this.indexOf(name, namespace);
        if (index < 0) {
            return false;
        }
        this.remove(index);
        return true;
    }

    @Override
    public Attribute set(int index, Attribute attribute) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        if (attribute.getParent() != null) {
            throw new IllegalAddException("The attribute already has an existing parent \"" + attribute.getParent().getQualifiedName() + "\"");
        }
        int duplicate = this.indexOfDuplicate(attribute);
        if (duplicate >= 0 && duplicate != index) {
            throw new IllegalAddException("Cannot set duplicate attribute");
        }
        String reason = Verifier.checkNamespaceCollision(attribute, this.parent, index);
        if (reason != null) {
            throw new IllegalAddException(this.parent, attribute, reason);
        }
        Attribute old = this.attributeData[index];
        old.setParent(null);
        this.attributeData[index] = attribute;
        attribute.setParent(this.parent);
        return old;
    }

    private int indexOfDuplicate(Attribute attribute) {
        return this.indexOf(attribute.getName(), attribute.getNamespace());
    }

    @Override
    public Iterator<Attribute> iterator() {
        return new ALIterator();
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    private final int binarySearch(int[] indexes, int len, int val, Comparator<? super Attribute> comp) {
        int left = 0;
        int mid = 0;
        int right = len - 1;
        int cmp = 0;
        Attribute base = this.attributeData[val];
        while (left <= right) {
            mid = left + right >>> 1;
            cmp = comp.compare(base, this.attributeData[indexes[mid]]);
            if (cmp == 0) {
                while (cmp == 0 && mid < right && comp.compare(base, this.attributeData[indexes[mid + 1]]) == 0) {
                    ++mid;
                }
                return mid + 1;
            }
            if (cmp < 0) {
                right = mid - 1;
                continue;
            }
            left = mid + 1;
        }
        return left;
    }

    private void sortInPlace(int[] indexes) {
        int i;
        int[] unsorted = ArrayCopy.copyOf(indexes, indexes.length);
        Arrays.sort(unsorted);
        Attribute[] usc = new Attribute[unsorted.length];
        for (i = 0; i < usc.length; ++i) {
            usc[i] = this.attributeData[indexes[i]];
        }
        for (i = 0; i < indexes.length; ++i) {
            this.attributeData[unsorted[i]] = usc[i];
        }
    }

    @Override
    public void sort(Comparator<? super Attribute> comp) {
        if (comp == null) {
            comp = ATTRIBUTE_NATURAL;
        }
        int sz = this.size;
        int[] indexes = new int[sz];
        int i = 0;
        while (i < sz) {
            int ip = this.binarySearch(indexes, i, i, comp);
            if (ip < i) {
                System.arraycopy(indexes, ip, indexes, ip + 1, i - ip);
            }
            indexes[ip] = i++;
        }
        this.sortInPlace(indexes);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private final class ALIterator
    implements Iterator<Attribute> {
        private int expect = -1;
        private int cursor = 0;
        private boolean canremove = false;

        private ALIterator() {
            this.expect = AttributeList.this.modCount;
        }

        @Override
        public boolean hasNext() {
            return this.cursor < AttributeList.this.size;
        }

        @Override
        public Attribute next() {
            if (AttributeList.this.modCount != this.expect) {
                throw new ConcurrentModificationException("ContentList was modified outside of this Iterator");
            }
            if (this.cursor >= AttributeList.this.size) {
                throw new NoSuchElementException("Iterated beyond the end of the ContentList.");
            }
            this.canremove = true;
            return AttributeList.this.attributeData[this.cursor++];
        }

        @Override
        public void remove() {
            if (AttributeList.this.modCount != this.expect) {
                throw new ConcurrentModificationException("ContentList was modified outside of this Iterator");
            }
            if (!this.canremove) {
                throw new IllegalStateException("Can only remove() content after a call to next()");
            }
            AttributeList.this.remove(--this.cursor);
            this.expect = AttributeList.this.modCount;
            this.canremove = false;
        }
    }
}

