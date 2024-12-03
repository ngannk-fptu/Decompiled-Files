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
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import org.jdom2.Content;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.IllegalAddException;
import org.jdom2.Parent;
import org.jdom2.filter.Filter;
import org.jdom2.internal.ArrayCopy;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class ContentList
extends AbstractList<Content>
implements RandomAccess {
    private static final int INITIAL_ARRAY_SIZE = 4;
    private Content[] elementData = null;
    private int size;
    private transient int sizeModCount = Integer.MIN_VALUE;
    private transient int dataModiCount = Integer.MIN_VALUE;
    private final Parent parent;

    ContentList(Parent parent) {
        this.parent = parent;
    }

    final void uncheckedAddContent(Content c) {
        c.parent = this.parent;
        this.ensureCapacity(this.size + 1);
        this.elementData[this.size++] = c;
        this.incModCount();
    }

    private final void setModCount(int sizemod, int datamod) {
        this.sizeModCount = sizemod;
        this.dataModiCount = datamod;
    }

    private final int getModCount() {
        return this.sizeModCount;
    }

    private final void incModCount() {
        ++this.dataModiCount;
        ++this.sizeModCount;
    }

    private final void incDataModOnly() {
        ++this.dataModiCount;
    }

    private final int getDataModCount() {
        return this.dataModiCount;
    }

    private final void checkIndex(int index, boolean excludes) {
        int max;
        int n = max = excludes ? this.size - 1 : this.size;
        if (index < 0 || index > max) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size);
        }
    }

    private final void checkPreConditions(Content child, int index, boolean replace) {
        if (child == null) {
            throw new NullPointerException("Cannot add null object");
        }
        this.checkIndex(index, replace);
        if (child.getParent() != null) {
            Parent p = child.getParent();
            if (p instanceof Document) {
                throw new IllegalAddException((Element)child, "The Content already has an existing parent document");
            }
            throw new IllegalAddException("The Content already has an existing parent \"" + ((Element)p).getQualifiedName() + "\"");
        }
        if (child == this.parent) {
            throw new IllegalAddException("The Element cannot be added to itself");
        }
        if (this.parent instanceof Element && child instanceof Element && ((Element)child).isAncestor((Element)this.parent)) {
            throw new IllegalAddException("The Element cannot be added as a descendent of itself");
        }
    }

    @Override
    public void add(int index, Content child) {
        this.checkPreConditions(child, index, false);
        this.parent.canContainContent(child, index, false);
        child.setParent(this.parent);
        this.ensureCapacity(this.size + 1);
        if (index == this.size) {
            this.elementData[this.size++] = child;
        } else {
            System.arraycopy(this.elementData, index, this.elementData, index + 1, this.size - index);
            this.elementData[index] = child;
            ++this.size;
        }
        this.incModCount();
    }

    @Override
    public boolean addAll(Collection<? extends Content> collection) {
        return this.addAll(this.size, collection);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean addAll(int index, Collection<? extends Content> collection) {
        if (collection == null) {
            throw new NullPointerException("Can not add a null collection to the ContentList");
        }
        this.checkIndex(index, false);
        if (collection.isEmpty()) {
            return false;
        }
        int addcnt = collection.size();
        if (addcnt == 1) {
            this.add(index, collection.iterator().next());
            return true;
        }
        this.ensureCapacity(this.size() + addcnt);
        int tmpmodcount = this.getModCount();
        int tmpdmc = this.getDataModCount();
        boolean ok = false;
        int count = 0;
        try {
            for (Content content : collection) {
                this.add(index + count, content);
                ++count;
            }
            ok = true;
        }
        finally {
            if (!ok) {
                while (--count >= 0) {
                    this.remove(index + count);
                }
                this.setModCount(tmpmodcount, tmpdmc);
            }
        }
        return true;
    }

    @Override
    public void clear() {
        if (this.elementData != null) {
            for (int i = 0; i < this.size; ++i) {
                Content obj = this.elementData[i];
                ContentList.removeParent(obj);
            }
            this.elementData = null;
            this.size = 0;
        }
        this.incModCount();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void clearAndSet(Collection<? extends Content> collection) {
        if (collection == null || collection.isEmpty()) {
            this.clear();
            return;
        }
        Content[] old = this.elementData;
        int oldSize = this.size;
        int oldModCount = this.getModCount();
        int oldDataModCount = this.getDataModCount();
        while (this.size > 0) {
            old[--this.size].setParent(null);
        }
        this.size = 0;
        this.elementData = null;
        boolean ok = false;
        try {
            this.addAll(0, collection);
            ok = true;
        }
        finally {
            if (!ok) {
                this.elementData = old;
                while (this.size < oldSize) {
                    this.elementData[this.size++].setParent(this.parent);
                }
                this.setModCount(oldModCount, oldDataModCount);
            }
        }
    }

    void ensureCapacity(int minCapacity) {
        if (this.elementData == null) {
            this.elementData = new Content[Math.max(minCapacity, 4)];
            return;
        }
        if (minCapacity < this.elementData.length) {
            return;
        }
        int newcap = this.size * 3 / 2 + 1;
        this.elementData = ArrayCopy.copyOf(this.elementData, newcap < minCapacity ? minCapacity : newcap);
    }

    @Override
    public Content get(int index) {
        this.checkIndex(index, true);
        return this.elementData[index];
    }

    <E extends Content> List<E> getView(Filter<E> filter) {
        return new FilterList<E>(filter);
    }

    int indexOfFirstElement() {
        if (this.elementData != null) {
            for (int i = 0; i < this.size; ++i) {
                if (!(this.elementData[i] instanceof Element)) continue;
                return i;
            }
        }
        return -1;
    }

    int indexOfDocType() {
        if (this.elementData != null) {
            for (int i = 0; i < this.size; ++i) {
                if (!(this.elementData[i] instanceof DocType)) continue;
                return i;
            }
        }
        return -1;
    }

    @Override
    public Content remove(int index) {
        this.checkIndex(index, true);
        Content old = this.elementData[index];
        ContentList.removeParent(old);
        System.arraycopy(this.elementData, index + 1, this.elementData, index, this.size - index - 1);
        this.elementData[--this.size] = null;
        this.incModCount();
        return old;
    }

    private static void removeParent(Content c) {
        c.setParent(null);
    }

    @Override
    public Content set(int index, Content child) {
        this.checkPreConditions(child, index, true);
        this.parent.canContainContent(child, index, true);
        Content old = this.elementData[index];
        ContentList.removeParent(old);
        child.setParent(this.parent);
        this.elementData[index] = child;
        this.incDataModOnly();
        return old;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<Content> iterator() {
        return new CLIterator();
    }

    @Override
    public ListIterator<Content> listIterator() {
        return new CLListIterator(0);
    }

    @Override
    public ListIterator<Content> listIterator(int start) {
        return new CLListIterator(start);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    private void sortInPlace(int[] indexes) {
        int i;
        int[] unsorted = ArrayCopy.copyOf(indexes, indexes.length);
        Arrays.sort(unsorted);
        Content[] usc = new Content[unsorted.length];
        for (i = 0; i < usc.length; ++i) {
            usc[i] = this.elementData[indexes[i]];
        }
        for (i = 0; i < indexes.length; ++i) {
            this.elementData[unsorted[i]] = usc[i];
        }
    }

    private final int binarySearch(int[] indexes, int len, int val, Comparator<? super Content> comp) {
        int left = 0;
        int mid = 0;
        int right = len - 1;
        int cmp = 0;
        Content base = this.elementData[val];
        while (left <= right) {
            mid = left + right >>> 1;
            cmp = comp.compare(base, this.elementData[indexes[mid]]);
            if (cmp == 0) {
                while (cmp == 0 && mid < right && comp.compare(base, this.elementData[indexes[mid + 1]]) == 0) {
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

    @Override
    public final void sort(Comparator<? super Content> comp) {
        if (comp == null) {
            return;
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
    final class FilterListIterator<F extends Content>
    implements ListIterator<F> {
        private final FilterList<F> filterlist;
        private boolean forward = false;
        private boolean canremove = false;
        private boolean canset = false;
        private int expectedmod = -1;
        private int cursor = -1;

        FilterListIterator(FilterList<F> flist, int start) {
            this.filterlist = flist;
            this.expectedmod = ContentList.this.getModCount();
            this.forward = false;
            if (start < 0) {
                throw new IndexOutOfBoundsException("Index: " + start + " Size: " + this.filterlist.size());
            }
            int adj = ((FilterList)this.filterlist).resync(start);
            if (adj == ContentList.this.size && start > this.filterlist.size()) {
                throw new IndexOutOfBoundsException("Index: " + start + " Size: " + this.filterlist.size());
            }
            this.cursor = start;
        }

        private void checkConcurrent() {
            if (this.expectedmod != ContentList.this.getModCount()) {
                throw new ConcurrentModificationException("The ContentList supporting the FilterList this iterator is processing has been modified by something other than this Iterator.");
            }
        }

        @Override
        public boolean hasNext() {
            return ((FilterList)this.filterlist).resync(this.forward ? this.cursor + 1 : this.cursor) < ContentList.this.size;
        }

        @Override
        public boolean hasPrevious() {
            return (this.forward ? this.cursor : this.cursor - 1) >= 0;
        }

        @Override
        public int nextIndex() {
            return this.forward ? this.cursor + 1 : this.cursor;
        }

        @Override
        public int previousIndex() {
            return this.forward ? this.cursor : this.cursor - 1;
        }

        @Override
        public F next() {
            int next;
            this.checkConcurrent();
            int n = next = this.forward ? this.cursor + 1 : this.cursor;
            if (((FilterList)this.filterlist).resync(next) >= ContentList.this.size) {
                throw new NoSuchElementException("next() is beyond the end of the Iterator");
            }
            this.cursor = next;
            this.forward = true;
            this.canremove = true;
            this.canset = true;
            return (F)this.filterlist.get(this.cursor);
        }

        @Override
        public F previous() {
            int prev;
            this.checkConcurrent();
            int n = prev = this.forward ? this.cursor : this.cursor - 1;
            if (prev < 0) {
                throw new NoSuchElementException("previous() is beyond the beginning of the Iterator");
            }
            this.cursor = prev;
            this.forward = false;
            this.canremove = true;
            this.canset = true;
            return (F)this.filterlist.get(this.cursor);
        }

        @Override
        public void add(Content obj) {
            this.checkConcurrent();
            int next = this.forward ? this.cursor + 1 : this.cursor;
            this.filterlist.add(next, obj);
            this.expectedmod = ContentList.this.getModCount();
            this.canset = false;
            this.canremove = false;
            this.cursor = next;
            this.forward = true;
        }

        @Override
        public void remove() {
            this.checkConcurrent();
            if (!this.canremove) {
                throw new IllegalStateException("Can not remove an element unless either next() or previous() has been called since the last remove()");
            }
            this.filterlist.remove(this.cursor);
            this.forward = false;
            this.expectedmod = ContentList.this.getModCount();
            this.canremove = false;
            this.canset = false;
        }

        @Override
        public void set(F obj) {
            this.checkConcurrent();
            if (!this.canset) {
                throw new IllegalStateException("Can not set an element unless either next() or previous() has been called since the last remove() or set()");
            }
            this.filterlist.set(this.cursor, obj);
            this.expectedmod = ContentList.this.getModCount();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    class FilterList<F extends Content>
    extends AbstractList<F> {
        final Filter<F> filter;
        int[] backingpos;
        int backingsize;
        int xdata;

        FilterList(Filter<F> filter) {
            this.backingpos = new int[ContentList.this.size + 4];
            this.backingsize = 0;
            this.xdata = -1;
            this.filter = filter;
        }

        @Override
        public boolean isEmpty() {
            return this.resync(0) == ContentList.this.size;
        }

        private final int resync(int index) {
            if (this.xdata != ContentList.this.getDataModCount()) {
                this.xdata = ContentList.this.getDataModCount();
                this.backingsize = 0;
                if (ContentList.this.size >= this.backingpos.length) {
                    this.backingpos = new int[ContentList.this.size + 1];
                }
            }
            if (index >= 0 && index < this.backingsize) {
                return this.backingpos[index];
            }
            int bpi = 0;
            if (this.backingsize > 0) {
                bpi = this.backingpos[this.backingsize - 1] + 1;
            }
            while (bpi < ContentList.this.size) {
                Content gotit = (Content)this.filter.filter(ContentList.this.elementData[bpi]);
                if (gotit != null) {
                    this.backingpos[this.backingsize] = bpi;
                    if (this.backingsize++ == index) {
                        return bpi;
                    }
                }
                ++bpi;
            }
            return ContentList.this.size;
        }

        @Override
        public void add(int index, Content obj) {
            if (index < 0) {
                throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
            }
            int adj = this.resync(index);
            if (adj == ContentList.this.size && index > this.size()) {
                throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
            }
            if (this.filter.matches(obj)) {
                ContentList.this.add(adj, obj);
                if (this.backingpos.length <= ContentList.this.size) {
                    this.backingpos = ArrayCopy.copyOf(this.backingpos, this.backingpos.length + 1);
                }
            } else {
                throw new IllegalAddException("Filter won't allow the " + obj.getClass().getName() + " '" + obj + "' to be added to the list");
            }
            this.backingpos[index] = adj;
            this.backingsize = index + 1;
            this.xdata = ContentList.this.getDataModCount();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean addAll(int index, Collection<? extends F> collection) {
            if (collection == null) {
                throw new NullPointerException("Cannot add a null collection");
            }
            if (index < 0) {
                throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
            }
            int adj = this.resync(index);
            if (adj == ContentList.this.size && index > this.size()) {
                throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
            }
            int addcnt = collection.size();
            if (addcnt == 0) {
                return false;
            }
            ContentList.this.ensureCapacity(ContentList.this.size() + addcnt);
            int tmpmodcount = ContentList.this.getModCount();
            int tmpdmc = ContentList.this.getDataModCount();
            boolean ok = false;
            int count = 0;
            try {
                for (Content c : collection) {
                    if (c == null) {
                        throw new NullPointerException("Cannot add null content");
                    }
                    if (this.filter.matches(c)) {
                        ContentList.this.add(adj + count, c);
                        if (this.backingpos.length <= ContentList.this.size) {
                            this.backingpos = ArrayCopy.copyOf(this.backingpos, this.backingpos.length + addcnt);
                        }
                        this.backingpos[index + count] = adj + count;
                        this.backingsize = index + count + 1;
                        this.xdata = ContentList.this.getDataModCount();
                        ++count;
                        continue;
                    }
                    throw new IllegalAddException("Filter won't allow the " + c.getClass().getName() + " '" + c + "' to be added to the list");
                }
                ok = true;
            }
            finally {
                if (!ok) {
                    while (--count >= 0) {
                        ContentList.this.remove(adj + count);
                    }
                    ContentList.this.setModCount(tmpmodcount, tmpdmc);
                    this.backingsize = index;
                    this.xdata = tmpmodcount;
                }
            }
            return true;
        }

        @Override
        public F get(int index) {
            if (index < 0) {
                throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
            }
            int adj = this.resync(index);
            if (adj == ContentList.this.size) {
                throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
            }
            return (F)((Content)this.filter.filter(ContentList.this.get(adj)));
        }

        @Override
        public Iterator<F> iterator() {
            return new FilterListIterator(this, 0);
        }

        @Override
        public ListIterator<F> listIterator() {
            return new FilterListIterator(this, 0);
        }

        @Override
        public ListIterator<F> listIterator(int index) {
            return new FilterListIterator(this, index);
        }

        @Override
        public F remove(int index) {
            if (index < 0) {
                throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
            }
            int adj = this.resync(index);
            if (adj == ContentList.this.size) {
                throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
            }
            Content oldc = ContentList.this.remove(adj);
            this.backingsize = index;
            this.xdata = ContentList.this.getDataModCount();
            return (F)((Content)this.filter.filter(oldc));
        }

        @Override
        public F set(int index, F obj) {
            if (index < 0) {
                throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
            }
            int adj = this.resync(index);
            if (adj == ContentList.this.size) {
                throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
            }
            Content ins = (Content)this.filter.filter(obj);
            if (ins != null) {
                Content oldc = (Content)this.filter.filter(ContentList.this.set(adj, ins));
                this.xdata = ContentList.this.getDataModCount();
                return (F)oldc;
            }
            throw new IllegalAddException("Filter won't allow index " + index + " to be set to " + obj.getClass().getName());
        }

        @Override
        public int size() {
            this.resync(-1);
            return this.backingsize;
        }

        private final int fbinarySearch(int[] indexes, int len, int val, Comparator<? super F> comp) {
            int left = 0;
            int mid = 0;
            int right = len - 1;
            int cmp = 0;
            Content base = ContentList.this.elementData[this.backingpos[val]];
            while (left <= right) {
                cmp = comp.compare(base, ContentList.this.elementData[indexes[mid]]);
                if (cmp == 0) {
                    for (mid = left + right >>> 1; cmp == 0 && mid < right && comp.compare(base, ContentList.this.elementData[indexes[mid + 1]]) == 0; ++mid) {
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

        @Override
        public final void sort(Comparator<? super F> comp) {
            if (comp == null) {
                return;
            }
            int sz = this.size();
            int[] indexes = new int[sz];
            for (int i = 0; i < sz; ++i) {
                int ip = this.fbinarySearch(indexes, i, i, comp);
                if (ip < i) {
                    System.arraycopy(indexes, ip, indexes, ip + 1, i - ip);
                }
                indexes[ip] = this.backingpos[i];
            }
            ContentList.this.sortInPlace(indexes);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private final class CLListIterator
    implements ListIterator<Content> {
        private boolean forward = false;
        private boolean canremove = false;
        private boolean canset = false;
        private int expectedmod = -1;
        private int cursor = -1;

        CLListIterator(int start) {
            this.expectedmod = ContentList.this.getModCount();
            this.forward = false;
            ContentList.this.checkIndex(start, false);
            this.cursor = start;
        }

        private void checkConcurrent() {
            if (this.expectedmod != ContentList.this.getModCount()) {
                throw new ConcurrentModificationException("The ContentList supporting this iterator has been modified bysomething other than this Iterator.");
            }
        }

        @Override
        public boolean hasNext() {
            return (this.forward ? this.cursor + 1 : this.cursor) < ContentList.this.size;
        }

        @Override
        public boolean hasPrevious() {
            return (this.forward ? this.cursor : this.cursor - 1) >= 0;
        }

        @Override
        public int nextIndex() {
            return this.forward ? this.cursor + 1 : this.cursor;
        }

        @Override
        public int previousIndex() {
            return this.forward ? this.cursor : this.cursor - 1;
        }

        @Override
        public Content next() {
            int next;
            this.checkConcurrent();
            int n = next = this.forward ? this.cursor + 1 : this.cursor;
            if (next >= ContentList.this.size) {
                throw new NoSuchElementException("next() is beyond the end of the Iterator");
            }
            this.cursor = next;
            this.forward = true;
            this.canremove = true;
            this.canset = true;
            return ContentList.this.elementData[this.cursor];
        }

        @Override
        public Content previous() {
            int prev;
            this.checkConcurrent();
            int n = prev = this.forward ? this.cursor : this.cursor - 1;
            if (prev < 0) {
                throw new NoSuchElementException("previous() is beyond the beginning of the Iterator");
            }
            this.cursor = prev;
            this.forward = false;
            this.canremove = true;
            this.canset = true;
            return ContentList.this.elementData[this.cursor];
        }

        @Override
        public void add(Content obj) {
            this.checkConcurrent();
            int next = this.forward ? this.cursor + 1 : this.cursor;
            ContentList.this.add(next, obj);
            this.expectedmod = ContentList.this.getModCount();
            this.canset = false;
            this.canremove = false;
            this.cursor = next;
            this.forward = true;
        }

        @Override
        public void remove() {
            this.checkConcurrent();
            if (!this.canremove) {
                throw new IllegalStateException("Can not remove an element unless either next() or previous() has been called since the last remove()");
            }
            ContentList.this.remove(this.cursor);
            this.forward = false;
            this.expectedmod = ContentList.this.getModCount();
            this.canremove = false;
            this.canset = false;
        }

        @Override
        public void set(Content obj) {
            this.checkConcurrent();
            if (!this.canset) {
                throw new IllegalStateException("Can not set an element unless either next() or previous() has been called since the last remove() or set()");
            }
            ContentList.this.set(this.cursor, obj);
            this.expectedmod = ContentList.this.getModCount();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private final class CLIterator
    implements Iterator<Content> {
        private int expect = -1;
        private int cursor = 0;
        private boolean canremove = false;

        private CLIterator() {
            this.expect = ContentList.this.getModCount();
        }

        @Override
        public boolean hasNext() {
            return this.cursor < ContentList.this.size;
        }

        @Override
        public Content next() {
            if (ContentList.this.getModCount() != this.expect) {
                throw new ConcurrentModificationException("ContentList was modified outside of this Iterator");
            }
            if (this.cursor >= ContentList.this.size) {
                throw new NoSuchElementException("Iterated beyond the end of the ContentList.");
            }
            this.canremove = true;
            return ContentList.this.elementData[this.cursor++];
        }

        @Override
        public void remove() {
            if (ContentList.this.getModCount() != this.expect) {
                throw new ConcurrentModificationException("ContentList was modified outside of this Iterator");
            }
            if (!this.canremove) {
                throw new IllegalStateException("Can only remove() content after a call to next()");
            }
            this.canremove = false;
            ContentList.this.remove(--this.cursor);
            this.expect = ContentList.this.getModCount();
        }
    }
}

