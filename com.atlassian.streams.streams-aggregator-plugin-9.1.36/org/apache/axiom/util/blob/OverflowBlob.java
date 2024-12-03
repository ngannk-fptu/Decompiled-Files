/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.util.blob;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.axiom.ext.io.StreamCopyException;
import org.apache.axiom.util.blob.BlobOutputStream;
import org.apache.axiom.util.blob.WritableBlob;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OverflowBlob
implements WritableBlob {
    private static final Log log = LogFactory.getLog(OverflowBlob.class);
    static final int STATE_NEW = 0;
    static final int STATE_UNCOMMITTED = 1;
    static final int STATE_COMMITTED = 2;
    final int chunkSize;
    final String tempPrefix;
    final String tempSuffix;
    byte[][] chunks;
    int chunkIndex;
    int chunkOffset;
    File temporaryFile;
    int state = 0;

    public OverflowBlob(int numberOfChunks, int chunkSize, String tempPrefix, String tempSuffix) {
        this.chunkSize = chunkSize;
        this.tempPrefix = tempPrefix;
        this.tempSuffix = tempSuffix;
        this.chunks = new byte[numberOfChunks][];
    }

    public boolean isSupportingReadUncommitted() {
        return false;
    }

    byte[] getCurrentChunk() {
        if (this.chunkOffset == 0) {
            byte[] chunk = new byte[this.chunkSize];
            this.chunks[this.chunkIndex] = chunk;
            return chunk;
        }
        return this.chunks[this.chunkIndex];
    }

    FileOutputStream switchToTempFile() throws IOException {
        this.temporaryFile = File.createTempFile(this.tempPrefix, this.tempSuffix);
        if (log.isDebugEnabled()) {
            log.debug((Object)("Using temporary file " + this.temporaryFile));
        }
        this.temporaryFile.deleteOnExit();
        FileOutputStream fileOutputStream = new FileOutputStream(this.temporaryFile);
        for (int i = 0; i < this.chunkIndex; ++i) {
            fileOutputStream.write(this.chunks[i]);
        }
        if (this.chunkOffset > 0) {
            fileOutputStream.write(this.chunks[this.chunkIndex], 0, this.chunkOffset);
        }
        this.chunks = null;
        return fileOutputStream;
    }

    public BlobOutputStream getOutputStream() {
        if (this.state != 0) {
            throw new IllegalStateException();
        }
        this.state = 1;
        return new OutputStreamImpl();
    }

    public long readFrom(InputStream in, long length, boolean commit) throws StreamCopyException {
        long read;
        block13: {
            FileOutputStream fileOutputStream;
            read = 0L;
            long toRead = length == -1L ? Long.MAX_VALUE : length;
            while (true) {
                int c;
                try {
                    int len = this.chunkSize - this.chunkOffset;
                    if ((long)len > toRead) {
                        len = (int)toRead;
                    }
                    c = in.read(this.getCurrentChunk(), this.chunkOffset, len);
                }
                catch (IOException ex) {
                    throw new StreamCopyException(1, ex);
                }
                if (c == -1) break block13;
                read += (long)c;
                toRead -= (long)c;
                this.chunkOffset += c;
                if (this.chunkOffset != this.chunkSize) continue;
                ++this.chunkIndex;
                this.chunkOffset = 0;
                if (this.chunkIndex == this.chunks.length) break;
            }
            try {
                fileOutputStream = this.switchToTempFile();
            }
            catch (IOException ex) {
                throw new StreamCopyException(2, ex);
            }
            byte[] buf = new byte[4096];
            while (true) {
                int c2;
                try {
                    c2 = in.read(buf, 0, (int)Math.min(toRead, 4096L));
                }
                catch (IOException ex) {
                    throw new StreamCopyException(1, ex);
                }
                if (c2 == -1) break;
                try {
                    fileOutputStream.write(buf, 0, c2);
                }
                catch (IOException ex) {
                    throw new StreamCopyException(2, ex);
                }
                read += (long)c2;
                toRead -= (long)c2;
            }
            try {
                fileOutputStream.close();
            }
            catch (IOException ex) {
                throw new StreamCopyException(2, ex);
            }
        }
        this.state = commit ? 2 : 1;
        return read;
    }

    public long readFrom(InputStream in, long length) throws StreamCopyException {
        return this.readFrom(in, length, this.state == 0);
    }

    public InputStream getInputStream() throws IOException {
        if (this.state != 2) {
            throw new IllegalStateException();
        }
        if (this.temporaryFile != null) {
            return new FileInputStream(this.temporaryFile);
        }
        return new InputStreamImpl();
    }

    /*
     * Exception decompiling
     */
    public void writeTo(OutputStream out) throws StreamCopyException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 15[UNCONDITIONALDOLOOP]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public long getLength() {
        if (this.temporaryFile != null) {
            return this.temporaryFile.length();
        }
        return this.chunkIndex * this.chunkSize + this.chunkOffset;
    }

    public void release() {
        if (this.temporaryFile != null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Deleting temporary file " + this.temporaryFile));
            }
            this.temporaryFile.delete();
        }
    }

    protected void finalize() throws Throwable {
        if (this.temporaryFile != null) {
            log.warn((Object)("Cleaning up unreleased temporary file " + this.temporaryFile));
            this.temporaryFile.delete();
        }
    }

    class InputStreamImpl
    extends InputStream {
        private int currentChunkIndex;
        private int currentChunkOffset;
        private int markChunkIndex;
        private int markChunkOffset;

        InputStreamImpl() {
        }

        public int available() throws IOException {
            return (OverflowBlob.this.chunkIndex - this.currentChunkIndex) * OverflowBlob.this.chunkSize + OverflowBlob.this.chunkOffset - this.currentChunkOffset;
        }

        public int read(byte[] b, int off, int len) throws IOException {
            if (len == 0) {
                return 0;
            }
            int read = 0;
            while (len > 0 && (this.currentChunkIndex != OverflowBlob.this.chunkIndex || this.currentChunkOffset != OverflowBlob.this.chunkOffset)) {
                int c = this.currentChunkIndex == OverflowBlob.this.chunkIndex ? Math.min(len, OverflowBlob.this.chunkOffset - this.currentChunkOffset) : Math.min(len, OverflowBlob.this.chunkSize - this.currentChunkOffset);
                System.arraycopy(OverflowBlob.this.chunks[this.currentChunkIndex], this.currentChunkOffset, b, off, c);
                len -= c;
                off += c;
                this.currentChunkOffset += c;
                read += c;
                if (this.currentChunkOffset != OverflowBlob.this.chunkSize) continue;
                ++this.currentChunkIndex;
                this.currentChunkOffset = 0;
            }
            if (read == 0) {
                return -1;
            }
            return read;
        }

        public int read(byte[] b) throws IOException {
            return this.read(b, 0, b.length);
        }

        public int read() throws IOException {
            byte[] b = new byte[1];
            return this.read(b) == -1 ? -1 : b[0] & 0xFF;
        }

        public boolean markSupported() {
            return true;
        }

        public void mark(int readlimit) {
            this.markChunkIndex = this.currentChunkIndex;
            this.markChunkOffset = this.currentChunkOffset;
        }

        public void reset() throws IOException {
            this.currentChunkIndex = this.markChunkIndex;
            this.currentChunkOffset = this.markChunkOffset;
        }

        public long skip(long n) throws IOException {
            int available = this.available();
            int c = n < (long)available ? (int)n : available;
            int newOffset = this.currentChunkOffset + c;
            int chunkDelta = newOffset / OverflowBlob.this.chunkSize;
            this.currentChunkIndex += chunkDelta;
            this.currentChunkOffset = newOffset - chunkDelta * OverflowBlob.this.chunkSize;
            return c;
        }

        public void close() throws IOException {
        }
    }

    class OutputStreamImpl
    extends BlobOutputStream {
        private FileOutputStream fileOutputStream;

        OutputStreamImpl() {
        }

        public WritableBlob getBlob() {
            return OverflowBlob.this;
        }

        public void write(byte[] b, int off, int len) throws IOException {
            if (this.fileOutputStream != null) {
                this.fileOutputStream.write(b, off, len);
            } else if (len > (OverflowBlob.this.chunks.length - OverflowBlob.this.chunkIndex) * OverflowBlob.this.chunkSize - OverflowBlob.this.chunkOffset) {
                this.fileOutputStream = OverflowBlob.this.switchToTempFile();
                this.fileOutputStream.write(b, off, len);
            } else {
                while (len > 0) {
                    byte[] chunk = OverflowBlob.this.getCurrentChunk();
                    int c = Math.min(len, OverflowBlob.this.chunkSize - OverflowBlob.this.chunkOffset);
                    System.arraycopy(b, off, chunk, OverflowBlob.this.chunkOffset, c);
                    len -= c;
                    off += c;
                    OverflowBlob.this.chunkOffset += c;
                    if (OverflowBlob.this.chunkOffset != OverflowBlob.this.chunkSize) continue;
                    ++OverflowBlob.this.chunkIndex;
                    OverflowBlob.this.chunkOffset = 0;
                }
            }
        }

        public void write(byte[] b) throws IOException {
            this.write(b, 0, b.length);
        }

        public void write(int b) throws IOException {
            this.write(new byte[]{(byte)b}, 0, 1);
        }

        public void flush() throws IOException {
            if (this.fileOutputStream != null) {
                this.fileOutputStream.flush();
            }
        }

        public void close() throws IOException {
            if (this.fileOutputStream != null) {
                this.fileOutputStream.close();
            }
            OverflowBlob.this.state = 2;
        }
    }
}

