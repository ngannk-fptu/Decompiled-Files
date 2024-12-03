/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.List;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexWriter;

public abstract class FilterDirectoryReader
extends DirectoryReader {
    protected final DirectoryReader in;

    public FilterDirectoryReader(DirectoryReader in) {
        this(in, new StandardReaderWrapper());
    }

    public FilterDirectoryReader(DirectoryReader in, SubReaderWrapper wrapper) {
        super(in.directory(), wrapper.wrap(in.getSequentialSubReaders()));
        this.in = in;
    }

    protected abstract DirectoryReader doWrapDirectoryReader(DirectoryReader var1);

    private final DirectoryReader wrapDirectoryReader(DirectoryReader in) {
        return in == null ? null : this.doWrapDirectoryReader(in);
    }

    @Override
    protected final DirectoryReader doOpenIfChanged() throws IOException {
        return this.wrapDirectoryReader(this.in.doOpenIfChanged());
    }

    @Override
    protected final DirectoryReader doOpenIfChanged(IndexCommit commit) throws IOException {
        return this.wrapDirectoryReader(this.in.doOpenIfChanged(commit));
    }

    @Override
    protected final DirectoryReader doOpenIfChanged(IndexWriter writer, boolean applyAllDeletes) throws IOException {
        return this.wrapDirectoryReader(this.in.doOpenIfChanged(writer, applyAllDeletes));
    }

    @Override
    public long getVersion() {
        return this.in.getVersion();
    }

    @Override
    public boolean isCurrent() throws IOException {
        return this.in.isCurrent();
    }

    @Override
    public IndexCommit getIndexCommit() throws IOException {
        return this.in.getIndexCommit();
    }

    @Override
    protected void doClose() throws IOException {
        this.in.doClose();
    }

    public static class StandardReaderWrapper
    extends SubReaderWrapper {
        @Override
        public AtomicReader wrap(AtomicReader reader) {
            return reader;
        }
    }

    public static abstract class SubReaderWrapper {
        private AtomicReader[] wrap(List<? extends AtomicReader> readers) {
            AtomicReader[] wrapped = new AtomicReader[readers.size()];
            for (int i = 0; i < readers.size(); ++i) {
                wrapped[i] = this.wrap(readers.get(i));
            }
            return wrapped;
        }

        public abstract AtomicReader wrap(AtomicReader var1);
    }
}

