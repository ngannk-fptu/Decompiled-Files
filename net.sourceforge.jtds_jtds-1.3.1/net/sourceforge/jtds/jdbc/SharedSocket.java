/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.SocketFactory;
import net.sourceforge.jtds.jdbc.CharsetInfo;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.RequestStream;
import net.sourceforge.jtds.jdbc.ResponseStream;
import net.sourceforge.jtds.ssl.SocketFactories;
import net.sourceforge.jtds.util.Logger;

class SharedSocket {
    private Socket socket;
    private Socket sslSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private int maxBufSize = 512;
    private final AtomicInteger _LastID = new AtomicInteger();
    private final ConcurrentMap<Integer, VirtualSocket> _VirtualSockets = new ConcurrentHashMap<Integer, VirtualSocket>();
    private VirtualSocket responseOwner;
    private final byte[] hdrBuf = new byte[8];
    private final File bufferDir;
    private static int globalMemUsage;
    private static int peakMemUsage;
    private static int memoryBudget;
    private static int minMemPkts;
    private static boolean securityViolation;
    private int tdsVersion;
    protected final int serverType;
    private CharsetInfo charsetInfo;
    private int packetCount;
    private String host;
    private int port;
    private boolean cancelPending;
    private final Object cancelMonitor = new Object();
    private final byte[] doneBuffer = new byte[9];
    private int doneBufferFrag = 0;
    private static final int TDS_DONE_TOKEN = 253;
    private static final int TDS_DONE_LEN = 9;
    private static final int TDS_HDR_LEN = 8;

    protected SharedSocket(File bufferDir, int tdsVersion, int serverType) {
        this.bufferDir = bufferDir;
        this.tdsVersion = tdsVersion;
        this.serverType = serverType;
    }

    SharedSocket(JtdsConnection connection) throws IOException, UnknownHostException {
        this(connection.getBufferDir(), connection.getTdsVersion(), connection.getServerType());
        this.host = connection.getServerName();
        this.port = connection.getPortNumber();
        this.socket = this.createSocketForJDBC3(connection);
        this.setOut(new DataOutputStream(this.socket.getOutputStream()));
        this.setIn(new DataInputStream(this.socket.getInputStream()));
        this.socket.setTcpNoDelay(connection.getTcpNoDelay());
        this.socket.setSoTimeout(connection.getSocketTimeout() * 1000);
        this.socket.setKeepAlive(connection.getSocketKeepAlive());
    }

    private Socket createSocketForJDBC3(JtdsConnection connection) throws IOException {
        String host = connection.getServerName();
        int port = connection.getPortNumber();
        String bindAddress = connection.getBindAddress();
        int loginTimeout = connection.getLoginTimeout();
        Socket socket = new Socket();
        InetSocketAddress address = new InetSocketAddress(host, port);
        if (bindAddress != null && !bindAddress.isEmpty()) {
            socket.bind(new InetSocketAddress(bindAddress, 0));
        }
        socket.connect(address, loginTimeout * 1000);
        return socket;
    }

    String getMAC() {
        try {
            byte[] address;
            NetworkInterface nic = NetworkInterface.getByInetAddress(this.socket.getLocalAddress());
            byte[] byArray = address = nic == null ? null : nic.getHardwareAddress();
            if (address != null) {
                String mac = "";
                for (int k = 0; k < address.length; ++k) {
                    String macValue = String.format("%02X", address[k]);
                    mac = mac + macValue;
                }
                return mac;
            }
        }
        catch (SocketException socketException) {
            // empty catch block
        }
        return null;
    }

    void enableEncryption(String ssl) throws IOException {
        Logger.println("Enabling TLS encryption");
        SocketFactory sf = SocketFactories.getSocketFactory(ssl, this.socket);
        this.sslSocket = sf.createSocket(this.getHost(), this.getPort());
        this.setOut(new DataOutputStream(this.sslSocket.getOutputStream()));
        this.setIn(new DataInputStream(this.sslSocket.getInputStream()));
    }

    void disableEncryption() throws IOException {
        Logger.println("Disabling TLS encryption");
        this.sslSocket.close();
        this.sslSocket = null;
        this.setOut(new DataOutputStream(this.socket.getOutputStream()));
        this.setIn(new DataInputStream(this.socket.getInputStream()));
    }

    void setCharsetInfo(CharsetInfo charsetInfo) {
        this.charsetInfo = charsetInfo;
    }

    CharsetInfo getCharsetInfo() {
        return this.charsetInfo;
    }

    String getCharset() {
        return this.charsetInfo.getCharset();
    }

    RequestStream getRequestStream(int bufferSize, int maxPrecision) {
        VirtualSocket vsock;
        int id;
        do {
            id = this._LastID.incrementAndGet();
            vsock = new VirtualSocket(id);
        } while (this._VirtualSockets.putIfAbsent(id, vsock) != null);
        return new RequestStream(this, vsock, bufferSize, maxPrecision);
    }

    ResponseStream getResponseStream(RequestStream requestStream, int bufferSize) {
        return new ResponseStream(this, requestStream.getVirtualSocket(), bufferSize);
    }

