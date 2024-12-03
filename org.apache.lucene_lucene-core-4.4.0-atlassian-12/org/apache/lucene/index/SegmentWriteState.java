/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import org.apache.lucene.index.BufferedDeletes;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.util.InfoStream;
import org.apache.lucene.util.MutableBits;

public class SegmentWriteState {
    public final InfoStream infoStream;
    public final Directory directory;
    public final SegmentInfo segmentInfo;
    public final FieldInfos fieldInfos;
    public int delCountOnFlush;
    public final BufferedDeletes segDeletes;
    public MutableBits liveDocs;
    public final String segmentSuffix;
    public int termIndexInterval;
    public final IOContext context;

    public SegmentWriteState(InfoStream infoStream, Directory directory, SegmentInfo segmentInfo, FieldInfos fieldInfos, int termIndexInterval, BufferedDeletes segDeletes, IOContext context) {
        this.infoStream = infoStream;
        this.segDeletes = segDeletes;
        this.directory = directory;
        this.segmentInfo = segmentInfo;
        this.fieldInfos = fieldInfos;
        this.termIndexInterval = termIndexInterval;
        this.segmentSuffix = "";
        this.context = context;
    }

    public SegmentWriteState(SegmentWriteState state, String segmentSuffix) {
        this.infoStream = state.infoStream;
        this.directory = state.directory;
        this.segmentInfo = state.segmentInfo;
        this.fieldInfos = state.fieldInfos;
        this.termIndexInterval = state.termIndexInterval;
        this.context = state.context;
        this.segmentSuffix = segmentSuffix;
        this.segDeletes = state.segDeletes;
        this.delCountOnFlush = state.delCountOnFlush;
    }
}

