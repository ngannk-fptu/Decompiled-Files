/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.TermVectorEntry;
import com.atlassian.lucene36.index.TermVectorMapper;
import com.atlassian.lucene36.index.TermVectorOffsetInfo;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FieldSortedTermVectorMapper
extends TermVectorMapper {
    private Map<String, SortedSet<TermVectorEntry>> fieldToTerms = new HashMap<String, SortedSet<TermVectorEntry>>();
    private SortedSet<TermVectorEntry> currentSet;
    private String currentField;
    private Comparator<TermVectorEntry> comparator;

    public FieldSortedTermVectorMapper(Comparator<TermVectorEntry> comparator) {
        this(false, false, comparator);
    }

    public FieldSortedTermVectorMapper(boolean ignoringPositions, boolean ignoringOffsets, Comparator<TermVectorEntry> comparator) {
        super(ignoringPositions, ignoringOffsets);
        this.comparator = comparator;
    }

    @Override
    public void map(String term, int frequency, TermVectorOffsetInfo[] offsets, int[] positions) {
        TermVectorEntry entry = new TermVectorEntry(this.currentField, term, frequency, offsets, positions);
        this.currentSet.add(entry);
    }

    @Override
    public void setExpectations(String field, int numTerms, boolean storeOffsets, boolean storePositions) {
        this.currentSet = new TreeSet<TermVectorEntry>(this.comparator);
        this.currentField = field;
        this.fieldToTerms.put(field, this.currentSet);
    }

    public Map<String, SortedSet<TermVectorEntry>> getFieldToTerms() {
        return this.fieldToTerms;
    }

    public Comparator<TermVectorEntry> getComparator() {
        return this.comparator;
    }
}

