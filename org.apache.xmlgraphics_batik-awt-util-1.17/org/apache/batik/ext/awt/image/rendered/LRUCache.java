/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.DoublyLinkedList
 *  org.apache.batik.util.DoublyLinkedList$Node
 */
package org.apache.batik.ext.awt.image.rendered;

import org.apache.batik.util.DoublyLinkedList;

public class LRUCache {
    private DoublyLinkedList free = null;
    private DoublyLinkedList used = null;
    private int maxSize = 0;

    public LRUCache(int size) {
        if (size <= 0) {
            size = 1;
        }
        this.maxSize = size;
        this.free = new DoublyLinkedList();
        this.used = new DoublyLinkedList();
        while (size > 0) {
            this.free.add((DoublyLinkedList.Node)new LRUNode());
            --size;
        }
    }

    public int getUsed() {
        return this.used.getSize();
    }

    public synchronized void setSize(int newSz) {
        if (this.maxSize < newSz) {
            for (int i = this.maxSize; i < newSz; ++i) {
                this.free.add((DoublyLinkedList.Node)new LRUNode());
            }
        } else if (this.maxSize > newSz) {
            for (int i = this.used.getSize(); i > newSz; --i) {
                LRUNode nde = (LRUNode)this.used.getTail();
                this.used.remove((DoublyLinkedList.Node)nde);
                nde.setObj(null);
            }
        }
        this.maxSize = newSz;
    }

    public synchronized void flush() {
        while (this.used.getSize() > 0) {
            LRUNode nde = (LRUNode)this.used.pop();
            nde.setObj(null);
            this.free.add((DoublyLinkedList.Node)nde);
        }
    }

    public synchronized void remove(LRUObj obj) {
        LRUNode nde = obj.lruGet();
        if (nde == null) {
            return;
        }
        this.used.remove((DoublyLinkedList.Node)nde);
        nde.setObj(null);
        this.free.add((DoublyLinkedList.Node)nde);
    }

    public synchronized void touch(LRUObj obj) {
        LRUNode nde = obj.lruGet();
        if (nde == null) {
            return;
        }
        this.used.touch((DoublyLinkedList.Node)nde);
    }

    public synchronized void add(LRUObj obj) {
        LRUNode nde = obj.lruGet();
        if (nde != null) {
            this.used.touch((DoublyLinkedList.Node)nde);
            return;
        }
        if (this.free.getSize() > 0) {
            nde = (LRUNode)this.free.pop();
            nde.setObj(obj);
            this.used.add((DoublyLinkedList.Node)nde);
        } else {
            nde = (LRUNode)this.used.getTail();
            nde.setObj(obj);
            this.used.touch((DoublyLinkedList.Node)nde);
        }
    }

    protected synchronized void print() {
        System.out.println("In Use: " + this.used.getSize() + " Free: " + this.free.getSize());
        LRUNode nde = (LRUNode)this.used.getHead();
        if (nde == null) {
            return;
        }
        do {
            System.out.println(nde.getObj());
        } while ((nde = (LRUNode)nde.getNext()) != this.used.getHead());
    }

    public static class LRUNode
    extends DoublyLinkedList.Node {
        private LRUObj obj = null;

        public LRUObj getObj() {
            return this.obj;
        }

        protected void setObj(LRUObj newObj) {
            if (this.obj != null) {
                this.obj.lruRemove();
            }
            this.obj = newObj;
            if (this.obj != null) {
                this.obj.lruSet(this);
            }
        }
    }

    public static interface LRUObj {
        public void lruSet(LRUNode var1);

        public LRUNode lruGet();

        public void lruRemove();
    }
}

