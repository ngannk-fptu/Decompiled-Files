/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.BaseCompositeReader;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.SegmentInfos;
import org.apache.lucene.index.StandardDirectoryReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NoSuchDirectoryException;

public abstract class DirectoryReader
extends BaseCompositeReader<AtomicReader> {
    public static final int DEFAULT_TERMS_INDEX_DIVISOR = 1;
    protected final Directory directory;

    public static DirectoryReader open(Directory directory) throws IOException {
        return StandardDirectoryReader.open(directory, null, 1);
    }

    public static DirectoryReader open(Directory directory, int termInfosIndexDivisor) throws IOException {
        return StandardDirectoryReader.open(directory, null, termInfosIndexDivisor);
    }

    public static DirectoryReader open(IndexWriter writer, boolean applyAllDeletes) throws IOException {
        return writer.getReader(applyAllDeletes);
    }

    public static DirectoryReader open(IndexCommit commit) throws IOException {
        return StandardDirectoryReader.open(commit.getDirectory(), commit, 1);
    }

    public static DirectoryReader open(IndexCommit commit, int termInfosIndexDivisor) throws IOException {
        return StandardDirectoryReader.open(commit.getDirectory(), commit, termInfosIndexDivisor);
    }

    public static DirectoryReader openIfChanged(DirectoryReader oldReader) throws IOException {
        DirectoryReader newReader = oldReader.doOpenIfChanged();
        assert (newReader != oldReader);
        return newReader;
    }

    public static DirectoryReader openIfChanged(DirectoryReader oldReader, IndexCommit commit) throws IOException {
        DirectoryReader newReader = oldReader.doOpenIfChanged(commit);
        assert (newReader != oldReader);
        return newReader;
    }

    public static DirectoryReader openIfChanged(DirectoryReader oldReader, IndexWriter writer, boolean applyAllDeletes) throws IOException {
        DirectoryReader newReader = oldReader.doOpenIfChanged(writer, applyAllDeletes);
        assert (newReader != oldReader);
        return newReader;
    }

    public static List<IndexCommit> listCommits(Directory dir) throws IOException {
        String[] files = dir.listAll();
        ArrayList<IndexCommit> commits = new ArrayList<IndexCommit>();
        SegmentInfos latest = new SegmentInfos();
        latest.read(dir);
        long currentGen = latest.getGeneration();
        commits.add(new StandardDirectoryReader.ReaderCommit(latest, dir));
        for (int i = 0; i < files.length; ++i) {
            String fileName = files[i];
            if (!fileName.startsWith("segments") || fileName.equals("segments.gen") || SegmentInfos.generationFromSegmentsFileName(fileName) >= currentGen) continue;
            SegmentInfos sis = new SegmentInfos();
            try {
                sis.read(dir, fileName);
            }
            catch (FileNotFoundException fnfe) {
                sis = null;
            }
            if (sis == null) continue;
            commits.add(new StandardDirectoryReader.ReaderCommit(sis, dir));
        }
        Collections.sort(commits);
        return commits;
    }

    public static boolean indexExists(Directory directory) throws IOException {
        String[] files;
        try {
            files = directory.listAll();
        }
        catch (NoSuchDirectoryException nsde) {
            return false;
        }
        if (files != null) {
            String prefix = "segments_";
            for (String file : files) {
                if (!file.startsWith(prefix) && !file.equals("segments.gen")) continue;
                return true;
            }
        }
        return false;
    }

    protected DirectoryReader(Directory directory, AtomicReader[] segmentReaders) {
        super((IndexReader[])segmentReaders);
        this.directory = directory;
    }

    public final Directory directory() {
        return this.directory;
    }

    protected abstract DirectoryReader doOpenIfChanged() throws IOException;

    protected abstract DirectoryReader doOpenIfChanged(IndexCommit var1) throws IOException;

    protected abstract DirectoryReader doOpenIfChanged(IndexWriter var1, boolean var2) throws IOException;

    public abstract long getVersion();

    public abstract boolean isCurrent() throws IOException;

    public abstract IndexCommit getIndexCommit() throws IOException;
}

