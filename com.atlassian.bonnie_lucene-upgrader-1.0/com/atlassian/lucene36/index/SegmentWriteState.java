/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.BufferedDeletes;
import com.atlassian.lucene36.index.FieldInfos;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.util.BitVector;
import java.io.PrintStream;

public class SegmentWriteState {
    public final PrintStream infoStream;
    public final Directory directory;
    public final String segmentName;
    public final FieldInfos fieldInfos;
    public final int numDocs;
    public boolean hasVectors;
    public final BufferedDeletes segDeletes;
    public BitVector deletedDocs;
    public final int termIndexInterval;
    public final int skipInterval = 16;
    public final int maxSkipLevels = 10;

    public SegmentWriteState(PrintStream infoStream, Directory directory, String segmentName, FieldInfos fieldInfos, int numDocs, int termIndexInterval, BufferedDeletes segDeletes) {
        this.infoStream = infoStream;
        this.segDeletes = segDeletes;
        this.directory = directory;
        this.segmentName = segmentName;
        this.fieldInfos = fieldInfos;
        this.numDocs = numDocs;
        this.termIndexInterval = termIndexInterval;
    }
}

