/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.bitmap;

import com.hazelcast.query.impl.bitmap.BitmapUtils;
import java.util.Arrays;

class SparseIntArray<E> {
    public static final int ARRAY_STORAGE_32_MAX_SPARSE_SIZE = 513;
    private static final int ARRAY_STORAGE_32_MAX_DENSE_SIZE = 262145;
    private static final int STORAGE_16_MAX_DENSE_SIZE = 65536;
    private static final int STORAGE_16_MAX_SPARSE_SIZE = 65536;
    private static final long SHORT_PREFIX_MASK_LONG = 0xFFFF0000L;
    private static final int SHORT_PREFIX_MASK_INT = -65536;
    private Storage32 storage = new ArrayStorage32();

    SparseIntArray() {
    }

    public E get(int index) {
        return (E)this.storage.get(index);
    }

    public void set(int index, E value) {
        assert (value != null);
        Storage32 newStorage = this.storage.set(index, value);
        if (newStorage != this.storage) {
            this.storage = newStorage;
        }
    }

    public boolean clear(int index) {
        Storage32 newStorage = this.storage.clear(index);
        if (newStorage == null) {
            return true;
        }
        if (newStorage != this.storage) {
            this.storage = newStorage;
        }
        return false;
    }

    public void clear() {
        this.storage = new ArrayStorage32();
    }

    public long iterate(Iterator<E> iterator) {
        return this.storage.iterate(iterator);
    }

    public long advance(int current, Iterator<E> iterator) {
        return this.storage.advance(current, iterator);
    }

    public long iterateAtLeastFrom(int index, Iterator<E> iterator) {
        return this.storage.iterateAtLeastFrom(index, iterator);
    }

    public long advanceAtLeastTo(int index, int current, Iterator<E> iterator) {
        return this.storage.advanceAtLeastTo(index, current, iterator);
    }

    private static final class Storage16 {
        public static final int END = -1;
        private static final int MIN_CAPACITY = 2;
        private int size;
        private short[] indexes;
        private Object[] values = new Object[2];

        Storage16(short index, Object value) {
            this.set(index, value);
        }

        public void set(short index, Object value) {
            if (this.indexes == null) {
                this.setDense(index, value);
            } else {
                this.setSparse(index, value);
            }
        }

        private void setDense(short index, Object value) {
            int unsignedIndex = BitmapUtils.toUnsignedInt(index);
            if (unsignedIndex < this.values.length) {
                if (this.values[unsignedIndex] == null) {
                    ++this.size;
                }
                this.values[unsignedIndex] = value;
                return;
            }
            int delta = BitmapUtils.denseCapacityDeltaShort(this.size, this.values.length);
            int newCapacity = Math.min(65536, this.size + delta);
            if (unsignedIndex < newCapacity) {
                this.values = Arrays.copyOf(this.values, newCapacity);
                this.values[unsignedIndex] = value;
                ++this.size;
                return;
            }
            Object[] oldValues = this.values;
            if (this.size == this.values.length) {
                this.indexes = new short[newCapacity];
                this.values = new Object[newCapacity];
            } else {
                this.indexes = new short[this.values.length];
            }
            int count = 0;
            for (int i = 0; i < this.values.length; ++i) {
                Object storedValue = oldValues[i];
                if (storedValue == null) continue;
                this.indexes[count] = (short)i;
                this.values[count] = storedValue;
                if (++count == this.size) break;
            }
            this.indexes[this.size] = index;
            this.values[this.size] = value;
            ++this.size;
        }

