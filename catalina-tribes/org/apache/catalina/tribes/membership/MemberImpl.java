/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.membership;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.transport.SenderState;
import org.apache.catalina.tribes.util.StringManager;

public class MemberImpl
implements Member,
Externalizable {
    @Deprecated
    public static final boolean DO_DNS_LOOKUPS = Boolean.parseBoolean(System.getProperty("org.apache.catalina.tribes.dns_lookups", "false"));
    public static final transient byte[] TRIBES_MBR_BEGIN = new byte[]{84, 82, 73, 66, 69, 83, 45, 66, 1, 0};
    public static final transient byte[] TRIBES_MBR_END = new byte[]{84, 82, 73, 66, 69, 83, 45, 69, 1, 0};
    protected static final StringManager sm = StringManager.getManager("org.apache.catalina.tribes.membership");
    protected volatile byte[] host = new byte[0];
    protected volatile transient String hostname;
    protected volatile int port;
    protected volatile int udpPort = -1;
    protected volatile int securePort = -1;
    protected AtomicInteger msgCount = new AtomicInteger(0);
    protected volatile long memberAliveTime = 0L;
    protected transient long serviceStartTime;
    protected transient byte[] dataPkg = null;
    protected volatile byte[] uniqueId = new byte[16];
    protected volatile byte[] payload = new byte[0];
    protected volatile byte[] command = new byte[0];
    protected volatile byte[] domain = new byte[0];
    protected volatile boolean local = false;

    public MemberImpl() {
    }

    public MemberImpl(String host, int port, long aliveTime) throws IOException {
        this.setHostname(host);
        this.port = port;
        this.memberAliveTime = aliveTime;
    }

    public MemberImpl(String host, int port, long aliveTime, byte[] payload) throws IOException {
        this(host, port, aliveTime);
        this.setPayload(payload);
    }

    @Override
    public boolean isReady() {
        return SenderState.getSenderState(this).isReady();
    }

    @Override
    public boolean isSuspect() {
        return SenderState.getSenderState(this).isSuspect();
    }

    @Override
    public boolean isFailing() {
        return SenderState.getSenderState(this).isFailing();
    }

    protected void inc() {
        this.msgCount.incrementAndGet();
    }

    public byte[] getData() {
        return this.getData(true);
    }

    @Override
    public byte[] getData(boolean getalive) {
        return this.getData(getalive, false);
    }

    @Override
    public synchronized int getDataLength() {
        return TRIBES_MBR_BEGIN.length + 4 + 8 + 4 + 4 + 4 + 1 + this.host.length + 4 + this.command.length + 4 + this.domain.length + 16 + 4 + this.payload.length + TRIBES_MBR_END.length;
    }

    @Override
    public synchronized byte[] getData(boolean getalive, boolean reset) {
        if (reset) {
            this.dataPkg = null;
        }
        if (this.dataPkg != null) {
            if (getalive) {
                long alive = System.currentTimeMillis() - this.getServiceStartTime();
                byte[] result = (byte[])this.dataPkg.clone();
                XByteBuffer.toBytes(alive, result, TRIBES_MBR_BEGIN.length + 4);
                this.dataPkg = result;
            }
            return this.dataPkg;
        }
        long alive = System.currentTimeMillis() - this.getServiceStartTime();
        byte[] data = new byte[this.getDataLength()];
        int bodylength = this.getDataLength() - TRIBES_MBR_BEGIN.length - TRIBES_MBR_END.length - 4;
        int pos = 0;
        System.arraycopy(TRIBES_MBR_BEGIN, 0, data, pos, TRIBES_MBR_BEGIN.length);
        XByteBuffer.toBytes(bodylength, data, pos += TRIBES_MBR_BEGIN.length);
        XByteBuffer.toBytes(alive, data, pos += 4);
        XByteBuffer.toBytes(this.port, data, pos += 8);
        XByteBuffer.toBytes(this.securePort, data, pos += 4);
        XByteBuffer.toBytes(this.udpPort, data, pos += 4);
        pos += 4;
        data[pos++] = (byte)this.host.length;
        System.arraycopy(this.host, 0, data, pos, this.host.length);
        XByteBuffer.toBytes(this.command.length, data, pos += this.host.length);
        System.arraycopy(this.command, 0, data, pos += 4, this.command.length);
        XByteBuffer.toBytes(this.domain.length, data, pos += this.command.length);
        System.arraycopy(this.domain, 0, data, pos += 4, this.domain.length);
        System.arraycopy(this.uniqueId, 0, data, pos += this.domain.length, this.uniqueId.length);
        XByteBuffer.toBytes(this.payload.length, data, pos += this.uniqueId.length);
        System.arraycopy(this.payload, 0, data, pos += 4, this.payload.length);
        System.arraycopy(TRIBES_MBR_END, 0, data, pos += this.payload.length, TRIBES_MBR_END.length);
        pos += TRIBES_MBR_END.length;
        this.dataPkg = data;
        return data;
    }

    public static Member getMember(byte[] data, MemberImpl member) {
        return MemberImpl.getMember(data, 0, data.length, member);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Member getMember(byte[] data, int offset, int length, MemberImpl member) {
        int pos = offset;
        if (XByteBuffer.firstIndexOf(data, offset, TRIBES_MBR_BEGIN) != pos) {
            throw new IllegalArgumentException(sm.getString("memberImpl.invalid.package.begin", org.apache.catalina.tribes.util.Arrays.toString(TRIBES_MBR_BEGIN)));
        }
        if (length < TRIBES_MBR_BEGIN.length + 4) {
            throw new ArrayIndexOutOfBoundsException(sm.getString("memberImpl.package.small"));
        }
        int bodylength = XByteBuffer.toInt(data, pos += TRIBES_MBR_BEGIN.length);
        pos += 4;
        if (length < bodylength + 4 + TRIBES_MBR_BEGIN.length + TRIBES_MBR_END.length) {
            throw new ArrayIndexOutOfBoundsException(sm.getString("memberImpl.notEnough.bytes"));
        }
        int endpos = pos + bodylength;
        if (XByteBuffer.firstIndexOf(data, endpos, TRIBES_MBR_END) != endpos) {
            throw new IllegalArgumentException(sm.getString("memberImpl.invalid.package.end", org.apache.catalina.tribes.util.Arrays.toString(TRIBES_MBR_END)));
        }
        byte[] alived = new byte[8];
        System.arraycopy(data, pos, alived, 0, 8);
        byte[] portd = new byte[4];
        System.arraycopy(data, pos += 8, portd, 0, 4);
        byte[] sportd = new byte[4];
        System.arraycopy(data, pos += 4, sportd, 0, 4);
        byte[] uportd = new byte[4];
        System.arraycopy(data, pos += 4, uportd, 0, 4);
        pos += 4;
        byte hl = data[pos++];
        byte[] addr = new byte[hl];
        System.arraycopy(data, pos, addr, 0, hl);
        int cl = XByteBuffer.toInt(data, pos += hl);
        byte[] command = new byte[cl];
        System.arraycopy(data, pos += 4, command, 0, command.length);
        int dl = XByteBuffer.toInt(data, pos += command.length);
        byte[] domain = new byte[dl];
        System.arraycopy(data, pos += 4, domain, 0, domain.length);
        byte[] uniqueId = new byte[16];
        System.arraycopy(data, pos += domain.length, uniqueId, 0, 16);
        int pl = XByteBuffer.toInt(data, pos += 16);
        byte[] payload = new byte[pl];
        System.arraycopy(data, pos += 4, payload, 0, payload.length);
        pos += payload.length;
        MemberImpl memberImpl = member;
        synchronized (memberImpl) {
            member.setHost(addr);
            member.setPort(XByteBuffer.toInt(portd, 0));
            member.setSecurePort(XByteBuffer.toInt(sportd, 0));
            member.setUdpPort(XByteBuffer.toInt(uportd, 0));
            member.setMemberAliveTime(XByteBuffer.toLong(alived, 0));
            member.setUniqueId(uniqueId);
            member.payload = payload;
            member.domain = domain;
            member.command = command;
            member.dataPkg = new byte[length];
            System.arraycopy(data, offset, member.dataPkg, 0, length);
        }
        return member;
    }

    public static Member getMember(byte[] data) {
        return MemberImpl.getMember(data, new MemberImpl());
    }

    public static Member getMember(byte[] data, int offset, int length) {
        return MemberImpl.getMember(data, offset, length, new MemberImpl());
    }

    @Override
    public String getName() {
        return "tcp://" + this.getHostname() + ":" + this.getPort();
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public byte[] getHost() {
        return this.host;
    }

    public String getHostname() {
        if (this.hostname != null) {
            return this.hostname;
        }
        try {
            byte[] host = this.host;
            this.hostname = DO_DNS_LOOKUPS ? InetAddress.getByAddress(host).getHostName() : org.apache.catalina.tribes.util.Arrays.toString(host, 0, host.length, true);
            return this.hostname;
        }
        catch (IOException x) {
            throw new RuntimeException(sm.getString("memberImpl.unableParse.hostname"), x);
        }
    }

    public int getMsgCount() {
        return this.msgCount.get();
    }

    @Override
    public long getMemberAliveTime() {
        return this.memberAliveTime;
    }

    public long getServiceStartTime() {
        return this.serviceStartTime;
    }

    @Override
    public byte[] getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public byte[] getPayload() {
        return this.payload;
    }

    @Override
    public byte[] getCommand() {
        return this.command;
    }

    @Override
    public byte[] getDomain() {
        return this.domain;
    }

    @Override
    public int getSecurePort() {
        return this.securePort;
    }

    @Override
    public int getUdpPort() {
        return this.udpPort;
    }

    @Override
    public void setMemberAliveTime(long time) {
        this.memberAliveTime = time;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(this.getClass().getName());
        buf.append('[');
        buf.append(this.getName()).append(',');
        buf.append(this.getHostname()).append(',');
        buf.append(this.port).append(", alive=");
        buf.append(this.memberAliveTime).append(", ");
        buf.append("securePort=").append(this.securePort).append(", ");
        buf.append("UDP Port=").append(this.udpPort).append(", ");
        buf.append("id=").append(MemberImpl.bToS(this.uniqueId)).append(", ");
        buf.append("payload=").append(MemberImpl.bToS(this.payload, 8)).append(", ");
        buf.append("command=").append(MemberImpl.bToS(this.command, 8)).append(", ");
        buf.append("domain=").append(MemberImpl.bToS(this.domain, 8));
        buf.append(']');
        return buf.toString();
    }

    public static String bToS(byte[] data) {
        return MemberImpl.bToS(data, data.length);
    }

    public static String bToS(byte[] data, int max) {
        StringBuilder buf = new StringBuilder(64);
        buf.append('{');
        for (int i = 0; data != null && i < data.length; ++i) {
            buf.append(String.valueOf(data[i])).append(' ');
            if (i != max) continue;
            buf.append("...(" + data.length + ")");
            break;
        }
        buf.append('}');
        return buf.toString();
    }

    public int hashCode() {
        return this.getHost()[0] + this.getHost()[1] + this.getHost()[2] + this.getHost()[3];
    }

    public boolean equals(Object o) {
        if (o instanceof MemberImpl) {
            return Arrays.equals(this.getHost(), ((MemberImpl)o).getHost()) && this.getPort() == ((MemberImpl)o).getPort() && Arrays.equals(this.getUniqueId(), ((MemberImpl)o).getUniqueId());
        }
        return false;
    }

    public synchronized void setHost(byte[] host) {
        this.host = host;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setHostname(String host) throws IOException {
        this.hostname = host;
        MemberImpl memberImpl = this;
        synchronized (memberImpl) {
            this.host = InetAddress.getByName(host).getAddress();
        }
    }

    public void setMsgCount(int msgCount) {
        this.msgCount.set(msgCount);
    }

    public synchronized void setPort(int port) {
        this.port = port;
        this.dataPkg = null;
    }

    public void setServiceStartTime(long serviceStartTime) {
        this.serviceStartTime = serviceStartTime;
    }

    public synchronized void setUniqueId(byte[] uniqueId) {
        this.uniqueId = uniqueId != null ? uniqueId : new byte[16];
        this.getData(true, true);
    }

    @Override
    public synchronized void setPayload(byte[] payload) {
        long oldPayloadLength = this.payload.length;
        long newPayloadLength = 0L;
        if (payload != null) {
            newPayloadLength = payload.length;
        }
        if (newPayloadLength > oldPayloadLength && newPayloadLength - oldPayloadLength + (long)this.getData(false, false).length > 65535L) {
            throw new IllegalArgumentException(sm.getString("memberImpl.large.payload"));
        }
        this.payload = payload != null ? payload : new byte[]{};
        this.getData(true, true);
    }

    @Override
    public synchronized void setCommand(byte[] command) {
        this.command = command != null ? command : new byte[]{};
        this.getData(true, true);
    }

    public synchronized void setDomain(byte[] domain) {
        this.domain = domain != null ? domain : new byte[]{};
        this.getData(true, true);
    }

    public synchronized void setSecurePort(int securePort) {
        this.securePort = securePort;
        this.dataPkg = null;
    }

    public synchronized void setUdpPort(int port) {
        this.udpPort = port;
        this.dataPkg = null;
    }

    @Override
    public boolean isLocal() {
        return this.local;
    }

    @Override
    public void setLocal(boolean local) {
        this.local = local;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int length = in.readInt();
        byte[] message = new byte[length];
        in.readFully(message);
        MemberImpl.getMember(message, this);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        byte[] data = this.getData();
        out.writeInt(data.length);
        out.write(data);
    }
}

