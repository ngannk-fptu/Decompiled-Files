/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.vectorhighlight;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.lucene.search.vectorhighlight.FieldFragList;
import org.apache.lucene.search.vectorhighlight.FieldPhraseList;
import org.apache.lucene.search.vectorhighlight.FragListBuilder;

public abstract class BaseFragListBuilder
implements FragListBuilder {
    public static final int MARGIN_DEFAULT = 6;
    public static final int MIN_FRAG_CHAR_SIZE_FACTOR = 3;
    final int margin;
    final int minFragCharSize;

    public BaseFragListBuilder(int margin) {
        if (margin < 0) {
            throw new IllegalArgumentException("margin(" + margin + ") is too small. It must be 0 or higher.");
        }
        this.margin = margin;
        this.minFragCharSize = Math.max(1, margin * 3);
    }

    public BaseFragListBuilder() {
        this(6);
    }

    protected FieldFragList createFieldFragList(FieldPhraseList fieldPhraseList, FieldFragList fieldFragList, int fragCharSize) {
        if (fragCharSize < this.minFragCharSize) {
            throw new IllegalArgumentException("fragCharSize(" + fragCharSize + ") is too small. It must be " + this.minFragCharSize + " or higher.");
        }
        ArrayList<FieldPhraseList.WeightedPhraseInfo> wpil = new ArrayList<FieldPhraseList.WeightedPhraseInfo>();
        IteratorQueue<FieldPhraseList.WeightedPhraseInfo> queue = new IteratorQueue<FieldPhraseList.WeightedPhraseInfo>(fieldPhraseList.getPhraseList().iterator());
        FieldPhraseList.WeightedPhraseInfo phraseInfo = null;
        int startOffset = 0;
        while ((phraseInfo = queue.top()) != null) {
            if (phraseInfo.getStartOffset() < startOffset) {
                queue.removeTop();
                continue;
            }
            wpil.clear();
            int currentPhraseStartOffset = phraseInfo.getStartOffset();
            int currentPhraseEndOffset = phraseInfo.getEndOffset();
            int spanStart = Math.max(currentPhraseStartOffset - this.margin, startOffset);
            int spanEnd = Math.max(currentPhraseEndOffset, spanStart + fragCharSize);
            if (this.acceptPhrase(queue.removeTop(), currentPhraseEndOffset - currentPhraseStartOffset, fragCharSize)) {
                wpil.add(phraseInfo);
            }
            while ((phraseInfo = queue.top()) != null && phraseInfo.getEndOffset() <= spanEnd) {
                currentPhraseEndOffset = phraseInfo.getEndOffset();
                if (!this.acceptPhrase(queue.removeTop(), currentPhraseEndOffset - currentPhraseStartOffset, fragCharSize)) continue;
                wpil.add(phraseInfo);
            }
            if (wpil.isEmpty()) continue;
            int matchLen = currentPhraseEndOffset - currentPhraseStartOffset;
            int newMargin = Math.max(0, (fragCharSize - matchLen) / 2);
            spanStart = currentPhraseStartOffset - newMargin;
            if (spanStart < startOffset) {
                spanStart = startOffset;
            }
            startOffset = spanEnd = spanStart + Math.max(matchLen, fragCharSize);
            fieldFragList.add(spanStart, spanEnd, wpil);
        }
        return fieldFragList;
    }

    protected boolean acceptPhrase(FieldPhraseList.WeightedPhraseInfo info, int matchLength, int fragCharSize) {
        return info.getTermsOffsets().size() <= 1 || matchLength <= fragCharSize;
    }

    private static final class IteratorQueue<T> {
        private final Iterator<T> iter;
        private T top;

        public IteratorQueue(Iterator<T> iter) {
            this.iter = iter;
            T removeTop = this.removeTop();
            assert (removeTop == null);
        }

        public T top() {
            return this.top;
        }

        public T removeTop() {
            T currentTop = this.top;
            this.top = this.iter.hasNext() ? this.iter.next() : null;
            return currentTop;
        }
    }
}

