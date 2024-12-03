/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.MappedRandomAccessFile;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class RandomAccessFileOrArray
implements DataInput,
Closeable {
    MappedRandomAccessFile rf;
    RandomAccessFile trf;
    boolean plainRandomAccess;
    String filename;
    byte[] arrayIn;
    int arrayInPtr;
    byte back;
    boolean isBack = false;
    private int startOffset = 0;

    public RandomAccessFileOrArray(String filename) throws IOException {
        this(filename, false, Document.plainRandomAccess);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public RandomAccessFileOrArray(String filename, boolean forceRead, boolean plainRandomAccess) throws IOException {
        this.plainRandomAccess = plainRandomAccess;
        File file = new File(filename);
        if (!file.exists() && FontFactory.isRegistered(filename)) {
            filename = (String)FontFactory.getFontImp().getFontPath(filename);
            file = new File(filename);
        }
        if (!file.canRead()) {
            if (filename.startsWith("file:/") || filename.startsWith("http://") || filename.startsWith("https://") || filename.startsWith("jar:") || filename.startsWith("wsjar:")) {
                try (InputStream is = new URL(filename).openStream();){
                    this.arrayIn = RandomAccessFileOrArray.InputStreamToArray(is);
                    return;
                }
            }
            InputStream is = null;
            is = "-".equals(filename) ? System.in : BaseFont.getResourceStream(filename);
            if (is == null) {
                throw new IOException(MessageLocalization.getComposedMessage("1.not.found.as.file.or.resource", filename));
            }
            try {
                this.arrayIn = RandomAccessFileOrArray.InputStreamToArray(is);
                return;
            }
            finally {
                try {
                    is.close();
                }
                catch (IOException iOException) {}
            }
        }
        if (forceRead) {
            try (FileInputStream s = new FileInputStream(file);){
                this.arrayIn = RandomAccessFileOrArray.InputStreamToArray(s);
            }
            return;
        }
        this.filename = filename;
        if (plainRandomAccess) {
            this.trf = new RandomAccessFile(filename, "r");
        } else {
            this.rf = new MappedRandomAccessFile(filename, "r");
        }
    }

    public RandomAccessFileOrArray(URL url) throws IOException {
        try (InputStream is = url.openStream();){
            this.arrayIn = RandomAccessFileOrArray.InputStreamToArray(is);
        }
    }

    public RandomAccessFileOrArray(InputStream is) throws IOException {
        this.arrayIn = RandomAccessFileOrArray.InputStreamToArray(is);
    }

    public static byte[] InputStreamToArray(InputStream is) throws IOException {
        int read;
        byte[] b = new byte[8192];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while ((read = is.read(b)) >= 1) {
            out.write(b, 0, read);
        }
        out.close();
        return out.toByteArray();
    }

    public RandomAccessFileOrArray(byte[] arrayIn) {
        this.arrayIn = arrayIn;
    }

    public RandomAccessFileOrArray(RandomAccessFileOrArray file) {
        this.filename = file.filename;
        this.arrayIn = file.arrayIn;
        this.startOffset = file.startOffset;
        this.plainRandomAccess = file.plainRandomAccess;
    }

    public void pushBack(byte b) {
        this.back = b;
        this.isBack = true;
    }

    public int read() throws IOException {
        if (this.isBack) {
            this.isBack = false;
            return this.back & 0xFF;
        }
        if (this.arrayIn == null) {
            return this.plainRandomAccess ? this.trf.read() : this.rf.read();
        }
        if (this.arrayInPtr >= this.arrayIn.length) {
            return -1;
        }
        return this.arrayIn[this.arrayInPtr++] & 0xFF;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        int n = 0;
        if (this.isBack) {
            this.isBack = false;
            if (len == 1) {
                b[off] = this.back;
                return 1;
            }
            n = 1;
            b[off++] = this.back;
            --len;
        }
        if (this.arrayIn == null) {
            return (this.plainRandomAccess ? this.trf.read(b, off, len) : this.rf.read(b, off, len)) + n;
        }
        if (this.arrayInPtr >= this.arrayIn.length) {
            return -1;
        }
        if (this.arrayInPtr + len > this.arrayIn.length) {
            len = this.arrayIn.length - this.arrayInPtr;
        }
        System.arraycopy(this.arrayIn, this.arrayInPtr, b, off, len);
        this.arrayInPtr += len;
        return len + n;
    }

    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        this.readFully(b, 0, b.length);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        int count;
        int n = 0;
        do {
            if ((count = this.read(b, off + n, len - n)) >= 0) continue;
            throw new EOFException();
        } while ((n += count) < len);
    }

    public long skip(long n) throws IOException {
        return this.skipBytes((int)n);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        int len;
        int pos;
        int newpos;
        if (n <= 0) {
            return 0;
        }
        int adj = 0;
        if (this.isBack) {
            this.isBack = false;
            if (n == 1) {
                return 1;
            }
            --n;
            adj = 1;
        }
        if ((newpos = (pos = this.getFilePointer()) + n) > (len = this.length())) {
            newpos = len;
        }
        this.seek(newpos);
        return newpos - pos + adj;
    }

    public void reOpen() throws IOException {
        if (this.filename != null && this.rf == null && this.trf == null) {
            if (this.plainRandomAccess) {
                this.trf = new RandomAccessFile(this.filename, "r");
            } else {
                this.rf = new MappedRandomAccessFile(this.filename, "r");
            }
        }
        this.seek(0);
    }

    protected void insureOpen() throws IOException {
        if (this.filename != null && this.rf == null && this.trf == null) {
            this.reOpen();
        }
    }

    public boolean isOpen() {
        return this.filename == null || this.rf != null || this.trf != null;
    }

    @Override
    public void close() throws IOException {
        this.isBack = false;
        if (this.rf != null) {
            this.rf.close();
            this.rf = null;
            this.plainRandomAccess = true;
        } else if (this.trf != null) {
            this.trf.close();
            this.trf = null;
        }
    }

    public int length() throws IOException {
        if (this.arrayIn == null) {
            this.insureOpen();
            return (int)(this.plainRandomAccess ? this.trf.length() : this.rf.length()) - this.startOffset;
        }
        return this.arrayIn.length - this.startOffset;
    }

    public void seek(int pos) throws IOException {
        pos += this.startOffset;
        this.isBack = false;
        if (this.arrayIn == null) {
            this.insureOpen();
            if (this.plainRandomAccess) {
                this.trf.seek(pos);
            } else {
                this.rf.seek(pos);
            }
        } else {
            this.arrayInPtr = pos;
        }
    }

    public void seek(long pos) throws IOException {
        this.seek((int)pos);
    }

    public int getFilePointer() throws IOException {
        int n;
        this.insureOpen();
        int n2 = n = this.isBack ? 1 : 0;
        if (this.arrayIn == null) {
            return (int)(this.plainRandomAccess ? this.trf.getFilePointer() : this.rf.getFilePointer()) - n - this.startOffset;
        }
        return this.arrayInPtr - n - this.startOffset;
    }

    @Override
    public boolean readBoolean() throws IOException {
        int ch = this.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return ch != 0;
    }

    @Override
    public byte readByte() throws IOException {
        int ch = this.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return (byte)ch;
    }

    @Override
    public int readUnsignedByte() throws IOException {
        int ch = this.read();
        if (ch < 0) {
            throw new EOFException();
        }
        return ch;
    }

    @Override
    public short readShort() throws IOException {
        int ch2;
        int ch1 = this.read();
        if ((ch1 | (ch2 = this.read())) < 0) {
            throw new EOFException();
        }
        return (short)((ch1 << 8) + ch2);
    }

    public final short readShortLE() throws IOException {
        int ch2;
        int ch1 = this.read();
        if ((ch1 | (ch2 = this.read())) < 0) {
            throw new EOFException();
        }
        return (short)((ch2 << 8) + (ch1 << 0));
    }

    @Override
    public int readUnsignedShort() throws IOException {
        int ch2;
        int ch1 = this.read();
        if ((ch1 | (ch2 = this.read())) < 0) {
            throw new EOFException();
        }
        return (ch1 << 8) + ch2;
    }

    public final int readUnsignedShortLE() throws IOException {
        int ch2;
        int ch1 = this.read();
        if ((ch1 | (ch2 = this.read())) < 0) {
            throw new EOFException();
        }
        return (ch2 << 8) + (ch1 << 0);
    }

    @Override
    public char readChar() throws IOException {
        int ch2;
        int ch1 = this.read();
        if ((ch1 | (ch2 = this.read())) < 0) {
            throw new EOFException();
        }
        return (char)((ch1 << 8) + ch2);
    }

    public final char readCharLE() throws IOException {
        int ch2;
        int ch1 = this.read();
        if ((ch1 | (ch2 = this.read())) < 0) {
            throw new EOFException();
        }
        return (char)((ch2 << 8) + (ch1 << 0));
    }

    @Override
    public int readInt() throws IOException {
        int ch4;
        int ch3;
        int ch2;
        int ch1 = this.read();
        if ((ch1 | (ch2 = this.read()) | (ch3 = this.read()) | (ch4 = this.read())) < 0) {
            throw new EOFException();
        }
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4;
    }

    public final int readIntLE() throws IOException {
        int ch4;
        int ch3;
        int ch2;
        int ch1 = this.read();
        if ((ch1 | (ch2 = this.read()) | (ch3 = this.read()) | (ch4 = this.read())) < 0) {
            throw new EOFException();
        }
        return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
    }

    public final long readUnsignedInt() throws IOException {
        long ch4;
        long ch3;
        long ch2;
        long ch1 = this.read();
        if ((ch1 | (ch2 = (long)this.read()) | (ch3 = (long)this.read()) | (ch4 = (long)this.read())) < 0L) {
            throw new EOFException();
        }
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
    }

    public final long readUnsignedIntLE() throws IOException {
        long ch4;
        long ch3;
        long ch2;
        long ch1 = this.read();
        if ((ch1 | (ch2 = (long)this.read()) | (ch3 = (long)this.read()) | (ch4 = (long)this.read())) < 0L) {
            throw new EOFException();
        }
        return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
    }

    @Override
    public long readLong() throws IOException {
        return ((long)this.readInt() << 32) + ((long)this.readInt() & 0xFFFFFFFFL);
    }

    public final long readLongLE() throws IOException {
        int i1 = this.readIntLE();
        int i2 = this.readIntLE();
        return ((long)i2 << 32) + ((long)i1 & 0xFFFFFFFFL);
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }

    public final float readFloatLE() throws IOException {
        return Float.intBitsToFloat(this.readIntLE());
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }

    public final double readDoubleLE() throws IOException {
        return Double.longBitsToDouble(this.readLongLE());
    }

    @Override
    public String readLine() throws IOException {
        StringBuilder input = new StringBuilder();
        int c = -1;
        boolean eol = false;
        block4: while (!eol) {
            c = this.read();
            switch (c) {
                case -1: 
                case 10: {
                    eol = true;
                    continue block4;
                }
                case 13: {
                    eol = true;
                    int cur = this.getFilePointer();
                    if (this.read() == 10) continue block4;
                    this.seek(cur);
                    continue block4;
                }
            }
            input.append((char)c);
        }
        if (c == -1 && input.length() == 0) {
            return null;
        }
        return input.toString();
    }

    @Override
    public String readUTF() throws IOException {
        return DataInputStream.readUTF(this);
    }

    public int getStartOffset() {
        return this.startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public ByteBuffer getNioByteBuffer() throws IOException {
        if (this.filename != null) {
            FileChannel channel = this.plainRandomAccess ? this.trf.getChannel() : this.rf.getChannel();
            return channel.map(FileChannel.MapMode.READ_ONLY, 0L, channel.size());
        }
        return ByteBuffer.wrap(this.arrayIn);
    }
}

