/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.TermVectorOffsetInfo;

public class TermVectorEntry {
    private String field;
    private String term;
    private int frequency;
    private TermVectorOffsetInfo[] offsets;
    int[] positions;

    public TermVectorEntry() {
    }

    public TermVectorEntry(String field, String term, int frequency, TermVectorOffsetInfo[] offsets, int[] positions) {
        this.field = field;
        this.term = term;
        this.frequency = frequency;
        this.offsets = offsets;
        this.positions = positions;
    }

    public String getField() {
        return this.field;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public TermVectorOffsetInfo[] getOffsets() {
        return this.offsets;
    }

    public int[] getPositions() {
        return this.positions;
    }

    public String getTerm() {
        return this.term;
    }

    void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    void setOffsets(TermVectorOffsetInfo[] offsets) {
        this.offsets = offsets;
    }

    void setPositions(int[] positions) {
        this.positions = positions;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TermVectorEntry that = (TermVectorEntry)o;
        return !(this.term != null ? !this.term.equals(that.term) : that.term != null);
    }

    public int hashCode() {
        return this.term != null ? this.term.hashCode() : 0;
    }

    public String toString() {
        return "TermVectorEntry{field='" + this.field + '\'' + ", term='" + this.term + '\'' + ", frequency=" + this.frequency + '}';
    }
}