        private void setSparse(short index, Object value) {
            int unsignedIndex = BitmapUtils.toUnsignedInt(index);
            int position = BitmapUtils.unsignedBinarySearch(this.indexes, this.size, unsignedIndex);
            if (position >= 0) {
                this.values[position] = value;
                return;
            }
            position = -(position + 1);
            if (this.size == this.indexes.length) {
                int delta = BitmapUtils.capacityDeltaShort(this.indexes.length);
                int lastIndex = BitmapUtils.toUnsignedInt(this.indexes[this.indexes.length - 1]);
                int denseCapacity = Math.max(unsignedIndex, lastIndex) + 1;
                if (denseCapacity <= this.size + delta) {
                    Object[] newValues = new Object[denseCapacity];
                    for (int i = 0; i < this.indexes.length; ++i) {
                        newValues[BitmapUtils.toUnsignedInt((short)this.indexes[i])] = this.values[i];
                    }
                    newValues[unsignedIndex] = value;
                    this.indexes = null;
                    this.values = newValues;
                    ++this.size;
                    return;
                }
                int newCapacity = Math.min(65536, this.size + BitmapUtils.capacityDeltaShort(this.indexes.length));
                short[] newIndexes = new short[newCapacity];
                System.arraycopy(this.indexes, 0, newIndexes, 0, position);
                System.arraycopy(this.indexes, position, newIndexes, position + 1, this.size - position);
                this.indexes = newIndexes;
                Object[] newValues = new Object[newCapacity];
                System.arraycopy(this.values, 0, newValues, 0, position);
                System.arraycopy(this.values, position, newValues, position + 1, this.size - position);
                this.values = newValues;
            } else {
                System.arraycopy(this.indexes, position, this.indexes, position + 1, this.size - position);
                System.arraycopy(this.values, position, this.values, position + 1, this.size - position);
            }
            this.indexes[position] = index;
            this.values[position] = value;
            ++this.size;
        }

        public boolean clear(short index) {
            if (this.indexes == null) {
                return this.clearDense(index);
            }
            return this.clearSparse(index);
        }

        private boolean clearDense(short index) {
            int unsignedIndex = BitmapUtils.toUnsignedInt(index);
            if (unsignedIndex >= this.values.length) {
                return false;
            }
            if (this.values[unsignedIndex] == null) {
                return false;
            }
            --this.size;
            if (this.size == 0) {
                return true;
            }
            this.values[unsignedIndex] = null;
            int wasted = this.values.length - this.size;
            int delta = BitmapUtils.capacityDeltaShort(this.values.length);
            if (wasted < delta) {
                return false;
            }
            int newCapacity = this.values.length - delta;
            if (newCapacity < 2) {
                return false;
            }
            assert (wasted == delta);
            assert (newCapacity == this.size);
            Object[] newValues = new Object[newCapacity];
            int left = this.size;
            for (int i = this.values.length - 1; i >= 0; --i) {
                Object value = this.values[i];
                if (value == null) continue;
                if (i >= newCapacity && this.indexes == null) {
                    this.indexes = new short[newCapacity];
                }
                --left;
                if (this.indexes != null) {
                    this.indexes[left] = (short)i;
                }
                newValues[left] = value;
                if (left == 0) break;
            }
            this.values = newValues;
            return false;
        }

        private boolean clearSparse(short index) {
            int unsignedIndex = BitmapUtils.toUnsignedInt(index);
            int position = BitmapUtils.unsignedBinarySearch(this.indexes, this.size, unsignedIndex);
            if (position < 0) {
                return false;
            }
            --this.size;
            if (this.size == 0) {
                return true;
            }
            int delta = BitmapUtils.capacityDeltaShort(this.indexes.length);
            int wasted = this.indexes.length - this.size;
            int newCapacity = this.indexes.length - delta;
            if (wasted >= delta && newCapacity >= 2) {
                short[] newIndexes = new short[newCapacity];
                System.arraycopy(this.indexes, 0, newIndexes, 0, position);
                System.arraycopy(this.indexes, position + 1, newIndexes, position, this.size - position);
                this.indexes = newIndexes;
                Object[] newValues = new Object[newCapacity];
                System.arraycopy(this.values, 0, newValues, 0, position);
                System.arraycopy(this.values, position + 1, newValues, position, this.size - position);
                this.values = newValues;
            } else {
                System.arraycopy(this.indexes, position + 1, this.indexes, position, this.size - position);
                System.arraycopy(this.values, position + 1, this.values, position, this.size - position);
                this.values[this.size] = null;
            }
            return false;
        }

