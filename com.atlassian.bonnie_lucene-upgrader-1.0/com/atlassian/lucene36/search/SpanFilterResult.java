/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.DocIdSet;
import java.util.ArrayList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SpanFilterResult {
    private DocIdSet docIdSet;
    private List<PositionInfo> positions;

    public SpanFilterResult(DocIdSet docIdSet, List<PositionInfo> positions) {
        this.docIdSet = docIdSet;
        this.positions = positions;
    }

    public List<PositionInfo> getPositions() {
        return this.positions;
    }

    public DocIdSet getDocIdSet() {
        return this.docIdSet;
    }

    public static class StartEnd {
        private int start;
        private int end;

        public StartEnd(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getEnd() {
            return this.end;
        }

        public int getStart() {
            return this.start;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class PositionInfo {
        private int doc;
        private List<StartEnd> positions;

        public PositionInfo(int doc) {
            this.doc = doc;
            this.positions = new ArrayList<StartEnd>();
        }

        public void addPosition(int start, int end) {
            this.positions.add(new StartEnd(start, end));
        }

        public int getDoc() {
            return this.doc;
        }

        public List<StartEnd> getPositions() {
            return this.positions;
        }
    }
}

