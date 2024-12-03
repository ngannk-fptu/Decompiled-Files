/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import net.sourceforge.jtds.jdbc.CharsetInfo;
import net.sourceforge.jtds.jdbc.SharedSocket;
import net.sourceforge.jtds.util.Logger;

public class ResponseStream {
    private final SharedSocket socket;
    private byte[] buffer;
    private int bufferPtr;
    private int bufferLen;
    private final SharedSocket.VirtualSocket _VirtualSocket;
    private boolean isClosed;
    private final byte[] byteBuffer = new byte[255];
    private final char[] charBuffer = new char[255];

    ResponseStream(SharedSocket socket, SharedSocket.VirtualSocket vsock, int bufferSize) {
        this._VirtualSocket = vsock;
        this.socket = socket;
        this.buffer = new byte[bufferSize];
        this.bufferLen = bufferSize;
        this.bufferPtr = bufferSize;
    }

    SharedSocket.VirtualSocket getVirtualSocket() {
        return this._VirtualSocket;
    }

    int peek() throws IOException {
        int b = this.read();
        --this.bufferPtr;
        return b;
    }

    int read() throws IOException {
        if (this.bufferPtr >= this.bufferLen) {
            this.getPacket();
        }
        return this.buffer[this.bufferPtr++] & 0xFF;
    }

    int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    int read(byte[] b, int off, int len) throws IOException {
        int bytesToRead = len;
        while (bytesToRead > 0) {
            int available;
            if (this.bufferPtr >= this.bufferLen) {
                this.getPacket();
            }
            int bc = (available = this.bufferLen - this.bufferPtr) > bytesToRead ? bytesToRead : available;
            System.arraycopy(this.buffer, this.bufferPtr, b, off, bc);
            off += bc;
            bytesToRead -= bc;
            this.bufferPtr += bc;
        }
        return len;
    }

    int read(char[] c) throws IOException {
        for (int i = 0; i < c.length; ++i) {
            if (this.bufferPtr >= this.bufferLen) {
                this.getPacket();
            }
            int b1 = this.buffer[this.bufferPtr++] & 0xFF;
            if (this.bufferPtr >= this.bufferLen) {
                this.getPacket();
            }
            int b2 = this.buffer[this.bufferPtr++] << 8;
            c[i] = (char)(b2 | b1);
        }
        return c.length;
    }

    String readString(int len) throws IOException {
        if (this.socket.getTdsVersion() >= 3) {
            return this.readUnicodeString(len);
        }
        return this.readNonUnicodeString(len);
    }

    void skipString(int len) throws IOException {
        if (len <= 0) {
            return;
        }
        if (this.socket.getTdsVersion() >= 3) {
            this.skip(len * 2);
        } else {
            this.skip(len);
        }
    }

    String readUnicodeString(int len) throws IOException {
        char[] chars = len > this.charBuffer.length ? new char[len] : this.charBuffer;
        for (int i = 0; i < len; ++i) {
            if (this.bufferPtr >= this.bufferLen) {
                this.getPacket();
            }
            int b1 = this.buffer[this.bufferPtr++] & 0xFF;
            if (this.bufferPtr >= this.bufferLen) {
                this.getPacket();
            }
            int b2 = this.buffer[this.bufferPtr++] << 8;
            chars[i] = (char)(b2 | b1);
        }
        return new String(chars, 0, len);
    }

    String readNonUnicodeString(int len) throws IOException {
        CharsetInfo info = this.socket.getCharsetInfo();
        return this.readString(len, info);
    }

    String readNonUnicodeString(int len, CharsetInfo charsetInfo) throws IOException {
        return this.readString(len, charsetInfo);
    }

    String readString(int len, CharsetInfo info) throws IOException {
        String charsetName = info.getCharset();
        byte[] bytes = len > this.byteBuffer.length ? new byte[len] : this.byteBuffer;
        this.read(bytes, 0, len);
        try {
            return new String(bytes, 0, len, charsetName);
        }
        catch (UnsupportedEncodingException e) {
            return new String(bytes, 0, len);
        }
    }

