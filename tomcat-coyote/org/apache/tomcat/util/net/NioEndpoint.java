/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.collections.SynchronizedQueue
 *  org.apache.tomcat.util.collections.SynchronizedStack
 *  org.apache.tomcat.util.compat.JreCompat
 *  org.apache.tomcat.util.compat.JrePlatform
 */
package org.apache.tomcat.util.net;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.channels.NetworkChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.net.ssl.SSLEngine;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.collections.SynchronizedQueue;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.compat.JrePlatform;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.AbstractJsseEndpoint;
import org.apache.tomcat.util.net.Acceptor;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.net.NioChannel;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SecureNioChannel;
import org.apache.tomcat.util.net.SendfileDataBase;
import org.apache.tomcat.util.net.SendfileState;
import org.apache.tomcat.util.net.SocketBufferHandler;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketProcessorBase;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.net.jsse.JSSESupport;

public class NioEndpoint
extends AbstractJsseEndpoint<NioChannel, SocketChannel> {
    private static final Log log = LogFactory.getLog(NioEndpoint.class);
    private static final Log logCertificate = LogFactory.getLog((String)(NioEndpoint.class.getName() + ".certificate"));
    private static final Log logHandshake = LogFactory.getLog((String)(NioEndpoint.class.getName() + ".handshake"));
    public static final int OP_REGISTER = 256;
    private volatile ServerSocketChannel serverSock = null;
    private volatile CountDownLatch stopLatch = null;
    private SynchronizedStack<PollerEvent> eventCache;
    private SynchronizedStack<NioChannel> nioChannels;
    private SocketAddress previousAcceptedSocketRemoteAddress = null;
    private long previousAcceptedSocketNanoTime = 0L;
    private boolean useInheritedChannel = false;
    private String unixDomainSocketPath = null;
    private String unixDomainSocketPathPermissions = null;
    private int pollerThreadPriority = 5;
    private long selectorTimeout = 1000L;
    private Poller poller = null;

    public void setUseInheritedChannel(boolean useInheritedChannel) {
        this.useInheritedChannel = useInheritedChannel;
    }

    public boolean getUseInheritedChannel() {
        return this.useInheritedChannel;
    }

    public String getUnixDomainSocketPath() {
        return this.unixDomainSocketPath;
    }

    public void setUnixDomainSocketPath(String unixDomainSocketPath) {
        this.unixDomainSocketPath = unixDomainSocketPath;
    }

    public String getUnixDomainSocketPathPermissions() {
        return this.unixDomainSocketPathPermissions;
    }

    public void setUnixDomainSocketPathPermissions(String unixDomainSocketPathPermissions) {
        this.unixDomainSocketPathPermissions = unixDomainSocketPathPermissions;
    }

    public void setPollerThreadPriority(int pollerThreadPriority) {
        this.pollerThreadPriority = pollerThreadPriority;
    }

    public int getPollerThreadPriority() {
        return this.pollerThreadPriority;
    }

    @Deprecated
    public void setPollerThreadCount(int pollerThreadCount) {
    }

    @Deprecated
    public int getPollerThreadCount() {
        return 1;
    }

    public void setSelectorTimeout(long timeout) {
        this.selectorTimeout = timeout;
    }

    public long getSelectorTimeout() {
        return this.selectorTimeout;
    }

    @Override
    public boolean getDeferAccept() {
        return false;
    }

    public int getKeepAliveCount() {
        if (this.poller == null) {
            return 0;
        }
        return this.poller.getKeyCount();
    }

    @Override
    public String getId() {
        if (this.getUseInheritedChannel()) {
            return "JVMInheritedChannel";
        }
        if (this.getUnixDomainSocketPath() != null) {
            return this.getUnixDomainSocketPath();
        }
        return null;
    }

    @Override
    public void bind() throws Exception {
        this.initServerSocket();
        this.setStopLatch(new CountDownLatch(1));
        this.initialiseSsl();
    }

    protected void initServerSocket() throws Exception {
        if (this.getUseInheritedChannel()) {
            Channel ic = System.inheritedChannel();
            if (ic instanceof ServerSocketChannel) {
                this.serverSock = (ServerSocketChannel)ic;
            }
            if (this.serverSock == null) {
                throw new IllegalArgumentException(sm.getString("endpoint.init.bind.inherited"));
            }
        } else if (this.getUnixDomainSocketPath() != null) {
            SocketAddress sa = JreCompat.getInstance().getUnixDomainSocketAddress(this.getUnixDomainSocketPath());
            this.serverSock = JreCompat.getInstance().openUnixDomainServerSocketChannel();
            this.serverSock.bind(sa, this.getAcceptCount());
            if (this.getUnixDomainSocketPathPermissions() != null) {
                Path path = Paths.get(this.getUnixDomainSocketPath(), new String[0]);
                Set<PosixFilePermission> permissions = PosixFilePermissions.fromString(this.getUnixDomainSocketPathPermissions());
                if (path.getFileSystem().supportedFileAttributeViews().contains("posix")) {
                    FileAttribute<Set<PosixFilePermission>> attrs = PosixFilePermissions.asFileAttribute(permissions);
                    Files.setAttribute(path, attrs.name(), attrs.value(), new LinkOption[0]);
                } else {
                    File file = path.toFile();
                    if (permissions.contains((Object)PosixFilePermission.OTHERS_READ) && !file.setReadable(true, false)) {
                        log.warn((Object)sm.getString("endpoint.nio.perms.readFail", new Object[]{file.getPath()}));
                    }
                    if (permissions.contains((Object)PosixFilePermission.OTHERS_WRITE) && !file.setWritable(true, false)) {
                        log.warn((Object)sm.getString("endpoint.nio.perms.writeFail", new Object[]{file.getPath()}));
                    }
                }
            }
        } else {
            this.serverSock = ServerSocketChannel.open();
            this.socketProperties.setProperties(this.serverSock.socket());
            InetSocketAddress addr = new InetSocketAddress(this.getAddress(), this.getPortWithOffset());
            this.serverSock.bind(addr, this.getAcceptCount());
        }
        this.serverSock.configureBlocking(true);
    }

    @Override
    public void startInternal() throws Exception {
        if (!this.running) {
            this.running = true;
            this.paused = false;
            if (this.socketProperties.getProcessorCache() != 0) {
                this.processorCache = new SynchronizedStack(128, this.socketProperties.getProcessorCache());
            }
            if (this.socketProperties.getEventCache() != 0) {
                this.eventCache = new SynchronizedStack(128, this.socketProperties.getEventCache());
            }
            if (this.socketProperties.getBufferPool() != 0) {
                this.nioChannels = new SynchronizedStack(128, this.socketProperties.getBufferPool());
            }
            if (this.getExecutor() == null) {
                this.createExecutor();
            }
            this.initializeConnectionLatch();
            this.poller = new Poller();
            Thread pollerThread = new Thread((Runnable)this.poller, this.getName() + "-Poller");
            pollerThread.setPriority(this.threadPriority);
            pollerThread.setDaemon(true);
            pollerThread.start();
            this.startAcceptorThread();
        }
    }

    @Override
    public void stopInternal() {
        if (!this.paused) {
            this.pause();
        }
        if (this.running) {
            this.running = false;
            this.acceptor.stop(10);
            if (this.poller != null) {
                this.poller.destroy();
                this.poller = null;
            }
            try {
                if (!this.getStopLatch().await(this.selectorTimeout + 100L, TimeUnit.MILLISECONDS)) {
                    log.warn((Object)sm.getString("endpoint.nio.stopLatchAwaitFail"));
                }
            }
            catch (InterruptedException e) {
                log.warn((Object)sm.getString("endpoint.nio.stopLatchAwaitInterrupted"), (Throwable)e);
            }
            this.shutdownExecutor();
            if (this.eventCache != null) {
                this.eventCache.clear();
                this.eventCache = null;
            }
            if (this.nioChannels != null) {
                NioChannel socket;
                while ((socket = (NioChannel)this.nioChannels.pop()) != null) {
                    socket.free();
                }
                this.nioChannels = null;
            }
            if (this.processorCache != null) {
                this.processorCache.clear();
                this.processorCache = null;
            }
        }
    }

    @Override
    public void unbind() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Destroy initiated for " + new InetSocketAddress(this.getAddress(), this.getPortWithOffset())));
        }
        if (this.running) {
            this.stop();
        }
        try {
            this.doCloseServerSocket();
        }
        catch (IOException ioe) {
            this.getLog().warn((Object)sm.getString("endpoint.serverSocket.closeFailed", new Object[]{this.getName()}), (Throwable)ioe);
        }
        this.destroySsl();
        super.unbind();
        if (this.getHandler() != null) {
            this.getHandler().recycle();
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Destroy completed for " + new InetSocketAddress(this.getAddress(), this.getPortWithOffset())));
        }
    }

    @Override
    protected void doCloseServerSocket() throws IOException {
        try {
            if (!this.getUseInheritedChannel() && this.serverSock != null) {
                this.serverSock.close();
            }
            this.serverSock = null;
        }
        finally {
            if (this.getUnixDomainSocketPath() != null && this.getBindState().wasBound()) {
                Files.delete(Paths.get(this.getUnixDomainSocketPath(), new String[0]));
            }
        }
    }

    @Override
    protected void unlockAccept() {
        block13: {
            if (this.getUnixDomainSocketPath() == null) {
                super.unlockAccept();
            } else {
                if (this.acceptor == null || this.acceptor.getState() != Acceptor.AcceptorState.RUNNING) {
                    return;
                }
                try {
                    SocketAddress sa = JreCompat.getInstance().getUnixDomainSocketAddress(this.getUnixDomainSocketPath());
                    try (SocketChannel socket = JreCompat.getInstance().openUnixDomainSocketChannel();){
                        socket.connect(sa);
                    }
                    for (long waitLeft = 1000L; waitLeft > 0L && this.acceptor.getState() == Acceptor.AcceptorState.RUNNING; waitLeft -= 5L) {
                        Thread.sleep(5L);
                    }
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    if (!this.getLog().isDebugEnabled()) break block13;
                    this.getLog().debug((Object)sm.getString("endpoint.debug.unlock.fail", new Object[]{String.valueOf(this.getPortWithOffset())}), t);
                }
            }
        }
    }

    protected SynchronizedStack<NioChannel> getNioChannels() {
        return this.nioChannels;
    }

    protected Poller getPoller() {
        return this.poller;
    }

    protected CountDownLatch getStopLatch() {
        return this.stopLatch;
    }

    protected void setStopLatch(CountDownLatch stopLatch) {
        this.stopLatch = stopLatch;
    }

    @Override
    protected boolean setSocketOptions(SocketChannel socket) {
        NioSocketWrapper socketWrapper = null;
        try {
            NioChannel channel = null;
            if (this.nioChannels != null) {
                channel = (NioChannel)this.nioChannels.pop();
            }
            if (channel == null) {
                SocketBufferHandler bufhandler = new SocketBufferHandler(this.socketProperties.getAppReadBufSize(), this.socketProperties.getAppWriteBufSize(), this.socketProperties.getDirectBuffer());
                channel = this.isSSLEnabled() ? new SecureNioChannel(bufhandler, this) : new NioChannel(bufhandler);
            }
            NioSocketWrapper newWrapper = new NioSocketWrapper(channel, this);
            channel.reset(socket, newWrapper);
            this.connections.put(socket, newWrapper);
            socketWrapper = newWrapper;
            socket.configureBlocking(false);
            if (this.getUnixDomainSocketPath() == null) {
                this.socketProperties.setProperties(socket.socket());
            }
            socketWrapper.setReadTimeout(this.getConnectionTimeout());
            socketWrapper.setWriteTimeout(this.getConnectionTimeout());
            socketWrapper.setKeepAliveLeft(this.getMaxKeepAliveRequests());
            this.poller.register(socketWrapper);
            return true;
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            try {
                log.error((Object)sm.getString("endpoint.socketOptionsError"), t);
            }
            catch (Throwable tt) {
                ExceptionUtils.handleThrowable((Throwable)tt);
            }
            if (socketWrapper == null) {
                this.destroySocket(socket);
            }
            return false;
        }
    }

    @Override
    protected void destroySocket(SocketChannel socket) {
        block2: {
            this.countDownConnection();
            try {
                socket.close();
            }
            catch (IOException ioe) {
                if (!log.isDebugEnabled()) break block2;
                log.debug((Object)sm.getString("endpoint.err.close"), (Throwable)ioe);
            }
        }
    }

    @Override
    protected NetworkChannel getServerSocket() {
        return this.serverSock;
    }

    @Override
    protected SocketChannel serverSocketAccept() throws Exception {
        SocketChannel result = this.serverSock.accept();
        if (!JrePlatform.IS_WINDOWS && this.getUnixDomainSocketPath() == null) {
            SocketAddress currentRemoteAddress = result.getRemoteAddress();
            long currentNanoTime = System.nanoTime();
            if (currentRemoteAddress.equals(this.previousAcceptedSocketRemoteAddress) && currentNanoTime - this.previousAcceptedSocketNanoTime < 1000L) {
                throw new IOException(sm.getString("endpoint.err.duplicateAccept"));
            }
            this.previousAcceptedSocketRemoteAddress = currentRemoteAddress;
            this.previousAcceptedSocketNanoTime = currentNanoTime;
        }
        return result;
    }

    @Override
    protected Log getLog() {
        return log;
    }

    @Override
    protected Log getLogCertificate() {
        return logCertificate;
    }

    @Override
    protected SocketProcessorBase<NioChannel> createSocketProcessor(SocketWrapperBase<NioChannel> socketWrapper, SocketEvent event) {
        return new SocketProcessor(socketWrapper, event);
    }

    public class Poller
    implements Runnable {
        private Selector selector;
        private final SynchronizedQueue<PollerEvent> events = new SynchronizedQueue();
        private volatile boolean close = false;
        private long nextExpiration = 0L;
        private AtomicLong wakeupCounter = new AtomicLong(0L);
        private volatile int keyCount = 0;

        public Poller() throws IOException {
            this.selector = Selector.open();
        }

        public int getKeyCount() {
            return this.keyCount;
        }

        public Selector getSelector() {
            return this.selector;
        }

        protected void destroy() {
            this.close = true;
            this.selector.wakeup();
        }

        private void addEvent(PollerEvent event) {
            this.events.offer((Object)event);
            if (this.wakeupCounter.incrementAndGet() == 0L) {
                this.selector.wakeup();
            }
        }

        private PollerEvent createPollerEvent(NioSocketWrapper socketWrapper, int interestOps) {
            PollerEvent r = null;
            if (NioEndpoint.this.eventCache != null) {
                r = (PollerEvent)NioEndpoint.this.eventCache.pop();
            }
            if (r == null) {
                r = new PollerEvent(socketWrapper, interestOps);
            } else {
                r.reset(socketWrapper, interestOps);
            }
            return r;
        }

        public void add(NioSocketWrapper socketWrapper, int interestOps) {
            PollerEvent pollerEvent = this.createPollerEvent(socketWrapper, interestOps);
            this.addEvent(pollerEvent);
            if (this.close) {
                NioEndpoint.this.processSocket(socketWrapper, SocketEvent.STOP, false);
            }
        }

        public boolean events() {
            boolean result = false;
            PollerEvent pe = null;
            int size = this.events.size();
            for (int i = 0; i < size && (pe = (PollerEvent)this.events.poll()) != null; ++i) {
                result = true;
                NioSocketWrapper socketWrapper = pe.getSocketWrapper();
                SocketChannel sc = ((NioChannel)socketWrapper.getSocket()).getIOChannel();
                int interestOps = pe.getInterestOps();
                if (sc == null) {
                    log.warn((Object)AbstractEndpoint.sm.getString("endpoint.nio.nullSocketChannel"));
                    socketWrapper.close();
                } else if (interestOps == 256) {
                    try {
                        sc.register(this.getSelector(), 1, socketWrapper);
                    }
                    catch (Exception x) {
                        log.error((Object)AbstractEndpoint.sm.getString("endpoint.nio.registerFail"), (Throwable)x);
                    }
                } else {
                    SelectionKey key = sc.keyFor(this.getSelector());
                    if (key == null) {
                        socketWrapper.close();
                    } else {
                        NioSocketWrapper attachment = (NioSocketWrapper)key.attachment();
                        if (attachment != null) {
                            try {
                                int ops = key.interestOps() | interestOps;
                                attachment.interestOps(ops);
                                key.interestOps(ops);
                            }
                            catch (CancelledKeyException ckx) {
                                this.cancelledKey(key, socketWrapper);
                            }
                        } else {
                            this.cancelledKey(key, socketWrapper);
                        }
                    }
                }
                if (!NioEndpoint.this.running || NioEndpoint.this.eventCache == null) continue;
                pe.reset();
                NioEndpoint.this.eventCache.push((Object)pe);
            }
            return result;
        }

        public void register(NioSocketWrapper socketWrapper) {
            socketWrapper.interestOps(1);
            PollerEvent pollerEvent = this.createPollerEvent(socketWrapper, 256);
            this.addEvent(pollerEvent);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void cancelledKey(SelectionKey sk, SocketWrapperBase<NioChannel> socketWrapper) {
            if (JreCompat.isJre11Available() && socketWrapper != null) {
                socketWrapper.close();
            } else {
                try {
                    if (sk != null) {
                        sk.attach(null);
                        if (sk.isValid()) {
                            sk.cancel();
                        }
                    }
                }
                catch (Throwable e) {
                    ExceptionUtils.handleThrowable((Throwable)e);
                    if (log.isDebugEnabled()) {
                        log.error((Object)AbstractEndpoint.sm.getString("endpoint.debug.channelCloseFail"), e);
                    }
                }
                finally {
                    if (socketWrapper != null) {
                        socketWrapper.close();
                    }
                }
            }
        }

        @Override
        public void run() {
            while (true) {
                Iterator<SelectionKey> iterator;
                boolean hasEvents;
                block8: {
                    hasEvents = false;
                    try {
                        if (!this.close) {
                            hasEvents = this.events();
                            this.keyCount = this.wakeupCounter.getAndSet(-1L) > 0L ? this.selector.selectNow() : this.selector.select(NioEndpoint.this.selectorTimeout);
                            this.wakeupCounter.set(0L);
                        }
                        if (this.close) {
                            this.events();
                            this.timeout(0, false);
                            try {
                                this.selector.close();
                            }
                            catch (IOException ioe) {
                                log.error((Object)AbstractEndpoint.sm.getString("endpoint.nio.selectorCloseFail"), (Throwable)ioe);
                            }
                            break;
                        }
                        if (this.keyCount != 0) break block8;
                        hasEvents |= this.events();
                    }
                    catch (Throwable x) {
                        ExceptionUtils.handleThrowable((Throwable)x);
                        log.error((Object)AbstractEndpoint.sm.getString("endpoint.nio.selectorLoopError"), x);
                        continue;
                    }
                }
                Iterator<SelectionKey> iterator2 = iterator = this.keyCount > 0 ? this.selector.selectedKeys().iterator() : null;
                while (iterator != null && iterator.hasNext()) {
                    SelectionKey sk = iterator.next();
                    iterator.remove();
                    NioSocketWrapper socketWrapper = (NioSocketWrapper)sk.attachment();
                    if (socketWrapper == null) continue;
                    this.processKey(sk, socketWrapper);
                }
                this.timeout(this.keyCount, hasEvents);
            }
            NioEndpoint.this.getStopLatch().countDown();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void processKey(SelectionKey sk, NioSocketWrapper socketWrapper) {
            try {
                if (this.close) {
                    this.cancelledKey(sk, socketWrapper);
                } else if (sk.isValid()) {
                    if (sk.isReadable() || sk.isWritable()) {
                        if (socketWrapper.getSendfileData() != null) {
                            this.processSendfile(sk, socketWrapper, false);
                        } else {
                            Object object;
                            this.unreg(sk, socketWrapper, sk.readyOps());
                            boolean closeSocket = false;
                            if (sk.isReadable()) {
                                if (socketWrapper.readOperation != null) {
                                    if (!socketWrapper.readOperation.process()) {
                                        closeSocket = true;
                                    }
                                } else if (socketWrapper.readBlocking) {
                                    object = socketWrapper.readLock;
                                    synchronized (object) {
                                        socketWrapper.readBlocking = false;
                                        socketWrapper.readLock.notify();
                                    }
                                } else if (!NioEndpoint.this.processSocket(socketWrapper, SocketEvent.OPEN_READ, true)) {
                                    closeSocket = true;
                                }
                            }
                            if (!closeSocket && sk.isWritable()) {
                                if (socketWrapper.writeOperation != null) {
                                    if (!socketWrapper.writeOperation.process()) {
                                        closeSocket = true;
                                    }
                                } else if (socketWrapper.writeBlocking) {
                                    object = socketWrapper.writeLock;
                                    synchronized (object) {
                                        socketWrapper.writeBlocking = false;
                                        socketWrapper.writeLock.notify();
                                    }
                                } else if (!NioEndpoint.this.processSocket(socketWrapper, SocketEvent.OPEN_WRITE, true)) {
                                    closeSocket = true;
                                }
                            }
                            if (closeSocket) {
                                this.cancelledKey(sk, socketWrapper);
                            }
                        }
                    }
                } else {
                    this.cancelledKey(sk, socketWrapper);
                }
            }
            catch (CancelledKeyException ckx) {
                this.cancelledKey(sk, socketWrapper);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                log.error((Object)AbstractEndpoint.sm.getString("endpoint.nio.keyProcessingError"), t);
            }
        }

        public SendfileState processSendfile(SelectionKey sk, NioSocketWrapper socketWrapper, boolean calledByProcessor) {
            NioChannel sc = null;
            try {
                ScatteringByteChannel wc;
                this.unreg(sk, socketWrapper, sk.readyOps());
                SendfileData sd = socketWrapper.getSendfileData();
                if (log.isTraceEnabled()) {
                    log.trace((Object)("Processing send file for: " + sd.fileName));
                }
                if (sd.fchannel == null) {
                    File f = new File(sd.fileName);
                    FileInputStream fis = new FileInputStream(f);
                    sd.fchannel = fis.getChannel();
                }
                ScatteringByteChannel scatteringByteChannel = wc = (sc = (NioChannel)socketWrapper.getSocket()) instanceof SecureNioChannel ? sc : sc.getIOChannel();
                if (sc.getOutboundRemaining() > 0) {
                    if (sc.flushOutbound()) {
                        socketWrapper.updateLastWrite();
                    }
                } else {
                    long written = sd.fchannel.transferTo(sd.pos, sd.length, (WritableByteChannel)((Object)wc));
                    if (written > 0L) {
                        sd.pos += written;
                        sd.length -= written;
                        socketWrapper.updateLastWrite();
                    } else if (sd.fchannel.size() <= sd.pos) {
                        throw new IOException(AbstractEndpoint.sm.getString("endpoint.sendfile.tooMuchData"));
                    }
                }
                if (sd.length <= 0L && sc.getOutboundRemaining() <= 0) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Send file complete for: " + sd.fileName));
                    }
                    socketWrapper.setSendfileData(null);
                    try {
                        sd.fchannel.close();
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    if (!calledByProcessor) {
                        switch (sd.keepAliveState) {
                            case NONE: {
                                if (log.isDebugEnabled()) {
                                    log.debug((Object)"Send file connection is being closed");
                                }
                                NioEndpoint.this.poller.cancelledKey(sk, socketWrapper);
                                break;
                            }
                            case PIPELINED: {
                                if (log.isDebugEnabled()) {
                                    log.debug((Object)"Connection is keep alive, processing pipe-lined data");
                                }
                                if (NioEndpoint.this.processSocket(socketWrapper, SocketEvent.OPEN_READ, true)) break;
                                NioEndpoint.this.poller.cancelledKey(sk, socketWrapper);
                                break;
                            }
                            case OPEN: {
                                if (log.isDebugEnabled()) {
                                    log.debug((Object)"Connection is keep alive, registering back for OP_READ");
                                }
                                this.reg(sk, socketWrapper, 1);
                            }
                        }
                    }
                    return SendfileState.DONE;
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)("OP_WRITE for sendfile: " + sd.fileName));
                }
                if (calledByProcessor) {
                    this.add(socketWrapper, 4);
                } else {
                    this.reg(sk, socketWrapper, 4);
                }
                return SendfileState.PENDING;
            }
            catch (IOException e) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)"Unable to complete sendfile request:", (Throwable)e);
                }
                if (!calledByProcessor && sc != null) {
                    NioEndpoint.this.poller.cancelledKey(sk, socketWrapper);
                }
                return SendfileState.ERROR;
            }
            catch (Throwable t) {
                log.error((Object)AbstractEndpoint.sm.getString("endpoint.sendfile.error"), t);
                if (!calledByProcessor && sc != null) {
                    NioEndpoint.this.poller.cancelledKey(sk, socketWrapper);
                }
                return SendfileState.ERROR;
            }
        }

        protected void unreg(SelectionKey sk, NioSocketWrapper socketWrapper, int readyOps) {
            this.reg(sk, socketWrapper, sk.interestOps() & ~readyOps);
        }

        protected void reg(SelectionKey sk, NioSocketWrapper socketWrapper, int intops) {
            sk.interestOps(intops);
            socketWrapper.interestOps(intops);
        }

        protected void timeout(int keyCount, boolean hasEvents) {
            long now = System.currentTimeMillis();
            if (this.nextExpiration > 0L && (keyCount > 0 || hasEvents) && now < this.nextExpiration && !this.close) {
                return;
            }
            int keycount = 0;
            try {
                for (SelectionKey key : this.selector.keys()) {
                    ++keycount;
                    NioSocketWrapper socketWrapper = (NioSocketWrapper)key.attachment();
                    try {
                        long timeout;
                        long delta;
                        if (socketWrapper == null) {
                            this.cancelledKey(key, null);
                            continue;
                        }
                        if (this.close) {
                            key.interestOps(0);
                            socketWrapper.interestOps(0);
                            this.cancelledKey(key, socketWrapper);
                            continue;
                        }
                        if (!socketWrapper.interestOpsHas(1) && !socketWrapper.interestOpsHas(4)) continue;
                        boolean readTimeout = false;
                        boolean writeTimeout = false;
                        if (socketWrapper.interestOpsHas(1)) {
                            delta = now - socketWrapper.getLastRead();
                            timeout = socketWrapper.getReadTimeout();
                            if (timeout > 0L && delta > timeout) {
                                readTimeout = true;
                            }
                        }
                        if (!readTimeout && socketWrapper.interestOpsHas(4)) {
                            delta = now - socketWrapper.getLastWrite();
                            timeout = socketWrapper.getWriteTimeout();
                            if (timeout > 0L && delta > timeout) {
                                writeTimeout = true;
                            }
                        }
                        if (!readTimeout && !writeTimeout) continue;
                        key.interestOps(0);
                        socketWrapper.interestOps(0);
                        socketWrapper.setError(new SocketTimeoutException());
                        if (readTimeout && socketWrapper.readOperation != null) {
                            if (socketWrapper.readOperation.process()) continue;
                            this.cancelledKey(key, socketWrapper);
                            continue;
                        }
                        if (writeTimeout && socketWrapper.writeOperation != null) {
                            if (socketWrapper.writeOperation.process()) continue;
                            this.cancelledKey(key, socketWrapper);
                            continue;
                        }
                        if (NioEndpoint.this.processSocket(socketWrapper, SocketEvent.ERROR, true)) continue;
                        this.cancelledKey(key, socketWrapper);
                    }
                    catch (CancelledKeyException ckx) {
                        this.cancelledKey(key, socketWrapper);
                    }
                }
            }
            catch (ConcurrentModificationException cme) {
                log.warn((Object)AbstractEndpoint.sm.getString("endpoint.nio.timeoutCme"), (Throwable)cme);
            }
            long prevExp = this.nextExpiration;
            this.nextExpiration = System.currentTimeMillis() + NioEndpoint.this.socketProperties.getTimeoutInterval();
            if (log.isTraceEnabled()) {
                log.trace((Object)("timeout completed: keys processed=" + keycount + "; now=" + now + "; nextExpiration=" + prevExp + "; keyCount=" + keyCount + "; hasEvents=" + hasEvents + "; eval=" + (now < prevExp && (keyCount > 0 || hasEvents) && !this.close)));
            }
        }
    }

    public static class NioSocketWrapper
    extends SocketWrapperBase<NioChannel> {
        private final SynchronizedStack<NioChannel> nioChannels;
        private final Poller poller;
        private int interestOps = 0;
        private volatile SendfileData sendfileData = null;
        private volatile long lastRead;
        private volatile long lastWrite = this.lastRead = System.currentTimeMillis();
        private final Object readLock;
        private volatile boolean readBlocking = false;
        private final Object writeLock;
        private volatile boolean writeBlocking = false;

        public NioSocketWrapper(NioChannel channel, NioEndpoint endpoint) {
            super(channel, endpoint);
            if (endpoint.getUnixDomainSocketPath() != null) {
                this.localAddr = "127.0.0.1";
                this.localName = "localhost";
                this.localPort = 0;
                this.remoteAddr = "127.0.0.1";
                this.remoteHost = "localhost";
                this.remotePort = 0;
            }
            this.nioChannels = endpoint.getNioChannels();
            this.poller = endpoint.getPoller();
            this.socketBufferHandler = channel.getBufHandler();
            this.readLock = this.readPending == null ? new Object() : this.readPending;
            this.writeLock = this.writePending == null ? new Object() : this.writePending;
        }

        public Poller getPoller() {
            return this.poller;
        }

        public int interestOps() {
            return this.interestOps;
        }

        public int interestOps(int ops) {
            this.interestOps = ops;
            return ops;
        }

        public boolean interestOpsHas(int targetOp) {
            return (this.interestOps() & targetOp) == targetOp;
        }

        public void setSendfileData(SendfileData sf) {
            this.sendfileData = sf;
        }

        public SendfileData getSendfileData() {
            return this.sendfileData;
        }

        public void updateLastWrite() {
            this.lastWrite = System.currentTimeMillis();
        }

        public long getLastWrite() {
            return this.lastWrite;
        }

        public void updateLastRead() {
            this.lastRead = System.currentTimeMillis();
        }

        public long getLastRead() {
            return this.lastRead;
        }

        @Override
        public boolean isReadyForRead() throws IOException {
            this.socketBufferHandler.configureReadBufferForRead();
            if (this.socketBufferHandler.getReadBuffer().remaining() > 0) {
                return true;
            }
            this.fillReadBuffer(false);
            boolean isReady = this.socketBufferHandler.getReadBuffer().position() > 0;
            return isReady;
        }

        @Override
        public int read(boolean block, byte[] b, int off, int len) throws IOException {
            int nRead = this.populateReadBuffer(b, off, len);
            if (nRead > 0) {
                return nRead;
            }
            nRead = this.fillReadBuffer(block);
            this.updateLastRead();
            if (nRead > 0) {
                this.socketBufferHandler.configureReadBufferForRead();
                nRead = Math.min(nRead, len);
                this.socketBufferHandler.getReadBuffer().get(b, off, nRead);
            }
            return nRead;
        }

        @Override
        public int read(boolean block, ByteBuffer to) throws IOException {
            int nRead = this.populateReadBuffer(to);
            if (nRead > 0) {
                return nRead;
            }
            int limit = this.socketBufferHandler.getReadBuffer().capacity();
            if (to.remaining() >= limit) {
                to.limit(to.position() + limit);
                nRead = this.fillReadBuffer(block, to);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Socket: [" + this + "], Read direct from socket: [" + nRead + "]"));
                }
                this.updateLastRead();
            } else {
                nRead = this.fillReadBuffer(block);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Socket: [" + this + "], Read into buffer: [" + nRead + "]"));
                }
                this.updateLastRead();
                if (nRead > 0) {
                    nRead = this.populateReadBuffer(to);
                }
            }
            return nRead;
        }

        @Override
        protected void doClose() {
            block13: {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Calling [" + this.getEndpoint() + "].closeSocket([" + this + "])"));
                }
                try {
                    this.getEndpoint().connections.remove(((NioChannel)this.getSocket()).getIOChannel());
                    if (((NioChannel)this.getSocket()).isOpen()) {
                        ((NioChannel)this.getSocket()).close(true);
                    }
                    if (this.getEndpoint().running && (this.nioChannels == null || !this.nioChannels.push((Object)((NioChannel)this.getSocket())))) {
                        ((NioChannel)this.getSocket()).free();
                    }
                }
                catch (Throwable e) {
                    ExceptionUtils.handleThrowable((Throwable)e);
                    if (log.isDebugEnabled()) {
                        log.error((Object)sm.getString("endpoint.debug.channelCloseFail"), e);
                    }
                }
                finally {
                    this.socketBufferHandler = SocketBufferHandler.EMPTY;
                    this.nonBlockingWriteBuffer.clear();
                    this.reset(NioChannel.CLOSED_NIO_CHANNEL);
                }
                try {
                    SendfileData data = this.getSendfileData();
                    if (data != null && data.fchannel != null && data.fchannel.isOpen()) {
                        data.fchannel.close();
                    }
                }
                catch (Throwable e) {
                    ExceptionUtils.handleThrowable((Throwable)e);
                    if (!log.isDebugEnabled()) break block13;
                    log.error((Object)sm.getString("endpoint.sendfile.closeError"), e);
                }
            }
        }

        private int fillReadBuffer(boolean block) throws IOException {
            this.socketBufferHandler.configureReadBufferForWrite();
            return this.fillReadBuffer(block, this.socketBufferHandler.getReadBuffer());
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private int fillReadBuffer(boolean block, ByteBuffer buffer) throws IOException {
            int n = 0;
            if (this.getSocket() == NioChannel.CLOSED_NIO_CHANNEL) {
                throw new ClosedChannelException();
            }
            if (block) {
                long timeout = this.getReadTimeout();
                long startNanos = 0L;
                do {
                    if (startNanos > 0L) {
                        long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
                        if (elapsedMillis == 0L) {
                            elapsedMillis = 1L;
                        }
                        if ((timeout -= elapsedMillis) <= 0L) {
                            throw new SocketTimeoutException();
                        }
                    }
                    Object object = this.readLock;
                    synchronized (object) {
                        n = ((NioChannel)this.getSocket()).read(buffer);
                        if (n == -1) {
                            throw new EOFException();
                        }
                        if (n == 0) {
                            if (!this.readBlocking) {
                                this.readBlocking = true;
                                this.registerReadInterest();
                            }
                            try {
                                if (timeout > 0L) {
                                    startNanos = System.nanoTime();
                                    this.readLock.wait(timeout);
                                } else {
                                    this.readLock.wait();
                                }
                            }
                            catch (InterruptedException interruptedException) {
                                // empty catch block
                            }
                        }
                    }
                } while (n == 0);
            } else {
                n = ((NioChannel)this.getSocket()).read(buffer);
                if (n == -1) {
                    throw new EOFException();
                }
            }
            return n;
        }

        @Override
        protected boolean flushNonBlocking() throws IOException {
            boolean dataLeft = this.socketOrNetworkBufferHasDataLeft();
            if (dataLeft) {
                this.doWrite(false);
                dataLeft = this.socketOrNetworkBufferHasDataLeft();
            }
            if (!dataLeft && !this.nonBlockingWriteBuffer.isEmpty() && !(dataLeft = this.nonBlockingWriteBuffer.write(this, false)) && this.socketOrNetworkBufferHasDataLeft()) {
                this.doWrite(false);
                dataLeft = this.socketOrNetworkBufferHasDataLeft();
            }
            return dataLeft;
        }

        private boolean socketOrNetworkBufferHasDataLeft() {
            return !this.socketBufferHandler.isWriteBufferEmpty() || ((NioChannel)this.getSocket()).getOutboundRemaining() > 0;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected void doWrite(boolean block, ByteBuffer buffer) throws IOException {
            int n = 0;
            if (this.getSocket() == NioChannel.CLOSED_NIO_CHANNEL) {
                throw new ClosedChannelException();
            }
            if (block) {
                if (this.previousIOException != null) {
                    throw new IOException(this.previousIOException);
                }
                long timeout = this.getWriteTimeout();
                long startNanos = 0L;
                do {
                    if (startNanos > 0L) {
                        long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
                        if (elapsedMillis == 0L) {
                            elapsedMillis = 1L;
                        }
                        if ((timeout -= elapsedMillis) <= 0L) {
                            this.previousIOException = new SocketTimeoutException();
                            throw this.previousIOException;
                        }
                    }
                    Object object = this.writeLock;
                    synchronized (object) {
                        block19: {
                            n = ((NioChannel)this.getSocket()).write(buffer);
                            if (n == 0 && (buffer.hasRemaining() || ((NioChannel)this.getSocket()).getOutboundRemaining() > 0)) {
                                if (!this.writeBlocking) {
                                    this.writeBlocking = true;
                                    this.registerWriteInterest();
                                }
                                try {
                                    if (timeout > 0L) {
                                        startNanos = System.nanoTime();
                                        this.writeLock.wait(timeout);
                                        break block19;
                                    }
                                    this.writeLock.wait();
                                }
                                catch (InterruptedException interruptedException) {}
                            } else if (startNanos > 0L) {
                                timeout = this.getWriteTimeout();
                                startNanos = 0L;
                            }
                        }
                    }
                } while (buffer.hasRemaining() || ((NioChannel)this.getSocket()).getOutboundRemaining() > 0);
            } else {
                while ((n = ((NioChannel)this.getSocket()).write(buffer)) > 0 && buffer.hasRemaining()) {
                }
            }
            this.updateLastWrite();
        }

        @Override
        public void registerReadInterest() {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("endpoint.debug.registerRead", new Object[]{this}));
            }
            this.getPoller().add(this, 1);
        }

        @Override
        public void registerWriteInterest() {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("endpoint.debug.registerWrite", new Object[]{this}));
            }
            this.getPoller().add(this, 4);
        }

        @Override
        public SendfileDataBase createSendfileData(String filename, long pos, long length) {
            return new SendfileData(filename, pos, length);
        }

        @Override
        public SendfileState processSendfile(SendfileDataBase sendfileData) {
            this.setSendfileData((SendfileData)sendfileData);
            SelectionKey key = ((NioChannel)this.getSocket()).getIOChannel().keyFor(this.getPoller().getSelector());
            if (key == null) {
                return SendfileState.ERROR;
            }
            return this.getPoller().processSendfile(key, this, true);
        }

        @Override
        protected void populateRemoteAddr() {
            InetAddress inetAddr;
            SocketChannel sc = ((NioChannel)this.getSocket()).getIOChannel();
            if (sc != null && (inetAddr = sc.socket().getInetAddress()) != null) {
                this.remoteAddr = inetAddr.getHostAddress();
            }
        }

        @Override
        protected void populateRemoteHost() {
            InetAddress inetAddr;
            SocketChannel sc = ((NioChannel)this.getSocket()).getIOChannel();
            if (sc != null && (inetAddr = sc.socket().getInetAddress()) != null) {
                this.remoteHost = inetAddr.getHostName();
                if (this.remoteAddr == null) {
                    this.remoteAddr = inetAddr.getHostAddress();
                }
            }
        }

        @Override
        protected void populateRemotePort() {
            SocketChannel sc = ((NioChannel)this.getSocket()).getIOChannel();
            if (sc != null) {
                this.remotePort = sc.socket().getPort();
            }
        }

        @Override
        protected void populateLocalName() {
            InetAddress inetAddr;
            SocketChannel sc = ((NioChannel)this.getSocket()).getIOChannel();
            if (sc != null && (inetAddr = sc.socket().getLocalAddress()) != null) {
                this.localName = inetAddr.getHostName();
            }
        }

        @Override
        protected void populateLocalAddr() {
            InetAddress inetAddr;
            SocketChannel sc = ((NioChannel)this.getSocket()).getIOChannel();
            if (sc != null && (inetAddr = sc.socket().getLocalAddress()) != null) {
                this.localAddr = inetAddr.getHostAddress();
            }
        }

        @Override
        protected void populateLocalPort() {
            SocketChannel sc = ((NioChannel)this.getSocket()).getIOChannel();
            if (sc != null) {
                this.localPort = sc.socket().getLocalPort();
            }
        }

        @Override
        public SSLSupport getSslSupport() {
            if (this.getSocket() instanceof SecureNioChannel) {
                SecureNioChannel ch = (SecureNioChannel)this.getSocket();
                return ch.getSSLSupport();
            }
            return null;
        }

        @Override
        public void doClientAuth(SSLSupport sslSupport) throws IOException {
            SecureNioChannel sslChannel = (SecureNioChannel)this.getSocket();
            SSLEngine engine = sslChannel.getSslEngine();
            if (!engine.getNeedClientAuth()) {
                engine.setNeedClientAuth(true);
                sslChannel.rehandshake(this.getEndpoint().getConnectionTimeout());
                ((JSSESupport)sslSupport).setSession(engine.getSession());
            }
        }

        @Override
        public void setAppReadBufHandler(ApplicationBufferHandler handler) {
            ((NioChannel)this.getSocket()).setAppReadBufHandler(handler);
        }

        @Override
        protected <A> SocketWrapperBase.OperationState<A> newOperationState(boolean read, ByteBuffer[] buffers, int offset, int length, SocketWrapperBase.BlockingMode block, long timeout, TimeUnit unit, A attachment, SocketWrapperBase.CompletionCheck check, CompletionHandler<Long, ? super A> handler, Semaphore semaphore, SocketWrapperBase.VectoredIOCompletionHandler<A> completion) {
            return new NioOperationState(read, buffers, offset, length, block, timeout, unit, attachment, check, handler, semaphore, completion);
        }

        private class NioOperationState<A>
        extends SocketWrapperBase.OperationState<A> {
            private volatile boolean inline;

            private NioOperationState(boolean read, ByteBuffer[] buffers, int offset, int length, SocketWrapperBase.BlockingMode block, long timeout, TimeUnit unit, A attachment, SocketWrapperBase.CompletionCheck check, CompletionHandler<Long, ? super A> handler, Semaphore semaphore, SocketWrapperBase.VectoredIOCompletionHandler<A> completion) {
                super(NioSocketWrapper.this, read, buffers, offset, length, block, timeout, unit, attachment, check, handler, semaphore, completion);
                this.inline = true;
            }

            @Override
            protected boolean isInline() {
                return this.inline;
            }

            @Override
            protected boolean hasOutboundRemaining() {
                return ((NioChannel)NioSocketWrapper.this.getSocket()).getOutboundRemaining() > 0;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                long nBytes = 0L;
                if (NioSocketWrapper.this.getError() == null) {
                    try {
                        NioOperationState nioOperationState = this;
                        synchronized (nioOperationState) {
                            if (!this.completionDone) {
                                if (log.isDebugEnabled()) {
                                    log.debug((Object)("Skip concurrent " + (this.read ? "read" : "write") + " notification"));
                                }
                                return;
                            }
                            if (this.read) {
                                if (!NioSocketWrapper.this.socketBufferHandler.isReadBufferEmpty()) {
                                    NioSocketWrapper.this.socketBufferHandler.configureReadBufferForRead();
                                    for (int i = 0; i < this.length && !NioSocketWrapper.this.socketBufferHandler.isReadBufferEmpty(); ++i) {
                                        nBytes += (long)SocketWrapperBase.transfer(NioSocketWrapper.this.socketBufferHandler.getReadBuffer(), this.buffers[this.offset + i]);
                                    }
                                }
                                if (nBytes == 0L) {
                                    nBytes = ((NioChannel)NioSocketWrapper.this.getSocket()).read(this.buffers, this.offset, this.length);
                                    NioSocketWrapper.this.updateLastRead();
                                }
                            } else {
                                boolean doWrite = true;
                                if (NioSocketWrapper.this.socketOrNetworkBufferHasDataLeft()) {
                                    NioSocketWrapper.this.socketBufferHandler.configureWriteBufferForRead();
                                    do {
                                        nBytes = ((NioChannel)NioSocketWrapper.this.getSocket()).write(NioSocketWrapper.this.socketBufferHandler.getWriteBuffer());
                                    } while (NioSocketWrapper.this.socketOrNetworkBufferHasDataLeft() && nBytes > 0L);
                                    if (NioSocketWrapper.this.socketOrNetworkBufferHasDataLeft()) {
                                        doWrite = false;
                                    }
                                    if (nBytes > 0L) {
                                        nBytes = 0L;
                                    }
                                }
                                if (doWrite) {
                                    long n = 0L;
                                    do {
                                        if ((n = ((NioChannel)NioSocketWrapper.this.getSocket()).write(this.buffers, this.offset, this.length)) == -1L) {
                                            nBytes = n;
                                            continue;
                                        }
                                        nBytes += n;
                                    } while (n > 0L);
                                    NioSocketWrapper.this.updateLastWrite();
                                }
                            }
                            if (nBytes != 0L || !SocketWrapperBase.buffersArrayHasRemaining(this.buffers, this.offset, this.length) && (this.read || !NioSocketWrapper.this.socketOrNetworkBufferHasDataLeft())) {
                                this.completionDone = false;
                            }
                        }
                    }
                    catch (IOException e) {
                        NioSocketWrapper.this.setError(e);
                    }
                }
                if (nBytes > 0L || nBytes == 0L && !SocketWrapperBase.buffersArrayHasRemaining(this.buffers, this.offset, this.length) && (this.read || !NioSocketWrapper.this.socketOrNetworkBufferHasDataLeft())) {
                    this.completion.completed(nBytes, this);
                } else if (nBytes < 0L || NioSocketWrapper.this.getError() != null) {
                    IOException error = NioSocketWrapper.this.getError();
                    if (error == null) {
                        error = new EOFException();
                    }
                    this.completion.failed((Throwable)error, this);
                } else {
                    this.inline = false;
                    if (this.read) {
                        NioSocketWrapper.this.registerReadInterest();
                    } else {
                        NioSocketWrapper.this.registerWriteInterest();
                    }
                }
            }
        }
    }

    protected class SocketProcessor
    extends SocketProcessorBase<NioChannel> {
        public SocketProcessor(SocketWrapperBase<NioChannel> socketWrapper, SocketEvent event) {
            super(socketWrapper, event);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected void doRun() {
            Poller poller = NioEndpoint.this.poller;
            if (poller == null) {
                this.socketWrapper.close();
                return;
            }
            try {
                int handshake = -1;
                try {
                    if (((NioChannel)this.socketWrapper.getSocket()).isHandshakeComplete()) {
                        handshake = 0;
                    } else if (this.event == SocketEvent.STOP || this.event == SocketEvent.DISCONNECT || this.event == SocketEvent.ERROR) {
                        handshake = -1;
                    } else {
                        handshake = ((NioChannel)this.socketWrapper.getSocket()).handshake(this.event == SocketEvent.OPEN_READ, this.event == SocketEvent.OPEN_WRITE);
                        this.event = SocketEvent.OPEN_READ;
                    }
                }
                catch (IOException x) {
                    handshake = -1;
                    if (logHandshake.isDebugEnabled()) {
                        logHandshake.debug((Object)AbstractEndpoint.sm.getString("endpoint.err.handshake", new Object[]{this.socketWrapper.getRemoteAddr(), Integer.toString(this.socketWrapper.getRemotePort())}), (Throwable)x);
                    }
                }
                catch (CancelledKeyException ckx) {
                    handshake = -1;
                }
                if (handshake == 0) {
                    AbstractEndpoint.Handler.SocketState state = AbstractEndpoint.Handler.SocketState.OPEN;
                    state = this.event == null ? NioEndpoint.this.getHandler().process(this.socketWrapper, SocketEvent.OPEN_READ) : NioEndpoint.this.getHandler().process(this.socketWrapper, this.event);
                    if (state == AbstractEndpoint.Handler.SocketState.CLOSED) {
                        poller.cancelledKey(this.getSelectionKey(), this.socketWrapper);
                    }
                } else if (handshake == -1) {
                    NioEndpoint.this.getHandler().process(this.socketWrapper, SocketEvent.CONNECT_FAIL);
                    poller.cancelledKey(this.getSelectionKey(), this.socketWrapper);
                } else if (handshake == 1) {
                    this.socketWrapper.registerReadInterest();
                } else if (handshake == 4) {
                    this.socketWrapper.registerWriteInterest();
                }
            }
            catch (CancelledKeyException cx) {
                poller.cancelledKey(this.getSelectionKey(), this.socketWrapper);
            }
            catch (VirtualMachineError vme) {
                ExceptionUtils.handleThrowable((Throwable)vme);
            }
            catch (Throwable t) {
                log.error((Object)AbstractEndpoint.sm.getString("endpoint.processing.fail"), t);
                poller.cancelledKey(this.getSelectionKey(), this.socketWrapper);
            }
            finally {
                this.socketWrapper = null;
                this.event = null;
                if (NioEndpoint.this.running && NioEndpoint.this.processorCache != null) {
                    NioEndpoint.this.processorCache.push((Object)this);
                }
            }
        }

        private SelectionKey getSelectionKey() {
            if (JreCompat.isJre11Available()) {
                return null;
            }
            SocketChannel socketChannel = ((NioChannel)this.socketWrapper.getSocket()).getIOChannel();
            if (socketChannel == null) {
                return null;
            }
            return socketChannel.keyFor(NioEndpoint.this.poller.getSelector());
        }
    }

    public static class SendfileData
    extends SendfileDataBase {
        protected volatile FileChannel fchannel;

        public SendfileData(String filename, long pos, long length) {
            super(filename, pos, length);
        }
    }

    public static class PollerEvent {
        private NioSocketWrapper socketWrapper;
        private int interestOps;

        public PollerEvent(NioSocketWrapper socketWrapper, int intOps) {
            this.reset(socketWrapper, intOps);
        }

        public void reset(NioSocketWrapper socketWrapper, int intOps) {
            this.socketWrapper = socketWrapper;
            this.interestOps = intOps;
        }

        public NioSocketWrapper getSocketWrapper() {
            return this.socketWrapper;
        }

        public int getInterestOps() {
            return this.interestOps;
        }

        public void reset() {
            this.reset(null, 0);
        }

        public String toString() {
            return "Poller event: socket [" + this.socketWrapper.getSocket() + "], socketWrapper [" + this.socketWrapper + "], interestOps [" + this.interestOps + "]";
        }
    }
}

