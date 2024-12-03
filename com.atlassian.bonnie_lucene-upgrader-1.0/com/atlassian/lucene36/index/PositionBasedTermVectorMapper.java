/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.TermVectorMapper;
import com.atlassian.lucene36.index.TermVectorOffsetInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PositionBasedTermVectorMapper
extends TermVectorMapper {
    private Map<String, Map<Integer, TVPositionInfo>> fieldToTerms;
    private String currentField;
    private Map<Integer, TVPositionInfo> currentPositions;
    private boolean storeOffsets;

    public PositionBasedTermVectorMapper() {
        super(false, false);
    }

    public PositionBasedTermVectorMapper(boolean ignoringOffsets) {
        super(false, ignoringOffsets);
    }

    @Override
    public boolean isIgnoringPositions() {
        return false;
    }

    @Override
    public void map(String term, int frequency, TermVectorOffsetInfo[] offsets, int[] positions) {
        for (int i = 0; i < positions.length; ++i) {
            Integer posVal = positions[i];
            TVPositionInfo pos = this.currentPositions.get(posVal);
            if (pos == null) {
                pos = new TVPositionInfo(positions[i], this.storeOffsets);
                this.currentPositions.put(posVal, pos);
            }
            pos.addTerm(term, offsets != null ? offsets[i] : null);
        }
    }

    @Override
    public void setExpectations(String field, int numTerms, boolean storeOffsets, boolean storePositions) {
        if (!storePositions) {
            throw new RuntimeException("You must store positions in order to use this Mapper");
        }
        if (storeOffsets) {
            // empty if block
        }
        this.fieldToTerms = new HashMap<String, Map<Integer, TVPositionInfo>>(numTerms);
        this.storeOffsets = storeOffsets;
        this.currentField = field;
        this.currentPositions = new HashMap<Integer, TVPositionInfo>();
        this.fieldToTerms.put(this.currentField, this.currentPositions);
    }

    public Map<String, Map<Integer, TVPositionInfo>> getFieldToTerms() {
        return this.fieldToTerms;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class TVPositionInfo {
        private int position;
        private List<String> terms;
        private List<TermVectorOffsetInfo> offsets;

        public TVPositionInfo(int position, boolean storeOffsets) {
            this.position = position;
            this.terms = new ArrayList<String>();
            if (storeOffsets) {
                this.offsets = new ArrayList<TermVectorOffsetInfo>();
            }
        }

        void addTerm(String term, TermVectorOffsetInfo info) {
            this.terms.add(term);
            if (this.offsets != null) {
                this.offsets.add(info);
            }
        }

        public int getPosition() {
            return this.position;
        }

        public List<String> getTerms() {
            return this.terms;
        }

        public List<TermVectorOffsetInfo> getOffsets() {
            return this.offsets;
        }
    }
}

