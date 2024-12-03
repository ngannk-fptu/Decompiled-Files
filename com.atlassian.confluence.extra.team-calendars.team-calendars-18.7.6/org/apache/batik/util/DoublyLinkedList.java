/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.util;

public class DoublyLinkedList {
    private Node head = null;
    private int size = 0;

    public synchronized int getSize() {
        return this.size;
    }

    public synchronized void empty() {
        while (this.size > 0) {
            this.pop();
        }
    }

    public Node getHead() {
        return this.head;
    }

    public Node getTail() {
        return this.head.getPrev();
    }

    public void touch(Node nde) {
        if (nde == null) {
            return;
        }
        nde.insertBefore(this.head);
        this.head = nde;
    }

    public void add(int index, Node nde) {
        if (nde == null) {
            return;
        }
        if (index == 0) {
            nde.insertBefore(this.head);
            this.head = nde;
        } else if (index == this.size) {
            nde.insertBefore(this.head);
        } else {
            Node after = this.head;
            while (index != 0) {
                after = after.getNext();
                --index;
            }
            nde.insertBefore(after);
        }
        ++this.size;
    }

    public void add(Node nde) {
        if (nde == null) {
            return;
        }
        nde.insertBefore(this.head);
        this.head = nde;
        ++this.size;
    }

    public void remove(Node nde) {
        if (nde == null) {
            return;
        }
        if (nde == this.head) {
            this.head = this.head.getNext() == this.head ? null : this.head.getNext();
        }
        nde.unlink();
        --this.size;
    }

    public Node pop() {
        if (this.head == null) {
            return null;
        }
        Node nde = this.head;
        this.remove(nde);
        return nde;
    }

    public Node unpush() {
        if (this.head == null) {
            return null;
        }
        Node nde = this.getTail();
        this.remove(nde);
        return nde;
    }

    public void push(Node nde) {
        nde.insertBefore(this.head);
        if (this.head == null) {
            this.head = nde;
        }
        ++this.size;
    }

    public void unpop(Node nde) {
        nde.insertBefore(this.head);
        this.head = nde;
        ++this.size;
    }

    public static class Node {
        private Node next = null;
        private Node prev = null;

        public final Node getNext() {
            return this.next;
        }

        public final Node getPrev() {
            return this.prev;
        }

        protected final void setNext(Node newNext) {
            this.next = newNext;
        }

        protected final void setPrev(Node newPrev) {
            this.prev = newPrev;
        }

        protected final void unlink() {
            if (this.getNext() != null) {
                this.getNext().setPrev(this.getPrev());
            }
            if (this.getPrev() != null) {
                this.getPrev().setNext(this.getNext());
            }
            this.setNext(null);
            this.setPrev(null);
        }

        protected final void insertBefore(Node nde) {
            if (this == nde) {
                return;
            }
            if (this.getPrev() != null) {
                this.unlink();
            }
            if (nde == null) {
                this.setNext(this);
                this.setPrev(this);
            } else {
                this.setNext(nde);
                this.setPrev(nde.getPrev());
                nde.setPrev(this);
                if (this.getPrev() != null) {
                    this.getPrev().setNext(this);
                }
            }
        }
    }
}

