/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.catalina.tribes.io.BufferPool;
import org.apache.catalina.tribes.io.ChannelData;
import org.apache.catalina.tribes.io.ReplicationStream;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class XByteBuffer
implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(XByteBuffer.class);
    protected static final StringManager sm = StringManager.getManager(XByteBuffer.class);
    private static final byte[] START_DATA = new byte[]{70, 76, 84, 50, 48, 48, 50};
    private static final byte[] END_DATA = new byte[]{84, 76, 70, 50, 48, 48, 51};
    protected byte[] buf = null;
    protected int bufSize = 0;
    protected boolean discard = true;
    private static final AtomicInteger invokecount = new AtomicInteger(0);

    public XByteBuffer(int size, boolean discard) {
        this.buf = new byte[size];
        this.discard = discard;
    }

    public XByteBuffer(byte[] data, boolean discard) {
        this(data, data.length + 128, discard);
    }

    public XByteBuffer(byte[] data, int size, boolean discard) {
        int length = Math.max(data.length, size);
        this.buf = new byte[length];
        System.arraycopy(data, 0, this.buf, 0, data.length);
        this.bufSize = data.length;
        this.discard = discard;
    }

    public int getLength() {
        return this.bufSize;
    }

    public void setLength(int size) {
        if (size > this.buf.length) {
            throw new ArrayIndexOutOfBoundsException(sm.getString("xByteBuffer.size.larger.buffer"));
        }
        this.bufSize = size;
    }

    public void trim(int length) {
        if (this.bufSize - length < 0) {
            throw new ArrayIndexOutOfBoundsException(sm.getString("xByteBuffer.unableTrim", Integer.toString(this.bufSize), Integer.toString(length)));
        }
        this.bufSize -= length;
    }

    public void reset() {
        this.bufSize = 0;
    }

    public byte[] getBytesDirect() {
        return this.buf;
    }

    public byte[] getBytes() {
        byte[] b = new byte[this.bufSize];
        System.arraycopy(this.buf, 0, b, 0, this.bufSize);
        return b;
    }

    public void clear() {
        this.bufSize = 0;
    }

    public boolean append(ByteBuffer b, int len) {
        int newcount = this.bufSize + len;
        if (newcount > this.buf.length) {
            this.expand(newcount);
        }
        b.get(this.buf, this.bufSize, len);
        this.bufSize = newcount;
        if (this.discard && this.bufSize > START_DATA.length && XByteBuffer.firstIndexOf(this.buf, 0, START_DATA) == -1) {
            this.bufSize = 0;
            log.error((Object)sm.getString("xByteBuffer.discarded.invalidHeader"));
            return false;
        }
        return true;
    }

    public boolean append(byte i) {
        int newcount = this.bufSize + 1;
        if (newcount > this.buf.length) {
            this.expand(newcount);
        }
        this.buf[this.bufSize] = i;
        this.bufSize = newcount;
        return true;
    }

    public boolean append(boolean i) {
        int newcount = this.bufSize + 1;
        if (newcount > this.buf.length) {
            this.expand(newcount);
        }
        XByteBuffer.toBytes(i, this.buf, this.bufSize);
        this.bufSize = newcount;
        return true;
    }

    public boolean append(long i) {
        int newcount = this.bufSize + 8;
        if (newcount > this.buf.length) {
            this.expand(newcount);
        }
        XByteBuffer.toBytes(i, this.buf, this.bufSize);
        this.bufSize = newcount;
        return true;
    }

    public boolean append(int i) {
        int newcount = this.bufSize + 4;
        if (newcount > this.buf.length) {
            this.expand(newcount);
        }
        XByteBuffer.toBytes(i, this.buf, this.bufSize);
        this.bufSize = newcount;
        return true;
    }

    public boolean append(byte[] b, int off, int len) {
        if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return false;
        }
        int newcount = this.bufSize + len;
        if (newcount > this.buf.length) {
            this.expand(newcount);
        }
        System.arraycopy(b, off, this.buf, this.bufSize, len);
        this.bufSize = newcount;
        if (this.discard && this.bufSize > START_DATA.length && XByteBuffer.firstIndexOf(this.buf, 0, START_DATA) == -1) {
            this.bufSize = 0;
            log.error((Object)sm.getString("xByteBuffer.discarded.invalidHeader"));
            return false;
        }
        return true;
    }

    public void expand(int newcount) {
        byte[] newbuf = new byte[Math.max(this.buf.length << 1, newcount)];
        System.arraycopy(this.buf, 0, newbuf, 0, this.bufSize);
        this.buf = newbuf;
    }

    public int getCapacity() {
        return this.buf.length;
    }

    public int countPackages() {
        return this.countPackages(false);
    }

    public int countPackages(boolean first) {
        int newpos;
        int size;
        int index;
        int cnt = 0;
        int pos = START_DATA.length;
        int start = 0;
        while (start < this.bufSize && (index = XByteBuffer.firstIndexOf(this.buf, start, START_DATA)) == start && this.bufSize - start >= 14 && (pos = start + START_DATA.length + 4 + (size = XByteBuffer.toInt(this.buf, pos))) + END_DATA.length <= this.bufSize && (newpos = XByteBuffer.firstIndexOf(this.buf, pos, END_DATA)) == pos) {
            ++cnt;
            start = pos + END_DATA.length;
            pos = start + START_DATA.length;
            if (!first) continue;
            break;
        }
        return cnt;
    }

    public boolean doesPackageExist() {
        return this.countPackages(true) > 0;
    }

    public XByteBuffer extractDataPackage(boolean clearFromBuffer) {
        int psize = this.countPackages(true);
        if (psize == 0) {
            throw new IllegalStateException(sm.getString("xByteBuffer.no.package"));
        }
        int size = XByteBuffer.toInt(this.buf, START_DATA.length);
        XByteBuffer xbuf = BufferPool.getBufferPool().getBuffer(size, false);
        xbuf.setLength(size);
        System.arraycopy(this.buf, START_DATA.length + 4, xbuf.getBytesDirect(), 0, size);
        if (clearFromBuffer) {
            int totalsize = START_DATA.length + 4 + size + END_DATA.length;
            this.bufSize -= totalsize;
            System.arraycopy(this.buf, totalsize, this.buf, 0, this.bufSize);
        }
        return xbuf;
    }

    public ChannelData extractPackage(boolean clearFromBuffer) {
        XByteBuffer xbuf = this.extractDataPackage(clearFromBuffer);
        ChannelData cdata = ChannelData.getDataFromPackage(xbuf);
        return cdata;
    }

    public static byte[] createDataPackage(ChannelData cdata) {
        int dlength = cdata.getDataPackageLength();
        int length = XByteBuffer.getDataPackageLength(dlength);
        byte[] data = new byte[length];
        int offset = 0;
        System.arraycopy(START_DATA, 0, data, offset, START_DATA.length);
        offset += START_DATA.length;
        XByteBuffer.toBytes(dlength, data, START_DATA.length);
        cdata.getDataPackage(data, offset += 4);
        System.arraycopy(END_DATA, 0, data, offset += dlength, END_DATA.length);
        offset += END_DATA.length;
        return data;
    }

    public static byte[] createDataPackage(byte[] data, int doff, int dlength, byte[] buffer, int bufoff) {
        if (buffer.length - bufoff > XByteBuffer.getDataPackageLength(dlength)) {
            throw new ArrayIndexOutOfBoundsException(sm.getString("xByteBuffer.unableCreate"));
        }
        System.arraycopy(START_DATA, 0, buffer, bufoff, START_DATA.length);
        XByteBuffer.toBytes(data.length, buffer, bufoff + START_DATA.length);
        System.arraycopy(data, doff, buffer, bufoff + START_DATA.length + 4, dlength);
        System.arraycopy(END_DATA, 0, buffer, bufoff + START_DATA.length + 4 + data.length, END_DATA.length);
        return buffer;
    }

    public static int getDataPackageLength(int datalength) {
        int length = START_DATA.length + 4 + datalength + END_DATA.length;
        return length;
    }

    public static byte[] createDataPackage(byte[] data) {
        int length = XByteBuffer.getDataPackageLength(data.length);
        byte[] result = new byte[length];
        return XByteBuffer.createDataPackage(data, 0, data.length, result, 0);
    }

    public static int toInt(byte[] b, int off) {
        return (b[off + 3] & 0xFF) + ((b[off + 2] & 0xFF) << 8) + ((b[off + 1] & 0xFF) << 16) + ((b[off + 0] & 0xFF) << 24);
    }

    public static long toLong(byte[] b, int off) {
        return ((long)b[off + 7] & 0xFFL) + (((long)b[off + 6] & 0xFFL) << 8) + (((long)b[off + 5] & 0xFFL) << 16) + (((long)b[off + 4] & 0xFFL) << 24) + (((long)b[off + 3] & 0xFFL) << 32) + (((long)b[off + 2] & 0xFFL) << 40) + (((long)b[off + 1] & 0xFFL) << 48) + (((long)b[off + 0] & 0xFFL) << 56);
    }

    public static byte[] toBytes(boolean bool, byte[] data, int offset) {
        data[offset] = (byte)(bool ? 1 : 0);
        return data;
    }

    public static boolean toBoolean(byte[] b, int offset) {
        return b[offset] != 0;
    }

    public static byte[] toBytes(int n, byte[] b, int offset) {
        b[offset + 3] = (byte)n;
        b[offset + 2] = (byte)(n >>>= 8);
        b[offset + 1] = (byte)(n >>>= 8);
        b[offset + 0] = (byte)(n >>>= 8);
        return b;
    }

    public static byte[] toBytes(long n, byte[] b, int offset) {
        b[offset + 7] = (byte)n;
        b[offset + 6] = (byte)(n >>>= 8);
        b[offset + 5] = (byte)(n >>>= 8);
        b[offset + 4] = (byte)(n >>>= 8);
        b[offset + 3] = (byte)(n >>>= 8);
        b[offset + 2] = (byte)(n >>>= 8);
        b[offset + 1] = (byte)(n >>>= 8);
        b[offset + 0] = (byte)(n >>>= 8);
        return b;
    }

    public static int firstIndexOf(byte[] src, int srcOff, byte[] find) {
        int result = -1;
        if (find.length > src.length) {
            return result;
        }
        if (find.length == 0 || src.length == 0) {
            return result;
        }
        if (srcOff >= src.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        boolean found = false;
        int srclen = src.length;
        int findlen = find.length;
        byte first = find[0];
        int pos = srcOff;
        while (!found) {
            while (pos < srclen && first != src[pos]) {
                ++pos;
            }
            if (pos >= srclen) {
                return -1;
            }
            if (srclen - pos < findlen) {
                return -1;
            }
            found = true;
            for (int i = 1; i < findlen && found; ++i) {
                found = find[i] == src[pos + i];
            }
            if (found) {
                result = pos;
                continue;
            }
            if (srclen - pos < findlen) {
                return -1;
            }
            ++pos;
        }
        return result;
    }

    public static Serializable deserialize(byte[] data) throws IOException, ClassNotFoundException, ClassCastException {
        return XByteBuffer.deserialize(data, 0, data.length);
    }

    public static Serializable deserialize(byte[] data, int offset, int length) throws IOException, ClassNotFoundException, ClassCastException {
        return XByteBuffer.deserialize(data, offset, length, null);
    }

    public static Serializable deserialize(byte[] data, int offset, int length, ClassLoader[] cls) throws IOException, ClassNotFoundException, ClassCastException {
        invokecount.addAndGet(1);
        Object message = null;
        if (cls == null) {
            cls = new ClassLoader[]{};
        }
        if (data != null && length > 0) {
            ByteArrayInputStream instream = new ByteArrayInputStream(data, offset, length);
            ObjectInputStream stream = null;
            stream = cls.length > 0 ? new ReplicationStream(instream, cls) : new ObjectInputStream(instream);
            message = stream.readObject();
            ((InputStream)instream).close();
            stream.close();
        }
        if (message == null) {
            return null;
        }
        if (message instanceof Serializable) {
            return (Serializable)message;
        }
        throw new ClassCastException(sm.getString("xByteBuffer.wrong.class", message.getClass().getName()));
    }

    public static byte[] serialize(Serializable msg) throws IOException {
        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(outs);
        out.writeObject(msg);
        out.flush();
        byte[] data = outs.toByteArray();
        return data;
    }

    public void setDiscard(boolean discard) {
        this.discard = discard;
    }

    public boolean getDiscard() {
        return this.discard;
    }
}

