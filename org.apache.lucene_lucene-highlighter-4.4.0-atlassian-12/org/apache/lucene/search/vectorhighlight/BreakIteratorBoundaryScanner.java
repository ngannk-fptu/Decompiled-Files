/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.vectorhighlight;

import java.text.BreakIterator;
import org.apache.lucene.search.vectorhighlight.BoundaryScanner;

public class BreakIteratorBoundaryScanner
implements BoundaryScanner {
    final BreakIterator bi;

    public BreakIteratorBoundaryScanner(BreakIterator bi) {
        this.bi = bi;
    }

    @Override
    public int findStartOffset(StringBuilder buffer, int start) {
        if (start > buffer.length() || start < 1) {
            return start;
        }
        this.bi.setText(buffer.substring(0, start));
        this.bi.last();
        return this.bi.previous();
    }

    @Override
    public int findEndOffset(StringBuilder buffer, int start) {
        if (start > buffer.length() || start < 0) {
            return start;
        }
        this.bi.setText(buffer.substring(start));
        return this.bi.next() + start;
    }
}

