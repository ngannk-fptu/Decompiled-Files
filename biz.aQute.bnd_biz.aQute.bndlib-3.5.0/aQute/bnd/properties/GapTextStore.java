/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.properties;

import aQute.bnd.properties.ITextStore;

public class GapTextStore
implements ITextStore {
    private final int fMinGapSize;
    private final int fMaxGapSize;
    private final float fSizeMultiplier;
    private char[] fContent = new char[0];
    private int fGapStart = 0;
    private int fGapEnd = 0;
    private int fThreshold = 0;

    public GapTextStore(int lowWatermark, int highWatermark) {
        this(highWatermark / 2, highWatermark / 2, 0.0f);
    }

    public GapTextStore() {
        this(256, 4096, 0.1f);
    }

    public GapTextStore(int minSize, int maxSize, float maxGapFactor) {
        this.fMinGapSize = minSize;
        this.fMaxGapSize = maxSize;
        this.fSizeMultiplier = 1.0f / (1.0f - maxGapFactor / 2.0f);
    }

    @Override
    public final char get(int offset) {
        if (offset < this.fGapStart) {
            return this.fContent[offset];
        }
        return this.fContent[offset + this.gapSize()];
    }

    @Override
    public final String get(int offset, int length) {
        if (this.fGapStart <= offset) {
            return new String(this.fContent, offset + this.gapSize(), length);
        }
        int end = offset + length;
        if (end <= this.fGapStart) {
            return new String(this.fContent, offset, length);
        }
        StringBuilder buf = new StringBuilder(length);
        buf.append(this.fContent, offset, this.fGapStart - offset);
        buf.append(this.fContent, this.fGapEnd, end - this.fGapStart);
        return buf.toString();
    }

    @Override
    public final int getLength() {
        return this.fContent.length - this.gapSize();
    }

    @Override
    public final void set(String text) {
        this.replace(0, this.getLength(), text);
    }

    @Override
    public final void replace(int offset, int length, String text) {
        if (text == null) {
            this.adjustGap(offset, length, 0);
        } else {
            int textLength = text.length();
            this.adjustGap(offset, length, textLength);
            if (textLength != 0) {
                text.getChars(0, textLength, this.fContent, offset);
            }
        }
    }

    private void adjustGap(int offset, int remove, int add) {
        int oldGapSize = this.gapSize();
        int newGapSize = oldGapSize - add + remove;
        boolean reuseArray = 0 <= newGapSize && newGapSize <= this.fThreshold;
        int newGapStart = offset + add;
        int newGapEnd = reuseArray ? this.moveGap(offset, remove, oldGapSize, newGapSize, newGapStart) : this.reallocate(offset, remove, oldGapSize, newGapSize, newGapStart);
        this.fGapStart = newGapStart;
        this.fGapEnd = newGapEnd;
    }

    private int moveGap(int offset, int remove, int oldGapSize, int newGapSize, int newGapStart) {
        int newGapEnd = newGapStart + newGapSize;
        if (offset < this.fGapStart) {
            int afterRemove = offset + remove;
            if (afterRemove < this.fGapStart) {
                int betweenSize = this.fGapStart - afterRemove;
                this.arrayCopy(afterRemove, this.fContent, newGapEnd, betweenSize);
            }
        } else {
            int offsetShifted = offset + oldGapSize;
            int betweenSize = offsetShifted - this.fGapEnd;
            this.arrayCopy(this.fGapEnd, this.fContent, this.fGapStart, betweenSize);
        }
        return newGapEnd;
    }

    private int reallocate(int offset, int remove, int oldGapSize, int newGapSize, int newGapStart) {
        int newLength = this.fContent.length - newGapSize;
        int newArraySize = (int)((float)newLength * this.fSizeMultiplier);
        if ((newGapSize = newArraySize - newLength) < this.fMinGapSize) {
            newGapSize = this.fMinGapSize;
            newArraySize = newLength + newGapSize;
        } else if (newGapSize > this.fMaxGapSize) {
            newGapSize = this.fMaxGapSize;
            newArraySize = newLength + newGapSize;
        }
        this.fThreshold = newGapSize * 2;
        char[] newContent = this.allocate(newArraySize);
        int newGapEnd = newGapStart + newGapSize;
        if (offset < this.fGapStart) {
            this.arrayCopy(0, newContent, 0, offset);
            int afterRemove = offset + remove;
            if (afterRemove < this.fGapStart) {
                int betweenSize = this.fGapStart - afterRemove;
                this.arrayCopy(afterRemove, newContent, newGapEnd, betweenSize);
                int restSize = this.fContent.length - this.fGapEnd;
                this.arrayCopy(this.fGapEnd, newContent, newGapEnd + betweenSize, restSize);
            } else {
                int restSize = this.fContent.length - (afterRemove += oldGapSize);
                this.arrayCopy(afterRemove, newContent, newGapEnd, restSize);
            }
        } else {
            this.arrayCopy(0, newContent, 0, this.fGapStart);
            int offsetShifted = offset + oldGapSize;
            int betweenSize = offsetShifted - this.fGapEnd;
            this.arrayCopy(this.fGapEnd, newContent, this.fGapStart, betweenSize);
            int afterRemove = offsetShifted + remove;
            int restSize = this.fContent.length - afterRemove;
            this.arrayCopy(afterRemove, newContent, newGapEnd, restSize);
        }
        this.fContent = newContent;
        return newGapEnd;
    }

    private char[] allocate(int size) {
        return new char[size];
    }

    private void arrayCopy(int srcPos, char[] dest, int destPos, int length) {
        if (length != 0) {
            System.arraycopy(this.fContent, srcPos, dest, destPos, length);
        }
    }

    private int gapSize() {
        return this.fGapEnd - this.fGapStart;
    }

    protected String getContentAsString() {
        return new String(this.fContent);
    }

    protected int getGapStartIndex() {
        return this.fGapStart;
    }

    protected int getGapEndIndex() {
        return this.fGapEnd;
    }
}

