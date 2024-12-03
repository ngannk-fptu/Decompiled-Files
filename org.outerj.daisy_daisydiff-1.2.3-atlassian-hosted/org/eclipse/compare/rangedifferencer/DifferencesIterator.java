/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.compare.rangedifferencer;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.compare.rangedifferencer.RangeDifference;

class DifferencesIterator {
    List fRange;
    int fIndex;
    RangeDifference[] fArray;
    RangeDifference fDifference;

    DifferencesIterator(RangeDifference[] differenceRanges) {
        this.fArray = differenceRanges;
        this.fIndex = 0;
        this.fRange = new ArrayList();
        this.fDifference = this.fIndex < this.fArray.length ? this.fArray[this.fIndex++] : null;
    }

    int getCount() {
        return this.fRange.size();
    }

    void next() {
        this.fRange.add(this.fDifference);
        if (this.fDifference != null) {
            this.fDifference = this.fIndex < this.fArray.length ? this.fArray[this.fIndex++] : null;
        }
    }

    DifferencesIterator other(DifferencesIterator right, DifferencesIterator left) {
        if (this == right) {
            return left;
        }
        return right;
    }

    void removeAll() {
        this.fRange.clear();
    }
}

