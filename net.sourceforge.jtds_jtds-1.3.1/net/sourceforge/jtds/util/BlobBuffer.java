/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.sql.SQLException;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.util.Logger;

public class BlobBuffer {
    private static final byte[] EMPTY_BUFFER = new byte[0];
    private static final int PAGE_SIZE = 1024;
    private static final int PAGE_MASK = -1024;
    private static final int BYTE_MASK = 1023;
    private static final int MAX_BUF_INC = 16384;
    private static final int INVALID_PAGE = -1;
    private byte[] buffer;
    private int length;
    private int currentPage;
    private File blobFile;
    private RandomAccessFile raFile;
    private boolean bufferDirty;
    private int openCount;
    private boolean isMemOnly;
    private final File bufferDir;
    private final int maxMemSize;

    public BlobBuffer(File bufferDir, long maxMemSize) {
        if (maxMemSize > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("The maximum in-memory buffer size of a blob buffer cannot exceed 2GB");
        }
        this.bufferDir = bufferDir;
        this.maxMemSize = (int)maxMemSize;
        this.buffer = EMPTY_BUFFER;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void finalize() throws Throwable {
        try {
            if (this.raFile != null) {
                this.raFile.close();
            }
        }
        catch (IOException iOException) {
        }
        finally {
            if (this.blobFile != null) {
                this.blobFile.delete();
            }
        }
    }

    public void createBlobFile() {
        if (this.bufferDir == null) {
            this.isMemOnly = true;
            return;
        }
        try {
            this.blobFile = File.createTempFile("jtds", ".tmp", this.bufferDir);
            this.raFile = new RandomAccessFile(this.blobFile, "rw");
            if (this.length > 0) {
                this.raFile.write(this.buffer, 0, this.length);
            }
            this.buffer = new byte[1024];
            this.currentPage = -1;
            this.openCount = 0;
        }
        catch (SecurityException e) {
            this.blobFile = null;
            this.raFile = null;
            this.isMemOnly = true;
            Logger.println("SecurityException creating BLOB file:");
            Logger.logException(e);
        }
        catch (IOException ioe) {
            this.blobFile = null;
            this.raFile = null;
            this.isMemOnly = true;
            Logger.println("IOException creating BLOB file:");
            Logger.logException(ioe);
        }
    }

    public void open() throws IOException {
        if (this.raFile == null && this.blobFile != null) {
            this.raFile = new RandomAccessFile(this.blobFile, "rw");
            this.openCount = 1;
            this.currentPage = -1;
            this.buffer = new byte[1024];
            return;
        }
        if (this.raFile != null) {
            ++this.openCount;
        }
    }

    public int read(int readPtr) throws IOException {
        if (readPtr >= this.length) {
            return -1;
        }
        if (this.raFile != null) {
            if (this.currentPage != (readPtr & 0xFFFFFC00)) {
                this.readPage(readPtr);
            }
            return this.buffer[readPtr & 0x3FF] & 0xFF;
        }
        return this.buffer[readPtr] & 0xFF;
    }

    public int read(int readPtr, byte[] bytes, int offset, int len) throws IOException {
        if (bytes == null) {
            throw new NullPointerException();
        }
        if (offset < 0 || offset > bytes.length || len < 0 || offset + len > bytes.length || offset + len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        if (readPtr >= this.length) {
            return -1;
        }
        if (this.raFile != null) {
            if ((len = Math.min(this.length - readPtr, len)) >= 1024) {
                if (this.bufferDirty) {
                    this.writePage(this.currentPage);
                }
                this.currentPage = -1;
                this.raFile.seek(readPtr);
                this.raFile.readFully(bytes, offset, len);
            } else {
                int inBuffer;
                for (int count = len; count > 0; count -= inBuffer) {
                    if (this.currentPage != (readPtr & 0xFFFFFC00)) {
                        this.readPage(readPtr);
                    }
                    inBuffer = Math.min(1024 - (readPtr & 0x3FF), count);
                    System.arraycopy(this.buffer, readPtr & 0x3FF, bytes, offset, inBuffer);
                    offset += inBuffer;
                    readPtr += inBuffer;
                }
            }
        } else {
            len = Math.min(this.length - readPtr, len);
            System.arraycopy(this.buffer, readPtr, bytes, offset, len);
        }
        return len;
    }

    public void write(int writePtr, int b) throws IOException {
        if (writePtr >= this.length) {
            if (writePtr > this.length) {
                throw new IOException("BLOB buffer has been truncated");
            }
            if (++this.length < 0) {
                throw new IOException("BLOB may not exceed 2GB in size");
            }
        }
        if (this.raFile != null) {
            if (this.currentPage != (writePtr & 0xFFFFFC00)) {
                this.readPage(writePtr);
            }
            this.buffer[writePtr & 0x3FF] = (byte)b;
            this.bufferDirty = true;
        } else {
            if (writePtr >= this.buffer.length) {
                this.growBuffer(writePtr + 1);
            }
            this.buffer[writePtr] = (byte)b;
        }
    }

    void write(int writePtr, byte[] bytes, int offset, int len) throws IOException {
        if (bytes == null) {
            throw new NullPointerException();
        }
        if (offset < 0 || offset > bytes.length || len < 0 || offset + len > bytes.length || offset + len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }
        if ((long)writePtr + (long)len > Integer.MAX_VALUE) {
            throw new IOException("BLOB may not exceed 2GB in size");
        }
        if (writePtr > this.length) {
            throw new IOException("BLOB buffer has been truncated");
        }
        if (this.raFile != null) {
            if (len >= 1024) {
                if (this.bufferDirty) {
                    this.writePage(this.currentPage);
                }
                this.currentPage = -1;
                this.raFile.seek(writePtr);
                this.raFile.write(bytes, offset, len);
                writePtr += len;
            } else {
                int inBuffer;
                for (int count = len; count > 0; count -= inBuffer) {
                    if (this.currentPage != (writePtr & 0xFFFFFC00)) {
                        this.readPage(writePtr);
                    }
                    inBuffer = Math.min(1024 - (writePtr & 0x3FF), count);
                    System.arraycopy(bytes, offset, this.buffer, writePtr & 0x3FF, inBuffer);
                    this.bufferDirty = true;
                    offset += inBuffer;
                    writePtr += inBuffer;
                }
            }
        } else {
            if (writePtr + len > this.buffer.length) {
                this.growBuffer(writePtr + len);
            }
            System.arraycopy(bytes, offset, this.buffer, writePtr, len);
            writePtr += len;
        }
        if (writePtr > this.length) {
            this.length = writePtr;
        }
    }

    public void readPage(int page) throws IOException {
        int res;
        page &= 0xFFFFFC00;
        if (this.bufferDirty) {
            this.writePage(this.currentPage);
        }
        if ((long)page > this.raFile.length()) {
            throw new IOException("readPage: Invalid page number " + page);
        }
        this.currentPage = page;
        this.raFile.seek(this.currentPage);
        int count = 0;
        while ((count += (res = this.raFile.read(this.buffer, count, this.buffer.length - count)) == -1 ? 0 : res) < 1024 && res != -1) {
        }
    }

    public void writePage(int page) throws IOException {
        if ((long)(page &= 0xFFFFFC00) > this.raFile.length()) {
            throw new IOException("writePage: Invalid page number " + page);
        }
        if (this.buffer.length != 1024) {
            throw new IllegalStateException("writePage: buffer size invalid");
        }
        this.raFile.seek(page);
        this.raFile.write(this.buffer);
        this.bufferDirty = false;
    }

    public void close() throws IOException {
        if (this.openCount > 0 && --this.openCount == 0 && this.raFile != null) {
            if (this.bufferDirty) {
                this.writePage(this.currentPage);
            }
            this.raFile.close();
            this.raFile = null;
            this.buffer = EMPTY_BUFFER;
            this.currentPage = -1;
        }
    }

    public void growBuffer(int minSize) {
        if (this.buffer.length == 0) {
            this.buffer = new byte[Math.max(1024, minSize)];
        } else {
            byte[] tmp = this.buffer.length * 2 > minSize && this.buffer.length <= 16384 ? new byte[this.buffer.length * 2] : new byte[minSize + 16384];
            System.arraycopy(this.buffer, 0, tmp, 0, this.buffer.length);
            this.buffer = tmp;
        }
    }

    public void setBuffer(byte[] bytes, boolean copy) {
        if (copy) {
            this.buffer = new byte[bytes.length];
            System.arraycopy(bytes, 0, this.buffer, 0, this.buffer.length);
        } else {
            this.buffer = bytes;
        }
        this.length = this.buffer.length;
    }

    public byte[] getBytes(long pos, int len) throws SQLException {
        if (--pos < 0L) {
            throw new SQLException(Messages.get("error.blobclob.badpos"), "HY090");
        }
        if (pos > (long)this.length) {
            throw new SQLException(Messages.get("error.blobclob.badposlen"), "HY090");
        }
        if (len < 0) {
            throw new SQLException(Messages.get("error.blobclob.badlen"), "HY090");
        }
        if (pos + (long)len > (long)this.length) {
            len = (int)((long)this.length - pos);
        }
        try {
            byte[] data = new byte[len];
            if (this.blobFile == null) {
                System.arraycopy(this.buffer, (int)pos, data, 0, len);
            } else {
                BlobInputStream is = new BlobInputStream(pos);
                int bc = is.read(data);
                ((InputStream)is).close();
                if (bc != data.length) {
                    throw new IOException("Unexpected EOF on BLOB data file bc=" + bc + " data.len=" + data.length);
                }
            }
            return data;
        }
        catch (IOException e) {
            throw new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "HY000");
        }
    }

    public InputStream getBinaryStream(boolean ascii) throws SQLException {
        try {
            if (ascii) {
                return new AsciiInputStream(0L);
            }
            return new BlobInputStream(0L);
        }
        catch (IOException e) {
            throw new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "HY000");
        }
    }

