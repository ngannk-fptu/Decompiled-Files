/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.transport.nio;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.Timestamp;
import java.util.Deque;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.catalina.tribes.io.ObjectReader;
import org.apache.catalina.tribes.transport.AbstractRxTask;
import org.apache.catalina.tribes.transport.ReceiverBase;
import org.apache.catalina.tribes.transport.RxTaskPool;
import org.apache.catalina.tribes.transport.nio.NioReceiverMBean;
import org.apache.catalina.tribes.transport.nio.NioReplicationTask;
import org.apache.catalina.tribes.util.ExceptionUtils;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class NioReceiver
extends ReceiverBase
implements Runnable,
NioReceiverMBean {
    private static final Log log = LogFactory.getLog(NioReceiver.class);
    protected static final StringManager sm = StringManager.getManager(NioReceiver.class);
    private volatile boolean running = false;
    private AtomicReference<Selector> selector = new AtomicReference();
    private ServerSocketChannel serverChannel = null;
    private DatagramChannel datagramChannel = null;
    protected final Deque<Runnable> events = new ConcurrentLinkedDeque<Runnable>();
    protected long lastCheck = System.currentTimeMillis();

    @Override
    public void stop() {
        this.stopListening();
        super.stop();
    }

    @Override
    public void start() throws IOException {
        super.start();
        try {
            this.setPool(new RxTaskPool(this.getMaxThreads(), this.getMinThreads(), this));
        }
        catch (Exception x) {
            log.fatal((Object)sm.getString("nioReceiver.threadpool.fail"), (Throwable)x);
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
            Thread t = new Thread((Runnable)this, "NioReceiver" + channelName);
            t.setDaemon(true);
            t.start();
        }
        catch (Exception x) {
            log.fatal((Object)sm.getString("nioReceiver.start.fail"), (Throwable)x);
            if (x instanceof IOException) {
                throw (IOException)x;
            }
            throw new IOException(x.getMessage());
        }
    }

    @Override
    public AbstractRxTask createRxTask() {
        NioReplicationTask thread = new NioReplicationTask(this, this);
        thread.setUseBufferPool(this.getUseBufferPool());
        thread.setRxBufSize(this.getRxBufSize());
        thread.setOptions(this.getWorkerThreadOptions());
        return thread;
    }

    protected void bind() throws IOException {
        this.serverChannel = ServerSocketChannel.open();
        ServerSocket serverSocket = this.serverChannel.socket();
        this.selector.set(Selector.open());
        this.bind(serverSocket, this.getPort(), this.getAutoBind());
        this.serverChannel.configureBlocking(false);
        this.serverChannel.register(this.selector.get(), 16);
        if (this.getUdpPort() > 0) {
            this.datagramChannel = DatagramChannel.open();
            this.configureDatagraChannel();
            this.bindUdp(this.datagramChannel.socket(), this.getUdpPort(), this.getAutoBind());
        }
    }

    private void configureDatagraChannel() throws IOException {
        this.datagramChannel.configureBlocking(false);
        this.datagramChannel.socket().setSendBufferSize(this.getUdpTxBufSize());
        this.datagramChannel.socket().setReceiveBufferSize(this.getUdpRxBufSize());
        this.datagramChannel.socket().setReuseAddress(this.getSoReuseAddress());
        this.datagramChannel.socket().setSoTimeout(this.getTimeout());
        this.datagramChannel.socket().setTrafficClass(this.getSoTrafficClass());
    }

    public void addEvent(Runnable event) {
        Selector selector = this.selector.get();
        if (selector != null) {
            this.events.add(event);
            if (log.isTraceEnabled()) {
                log.trace((Object)("Adding event to selector:" + event));
            }
            if (this.isListening()) {
                selector.wakeup();
            }
        }
    }

    public void events() {
        if (this.events.isEmpty()) {
            return;
        }
        Runnable r = null;
        while ((r = this.events.pollFirst()) != null) {
            try {
                if (log.isTraceEnabled()) {
                    log.trace((Object)("Processing event in selector:" + r));
                }
                r.run();
            }
            catch (Exception x) {
                log.error((Object)sm.getString("nioReceiver.eventsError"), (Throwable)x);
            }
        }
    }

    public static void cancelledKey(SelectionKey key) {
        block11: {
            block10: {
                block9: {
                    ObjectReader reader = (ObjectReader)key.attachment();
                    if (reader != null) {
                        reader.setCancelled(true);
                        reader.finish();
                    }
                    key.cancel();
                    key.attach(null);
                    if (key.channel() instanceof SocketChannel) {
                        try {
                            ((SocketChannel)key.channel()).socket().close();
                        }
                        catch (IOException e) {
                            if (!log.isDebugEnabled()) break block9;
                            log.debug((Object)"", (Throwable)e);
                        }
                    }
                }
                if (key.channel() instanceof DatagramChannel) {
                    try {
                        ((DatagramChannel)key.channel()).socket().close();
                    }
                    catch (Exception e) {
                        if (!log.isDebugEnabled()) break block10;
                        log.debug((Object)"", (Throwable)e);
                    }
                }
            }
            try {
                key.channel().close();
            }
            catch (IOException e) {
                if (!log.isDebugEnabled()) break block11;
                log.debug((Object)"", (Throwable)e);
            }
        }
    }

    protected void socketTimeouts() {
        Set<SelectionKey> keys;
        long now = System.currentTimeMillis();
        if (now - this.lastCheck < this.getSelectorTimeout()) {
            return;
        }
        Selector tmpsel = this.selector.get();
        Set<SelectionKey> set = keys = this.isListening() && tmpsel != null ? tmpsel.keys() : null;
        if (keys == null) {
            return;
        }
        for (SelectionKey key : keys) {
            try {
                if (key.interestOps() != 0) continue;
                ObjectReader ka = (ObjectReader)key.attachment();
                if (ka != null) {
                    long delta = now - ka.getLastAccess();
                    if (delta <= (long)this.getTimeout() || ka.isAccessed()) continue;
                    if (log.isWarnEnabled()) {
                        log.warn((Object)sm.getString("nioReceiver.threadsExhausted", this.getTimeout(), ka.isCancelled(), key, new Timestamp(ka.getLastAccess())));
                    }
                    ka.setLastAccess(now);
                    continue;
                }
                NioReceiver.cancelledKey(key);
            }
            catch (CancelledKeyException ckx) {
                NioReceiver.cancelledKey(key);
            }
        }
        this.lastCheck = System.currentTimeMillis();
    }

    protected void listen() throws Exception {
        if (this.doListen()) {
            log.warn((Object)sm.getString("nioReceiver.alreadyStarted"));
            return;
        }
        this.setListen(true);
        Selector selector = this.selector.get();
        if (selector != null && this.datagramChannel != null) {
            ObjectReader oreader = new ObjectReader(65535);
            this.registerChannel(selector, this.datagramChannel, 1, oreader);
        }
        while (this.doListen() && selector != null) {
            try {
                this.events();
                this.socketTimeouts();
                int n = selector.select(this.getSelectorTimeout());
                if (n == 0) continue;
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it != null && it.hasNext()) {
                    SelectionKey key = it.next();
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel)key.channel();
                        SocketChannel channel = server.accept();
                        channel.socket().setReceiveBufferSize(this.getRxBufSize());
                        channel.socket().setSendBufferSize(this.getTxBufSize());
                        channel.socket().setTcpNoDelay(this.getTcpNoDelay());
                        channel.socket().setKeepAlive(this.getSoKeepAlive());
                        channel.socket().setOOBInline(this.getOoBInline());
                        channel.socket().setReuseAddress(this.getSoReuseAddress());
                        channel.socket().setSoLinger(this.getSoLingerOn(), this.getSoLingerTime());
                        channel.socket().setSoTimeout(this.getTimeout());
                        ObjectReader attach = new ObjectReader(channel);
                        this.registerChannel(selector, channel, 1, attach);
                    }
                    if (key.isReadable()) {
                        this.readDataFromSocket(key);
                    } else {
                        key.interestOps(key.interestOps() & 0xFFFFFFFB);
                    }
                    it.remove();
                }
            }
            catch (ClosedSelectorException n) {
            }
            catch (CancelledKeyException nx) {
                log.warn((Object)sm.getString("nioReceiver.clientDisconnect"));
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                log.error((Object)sm.getString("nioReceiver.requestError"), t);
            }
        }
        this.serverChannel.close();
        if (this.datagramChannel != null) {
            block14: {
                try {
                    this.datagramChannel.close();
                }
                catch (Exception iox) {
                    if (!log.isDebugEnabled()) break block14;
                    log.debug((Object)"Unable to close datagram channel.", (Throwable)iox);
                }
            }
            this.datagramChannel = null;
        }
        this.closeSelector();
    }

    protected void stopListening() {
        this.setListen(false);
        Selector selector = this.selector.get();
        if (selector != null) {
            try {
                selector.wakeup();
                for (int count = 0; this.running && count < 50; ++count) {
                    Thread.sleep(100L);
                }
                if (this.running) {
                    log.warn((Object)sm.getString("nioReceiver.stop.threadRunning"));
                }
                this.closeSelector();
            }
            catch (Exception x) {
                log.error((Object)sm.getString("nioReceiver.stop.fail"), (Throwable)x);
            }
            finally {
                this.selector.set(null);
            }
        }
    }

    private void closeSelector() throws IOException {
        Selector selector = this.selector.getAndSet(null);
        if (selector == null) {
            return;
        }
        try {
            for (SelectionKey key : selector.keys()) {
                key.channel().close();
                key.attach(null);
                key.cancel();
            }
        }
        catch (IOException ignore) {
            if (log.isWarnEnabled()) {
                log.warn((Object)sm.getString("nioReceiver.cleanup.fail"), (Throwable)ignore);
            }
        }
        catch (ClosedSelectorException ignore) {
            // empty catch block
        }
        try {
            selector.selectNow();
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
        }
        selector.close();
    }

    protected void registerChannel(Selector selector, SelectableChannel channel, int ops, Object attach) throws Exception {
        if (channel == null) {
            return;
        }
        channel.configureBlocking(false);
        channel.register(selector, ops, attach);
    }

    @Override
    public void run() {
        this.running = true;
        try {
            this.listen();
        }
        catch (Exception x) {
            log.error((Object)sm.getString("nioReceiver.run.fail"), (Throwable)x);
        }
        finally {
            this.running = false;
        }
    }

    protected void readDataFromSocket(SelectionKey key) throws Exception {
        NioReplicationTask task = (NioReplicationTask)this.getTaskPool().getRxTask();
        if (task == null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)"No TcpReplicationThread available");
            }
        } else {
            task.serviceChannel(key);
            this.getExecutor().execute(task);
        }
    }
}

