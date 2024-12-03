/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.transport.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.io.BufferPool;
import org.apache.catalina.tribes.io.ChannelData;
import org.apache.catalina.tribes.io.ListenCallback;
import org.apache.catalina.tribes.io.ObjectReader;
import org.apache.catalina.tribes.transport.AbstractRxTask;
import org.apache.catalina.tribes.transport.Constants;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

@Deprecated
public class BioReplicationTask
extends AbstractRxTask {
    private static final Log log = LogFactory.getLog(BioReplicationTask.class);
    protected static final StringManager sm = StringManager.getManager(BioReplicationTask.class);
    protected Socket socket;
    protected ObjectReader reader;

    public BioReplicationTask(ListenCallback callback) {
        super(callback);
    }

    @Override
    public synchronized void run() {
        if (this.socket == null) {
            return;
        }
        try {
            this.drainSocket();
        }
        catch (Exception x) {
            log.error((Object)sm.getString("bioReplicationTask.unable.service"), (Throwable)x);
        }
        finally {
            block21: {
                block20: {
                    try {
                        this.socket.close();
                    }
                    catch (Exception e) {
                        if (!log.isDebugEnabled()) break block20;
                        log.debug((Object)sm.getString("bioReplicationTask.socket.closeFailed"), (Throwable)e);
                    }
                }
                try {
                    this.reader.close();
                }
                catch (Exception e) {
                    if (!log.isDebugEnabled()) break block21;
                    log.debug((Object)sm.getString("bioReplicationTask.reader.closeFailed"), (Throwable)e);
                }
            }
            this.reader = null;
            this.socket = null;
        }
        if (this.getTaskPool() != null) {
            this.getTaskPool().returnWorker(this);
        }
    }

    public synchronized void serviceSocket(Socket socket, ObjectReader reader) {
        this.socket = socket;
        this.reader = reader;
    }

    protected void execute(ObjectReader reader) throws Exception {
        int pkgcnt = reader.count();
        if (pkgcnt > 0) {
            ChannelMessage[] msgs = reader.execute();
            for (int i = 0; i < msgs.length; ++i) {
                if (ChannelData.sendAckAsync(msgs[i].getOptions())) {
                    this.sendAck(Constants.ACK_COMMAND);
                }
                try {
                    this.getCallback().messageDataReceived(msgs[i]);
                    if (ChannelData.sendAckSync(msgs[i].getOptions())) {
                        this.sendAck(Constants.ACK_COMMAND);
                    }
                }
                catch (Exception x) {
                    if (ChannelData.sendAckSync(msgs[i].getOptions())) {
                        this.sendAck(Constants.FAIL_ACK_COMMAND);
                    }
                    log.error((Object)sm.getString("bioReplicationTask.messageDataReceived.error"), (Throwable)x);
                }
                if (!this.getUseBufferPool()) continue;
                BufferPool.getBufferPool().returnBuffer(msgs[i].getMessage());
                msgs[i].setMessage(null);
            }
        }
    }

    protected void drainSocket() throws Exception {
        InputStream in = this.socket.getInputStream();
        byte[] buf = new byte[1024];
        int length = in.read(buf);
        while (length >= 0) {
            int count = this.reader.append(buf, 0, length, true);
            if (count > 0) {
                this.execute(this.reader);
            }
            length = in.read(buf);
        }
    }

    protected void sendAck(byte[] command) {
        try {
            OutputStream out = this.socket.getOutputStream();
            out.write(command);
            out.flush();
            if (log.isTraceEnabled()) {
                log.trace((Object)("ACK sent to " + this.socket.getPort()));
            }
        }
        catch (IOException x) {
            log.warn((Object)sm.getString("bioReplicationTask.unable.sendAck", x.getMessage()));
        }
    }

    @Override
    public void close() {
        block5: {
            block4: {
                try {
                    this.socket.close();
                }
                catch (Exception e) {
                    if (!log.isDebugEnabled()) break block4;
                    log.debug((Object)sm.getString("bioReplicationTask.socket.closeFailed"), (Throwable)e);
                }
            }
            try {
                this.reader.close();
            }
            catch (Exception e) {
                if (!log.isDebugEnabled()) break block5;
                log.debug((Object)sm.getString("bioReplicationTask.reader.closeFailed"), (Throwable)e);
            }
        }
        this.reader = null;
        this.socket = null;
        super.close();
    }
}