    public InputStream getUnicodeStream() throws SQLException {
        try {
            return new UnicodeInputStream(0L);
        }
        catch (IOException e) {
            throw new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "HY000");
        }
    }

    public OutputStream setBinaryStream(long pos, boolean ascii) throws SQLException {
        if (--pos < 0L) {
            throw new SQLException(Messages.get("error.blobclob.badpos"), "HY090");
        }
        if (pos > (long)this.length) {
            throw new SQLException(Messages.get("error.blobclob.badposlen"), "HY090");
        }
        try {
            if (!this.isMemOnly && this.blobFile == null) {
                this.createBlobFile();
            }
            if (ascii) {
                return new AsciiOutputStream(pos);
            }
            return new BlobOutputStream(pos);
        }
        catch (IOException e) {
            throw new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "HY000");
        }
    }

    public int setBytes(long pos, byte[] bytes, int offset, int len, boolean copy) throws SQLException {
        if (--pos < 0L) {
            throw new SQLException(Messages.get("error.blobclob.badpos"), "HY090");
        }
        if (pos > (long)this.length) {
            throw new SQLException(Messages.get("error.blobclob.badposlen"), "HY090");
        }
        if (bytes == null) {
            throw new SQLException(Messages.get("error.blob.bytesnull"), "HY009");
        }
        if (offset < 0 || offset > bytes.length) {
            throw new SQLException(Messages.get("error.blobclob.badoffset"), "HY090");
        }
        if (len < 0 || pos + (long)len > Integer.MAX_VALUE || offset + len > bytes.length) {
            throw new SQLException(Messages.get("error.blobclob.badlen"), "HY090");
        }
        if (this.blobFile == null && pos == 0L && len >= this.length && len <= this.maxMemSize) {
            if (copy) {
                this.buffer = new byte[len];
                System.arraycopy(bytes, offset, this.buffer, 0, len);
            } else {
                this.buffer = bytes;
            }
            this.length = len;
            return len;
        }
        try {
            if (!this.isMemOnly && this.blobFile == null) {
                this.createBlobFile();
            }
            this.open();
            int ptr = (int)pos;
            this.write(ptr, bytes, offset, len);
            this.close();
            return len;
        }
        catch (IOException e) {
            throw new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "HY000");
        }
    }

    public long getLength() {
        return this.length;
    }

    public void setLength(long length) {
        this.length = (int)length;
    }

    public void truncate(long len) throws SQLException {
        if (len < 0L) {
            throw new SQLException(Messages.get("error.blobclob.badlen"), "HY090");
        }
        if (len > (long)this.length) {
            throw new SQLException(Messages.get("error.blobclob.lentoolong"), "HY090");
        }
        this.length = (int)len;
        if (len == 0L) {
            try {
                if (this.blobFile != null) {
                    if (this.raFile != null) {
                        this.raFile.close();
                    }
                    this.blobFile.delete();
                }
            }
            catch (IOException e) {
                throw new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "HY000");
            }
            finally {
                this.buffer = EMPTY_BUFFER;
                this.blobFile = null;
                this.raFile = null;
                this.openCount = 0;
                this.currentPage = -1;
            }
        }
    }

    public int position(byte[] pattern, long start) throws SQLException {
        try {
            if (--start < 0L) {
                throw new SQLException(Messages.get("error.blobclob.badpos"), "HY090");
            }
            if (start >= (long)this.length) {
                throw new SQLException(Messages.get("error.blobclob.badposlen"), "HY090");
            }
            if (pattern == null) {
                throw new SQLException(Messages.get("error.blob.badpattern"), "HY009");
            }
            if (pattern.length == 0 || this.length == 0 || pattern.length > this.length) {
                return -1;
            }
            int limit = this.length - pattern.length;
            if (this.blobFile == null) {
                for (int i = (int)start; i <= limit; ++i) {
                    int p;
                    for (p = 0; p < pattern.length && this.buffer[i + p] == pattern[p]; ++p) {
                    }
                    if (p != pattern.length) continue;
                    return i + 1;
                }
            } else {
                this.open();
                for (int i = (int)start; i <= limit; ++i) {
                    int p;
                    for (p = 0; p < pattern.length && this.read(i + p) == (pattern[p] & 0xFF); ++p) {
                    }
                    if (p != pattern.length) continue;
                    this.close();
                    return i + 1;
                }
                this.close();
            }
            return -1;
        }
        catch (IOException e) {
            throw new SQLException(Messages.get("error.generic.ioerror", e.getMessage()), "HY000");
        }
    }

    private class AsciiOutputStream
    extends OutputStream {
        private int writePtr;
        private boolean open;

        AsciiOutputStream(long pos) throws IOException {
            BlobBuffer.this.open();
            this.open = true;
            this.writePtr = (int)pos;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void finalize() throws Throwable {
            if (this.open) {
                try {
                    this.close();
                }
                catch (IOException iOException) {
                }
                finally {
                    super.finalize();
                }
            }
        }

        @Override
        public void write(int b) throws IOException {
            BlobBuffer.this.write(this.writePtr++, b);
            BlobBuffer.this.write(this.writePtr++, 0);
        }

        @Override
        public void close() throws IOException {
            if (this.open) {
                BlobBuffer.this.close();
                this.open = false;
            }
        }
    }

    private class BlobOutputStream
    extends OutputStream {
        private int writePtr;
        private boolean open;

        BlobOutputStream(long pos) throws IOException {
            BlobBuffer.this.open();
            this.open = true;
            this.writePtr = (int)pos;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void finalize() throws Throwable {
            if (this.open) {
                try {
                    this.close();
                }
                catch (IOException iOException) {
                }
                finally {
                    super.finalize();
                }
            }
        }

        @Override
        public void write(int b) throws IOException {
            BlobBuffer.this.write(this.writePtr++, b);
        }

        @Override
        public void write(byte[] bytes, int offset, int len) throws IOException {
            BlobBuffer.this.write(this.writePtr, bytes, offset, len);
            this.writePtr += len;
        }

        @Override
        public void close() throws IOException {
            if (this.open) {
                BlobBuffer.this.close();
                this.open = false;
            }
        }
    }

    private class AsciiInputStream
    extends InputStream {
        private int readPtr;
        private boolean open;

        public AsciiInputStream(long pos) throws IOException {
            BlobBuffer.this.open();
            this.open = true;
            this.readPtr = (int)pos;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void finalize() throws Throwable {
            if (this.open) {
                try {
                    this.close();
                }
                catch (IOException iOException) {
                }
                finally {
                    super.finalize();
                }
            }
        }

        @Override
        public int available() throws IOException {
            return ((int)BlobBuffer.this.getLength() - this.readPtr) / 2;
        }

        @Override
        public int read() throws IOException {
            int b1 = BlobBuffer.this.read(this.readPtr);
            if (b1 >= 0) {
                ++this.readPtr;
                int b2 = BlobBuffer.this.read(this.readPtr);
                if (b2 >= 0) {
                    ++this.readPtr;
                    if (b2 != 0 || b1 > 127) {
                        b1 = 63;
                    }
                    return b1;
                }
            }
            return -1;
        }

        @Override
        public void close() throws IOException {
            if (this.open) {
                BlobBuffer.this.close();
                this.open = false;
            }
        }
    }

    private class UnicodeInputStream
    extends InputStream {
        private int readPtr;
        private boolean open;

        public UnicodeInputStream(long pos) throws IOException {
            BlobBuffer.this.open();
            this.open = true;
            this.readPtr = (int)pos;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void finalize() throws Throwable {
            if (this.open) {
                try {
                    this.close();
                }
                catch (IOException iOException) {
                }
                finally {
                    super.finalize();
                }
            }
        }

        @Override
        public int available() throws IOException {
            return (int)BlobBuffer.this.getLength() - this.readPtr;
        }

        @Override
        public int read() throws IOException {
            int b = BlobBuffer.this.read(this.readPtr ^ 1);
            if (b >= 0) {
                ++this.readPtr;
            }
            return b;
        }

        @Override
        public void close() throws IOException {
            if (this.open) {
                BlobBuffer.this.close();
                this.open = false;
            }
        }
    }

    private class BlobInputStream
    extends InputStream {
        private int readPtr;
        private boolean open;

        public BlobInputStream(long pos) throws IOException {
            BlobBuffer.this.open();
            this.open = true;
            this.readPtr = (int)pos;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void finalize() throws Throwable {
            if (this.open) {
                try {
                    this.close();
                }
                catch (IOException iOException) {
                }
                finally {
                    super.finalize();
                }
            }
        }

        @Override
        public int available() throws IOException {
            return (int)BlobBuffer.this.getLength() - this.readPtr;
        }

        @Override
        public int read() throws IOException {
            int b = BlobBuffer.this.read(this.readPtr);
            if (b >= 0) {
                ++this.readPtr;
            }
            return b;
        }

        @Override
        public int read(byte[] bytes, int offset, int len) throws IOException {
            int b = BlobBuffer.this.read(this.readPtr, bytes, offset, len);
            if (b > 0) {
                this.readPtr += b;
            }
            return b;
        }

        @Override
        public void close() throws IOException {
            if (this.open) {
                BlobBuffer.this.close();
                this.open = false;
            }
        }
    }
}