    short readShort() throws IOException {
        int b1 = this.read();
        return (short)(b1 | this.read() << 8);
    }

    int readInt() throws IOException {
        int b1 = this.read();
        int b2 = this.read() << 8;
        int b3 = this.read() << 16;
        int b4 = this.read() << 24;
        return b4 | b3 | b2 | b1;
    }

    long readLong() throws IOException {
        long b1 = this.read();
        long b2 = (long)this.read() << 8;
        long b3 = (long)this.read() << 16;
        long b4 = (long)this.read() << 24;
        long b5 = (long)this.read() << 32;
        long b6 = (long)this.read() << 40;
        long b7 = (long)this.read() << 48;
        long b8 = (long)this.read() << 56;
        return b1 | b2 | b3 | b4 | b5 | b6 | b7 | b8;
    }

    BigDecimal readUnsignedLong() throws IOException {
        int b1 = this.read() & 0xFF;
        long b2 = this.read();
        long b3 = (long)this.read() << 8;
        long b4 = (long)this.read() << 16;
        long b5 = (long)this.read() << 24;
        long b6 = (long)this.read() << 32;
        long b7 = (long)this.read() << 40;
        long b8 = (long)this.read() << 48;
        return new BigDecimal(Long.toString(b2 | b3 | b4 | b5 | b6 | b7 | b8)).multiply(new BigDecimal(256)).add(new BigDecimal(b1));
    }

    int skip(int skip) throws IOException {
        int tmp = skip;
        while (skip > 0) {
            int available;
            if (this.bufferPtr >= this.bufferLen) {
                this.getPacket();
            }
            if (skip > (available = this.bufferLen - this.bufferPtr)) {
                skip -= available;
                this.bufferPtr = this.bufferLen;
                continue;
            }
            this.bufferPtr += skip;
            skip = 0;
        }
        return tmp;
    }

    void skipToEnd() {
        try {
            this.bufferPtr = this.bufferLen;
            while (true) {
                this.buffer = this.socket.getNetPacket(this._VirtualSocket, this.buffer);
            }
        }
        catch (IOException iOException) {
            return;
        }
    }

    void close() {
        this.isClosed = true;
        this.socket.closeStream(this._VirtualSocket);
    }

    int getTdsVersion() {
        return this.socket.getTdsVersion();
    }

    int getServerType() {
        return this.socket.serverType;
    }

    InputStream getInputStream(int len) {
        return new TdsInputStream(this, len);
    }

    private void getPacket() throws IOException {
        while (this.bufferPtr >= this.bufferLen) {
            if (this.isClosed) {
                throw new IOException("ResponseStream is closed");
            }
            this.buffer = this.socket.getNetPacket(this._VirtualSocket, this.buffer);
            this.bufferLen = (this.buffer[2] & 0xFF) << 8 | this.buffer[3] & 0xFF;
            this.bufferPtr = 8;
            if (!Logger.isActive()) continue;
            Logger.logPacket(this._VirtualSocket.id, true, this.buffer);
        }
    }

    private static class TdsInputStream
    extends InputStream {
        ResponseStream tds;
        int maxLen;

        public TdsInputStream(ResponseStream tds, int maxLen) {
            this.tds = tds;
            this.maxLen = maxLen;
        }

        @Override
        public int read() throws IOException {
            return this.maxLen-- > 0 ? this.tds.read() : -1;
        }

        @Override
        public int read(byte[] bytes, int offset, int len) throws IOException {
            if (this.maxLen < 1) {
                return -1;
            }
            int bc = Math.min(this.maxLen, len);
            if (bc > 0) {
                this.maxLen -= (bc = this.tds.read(bytes, offset, bc)) == -1 ? 0 : bc;
            }
            return bc;
        }
    }
}

