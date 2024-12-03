/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.bitmap;

import com.hazelcast.query.impl.bitmap.AscendingLongIterator;
import com.hazelcast.query.impl.bitmap.BitmapUtils;
import com.hazelcast.query.impl.bitmap.SparseIntArray;
import java.util.Arrays;

final class SparseBitSet {
    public static final int ARRAY_STORAGE_32_MAX_SIZE = 513;
    public static final int ARRAY_STORAGE_16_MAX_SIZE = 4096;
    private static final long INT_PREFIX_MASK = -4294967296L;
    private static final long INT_POSTFIX_MASK = 0xFFFFFFFFL;
    private static final long SHORT_PREFIX_MASK = 0xFFFF0000L;
    private static final long INT_PREFIX_SHORT_POSTFIX_MASK = -4294901761L;
    private static final long INT_PREFIX_SHORT_PREFIX_MASK = -65536L;
    private static final long SHORT_POSTFIX_MASK = 65535L;
    private final SparseIntArray<Storage32> storages = new SparseIntArray();
    private int lastPrefix = -1;
    private Storage32 lastStorage;

    SparseBitSet() {
    }

    public void add(long member) {
        assert (member >= 0L);
        int prefix = (int)(member >>> 32);
        if (prefix == this.lastPrefix) {
            Storage32 newStorage = this.lastStorage.add((int)member);
            if (newStorage != this.lastStorage) {
                this.lastStorage = newStorage;
                this.storages.set(prefix, newStorage);
            }
        } else {
            this.lastPrefix = prefix;
            Storage32 storage = this.storages.get(prefix);
            if (storage == null) {
                ArrayStorage32 createdStorage = new ArrayStorage32((int)member);
                this.lastStorage = createdStorage;
                this.storages.set(prefix, createdStorage);
            } else {
                Storage32 newStorage = storage.add((int)member);
                if (newStorage == storage) {
                    this.lastStorage = storage;
                } else {
                    this.lastStorage = newStorage;
                    this.storages.set(prefix, newStorage);
                }
            }
        }
    }

    public boolean remove(long member) {
        assert (member >= 0L);
        int prefix = (int)(member >>> 32);
        if (prefix == this.lastPrefix) {
            if (this.lastStorage.remove((int)member)) {
                this.lastPrefix = -1;
                this.lastStorage = null;
                return this.storages.clear(prefix);
            }
            return false;
        }
        Storage32 storage = this.storages.get(prefix);
        if (storage == null) {
            return false;
        }
        if (storage.remove((int)member)) {
            this.lastPrefix = -1;
            this.lastStorage = null;
            return this.storages.clear(prefix);
        }
        this.lastPrefix = prefix;
        this.lastStorage = storage;
        return false;
    }

    public AscendingLongIterator iterator() {
        return new IteratorImpl(this.storages);
    }

