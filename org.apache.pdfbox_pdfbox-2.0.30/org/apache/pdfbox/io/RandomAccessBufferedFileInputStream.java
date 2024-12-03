/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.io;

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessRead;

public class RandomAccessBufferedFileInputStream
extends InputStream
implements RandomAccessRead {
    private static final String TMP_FILE_PREFIX = "tmpPDFBox";
    private int pageSizeShift = 12;
    private int pageSize = 1 << this.pageSizeShift;
    private long pageOffsetMask = -1L << this.pageSizeShift;
    private int maxCachedPages = 1000;
    private File tempFile;
    private byte[] lastRemovedCachePage = null;
    private final Map<Long, byte[]> pageCache = new LinkedHashMap<Long, byte[]>(this.maxCachedPages, 0.75f, true){
        private static final long serialVersionUID = -6302488539257741101L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, byte[]> eldest) {
            boolean doRemove;
            boolean bl = doRemove = this.size() > RandomAccessBufferedFileInputStream.this.maxCachedPages;
            if (doRemove) {
                RandomAccessBufferedFileInputStream.access$102(RandomAccessBufferedFileInputStream.this, eldest.getValue());
            }
            return doRemove;
        }
    };
    private long curPageOffset = -1L;
    private byte[] curPage = new byte[this.pageSize];
    private int offsetWithinPage = 0;
    private final RandomAccessFile raFile;
    private final long fileLength;
    private long fileOffset = 0L;
    private boolean isClosed;

    public RandomAccessBufferedFileInputStream(String filename) throws IOException {
        this(new File(filename));
    }

    public RandomAccessBufferedFileInputStream(File file) throws IOException {
        this.raFile = new RandomAccessFile(file, "r");
        this.fileLength = file.length();
        this.seek(0L);
    }

    public RandomAccessBufferedFileInputStream(InputStream input) throws IOException {
        this.tempFile = this.createTmpFile(input);
        this.fileLength = this.tempFile.length();
        this.raFile = new RandomAccessFile(this.tempFile, "r");
        this.seek(0L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private File createTmpFile(InputStream input) throws IOException {
        File file;
        FileOutputStream fos = null;
        try {
            File tmpFile = File.createTempFile(TMP_FILE_PREFIX, ".pdf");
            fos = new FileOutputStream(tmpFile);
            IOUtils.copy(input, fos);
            file = tmpFile;
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(fos);
            throw throwable;
        }
        IOUtils.closeQuietly(input);
        IOUtils.closeQuietly(fos);
        return file;
    }

    private void deleteTempFile() {
        if (this.tempFile != null) {
            this.tempFile.delete();
        }
    }

    @Override
    public long getPosition() {
        return this.fileOffset;
    }

    @Override
    public void seek(long newOffset) throws IOException {
        long newPageOffset = newOffset & this.pageOffsetMask;
        if (newPageOffset != this.curPageOffset) {
            byte[] newPage = this.pageCache.get(newPageOffset);
            if (newPage == null) {
                this.raFile.seek(newPageOffset);
                newPage = this.readPage();
                this.pageCache.put(newPageOffset, newPage);
            }
            this.curPageOffset = newPageOffset;
            this.curPage = newPage;
        }
        this.offsetWithinPage = (int)(newOffset - this.curPageOffset);
        this.fileOffset = newOffset;
    }

    private byte[] readPage() throws IOException {
        int curBytesRead;
        byte[] page;
        if (this.lastRemovedCachePage != null) {
            page = this.lastRemovedCachePage;
            this.lastRemovedCachePage = null;
        } else {
            page = new byte[this.pageSize];
        }
        for (int readBytes = 0; readBytes < this.pageSize && (curBytesRead = this.raFile.read(page, readBytes, this.pageSize - readBytes)) >= 0; readBytes += curBytesRead) {
        }
        return page;
    }

    @Override
    public int read() throws IOException {
        if (this.fileOffset >= this.fileLength) {
            return -1;
        }
        if (this.offsetWithinPage == this.pageSize) {
            this.seek(this.fileOffset);
        }
        ++this.fileOffset;
        return this.curPage[this.offsetWithinPage++] & 0xFF;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.fileOffset >= this.fileLength) {
            return -1;
        }
        if (this.offsetWithinPage == this.pageSize) {
            this.seek(this.fileOffset);
        }
        int commonLen = Math.min(this.pageSize - this.offsetWithinPage, len);
        if (this.fileLength - this.fileOffset < (long)this.pageSize) {
            commonLen = Math.min(commonLen, (int)(this.fileLength - this.fileOffset));
        }
        System.arraycopy(this.curPage, this.offsetWithinPage, b, off, commonLen);
        this.offsetWithinPage += commonLen;
        this.fileOffset += (long)commonLen;
        return commonLen;
    }

    @Override
    public int available() throws IOException {
        return (int)Math.min(this.fileLength - this.fileOffset, Integer.MAX_VALUE);
    }

    @Override
    public long skip(long n) throws IOException {
        long toSkip = n;
        if (this.fileLength - this.fileOffset < toSkip) {
            toSkip = this.fileLength - this.fileOffset;
        }
        if (toSkip < (long)this.pageSize && (long)this.offsetWithinPage + toSkip <= (long)this.pageSize) {
            this.offsetWithinPage = (int)((long)this.offsetWithinPage + toSkip);
            this.fileOffset += toSkip;
        } else {
            this.seek(this.fileOffset + toSkip);
        }
        return toSkip;
    }

    @Override
    public long length() throws IOException {
        return this.fileLength;
    }

    @Override
    public void close() throws IOException {
        this.raFile.close();
        this.deleteTempFile();
        this.pageCache.clear();
        this.isClosed = true;
    }

    @Override
    public boolean isClosed() {
        return this.isClosed;
    }

    @Override
    public int peek() throws IOException {
        int result = this.read();
        if (result != -1) {
            this.rewind(1);
        }
        return result;
    }

    @Override
    public void rewind(int bytes) throws IOException {
        this.seek(this.getPosition() - (long)bytes);
    }

    @Override
    public byte[] readFully(int length) throws IOException {
        int count;
        byte[] bytes = new byte[length];
        int bytesRead = 0;
        do {
            if ((count = this.read(bytes, bytesRead, length - bytesRead)) >= 0) continue;
            throw new EOFException();
        } while ((bytesRead += count) < length);
        return bytes;
    }

    @Override
    public boolean isEOF() throws IOException {
        int peek = this.peek();
        return peek == -1;
    }

    static /* synthetic */ byte[] access$102(RandomAccessBufferedFileInputStream x0, byte[] x1) {
        x0.lastRemovedCachePage = x1;
        return x1;
    }
}

