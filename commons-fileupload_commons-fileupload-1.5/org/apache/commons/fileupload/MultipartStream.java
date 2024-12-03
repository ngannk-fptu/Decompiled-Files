/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.fileupload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.util.Closeable;
import org.apache.commons.fileupload.util.Streams;

public class MultipartStream {
    public static final byte CR = 13;
    public static final byte LF = 10;
    public static final byte DASH = 45;
    public static final int HEADER_PART_SIZE_MAX = 10240;
    protected static final int DEFAULT_BUFSIZE = 4096;
    protected static final byte[] HEADER_SEPARATOR = new byte[]{13, 10, 13, 10};
    protected static final byte[] FIELD_SEPARATOR = new byte[]{13, 10};
    protected static final byte[] STREAM_TERMINATOR = new byte[]{45, 45};
    protected static final byte[] BOUNDARY_PREFIX = new byte[]{13, 10, 45, 45};
    private final InputStream input;
    private int boundaryLength;
    private final int keepRegion;
    private final byte[] boundary;
    private final int[] boundaryTable;
    private final int bufSize;
    private final byte[] buffer;
    private int head;
    private int tail;
    private String headerEncoding;
    private final ProgressNotifier notifier;

    @Deprecated
    public MultipartStream() {
        this(null, null, null);
    }

    @Deprecated
    public MultipartStream(InputStream input, byte[] boundary, int bufSize) {
        this(input, boundary, bufSize, null);
    }

    public MultipartStream(InputStream input, byte[] boundary, int bufSize, ProgressNotifier pNotifier) {
        if (boundary == null) {
            throw new IllegalArgumentException("boundary may not be null");
        }
        this.boundaryLength = boundary.length + BOUNDARY_PREFIX.length;
        if (bufSize < this.boundaryLength + 1) {
            throw new IllegalArgumentException("The buffer size specified for the MultipartStream is too small");
        }
        this.input = input;
        this.bufSize = Math.max(bufSize, this.boundaryLength * 2);
        this.buffer = new byte[this.bufSize];
        this.notifier = pNotifier;
        this.boundary = new byte[this.boundaryLength];
        this.boundaryTable = new int[this.boundaryLength + 1];
        this.keepRegion = this.boundary.length;
        System.arraycopy(BOUNDARY_PREFIX, 0, this.boundary, 0, BOUNDARY_PREFIX.length);
        System.arraycopy(boundary, 0, this.boundary, BOUNDARY_PREFIX.length, boundary.length);
        this.computeBoundaryTable();
        this.head = 0;
        this.tail = 0;
    }

    MultipartStream(InputStream input, byte[] boundary, ProgressNotifier pNotifier) {
        this(input, boundary, 4096, pNotifier);
    }

    @Deprecated
    public MultipartStream(InputStream input, byte[] boundary) {
        this(input, boundary, 4096, null);
    }

    public String getHeaderEncoding() {
        return this.headerEncoding;
    }

    public void setHeaderEncoding(String encoding) {
        this.headerEncoding = encoding;
    }

    public byte readByte() throws IOException {
        if (this.head == this.tail) {
            this.head = 0;
            this.tail = this.input.read(this.buffer, this.head, this.bufSize);
            if (this.tail == -1) {
                throw new IOException("No more data is available");
            }
            if (this.notifier != null) {
                this.notifier.noteBytesRead(this.tail);
            }
        }
        return this.buffer[this.head++];
    }

    public boolean readBoundary() throws FileUploadBase.FileUploadIOException, MalformedStreamException {
        boolean nextChunk;
        block6: {
            byte[] marker = new byte[2];
            nextChunk = false;
            this.head += this.boundaryLength;
            try {
                marker[0] = this.readByte();
                if (marker[0] == 10) {
                    return true;
                }
                marker[1] = this.readByte();
                if (MultipartStream.arrayequals(marker, STREAM_TERMINATOR, 2)) {
                    nextChunk = false;
                    break block6;
                }
                if (MultipartStream.arrayequals(marker, FIELD_SEPARATOR, 2)) {
                    nextChunk = true;
                    break block6;
                }
                throw new MalformedStreamException("Unexpected characters follow a boundary");
            }
            catch (FileUploadBase.FileUploadIOException e) {
                throw e;
            }
            catch (IOException e) {
                throw new MalformedStreamException("Stream ended unexpectedly");
            }
        }
        return nextChunk;
    }