        public Object get(short index) {
            int unsignedIndex = BitmapUtils.toUnsignedInt(index);
            if (this.indexes == null) {
                return unsignedIndex < this.values.length ? this.values[unsignedIndex] : null;
            }
            int position = BitmapUtils.unsignedBinarySearch(this.indexes, this.size, unsignedIndex);
            return position >= 0 ? this.values[position] : null;
        }

        public int iterate(Iterator iterator) {
            assert (this.size > 0);
            iterator.position16 = 0;
            int index = this.advance(iterator);
            assert (index != -1);
            return index;
        }

        public int advance(Iterator iterator) {
            int position;
            assert (this.size > 0);
            if (this.indexes == null) {
                for (position = iterator.position16; position < this.values.length; ++position) {
                    Object value = this.values[position];
                    if (value == null) continue;
                    iterator.value = value;
                    iterator.position16 = position + 1;
                    return position;
                }
                return -1;
            }
            if (position < this.size) {
                iterator.value = this.values[position];
                iterator.position16 = position + 1;
                return BitmapUtils.toUnsignedInt(this.indexes[position]);
            }
            return -1;
        }

        public int iterateAtLeastFrom(short index, Iterator iterator) {
            assert (this.size > 0);
            int unsignedIndex = BitmapUtils.toUnsignedInt(index);
            if (this.indexes == null) {
                for (int position = unsignedIndex; position < this.values.length; ++position) {
                    Object value = this.values[position];
                    if (value == null) continue;
                    iterator.value = value;
                    iterator.position16 = position + 1;
                    return position;
                }
                return -1;
            }
            int position = BitmapUtils.unsignedBinarySearch(this.indexes, this.size, unsignedIndex);
            if (position < 0) {
                if ((position = -(position + 1)) == this.size) {
                    return -1;
                }
                unsignedIndex = BitmapUtils.toUnsignedInt(this.indexes[position]);
            }
            iterator.value = this.values[position];
            iterator.position16 = position + 1;
            return unsignedIndex;
        }

        public int advanceAtLeastTo(short index, short current, Iterator iterator) {
            assert (this.size > 0);
            int unsignedIndex = BitmapUtils.toUnsignedInt(index);
            assert (BitmapUtils.toUnsignedInt(current) < unsignedIndex);
            if (this.indexes == null) {
                for (int position = unsignedIndex; position < this.values.length; ++position) {
                    Object value = this.values[position];
                    if (value == null) continue;
                    iterator.value = value;
                    iterator.position16 = position + 1;
                    return position;
                }
                return -1;
            }
            int position = iterator.position16;
            if (position == this.size) {
                return -1;
            }
            if ((position = BitmapUtils.unsignedBinarySearch(this.indexes, position, this.size, unsignedIndex)) < 0) {
                if ((position = -(position + 1)) == this.size) {
                    return -1;
                }
                unsignedIndex = BitmapUtils.toUnsignedInt(this.indexes[position]);
            }
            iterator.value = this.values[position];
            iterator.position16 = position + 1;
            return unsignedIndex;
        }
    }

