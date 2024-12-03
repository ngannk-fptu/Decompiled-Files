/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.transport.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.catalina.tribes.io.ObjectReader;
import org.apache.catalina.tribes.transport.AbstractRxTask;
import org.apache.catalina.tribes.transport.ReceiverBase;
import org.apache.catalina.tribes.transport.RxTaskPool;
import org.apache.catalina.tribes.transport.bio.BioReplicationTask;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

@Deprecated
public class BioReceiver
extends ReceiverBase
implements Runnable {
    private static final Log log = LogFactory.getLog(BioReceiver.class);
    protected static final StringManager sm = StringManager.getManager(BioReceiver.class);
    protected ServerSocket serverSocket;

    @Override
    public void start() throws IOException {
        super.start();
        try {
            this.setPool(new RxTaskPool(this.getMaxThreads(), this.getMinThreads(), this));
        }
        catch (Exception x) {
            log.fatal((Object)sm.getString("bioReceiver.threadpool.fail"), (Throwable)x);
            if (x instanceof IOException) {
                throw (IOException)x;
            }
            throw new IOException(x.getMessage());
        }
        try {
            this.getBind();
            this.bind();
            String channelName = "";
            if (this.getChannel().getName() != null) {
                channelName = "[" + this.getChannel().getName() + "]";
            }
            Thread t = new Thread((Runnable)this, "BioReceiver" + channelName);
            t.setDaemon(true);
            t.start();
        }
        catch (Exception x) {
            log.fatal((Object)sm.getString("bioReceiver.start.fail"), (Throwable)x);
            if (x instanceof IOException) {
                throw (IOException)x;
            }
            throw new IOException(x.getMessage());
        }
    }

    @Override
    public AbstractRxTask createRxTask() {
        return this.getReplicationThread();
    }

    protected BioReplicationTask getReplicationThread() {
        BioReplicationTask result = new BioReplicationTask(this);
        result.setOptions(this.getWorkerThreadOptions());
        result.setUseBufferPool(this.getUseBufferPool());
        return result;
    }

    @Override
    public void stop() {
        block2: {
            this.setListen(false);
            try {
                this.serverSocket.close();
            }
            catch (Exception x) {
                if (!log.isDebugEnabled()) break block2;
                log.debug((Object)sm.getString("bioReceiver.socket.closeFailed"), (Throwable)x);
            }
        }
        super.stop();
    }

    protected void bind() throws IOException {
        this.serverSocket = new ServerSocket();
        this.bind(this.serverSocket, this.getPort(), this.getAutoBind());
    }

    @Override
    public void run() {
        try {
            this.listen();
        }
        catch (Exception x) {
            log.error((Object)sm.getString("bioReceiver.run.fail"), (Throwable)x);
        }
    }

    public void listen() throws Exception {
        if (this.doListen()) {
            log.warn((Object)sm.getString("bioReceiver.already.started"));
            return;
        }
        this.setListen(true);
        while (this.doListen()) {
            BioReplicationTask task;
            Socket socket;
            block6: {
                socket = null;
                if (this.getTaskPool().available() < 1 && log.isWarnEnabled()) {
                    log.warn((Object)sm.getString("bioReceiver.threads.busy"));
                }
                if ((task = (BioReplicationTask)this.getTaskPool().getRxTask()) == null) continue;
                try {
                    socket = this.serverSocket.accept();
                }
                catch (Exception x) {
                    if (!this.doListen()) break block6;
                    throw x;
                }
            }
            if (!this.doListen()) {
                task.serviceSocket(null, null);
                this.getExecutor().execute(task);
                task.close();
                break;
            }
            if (socket == null) continue;
            socket.setReceiveBufferSize(this.getRxBufSize());
            socket.setSendBufferSize(this.getTxBufSize());
            socket.setTcpNoDelay(this.getTcpNoDelay());
            socket.setKeepAlive(this.getSoKeepAlive());
            socket.setOOBInline(this.getOoBInline());
            socket.setReuseAddress(this.getSoReuseAddress());
            socket.setSoLinger(this.getSoLingerOn(), this.getSoLingerTime());
            socket.setSoTimeout(this.getTimeout());
            ObjectReader reader = new ObjectReader(socket);
            task.serviceSocket(socket, reader);
            this.getExecutor().execute(task);
        }
    }
}

