/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.vectorhighlight;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.search.vectorhighlight.FieldTermStack;

public class FieldPhraseList {
    LinkedList<WeightedPhraseInfo> phraseList = new LinkedList();

    public FieldPhraseList(FieldTermStack fieldTermStack, FieldQuery fieldQuery) {
        this(fieldTermStack, fieldQuery, Integer.MAX_VALUE);
    }

    public List<WeightedPhraseInfo> getPhraseList() {
        return this.phraseList;
    }

    public FieldPhraseList(FieldTermStack fieldTermStack, FieldQuery fieldQuery, int phraseLimit) {
        String field = fieldTermStack.getFieldName();
        LinkedList<FieldTermStack.TermInfo> phraseCandidate = new LinkedList<FieldTermStack.TermInfo>();
        FieldQuery.QueryPhraseMap currMap = null;
        FieldQuery.QueryPhraseMap nextMap = null;
        block0: while (!fieldTermStack.isEmpty() && this.phraseList.size() < phraseLimit) {
            phraseCandidate.clear();
            FieldTermStack.TermInfo ti = fieldTermStack.pop();
            currMap = fieldQuery.getFieldTermMap(field, ti.getText());
            if (currMap == null) continue;
            phraseCandidate.add(ti);
            while (true) {
                ti = fieldTermStack.pop();
                nextMap = null;
                if (ti != null) {
                    nextMap = currMap.getTermMap(ti.getText());
                }
                if (ti == null || nextMap == null) {
                    if (ti != null) {
                        fieldTermStack.push(ti);
                    }
                    if (currMap.isValidTermOrPhrase(phraseCandidate)) {
                        this.addIfNoOverlap(new WeightedPhraseInfo(phraseCandidate, currMap.getBoost(), currMap.getTermOrPhraseNumber()));
                        continue block0;
                    }
                    while (phraseCandidate.size() > 1) {
                        fieldTermStack.push(phraseCandidate.removeLast());
                        currMap = fieldQuery.searchPhrase(field, phraseCandidate);
                        if (currMap == null) continue;
                        this.addIfNoOverlap(new WeightedPhraseInfo(phraseCandidate, currMap.getBoost(), currMap.getTermOrPhraseNumber()));
                        continue block0;
                    }
                    continue block0;
                }
                phraseCandidate.add(ti);
                currMap = nextMap;
            }
        }
    }

    public void addIfNoOverlap(WeightedPhraseInfo wpi) {
        for (WeightedPhraseInfo existWpi : this.getPhraseList()) {
            if (!existWpi.isOffsetOverlap(wpi)) continue;
            existWpi.getTermsInfos().addAll(wpi.getTermsInfos());
            return;
        }
        this.getPhraseList().add(wpi);
    }

    public static class WeightedPhraseInfo {
        private String text;
        private List<Toffs> termsOffsets;
        private float boost;
        private int seqnum;
        private ArrayList<FieldTermStack.TermInfo> termsInfos;

        public String getText() {
            return this.text;
        }

        public List<Toffs> getTermsOffsets() {
            return this.termsOffsets;
        }

        public float getBoost() {
            return this.boost;
        }

        public List<FieldTermStack.TermInfo> getTermsInfos() {
            return this.termsInfos;
        }

        public WeightedPhraseInfo(LinkedList<FieldTermStack.TermInfo> terms, float boost) {
            this(terms, boost, 0);
        }

        public WeightedPhraseInfo(LinkedList<FieldTermStack.TermInfo> terms, float boost, int seqnum) {
            this.boost = boost;
            this.seqnum = seqnum;
            this.termsInfos = new ArrayList<FieldTermStack.TermInfo>(terms);
            this.termsOffsets = new ArrayList<Toffs>(terms.size());
            FieldTermStack.TermInfo ti = terms.get(0);
            this.termsOffsets.add(new Toffs(ti.getStartOffset(), ti.getEndOffset()));
            if (terms.size() == 1) {
                this.text = ti.getText();
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(ti.getText());
            int pos = ti.getPosition();
            for (int i = 1; i < terms.size(); ++i) {
                ti = terms.get(i);
                sb.append(ti.getText());
                if (ti.getPosition() - pos == 1) {
                    Toffs to = this.termsOffsets.get(this.termsOffsets.size() - 1);
                    to.setEndOffset(ti.getEndOffset());
                } else {
                    this.termsOffsets.add(new Toffs(ti.getStartOffset(), ti.getEndOffset()));
                }
                pos = ti.getPosition();
            }
            this.text = sb.toString();
        }

        public int getStartOffset() {
            return this.termsOffsets.get(0).startOffset;
        }

        public int getEndOffset() {
            return this.termsOffsets.get(this.termsOffsets.size() - 1).endOffset;
        }

        public boolean isOffsetOverlap(WeightedPhraseInfo other) {
            int so = this.getStartOffset();
            int eo = this.getEndOffset();
            int oso = other.getStartOffset();
            int oeo = other.getEndOffset();
            if (so <= oso && oso < eo) {
                return true;
            }
            if (so < oeo && oeo <= eo) {
                return true;
            }
            if (oso <= so && so < oeo) {
                return true;
            }
            return oso < eo && eo <= oeo;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.text).append('(').append(this.boost).append(")(");
            for (Toffs to : this.termsOffsets) {
                sb.append(to);
            }
            sb.append(')');
            return sb.toString();
        }

        public int getSeqnum() {
            return this.seqnum;
        }

        public static class Toffs {
            private int startOffset;
            private int endOffset;

            public Toffs(int startOffset, int endOffset) {
                this.startOffset = startOffset;
                this.endOffset = endOffset;
            }

            public void setEndOffset(int endOffset) {
                this.endOffset = endOffset;
            }

            public int getStartOffset() {
                return this.startOffset;
            }

            public int getEndOffset() {
                return this.endOffset;
            }

            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append('(').append(this.startOffset).append(',').append(this.endOffset).append(')');
                return sb.toString();
            }
        }
    }
}

