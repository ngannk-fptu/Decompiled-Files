/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.transport.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import org.apache.catalina.tribes.RemoteProcessException;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.transport.AbstractSender;
import org.apache.catalina.tribes.transport.Constants;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class NioSender
extends AbstractSender {
    private static final Log log = LogFactory.getLog(NioSender.class);
    protected static final StringManager sm = StringManager.getManager(NioSender.class);
    protected Selector selector;
    protected SocketChannel socketChannel = null;
    protected DatagramChannel dataChannel = null;
    protected ByteBuffer readbuf = null;
    protected ByteBuffer writebuf = null;
    protected volatile byte[] current = null;
    protected final XByteBuffer ackbuf = new XByteBuffer(128, true);
    protected int remaining = 0;
    protected boolean complete;
    protected boolean connecting = false;

    /*
     * Enabled aggressive block sorting
     */
    public boolean process(SelectionKey key, boolean waitForAck) throws IOException {
        int ops = key.readyOps();
        key.interestOps(key.interestOps() & ~ops);
        if (!this.isConnected() && !this.connecting) {
            throw new IOException(sm.getString("nioSender.sender.disconnected"));
        }
        if (!key.isValid()) {
            throw new IOException(sm.getString("nioSender.key.inValid"));
        }
        if (key.isConnectable()) {
            if (this.socketChannel.finishConnect()) {
                this.completeConnect();
                if (this.current == null) return false;
                key.interestOps(key.interestOps() | 4);
                return false;
            }
            key.interestOps(key.interestOps() | 8);
            return false;
        }
        if (key.isWritable()) {
            boolean writecomplete = this.write();
            if (!writecomplete) {
                key.interestOps(key.interestOps() | 4);
                return false;
            }
            if (waitForAck) {
                key.interestOps(key.interestOps() | 1);
                return false;
            }
            this.read();
            this.setRequestCount(this.getRequestCount() + 1);
            return true;
        }
        if (!key.isReadable()) {
            log.warn((Object)sm.getString("nioSender.unknown.state", Integer.toString(ops)));
            throw new IOException(sm.getString("nioSender.unknown.state", Integer.toString(ops)));
        }
        boolean readcomplete = this.read();
        if (readcomplete) {
            this.setRequestCount(this.getRequestCount() + 1);
            return true;
        }
        key.interestOps(key.interestOps() | 1);
        return false;
    }

    private void configureSocket() throws IOException {
        if (this.socketChannel != null) {
            this.socketChannel.configureBlocking(false);
            this.socketChannel.socket().setSendBufferSize(this.getTxBufSize());
            this.socketChannel.socket().setReceiveBufferSize(this.getRxBufSize());
            this.socketChannel.socket().setSoTimeout((int)this.getTimeout());
            this.socketChannel.socket().setSoLinger(this.getSoLingerOn(), this.getSoLingerOn() ? this.getSoLingerTime() : 0);
            this.socketChannel.socket().setTcpNoDelay(this.getTcpNoDelay());
            this.socketChannel.socket().setKeepAlive(this.getSoKeepAlive());
            this.socketChannel.socket().setReuseAddress(this.getSoReuseAddress());
            this.socketChannel.socket().setOOBInline(this.getOoBInline());
            this.socketChannel.socket().setSoLinger(this.getSoLingerOn(), this.getSoLingerTime());
            this.socketChannel.socket().setTrafficClass(this.getSoTrafficClass());
        } else if (this.dataChannel != null) {
            this.dataChannel.configureBlocking(false);
            this.dataChannel.socket().setSendBufferSize(this.getUdpTxBufSize());
            this.dataChannel.socket().setReceiveBufferSize(this.getUdpRxBufSize());
            this.dataChannel.socket().setSoTimeout((int)this.getTimeout());
            this.dataChannel.socket().setReuseAddress(this.getSoReuseAddress());
            this.dataChannel.socket().setTrafficClass(this.getSoTrafficClass());
        }
    }

    private void completeConnect() {
        this.setConnected(true);
        this.connecting = false;
        this.setRequestCount(0);
        this.setConnectTime(System.currentTimeMillis());
    }

    protected boolean read() throws IOException {
        int read;
        if (this.current == null) {
            return true;
        }
        int n = read = this.isUdpBased() ? this.dataChannel.read(this.readbuf) : this.socketChannel.read(this.readbuf);
        if (read == -1) {
            throw new IOException(sm.getString("nioSender.unable.receive.ack"));
        }
        if (read == 0) {
            return false;
        }
        this.readbuf.flip();
        this.ackbuf.append(this.readbuf, read);
        this.readbuf.clear();
        if (this.ackbuf.doesPackageExist()) {
            byte[] ackcmd = this.ackbuf.extractDataPackage(true).getBytes();
            boolean ack = Arrays.equals(ackcmd, Constants.ACK_DATA);
            boolean fack = Arrays.equals(ackcmd, Constants.FAIL_ACK_DATA);
            if (fack && this.getThrowOnFailedAck()) {
                throw new RemoteProcessException(sm.getString("nioSender.receive.failedAck"));
            }
            return ack || fack;
        }
        return false;
    }

    protected boolean write() throws IOException {
        if (!this.isConnected() || this.socketChannel == null && this.dataChannel == null) {
            throw new IOException(sm.getString("nioSender.not.connected"));
        }
        if (this.current != null) {
            if (this.remaining > 0) {
                int byteswritten = this.isUdpBased() ? this.dataChannel.write(this.writebuf) : this.socketChannel.write(this.writebuf);
                this.remaining -= byteswritten;
                if (this.remaining < 0) {
                    this.remaining = 0;
                }
            }
            return this.remaining == 0;
        }
        return true;
    }

    @Override
    public synchronized void connect() throws IOException {
        if (this.connecting || this.isConnected()) {
            return;
        }
        this.connecting = true;
        if (this.isConnected()) {
            throw new IOException(sm.getString("nioSender.already.connected"));
        }
        if (this.readbuf == null) {
            this.readbuf = this.getReadBuffer();
        } else {
            this.readbuf.clear();
        }
        if (this.writebuf == null) {
            this.writebuf = this.getWriteBuffer();
        } else {
            this.writebuf.clear();
        }
        if (this.isUdpBased()) {
            InetSocketAddress daddr = new InetSocketAddress(this.getAddress(), this.getUdpPort());
            if (this.dataChannel != null) {
                throw new IOException(sm.getString("nioSender.datagram.already.established"));
            }
            this.dataChannel = DatagramChannel.open();
            this.configureSocket();
            this.dataChannel.connect(daddr);
            this.completeConnect();
            this.dataChannel.register(this.getSelector(), 4, this);
        } else {
            InetSocketAddress addr = new InetSocketAddress(this.getAddress(), this.getPort());
            if (this.socketChannel != null) {
                throw new IOException(sm.getString("nioSender.socketChannel.already.established"));
            }
            this.socketChannel = SocketChannel.open();
            this.configureSocket();
            if (this.socketChannel.connect(addr)) {
                this.completeConnect();
                this.socketChannel.register(this.getSelector(), 4, this);
            } else {
                this.socketChannel.register(this.getSelector(), 8, this);
            }
        }
    }

    @Override
    public void disconnect() {
        block18: {
            try {
                this.connecting = false;
                this.setConnected(false);
                if (this.socketChannel != null) {
                    try {
                        try {
                            this.socketChannel.socket().close();
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        try {
                            this.socketChannel.close();
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                    finally {
                        this.socketChannel = null;
                    }
                }
                if (this.dataChannel == null) break block18;
                try {
                    try {
                        this.dataChannel.socket().close();
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    try {
                        this.dataChannel.close();
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                finally {
                    this.dataChannel = null;
                }
            }
            catch (Exception x) {
                log.error((Object)sm.getString("nioSender.unable.disconnect", x.getMessage()));
                if (!log.isDebugEnabled()) break block18;
                log.debug((Object)sm.getString("nioSender.unable.disconnect", x.getMessage()), (Throwable)x);
            }
        }
    }

    public void reset() {
        if (this.isConnected() && this.readbuf == null) {
            this.readbuf = this.getReadBuffer();
        }
        if (this.readbuf != null) {
            this.readbuf.clear();
        }
        if (this.writebuf != null) {
            this.writebuf.clear();
        }
        this.current = null;
        this.ackbuf.clear();
        this.remaining = 0;
        this.complete = false;
        this.setAttempt(0);
        this.setUdpBased(false);
    }

    private ByteBuffer getReadBuffer() {
        return this.getBuffer(this.getRxBufSize());
    }

    private ByteBuffer getWriteBuffer() {
        return this.getBuffer(this.getTxBufSize());
    }

    private ByteBuffer getBuffer(int size) {
        return this.getDirectBuffer() ? ByteBuffer.allocateDirect(size) : ByteBuffer.allocate(size);
    }

    public void setMessage(byte[] data) throws IOException {
        this.setMessage(data, 0, data.length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setMessage(byte[] data, int offset, int length) throws IOException {
        if (data != null) {
            NioSender nioSender = this;
            synchronized (nioSender) {
                this.current = data;
                this.remaining = length;
                this.ackbuf.clear();
                if (this.writebuf != null) {
                    this.writebuf.clear();
                } else {
                    this.writebuf = this.getBuffer(length);
                }
                if (this.writebuf.capacity() < length) {
                    this.writebuf = this.getBuffer(length);
                }
                this.writebuf.put(data, offset, length);
                this.writebuf.flip();
                if (this.isConnected()) {
                    if (this.isUdpBased()) {
                        this.dataChannel.register(this.getSelector(), 4, this);
                    } else {
                        this.socketChannel.register(this.getSelector(), 4, this);
                    }
                }
            }
        }
    }

    public byte[] getMessage() {
        return this.current;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public Selector getSelector() {
        return this.selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}

