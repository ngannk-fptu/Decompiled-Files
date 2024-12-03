/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.io;

import java.io.EOFException;
import java.io.IOException;
import org.apache.pdfbox.io.RandomAccess;
import org.apache.pdfbox.io.ScratchFile;

class ScratchFileBuffer
implements RandomAccess {
    private final int pageSize;
    private ScratchFile pageHandler;
    private long size = 0L;
    private int currentPagePositionInPageIndexes;
    private long currentPageOffset;
    private byte[] currentPage;
    private int positionInPage;
    private boolean currentPageContentChanged = false;
    private int[] pageIndexes = new int[16];
    private int pageCount = 0;

    ScratchFileBuffer(ScratchFile pageHandler) throws IOException {
        pageHandler.checkClosed();
        this.pageHandler = pageHandler;
        this.pageSize = this.pageHandler.getPageSize();
        this.addPage();
    }

    private void checkClosed() throws IOException {
        if (this.pageHandler == null) {
            throw new IOException("Buffer already closed");
        }
        this.pageHandler.checkClosed();
    }

    private void addPage() throws IOException {
        int newPageIdx;
        if (this.pageCount + 1 >= this.pageIndexes.length) {
            int newSize = this.pageIndexes.length * 2;
            if (newSize < this.pageIndexes.length) {
                if (this.pageIndexes.length == Integer.MAX_VALUE) {
                    throw new IOException("Maximum buffer size reached.");
                }
                newSize = Integer.MAX_VALUE;
            }
            int[] newPageIndexes = new int[newSize];
            System.arraycopy(this.pageIndexes, 0, newPageIndexes, 0, this.pageCount);
            this.pageIndexes = newPageIndexes;
        }
        this.pageIndexes[this.pageCount] = newPageIdx = this.pageHandler.getNewPage();
        this.currentPagePositionInPageIndexes = this.pageCount;
        this.currentPageOffset = (long)this.pageCount * (long)this.pageSize;
        ++this.pageCount;
        this.currentPage = new byte[this.pageSize];
        this.positionInPage = 0;
    }

    @Override
    public long length() throws IOException {
        return this.size;
    }

    private boolean ensureAvailableBytesInPage(boolean addNewPageIfNeeded) throws IOException {
        if (this.positionInPage >= this.pageSize) {
            if (this.currentPageContentChanged) {
                this.pageHandler.writePage(this.pageIndexes[this.currentPagePositionInPageIndexes], this.currentPage);
                this.currentPageContentChanged = false;
            }
            if (this.currentPagePositionInPageIndexes + 1 < this.pageCount) {
                this.currentPage = this.pageHandler.readPage(this.pageIndexes[++this.currentPagePositionInPageIndexes]);
                this.currentPageOffset = (long)this.currentPagePositionInPageIndexes * (long)this.pageSize;
                this.positionInPage = 0;
            } else if (addNewPageIfNeeded) {
                this.addPage();
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void write(int b) throws IOException {
        this.checkClosed();
        this.ensureAvailableBytesInPage(true);
        this.currentPage[this.positionInPage++] = (byte)b;
        this.currentPageContentChanged = true;
        if (this.currentPageOffset + (long)this.positionInPage > this.size) {
            this.size = this.currentPageOffset + (long)this.positionInPage;
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        int bytesToWrite;
        this.checkClosed();
        int bOff = off;
        for (int remain = len; remain > 0; remain -= bytesToWrite) {
            this.ensureAvailableBytesInPage(true);
            bytesToWrite = Math.min(remain, this.pageSize - this.positionInPage);
            System.arraycopy(b, bOff, this.currentPage, this.positionInPage, bytesToWrite);
            this.positionInPage += bytesToWrite;
            this.currentPageContentChanged = true;
            bOff += bytesToWrite;
        }
        if (this.currentPageOffset + (long)this.positionInPage > this.size) {
            this.size = this.currentPageOffset + (long)this.positionInPage;
        }
    }

    @Override
    public final void clear() throws IOException {
        this.checkClosed();
        this.pageHandler.markPagesAsFree(this.pageIndexes, 1, this.pageCount - 1);
        this.pageCount = 1;
        if (this.currentPagePositionInPageIndexes > 0) {
            this.currentPage = this.pageHandler.readPage(this.pageIndexes[0]);
            this.currentPagePositionInPageIndexes = 0;
            this.currentPageOffset = 0L;
        }
        this.positionInPage = 0;
        this.size = 0L;
        this.currentPageContentChanged = false;
    }

    @Override
    public long getPosition() throws IOException {
        this.checkClosed();
        return this.currentPageOffset + (long)this.positionInPage;
    }

    @Override
    public void seek(long seekToPosition) throws IOException {
        this.checkClosed();
        if (seekToPosition > this.size) {
            throw new EOFException();
        }
        if (seekToPosition < 0L) {
            throw new IOException("Negative seek offset: " + seekToPosition);
        }
        if (seekToPosition >= this.currentPageOffset && seekToPosition <= this.currentPageOffset + (long)this.pageSize) {
            this.positionInPage = (int)(seekToPosition - this.currentPageOffset);
        } else {
            if (this.currentPageContentChanged) {
                this.pageHandler.writePage(this.pageIndexes[this.currentPagePositionInPageIndexes], this.currentPage);
                this.currentPageContentChanged = false;
            }
            int newPagePosition = (int)(seekToPosition / (long)this.pageSize);
            if (seekToPosition % (long)this.pageSize == 0L && seekToPosition == this.size) {
                --newPagePosition;
            }
            this.currentPage = this.pageHandler.readPage(this.pageIndexes[newPagePosition]);
            this.currentPagePositionInPageIndexes = newPagePosition;
            this.currentPageOffset = (long)this.currentPagePositionInPageIndexes * (long)this.pageSize;
            this.positionInPage = (int)(seekToPosition - this.currentPageOffset);
        }
    }

    @Override
    public boolean isClosed() {
        return this.pageHandler == null;
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
        this.seek(this.currentPageOffset + (long)this.positionInPage - (long)bytes);
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
        this.checkClosed();
        return this.currentPageOffset + (long)this.positionInPage >= this.size;
    }

    @Override
    public int available() throws IOException {
        this.checkClosed();
        return (int)Math.min(this.size - (this.currentPageOffset + (long)this.positionInPage), Integer.MAX_VALUE);
    }

    @Override
    public int read() throws IOException {
        this.checkClosed();
        if (this.currentPageOffset + (long)this.positionInPage >= this.size) {
            return -1;
        }
        if (!this.ensureAvailableBytesInPage(false)) {
            throw new IOException("Unexpectedly no bytes available for read in buffer.");
        }
        return this.currentPage[this.positionInPage++] & 0xFF;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int readBytes;
        this.checkClosed();
        if (this.currentPageOffset + (long)this.positionInPage >= this.size) {
            return -1;
        }
        int totalBytesRead = 0;
        int bOff = off;
        for (int remain = (int)Math.min((long)len, this.size - (this.currentPageOffset + (long)this.positionInPage)); remain > 0; remain -= readBytes) {
            if (!this.ensureAvailableBytesInPage(false)) {
                throw new IOException("Unexpectedly no bytes available for read in buffer.");
            }
            readBytes = Math.min(remain, this.pageSize - this.positionInPage);
            System.arraycopy(this.currentPage, this.positionInPage, b, bOff, readBytes);
            this.positionInPage += readBytes;
            totalBytesRead += readBytes;
            bOff += readBytes;
        }
        return totalBytesRead;
    }

    @Override
    public void close() throws IOException {
        this.close(true);
    }

    void close(boolean removeBuffer) {
        if (this.pageHandler != null) {
            this.pageHandler.markPagesAsFree(this.pageIndexes, 0, this.pageCount);
            if (removeBuffer) {
                this.pageHandler.removeBuffer(this);
            }
            this.pageHandler = null;
            this.pageIndexes = null;
            this.currentPage = null;
            this.currentPageOffset = 0L;
            this.currentPagePositionInPageIndexes = -1;
            this.positionInPage = 0;
            this.size = 0L;
        }
    }
}