    private static final class PrefixStorage32
    implements Storage32 {
        private static final int MIN_CAPACITY = 2;
        private static final int MAX_CAPACITY = 65536;
        private int size;
        private short[] prefixes;
        private Storage16[] storages;
        private int lastPrefix = -1;
        private Storage16 lastStorage;

        PrefixStorage32(Object[] values, int size, int index, Object value) {
            assert (size <= values.length);
            assert (index >= values.length);
            this.prefixes = new short[2];
            this.storages = new Storage16[2];
            int count = 0;
            for (int i = 0; i < values.length; ++i) {
                Object storedValue = values[i];
                if (storedValue == null) continue;
                this.append(i, storedValue);
                if (++count == size) break;
            }
            this.append(index, value);
        }

        PrefixStorage32(Object[] values, int size) {
            assert (size <= values.length);
            this.prefixes = new short[2];
            this.storages = new Storage16[2];
            int count = 0;
            for (int i = 0; i < values.length; ++i) {
                Object storedValue = values[i];
                if (storedValue == null) continue;
                this.append(i, storedValue);
                if (++count == size) break;
            }
        }

        PrefixStorage32(int[] indexes, Object[] values, int position, int index, Object value) {
            int i;
            this.prefixes = new short[2];
            this.storages = new Storage16[2];
            for (i = 0; i < position; ++i) {
                this.append(indexes[i], values[i]);
            }
            this.append(index, value);
            for (i = position; i < indexes.length; ++i) {
                this.append(indexes[i], values[i]);
            }
        }

        @Override
        public Storage32 set(int index, Object value) {
            int position;
            short prefix = (short)(index >>> 16);
            int unsignedPrefix = BitmapUtils.toUnsignedInt(prefix);
            if (unsignedPrefix == this.lastPrefix) {
                this.lastStorage.set((short)index, value);
                return this;
            }
            int n = position = this.size == 0 ? -1 : BitmapUtils.unsignedBinarySearch(this.prefixes, this.size, unsignedPrefix);
            if (position >= 0) {
                Storage16 storage = this.storages[position];
                storage.set((short)index, value);
                this.lastPrefix = unsignedPrefix;
                this.lastStorage = storage;
                return this;
            }
            position = -(position + 1);
            if (this.size == this.prefixes.length) {
                int newCapacity = Math.min(65536, this.size + BitmapUtils.capacityDeltaShort(this.prefixes.length));
                short[] newPrefixes = new short[newCapacity];
                System.arraycopy(this.prefixes, 0, newPrefixes, 0, position);
                System.arraycopy(this.prefixes, position, newPrefixes, position + 1, this.size - position);
                this.prefixes = newPrefixes;
                Storage16[] newStorages = new Storage16[newCapacity];
                System.arraycopy(this.storages, 0, newStorages, 0, position);
                System.arraycopy(this.storages, position, newStorages, position + 1, this.size - position);
                this.storages = newStorages;
            } else {
                System.arraycopy(this.prefixes, position, this.prefixes, position + 1, this.size - position);
                System.arraycopy(this.storages, position, this.storages, position + 1, this.size - position);
            }
            Storage16 createdStorage = new Storage16((short)index, value);
            this.prefixes[position] = prefix;
            this.storages[position] = createdStorage;
            this.lastPrefix = unsignedPrefix;
            this.lastStorage = createdStorage;
            ++this.size;
            return this;
        }

        @Override
        public Storage32 clear(int index) {
            int position;
            Storage16 storage;
            short prefix = (short)(index >>> 16);
            int unsignedPrefix = BitmapUtils.toUnsignedInt(prefix);
            if (unsignedPrefix == this.lastPrefix) {
                storage = this.lastStorage;
                if (!storage.clear((short)index)) {
                    return this;
                }
                position = BitmapUtils.unsignedBinarySearch(this.prefixes, this.size, unsignedPrefix);
                assert (position >= 0);
            } else {
                if (this.size == 0) {
                    return this;
                }
                position = BitmapUtils.unsignedBinarySearch(this.prefixes, this.size, unsignedPrefix);
                if (position < 0) {
                    return this;
                }
                storage = this.storages[position];
                if (!storage.clear((short)index)) {
                    this.lastStorage = storage;
                    this.lastPrefix = unsignedPrefix;
                    return this;
                }
            }
            --this.size;
            this.lastStorage = null;
            this.lastPrefix = -1;
            if (this.size == 0) {
                this.storages[position] = null;
                return null;
            }
            int delta = BitmapUtils.capacityDeltaShort(this.prefixes.length);
            int wasted = this.prefixes.length - this.size;
            int newCapacity = this.prefixes.length - delta;
            if (wasted >= delta && newCapacity >= 2) {
                short[] newPrefixes = new short[newCapacity];
                System.arraycopy(this.prefixes, 0, newPrefixes, 0, position);
                System.arraycopy(this.prefixes, position + 1, newPrefixes, position, this.size - position);
                this.prefixes = newPrefixes;
                Storage16[] newStorages = new Storage16[newCapacity];
                System.arraycopy(this.storages, 0, newStorages, 0, position);
                System.arraycopy(this.storages, position + 1, newStorages, position, this.size - position);
                this.storages = newStorages;
            } else {
                System.arraycopy(this.prefixes, position + 1, this.prefixes, position, this.size - position);
                System.arraycopy(this.storages, position + 1, this.storages, position, this.size - position);
                this.storages[this.size] = null;
            }
            return this;
        }

        @Override
        public Object get(int index) {
            short prefix = (short)(index >>> 16);
            int unsignedPrefix = BitmapUtils.toUnsignedInt(prefix);
            if (unsignedPrefix == this.lastPrefix) {
                return this.lastStorage.get((short)index);
            }
            if (this.size == 0) {
                return null;
            }
            int position = BitmapUtils.unsignedBinarySearch(this.prefixes, this.size, unsignedPrefix);
            if (position < 0) {
                return null;
            }
            Storage16 storage = this.storages[position];
            this.lastPrefix = unsignedPrefix;
            this.lastStorage = storage;
            return storage.get((short)index);
        }

        @Override
        public long iterate(Iterator iterator) {
            if (this.size > 0) {
                iterator.position32 = 1;
                iterator.storage16 = this.storages[0];
                return BitmapUtils.toUnsignedLong(this.prefixes[0]) << 16 | (long)iterator.storage16.iterate(iterator);
            }
            return -1L;
        }

        @Override
        public long advance(int current, Iterator iterator) {
            int postfix = iterator.storage16.advance(iterator);
            if (postfix != -1) {
                return BitmapUtils.toUnsignedLong(current) & 0xFFFF0000L | (long)postfix;
            }
            int index = iterator.position32;
            if (index < this.size) {
                iterator.storage16 = this.storages[index];
                iterator.position32 = index + 1;
                postfix = iterator.storage16.iterate(iterator);
                return BitmapUtils.toUnsignedLong(this.prefixes[index]) << 16 | (long)postfix;
            }
            return -1L;
        }

        @Override
        public long iterateAtLeastFrom(int index, Iterator iterator) {
            if (this.size == 0) {
                return -1L;
            }
            return this.iterateAtLeastFrom(index, 0, iterator);
        }

        @Override
        public long advanceAtLeastTo(int index, int current, Iterator iterator) {
            short prefix = (short)(index >>> 16);
            int unsignedPrefix = BitmapUtils.toUnsignedInt(prefix);
            int currentUnsignedPrefix = (current & 0xFFFF0000) >>> 16;
            assert (currentUnsignedPrefix <= unsignedPrefix);
            if (unsignedPrefix == currentUnsignedPrefix) {
                int postfix = iterator.storage16.advanceAtLeastTo((short)index, (short)current, iterator);
                if (postfix != -1) {
                    return BitmapUtils.toUnsignedLong(prefix) << 16 | (long)postfix;
                }
                int position = iterator.position32;
                if (position == this.size) {
                    return -1L;
                }
                Storage16 storage = this.storages[position];
                iterator.storage16 = storage;
                iterator.position32 = position + 1;
                postfix = storage.iterate(iterator);
                assert (postfix != -1);
                return BitmapUtils.toUnsignedLong(this.prefixes[position]) << 16 | (long)postfix;
            }
            int position = iterator.position32;
            if (position == this.size) {
                return -1L;
            }
            return this.iterateAtLeastFrom(index, position, iterator);
        }

        private void append(int index, Object value) {
            short prefix = (short)(index >>> 16);
            if (this.size != 0 && prefix == this.prefixes[this.size - 1]) {
                this.storages[this.size - 1].set((short)index, value);
                return;
            }
            if (this.size == this.prefixes.length) {
                int newCapacity = Math.min(65536, this.size + BitmapUtils.capacityDeltaShort(this.prefixes.length));
                this.prefixes = Arrays.copyOf(this.prefixes, newCapacity);
                this.storages = Arrays.copyOf(this.storages, newCapacity);
            }
            this.prefixes[this.size] = prefix;
            this.storages[this.size] = new Storage16((short)index, value);
            ++this.size;
        }

        private long iterateAtLeastFrom(int index, int startFrom, Iterator iterator) {
            int postfix;
            Storage16 storage;
            short prefix = (short)(index >>> 16);
            int position = BitmapUtils.unsignedBinarySearch(this.prefixes, startFrom, this.size, BitmapUtils.toUnsignedInt(prefix));
            if (position < 0) {
                if ((position = -(position + 1)) == this.size) {
                    return -1L;
                }
                storage = this.storages[position];
                prefix = this.prefixes[position];
                postfix = storage.iterate(iterator);
                assert (postfix != -1);
            } else {
                storage = this.storages[position];
                postfix = storage.iterateAtLeastFrom((short)index, iterator);
            }
            if (postfix != -1) {
                iterator.storage16 = storage;
                iterator.position32 = position + 1;
                return BitmapUtils.toUnsignedLong(prefix) << 16 | (long)postfix;
            }
            if (++position == this.size) {
                return -1L;
            }
            storage = this.storages[position];
            iterator.storage16 = storage;
            iterator.position32 = position + 1;
            postfix = storage.iterate(iterator);
            assert (postfix != -1);
            return BitmapUtils.toUnsignedLong(this.prefixes[position]) << 16 | (long)postfix;
        }
    }

