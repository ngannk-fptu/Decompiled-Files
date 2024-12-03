/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.query.impl.bitmap;

import com.hazelcast.query.impl.Numbers;
import com.hazelcast.query.impl.bitmap.AscendingLongIterator;
import com.hazelcast.query.impl.bitmap.SparseArray;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;

final class BitmapAlgorithms {
    private BitmapAlgorithms() {
    }

    public static AscendingLongIterator and(AscendingLongIterator[] iterators) {
        return new AndIterator(iterators);
    }

    public static AscendingLongIterator or(AscendingLongIterator[] iterators) {
        return new OrIterator(iterators);
    }

    public static AscendingLongIterator not(AscendingLongIterator iterator, SparseArray<?> universe) {
        return new NotIterator(iterator, universe);
    }

    private static final class NotIterator
    implements AscendingLongIterator {
        private final AscendingLongIterator iterator;
        private final AscendingLongIterator universe;
        private long index;
        private long gapEnd;

        NotIterator(AscendingLongIterator iterator, SparseArray<?> universe) {
            this.iterator = iterator;
            this.universe = universe.iterator();
            this.gapEnd = iterator.advance();
            this.advance();
        }

        @Override
        public long getIndex() {
            return this.index;
        }

        @Override
        public long advance() {
            long current = this.index;
            long newIndex = this.universe.advance();
            if (newIndex != this.gapEnd) {
                this.index = newIndex;
                return current;
            }
            long newGapEnd = this.gapEnd;
            while (newIndex == newGapEnd && newIndex != -1L) {
                long newGapStart;
                do {
                    newGapStart = newGapEnd + 1L;
                } while ((newGapEnd = this.iterator.advance()) == newGapStart);
                if (newGapStart == Long.MIN_VALUE) {
                    assert (newGapEnd == -1L);
                    this.universe.advanceAtLeastTo(Long.MAX_VALUE);
                    this.universe.advance();
                    break;
                }
                newIndex = this.universe.advanceAtLeastTo(newGapStart);
            }
            this.index = this.universe.advance();
            this.gapEnd = newGapEnd;
            return current;
        }

        @Override
        public long advanceAtLeastTo(long member) {
            if (this.index >= member) {
                return this.index;
            }
            if (member > this.gapEnd) {
                this.gapEnd = this.iterator.advanceAtLeastTo(member);
                this.iterator.advance();
            }
            this.universe.advanceAtLeastTo(member);
            this.advance();
            return this.index;
        }
    }

    private static final class OrIterator
    implements AscendingLongIterator {
        private final AscendingLongIterator[] iterators;
        private int size;

        OrIterator(AscendingLongIterator[] iterators) {
            this.iterators = iterators;
            this.size = iterators.length;
            this.heapify();
            this.removeEmptyIterators();
        }

        @Override
        public long getIndex() {
            return this.size == 0 ? -1L : this.iterators[0].getIndex();
        }

        @Override
        public long advance() {
            if (this.size == 0) {
                return -1L;
            }
            long current = this.iterators[0].getIndex();
            this.update();
            while (this.size > 0 && this.iterators[0].getIndex() == current) {
                this.update();
            }
            return current;
        }

        @Override
        public long advanceAtLeastTo(long member) {
            long index;
            if (this.size == 0) {
                return -1L;
            }
            while ((index = this.iterators[0].getIndex()) < member) {
                this.update();
                if (this.size != 0) continue;
                return -1L;
            }
            return index;
        }

        private void removeEmptyIterators() {
            while (this.size > 0 && this.iterators[0].getIndex() == -1L) {
                --this.size;
                if (this.size == 0) continue;
                this.siftDown(0, this.iterators[this.size]);
            }
        }

        private void update() {
            assert (this.size > 0);
            AscendingLongIterator iterator = this.iterators[0];
            long index = iterator.advance();
            long newIndex = iterator.getIndex();
            if (newIndex == -1L) {
                --this.size;
                if (this.size != 0) {
                    this.siftDown(0, this.iterators[this.size]);
                }
            } else {
                assert (newIndex > index);
                this.siftDown(0, iterator);
            }
        }

        private void heapify() {
            for (int i = (this.size >>> 1) - 1; i >= 0; --i) {
                this.siftDown(i, this.iterators[i]);
            }
        }

        private void siftDown(int index, AscendingLongIterator iterator) {
            int firstLeafIndex = this.size >>> 1;
            while (index < firstLeafIndex) {
                int childIndex = (index << 1) + 1;
                AscendingLongIterator child = this.iterators[childIndex];
                int rightChildIndex = childIndex + 1;
                if (rightChildIndex < this.size && child.getIndex() > this.iterators[rightChildIndex].getIndex()) {
                    childIndex = rightChildIndex;
                    child = this.iterators[childIndex];
                }
                if (iterator.getIndex() <= child.getIndex()) break;
                this.iterators[index] = child;
                index = childIndex;
            }
            this.iterators[index] = iterator;
        }
    }

    private static final class AndIterator
    implements AscendingLongIterator {
        private final Node[] nodes;
        private Node first;
        private Node last;
        private long index;

        AndIterator(AscendingLongIterator[] iterators) {
            Node[] nodes = new Node[iterators.length];
            for (int i = 0; i < nodes.length; ++i) {
                nodes[i] = new Node(iterators[i]);
            }
            this.nodes = nodes;
            this.orderAndLink();
            this.advance();
        }

        @Override
        public long getIndex() {
            return this.index;
        }

        @Override
        public long advance() {
            long current = this.index;
            long min = this.first.iterator.getIndex();
            long max = this.last.iterator.getIndex();
            while (min != max && min != -1L && max != -1L) {
                max = this.first.iterator.advanceAtLeastTo(max);
                Node second = this.first.next;
                this.first.next = null;
                this.last.next = this.first;
                this.last = this.first;
                this.first = second;
                min = this.first.iterator.getIndex();
            }
            if (max == -1L) {
                this.index = -1L;
                return current;
            }
            if (min != -1L) {
                this.last.iterator.advance();
            }
            this.index = min;
            return current;
        }

        @Override
        public long advanceAtLeastTo(long member) {
            if (this.index >= member) {
                return this.index;
            }
            this.last.iterator.advanceAtLeastTo(member);
            this.advance();
            return this.index;
        }

        private void orderAndLink() {
            Arrays.sort(this.nodes);
            this.first = this.nodes[0];
            this.last = this.nodes[this.nodes.length - 1];
            for (int i = 0; i < this.nodes.length - 1; ++i) {
                this.nodes[i].next = this.nodes[i + 1];
            }
            this.last.next = null;
        }

        @SuppressFBWarnings(value={"EQ_COMPARETO_USE_OBJECT_EQUALS"})
        private static final class Node
        implements Comparable<Node> {
            final AscendingLongIterator iterator;
            Node next;

            Node(AscendingLongIterator iterator) {
                this.iterator = iterator;
            }

            @Override
            public int compareTo(Node that) {
                return Numbers.compareLongs(this.iterator.getIndex(), that.iterator.getIndex());
            }

            public String toString() {
                return Long.toString(this.iterator.getIndex());
            }
        }
    }
}

