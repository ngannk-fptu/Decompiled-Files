/*
 * Decompiled with CFR 0.152.
 */
package org.jdom;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.jdom.CDATA;
import org.jdom.Content;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.IllegalAddException;
import org.jdom.Parent;
import org.jdom.Text;
import org.jdom.filter.Filter;

final class ContentList
extends AbstractList
implements Serializable {
    private static final String CVS_ID = "@(#) $RCSfile: ContentList.java,v $ $Revision: 1.42 $ $Date: 2007/11/10 05:28:58 $ $Name:  $";
    private static final long serialVersionUID = 1L;
    private static final int INITIAL_ARRAY_SIZE = 5;
    private Content[] elementData;
    private int size;
    private Parent parent;

    ContentList(Parent parent) {
        this.parent = parent;
    }

    final void uncheckedAddContent(Content c) {
        c.parent = this.parent;
        this.ensureCapacity(this.size + 1);
        this.elementData[this.size++] = c;
        ++this.modCount;
    }

    @Override
    public void add(int index, Object obj) {
        if (obj == null) {
            throw new IllegalAddException("Cannot add null object");
        }
        if (obj instanceof String) {
            obj = new Text(obj.toString());
        }
        if (!(obj instanceof Content)) {
            throw new IllegalAddException("Class " + obj.getClass().getName() + " is of unrecognized type and cannot be added");
        }
        this.add(index, (Content)obj);
    }

    private void documentCanContain(int index, Content child) throws IllegalAddException {
        if (child instanceof Element) {
            if (this.indexOfFirstElement() >= 0) {
                throw new IllegalAddException("Cannot add a second root element, only one is allowed");
            }
            if (this.indexOfDocType() >= index) {
                throw new IllegalAddException("A root element cannot be added before the DocType");
            }
        }
        if (child instanceof DocType) {
            if (this.indexOfDocType() >= 0) {
                throw new IllegalAddException("Cannot add a second doctype, only one is allowed");
            }
            int firstElt = this.indexOfFirstElement();
            if (firstElt != -1 && firstElt < index) {
                throw new IllegalAddException("A DocType cannot be added after the root element");
            }
        }
        if (child instanceof CDATA) {
            throw new IllegalAddException("A CDATA is not allowed at the document root");
        }
        if (child instanceof Text) {
            throw new IllegalAddException("A Text is not allowed at the document root");
        }
        if (child instanceof EntityRef) {
            throw new IllegalAddException("An EntityRef is not allowed at the document root");
        }
    }

    private static void elementCanContain(int index, Content child) throws IllegalAddException {
        if (child instanceof DocType) {
            throw new IllegalAddException("A DocType is not allowed except at the document level");
        }
    }

    @Override
    void add(int index, Content child) {
        if (child == null) {
            throw new IllegalAddException("Cannot add null object");
        }
        if (this.parent instanceof Document) {
            this.documentCanContain(index, child);
        } else {
            ContentList.elementCanContain(index, child);
        }
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
        if (index < 0 || index > this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        child.setParent(this.parent);
        this.ensureCapacity(this.size + 1);
        if (index == this.size) {
            this.elementData[this.size++] = child;
        } else {
            System.arraycopy(this.elementData, index, this.elementData, index + 1, this.size - index);
            this.elementData[index] = child;
            ++this.size;
        }
        ++this.modCount;
    }

    @Override
    public boolean addAll(Collection collection) {
        return this.addAll(this.size(), collection);
    }

    @Override
    public boolean addAll(int index, Collection collection) {
        if (index < 0 || index > this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        if (collection == null || collection.size() == 0) {
            return false;
        }
        this.ensureCapacity(this.size() + collection.size());
        int count = 0;
        try {
            for (Object obj : collection) {
                this.add(index + count, obj);
                ++count;
            }
        }
        catch (RuntimeException exception) {
            for (int i = 0; i < count; ++i) {
                this.remove(index);
            }
            throw exception;
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
        ++this.modCount;
    }

    void clearAndSet(Collection collection) {
        Content[] old = this.elementData;
        int oldSize = this.size;
        this.elementData = null;
        this.size = 0;
        if (collection != null && collection.size() != 0) {
            this.ensureCapacity(collection.size());
            try {
                this.addAll(0, collection);
            }
            catch (RuntimeException exception) {
                this.elementData = old;
                this.size = oldSize;
                throw exception;
            }
        }
        if (old != null) {
            for (int i = 0; i < oldSize; ++i) {
                ContentList.removeParent(old[i]);
            }
        }
        ++this.modCount;
    }

    void ensureCapacity(int minCapacity) {
        if (this.elementData == null) {
            this.elementData = new Content[Math.max(minCapacity, 5)];
        } else {
            int oldCapacity = this.elementData.length;
            if (minCapacity > oldCapacity) {
                Content[] oldData = this.elementData;
                int newCapacity = oldCapacity * 3 / 2 + 1;
                if (newCapacity < minCapacity) {
                    newCapacity = minCapacity;
                }
                this.elementData = new Content[newCapacity];
                System.arraycopy(oldData, 0, this.elementData, 0, this.size);
            }
        }
    }

    @Override
    public Object get(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        return this.elementData[index];
    }

    List getView(Filter filter) {
        return new FilterList(filter);
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
    public Object remove(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        Content old = this.elementData[index];
        ContentList.removeParent(old);
        int numMoved = this.size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(this.elementData, index + 1, this.elementData, index, numMoved);
        }
        this.elementData[--this.size] = null;
        ++this.modCount;
        return old;
    }

    private static void removeParent(Content c) {
        c.setParent(null);
    }

    @Override
    public Object set(int index, Object obj) {
        int docTypeIndex;
        int root;
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + " Size: " + this.size());
        }
        if (obj instanceof Element && this.parent instanceof Document && (root = this.indexOfFirstElement()) >= 0 && root != index) {
            throw new IllegalAddException("Cannot add a second root element, only one is allowed");
        }
        if (obj instanceof DocType && this.parent instanceof Document && (docTypeIndex = this.indexOfDocType()) >= 0 && docTypeIndex != index) {
            throw new IllegalAddException("Cannot add a second doctype, only one is allowed");
        }
        Object old = this.remove(index);
        try {
            this.add(index, obj);
        }
        catch (RuntimeException exception) {
            this.add(index, old);
            throw exception;
        }
        return old;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    private int getModCount() {
        return this.modCount;
    }

    class FilterListIterator
    implements ListIterator {
        Filter filter;
        private boolean forward = false;
        private boolean canremove = false;
        private boolean canset = false;
        private int cursor = -1;
        private int tmpcursor = -1;
        private int index = -1;
        private int expected = -1;
        private int fsize = 0;

        FilterListIterator(Filter filter, int start) {
            this.filter = filter;
            this.expected = ContentList.this.getModCount();
            this.forward = false;
            if (start < 0) {
                throw new IndexOutOfBoundsException("Index: " + start);
            }
            this.fsize = 0;
            for (int i = 0; i < ContentList.this.size(); ++i) {
                if (!filter.matches(ContentList.this.get(i))) continue;
                if (start == this.fsize) {
                    this.cursor = i;
                    this.index = this.fsize;
                }
                ++this.fsize;
            }
            if (start > this.fsize) {
                throw new IndexOutOfBoundsException("Index: " + start + " Size: " + this.fsize);
            }
            if (this.cursor == -1) {
                this.cursor = ContentList.this.size();
                this.index = this.fsize;
            }
        }

        @Override
        public boolean hasNext() {
            return this.nextIndex() < this.fsize;
        }

        @Override
        public Object next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException("next() is beyond the end of the Iterator");
            }
            this.index = this.nextIndex();
            this.cursor = this.tmpcursor;
            this.forward = true;
            this.canremove = true;
            this.canset = true;
            return ContentList.this.get(this.cursor);
        }

        @Override
        public boolean hasPrevious() {
            return this.previousIndex() >= 0;
        }

        public Object previous() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException("previous() is before the start of the Iterator");
            }
            this.index = this.previousIndex();
            this.cursor = this.tmpcursor;
            this.forward = false;
            this.canremove = true;
            this.canset = true;
            return ContentList.this.get(this.cursor);
        }

        @Override
        public int nextIndex() {
            this.checkConcurrentModification();
            if (this.forward) {
                for (int i = this.cursor + 1; i < ContentList.this.size(); ++i) {
                    if (!this.filter.matches(ContentList.this.get(i))) continue;
                    this.tmpcursor = i;
                    return this.index + 1;
                }
                this.tmpcursor = ContentList.this.size();
                return this.index + 1;
            }
            this.tmpcursor = this.cursor;
            return this.index;
        }

        @Override
        public int previousIndex() {
            this.checkConcurrentModification();
            if (!this.forward) {
                for (int i = this.cursor - 1; i >= 0; --i) {
                    if (!this.filter.matches(ContentList.this.get(i))) continue;
                    this.tmpcursor = i;
                    return this.index - 1;
                }
                this.tmpcursor = -1;
                return this.index - 1;
            }
            this.tmpcursor = this.cursor;
            return this.index;
        }

        public void add(Object obj) {
            if (!this.filter.matches(obj)) {
                throw new IllegalAddException("Filter won't allow the " + obj.getClass().getName() + " '" + obj + "' to be added to the list");
            }
            this.nextIndex();
            ContentList.this.add(this.tmpcursor, obj);
            this.expected = ContentList.this.getModCount();
            this.canset = false;
            this.canremove = false;
            if (this.forward) {
                ++this.index;
            } else {
                this.forward = true;
            }
            ++this.fsize;
            this.cursor = this.tmpcursor;
        }

        @Override
        public void remove() {
            if (!this.canremove) {
                throw new IllegalStateException("Can not remove an element unless either next() or previous() has been called since the last remove()");
            }
            boolean dir = this.forward;
            this.forward = true;
            try {
                this.nextIndex();
                ContentList.this.remove(this.cursor);
            }
            finally {
                this.forward = dir;
            }
            this.cursor = this.tmpcursor - 1;
            this.expected = ContentList.this.getModCount();
            this.forward = false;
            this.canremove = false;
            this.canset = false;
            --this.fsize;
        }

        public void set(Object obj) {
            if (!this.canset) {
                throw new IllegalStateException("Can not set an element unless either next() or previous() has been called since the last remove() or set()");
            }
            this.checkConcurrentModification();
            if (!this.filter.matches(obj)) {
                throw new IllegalAddException("Filter won't allow index " + this.index + " to be set to " + obj.getClass().getName());
            }
            ContentList.this.set(this.cursor, obj);
            this.expected = ContentList.this.getModCount();
        }

        private void checkConcurrentModification() {
            if (this.expected != ContentList.this.getModCount()) {
                throw new ConcurrentModificationException();
            }
        }
    }

    class FilterList
    extends AbstractList
    implements Serializable {
        Filter filter;
        int count = 0;
        int expected = -1;

        FilterList(Filter filter) {
            this.filter = filter;
        }

        @Override
        public void add(int index, Object obj) {
            if (this.filter.matches(obj)) {
                int adjusted = this.getAdjustedIndex(index);
                ContentList.this.add(adjusted, obj);
                ++this.expected;
                ++this.count;
            } else {
                throw new IllegalAddException("Filter won't allow the " + obj.getClass().getName() + " '" + obj + "' to be added to the list");
            }
        }

        @Override
        public Object get(int index) {
            int adjusted = this.getAdjustedIndex(index);
            return ContentList.this.get(adjusted);
        }

        @Override
        public Iterator iterator() {
            return new FilterListIterator(this.filter, 0);
        }

        @Override
        public ListIterator listIterator() {
            return new FilterListIterator(this.filter, 0);
        }

        @Override
        public ListIterator listIterator(int index) {
            return new FilterListIterator(this.filter, index);
        }

        @Override
        public Object remove(int index) {
            int adjusted = this.getAdjustedIndex(index);
            Object old = ContentList.this.get(adjusted);
            if (this.filter.matches(old)) {
                old = ContentList.this.remove(adjusted);
                ++this.expected;
                --this.count;
            } else {
                throw new IllegalAddException("Filter won't allow the " + old.getClass().getName() + " '" + old + "' (index " + index + ") to be removed");
            }
            return old;
        }

        @Override
        public Object set(int index, Object obj) {
            Object old = null;
            if (this.filter.matches(obj)) {
                int adjusted = this.getAdjustedIndex(index);
                old = ContentList.this.get(adjusted);
                if (!this.filter.matches(old)) {
                    throw new IllegalAddException("Filter won't allow the " + old.getClass().getName() + " '" + old + "' (index " + index + ") to be removed");
                }
                old = ContentList.this.set(adjusted, obj);
                this.expected += 2;
            } else {
                throw new IllegalAddException("Filter won't allow index " + index + " to be set to " + obj.getClass().getName());
            }
            return old;
        }

        @Override
        public int size() {
            if (this.expected == ContentList.this.getModCount()) {
                return this.count;
            }
            this.count = 0;
            for (int i = 0; i < ContentList.this.size(); ++i) {
                Content obj = ContentList.this.elementData[i];
                if (!this.filter.matches(obj)) continue;
                ++this.count;
            }
            this.expected = ContentList.this.getModCount();
            return this.count;
        }

        private final int getAdjustedIndex(int index) {
            int adjusted = 0;
            for (int i = 0; i < ContentList.this.size; ++i) {
                Content obj = ContentList.this.elementData[i];
                if (!this.filter.matches(obj)) continue;
                if (index == adjusted) {
                    return i;
                }
                ++adjusted;
            }
            if (index == adjusted) {
                return ContentList.this.size;
            }
            return ContentList.this.size + 1;
        }
    }
}

