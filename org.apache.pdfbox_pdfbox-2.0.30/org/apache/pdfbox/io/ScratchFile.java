/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.io.RandomAccess;
import org.apache.pdfbox.io.ScratchFileBuffer;

public class ScratchFile
implements Closeable {
    private static final Log LOG = LogFactory.getLog(ScratchFile.class);
    private static final int ENLARGE_PAGE_COUNT = 16;
    private static final int INIT_UNRESTRICTED_MAINMEM_PAGECOUNT = 100000;
    private static final int PAGE_SIZE = 4096;
    private final Object ioLock = new Object();
    private final File scratchFileDirectory;
    private File file;
    private RandomAccessFile raf;
    private volatile int pageCount = 0;
    private final BitSet freePages = new BitSet();
    private volatile byte[][] inMemoryPages;
    private final int inMemoryMaxPageCount;
    private final int maxPageCount;
    private final boolean useScratchFile;
    private final boolean maxMainMemoryIsRestricted;
    private final List<ScratchFileBuffer> buffers = new ArrayList<ScratchFileBuffer>();
    private volatile boolean isClosed = false;

    public ScratchFile(File scratchFileDirectory) throws IOException {
        this(MemoryUsageSetting.setupTempFileOnly().setTempDir(scratchFileDirectory));
    }

    public ScratchFile(MemoryUsageSetting memUsageSetting) throws IOException {
        this.maxMainMemoryIsRestricted = !memUsageSetting.useMainMemory() || memUsageSetting.isMainMemoryRestricted();
        this.useScratchFile = this.maxMainMemoryIsRestricted && memUsageSetting.useTempFile();
        File file = this.scratchFileDirectory = this.useScratchFile ? memUsageSetting.getTempDir() : null;
        if (this.scratchFileDirectory != null && !this.scratchFileDirectory.isDirectory()) {
            throw new IOException("Scratch file directory does not exist: " + this.scratchFileDirectory);
        }
        int n = this.maxPageCount = memUsageSetting.isStorageRestricted() ? (int)Math.min(Integer.MAX_VALUE, memUsageSetting.getMaxStorageBytes() / 4096L) : Integer.MAX_VALUE;
        this.inMemoryMaxPageCount = memUsageSetting.useMainMemory() ? (memUsageSetting.isMainMemoryRestricted() ? (int)Math.min(Integer.MAX_VALUE, memUsageSetting.getMaxMainMemoryBytes() / 4096L) : Integer.MAX_VALUE) : 0;
        this.inMemoryPages = new byte[this.maxMainMemoryIsRestricted ? this.inMemoryMaxPageCount : 100000][];
        this.freePages.set(0, this.inMemoryPages.length);
    }

    public static ScratchFile getMainMemoryOnlyInstance() {
        try {
            return new ScratchFile(MemoryUsageSetting.setupMainMemoryOnly());
        }
        catch (IOException ioe) {
            LOG.error((Object)("Unexpected exception occurred creating main memory scratch file instance: " + ioe.getMessage()));
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    int getNewPage() throws IOException {
        BitSet bitSet = this.freePages;
        synchronized (bitSet) {
            int idx = this.freePages.nextSetBit(0);
            if (idx < 0) {
                this.enlarge();
                idx = this.freePages.nextSetBit(0);
                if (idx < 0) {
                    throw new IOException("Maximum allowed scratch file memory exceeded.");
                }
            }
            this.freePages.clear(idx);
            if (idx >= this.pageCount) {
                this.pageCount = idx + 1;
            }
            return idx;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void enlarge() throws IOException {
        Object object = this.ioLock;
        synchronized (object) {
            int oldSize;
            int newSize;
            this.checkClosed();
            if (this.pageCount >= this.maxPageCount) {
                return;
            }
            if (this.useScratchFile) {
                long fileLen;
                long expectedFileLen;
                if (this.raf == null) {
                    this.file = File.createTempFile("PDFBox", ".tmp", this.scratchFileDirectory);
                    try {
                        this.raf = new RandomAccessFile(this.file, "rw");
                    }
                    catch (IOException e) {
                        if (!this.file.delete()) {
                            LOG.warn((Object)("Error deleting scratch file: " + this.file.getAbsolutePath()));
                        }
                        throw e;
                    }
                }
                if ((expectedFileLen = ((long)this.pageCount - (long)this.inMemoryMaxPageCount) * 4096L) != (fileLen = this.raf.length())) {
                    throw new IOException("Expected scratch file size of " + expectedFileLen + " but found " + fileLen + " in file " + this.file);
                }
                if (this.pageCount + 16 > this.pageCount) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug((Object)("file: " + this.file));
                        LOG.debug((Object)("fileLen before: " + fileLen + ", raf length: " + this.raf.length() + ", file length: " + this.file.length()));
                    }
                    this.raf.setLength(fileLen += 65536L);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug((Object)("fileLen after1: " + fileLen + ", raf length: " + this.raf.length() + ", file length: " + this.file.length()));
                    }
                    if (fileLen != this.raf.length()) {
                        long origFilePointer = this.raf.getFilePointer();
                        this.raf.seek(fileLen - 1L);
                        this.raf.write(0);
                        this.raf.seek(origFilePointer);
                        LOG.debug((Object)("fileLen after2:  " + fileLen + ", raf length: " + this.raf.length() + ", file length: " + this.file.length()));
                    }
                    this.freePages.set(this.pageCount, this.pageCount + 16);
                }
            } else if (!this.maxMainMemoryIsRestricted && (newSize = (int)Math.min((long)(oldSize = this.inMemoryPages.length) * 2L, Integer.MAX_VALUE)) > oldSize) {
                byte[][] newInMemoryPages = new byte[newSize][];
                System.arraycopy(this.inMemoryPages, 0, newInMemoryPages, 0, oldSize);
                this.inMemoryPages = newInMemoryPages;
                this.freePages.set(oldSize, newSize);
            }
        }
    }

    int getPageSize() {
        return 4096;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    byte[] readPage(int pageIdx) throws IOException {
        if (pageIdx < 0 || pageIdx >= this.pageCount) {
            this.checkClosed();
            throw new IOException("Page index out of range: " + pageIdx + ". Max value: " + (this.pageCount - 1));
        }
        if (pageIdx < this.inMemoryMaxPageCount) {
            byte[] page = this.inMemoryPages[pageIdx];
            if (page == null) {
                this.checkClosed();
                throw new IOException("Requested page with index " + pageIdx + " was not written before.");
            }
            return page;
        }
        Object object = this.ioLock;
        synchronized (object) {
            if (this.raf == null) {
                this.checkClosed();
                throw new IOException("Missing scratch file to read page with index " + pageIdx + " from.");
            }
            byte[] page = new byte[4096];
            this.raf.seek(((long)pageIdx - (long)this.inMemoryMaxPageCount) * 4096L);
            this.raf.readFully(page);
            return page;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void writePage(int pageIdx, byte[] page) throws IOException {
        if (pageIdx < 0 || pageIdx >= this.pageCount) {
            this.checkClosed();
            throw new IOException("Page index out of range: " + pageIdx + ". Max value: " + (this.pageCount - 1));
        }
        if (page.length != 4096) {
            throw new IOException("Wrong page size to write: " + page.length + ". Expected: " + 4096);
        }
        if (pageIdx < this.inMemoryMaxPageCount) {
            if (this.maxMainMemoryIsRestricted) {
                this.inMemoryPages[pageIdx] = page;
            } else {
                Object object = this.ioLock;
                synchronized (object) {
                    this.inMemoryPages[pageIdx] = page;
                }
            }
            this.checkClosed();
        } else {
            Object object = this.ioLock;
            synchronized (object) {
                this.checkClosed();
                this.raf.seek(((long)pageIdx - (long)this.inMemoryMaxPageCount) * 4096L);
                this.raf.write(page);
            }
        }
    }

    void checkClosed() throws IOException {
        if (this.isClosed) {
            throw new IOException("Scratch file already closed");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public RandomAccess createBuffer() throws IOException {
        ScratchFileBuffer newBuffer = new ScratchFileBuffer(this);
        List<ScratchFileBuffer> list = this.buffers;
        synchronized (list) {
            this.buffers.add(newBuffer);
        }
        return newBuffer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void removeBuffer(ScratchFileBuffer buffer) {
        List<ScratchFileBuffer> list = this.buffers;
        synchronized (list) {
            this.buffers.remove(buffer);
        }
    }

    public RandomAccess createBuffer(InputStream input) throws IOException {
        int bytesRead;
        RandomAccess buf = this.createBuffer();
        byte[] byteBuffer = new byte[8192];
        while ((bytesRead = input.read(byteBuffer)) > -1) {
            buf.write(byteBuffer, 0, bytesRead);
        }
        buf.seek(0L);
        return buf;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void markPagesAsFree(int[] pageIndexes, int off, int count) {
        BitSet bitSet = this.freePages;
        synchronized (bitSet) {
            for (int aIdx = off; aIdx < count; ++aIdx) {
                int pageIdx = pageIndexes[aIdx];
                if (pageIdx < 0 || pageIdx >= this.pageCount || this.freePages.get(pageIdx)) continue;
                this.freePages.set(pageIdx);
                if (pageIdx >= this.inMemoryMaxPageCount) continue;
                this.inMemoryPages[pageIdx] = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws IOException {
        IOException ioexc = null;
        Object object = this.ioLock;
        synchronized (object) {
            if (this.isClosed) {
                return;
            }
            this.isClosed = true;
            for (ScratchFileBuffer buffer : this.buffers) {
                if (buffer == null || buffer.isClosed()) continue;
                buffer.close(false);
            }
            this.buffers.clear();
            if (this.raf != null) {
                try {
                    this.raf.close();
                }
                catch (IOException ioe) {
                    ioexc = ioe;
                }
            }
            if (this.file != null && !this.file.delete() && this.file.exists() && ioexc == null) {
                ioexc = new IOException("Error deleting scratch file: " + this.file.getAbsolutePath());
            }
        }
        object = this.freePages;
        synchronized (object) {
            this.freePages.clear();
            this.pageCount = 0;
        }
        if (ioexc != null) {
            throw ioexc;
        }
    }
}

