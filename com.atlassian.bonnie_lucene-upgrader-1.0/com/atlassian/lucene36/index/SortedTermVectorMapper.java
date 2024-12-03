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
public class SortedTermVectorMapper
extends TermVectorMapper {
    private SortedSet<TermVectorEntry> currentSet;
    private Map<String, TermVectorEntry> termToTVE = new HashMap<String, TermVectorEntry>();
    private boolean storeOffsets;
    private boolean storePositions;
    public static final String ALL = "_ALL_";

    public SortedTermVectorMapper(Comparator<TermVectorEntry> comparator) {
        this(false, false, comparator);
    }

    public SortedTermVectorMapper(boolean ignoringPositions, boolean ignoringOffsets, Comparator<TermVectorEntry> comparator) {
        super(ignoringPositions, ignoringOffsets);
        this.currentSet = new TreeSet<TermVectorEntry>(comparator);
    }

    @Override
    public void map(String term, int frequency, TermVectorOffsetInfo[] offsets, int[] positions) {
        TermVectorEntry entry = this.termToTVE.get(term);
        if (entry == null) {
            entry = new TermVectorEntry(ALL, term, frequency, (TermVectorOffsetInfo[])(this.storeOffsets ? offsets : null), (int[])(this.storePositions ? positions : null));
            this.termToTVE.put(term, entry);
            this.currentSet.add(entry);
        } else {
            entry.setFrequency(entry.getFrequency() + frequency);
            if (this.storeOffsets) {
                TermVectorOffsetInfo[] existingOffsets = entry.getOffsets();
                if (existingOffsets != null && offsets != null && offsets.length > 0) {
                    TermVectorOffsetInfo[] newOffsets = new TermVectorOffsetInfo[existingOffsets.length + offsets.length];
                    System.arraycopy(existingOffsets, 0, newOffsets, 0, existingOffsets.length);
                    System.arraycopy(offsets, 0, newOffsets, existingOffsets.length, offsets.length);
                    entry.setOffsets(newOffsets);
                } else if (existingOffsets == null && offsets != null && offsets.length > 0) {
                    entry.setOffsets(offsets);
                }
            }
            if (this.storePositions) {
                int[] existingPositions = entry.getPositions();
                if (existingPositions != null && positions != null && positions.length > 0) {
                    int[] newPositions = new int[existingPositions.length + positions.length];
                    System.arraycopy(existingPositions, 0, newPositions, 0, existingPositions.length);
                    System.arraycopy(positions, 0, newPositions, existingPositions.length, positions.length);
                    entry.setPositions(newPositions);
                } else if (existingPositions == null && positions != null && positions.length > 0) {
                    entry.setPositions(positions);
                }
            }
        }
    }

    @Override
    public void setExpectations(String field, int numTerms, boolean storeOffsets, boolean storePositions) {
        this.storeOffsets = storeOffsets;
        this.storePositions = storePositions;
    }

    public SortedSet<TermVectorEntry> getTermVectorEntrySet() {
        return this.currentSet;
    }
}

