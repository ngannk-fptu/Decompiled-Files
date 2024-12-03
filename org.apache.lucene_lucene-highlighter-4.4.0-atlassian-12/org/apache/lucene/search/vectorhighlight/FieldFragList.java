/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.vectorhighlight;

import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.search.vectorhighlight.FieldPhraseList;

public abstract class FieldFragList {
    private List<WeightedFragInfo> fragInfos = new ArrayList<WeightedFragInfo>();

    public FieldFragList(int fragCharSize) {
    }

    public abstract void add(int var1, int var2, List<FieldPhraseList.WeightedPhraseInfo> var3);

    public List<WeightedFragInfo> getFragInfos() {
        return this.fragInfos;
    }

    public static class WeightedFragInfo {
        private List<SubInfo> subInfos;
        private float totalBoost;
        private int startOffset;
        private int endOffset;

        public WeightedFragInfo(int startOffset, int endOffset, List<SubInfo> subInfos, float totalBoost) {
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.totalBoost = totalBoost;
            this.subInfos = subInfos;
        }

        public List<SubInfo> getSubInfos() {
            return this.subInfos;
        }

        public float getTotalBoost() {
            return this.totalBoost;
        }

        public int getStartOffset() {
            return this.startOffset;
        }

        public int getEndOffset() {
            return this.endOffset;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("subInfos=(");
            for (SubInfo si : this.subInfos) {
                sb.append(si.toString());
            }
            sb.append(")/").append(this.totalBoost).append('(').append(this.startOffset).append(',').append(this.endOffset).append(')');
            return sb.toString();
        }

        public static class SubInfo {
            private final String text;
            private final List<FieldPhraseList.WeightedPhraseInfo.Toffs> termsOffsets;
            private int seqnum;

            public SubInfo(String text, List<FieldPhraseList.WeightedPhraseInfo.Toffs> termsOffsets, int seqnum) {
                this.text = text;
                this.termsOffsets = termsOffsets;
                this.seqnum = seqnum;
            }

            public List<FieldPhraseList.WeightedPhraseInfo.Toffs> getTermsOffsets() {
                return this.termsOffsets;
            }

            public int getSeqnum() {
                return this.seqnum;
            }

            public String getText() {
                return this.text;
            }

            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append(this.text).append('(');
                for (FieldPhraseList.WeightedPhraseInfo.Toffs to : this.termsOffsets) {
                    sb.append(to.toString());
                }
                sb.append(')');
                return sb.toString();
            }
        }
    }
}

