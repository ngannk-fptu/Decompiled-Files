/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import net.sourceforge.jtds.jdbc.SharedSocket;
import net.sourceforge.jtds.jdbc.Support;
import net.sourceforge.jtds.util.Logger;

public class RequestStream {
    private final SharedSocket socket;
    private byte[] buffer;
    private int bufferPtr;
    private byte pktType;
    private final SharedSocket.VirtualSocket _VirtualSocket;
    private boolean isClosed;
    private final int bufferSize;
    private final int maxPrecision;

    RequestStream(SharedSocket socket, SharedSocket.VirtualSocket vsock, int bufferSize, int maxPrecision) {
        this._VirtualSocket = vsock;
        this.socket = socket;
        this.bufferSize = bufferSize;
        this.buffer = new byte[bufferSize];
        this.bufferPtr = 8;
        this.maxPrecision = maxPrecision;
    }

    void setBufferSize(int size) {
        if (size < this.bufferPtr || size == this.bufferSize) {
            return;
        }
        if (size < 512 || size > 32768) {
            throw new IllegalArgumentException("Invalid buffer size parameter " + size);
        }
        byte[] tmp = new byte[size];
        System.arraycopy(this.buffer, 0, tmp, 0, this.bufferPtr);
        this.buffer = tmp;
    }

    int getBufferSize() {
        return this.bufferSize;
    }

    int getMaxPrecision() {
        return this.maxPrecision;
    }

    byte getMaxDecimalBytes() {
        return (byte)(this.maxPrecision <= 28 ? 13 : 17);
    }

    SharedSocket.VirtualSocket getVirtualSocket() {
        return this._VirtualSocket;
    }

    void setPacketType(byte pktType) {
        this.pktType = pktType;
    }

    void write(byte b) throws IOException {
        if (this.bufferPtr == this.buffer.length) {
            this.putPacket(0);
        }
        this.buffer[this.bufferPtr++] = b;
    }

    void write(byte[] b) throws IOException {
        int bytesToWrite = b.length;
        int off = 0;
        while (bytesToWrite > 0) {
            int available = this.buffer.length - this.bufferPtr;
            if (available == 0) {
                this.putPacket(0);
                continue;
            }
            int bc = available > bytesToWrite ? bytesToWrite : available;
            System.arraycopy(b, off, this.buffer, this.bufferPtr, bc);
            off += bc;
            this.bufferPtr += bc;
            bytesToWrite -= bc;
        }
    }

    void write(byte[] b, int off, int len) throws IOException {
        int limit = off + len > b.length ? b.length : off + len;
        int bytesToWrite = limit - off;
        int i = len - bytesToWrite;
        while (bytesToWrite > 0) {
            int available = this.buffer.length - this.bufferPtr;
            if (available == 0) {
                this.putPacket(0);
                continue;
            }
            int bc = available > bytesToWrite ? bytesToWrite : available;
            System.arraycopy(b, off, this.buffer, this.bufferPtr, bc);
            off += bc;
            this.bufferPtr += bc;
            bytesToWrite -= bc;
        }
        while (i > 0) {
            this.write((byte)0);
            --i;
        }
    }

    void write(int i) throws IOException {
        this.write((byte)i);
        this.write((byte)(i >> 8));
        this.write((byte)(i >> 16));
        this.write((byte)(i >> 24));
    }

    void write(short s) throws IOException {
        this.write((byte)s);
        this.write((byte)(s >> 8));
    }

    void write(long l) throws IOException {
        this.write((byte)l);
        this.write((byte)(l >> 8));
        this.write((byte)(l >> 16));
        this.write((byte)(l >> 24));
        this.write((byte)(l >> 32));
        this.write((byte)(l >> 40));
        this.write((byte)(l >> 48));
        this.write((byte)(l >> 56));
    }

    void write(double f) throws IOException {
        long l = Double.doubleToLongBits(f);
        this.write((byte)l);
        this.write((byte)(l >> 8));
        this.write((byte)(l >> 16));
        this.write((byte)(l >> 24));
        this.write((byte)(l >> 32));
        this.write((byte)(l >> 40));
        this.write((byte)(l >> 48));
        this.write((byte)(l >> 56));
    }

    void write(float f) throws IOException {
        int l = Float.floatToIntBits(f);
        this.write((byte)l);
        this.write((byte)(l >> 8));
        this.write((byte)(l >> 16));
        this.write((byte)(l >> 24));
    }

    void write(String s) throws IOException {
        if (this.socket.getTdsVersion() >= 3) {
            int len = s.length();
            for (int i = 0; i < len; ++i) {
                char c = s.charAt(i);
                if (this.bufferPtr == this.buffer.length) {
                    this.putPacket(0);
                }
                this.buffer[this.bufferPtr++] = (byte)c;
                if (this.bufferPtr == this.buffer.length) {
                    this.putPacket(0);
                }
                this.buffer[this.bufferPtr++] = (byte)(c >> 8);
            }
        } else {
            this.writeAscii(s);
        }
    }

