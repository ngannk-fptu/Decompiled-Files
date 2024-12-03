/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.hpack;

import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http2.hpack.HPackEntry;
import org.apache.hc.core5.http2.hpack.HPackHeader;
import org.apache.hc.core5.http2.hpack.StaticTable;
import org.apache.hc.core5.util.Args;

final class FifoLinkedList {
    private final InternalNode master = new InternalNode(null);
    private int length;

    FifoLinkedList() {
        this.master.previous = this.master;
        this.master.next = this.master;
    }

    public Header get(int index) {
        Args.check(index <= this.length, "Length %s cannot be greater then index %s ", this.length, index);
        Args.notNegative(index, "index");
        InternalNode current = this.master.next;
        int n = 0;
        while (current != this.master) {
            if (index == n) {
                return current.header;
            }
            current = current.next;
            ++n;
        }
        return null;
    }

    public int getIndex(InternalNode node) {
        int seqNum = node.seqNum;
        if (seqNum < 1) {
            return -1;
        }
        return this.length - (seqNum - this.master.previous.seqNum) - 1;
    }

    public Header getFirst() {
        return this.master.next.header;
    }

    public Header getLast() {
        return this.master.previous.header;
    }

    public int size() {
        return this.length;
    }

    public InternalNode addFirst(HPackHeader header) {
        InternalNode newNode = new InternalNode(header);
        InternalNode oldNode = this.master.next;
        this.master.next = newNode;
        newNode.previous = this.master;
        newNode.next = oldNode;
        oldNode.previous = newNode;
        newNode.seqNum = oldNode.seqNum + 1;
        ++this.length;
        return newNode;
    }

    public InternalNode removeLast() {
        InternalNode last = this.master.previous;
        if (last.header != null) {
            InternalNode lastButOne = last.previous;
            this.master.previous = lastButOne;
            lastButOne.next = this.master;
            last.previous = null;
            last.next = null;
            last.seqNum = 0;
            --this.length;
            return last;
        }
        this.master.seqNum = 0;
        return null;
    }

    public void clear() {
        this.master.previous = this.master;
        this.master.next = this.master;
        this.master.seqNum = 0;
        this.length = 0;
    }

    class InternalNode
    implements HPackEntry {
        private final HPackHeader header;
        private InternalNode previous;
        private InternalNode next;
        private int seqNum;

        InternalNode(HPackHeader header) {
            this.header = header;
        }

        @Override
        public HPackHeader getHeader() {
            return this.header;
        }

        @Override
        public int getIndex() {
            return StaticTable.INSTANCE.length() + FifoLinkedList.this.getIndex(this) + 1;
        }

        public String toString() {
            return "[" + (this.header != null ? this.header.toString() : "master") + "; seqNum=" + this.seqNum + "; previous=" + (this.previous != null ? this.previous.header : null) + "; next=" + (this.next != null ? this.next.header : null) + ']';
        }
    }
}

