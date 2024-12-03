/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.bitmap;

import com.hazelcast.query.impl.bitmap.AscendingLongIterator;
import com.hazelcast.query.impl.bitmap.SparseIntArray;

final class SparseArray<E> {
    private static final long INT_PREFIX_MASK = -4294967296L;
    private final SparseIntArray<SparseIntArray<E>> storages = new SparseIntArray();
    private int lastPrefix = -1;
    private SparseIntArray<E> lastStorage;

    SparseArray() {
    }

    public void set(long index, E value) {
        assert (index >= 0L);
        assert (value != null);
        int prefix = (int)(index >>> 32);
        if (prefix == this.lastPrefix) {
            this.lastStorage.set((int)index, value);
        } else {
            this.lastPrefix = prefix;
            SparseIntArray<E> storage = this.storages.get(prefix);
            if (storage == null) {
                SparseIntArray<E> createdStorage = new SparseIntArray<E>();
                createdStorage.set((int)index, value);
                this.lastStorage = createdStorage;
                this.storages.set(prefix, createdStorage);
            } else {
                storage.set((int)index, value);
                this.lastStorage = storage;
            }
        }
    }

    public void clear(long index) {
        assert (index >= 0L);
        int prefix = (int)(index >>> 32);
        if (prefix == this.lastPrefix) {
            if (this.lastStorage.clear((int)index)) {
                this.lastPrefix = -1;
                this.lastStorage = null;
                this.storages.clear(prefix);
            }
        } else {
            SparseIntArray<E> storage = this.storages.get(prefix);
            if (storage != null) {
                if (storage.clear((int)index)) {
                    this.storages.clear(prefix);
                } else {
                    this.lastPrefix = prefix;
                    this.lastStorage = storage;
                }
            }
        }
    }

    public void clear() {
        this.lastPrefix = -1;
        this.lastStorage = null;
        this.storages.clear();
    }

    public Iterator<E> iterator() {
        return new IteratorImpl(this.storages);
    }

    private static final class IteratorImpl<T>
    extends SparseIntArray.Iterator<T>
    implements Iterator<T> {
        private final SparseIntArray<SparseIntArray<T>> storages;
        private final SparseIntArray.Iterator<SparseIntArray<T>> storageIterator;
        private SparseIntArray<T> storage;
        private long index;

        IteratorImpl(SparseIntArray<SparseIntArray<T>> storages) {
            this.storages = storages;
            this.storageIterator = new SparseIntArray.Iterator();
            long prefix = storages.iterate(this.storageIterator);
            if (prefix != -1L) {
                this.storage = this.storageIterator.getValue();
                long postfix = this.storage.iterate(this);
                assert (postfix != -1L);
                this.index = prefix << 32 | postfix;
            } else {
                this.index = -1L;
            }
        }

        @Override
        public long getIndex() {
            return this.index;
        }

        @Override
        public long advance() {
            long current = this.index;
            if (current == -1L) {
                return -1L;
            }
            long postfix = this.storage.advance((int)current, this);
            if (postfix != -1L) {
                this.index = current & 0xFFFFFFFF00000000L | postfix;
                return current;
            }
            long prefix = this.storages.advance((int)(current >>> 32), this.storageIterator);
            if (prefix != -1L) {
                this.storage = this.storageIterator.getValue();
                postfix = this.storage.iterate(this);
                if (postfix != -1L) {
                    this.index = prefix << 32 | postfix;
                    return current;
                }
            }
            this.index = -1L;
            return current;
        }

        @Override
        public long advanceAtLeastTo(long member) {
            assert (member >= 0L);
            long current = this.index;
            if (current == -1L) {
                return -1L;
            }
            if (current >= member) {
                return current;
            }
            int memberPrefix = (int)(member >>> 32);
            int currentPrefix = (int)(current >>> 32);
            if (memberPrefix == currentPrefix) {
                long postfix = this.storage.advanceAtLeastTo((int)member, (int)current, this);
                if (postfix != -1L) {
                    this.index = current & 0xFFFFFFFF00000000L | postfix;
                    return this.index;
                }
                long prefix = this.storages.advance(currentPrefix, this.storageIterator);
                if (prefix != -1L) {
                    this.storage = this.storageIterator.getValue();
                    postfix = this.storage.iterate(this);
                    assert (postfix != -1L);
                    this.index = prefix << 32 | postfix;
                    return this.index;
                }
            } else {
                long prefix = this.storages.advanceAtLeastTo(memberPrefix, currentPrefix, this.storageIterator);
                if (prefix != -1L) {
                    this.storage = this.storageIterator.getValue();
                    if (prefix == (long)memberPrefix) {
                        long postfix = this.storage.iterateAtLeastFrom((int)member, this);
                        if (postfix != -1L) {
                            this.index = prefix << 32 | postfix;
                            return this.index;
                        }
                        if ((prefix = this.storages.advance((int)prefix, this.storageIterator)) != -1L) {
                            this.storage = this.storageIterator.getValue();
                            postfix = this.storage.iterate(this);
                            assert (postfix != -1L);
                            this.index = prefix << 32 | postfix;
                            return this.index;
                        }
                    } else {
                        long postfix = this.storage.iterate(this);
                        assert (postfix != -1L);
                        this.index = prefix << 32 | postfix;
                        return this.index;
                    }
                }
            }
            this.index = -1L;
            return this.index;
        }
    }

    public static interface Iterator<T>
    extends AscendingLongIterator {
        public T getValue();
    }
}

