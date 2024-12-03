/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DirectoryReader;
import com.atlassian.lucene36.index.IndexDeletionPolicy;
import com.atlassian.lucene36.index.IndexWriter;
import com.atlassian.lucene36.index.ReadOnlySegmentReader;
import com.atlassian.lucene36.index.SegmentInfos;
import com.atlassian.lucene36.index.SegmentReader;
import com.atlassian.lucene36.store.Directory;
import java.io.IOException;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class ReadOnlyDirectoryReader
extends DirectoryReader {
    ReadOnlyDirectoryReader(Directory directory, SegmentInfos sis, IndexDeletionPolicy deletionPolicy, int termInfosIndexDivisor) throws IOException {
        super(directory, sis, deletionPolicy, true, termInfosIndexDivisor);
    }

    ReadOnlyDirectoryReader(Directory directory, SegmentInfos infos, SegmentReader[] oldReaders, int[] oldStarts, Map<String, byte[]> oldNormsCache, boolean doClone, int termInfosIndexDivisor) throws IOException {
        super(directory, infos, oldReaders, oldStarts, oldNormsCache, true, doClone, termInfosIndexDivisor);
    }

    ReadOnlyDirectoryReader(IndexWriter writer, SegmentInfos infos, int termInfosIndexDivisor, boolean applyAllDeletes) throws IOException {
        super(writer, infos, termInfosIndexDivisor, applyAllDeletes);
    }

    @Override
    protected void acquireWriteLock() {
        ReadOnlySegmentReader.noWrite();
    }
}

