/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.list;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.collections.list.AbstractLinkedList;

public class CursorableLinkedList
extends AbstractLinkedList
implements Serializable {
    private static final long serialVersionUID = 8836393098519411393L;
    protected transient List cursors = new ArrayList();

    public CursorableLinkedList() {
        this.init();
    }

    public CursorableLinkedList(Collection coll) {
        super(coll);
    }

    protected void init() {
        super.init();
        this.cursors = new ArrayList();
    }

    public Iterator iterator() {
        return super.listIterator(0);
    }

    public ListIterator listIterator() {
        return this.cursor(0);
    }

    public ListIterator listIterator(int fromIndex) {
        return this.cursor(fromIndex);
    }

    public Cursor cursor() {
        return this.cursor(0);
    }

    public Cursor cursor(int fromIndex) {
        Cursor cursor = new Cursor(this, fromIndex);
        this.registerCursor(cursor);
        return cursor;
    }

    protected void updateNode(AbstractLinkedList.Node node, Object value) {
        super.updateNode(node, value);
        this.broadcastNodeChanged(node);
    }

    protected void addNode(AbstractLinkedList.Node nodeToInsert, AbstractLinkedList.Node insertBeforeNode) {
        super.addNode(nodeToInsert, insertBeforeNode);
        this.broadcastNodeInserted(nodeToInsert);
    }

    protected void removeNode(AbstractLinkedList.Node node) {
        super.removeNode(node);
        this.broadcastNodeRemoved(node);
    }

    protected void removeAllNodes() {
        if (this.size() > 0) {
            Iterator it = this.iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
        }
    }

    protected void registerCursor(Cursor cursor) {
        Iterator it = this.cursors.iterator();
        while (it.hasNext()) {
            WeakReference ref = (WeakReference)it.next();
            if (ref.get() != null) continue;
            it.remove();
        }
        this.cursors.add(new WeakReference<Cursor>(cursor));
    }

    protected void unregisterCursor(Cursor cursor) {
        Iterator it = this.cursors.iterator();
        while (it.hasNext()) {
            WeakReference ref = (WeakReference)it.next();
            Cursor cur = (Cursor)ref.get();
            if (cur == null) {
                it.remove();
                continue;
            }
            if (cur != cursor) continue;
            ref.clear();
            it.remove();
            break;
        }
    }

    protected void broadcastNodeChanged(AbstractLinkedList.Node node) {
        Iterator it = this.cursors.iterator();
        while (it.hasNext()) {
            WeakReference ref = (WeakReference)it.next();
            Cursor cursor = (Cursor)ref.get();
            if (cursor == null) {
                it.remove();
                continue;
            }
            cursor.nodeChanged(node);
        }
    }

    protected void broadcastNodeRemoved(AbstractLinkedList.Node node) {
        Iterator it = this.cursors.iterator();
        while (it.hasNext()) {
            WeakReference ref = (WeakReference)it.next();
            Cursor cursor = (Cursor)ref.get();
            if (cursor == null) {
                it.remove();
                continue;
            }
            cursor.nodeRemoved(node);
        }
    }

    protected void broadcastNodeInserted(AbstractLinkedList.Node node) {
        Iterator it = this.cursors.iterator();
        while (it.hasNext()) {
            WeakReference ref = (WeakReference)it.next();
            Cursor cursor = (Cursor)ref.get();
            if (cursor == null) {
                it.remove();
                continue;
            }
            cursor.nodeInserted(node);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        this.doWriteObject(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.doReadObject(in);
    }

    protected ListIterator createSubListListIterator(AbstractLinkedList.LinkedSubList subList, int fromIndex) {
        SubCursor cursor = new SubCursor(subList, fromIndex);
        this.registerCursor(cursor);
        return cursor;
    }

    protected static class SubCursor
    extends Cursor {
        protected final AbstractLinkedList.LinkedSubList sub;

        protected SubCursor(AbstractLinkedList.LinkedSubList sub, int index) {
            super((CursorableLinkedList)sub.parent, index + sub.offset);
            this.sub = sub;
        }

        public boolean hasNext() {
            return this.nextIndex() < this.sub.size;
        }

        public boolean hasPrevious() {
            return this.previousIndex() >= 0;
        }

        public int nextIndex() {
            return super.nextIndex() - this.sub.offset;
        }

        public void add(Object obj) {
            super.add(obj);
            this.sub.expectedModCount = this.parent.modCount;
            ++this.sub.size;
        }

        public void remove() {
            super.remove();
            this.sub.expectedModCount = this.parent.modCount;
            --this.sub.size;
        }
    }

    public static class Cursor
    extends AbstractLinkedList.LinkedListIterator {
        boolean valid = true;
        boolean nextIndexValid = true;
        boolean currentRemovedByAnother = false;

        protected Cursor(CursorableLinkedList parent, int index) {
            super(parent, index);
        }

        public void remove() {
            if (this.current != null || !this.currentRemovedByAnother) {
                this.checkModCount();
                this.parent.removeNode(this.getLastNodeReturned());
            }
            this.currentRemovedByAnother = false;
        }

        public void add(Object obj) {
            super.add(obj);
            this.next = this.next.next;
        }

        public int nextIndex() {
            if (!this.nextIndexValid) {
                if (this.next == this.parent.header) {
                    this.nextIndex = this.parent.size();
                } else {
                    int pos = 0;
                    AbstractLinkedList.Node temp = this.parent.header.next;
                    while (temp != this.next) {
                        ++pos;
                        temp = temp.next;
                    }
                    this.nextIndex = pos;
                }
                this.nextIndexValid = true;
            }
            return this.nextIndex;
        }

        protected void nodeChanged(AbstractLinkedList.Node node) {
        }

        protected void nodeRemoved(AbstractLinkedList.Node node) {
            if (node == this.next && node == this.current) {
                this.next = node.next;
                this.current = null;
                this.currentRemovedByAnother = true;
            } else if (node == this.next) {
                this.next = node.next;
                this.currentRemovedByAnother = false;
            } else if (node == this.current) {
                this.current = null;
                this.currentRemovedByAnother = true;
                --this.nextIndex;
            } else {
                this.nextIndexValid = false;
                this.currentRemovedByAnother = false;
            }
        }

        protected void nodeInserted(AbstractLinkedList.Node node) {
            if (node.previous == this.current) {
                this.next = node;
            } else if (this.next.previous == node) {
                this.next = node;
            } else {
                this.nextIndexValid = false;
            }
        }

        protected void checkModCount() {
            if (!this.valid) {
                throw new ConcurrentModificationException("Cursor closed");
            }
        }

        public void close() {
            if (this.valid) {
                ((CursorableLinkedList)this.parent).unregisterCursor(this);
                this.valid = false;
            }
        }
    }
}

