/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.codecs.lucene3x;

import java.io.IOException;
import org.apache.lucene.codecs.TermVectorsFormat;
import org.apache.lucene.codecs.TermVectorsReader;
import org.apache.lucene.codecs.TermVectorsWriter;
import org.apache.lucene.codecs.lucene3x.Lucene3xSegmentInfoFormat;
import org.apache.lucene.codecs.lucene3x.Lucene3xTermVectorsReader;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.IndexFileNames;
import org.apache.lucene.index.SegmentInfo;
import org.apache.lucene.store.CompoundFileDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;

@Deprecated
class Lucene3xTermVectorsFormat
extends TermVectorsFormat {
    Lucene3xTermVectorsFormat() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TermVectorsReader vectorsReader(Directory directory, SegmentInfo segmentInfo, FieldInfos fieldInfos, IOContext context) throws IOException {
        boolean exists;
        String fileName = IndexFileNames.segmentFileName(Lucene3xSegmentInfoFormat.getDocStoreSegment(segmentInfo), "", "tvf");
        if (Lucene3xSegmentInfoFormat.getDocStoreOffset(segmentInfo) != -1 && Lucene3xSegmentInfoFormat.getDocStoreIsCompoundFile(segmentInfo)) {
            String cfxFileName = IndexFileNames.segmentFileName(Lucene3xSegmentInfoFormat.getDocStoreSegment(segmentInfo), "", "cfx");
            if (segmentInfo.dir.fileExists(cfxFileName)) {
                try (CompoundFileDirectory cfsDir = new CompoundFileDirectory(segmentInfo.dir, cfxFileName, context, false);){
                    exists = ((Directory)cfsDir).fileExists(fileName);
                }
            } else {
                exists = false;
            }
        } else {
            exists = directory.fileExists(fileName);
        }
        if (!exists) {
            return null;
        }
        return new Lucene3xTermVectorsReader(directory, segmentInfo, fieldInfos, context);
    }

    @Override
    public TermVectorsWriter vectorsWriter(Directory directory, SegmentInfo segmentInfo, IOContext context) throws IOException {
        throw new UnsupportedOperationException("this codec can only be used for reading");
    }
}