    public void setBoundary(byte[] boundary) throws IllegalBoundaryException {
        if (boundary.length != this.boundaryLength - BOUNDARY_PREFIX.length) {
            throw new IllegalBoundaryException("The length of a boundary token cannot be changed");
        }
        System.arraycopy(boundary, 0, this.boundary, BOUNDARY_PREFIX.length, boundary.length);
        this.computeBoundaryTable();
    }

    private void computeBoundaryTable() {
        int position = 2;
        int candidate = 0;
        this.boundaryTable[0] = -1;
        this.boundaryTable[1] = 0;
        while (position <= this.boundaryLength) {
            if (this.boundary[position - 1] == this.boundary[candidate]) {
                this.boundaryTable[position] = candidate + 1;
                ++candidate;
                ++position;
                continue;
            }
            if (candidate > 0) {
                candidate = this.boundaryTable[candidate];
                continue;
            }
            this.boundaryTable[position] = 0;
            ++position;
        }
    }

    public String readHeaders() throws FileUploadBase.FileUploadIOException, MalformedStreamException {
        int i = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int size = 0;
        while (i < HEADER_SEPARATOR.length) {
            byte b;
            try {
                b = this.readByte();
            }
            catch (FileUploadBase.FileUploadIOException e) {
                throw e;
            }
            catch (IOException e) {
                throw new MalformedStreamException("Stream ended unexpectedly");
            }
            if (++size > 10240) {
                throw new MalformedStreamException(String.format("Header section has more than %s bytes (maybe it is not properly terminated)", 10240));
            }
            i = b == HEADER_SEPARATOR[i] ? ++i : 0;
            baos.write(b);
        }
        String headers = null;
        if (this.headerEncoding != null) {
            try {
                headers = baos.toString(this.headerEncoding);
            }
            catch (UnsupportedEncodingException e) {
                headers = baos.toString();
            }
        } else {
            headers = baos.toString();
        }
        return headers;
    }

    public int readBodyData(OutputStream output) throws MalformedStreamException, IOException {
        return (int)Streams.copy(this.newInputStream(), output, false);
    }

    ItemInputStream newInputStream() {
        return new ItemInputStream();
    }

    public int discardBodyData() throws MalformedStreamException, IOException {
        return this.readBodyData(null);
    }

    public boolean skipPreamble() throws IOException {
        System.arraycopy(this.boundary, 2, this.boundary, 0, this.boundary.length - 2);
        this.boundaryLength = this.boundary.length - 2;
        this.computeBoundaryTable();
        try {
            this.discardBodyData();
            boolean bl = this.readBoundary();
            return bl;
        }
        catch (MalformedStreamException e) {
            boolean bl = false;
            return bl;
        }
        finally {
            System.arraycopy(this.boundary, 0, this.boundary, 2, this.boundary.length - 2);
            this.boundaryLength = this.boundary.length;
            this.boundary[0] = 13;
            this.boundary[1] = 10;
            this.computeBoundaryTable();
        }
    }

    public static boolean arrayequals(byte[] a, byte[] b, int count) {
        for (int i = 0; i < count; ++i) {
            if (a[i] == b[i]) continue;
            return false;
        }
        return true;
    }

    protected int findByte(byte value, int pos) {
        for (int i = pos; i < this.tail; ++i) {
            if (this.buffer[i] != value) continue;
            return i;
        }
        return -1;
    }

    protected int findSeparator() {
        int tablePos = 0;
        for (int bufferPos = this.head; bufferPos < this.tail; ++bufferPos) {
            while (tablePos >= 0 && this.buffer[bufferPos] != this.boundary[tablePos]) {
                tablePos = this.boundaryTable[tablePos];
            }
            if (++tablePos != this.boundaryLength) continue;
            return bufferPos - this.boundaryLength;
        }
        return -1;
    }