    void write(char[] s, int off, int len) throws IOException {
        int limit;
        int n = limit = off + len > s.length ? s.length : off + len;
        for (int i = off; i < limit; ++i) {
            char c = s[i];
            if (this.bufferPtr == this.buffer.length) {
                this.putPacket(0);
            }
            this.buffer[this.bufferPtr++] = (byte)c;
            if (this.bufferPtr == this.buffer.length) {
                this.putPacket(0);
            }
            this.buffer[this.bufferPtr++] = (byte)(c >> 8);
        }
    }

    void writeAscii(String s) throws IOException {
        String charsetName = this.socket.getCharset();
        if (charsetName != null) {
            try {
                this.write(s.getBytes(charsetName));
            }
            catch (UnsupportedEncodingException e) {
                this.write(s.getBytes());
            }
        } else {
            this.write(s.getBytes());
        }
    }

    void writeStreamBytes(InputStream in, int length) throws IOException {
        byte[] buffer = new byte[1024];
        while (length > 0) {
            int res = in.read(buffer);
            if (res < 0) {
                throw new IOException("Data in stream less than specified by length");
            }
            this.write(buffer, 0, res);
            length -= res;
        }
        if (length < 0 || in.read() >= 0) {
            throw new IOException("More data in stream than specified by length");
        }
    }

    void writeReaderChars(Reader in, int length) throws IOException {
        char[] cbuffer = new char[512];
        byte[] bbuffer = new byte[1024];
        while (length > 0) {
            int res = in.read(cbuffer);
            if (res < 0) {
                throw new IOException("Data in stream less than specified by length");
            }
            int j = -1;
            for (int i = 0; i < res; ++i) {
                bbuffer[++j] = (byte)cbuffer[i];
                bbuffer[++j] = (byte)(cbuffer[i] >> 8);
            }
            this.write(bbuffer, 0, res * 2);
            length -= res;
        }
        if (length < 0 || in.read() >= 0) {
            throw new IOException("More data in stream than specified by length");
        }
    }

    void writeReaderBytes(Reader in, int length) throws IOException {
        int result;
        char[] buffer = new char[1024];
        for (int i = 0; i < length; i += result) {
            result = in.read(buffer);
            if (result == -1) {
                throw new IOException("Data in stream less than specified by length");
            }
            if (i + result > length) {
                throw new IOException("More data in stream than specified by length");
            }
            this.write(Support.encodeString(this.socket.getCharset(), new String(buffer, 0, result)));
        }
    }

    void write(BigDecimal value) throws IOException {
        if (value == null) {
            this.write((byte)0);
        } else {
            byte signum = (byte)(value.signum() >= 0 ? 1 : 0);
            BigInteger bi = value.unscaledValue();
            byte[] mantisse = bi.abs().toByteArray();
            byte len = (byte)(mantisse.length + 1);
            if (len > this.getMaxDecimalBytes()) {
                throw new IOException("BigDecimal to big to send");
            }
            if (this.socket.serverType == 2) {
                this.write(len);
                this.write((byte)(signum == 0 ? 1 : 0));
                for (int i = 0; i < mantisse.length; ++i) {
                    this.write(mantisse[i]);
                }
            } else {
                this.write(len);
                this.write(signum);
                for (int i = mantisse.length - 1; i >= 0; --i) {
                    this.write(mantisse[i]);
                }
            }
        }
    }

    void flush() throws IOException {
        this.putPacket(1);
    }

    void close() {
        this.isClosed = true;
    }

    int getTdsVersion() {
        return this.socket.getTdsVersion();
    }

    int getServerType() {
        return this.socket.serverType;
    }

    private void putPacket(int last) throws IOException {
        if (this.isClosed) {
            throw new IOException("RequestStream is closed");
        }
        this.buffer[0] = this.pktType;
        this.buffer[1] = (byte)last;
        this.buffer[2] = (byte)(this.bufferPtr >> 8);
        this.buffer[3] = (byte)this.bufferPtr;
        this.buffer[4] = 0;
        this.buffer[5] = 0;
        this.buffer[6] = (byte)(this.socket.getTdsVersion() >= 3 ? 1 : 0);
        this.buffer[7] = 0;
        if (Logger.isActive()) {
            Logger.logPacket(this._VirtualSocket.id, false, this.buffer);
        }
        this.buffer = this.socket.sendNetPacket(this._VirtualSocket, this.buffer);
        this.bufferPtr = 8;
    }
}