    int getTdsVersion() {
        return this.tdsVersion;
    }

    protected void setTdsVersion(int tdsVersion) {
        this.tdsVersion = tdsVersion;
    }

    static void setMemoryBudget(int memoryBudget) {
        SharedSocket.memoryBudget = memoryBudget;
    }

    static int getMemoryBudget() {
        return memoryBudget;
    }

    static void setMinMemPkts(int minMemPkts) {
        SharedSocket.minMemPkts = minMemPkts;
    }

    static int getMinMemPkts() {
        return minMemPkts;
    }

    boolean isConnected() {
        return this.socket != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean cancel(VirtualSocket vsock) {
        Object object = this.cancelMonitor;
        synchronized (object) {
            if (this.responseOwner == vsock && !this.cancelPending) {
                try {
                    this.cancelPending = true;
                    this.doneBufferFrag = 0;
                    byte[] cancel = new byte[]{6, 1, 0, 8, 0, 0, this.tdsVersion >= 3 ? (byte)1 : 0, 0};
                    this.getOut().write(cancel, 0, 8);
                    this.getOut().flush();
                    if (Logger.isActive()) {
                        Logger.logPacket(vsock.id, false, cancel);
                    }
                    return true;
                }
                catch (IOException iOException) {
                    // empty catch block
                }
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void close() throws IOException {
        if (Logger.isActive()) {
            Logger.println("TdsSocket: Max buffer memory used = " + peakMemUsage / 1024 + "KB");
        }
        for (VirtualSocket vsock : this._VirtualSockets.values()) {
            if (vsock == null || vsock.diskQueue == null) continue;
            try {
                vsock.diskQueue.close();
                vsock.queueFile.delete();
            }
            catch (IOException iOException) {}
        }
        this._VirtualSockets.clear();
        try {
            if (this.sslSocket != null) {
                this.sslSocket.close();
                this.sslSocket = null;
            }
        }
        finally {
            if (this.socket != null) {
                this.socket.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void forceClose() {
        if (this.socket != null) {
            try {
                this.socket.close();
            }
            catch (IOException iOException) {
            }
            finally {
                this.sslSocket = null;
                this.socket = null;
            }
        }
    }

    void closeStream(VirtualSocket vsock) {
        this._VirtualSockets.remove(vsock.id);
        if (vsock.diskQueue != null) {
            try {
                vsock.diskQueue.close();
                vsock.queueFile.delete();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    byte[] sendNetPacket(VirtualSocket vsock, byte[] buffer) throws IOException {
        ConcurrentMap<Integer, VirtualSocket> concurrentMap = this._VirtualSockets;
        synchronized (concurrentMap) {
            while (vsock.inputPkts > 0) {
                if (Logger.isActive()) {
                    Logger.println("TdsSocket: Unread data in input packet queue");
                }
                this.dequeueInput(vsock);
            }
            if (this.responseOwner != null) {
                byte[] tmpBuf = null;
                boolean ourData = this.responseOwner == vsock;
                VirtualSocket tmpSock = this.responseOwner;
                do {
                    tmpBuf = this.readPacket(ourData ? tmpBuf : null);
                    if (ourData) continue;
                    this.enqueueInput(tmpSock, tmpBuf);
                } while (tmpBuf[1] == 0);
            }
            this.getOut().write(buffer, 0, SharedSocket.getPktLen(buffer));
            if (buffer[1] != 0) {
                this.getOut().flush();
                this.responseOwner = vsock;
            }
            return buffer;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    byte[] getNetPacket(VirtualSocket vsock, byte[] buffer) throws IOException {
        ConcurrentMap<Integer, VirtualSocket> concurrentMap = this._VirtualSockets;
        synchronized (concurrentMap) {
            if (vsock.inputPkts > 0) {
                return this.dequeueInput(vsock);
            }
            if (this.responseOwner == null) {
                throw new IOException("Stream " + vsock.id + " attempting to read when no request has been sent");
            }
            if (this.responseOwner != vsock) {
                throw new IOException("Stream " + vsock.id + " is trying to read data that belongs to stream " + this.responseOwner.id);
            }
            return this.readPacket(buffer);
        }
    }

    private void enqueueInput(VirtualSocket vsock, byte[] buffer) throws IOException {
        if (globalMemUsage + buffer.length > memoryBudget && vsock.pktQueue.size() >= minMemPkts && !securityViolation && vsock.diskQueue == null) {
            try {
                vsock.queueFile = File.createTempFile("jtds", ".tmp", this.bufferDir);
                vsock.diskQueue = new RandomAccessFile(vsock.queueFile, "rw");
                while (vsock.pktQueue.size() > 0) {
                    byte[] tmpBuf = (byte[])vsock.pktQueue.removeFirst();
                    vsock.diskQueue.write(tmpBuf, 0, SharedSocket.getPktLen(tmpBuf));
                    ++vsock.pktsOnDisk;
                }
            }
            catch (SecurityException se) {
                securityViolation = true;
                vsock.queueFile = null;
                vsock.diskQueue = null;
            }
        }
        if (vsock.diskQueue != null) {
            vsock.diskQueue.write(buffer, 0, SharedSocket.getPktLen(buffer));
            ++vsock.pktsOnDisk;
        } else {
            vsock.pktQueue.addLast(buffer);
            if ((globalMemUsage += buffer.length) > peakMemUsage) {
                peakMemUsage = globalMemUsage;
            }
        }
        ++vsock.inputPkts;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private byte[] dequeueInput(VirtualSocket vsock) throws IOException {
        byte[] buffer = null;
        if (vsock.pktsOnDisk > 0) {
            if (vsock.diskQueue.getFilePointer() == vsock.diskQueue.length()) {
                vsock.diskQueue.seek(0L);
            }
            vsock.diskQueue.readFully(this.hdrBuf, 0, 8);
            int len = SharedSocket.getPktLen(this.hdrBuf);
            buffer = new byte[len];
            System.arraycopy(this.hdrBuf, 0, buffer, 0, 8);
            vsock.diskQueue.readFully(buffer, 8, len - 8);
            --vsock.pktsOnDisk;
            if (vsock.pktsOnDisk < 1) {
                try {
                    vsock.diskQueue.close();
                    vsock.queueFile.delete();
                }
                finally {
                    vsock.queueFile = null;
                    vsock.diskQueue = null;
                }
            }
        } else if (vsock.pktQueue.size() > 0) {
            buffer = (byte[])vsock.pktQueue.removeFirst();
            globalMemUsage -= buffer.length;
        }
        if (buffer != null) {
            --vsock.inputPkts;
        }
        return buffer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private byte[] readPacket(byte[] buffer) throws IOException {
        try {
            this.getIn().readFully(this.hdrBuf);
        }
        catch (EOFException e) {
            throw new IOException("DB server closed connection.");
        }
        byte packetType = this.hdrBuf[0];
        if (packetType != 2 && packetType != 1 && packetType != 15 && packetType != 4) {
            throw new IOException("Unknown packet type 0x" + Integer.toHexString(packetType & 0xFF));
        }
        int len = SharedSocket.getPktLen(this.hdrBuf);
        if (len < 8 || len > 65536) {
            throw new IOException("Invalid network packet length " + len);
        }
        if (buffer == null || len > buffer.length) {
            buffer = new byte[len];
            if (len > this.maxBufSize) {
                this.maxBufSize = len;
            }
        }
        System.arraycopy(this.hdrBuf, 0, buffer, 0, 8);
        try {
            this.getIn().readFully(buffer, 8, len - 8);
        }
        catch (EOFException e) {
            throw new IOException("DB server closed connection.");
        }
        if (++this.packetCount == 1 && this.serverType == 1 && "NTLMSSP".equals(new String(buffer, 11, 7))) {
            buffer[1] = 1;
        }
        Object object = this.cancelMonitor;
        synchronized (object) {
            if (this.cancelPending) {
                int frag = Math.min(9, len - 8);
                int keep = 9 - frag;
                System.arraycopy(this.doneBuffer, frag, this.doneBuffer, 0, keep);
                System.arraycopy(buffer, len - frag, this.doneBuffer, keep, frag);
                this.doneBufferFrag = Math.min(9, this.doneBufferFrag + frag);
                if (this.doneBufferFrag < 9) {
                    buffer[1] = 0;
                }
                if (buffer[1] == 1) {
                    if ((this.doneBuffer[0] & 0xFF) < 253) {
                        throw new IOException("Expecting a TDS_DONE or TDS_DONEPROC.");
                    }
                    if ((this.doneBuffer[1] & 0x20) != 0) {
                        this.cancelPending = false;
                    } else {
                        buffer[1] = 0;
                    }
                }
            }
            if (buffer[1] != 0) {
                this.responseOwner = null;
            }
        }
        return buffer;
    }

    static int getPktLen(byte[] buf) {
        int lo = buf[3] & 0xFF;
        int hi = (buf[2] & 0xFF) << 8;
        return hi | lo;
    }

    protected void setTimeout(int timeout) throws SocketException {
        this.socket.setSoTimeout(timeout);
    }

    protected void setKeepAlive(boolean keepAlive) throws SocketException {
        this.socket.setKeepAlive(keepAlive);
    }

    protected DataInputStream getIn() {
        return this.in;
    }

    protected void setIn(DataInputStream in) {
        this.in = in;
    }

    protected DataOutputStream getOut() {
        return this.out;
    }

    protected void setOut(DataOutputStream out) {
        this.out = out;
    }

    protected String getHost() {
        return this.host;
    }

    protected int getPort() {
        return this.port;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void finalize() throws Throwable {
        try {
            this.close();
        }
        finally {
            super.finalize();
        }
    }

    static {
        memoryBudget = 100000;
        minMemPkts = 8;
    }

    static class VirtualSocket {
        final int id;
        final LinkedList pktQueue;
        File queueFile;
        RandomAccessFile diskQueue;
        int pktsOnDisk;
        int inputPkts;

        private VirtualSocket(int streamId) {
            this.id = streamId;
            this.pktQueue = new LinkedList();
        }
    }
}