    private static final class ArrayStorage32
    implements Storage32 {
        private static final int MIN_CAPACITY = 1;
        private int size;
        private int[] indexes;
        private Object[] values = new Object[1];

        private ArrayStorage32() {
        }

        @Override
        public Storage32 set(int index, Object value) {
            if (this.indexes == null) {
                return this.setDense(index, value);
            }
            return this.setSparse(index, value);
        }

        private Storage32 setDense(int index, Object value) {
            long unsignedIndex = BitmapUtils.toUnsignedLong(index);
            if (unsignedIndex < (long)this.values.length) {
                if (this.values[index] == null) {
                    ++this.size;
                }
                this.values[index] = value;
                return this;
            }
            int delta = BitmapUtils.denseCapacityDeltaInt(this.size, this.values.length);
            int newCapacity = Math.min(262145, this.size + delta);
            if (unsignedIndex < (long)newCapacity) {
                this.values = Arrays.copyOf(this.values, newCapacity);
                this.values[index] = value;
                ++this.size;
                return this;
            }
            if (this.size >= 513) {
                return new PrefixStorage32(this.values, this.size, index, value);
            }
            Object[] oldValues = this.values;
            if (this.size == this.values.length) {
                this.indexes = new int[newCapacity];
                this.values = new Object[newCapacity];
            } else {
                this.indexes = new int[this.values.length];
            }
            int count = 0;
            for (int i = 0; i < this.values.length; ++i) {
                Object storedValue = oldValues[i];
                if (storedValue == null) continue;
                this.indexes[count] = i;
                this.values[count] = storedValue;
                if (++count == this.size) break;
            }
            this.indexes[this.size] = index;
            this.values[this.size] = value;
            ++this.size;
            return this;
        }

        private Storage32 setSparse(int index, Object value) {
            assert (this.size > 0);
            long unsignedIndex = BitmapUtils.toUnsignedLong(index);
            int position = BitmapUtils.unsignedBinarySearch(this.indexes, this.size, unsignedIndex);
            if (position >= 0) {
                this.values[position] = value;
                return this;
            }
            position = -(position + 1);
            if (this.size == this.indexes.length) {
                int delta = BitmapUtils.capacityDeltaInt(this.indexes.length);
                long lastIndex = BitmapUtils.toUnsignedLong(this.indexes[this.indexes.length - 1]);
                long denseCapacity = Math.max(unsignedIndex, lastIndex) + 1L;
                if (denseCapacity <= (long)(this.size + delta)) {
                    Object[] newValues = new Object[(int)denseCapacity];
                    for (int i = 0; i < this.indexes.length; ++i) {
                        newValues[this.indexes[i]] = this.values[i];
                    }
                    newValues[index] = value;
                    this.indexes = null;
                    this.values = newValues;
                    ++this.size;
                    return this;
                }
                if (this.size >= 513) {
                    return new PrefixStorage32(this.indexes, this.values, position, index, value);
                }
                int newCapacity = Math.min(513, this.size + delta);
                int[] newIndexes = new int[newCapacity];
                System.arraycopy(this.indexes, 0, newIndexes, 0, position);
                System.arraycopy(this.indexes, position, newIndexes, position + 1, this.size - position);
                this.indexes = newIndexes;
                Object[] newValues = new Object[newCapacity];
                System.arraycopy(this.values, 0, newValues, 0, position);
                System.arraycopy(this.values, position, newValues, position + 1, this.size - position);
                this.values = newValues;
            } else {
                System.arraycopy(this.indexes, position, this.indexes, position + 1, this.size - position);
                System.arraycopy(this.values, position, this.values, position + 1, this.size - position);
            }
            this.indexes[position] = index;
            this.values[position] = value;
            ++this.size;
            return this;
        }

        @Override
        public Storage32 clear(int index) {
            if (this.indexes == null) {
                return this.clearDense(index);
            }
            return this.clearSparse(index);
        }

        private Storage32 clearDense(int index) {
            long unsignedIndex = BitmapUtils.toUnsignedLong(index);
            if (unsignedIndex >= (long)this.values.length) {
                return this;
            }
            if (this.values[index] == null) {
                return this;
            }
            this.values[index] = null;
            --this.size;
            if (this.size == 0) {
                return null;
            }
            int wasted = this.values.length - this.size;
            int delta = BitmapUtils.capacityDeltaInt(this.values.length);
            if (wasted < delta) {
                return this;
            }
            int newCapacity = this.values.length - delta;
            if (newCapacity < 1) {
                return this;
            }
            assert (wasted == delta);
            assert (newCapacity == this.size);
            Object[] newValues = new Object[newCapacity];
            int left = this.size;
            for (int i = this.values.length - 1; i >= 0; --i) {
                Object value = this.values[i];
                if (value == null) continue;
                if (i >= newCapacity && this.indexes == null) {
                    if (this.size > 513) {
                        return new PrefixStorage32(this.values, this.size);
                    }
                    this.indexes = new int[newCapacity];
                }
                --left;
                if (this.indexes != null) {
                    this.indexes[left] = i;
                }
                newValues[left] = value;
                if (left == 0) break;
            }
            assert (left == 0);
            this.values = newValues;
            return this;
        }

        private Storage32 clearSparse(int index) {
            assert (this.size > 0);
            long unsignedIndex = BitmapUtils.toUnsignedLong(index);
            int position = BitmapUtils.unsignedBinarySearch(this.indexes, this.size, unsignedIndex);
            if (position < 0) {
                return this;
            }
            --this.size;
            if (this.size == 0) {
                this.values[position] = null;
                this.indexes = null;
                return null;
            }
            int delta = BitmapUtils.capacityDeltaInt(this.indexes.length);
            int wasted = this.indexes.length - this.size;
            int newCapacity = this.indexes.length - delta;
            if (wasted >= delta && newCapacity >= 1) {
                int[] newIndexes = new int[newCapacity];
                System.arraycopy(this.indexes, 0, newIndexes, 0, position);
                System.arraycopy(this.indexes, position + 1, newIndexes, position, this.size - position);
                this.indexes = newIndexes;
                Object[] newValues = new Object[newCapacity];
                System.arraycopy(this.values, 0, newValues, 0, position);
                System.arraycopy(this.values, position + 1, newValues, position, this.size - position);
                this.values = newValues;
            } else {
                System.arraycopy(this.indexes, position + 1, this.indexes, position, this.size - position);
                System.arraycopy(this.values, position + 1, this.values, position, this.size - position);
                this.values[this.size] = null;
            }
            return this;
        }

        @Override
        public Object get(int index) {
            long unsignedIndex = BitmapUtils.toUnsignedLong(index);
            if (this.indexes == null) {
                return unsignedIndex < (long)this.values.length ? this.values[index] : null;
            }
            assert (this.size > 0);
            int position = BitmapUtils.unsignedBinarySearch(this.indexes, this.size, unsignedIndex);
            return position >= 0 ? this.values[position] : null;
        }

        @Override
        public long iterate(Iterator iterator) {
            if (this.size > 0) {
                iterator.position32 = 0;
                long index = this.advance(0, iterator);
                assert (index != -1L);
                return index;
            }
            return -1L;
        }

        @Override
        public long advance(int current, Iterator iterator) {
            int position;
            if (this.indexes == null) {
                for (position = iterator.position32; position < this.values.length; ++position) {
                    Object value = this.values[position];
                    if (value == null) continue;
                    iterator.value = value;
                    iterator.position32 = position + 1;
                    return position;
                }
                return -1L;
            }
            assert (this.size > 0);
            if (position < this.size) {
                iterator.value = this.values[position];
                iterator.position32 = position + 1;
                return BitmapUtils.toUnsignedLong(this.indexes[position]);
            }
            return -1L;
        }

        @Override
        public long iterateAtLeastFrom(int index, Iterator iterator) {
            long unsignedIndex = BitmapUtils.toUnsignedLong(index);
            if (this.indexes == null) {
                for (long position = unsignedIndex; position < (long)this.values.length; ++position) {
                    Object value = this.values[(int)position];
                    if (value == null) continue;
                    iterator.value = value;
                    iterator.position32 = (int)position + 1;
                    return position;
                }
                return -1L;
            }
            assert (this.size > 0);
            int position = BitmapUtils.unsignedBinarySearch(this.indexes, this.size, unsignedIndex);
            if (position < 0) {
                if ((position = -(position + 1)) == this.size) {
                    return -1L;
                }
                unsignedIndex = BitmapUtils.toUnsignedLong(this.indexes[position]);
            }
            iterator.position32 = position + 1;
            iterator.value = this.values[position];
            return unsignedIndex;
        }

        @Override
        public long advanceAtLeastTo(int index, int current, Iterator iterator) {
            long unsignedIndex = BitmapUtils.toUnsignedLong(index);
            assert (BitmapUtils.toUnsignedLong(current) < unsignedIndex);
            if (this.indexes == null) {
                for (long position = unsignedIndex; position < (long)this.values.length; ++position) {
                    Object value = this.values[(int)position];
                    if (value == null) continue;
                    iterator.value = value;
                    iterator.position32 = (int)position + 1;
                    return position;
                }
                return -1L;
            }
            assert (this.size > 0);
            int position = iterator.position32;
            if (position == this.size) {
                return -1L;
            }
            if ((position = BitmapUtils.unsignedBinarySearch(this.indexes, position, this.size, unsignedIndex)) < 0) {
                if ((position = -(position + 1)) == this.size) {
                    return -1L;
                }
                unsignedIndex = BitmapUtils.toUnsignedLong(this.indexes[position]);
            }
            iterator.value = this.values[position];
            iterator.position32 = position + 1;
            return unsignedIndex;
        }
    }

    private static interface Storage32 {
        public Storage32 set(int var1, Object var2);

        public Storage32 clear(int var1);

        public Object get(int var1);

        public long iterate(Iterator var1);

        public long advance(int var1, Iterator var2);

        public long iterateAtLeastFrom(int var1, Iterator var2);

        public long advanceAtLeastTo(int var1, int var2, Iterator var3);
    }

    public static class Iterator<T> {
        public static final long END = -1L;
        private int position32;
        private Storage16 storage16;
        private int position16;
        private T value;

        public final T getValue() {
            return this.value;
        }
    }
}

