/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.compare.rangedifferencer;

public class RangeDifference {
    public static final int NOCHANGE = 0;
    public static final int CHANGE = 2;
    public static final int CONFLICT = 1;
    public static final int RIGHT = 2;
    public static final int LEFT = 3;
    public static final int ANCESTOR = 4;
    public static final int ERROR = 5;
    int fKind;
    int fLeftStart;
    int fLeftLength;
    int fRightStart;
    int fRightLength;
    int lAncestorStart;
    int lAncestorLength;

    RangeDifference(int changeKind) {
        this.fKind = changeKind;
    }

    public RangeDifference(int kind, int rightStart, int rightLength, int leftStart, int leftLength) {
        this.fKind = kind;
        this.fRightStart = rightStart;
        this.fRightLength = rightLength;
        this.fLeftStart = leftStart;
        this.fLeftLength = leftLength;
    }

    RangeDifference(int kind, int rightStart, int rightLength, int leftStart, int leftLength, int ancestorStart, int ancestorLength) {
        this(kind, rightStart, rightLength, leftStart, leftLength);
        this.lAncestorStart = ancestorStart;
        this.lAncestorLength = ancestorLength;
    }

    public int kind() {
        return this.fKind;
    }

    public int ancestorStart() {
        return this.lAncestorStart;
    }

    public int ancestorLength() {
        return this.lAncestorLength;
    }

    public int ancestorEnd() {
        return this.lAncestorStart + this.lAncestorLength;
    }

    public int rightStart() {
        return this.fRightStart;
    }

    public int rightLength() {
        return this.fRightLength;
    }

    public int rightEnd() {
        return this.fRightStart + this.fRightLength;
    }

    public int leftStart() {
        return this.fLeftStart;
    }

    public int leftLength() {
        return this.fLeftLength;
    }

    public int leftEnd() {
        return this.fLeftStart + this.fLeftLength;
    }

    public int maxLength() {
        return Math.max(this.fRightLength, Math.max(this.fLeftLength, this.lAncestorLength));
    }

    public boolean equals(Object obj) {
        if (obj instanceof RangeDifference) {
            RangeDifference other = (RangeDifference)obj;
            return this.fKind == other.fKind && this.fLeftStart == other.fLeftStart && this.fLeftLength == other.fLeftLength && this.fRightStart == other.fRightStart && this.fRightLength == other.fRightLength && this.lAncestorStart == other.lAncestorStart && this.lAncestorLength == other.lAncestorLength;
        }
        return super.equals(obj);
    }

    public String toString() {
        String string = "Left: " + this.toRangeString(this.fLeftStart, this.fLeftLength) + " Right: " + this.toRangeString(this.fRightStart, this.fRightLength);
        if (this.lAncestorLength > 0 || this.lAncestorStart > 0) {
            string = string + " Ancestor: " + this.toRangeString(this.lAncestorStart, this.lAncestorLength);
        }
        return string;
    }

    private String toRangeString(int start, int length) {
        return "(" + start + ", " + length + ")";
    }
}