    private static final class IteratorImpl
    extends SparseIntArray.Iterator<Storage32>
    implements AscendingLongIterator {
        private final SparseIntArray<Storage32> storage64;
        private int position32;
        private Storage16 storage16;
        private int position16;
        private long bitSet16;
        private long index;

        IteratorImpl(SparseIntArray<Storage32> storage64) {
            this.storage64 = storage64;
            long prefix = storage64.iterate(this);
            if (prefix != -1L) {
                this.index = prefix << 32;
                this.getStorage32().iterate(this);
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
            if (this.getStorage32().advance(this)) {
                return current;
            }
            long prefix = this.storage64.advance((int)(current >>> 32), this);
            if (prefix != -1L) {
                this.index = prefix << 32;
                this.getStorage32().iterate(this);
                return current;
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
                if (this.getStorage32().advanceAtLeastTo((int)member, this)) {
                    return this.index;
                }
                long prefix = this.storage64.advance(currentPrefix, this);
                if (prefix != -1L) {
                    this.index = prefix << 32;
                    this.getStorage32().iterate(this);
                    return this.index;
                }
            } else {
                long prefix = this.storage64.advanceAtLeastTo(memberPrefix, currentPrefix, this);
                if (prefix != -1L) {
                    if (prefix == (long)memberPrefix) {
                        this.index = prefix << 32;
                        if (this.getStorage32().iterateAtLeastFrom((int)member, this)) {
                            return this.index;
                        }
                        if ((prefix = this.storage64.advance((int)prefix, this)) != -1L) {
                            this.index = prefix << 32;
                            this.getStorage32().iterate(this);
                            return this.index;
                        }
                    } else {
                        this.index = prefix << 32;
                        this.getStorage32().iterate(this);
                        return this.index;
                    }
                }
            }
            this.index = -1L;
            return -1L;
        }

        private Storage32 getStorage32() {
            return (Storage32)this.getValue();
        }
    }

    private static final class BitSetStorage16
    implements Storage16 {
        public static final int BIT_SET_LONG_SHIFT = 6;
        private static final int MIN_SIZE = 4095;
        private static final int SIZE = 1024;
        private static final long POSTFIX_MASK = -64L;
        private final long[] members = new long[1024];
        private int size;

        BitSetStorage16(short[] members, short member, int index) {
            int i;
            for (i = 0; i < index; ++i) {
                this.append(members[i]);
            }
            this.append(member);
            for (i = index; i < members.length; ++i) {
                this.append(members[i]);
            }
            this.size = members.length + 1;
        }

        @Override
        public Storage16 add(short member) {
            long newBitSet;
            int bitIndex = BitmapUtils.toUnsignedInt(member);
            int longIndex = bitIndex >>> 6;
            long bitSet = this.members[longIndex];
            this.members[longIndex] = newBitSet = bitSet | 1L << bitIndex;
            if (newBitSet != bitSet) {
                ++this.size;
            }
            return this;
        }

        @Override
        public Storage16 remove(short member) {
            long newBitSet;
            int bitIndex = BitmapUtils.toUnsignedInt(member);
            int longIndex = bitIndex >>> 6;
            long bitSet = this.members[longIndex];
            this.members[longIndex] = newBitSet = bitSet & (1L << bitIndex ^ 0xFFFFFFFFFFFFFFFFL);
            if (newBitSet != bitSet) {
                --this.size;
                if (this.size == 4095) {
                    return new ArrayStorage16(this.members, this.size);
                }
            }
            return this;
        }

        @Override
        public void iterate(IteratorImpl iterator) {
            assert (this.size > 0);
            iterator.position16 = 0;
            iterator.bitSet16 = this.members[0];
            iterator.index = iterator.index & 0xFFFFFFFFFFFF0000L;
            boolean advanced = this.advance(iterator);
            assert (advanced);
        }

        @Override
        public boolean advance(IteratorImpl iterator) {
            long bitSet = iterator.bitSet16;
            if (bitSet != 0L) {
                iterator.index = iterator.index & 0xFFFFFFFFFFFFFFC0L | (long)Long.numberOfTrailingZeros(bitSet);
                iterator.bitSet16 = bitSet & bitSet - 1L;
                return true;
            }
            int index = iterator.position16;
            do {
                if (++index != this.members.length) continue;
                return false;
            } while ((bitSet = this.members[index]) == 0L);
            iterator.index = iterator.index & 0xFFFFFFFFFFFF0000L | (long)(index << 6) | (long)Long.numberOfTrailingZeros(bitSet);
            iterator.bitSet16 = bitSet & bitSet - 1L;
            iterator.position16 = index;
            return true;
        }

        @Override
        public boolean iterateAtLeastFrom(short member, IteratorImpl iterator) {
            int bitIndex = BitmapUtils.toUnsignedInt(member);
            int longIndex = bitIndex >>> 6;
            iterator.position16 = longIndex;
            iterator.bitSet16 = this.members[longIndex] & -(1L << bitIndex);
            iterator.index = iterator.index & 0xFFFFFFFFFFFF0000L | (long)(longIndex << 6);
            return this.advance(iterator);
        }

        @Override
        public boolean advanceAtLeastTo(short member, IteratorImpl iterator) {
            long current = iterator.index;
            int bitIndex = BitmapUtils.toUnsignedInt(member);
            assert ((current & 0xFFFFL) < (long)bitIndex);
            int longIndex = bitIndex >>> 6;
            iterator.position16 = longIndex;
            iterator.bitSet16 = this.members[longIndex] & -(1L << bitIndex);
            iterator.index = current & 0xFFFFFFFFFFFF0000L | (long)(longIndex << 6);
            return this.advance(iterator);
        }

        private void append(short member) {
            int bitIndex = BitmapUtils.toUnsignedInt(member);
            int n = bitIndex >>> 6;
            this.members[n] = this.members[n] | 1L << bitIndex;
        }
    }

    private static final class ArrayStorage16
    implements Storage16 {
        private static final int MIN_CAPACITY = 2;
        private int size;
        private short[] members;

        ArrayStorage16(short member) {
            this.size = 1;
            this.members = new short[2];
            this.members[0] = member;
        }

        ArrayStorage16(long[] bits, int size) {
            assert (size == 4095);
            this.size = size;
            short[] members = new short[4096];
            int index = 0;
            for (int i = 0; i < bits.length; ++i) {
                int base = i << 6;
                for (long value = bits[i]; value != 0L; value &= value - 1L) {
                    int offset = Long.numberOfTrailingZeros(value);
                    members[index++] = (short)(base + offset);
                }
            }
            assert (index == size);
            this.members = members;
        }

        @Override
        public Storage16 add(short member) {
            int index = BitmapUtils.unsignedBinarySearch(this.members, this.size, BitmapUtils.toUnsignedInt(member));
            if (index >= 0) {
                return this;
            }
            index = -(index + 1);
            if (this.size == this.members.length) {
                if (this.size == 4096) {
                    return new BitSetStorage16(this.members, member, index);
                }
                int newCapacity = Math.min(4096, this.size + BitmapUtils.capacityDeltaShort(this.members.length));
                short[] newMembers = new short[newCapacity];
                System.arraycopy(this.members, 0, newMembers, 0, index);
                System.arraycopy(this.members, index, newMembers, index + 1, this.size - index);
                this.members = newMembers;
            } else {
                System.arraycopy(this.members, index, this.members, index + 1, this.size - index);
            }
            this.members[index] = member;
            ++this.size;
            return this;
        }

        @Override
        public Storage16 remove(short member) {
            int index = BitmapUtils.unsignedBinarySearch(this.members, this.size, BitmapUtils.toUnsignedInt(member));
            if (index < 0) {
                return this;
            }
            --this.size;
            if (this.size == 0) {
                return null;
            }
            int delta = BitmapUtils.capacityDeltaShort(this.members.length);
            int wasted = this.members.length - this.size;
            int newCapacity = this.members.length - delta;
            if (wasted >= delta && newCapacity >= 2) {
                short[] newMembers = new short[newCapacity];
                System.arraycopy(this.members, 0, newMembers, 0, index);
                System.arraycopy(this.members, index + 1, newMembers, index, this.size - index);
                this.members = newMembers;
            } else {
                System.arraycopy(this.members, index + 1, this.members, index, this.size - index);
            }
            return this;
        }

        @Override
        public void iterate(IteratorImpl iterator) {
            assert (this.size > 0);
            iterator.position16 = 1;
            iterator.index = iterator.index & 0xFFFFFFFFFFFF0000L | (long)BitmapUtils.toUnsignedInt(this.members[0]);
        }

        @Override
        public boolean advance(IteratorImpl iterator) {
            int index = iterator.position16;
            if (index < this.size) {
                iterator.index = iterator.index & 0xFFFFFFFFFFFF0000L | (long)BitmapUtils.toUnsignedInt(this.members[index]);
                iterator.position16 = index + 1;
                return true;
            }
            return false;
        }

        @Override
        public boolean iterateAtLeastFrom(short member, IteratorImpl iterator) {
            int unsignedMember = BitmapUtils.toUnsignedInt(member);
            int index = BitmapUtils.unsignedBinarySearch(this.members, this.size, unsignedMember);
            if (index < 0) {
                if ((index = -(index + 1)) == this.size) {
                    return false;
                }
                unsignedMember = BitmapUtils.toUnsignedInt(this.members[index]);
            }
            iterator.index = iterator.index & 0xFFFFFFFFFFFF0000L | (long)unsignedMember;
            iterator.position16 = index + 1;
            return true;
        }

        @Override
        public boolean advanceAtLeastTo(short member, IteratorImpl iterator) {
            int unsignedMember = BitmapUtils.toUnsignedInt(member);
            long current = iterator.index;
            assert ((current & 0xFFFFL) < (long)unsignedMember);
            int position = iterator.position16;
            if (position == this.size) {
                return false;
            }
            if ((position = BitmapUtils.unsignedBinarySearch(this.members, position, this.size, unsignedMember)) < 0) {
                if ((position = -(position + 1)) == this.size) {
                    return false;
                }
                unsignedMember = BitmapUtils.toUnsignedInt(this.members[position]);
            }
            iterator.index = current & 0xFFFFFFFFFFFF0000L | (long)unsignedMember;
            iterator.position16 = position + 1;
            return true;
        }

        public void append(short member) {
            if (this.size == this.members.length) {
                int newCapacity = this.size + BitmapUtils.capacityDeltaShort(this.members.length);
                assert (newCapacity <= 4096);
                this.members = Arrays.copyOf(this.members, newCapacity);
            }
            this.members[this.size] = member;
            ++this.size;
        }
    }

    private static interface Storage16 {
        public Storage16 add(short var1);

        public Storage16 remove(short var1);

        public void iterate(IteratorImpl var1);

        public boolean advance(IteratorImpl var1);

        public boolean iterateAtLeastFrom(short var1, IteratorImpl var2);

        public boolean advanceAtLeastTo(short var1, IteratorImpl var2);
    }

    private static final class PrefixStorage32
    implements Storage32 {
        private static final int MIN_CAPACITY = 2;
        private static final int MAX_CAPACITY = 65536;
        private int size;
        private short[] prefixes = new short[2];
        private Storage16[] storages = new Storage16[2];
        private int lastPrefix = -1;
        private Storage16 lastStorage;

        PrefixStorage32(int[] members, int member, int index) {
            int i;
            for (i = 0; i < index; ++i) {
                this.append(members[i]);
            }
            this.append(member);
            for (i = index; i < members.length; ++i) {
                this.append(members[i]);
            }
        }

        @Override
        public Storage32 add(int member) {
            short prefix = (short)(member >>> 16);
            int unsignedPrefix = BitmapUtils.toUnsignedInt(prefix);
            if (unsignedPrefix == this.lastPrefix) {
                Storage16 newStorage = this.lastStorage.add((short)member);
                if (newStorage != this.lastStorage) {
                    int index = BitmapUtils.unsignedBinarySearch(this.prefixes, this.size, unsignedPrefix);
                    assert (index >= 0);
                    this.storages[index] = newStorage;
                    this.lastStorage = newStorage;
                }
                return this;
            }
            int index = BitmapUtils.unsignedBinarySearch(this.prefixes, this.size, unsignedPrefix);
            if (index >= 0) {
                Storage16 storage = this.storages[index];
                Storage16 newStorage = storage.add((short)member);
                if (newStorage != storage) {
                    this.storages[index] = newStorage;
                }
                this.lastPrefix = unsignedPrefix;
                this.lastStorage = newStorage;
                return this;
            }
            index = -(index + 1);
            if (this.size == this.prefixes.length) {
                int newCapacity = Math.min(65536, this.size + BitmapUtils.capacityDeltaShort(this.prefixes.length));
                short[] newPrefixes = new short[newCapacity];
                System.arraycopy(this.prefixes, 0, newPrefixes, 0, index);
                System.arraycopy(this.prefixes, index, newPrefixes, index + 1, this.size - index);
                this.prefixes = newPrefixes;
                Storage16[] newStorages = new Storage16[newCapacity];
                System.arraycopy(this.storages, 0, newStorages, 0, index);
                System.arraycopy(this.storages, index, newStorages, index + 1, this.size - index);
                this.storages = newStorages;
            } else {
                System.arraycopy(this.prefixes, index, this.prefixes, index + 1, this.size - index);
                System.arraycopy(this.storages, index, this.storages, index + 1, this.size - index);
            }
            ArrayStorage16 createdStorage = new ArrayStorage16((short)member);
            this.prefixes[index] = prefix;
            this.storages[index] = createdStorage;
            this.lastPrefix = unsignedPrefix;
            this.lastStorage = createdStorage;
            ++this.size;
            return this;
        }

        @Override
        public boolean remove(int member) {
            int index;
            Storage16 newStorage;
            Storage16 storage;
            short prefix = (short)(member >>> 16);
            int unsignedPrefix = BitmapUtils.toUnsignedInt(prefix);
            if (unsignedPrefix == this.lastPrefix) {
                storage = this.lastStorage;
                newStorage = storage.remove((short)member);
                if (newStorage == storage) {
                    return false;
                }
                index = BitmapUtils.unsignedBinarySearch(this.prefixes, this.size, unsignedPrefix);
                assert (index >= 0);
            } else {
                index = BitmapUtils.unsignedBinarySearch(this.prefixes, this.size, unsignedPrefix);
                if (index < 0) {
                    return false;
                }
                storage = this.storages[index];
                newStorage = storage.remove((short)member);
                if (newStorage == storage) {
                    this.lastStorage = storage;
                    this.lastPrefix = unsignedPrefix;
                    return false;
                }
            }
            if (newStorage == null) {
                --this.size;
                this.lastStorage = null;
                this.lastPrefix = -1;
                if (this.size == 0) {
                    return true;
                }
                int delta = BitmapUtils.capacityDeltaShort(this.prefixes.length);
                int wasted = this.prefixes.length - this.size;
                int newCapacity = this.prefixes.length - delta;
                if (wasted >= delta && newCapacity >= 2) {
                    short[] newPrefixes = new short[newCapacity];
                    System.arraycopy(this.prefixes, 0, newPrefixes, 0, index);
                    System.arraycopy(this.prefixes, index + 1, newPrefixes, index, this.size - index);
                    this.prefixes = newPrefixes;
                    Storage16[] newStorages = new Storage16[newCapacity];
                    System.arraycopy(this.storages, 0, newStorages, 0, index);
                    System.arraycopy(this.storages, index + 1, newStorages, index, this.size - index);
                    this.storages = newStorages;
                } else {
                    System.arraycopy(this.prefixes, index + 1, this.prefixes, index, this.size - index);
                    System.arraycopy(this.storages, index + 1, this.storages, index, this.size - index);
                }
            } else {
                this.lastStorage = newStorage;
                this.lastPrefix = unsignedPrefix;
                this.storages[index] = newStorage;
            }
            return false;
        }

        @Override
        public void iterate(IteratorImpl iterator) {
            assert (this.size > 0);
            iterator.position32 = 1;
            Storage16 storage = this.storages[0];
            iterator.storage16 = storage;
            iterator.index = iterator.index & 0xFFFFFFFF00000000L | BitmapUtils.toUnsignedLong(this.prefixes[0]) << 16;
            storage.iterate(iterator);
        }

        @Override
        public boolean advance(IteratorImpl iterator) {
            if (iterator.storage16.advance(iterator)) {
                return true;
            }
            int position = iterator.position32;
            if (position < this.size) {
                Storage16 storage = this.storages[position];
                iterator.storage16 = storage;
                iterator.index = iterator.index & 0xFFFFFFFF00000000L | BitmapUtils.toUnsignedLong(this.prefixes[position]) << 16;
                iterator.position32 = position + 1;
                storage.iterate(iterator);
                return true;
            }
            return false;
        }

        @Override
        public boolean iterateAtLeastFrom(int member, IteratorImpl iterator) {
            return this.iterateAtLeastFrom(member, 0, iterator);
        }

        @Override
        public boolean advanceAtLeastTo(int member, IteratorImpl iterator) {
            short prefix = (short)(member >>> 16);
            int unsignedPrefix = BitmapUtils.toUnsignedInt(prefix);
            long current = iterator.index;
            int currentUnsignedPrefix = (int)(current & 0xFFFF0000L) >>> 16;
            assert (currentUnsignedPrefix <= unsignedPrefix);
            if (unsignedPrefix == currentUnsignedPrefix) {
                if (iterator.storage16.advanceAtLeastTo((short)member, iterator)) {
                    return true;
                }
                int position = iterator.position32;
                if (position == this.size) {
                    return false;
                }
                Storage16 storage = this.storages[position];
                iterator.storage16 = storage;
                iterator.index = current & 0xFFFFFFFF00000000L | BitmapUtils.toUnsignedLong(this.prefixes[position]) << 16;
                iterator.position32 = position + 1;
                storage.iterate(iterator);
                return true;
            }
            int position = iterator.position32;
            if (position == this.size) {
                return false;
            }
            return this.iterateAtLeastFrom(member, position, iterator);
        }

        private void append(int member) {
            short prefix = (short)(member >>> 16);
            if (this.size != 0 && prefix == this.prefixes[this.size - 1]) {
                ((ArrayStorage16)this.storages[this.size - 1]).append((short)member);
                return;
            }
            if (this.size == this.prefixes.length) {
                int newCapacity = Math.min(65536, this.size + BitmapUtils.capacityDeltaShort(this.prefixes.length));
                this.prefixes = Arrays.copyOf(this.prefixes, newCapacity);
                this.storages = Arrays.copyOf(this.storages, newCapacity);
            }
            this.prefixes[this.size] = prefix;
            this.storages[this.size] = new ArrayStorage16((short)member);
            ++this.size;
        }

        private boolean iterateAtLeastFrom(int member, int fromPosition, IteratorImpl iterator) {
            short prefix = (short)(member >>> 16);
            int position = BitmapUtils.unsignedBinarySearch(this.prefixes, fromPosition, this.size, BitmapUtils.toUnsignedInt(prefix));
            if (position < 0 && (position = -(position + 1)) == this.size) {
                return false;
            }
            Storage16 storage = this.storages[position];
            if (storage.iterateAtLeastFrom((short)member, iterator)) {
                iterator.storage16 = storage;
                iterator.index = iterator.index & 0xFFFFFFFF0000FFFFL | BitmapUtils.toUnsignedLong(this.prefixes[position]) << 16;
                iterator.position32 = position + 1;
            } else {
                if (++position == this.size) {
                    return false;
                }
                storage = this.storages[position];
                iterator.storage16 = storage;
                iterator.index = iterator.index & 0xFFFFFFFF00000000L | BitmapUtils.toUnsignedLong(this.prefixes[position]) << 16;
                iterator.position32 = position + 1;
                storage.iterate(iterator);
            }
            return true;
        }
    }

    private static final class ArrayStorage32
    implements Storage32 {
        private static final int MIN_CAPACITY = 1;
        private int size = 1;
        private int[] members = new int[1];

        ArrayStorage32(int member) {
            this.members[0] = member;
        }

        @Override
        public Storage32 add(int member) {
            int index = BitmapUtils.unsignedBinarySearch(this.members, this.size, BitmapUtils.toUnsignedLong(member));
            if (index >= 0) {
                return this;
            }
            index = -(index + 1);
            if (this.size == this.members.length) {
                if (this.size == 513) {
                    return new PrefixStorage32(this.members, member, index);
                }
                int newCapacity = Math.min(513, this.size + BitmapUtils.capacityDeltaInt(this.members.length));
                int[] newMembers = new int[newCapacity];
                System.arraycopy(this.members, 0, newMembers, 0, index);
                System.arraycopy(this.members, index, newMembers, index + 1, this.size - index);
                this.members = newMembers;
            } else {
                System.arraycopy(this.members, index, this.members, index + 1, this.size - index);
            }
            this.members[index] = member;
            ++this.size;
            return this;
        }

        @Override
        public boolean remove(int member) {
            int index = BitmapUtils.unsignedBinarySearch(this.members, this.size, BitmapUtils.toUnsignedLong(member));
            if (index < 0) {
                return false;
            }
            --this.size;
            if (this.size == 0) {
                return true;
            }
            int delta = BitmapUtils.capacityDeltaInt(this.members.length);
            int wasted = this.members.length - this.size;
            int newCapacity = this.members.length - delta;
            if (wasted >= delta && newCapacity >= 1) {
                int[] newMembers = new int[newCapacity];
                System.arraycopy(this.members, 0, newMembers, 0, index);
                System.arraycopy(this.members, index + 1, newMembers, index, this.size - index);
                this.members = newMembers;
            } else {
                System.arraycopy(this.members, index + 1, this.members, index, this.size - index);
            }
            return false;
        }

        @Override
        public void iterate(IteratorImpl iterator) {
            assert (this.size > 0);
            iterator.position32 = 1;
            iterator.index = iterator.index & 0xFFFFFFFF00000000L | BitmapUtils.toUnsignedLong(this.members[0]);
        }

        @Override
        public boolean advance(IteratorImpl iterator) {
            int position = iterator.position32;
            if (position < this.size) {
                iterator.index = iterator.index & 0xFFFFFFFF00000000L | BitmapUtils.toUnsignedLong(this.members[position]);
                iterator.position32 = position + 1;
                return true;
            }
            return false;
        }

        @Override
        public boolean iterateAtLeastFrom(int member, IteratorImpl iterator) {
            long unsignedMember = BitmapUtils.toUnsignedLong(member);
            int position = BitmapUtils.unsignedBinarySearch(this.members, this.size, unsignedMember);
            if (position < 0) {
                if ((position = -(position + 1)) == this.size) {
                    return false;
                }
                unsignedMember = BitmapUtils.toUnsignedLong(this.members[position]);
            }
            iterator.index = iterator.index & 0xFFFFFFFF00000000L | unsignedMember;
            iterator.position32 = position + 1;
            return true;
        }

        @Override
        public boolean advanceAtLeastTo(int member, IteratorImpl iterator) {
            long unsignedMember = BitmapUtils.toUnsignedLong(member);
            long current = iterator.index;
            assert ((current & 0xFFFFFFFFL) < unsignedMember);
            int position = iterator.position32;
            if (position == this.size) {
                return false;
            }
            if ((position = BitmapUtils.unsignedBinarySearch(this.members, position, this.size, unsignedMember)) < 0) {
                if ((position = -(position + 1)) == this.size) {
                    return false;
                }
                unsignedMember = BitmapUtils.toUnsignedLong(this.members[position]);
            }
            iterator.index = current & 0xFFFFFFFF00000000L | unsignedMember;
            iterator.position32 = position + 1;
            return true;
        }
    }

    private static interface Storage32 {
        public Storage32 add(int var1);

        public boolean remove(int var1);

        public void iterate(IteratorImpl var1);

        public boolean advance(IteratorImpl var1);

        public boolean iterateAtLeastFrom(int var1, IteratorImpl var2);

        public boolean advanceAtLeastTo(int var1, IteratorImpl var2);
    }
}