    public class ItemInputStream
    extends InputStream
    implements Closeable {
        private long total;
        private int pad;
        private int pos;
        private boolean closed;
        private static final int BYTE_POSITIVE_OFFSET = 256;

        ItemInputStream() {
            this.findSeparator();
        }

        private void findSeparator() {
            this.pos = MultipartStream.this.findSeparator();
            if (this.pos == -1) {
                this.pad = MultipartStream.this.tail - MultipartStream.this.head > MultipartStream.this.keepRegion ? MultipartStream.this.keepRegion : MultipartStream.this.tail - MultipartStream.this.head;
            }
        }

        public long getBytesRead() {
            return this.total;
        }

        @Override
        public int available() throws IOException {
            if (this.pos == -1) {
                return MultipartStream.this.tail - MultipartStream.this.head - this.pad;
            }
            return this.pos - MultipartStream.this.head;
        }

        @Override
        public int read() throws IOException {
            if (this.closed) {
                throw new FileItemStream.ItemSkippedException();
            }
            if (this.available() == 0 && this.makeAvailable() == 0) {
                return -1;
            }
            ++this.total;
            byte b = MultipartStream.this.buffer[MultipartStream.this.head++];
            if (b >= 0) {
                return b;
            }
            return b + 256;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (this.closed) {
                throw new FileItemStream.ItemSkippedException();
            }
            if (len == 0) {
                return 0;
            }
            int res = this.available();
            if (res == 0 && (res = this.makeAvailable()) == 0) {
                return -1;
            }
            res = Math.min(res, len);
            System.arraycopy(MultipartStream.this.buffer, MultipartStream.this.head, b, off, res);
            MultipartStream.this.head = MultipartStream.this.head + res;
            this.total += (long)res;
            return res;
        }

        @Override
        public void close() throws IOException {
            this.close(false);
        }

        public void close(boolean pCloseUnderlying) throws IOException {
            if (this.closed) {
                return;
            }
            if (pCloseUnderlying) {
                this.closed = true;
                MultipartStream.this.input.close();
            } else {
                int av;
                while ((av = this.available()) != 0 || (av = this.makeAvailable()) != 0) {
                    this.skip(av);
                }
            }
            this.closed = true;
        }

        @Override
        public long skip(long bytes) throws IOException {
            if (this.closed) {
                throw new FileItemStream.ItemSkippedException();
            }
            int av = this.available();
            if (av == 0 && (av = this.makeAvailable()) == 0) {
                return 0L;
            }
            long res = Math.min((long)av, bytes);
            MultipartStream.this.head = (int)((long)MultipartStream.this.head + res);
            return res;
        }

        private int makeAvailable() throws IOException {
            int av;
            if (this.pos != -1) {
                return 0;
            }
            this.total += (long)(MultipartStream.this.tail - MultipartStream.this.head - this.pad);
            System.arraycopy(MultipartStream.this.buffer, MultipartStream.this.tail - this.pad, MultipartStream.this.buffer, 0, this.pad);
            MultipartStream.this.head = 0;
            MultipartStream.this.tail = this.pad;
            do {
                int bytesRead;
                if ((bytesRead = MultipartStream.this.input.read(MultipartStream.this.buffer, MultipartStream.this.tail, MultipartStream.this.bufSize - MultipartStream.this.tail)) == -1) {
                    String msg = "Stream ended unexpectedly";
                    throw new MalformedStreamException("Stream ended unexpectedly");
                }
                if (MultipartStream.this.notifier != null) {
                    MultipartStream.this.notifier.noteBytesRead(bytesRead);
                }
                MultipartStream.this.tail = MultipartStream.this.tail + bytesRead;
                this.findSeparator();
            } while ((av = this.available()) <= 0 && this.pos == -1);
            return av;
        }

        @Override
        public boolean isClosed() {
            return this.closed;
        }
    }

    public static class IllegalBoundaryException
    extends IOException {
        private static final long serialVersionUID = -161533165102632918L;

        public IllegalBoundaryException() {
        }

        public IllegalBoundaryException(String message) {
            super(message);
        }
    }

    public static class MalformedStreamException
    extends IOException {
        private static final long serialVersionUID = 6466926458059796677L;

        public MalformedStreamException() {
        }

        public MalformedStreamException(String message) {
            super(message);
        }
    }

    public static class ProgressNotifier {
        private final ProgressListener listener;
        private final long contentLength;
        private long bytesRead;
        private int items;

        ProgressNotifier(ProgressListener pListener, long pContentLength) {
            this.listener = pListener;
            this.contentLength = pContentLength;
        }

        void noteBytesRead(int pBytes) {
            this.bytesRead += (long)pBytes;
            this.notifyListener();
        }

        void noteItem() {
            ++this.items;
            this.notifyListener();
        }

        private void notifyListener() {
            if (this.listener != null) {
                this.listener.update(this.bytesRead, this.contentLength, this.items);
            }
        }
    }
}

