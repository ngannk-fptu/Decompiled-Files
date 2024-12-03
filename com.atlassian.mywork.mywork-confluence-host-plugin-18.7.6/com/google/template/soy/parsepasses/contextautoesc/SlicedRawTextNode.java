/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package com.google.template.soy.parsepasses.contextautoesc;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.template.soy.parsepasses.contextautoesc.Context;
import com.google.template.soy.parsepasses.contextautoesc.RawTextContextUpdater;
import com.google.template.soy.parsepasses.contextautoesc.SoyAutoescapeException;
import com.google.template.soy.soytree.RawTextNode;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public final class SlicedRawTextNode {
    private RawTextNode rawTextNode;
    private final Context startContext;
    private Context endContext;
    private final List<RawTextSlice> slices = Lists.newArrayList();

    public SlicedRawTextNode(RawTextNode rawTextNode, Context startContext) {
        this.rawTextNode = rawTextNode;
        this.startContext = startContext;
    }

    public RawTextNode getRawTextNode() {
        return this.rawTextNode;
    }

    public List<RawTextSlice> getSlices() {
        return Collections.unmodifiableList(this.slices);
    }

    public Context getStartContext() {
        return this.startContext;
    }

    public Context getEndContext() {
        return this.endContext;
    }

    void setEndContext(Context endContext) {
        this.endContext = endContext;
    }

    void addSlice(int startOffset, int endOffset, Context context) {
        RawTextSlice last;
        int lastSliceIndex = this.slices.size() - 1;
        if (lastSliceIndex >= 0 && (last = this.slices.get(lastSliceIndex)).endOffset == startOffset && context.equals(last.context)) {
            this.slices.remove(lastSliceIndex);
            startOffset = last.startOffset;
        }
        this.slices.add(new RawTextSlice(context, this, startOffset, endOffset));
    }

    void replaceNode(RawTextNode replacement) {
        this.rawTextNode.getParent().replaceChild(this.rawTextNode, replacement);
        this.rawTextNode = replacement;
    }

    RawTextSlice insertSlice(int index, Context context, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length " + length + " < 0");
        }
        int startOffset = index == 0 ? 0 : this.slices.get(index - 1).endOffset;
        for (RawTextSlice follower : this.slices.subList(index, this.slices.size())) {
            follower.shiftOffsets(length);
        }
        RawTextSlice slice = new RawTextSlice(context, this, startOffset, startOffset + length);
        this.slices.add(index, slice);
        return slice;
    }

    @VisibleForTesting
    void mergeAdjacentSlicesWithSameContext() {
        int nMerged = 0;
        int i = 0;
        int n = this.slices.size();
        while (i < n) {
            int next;
            RawTextSlice slice = this.slices.get(i);
            for (next = i + 1; next < n && slice.context.equals(this.slices.get((int)next).context); ++next) {
            }
            RawTextSlice merged = next - i == 1 ? slice : new RawTextSlice(slice.context, this, slice.startOffset, this.slices.get(next - 1).endOffset);
            this.slices.set(nMerged, merged);
            i = next;
            ++nMerged;
        }
        this.slices.subList(nMerged, this.slices.size()).clear();
    }

    public static List<RawTextSlice> find(Iterable<? extends SlicedRawTextNode> slicedTextNodes, @Nullable Predicate<? super Context> prevContextPredicate, @Nullable Predicate<? super Context> sliceContextPredicate, @Nullable Predicate<? super Context> nextContextPredicate) {
        if (prevContextPredicate == null) {
            prevContextPredicate = Predicates.alwaysTrue();
        }
        if (sliceContextPredicate == null) {
            sliceContextPredicate = Predicates.alwaysTrue();
        }
        if (nextContextPredicate == null) {
            nextContextPredicate = Predicates.alwaysTrue();
        }
        ImmutableList.Builder matches = ImmutableList.builder();
        for (SlicedRawTextNode slicedRawTextNode : slicedTextNodes) {
            slicedRawTextNode.mergeAdjacentSlicesWithSameContext();
            Context prevContext = slicedRawTextNode.startContext;
            List<RawTextSlice> slices = slicedRawTextNode.slices;
            int n = slices.size();
            for (int i = 0; i < n; ++i) {
                RawTextSlice current = slices.get(i);
                Context nextContext = i + 1 < n ? slices.get((int)(i + 1)).context : slicedRawTextNode.endContext;
                if (prevContextPredicate.apply((Object)prevContext) && sliceContextPredicate.apply((Object)current.context) && nextContextPredicate.apply((Object)nextContext)) {
                    matches.add((Object)current);
                }
                prevContext = current.context;
            }
        }
        return matches.build();
    }

    public static final class RawTextSlice {
        public final Context context;
        public final SlicedRawTextNode slicedRawTextNode;
        private int startOffset;
        private int endOffset;

        RawTextSlice(Context context, SlicedRawTextNode slicedRawTextNode, int startOffset, int endOffset) {
            this.context = context;
            this.slicedRawTextNode = slicedRawTextNode;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
        }

        public int getStartOffset() {
            return this.startOffset;
        }

        public int getEndOffset() {
            return this.endOffset;
        }

        public int getLength() {
            return this.endOffset - this.startOffset;
        }

        private RawTextSlice split(int offset) {
            int wholeTextOffset;
            int indexInParent = this.slicedRawTextNode.slices.indexOf(this);
            if (indexInParent < 0) {
                throw new AssertionError((Object)"slice is not in its parent");
            }
            Preconditions.checkElementIndex((int)offset, (int)this.getLength(), (String)"slice offset");
            RawTextSlice secondSlice = this.slicedRawTextNode.insertSlice(indexInParent + 1, this.context, 0);
            secondSlice.startOffset = wholeTextOffset = offset + this.startOffset;
            this.endOffset = wholeTextOffset;
            return secondSlice;
        }

        public void insertText(int offset, String text) throws SoyAutoescapeException {
            int indexInParent = this.slicedRawTextNode.slices.indexOf(this);
            if (indexInParent < 0) {
                throw new AssertionError((Object)"slice is not in its parent");
            }
            Preconditions.checkElementIndex((int)offset, (int)this.getLength(), (String)"slice offset");
            int insertionIndex = -1;
            int insertionOffset = -1;
            if (offset == 0) {
                insertionIndex = indexInParent;
                insertionOffset = this.startOffset;
            } else if (offset == this.getLength()) {
                insertionIndex = indexInParent;
                insertionOffset = this.endOffset;
            } else {
                this.split(offset).insertText(0, text);
                return;
            }
            RawTextNode rawTextNode = this.slicedRawTextNode.getRawTextNode();
            String originalText = rawTextNode.getRawText();
            String replacementText = originalText.substring(0, insertionOffset) + text + originalText.substring(insertionOffset);
            RawTextNode replacementNode = new RawTextNode(rawTextNode.getId(), replacementText);
            replacementNode.setSourceLocation(rawTextNode.getSourceLocation());
            Context startContext = this.slicedRawTextNode.startContext;
            Context expectedEndContext = this.slicedRawTextNode.endContext;
            SlicedRawTextNode retyped = RawTextContextUpdater.processRawText(replacementNode, startContext);
            Context actualEndContext = retyped.getEndContext();
            if (!expectedEndContext.equals(actualEndContext)) {
                throw SoyAutoescapeException.createWithNode("Inserting `" + text + "` would cause text node to end in context " + actualEndContext + " instead of " + expectedEndContext, rawTextNode);
            }
            this.slicedRawTextNode.replaceNode(replacementNode);
            int insertionEndOffset = insertionOffset + text.length();
            for (RawTextSlice slice : retyped.slices) {
                if (slice.endOffset <= insertionOffset) continue;
                if (slice.startOffset >= insertionEndOffset) break;
                int length = Math.min(insertionEndOffset, slice.endOffset) - Math.max(insertionOffset, slice.startOffset);
                this.slicedRawTextNode.insertSlice(insertionIndex, slice.context, length);
                ++insertionIndex;
            }
        }

        public String getRawText() {
            return this.slicedRawTextNode.rawTextNode.getRawText().substring(this.startOffset, this.endOffset);
        }

        void shiftOffsets(int delta) {
            this.startOffset += delta;
            this.endOffset += delta;
        }

        public String toString() {
            String rawText = this.getRawText();
            int id = this.slicedRawTextNode.rawTextNode.getId();
            return "\"" + rawText.replaceAll("\"|\\\\", "\\\\$0") + "\"#" + id;
        }
    }
}

