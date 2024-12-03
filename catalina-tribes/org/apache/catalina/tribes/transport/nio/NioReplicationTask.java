/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.transport.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.sql.Timestamp;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.RemoteProcessException;
import org.apache.catalina.tribes.UniqueId;
import org.apache.catalina.tribes.io.BufferPool;
import org.apache.catalina.tribes.io.ChannelData;
import org.apache.catalina.tribes.io.ListenCallback;
import org.apache.catalina.tribes.io.ObjectReader;
import org.apache.catalina.tribes.transport.AbstractRxTask;
import org.apache.catalina.tribes.transport.Constants;
import org.apache.catalina.tribes.transport.nio.NioReceiver;
import org.apache.catalina.tribes.util.Logs;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class NioReplicationTask
extends AbstractRxTask {
    private static final Log log = LogFactory.getLog(NioReplicationTask.class);
    protected static final StringManager sm = StringManager.getManager(NioReplicationTask.class);
    private ByteBuffer buffer = null;
    private SelectionKey key;
    private int rxBufSize;
    private final NioReceiver receiver;

    public NioReplicationTask(ListenCallback callback, NioReceiver receiver) {
        super(callback);
        this.receiver = receiver;
    }

    @Override
    public synchronized void run() {
        if (this.buffer == null) {
            int size = this.getRxBufSize();
            if (this.key.channel() instanceof DatagramChannel) {
                size = 65535;
            }
            this.buffer = (this.getOptions() & 4) == 4 ? ByteBuffer.allocateDirect(size) : ByteBuffer.allocate(size);
        } else {
            this.buffer.clear();
        }
        if (this.key == null) {
            return;
        }
        if (log.isTraceEnabled()) {
            log.trace((Object)("Servicing key:" + this.key));
        }
        try {
            ObjectReader reader = (ObjectReader)this.key.attachment();
            if (reader == null) {
                if (log.isTraceEnabled()) {
                    log.trace((Object)("No object reader, cancelling:" + this.key));
                }
                this.cancelKey(this.key);
            } else {
                if (log.isTraceEnabled()) {
                    log.trace((Object)("Draining channel:" + this.key));
                }
                this.drainChannel(this.key, reader);
            }
        }
        catch (Exception e) {
            if (!(e instanceof CancelledKeyException)) {
                if (e instanceof IOException) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("IOException in replication worker, unable to drain channel. Probable cause: Keep alive socket closed[" + e.getMessage() + "]."), (Throwable)e);
                    } else {
                        log.warn((Object)sm.getString("nioReplicationTask.unable.drainChannel.ioe", e.getMessage()));
                    }
                } else if (log.isErrorEnabled()) {
                    log.error((Object)sm.getString("nioReplicationTask.exception.drainChannel"), (Throwable)e);
                }
            }
            this.cancelKey(this.key);
        }
        this.key = null;
        this.getTaskPool().returnWorker(this);
    }

    public synchronized void serviceChannel(SelectionKey key) {
        ObjectReader reader;
        if (log.isTraceEnabled()) {
            log.trace((Object)("About to service key:" + key));
        }
        if ((reader = (ObjectReader)key.attachment()) != null) {
            reader.setLastAccess(System.currentTimeMillis());
        }
        this.key = key;
        key.interestOps(key.interestOps() & 0xFFFFFFFE);
        key.interestOps(key.interestOps() & 0xFFFFFFFB);
    }

    protected void drainChannel(SelectionKey key, ObjectReader reader) throws Exception {
        reader.access();
        ReadableByteChannel channel = (ReadableByteChannel)((Object)key.channel());
        int count = -1;
        SocketAddress saddr = null;
        if (channel instanceof SocketChannel) {
            while ((count = channel.read(this.buffer)) > 0) {
                this.buffer.flip();
                if (this.buffer.hasArray()) {
                    reader.append(this.buffer.array(), 0, count, false);
                } else {
                    reader.append(this.buffer, count, false);
                }
                this.buffer.clear();
                if (!reader.hasPackage()) continue;
                break;
            }
        } else if (channel instanceof DatagramChannel) {
            DatagramChannel dchannel = (DatagramChannel)channel;
            saddr = dchannel.receive(this.buffer);
            this.buffer.flip();
            if (this.buffer.hasArray()) {
                reader.append(this.buffer.array(), 0, this.buffer.limit() - this.buffer.position(), false);
            } else {
                reader.append(this.buffer, this.buffer.limit() - this.buffer.position(), false);
            }
            this.buffer.clear();
            count = reader.hasPackage() ? 1 : -1;
        }
        int pkgcnt = reader.count();
        if (count < 0 && pkgcnt == 0) {
            this.remoteEof(key);
            return;
        }
        ChannelMessage[] msgs = pkgcnt == 0 ? ChannelData.EMPTY_DATA_ARRAY : reader.execute();
        this.registerForRead(key, reader);
        for (ChannelMessage msg : msgs) {
            block22: {
                if (ChannelData.sendAckAsync(msg.getOptions())) {
                    this.sendAck(key, (WritableByteChannel)((Object)channel), Constants.ACK_COMMAND, saddr);
                }
                try {
                    if (Logs.MESSAGES.isTraceEnabled()) {
                        try {
                            Logs.MESSAGES.trace((Object)("NioReplicationThread - Received msg:" + new UniqueId(msg.getUniqueId()) + " at " + new Timestamp(System.currentTimeMillis())));
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                    }
                    this.getCallback().messageDataReceived(msg);
                    if (ChannelData.sendAckSync(msg.getOptions())) {
                        this.sendAck(key, (WritableByteChannel)((Object)channel), Constants.ACK_COMMAND, saddr);
                    }
                }
                catch (RemoteProcessException e) {
                    if (log.isDebugEnabled()) {
                        log.error((Object)sm.getString("nioReplicationTask.process.clusterMsg.failed"), (Throwable)e);
                    }
                    if (ChannelData.sendAckSync(msg.getOptions())) {
                        this.sendAck(key, (WritableByteChannel)((Object)channel), Constants.FAIL_ACK_COMMAND, saddr);
                    }
                }
                catch (Exception e) {
                    log.error((Object)sm.getString("nioReplicationTask.process.clusterMsg.failed"), (Throwable)e);
                    if (!ChannelData.sendAckSync(msg.getOptions())) break block22;
                    this.sendAck(key, (WritableByteChannel)((Object)channel), Constants.FAIL_ACK_COMMAND, saddr);
                }
            }
            if (!this.getUseBufferPool()) continue;
            BufferPool.getBufferPool().returnBuffer(msg.getMessage());
            msg.setMessage(null);
        }
        if (count < 0) {
            this.remoteEof(key);
        }
    }

    private void remoteEof(SelectionKey key) {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Channel closed on the remote end, disconnecting");
        }
        this.cancelKey(key);
    }

    protected void registerForRead(SelectionKey key, ObjectReader reader) {
        if (log.isTraceEnabled()) {
            log.trace((Object)("Adding key for read event:" + key));
        }
        reader.finish();
        Runnable r = () -> {
            try {
                if (key.isValid()) {
                    int resumeOps = key.interestOps() | 1;
                    key.interestOps(resumeOps);
                    if (log.isTraceEnabled()) {
                        log.trace((Object)("Registering key for read:" + key));
                    }
                }
            }
            catch (CancelledKeyException ckx) {
                NioReceiver.cancelledKey(key);
                if (log.isTraceEnabled()) {
                    log.trace((Object)("CKX Cancelling key:" + key));
                }
            }
            catch (Exception x) {
                log.error((Object)sm.getString("nioReplicationTask.error.register.key", key), (Throwable)x);
            }
        };
        this.receiver.addEvent(r);
    }

    private void cancelKey(SelectionKey key) {
        ObjectReader reader;
        if (log.isTraceEnabled()) {
            log.trace((Object)("Adding key for cancel event:" + key));
        }
        if ((reader = (ObjectReader)key.attachment()) != null) {
            reader.setCancelled(true);
            reader.finish();
        }
        Runnable cx = () -> {
            if (log.isTraceEnabled()) {
                log.trace((Object)("Cancelling key:" + key));
            }
            NioReceiver.cancelledKey(key);
        };
        this.receiver.addEvent(cx);
    }

    protected void sendAck(SelectionKey key, WritableByteChannel channel, byte[] command, SocketAddress udpaddr) {
        try {
            int total;
            ByteBuffer buf = ByteBuffer.wrap(command);
            if (channel instanceof DatagramChannel) {
                DatagramChannel dchannel = (DatagramChannel)channel;
                for (total = 0; total < command.length; total += dchannel.send(buf, udpaddr)) {
                }
            } else {
                while (total < command.length) {
                    total += channel.write(buf);
                }
            }
            if (log.isTraceEnabled()) {
                log.trace((Object)("ACK sent to " + (channel instanceof SocketChannel ? ((SocketChannel)channel).socket().getInetAddress() : ((DatagramChannel)channel).socket().getInetAddress())));
            }
        }
        catch (IOException x) {
            log.warn((Object)sm.getString("nioReplicationTask.unable.ack", x.getMessage()));
        }
    }

    public void setRxBufSize(int rxBufSize) {
        this.rxBufSize = rxBufSize;
    }

    public int getRxBufSize() {
        return this.rxBufSize;
    }
}

