/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.io;

import java.sql.Timestamp;
import java.util.Arrays;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.io.BufferPool;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.membership.MemberImpl;
import org.apache.catalina.tribes.util.UUIDGenerator;

public class ChannelData
implements ChannelMessage {
    private static final long serialVersionUID = 1L;
    public static final ChannelData[] EMPTY_DATA_ARRAY = new ChannelData[0];
    public static volatile boolean USE_SECURE_RANDOM_FOR_UUID = false;
    private int options = 0;
    private XByteBuffer message;
    private long timestamp;
    private byte[] uniqueId;
    private Member address;

    public ChannelData() {
        this(true);
    }

    public ChannelData(boolean generateUUID) {
        if (generateUUID) {
            this.generateUUID();
        }
    }

    public ChannelData(byte[] uniqueId, XByteBuffer message, long timestamp) {
        this.uniqueId = uniqueId;
        this.message = message;
        this.timestamp = timestamp;
    }

    @Override
    public XByteBuffer getMessage() {
        return this.message;
    }

    @Override
    public void setMessage(XByteBuffer message) {
        this.message = message;
    }

    @Override
    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public byte[] getUniqueId() {
        return this.uniqueId;
    }

    public void setUniqueId(byte[] uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public int getOptions() {
        return this.options;
    }

    @Override
    public void setOptions(int options) {
        this.options = options;
    }

    @Override
    public Member getAddress() {
        return this.address;
    }

    @Override
    public void setAddress(Member address) {
        this.address = address;
    }

    public void generateUUID() {
        byte[] data = new byte[16];
        UUIDGenerator.randomUUID(USE_SECURE_RANDOM_FOR_UUID, data, 0);
        this.setUniqueId(data);
    }

    public int getDataPackageLength() {
        int length = 16 + this.uniqueId.length + 4 + this.address.getDataLength() + 4 + this.message.getLength();
        return length;
    }

    public byte[] getDataPackage() {
        int length = this.getDataPackageLength();
        byte[] data = new byte[length];
        int offset = 0;
        return this.getDataPackage(data, offset);
    }

    public byte[] getDataPackage(byte[] data, int offset) {
        byte[] addr = this.address.getData(false);
        XByteBuffer.toBytes(this.options, data, offset);
        XByteBuffer.toBytes(this.timestamp, data, offset += 4);
        XByteBuffer.toBytes(this.uniqueId.length, data, offset += 8);
        System.arraycopy(this.uniqueId, 0, data, offset += 4, this.uniqueId.length);
        XByteBuffer.toBytes(addr.length, data, offset += this.uniqueId.length);
        System.arraycopy(addr, 0, data, offset += 4, addr.length);
        XByteBuffer.toBytes(this.message.getLength(), data, offset += addr.length);
        System.arraycopy(this.message.getBytesDirect(), 0, data, offset += 4, this.message.getLength());
        return data;
    }

    public static ChannelData getDataFromPackage(XByteBuffer xbuf) {
        ChannelData data = new ChannelData(false);
        int offset = 0;
        data.setOptions(XByteBuffer.toInt(xbuf.getBytesDirect(), offset));
        data.setTimestamp(XByteBuffer.toLong(xbuf.getBytesDirect(), offset += 4));
        data.uniqueId = new byte[XByteBuffer.toInt(xbuf.getBytesDirect(), offset += 8)];
        System.arraycopy(xbuf.getBytesDirect(), offset += 4, data.uniqueId, 0, data.uniqueId.length);
        int addrlen = XByteBuffer.toInt(xbuf.getBytesDirect(), offset += data.uniqueId.length);
        data.setAddress(MemberImpl.getMember(xbuf.getBytesDirect(), offset += 4, addrlen));
        int xsize = XByteBuffer.toInt(xbuf.getBytesDirect(), offset += addrlen);
        System.arraycopy(xbuf.getBytesDirect(), offset += 4, xbuf.getBytesDirect(), 0, xsize);
        xbuf.setLength(xsize);
        data.message = xbuf;
        return data;
    }

    public static ChannelData getDataFromPackage(byte[] b) {
        ChannelData data = new ChannelData(false);
        int offset = 0;
        data.setOptions(XByteBuffer.toInt(b, offset));
        data.setTimestamp(XByteBuffer.toLong(b, offset += 4));
        data.uniqueId = new byte[XByteBuffer.toInt(b, offset += 8)];
        System.arraycopy(b, offset += 4, data.uniqueId, 0, data.uniqueId.length);
        byte[] addr = new byte[XByteBuffer.toInt(b, offset += data.uniqueId.length)];
        System.arraycopy(b, offset += 4, addr, 0, addr.length);
        data.setAddress(MemberImpl.getMember(addr));
        int xsize = XByteBuffer.toInt(b, offset += addr.length);
        data.message = BufferPool.getBufferPool().getBuffer(xsize, false);
        System.arraycopy(b, offset += 4, data.message.getBytesDirect(), 0, xsize);
        data.message.append(b, offset, xsize);
        offset += xsize;
        return data;
    }

    public int hashCode() {
        return XByteBuffer.toInt(this.getUniqueId(), 0);
    }

    public boolean equals(Object o) {
        if (o instanceof ChannelData) {
            return Arrays.equals(this.getUniqueId(), ((ChannelData)o).getUniqueId());
        }
        return false;
    }

    @Override
    public ChannelData clone() {
        ChannelData clone;
        try {
            clone = (ChannelData)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
        if (this.message != null) {
            clone.message = new XByteBuffer(this.message.getBytesDirect(), false);
        }
        return clone;
    }

    @Override
    public Object deepclone() {
        byte[] d = this.getDataPackage();
        return ChannelData.getDataFromPackage(d);
    }

    public static boolean sendAckSync(int options) {
        return (2 & options) == 2 && (4 & options) == 4;
    }

    public static boolean sendAckAsync(int options) {
        return (2 & options) == 2 && (4 & options) != 4;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("ClusterData[src=");
        buf.append(this.getAddress()).append("; id=");
        buf.append(ChannelData.bToS(this.getUniqueId())).append("; sent=");
        buf.append(new Timestamp(this.getTimestamp()).toString()).append(']');
        return buf.toString();
    }

    public static String bToS(byte[] data) {
        StringBuilder buf = new StringBuilder(64);
        buf.append('{');
        for (int i = 0; data != null && i < data.length; ++i) {
            buf.append(String.valueOf(data[i])).append(' ');
        }
        buf.append('}');
        return buf.toString();
    }
}

