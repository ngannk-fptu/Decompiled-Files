/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram.packedarray;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.HdrHistogram.packedarray.IterationValue;
import org.HdrHistogram.packedarray.ResizeException;

abstract class AbstractPackedArrayContext
implements Serializable {
    private static final int PACKED_ARRAY_GROWTH_INCREMENT = 16;
    private static final int PACKED_ARRAY_GROWTH_FRACTION_POW2 = 4;
    private static final int SET_0_START_INDEX = 0;
    private static final int NUMBER_OF_SETS = 8;
    private static final int LEAF_LEVEL_SHIFT = 3;
    private static final int NON_LEAF_ENTRY_HEADER_SIZE_IN_SHORTS = 2;
    private static final int NON_LEAF_ENTRY_SLOT_INDICATORS_OFFSET = 0;
    private static final int NON_LEAF_ENTRY_PREVIOUS_VERSION_OFFSET = 1;
    static final int MINIMUM_INITIAL_PACKED_ARRAY_CAPACITY = 16;
    static final int MAX_SUPPORTED_PACKED_COUNTS_ARRAY_LENGTH = 8191;
    private final boolean isPacked;
    private int physicalLength;
    private int virtualLength = 0;
    private int topLevelShift = Integer.MAX_VALUE;

    AbstractPackedArrayContext(int virtualLength, int initialPhysicalLength) {
        this.physicalLength = Math.max(initialPhysicalLength, 16);
        boolean bl = this.isPacked = this.physicalLength <= 8191;
        if (!this.isPacked) {
            this.physicalLength = virtualLength;
        }
    }

    void init(int virtualLength) {
        if (!this.isPacked()) {
            this.virtualLength = virtualLength;
            return;
        }
        while (!this.casPopulatedShortLength(this.getPopulatedShortLength(), 8)) {
        }
        for (int i = 0; i < 8; ++i) {
            this.setAtShortIndex(0 + i, (short)0);
        }
        this.setVirtualLength(virtualLength);
    }

    abstract int length();

    abstract int getPopulatedShortLength();

    abstract boolean casPopulatedShortLength(int var1, int var2);

    abstract boolean casPopulatedLongLength(int var1, int var2);

    abstract long getAtLongIndex(int var1);

    abstract boolean casAtLongIndex(int var1, long var2, long var4);

    abstract void lazySetAtLongIndex(int var1, long var2);

    abstract void clearContents();

    abstract void resizeArray(int var1);

    abstract long getAtUnpackedIndex(int var1);

    abstract void setAtUnpackedIndex(int var1, long var2);

    abstract void lazysetAtUnpackedIndex(int var1, long var2);

    abstract long incrementAndGetAtUnpackedIndex(int var1);

    abstract long addAndGetAtUnpackedIndex(int var1, long var2);

    abstract String unpackedToString();

    void setValuePart(int longIndex, long valuePartAsLong, long valuePartMask, int valuePartShift) {
        long newLongValue;
        long currentLongValue;
        boolean success;
        while (!(success = this.casAtLongIndex(longIndex, currentLongValue = this.getAtLongIndex(longIndex), newLongValue = currentLongValue & (valuePartMask ^ 0xFFFFFFFFFFFFFFFFL) | valuePartAsLong << valuePartShift))) {
        }
    }

    short getAtShortIndex(int shortIndex) {
        return (short)(this.getAtLongIndex(shortIndex >> 2) >> ((shortIndex & 3) << 4) & 0xFFFFL);
    }

    short getIndexAtShortIndex(int shortIndex) {
        return (short)(this.getAtLongIndex(shortIndex >> 2) >> ((shortIndex & 3) << 4) & 0x7FFFL);
    }

    void setAtShortIndex(int shortIndex, short value) {
        int longIndex = shortIndex >> 2;
        int shortShift = (shortIndex & 3) << 4;
        long shortMask = 65535L << shortShift;
        long shortValueAsLong = (long)value & 0xFFFFL;
        this.setValuePart(longIndex, shortValueAsLong, shortMask, shortShift);
    }

    boolean casAtShortIndex(int shortIndex, short expectedValue, short newValue) {
        long newLongValue;
        long currentLongValue;
        boolean success;
        int longIndex = shortIndex >> 2;
        int shortShift = (shortIndex & 3) << 4;
        long shortMask = 65535L << shortShift ^ 0xFFFFFFFFFFFFFFFFL;
        long newShortValueAsLong = (long)newValue & 0xFFFFL;
        long expectedShortValueAsLong = (long)expectedValue & 0xFFFFL;
        do {
            long currentShortValueAsLong;
            if ((currentShortValueAsLong = (currentLongValue = this.getAtLongIndex(longIndex)) >> shortShift & 0xFFFFL) == expectedShortValueAsLong) continue;
            return false;
        } while (!(success = this.casAtLongIndex(longIndex, currentLongValue, newLongValue = currentLongValue & shortMask | newShortValueAsLong << shortShift)));
        return true;
    }

    byte getAtByteIndex(int byteIndex) {
        return (byte)(this.getAtLongIndex(byteIndex >> 3) >> ((byteIndex & 7) << 3) & 0xFFL);
    }

    void setAtByteIndex(int byteIndex, byte value) {
        int longIndex = byteIndex >> 3;
        int byteShift = (byteIndex & 7) << 3;
        long byteMask = 255L << byteShift;
        long byteValueAsLong = (long)value & 0xFFL;
        this.setValuePart(longIndex, byteValueAsLong, byteMask, byteShift);
    }

    long addAtByteIndex(int byteIndex, byte valueToAdd) {
        long byteValueAsLong;
        long newValue;
        long newByteValueAsLong;
        long newLongValue;
        long currentLongValue;
        boolean success;
        int longIndex = byteIndex >> 3;
        int byteShift = (byteIndex & 7) << 3;
        long byteMask = 255L << byteShift;
        while (!(success = this.casAtLongIndex(longIndex, currentLongValue = this.getAtLongIndex(longIndex), newLongValue = currentLongValue & (byteMask ^ 0xFFFFFFFFFFFFFFFFL) | (newByteValueAsLong = (newValue = (byteValueAsLong = currentLongValue >> byteShift & 0xFFL) + ((long)valueToAdd & 0xFFL)) & 0xFFL) << byteShift))) {
        }
        return newValue;
    }

    private int getPackedSlotIndicators(int entryIndex) {
        return this.getAtShortIndex(entryIndex + 0) & 0xFFFF;
    }

    private void setPackedSlotIndicators(int entryIndex, short newPackedSlotIndicators) {
        this.setAtShortIndex(entryIndex + 0, newPackedSlotIndicators);
    }

    private short getPreviousVersionIndex(int entryIndex) {
        return this.getAtShortIndex(entryIndex + 1);
    }

    private void setPreviousVersionIndex(int entryIndex, short newPreviosVersionIndex) {
        this.setAtShortIndex(entryIndex + 1, newPreviosVersionIndex);
    }

    private short getIndexAtEntrySlot(int entryIndex, int slot) {
        return this.getAtShortIndex(entryIndex + 2 + slot);
    }

    private void setIndexAtEntrySlot(int entryIndex, int slot, short newIndexValue) {
        this.setAtShortIndex(entryIndex + 2 + slot, newIndexValue);
    }

    private boolean casIndexAtEntrySlot(int entryIndex, int slot, short expectedIndexValue, short newIndexValue) {
        return this.casAtShortIndex(entryIndex + 2 + slot, expectedIndexValue, newIndexValue);
    }

    private boolean casIndexAtEntrySlotIfNonZeroAndLessThan(int entryIndex, int slot, short newIndexValue) {
        short existingIndexValue;
        boolean success;
        do {
            if ((existingIndexValue = this.getIndexAtEntrySlot(entryIndex, slot)) == 0) {
                return false;
            }
            if (newIndexValue > existingIndexValue) continue;
            return false;
        } while (!(success = this.casIndexAtEntrySlot(entryIndex, slot, existingIndexValue, newIndexValue)));
        return true;
    }

    private void expandArrayIfNeeded(int entryLengthInLongs) throws ResizeException {
        int currentLength = this.length();
        if (this.length() < this.getPopulatedLongLength() + entryLengthInLongs) {
            int growthIncrement = Math.max(entryLengthInLongs, 16);
            growthIncrement = Math.max(growthIncrement, this.getPopulatedLongLength() >> 4);
            throw new ResizeException(currentLength + growthIncrement);
        }
    }

    private int newEntry(int entryLengthInShorts) throws ResizeException {
        int newEntryIndex;
        boolean success;
        do {
            newEntryIndex = this.getPopulatedShortLength();
            this.expandArrayIfNeeded((entryLengthInShorts >> 2) + 1);
        } while (!(success = this.casPopulatedShortLength(newEntryIndex, newEntryIndex + entryLengthInShorts)));
        for (int i = 0; i < entryLengthInShorts; ++i) {
            this.setAtShortIndex(newEntryIndex + i, (short)-1);
        }
        return newEntryIndex;
    }

    private int newLeafEntry() throws ResizeException {
        int newEntryIndex;
        boolean success;
        do {
            newEntryIndex = this.getPopulatedLongLength();
            this.expandArrayIfNeeded(1);
        } while (!(success = this.casPopulatedLongLength(newEntryIndex, newEntryIndex + 1)));
        this.lazySetAtLongIndex(newEntryIndex, 0L);
        return newEntryIndex;
    }

    private void consolidateEntry(int entryIndex) {
        short previousVersionIndex = this.getPreviousVersionIndex(entryIndex);
        if (previousVersionIndex == 0) {
            return;
        }
        if (this.getPreviousVersionIndex(previousVersionIndex) != 0) {
            throw new IllegalStateException("Encountered Previous Version Entry that is not itself consolidated.");
        }
        int previousVersionPackedSlotsIndicators = this.getPackedSlotIndicators(previousVersionIndex);
        int packedSlotsIndicators = this.getPackedSlotIndicators(entryIndex);
        int insertedSlotMask = packedSlotsIndicators ^ previousVersionPackedSlotsIndicators;
        int slotsBelowBitNumber = packedSlotsIndicators & insertedSlotMask - 1;
        int insertedSlotIndex = Integer.bitCount(slotsBelowBitNumber);
        int numberOfSlotsInEntry = Integer.bitCount(packedSlotsIndicators);
        int sourceSlot = 0;
        for (int targetSlot = 0; targetSlot < numberOfSlotsInEntry; ++targetSlot) {
            if (targetSlot == insertedSlotIndex) continue;
            boolean success = true;
            do {
                short indexAtSlot;
                if ((indexAtSlot = this.getIndexAtEntrySlot(previousVersionIndex, sourceSlot)) == 0) continue;
                this.casIndexAtEntrySlotIfNonZeroAndLessThan(entryIndex, targetSlot, indexAtSlot);
                success = this.casIndexAtEntrySlot(previousVersionIndex, sourceSlot, indexAtSlot, (short)0);
            } while (!success);
            ++sourceSlot;
        }
        this.setPreviousVersionIndex(entryIndex, (short)0);
    }

    private int expandEntry(int existingEntryIndex, int entryPointerIndex, int insertedSlotIndex, int insertedSlotMask, boolean nextLevelIsLeaf) throws RetryException, ResizeException {
        int packedSlotIndicators = this.getAtShortIndex(existingEntryIndex) & 0xFFFF;
        int numberOfslotsInExpandedEntry = Integer.bitCount(packedSlotIndicators |= insertedSlotMask);
        if (insertedSlotIndex >= numberOfslotsInExpandedEntry) {
            throw new IllegalStateException("inserted slot index is out of range given provided masks");
        }
        int expandedEntryLength = numberOfslotsInExpandedEntry + 2;
        int indexOfNewNextLevelEntry = 0;
        if (nextLevelIsLeaf) {
            indexOfNewNextLevelEntry = this.newLeafEntry();
        } else {
            indexOfNewNextLevelEntry = this.newEntry(2);
            this.setPackedSlotIndicators(indexOfNewNextLevelEntry, (short)0);
            this.setPreviousVersionIndex(indexOfNewNextLevelEntry, (short)0);
        }
        short insertedSlotValue = (short)indexOfNewNextLevelEntry;
        int expandedEntryIndex = this.newEntry(expandedEntryLength);
        this.setPackedSlotIndicators(expandedEntryIndex, (short)packedSlotIndicators);
        this.setPreviousVersionIndex(expandedEntryIndex, (short)existingEntryIndex);
        this.setIndexAtEntrySlot(expandedEntryIndex, insertedSlotIndex, insertedSlotValue);
        boolean success = this.casAtShortIndex(entryPointerIndex, (short)existingEntryIndex, (short)expandedEntryIndex);
        if (!success) {
            throw new RetryException();
        }
        this.consolidateEntry(expandedEntryIndex);
        return expandedEntryIndex;
    }

    private int getRootEntry(int setNumber) {
        try {
            return this.getRootEntry(setNumber, false);
        }
        catch (RetryException | ResizeException ex) {
            throw new IllegalStateException("Should not Resize or Retry exceptions on real-only read: ", ex);
        }
    }

    private int getRootEntry(int setNumber, boolean insertAsNeeded) throws RetryException, ResizeException {
        int entryPointerIndex = 0 + setNumber;
        int entryIndex = this.getIndexAtShortIndex(entryPointerIndex);
        if (entryIndex == 0) {
            if (!insertAsNeeded) {
                return 0;
            }
            entryIndex = this.newEntry(2);
            this.setPackedSlotIndicators(entryIndex, (short)0);
            this.setPreviousVersionIndex(entryIndex, (short)0);
            boolean success = this.casAtShortIndex(entryPointerIndex, (short)0, (short)entryIndex);
            if (!success) {
                throw new RetryException();
            }
        }
        if (this.getTopLevelShift() != 3 && this.getPreviousVersionIndex(entryIndex) != 0) {
            this.consolidateEntry(entryIndex);
        }
        return entryIndex;
    }

    int getPackedIndex(int setNumber, int virtualIndex, boolean insertAsNeeded) throws ResizeException {
        int byteIndex = 0;
        do {
            try {
                assert (setNumber >= 0 && setNumber < 8);
                if (virtualIndex >= this.getVirtualLength()) {
                    throw new ArrayIndexOutOfBoundsException(String.format("Attempting access at index %d, beyond virtualLength %d", virtualIndex, this.getVirtualLength()));
                }
                int entryPointerIndex = 0 + setNumber;
                int entryIndex = this.getRootEntry(setNumber, insertAsNeeded);
                if (entryIndex == 0) {
                    return -1;
                }
                for (int indexShift = this.getTopLevelShift(); indexShift >= 3; indexShift -= 4) {
                    boolean nextLevelIsLeaf = indexShift == 3;
                    int packedSlotIndicators = this.getPackedSlotIndicators(entryIndex);
                    int slotBitNumber = virtualIndex >>> indexShift & 0xF;
                    int slotMask = 1 << slotBitNumber;
                    int slotsBelowBitNumber = packedSlotIndicators & slotMask - 1;
                    int slotNumber = Integer.bitCount(slotsBelowBitNumber);
                    if ((packedSlotIndicators & slotMask) == 0) {
                        if (!insertAsNeeded) {
                            return -1;
                        }
                        entryIndex = this.expandEntry(entryIndex, entryPointerIndex, slotNumber, slotMask, nextLevelIsLeaf);
                    }
                    if ((entryIndex = (int)this.getIndexAtShortIndex(entryPointerIndex = entryIndex + 2 + slotNumber)) == 0) {
                        throw new RetryException();
                    }
                    if (nextLevelIsLeaf || this.getPreviousVersionIndex(entryIndex) == 0) continue;
                    this.consolidateEntry(entryIndex);
                }
                byteIndex = (entryIndex << 3) + (virtualIndex & 7);
            }
            catch (RetryException retryException) {
                // empty catch block
            }
        } while (byteIndex == 0);
        return byteIndex;
    }

    private long contextLocalGetValueAtIndex(int virtualIndex) {
        long value = 0L;
        for (int byteNum = 0; byteNum < 8; ++byteNum) {
            long byteValueAtPackedIndex;
            int packedIndex = 0;
            do {
                try {
                    packedIndex = this.getPackedIndex(byteNum, virtualIndex, false);
                    if (packedIndex < 0) {
                        return value;
                    }
                    byteValueAtPackedIndex = ((long)this.getAtByteIndex(packedIndex) & 0xFFL) << (byteNum << 3);
                }
                catch (ResizeException ex) {
                    throw new IllegalStateException("Should never encounter a resize excpetion without inserts");
                }
            } while (packedIndex == 0);
            value += byteValueAtPackedIndex;
        }
        return value;
    }

    void populateEquivalentEntriesWithZerosFromOther(AbstractPackedArrayContext other) {
        if (this.getVirtualLength() < other.getVirtualLength()) {
            throw new IllegalStateException("Cannot populate array of smaller virtrual length");
        }
        for (int i = 0; i < 8; ++i) {
            short otherEntryIndex = other.getAtShortIndex(0 + i);
            if (otherEntryIndex == 0) continue;
            int entryIndexPointer = 0 + i;
            for (i = this.getTopLevelShift(); i > other.getTopLevelShift(); i -= 4) {
                int sizeOfEntry = 3;
                int newEntryIndex = 0;
                do {
                    try {
                        newEntryIndex = this.newEntry(sizeOfEntry);
                    }
                    catch (ResizeException ex) {
                        this.resizeArray(ex.getNewSize());
                    }
                } while (newEntryIndex == 0);
                this.setAtShortIndex(entryIndexPointer, (short)newEntryIndex);
                this.setPackedSlotIndicators(newEntryIndex, (short)1);
                this.setPreviousVersionIndex(newEntryIndex, (short)0);
                entryIndexPointer = newEntryIndex + 2;
            }
            this.copyEntriesAtLevelFromOther(other, otherEntryIndex, entryIndexPointer, other.getTopLevelShift());
        }
    }

    private void copyEntriesAtLevelFromOther(AbstractPackedArrayContext other, int otherLevelEntryIndex, int levelEntryIndexPointer, int otherIndexShift) {
        boolean nextLevelIsLeaf = otherIndexShift == 3;
        int packedSlotIndicators = other.getPackedSlotIndicators(otherLevelEntryIndex);
        int numberOfSlots = Integer.bitCount(packedSlotIndicators);
        int sizeOfEntry = 2 + numberOfSlots;
        int entryIndex = 0;
        do {
            try {
                entryIndex = this.newEntry(sizeOfEntry);
            }
            catch (ResizeException ex) {
                this.resizeArray(ex.getNewSize());
            }
        } while (entryIndex == 0);
        this.setAtShortIndex(levelEntryIndexPointer, (short)entryIndex);
        this.setAtShortIndex(entryIndex + 0, (short)packedSlotIndicators);
        this.setAtShortIndex(entryIndex + 1, (short)0);
        for (int i = 0; i < numberOfSlots; ++i) {
            if (nextLevelIsLeaf) {
                int leafEntryIndex = 0;
                do {
                    try {
                        leafEntryIndex = this.newLeafEntry();
                    }
                    catch (ResizeException ex) {
                        this.resizeArray(ex.getNewSize());
                    }
                } while (leafEntryIndex == 0);
                this.setIndexAtEntrySlot(entryIndex, i, (short)leafEntryIndex);
                this.lazySetAtLongIndex(leafEntryIndex, 0L);
                continue;
            }
            short otherNextLevelEntryIndex = other.getIndexAtEntrySlot(otherLevelEntryIndex, i);
            this.copyEntriesAtLevelFromOther(other, otherNextLevelEntryIndex, entryIndex + 2 + i, otherIndexShift - 4);
        }
    }

    private int seekToPopulatedVirtualIndexStartingAtLevel(int startingVirtualIndex, int levelEntryIndex, int indexShift) throws RetryException {
        boolean nextLevelIsLeaf;
        int virtualIndex = startingVirtualIndex;
        int firstVirtualIndexPastThisLevel = (virtualIndex >>> indexShift | 0xF) + 1 << indexShift;
        boolean bl = nextLevelIsLeaf = indexShift == 3;
        do {
            int startingSlotBitNumber;
            int slotMask;
            int packedSlotIndicators;
            int slotsAtAndAboveBitNumber;
            int nextActiveSlotBitNumber;
            if ((nextActiveSlotBitNumber = Integer.numberOfTrailingZeros(slotsAtAndAboveBitNumber = (packedSlotIndicators = this.getPackedSlotIndicators(levelEntryIndex)) & ~((slotMask = 1 << (startingSlotBitNumber = virtualIndex >>> indexShift & 0xF)) - 1))) > 15) {
                int indexShiftAbove = indexShift + 4;
                virtualIndex += 1 << indexShiftAbove;
                return -(virtualIndex &= ~((1 << indexShiftAbove) - 1));
            }
            if (nextActiveSlotBitNumber != startingSlotBitNumber) {
                virtualIndex += nextActiveSlotBitNumber - startingSlotBitNumber << indexShift;
                virtualIndex &= ~((1 << indexShift) - 1);
            }
            if (nextLevelIsLeaf) {
                return virtualIndex;
            }
            int nextSlotMask = 1 << nextActiveSlotBitNumber;
            int slotsBelowNextBitNumber = packedSlotIndicators & nextSlotMask - 1;
            int nextSlotNumber = Integer.bitCount(slotsBelowNextBitNumber);
            if ((packedSlotIndicators & nextSlotMask) == 0) {
                throw new IllegalStateException("Unexpected 0 at slot index");
            }
            int entryPointerIndex = levelEntryIndex + 2 + nextSlotNumber;
            short nextLevelEntryIndex = this.getIndexAtShortIndex(entryPointerIndex);
            if (nextLevelEntryIndex == 0) {
                throw new RetryException();
            }
            if (this.getPreviousVersionIndex(nextLevelEntryIndex) != 0) {
                this.consolidateEntry(nextLevelEntryIndex);
            }
            if ((virtualIndex = this.seekToPopulatedVirtualIndexStartingAtLevel(virtualIndex, nextLevelEntryIndex, indexShift - 4)) >= 0) {
                return virtualIndex;
            }
            virtualIndex = -virtualIndex;
        } while (virtualIndex < firstVirtualIndexPastThisLevel);
        return virtualIndex;
    }

    private int findFirstPotentiallyPopulatedVirtualIndexStartingAt(int startingVirtualIndex) {
        boolean retry;
        int nextVirtrualIndex = -1;
        do {
            retry = false;
            try {
                int entryIndex = this.getRootEntry(0);
                if (entryIndex == 0) {
                    return this.getVirtualLength();
                }
                nextVirtrualIndex = this.seekToPopulatedVirtualIndexStartingAtLevel(startingVirtualIndex, entryIndex, this.getTopLevelShift());
            }
            catch (RetryException ex) {
                retry = true;
            }
        } while (retry);
        if (nextVirtrualIndex < 0 || nextVirtrualIndex >= this.getVirtualLength()) {
            return this.getVirtualLength();
        }
        return nextVirtrualIndex;
    }

    Iterable<IterationValue> nonZeroValues() {
        return new Iterable<IterationValue>(){

            @Override
            public Iterator<IterationValue> iterator() {
                return new NonZeroValuesIterator();
            }
        };
    }

    boolean isPacked() {
        return this.isPacked;
    }

    int getPhysicalLength() {
        return this.physicalLength;
    }

    int getVirtualLength() {
        return this.virtualLength;
    }

    int determineTopLevelShiftForVirtualLength(int virtualLength) {
        int sizeMagnitude = (int)Math.ceil(Math.log(virtualLength) / Math.log(2.0));
        int eightsSizeMagnitude = sizeMagnitude - 3;
        int multipleOfFourSizeMagnitude = (int)Math.ceil((double)eightsSizeMagnitude / 4.0) * 4;
        multipleOfFourSizeMagnitude = Math.max(multipleOfFourSizeMagnitude, 8);
        int topLevelShiftNeeded = multipleOfFourSizeMagnitude - 4 + 3;
        return topLevelShiftNeeded;
    }

    void setVirtualLength(int virtualLength) {
        if (!this.isPacked()) {
            throw new IllegalStateException("Should never be adjusting the virtual size of a non-packed context");
        }
        int newTopLevelShift = this.determineTopLevelShiftForVirtualLength(virtualLength);
        this.setTopLevelShift(newTopLevelShift);
        this.virtualLength = virtualLength;
    }

    int getTopLevelShift() {
        return this.topLevelShift;
    }

    private void setTopLevelShift(int topLevelShift) {
        this.topLevelShift = topLevelShift;
    }

    int getPopulatedLongLength() {
        return this.getPopulatedShortLength() + 3 >> 2;
    }

    int getPopulatedByteLength() {
        return this.getPopulatedShortLength() << 1;
    }

    private String nonLeafEntryToString(int entryIndex, int indexShift, int indentLevel) {
        String output = "";
        for (int i = 0; i < indentLevel; ++i) {
            output = output + "  ";
        }
        try {
            int packedSlotIndicators = this.getPackedSlotIndicators(entryIndex);
            output = output + String.format("slotIndiators: 0x%02x, prevVersionIndex: %3d: [ ", packedSlotIndicators, this.getPreviousVersionIndex(entryIndex));
            int numberOfslotsInEntry = Integer.bitCount(packedSlotIndicators);
            for (int i = 0; i < numberOfslotsInEntry; ++i) {
                output = output + String.format("%d", this.getIndexAtEntrySlot(entryIndex, i));
                if (i >= numberOfslotsInEntry - 1) continue;
                output = output + ", ";
            }
            output = output + String.format(" ] (indexShift = %d)\n", indexShift);
            boolean nextLevelIsLeaf = indexShift == 3;
            for (int i = 0; i < numberOfslotsInEntry; ++i) {
                short nextLevelEntryIndex = this.getIndexAtEntrySlot(entryIndex, i);
                output = nextLevelIsLeaf ? output + this.leafEntryToString(nextLevelEntryIndex, indentLevel + 4) : output + this.nonLeafEntryToString(nextLevelEntryIndex, indexShift - 4, indentLevel + 4);
            }
        }
        catch (Exception ex) {
            output = output + String.format("Exception thrown at nonLeafEnty at index %d with indexShift %d\n", entryIndex, indexShift);
        }
        return output;
    }

    private String leafEntryToString(int entryIndex, int indentLevel) {
        int i;
        String output = "";
        for (i = 0; i < indentLevel; ++i) {
            output = output + "  ";
        }
        try {
            output = output + "Leaf bytes : ";
            for (i = 56; i >= 0; i -= 8) {
                output = output + String.format("0x%02x ", this.getAtLongIndex(entryIndex) >>> i & 0xFFL);
            }
            output = output + "\n";
        }
        catch (Exception ex) {
            output = output + String.format("Exception thrown at leafEnty at index %d\n", entryIndex);
        }
        return output;
    }

    private String recordedValuesToString() {
        String output = "";
        try {
            for (IterationValue v : this.nonZeroValues()) {
                output = output + String.format("[%d] : %d\n", v.getIndex(), v.getValue());
            }
            return output;
        }
        catch (Exception ex) {
            output = output + "!!! Exception thown in value iteration...\n";
            return output;
        }
    }

    public String toString() {
        String output = "PackedArrayContext:\n";
        if (!this.isPacked()) {
            return output + "Context is unpacked:\n" + this.unpackedToString();
        }
        for (int setNumber = 0; setNumber < 8; ++setNumber) {
            try {
                int entryPointerIndex = 0 + setNumber;
                short entryIndex = this.getIndexAtShortIndex(entryPointerIndex);
                output = output + String.format("Set %d: root = %d \n", setNumber, (int)entryIndex);
                if (entryIndex == 0) continue;
                output = output + this.nonLeafEntryToString(entryIndex, this.getTopLevelShift(), 4);
                continue;
            }
            catch (Exception ex) {
                output = output + String.format("Exception thrown in set %d\n", setNumber);
            }
        }
        output = output + this.recordedValuesToString();
        return output;
    }

    private static class RetryException
    extends Exception {
        private RetryException() {
        }
    }

    class NonZeroValues
    implements Iterable<IterationValue> {
        NonZeroValues() {
        }

        @Override
        public Iterator<IterationValue> iterator() {
            return new NonZeroValuesIterator();
        }
    }

    class NonZeroValuesIterator
    implements Iterator<IterationValue> {
        int nextVirtrualIndex = 0;
        long nextValue;
        final IterationValue currentIterationValue = new IterationValue();

        private void findFirstNonZeroValueVirtualIndexStartingAt(int startingVirtualIndex) {
            if (!AbstractPackedArrayContext.this.isPacked()) {
                this.nextVirtrualIndex = startingVirtualIndex;
                while (this.nextVirtrualIndex < AbstractPackedArrayContext.this.getVirtualLength()) {
                    this.nextValue = AbstractPackedArrayContext.this.getAtUnpackedIndex(this.nextVirtrualIndex);
                    if (this.nextValue != 0L) {
                        return;
                    }
                    ++this.nextVirtrualIndex;
                }
                return;
            }
            this.nextVirtrualIndex = startingVirtualIndex;
            while (true) {
                this.nextVirtrualIndex = AbstractPackedArrayContext.this.findFirstPotentiallyPopulatedVirtualIndexStartingAt(this.nextVirtrualIndex);
                if (this.nextVirtrualIndex >= AbstractPackedArrayContext.this.getVirtualLength() || (this.nextValue = AbstractPackedArrayContext.this.contextLocalGetValueAtIndex(this.nextVirtrualIndex)) != 0L) break;
                ++this.nextVirtrualIndex;
            }
        }

        @Override
        public IterationValue next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.currentIterationValue.set(this.nextVirtrualIndex, this.nextValue);
            this.findFirstNonZeroValueVirtualIndexStartingAt(this.nextVirtrualIndex + 1);
            return this.currentIterationValue;
        }

        @Override
        public boolean hasNext() {
            return this.nextVirtrualIndex >= 0 && this.nextVirtrualIndex < AbstractPackedArrayContext.this.getVirtualLength();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        NonZeroValuesIterator() {
            this.findFirstNonZeroValueVirtualIndexStartingAt(0);
        }
    }
}

