/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.collections.SynchronizedStack
 *  org.apache.tomcat.util.compat.JrePlatform
 */
package org.apache.tomcat.util.net;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.channels.NetworkChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.net.ssl.SSLEngine;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.apache.tomcat.util.compat.JrePlatform;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.AbstractJsseEndpoint;
import org.apache.tomcat.util.net.Acceptor;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.net.Nio2Channel;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SecureNio2Channel;
import org.apache.tomcat.util.net.SendfileDataBase;
import org.apache.tomcat.util.net.SendfileState;
import org.apache.tomcat.util.net.SocketBufferHandler;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.SocketProcessorBase;
import org.apache.tomcat.util.net.SocketWrapperBase;
import org.apache.tomcat.util.net.jsse.JSSESupport;

public class Nio2Endpoint
extends AbstractJsseEndpoint<Nio2Channel, AsynchronousSocketChannel> {
    private static final Log log = LogFactory.getLog(Nio2Endpoint.class);
    private static final Log logCertificate = LogFactory.getLog((String)(Nio2Endpoint.class.getName() + ".certificate"));
    private static final Log logHandshake = LogFactory.getLog((String)(Nio2Endpoint.class.getName() + ".handshake"));
    private volatile AsynchronousServerSocketChannel serverSock = null;
    private static ThreadLocal<Boolean> inlineCompletion = new ThreadLocal();
    private AsynchronousChannelGroup threadGroup = null;
    private volatile boolean allClosed;
    private SynchronizedStack<Nio2Channel> nioChannels;
    private SocketAddress previousAcceptedSocketRemoteAddress = null;
    private long previousAcceptedSocketNanoTime = 0L;

    @Override
    public boolean getDeferAccept() {
        return false;
    }

    public int getKeepAliveCount() {
        return -1;
    }

    @Override
    public void bind() throws Exception {
        if (this.getExecutor() == null) {
            this.createExecutor();
        }
        if (this.getExecutor() instanceof ExecutorService) {
            this.threadGroup = AsynchronousChannelGroup.withThreadPool((ExecutorService)this.getExecutor());
        }
        if (!this.internalExecutor) {
            log.warn((Object)sm.getString("endpoint.nio2.exclusiveExecutor"));
        }
        this.serverSock = AsynchronousServerSocketChannel.open(this.threadGroup);
        this.socketProperties.setProperties(this.serverSock);
        InetSocketAddress addr = new InetSocketAddress(this.getAddress(), this.getPortWithOffset());
        this.serverSock.bind(addr, this.getAcceptCount());
        this.initialiseSsl();
    }

    @Override
    public void startInternal() throws Exception {
        if (!this.running) {
            this.allClosed = false;
            this.running = true;
            this.paused = false;
            if (this.socketProperties.getProcessorCache() != 0) {
                this.processorCache = new SynchronizedStack(128, this.socketProperties.getProcessorCache());
            }
            if (this.socketProperties.getBufferPool() != 0) {
                this.nioChannels = new SynchronizedStack(128, this.socketProperties.getBufferPool());
            }
            if (this.getExecutor() == null) {
                this.createExecutor();
            }
            this.initializeConnectionLatch();
            this.startAcceptorThread();
        }
    }

    @Override
    protected void startAcceptorThread() {
        if (this.acceptor == null) {
            this.acceptor = new Nio2Acceptor(this);
            this.acceptor.setThreadName(this.getName() + "-Acceptor");
        }
        this.acceptor.state = Acceptor.AcceptorState.RUNNING;
        this.getExecutor().execute(this.acceptor);
    }

    @Override
    public void resume() {
        super.resume();
        if (this.isRunning()) {
            this.acceptor.state = Acceptor.AcceptorState.RUNNING;
            this.getExecutor().execute(this.acceptor);
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
            this.getExecutor().execute(() -> {
                try {
                    for (SocketWrapperBase wrapper : this.getConnections()) {
                        wrapper.close();
                    }
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                }
                finally {
                    this.allClosed = true;
                }
            });
            if (this.nioChannels != null) {
                Nio2Channel socket;
                while ((socket = (Nio2Channel)this.nioChannels.pop()) != null) {
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
        if (this.running) {
            this.stop();
        }
        this.doCloseServerSocket();
        this.destroySsl();
        super.unbind();
        this.shutdownExecutor();
        if (this.getHandler() != null) {
            this.getHandler().recycle();
        }
    }

    @Override
    protected void doCloseServerSocket() throws IOException {
        if (this.serverSock != null) {
            this.serverSock.close();
            this.serverSock = null;
        }
    }

    @Override
    public void shutdownExecutor() {
        if (this.threadGroup != null && this.internalExecutor) {
            try {
                long timeout;
                for (timeout = this.getExecutorTerminationTimeoutMillis(); timeout > 0L && !this.allClosed; --timeout) {
                    Thread.sleep(1L);
                }
                this.threadGroup.shutdownNow();
                if (timeout > 0L) {
                    this.threadGroup.awaitTermination(timeout, TimeUnit.MILLISECONDS);
                }
            }
            catch (IOException e) {
                this.getLog().warn((Object)sm.getString("endpoint.warn.executorShutdown", new Object[]{this.getName()}), (Throwable)e);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            if (!this.threadGroup.isTerminated()) {
                this.getLog().warn((Object)sm.getString("endpoint.warn.executorShutdown", new Object[]{this.getName()}));
            }
            this.threadGroup = null;
        }
        super.shutdownExecutor();
    }

    @Override
    protected boolean setSocketOptions(AsynchronousSocketChannel socket) {
        Nio2SocketWrapper socketWrapper = null;
        try {
            Nio2Channel channel = null;
            if (this.nioChannels != null) {
                channel = (Nio2Channel)this.nioChannels.pop();
            }
            if (channel == null) {
                SocketBufferHandler bufhandler = new SocketBufferHandler(this.socketProperties.getAppReadBufSize(), this.socketProperties.getAppWriteBufSize(), this.socketProperties.getDirectBuffer());
                channel = this.isSSLEnabled() ? new SecureNio2Channel(bufhandler, this) : new Nio2Channel(bufhandler);
            }
            Nio2SocketWrapper newWrapper = new Nio2SocketWrapper(channel, this);
            channel.reset(socket, newWrapper);
            this.connections.put(socket, newWrapper);
            socketWrapper = newWrapper;
            this.socketProperties.setProperties(socket);
            socketWrapper.setReadTimeout(this.getConnectionTimeout());
            socketWrapper.setWriteTimeout(this.getConnectionTimeout());
            socketWrapper.setKeepAliveLeft(this.getMaxKeepAliveRequests());
            return this.processSocket(socketWrapper, SocketEvent.OPEN_READ, false);
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            log.error((Object)sm.getString("endpoint.socketOptionsError"), t);
            if (socketWrapper == null) {
                this.destroySocket(socket);
            }
            return false;
        }
    }

    @Override
    protected void destroySocket(AsynchronousSocketChannel socket) {
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

    protected SynchronizedStack<Nio2Channel> getNioChannels() {
        return this.nioChannels;
    }

    @Override
    protected NetworkChannel getServerSocket() {
        return this.serverSock;
    }

    @Override
    protected AsynchronousSocketChannel serverSocketAccept() throws Exception {
        AsynchronousSocketChannel result = this.serverSock.accept().get();
        if (!JrePlatform.IS_WINDOWS) {
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
    protected SocketProcessorBase<Nio2Channel> createSocketProcessor(SocketWrapperBase<Nio2Channel> socketWrapper, SocketEvent event) {
        return new SocketProcessor(socketWrapper, event);
    }

    public static void startInline() {
        inlineCompletion.set(Boolean.TRUE);
    }

    public static void endInline() {
        inlineCompletion.set(Boolean.FALSE);
    }

    public static boolean isInline() {
        Boolean flag = inlineCompletion.get();
        if (flag == null) {
            return false;
        }
        return flag;
    }

    protected class Nio2Acceptor
    extends Acceptor<AsynchronousSocketChannel>
    implements CompletionHandler<AsynchronousSocketChannel, Void> {
        protected int errorDelay;

        public Nio2Acceptor(AbstractEndpoint<?, AsynchronousSocketChannel> endpoint) {
            super(endpoint);
            this.errorDelay = 0;
        }

        @Override
        public void run() {
            if (!Nio2Endpoint.this.isPaused()) {
                try {
                    Nio2Endpoint.this.countUpOrAwaitConnection();
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                if (!Nio2Endpoint.this.isPaused()) {
                    Nio2Endpoint.this.serverSock.accept(null, this);
                } else {
                    this.state = Acceptor.AcceptorState.PAUSED;
                }
            } else {
                this.state = Acceptor.AcceptorState.PAUSED;
            }
        }

        @Override
        public void stop(int waitSeconds) {
            Nio2Endpoint.this.acceptor.state = Acceptor.AcceptorState.ENDED;
        }

        @Override
        public void completed(AsynchronousSocketChannel socket, Void attachment) {
            this.errorDelay = 0;
            if (Nio2Endpoint.this.isRunning() && !Nio2Endpoint.this.isPaused()) {
                if (Nio2Endpoint.this.getMaxConnections() == -1) {
                    Nio2Endpoint.this.serverSock.accept(null, this);
                } else if (Nio2Endpoint.this.getConnectionCount() < (long)Nio2Endpoint.this.getMaxConnections()) {
                    try {
                        Nio2Endpoint.this.countUpOrAwaitConnection();
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                    Nio2Endpoint.this.serverSock.accept(null, this);
                } else {
                    Nio2Endpoint.this.getExecutor().execute(this);
                }
                if (!Nio2Endpoint.this.setSocketOptions(socket)) {
                    Nio2Endpoint.this.closeSocket(socket);
                }
            } else {
                if (Nio2Endpoint.this.isRunning()) {
                    this.state = Acceptor.AcceptorState.PAUSED;
                }
                Nio2Endpoint.this.destroySocket(socket);
            }
        }

        @Override
        public void failed(Throwable t, Void attachment) {
            if (Nio2Endpoint.this.isRunning()) {
                if (!Nio2Endpoint.this.isPaused()) {
                    if (Nio2Endpoint.this.getMaxConnections() == -1) {
                        Nio2Endpoint.this.serverSock.accept(null, this);
                    } else {
                        Nio2Endpoint.this.getExecutor().execute(this);
                    }
                } else {
                    this.state = Acceptor.AcceptorState.PAUSED;
                }
                Nio2Endpoint.this.countDownConnection();
                this.errorDelay = this.handleExceptionWithDelay(this.errorDelay);
                ExceptionUtils.handleThrowable((Throwable)t);
                log.error((Object)AbstractEndpoint.sm.getString("endpoint.accept.fail"), t);
            } else {
                Nio2Endpoint.this.countDownConnection();
            }
        }
    }

    public static class Nio2SocketWrapper
    extends SocketWrapperBase<Nio2Channel> {
        private final SynchronizedStack<Nio2Channel> nioChannels;
        private SendfileData sendfileData = null;
        private final CompletionHandler<Integer, ByteBuffer> readCompletionHandler;
        private boolean readInterest = false;
        private boolean readNotify = false;
        private final CompletionHandler<Integer, ByteBuffer> writeCompletionHandler;
        private final CompletionHandler<Long, ByteBuffer[]> gatheringWriteCompletionHandler;
        private boolean writeInterest = false;
        private boolean writeNotify = false;
        private CompletionHandler<Integer, SendfileData> sendfileHandler = new CompletionHandler<Integer, SendfileData>(){

            @Override
            public void completed(Integer nWrite, SendfileData attachment) {
                if (nWrite < 0) {
                    this.failed((Throwable)new EOFException(), attachment);
                    return;
                }
                attachment.pos += (long)nWrite.intValue();
                ByteBuffer buffer = ((Nio2Channel)this.getSocket()).getBufHandler().getWriteBuffer();
                if (!buffer.hasRemaining()) {
                    if (attachment.length <= 0L) {
                        this.setSendfileData(null);
                        try {
                            attachment.fchannel.close();
                        }
                        catch (IOException iOException) {
                            // empty catch block
                        }
                        if (Nio2Endpoint.isInline()) {
                            attachment.doneInline = true;
                        } else {
                            switch (attachment.keepAliveState) {
                                case NONE: {
                                    this.getEndpoint().processSocket(this, SocketEvent.DISCONNECT, false);
                                    break;
                                }
                                case PIPELINED: {
                                    if (this.getEndpoint().processSocket(this, SocketEvent.OPEN_READ, true)) break;
                                    this.close();
                                    break;
                                }
                                case OPEN: {
                                    this.registerReadInterest();
                                }
                            }
                        }
                        return;
                    }
                    ((Nio2Channel)this.getSocket()).getBufHandler().configureWriteBufferForWrite();
                    int nRead = -1;
                    try {
                        nRead = attachment.fchannel.read(buffer);
                    }
                    catch (IOException e) {
                        this.failed((Throwable)e, attachment);
                        return;
                    }
                    if (nRead > 0) {
                        ((Nio2Channel)this.getSocket()).getBufHandler().configureWriteBufferForRead();
                        if (attachment.length < (long)buffer.remaining()) {
                            buffer.limit(buffer.limit() - buffer.remaining() + (int)attachment.length);
                        }
                        attachment.length -= (long)nRead;
                    } else {
                        this.failed((Throwable)new EOFException(), attachment);
                        return;
                    }
                }
                ((Nio2Channel)this.getSocket()).write(buffer, AbstractEndpoint.toTimeout(this.getWriteTimeout()), TimeUnit.MILLISECONDS, attachment, this);
            }

            @Override
            public void failed(Throwable exc, SendfileData attachment) {
                try {
                    attachment.fchannel.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                if (!Nio2Endpoint.isInline()) {
                    this.getEndpoint().processSocket(this, SocketEvent.ERROR, false);
                } else {
                    attachment.doneInline = true;
                    attachment.error = true;
                }
            }
        };

        public Nio2SocketWrapper(Nio2Channel channel, final Nio2Endpoint endpoint) {
            super(channel, endpoint);
            this.nioChannels = endpoint.getNioChannels();
            this.socketBufferHandler = channel.getBufHandler();
            this.readCompletionHandler = new CompletionHandler<Integer, ByteBuffer>(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void completed(Integer nBytes, ByteBuffer attachment) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Socket: [" + this + "], Interest: [" + readInterest + "]"));
                    }
                    boolean notify = false;
                    CompletionHandler completionHandler = readCompletionHandler;
                    synchronized (completionHandler) {
                        readNotify = false;
                        if (nBytes < 0) {
                            this.failed((Throwable)new EOFException(), attachment);
                        } else {
                            if (readInterest && !Nio2Endpoint.isInline()) {
                                readNotify = true;
                            } else {
                                readPending.release();
                            }
                            readInterest = false;
                        }
                        notify = readNotify;
                    }
                    if (notify) {
                        this.getEndpoint().processSocket(this, SocketEvent.OPEN_READ, false);
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    IOException ioe = exc instanceof IOException ? (IOException)exc : new IOException(exc);
                    this.setError(ioe);
                    if (exc instanceof AsynchronousCloseException) {
                        readPending.release();
                        this.getEndpoint().processSocket(this, SocketEvent.STOP, false);
                    } else if (!this.getEndpoint().processSocket(this, SocketEvent.ERROR, true)) {
                        this.close();
                    }
                }
            };
            this.writeCompletionHandler = new CompletionHandler<Integer, ByteBuffer>(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void completed(Integer nBytes, ByteBuffer attachment) {
                    boolean notify = false;
                    CompletionHandler completionHandler = writeCompletionHandler;
                    synchronized (completionHandler) {
                        writeNotify = false;
                        if (nBytes < 0) {
                            this.failed((Throwable)new EOFException(SocketWrapperBase.sm.getString("iob.failedwrite")), attachment);
                        } else if (!nonBlockingWriteBuffer.isEmpty()) {
                            ByteBuffer[] array = nonBlockingWriteBuffer.toArray(attachment);
                            ((Nio2Channel)this.getSocket()).write(array, 0, array.length, AbstractEndpoint.toTimeout(this.getWriteTimeout()), TimeUnit.MILLISECONDS, array, gatheringWriteCompletionHandler);
                        } else if (attachment.hasRemaining()) {
                            ((Nio2Channel)this.getSocket()).write(attachment, AbstractEndpoint.toTimeout(this.getWriteTimeout()), TimeUnit.MILLISECONDS, attachment, writeCompletionHandler);
                        } else {
                            if (writeInterest && !Nio2Endpoint.isInline()) {
                                writeNotify = true;
                                notify = true;
                            } else {
                                writePending.release();
                            }
                            writeInterest = false;
                        }
                    }
                    if (notify && !endpoint.processSocket(this, SocketEvent.OPEN_WRITE, true)) {
                        this.close();
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    IOException ioe = exc instanceof IOException ? (IOException)exc : new IOException(exc);
                    this.setError(ioe);
                    writePending.release();
                    if (!endpoint.processSocket(this, SocketEvent.ERROR, true)) {
                        this.close();
                    }
                }
            };
            this.gatheringWriteCompletionHandler = new CompletionHandler<Long, ByteBuffer[]>(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void completed(Long nBytes, ByteBuffer[] attachment) {
                    boolean notify = false;
                    CompletionHandler completionHandler = writeCompletionHandler;
                    synchronized (completionHandler) {
                        writeNotify = false;
                        if (nBytes < 0L) {
                            this.failed((Throwable)new EOFException(SocketWrapperBase.sm.getString("iob.failedwrite")), attachment);
                        } else if (!nonBlockingWriteBuffer.isEmpty() || SocketWrapperBase.buffersArrayHasRemaining(attachment, 0, attachment.length)) {
                            ByteBuffer[] array = nonBlockingWriteBuffer.toArray(attachment);
                            ((Nio2Channel)this.getSocket()).write(array, 0, array.length, AbstractEndpoint.toTimeout(this.getWriteTimeout()), TimeUnit.MILLISECONDS, array, gatheringWriteCompletionHandler);
                        } else {
                            if (writeInterest && !Nio2Endpoint.isInline()) {
                                writeNotify = true;
                                notify = true;
                            } else {
                                writePending.release();
                            }
                            writeInterest = false;
                        }
                    }
                    if (notify && !endpoint.processSocket(this, SocketEvent.OPEN_WRITE, true)) {
                        this.close();
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer[] attachment) {
                    IOException ioe = exc instanceof IOException ? (IOException)exc : new IOException(exc);
                    this.setError(ioe);
                    writePending.release();
                    if (!endpoint.processSocket(this, SocketEvent.ERROR, true)) {
                        this.close();
                    }
                }
            };
        }

        public void setSendfileData(SendfileData sf) {
            this.sendfileData = sf;
        }

        public SendfileData getSendfileData() {
            return this.sendfileData;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isReadyForRead() throws IOException {
            CompletionHandler<Integer, ByteBuffer> completionHandler = this.readCompletionHandler;
            synchronized (completionHandler) {
                boolean isReady;
                if (this.readNotify) {
                    return true;
                }
                if (!this.readPending.tryAcquire()) {
                    this.readInterest = true;
                    return false;
                }
                if (!this.socketBufferHandler.isReadBufferEmpty()) {
                    this.readPending.release();
                    return true;
                }
                boolean bl = isReady = this.fillReadBuffer(false) > 0;
                if (!isReady) {
                    this.readInterest = true;
                }
                return isReady;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isReadyForWrite() {
            CompletionHandler<Integer, ByteBuffer> completionHandler = this.writeCompletionHandler;
            synchronized (completionHandler) {
                boolean isReady;
                if (this.writeNotify) {
                    return true;
                }
                if (!this.writePending.tryAcquire()) {
                    this.writeInterest = true;
                    return false;
                }
                if (this.socketBufferHandler.isWriteBufferEmpty() && this.nonBlockingWriteBuffer.isEmpty()) {
                    this.writePending.release();
                    return true;
                }
                boolean bl = isReady = !this.flushNonBlockingInternal(true);
                if (!isReady) {
                    this.writeInterest = true;
                }
                return isReady;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int read(boolean block, byte[] b, int off, int len) throws IOException {
            int nRead;
            this.checkError();
            if (log.isDebugEnabled()) {
                log.debug((Object)("Socket: [" + this + "], block: [" + block + "], length: [" + len + "]"));
            }
            if (this.socketBufferHandler == null) {
                throw new IOException(sm.getString("socket.closed"));
            }
            boolean notify = false;
            CompletionHandler<Integer, ByteBuffer> completionHandler = this.readCompletionHandler;
            synchronized (completionHandler) {
                notify = this.readNotify;
            }
            if (!notify) {
                if (block) {
                    try {
                        this.readPending.acquire();
                    }
                    catch (InterruptedException e) {
                        throw new IOException(e);
                    }
                } else if (!this.readPending.tryAcquire()) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Socket: [" + this + "], Read in progress. Returning [0]"));
                    }
                    return 0;
                }
            }
            if ((nRead = this.populateReadBuffer(b, off, len)) > 0) {
                CompletionHandler<Integer, ByteBuffer> completionHandler2 = this.readCompletionHandler;
                synchronized (completionHandler2) {
                    this.readNotify = false;
                }
                this.readPending.release();
                return nRead;
            }
            CompletionHandler<Integer, ByteBuffer> completionHandler3 = this.readCompletionHandler;
            synchronized (completionHandler3) {
                nRead = this.fillReadBuffer(block);
                if (nRead > 0) {
                    this.socketBufferHandler.configureReadBufferForRead();
                    nRead = Math.min(nRead, len);
                    this.socketBufferHandler.getReadBuffer().get(b, off, nRead);
                } else if (nRead == 0 && !block) {
                    this.readInterest = true;
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Socket: [" + this + "], Read: [" + nRead + "]"));
                }
                return nRead;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int read(boolean block, ByteBuffer to) throws IOException {
            int nRead;
            this.checkError();
            if (this.socketBufferHandler == null) {
                throw new IOException(sm.getString("socket.closed"));
            }
            boolean notify = false;
            CompletionHandler<Integer, ByteBuffer> completionHandler = this.readCompletionHandler;
            synchronized (completionHandler) {
                notify = this.readNotify;
            }
            if (!notify) {
                if (block) {
                    try {
                        this.readPending.acquire();
                    }
                    catch (InterruptedException e) {
                        throw new IOException(e);
                    }
                } else if (!this.readPending.tryAcquire()) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Socket: [" + this + "], Read in progress. Returning [0]"));
                    }
                    return 0;
                }
            }
            if ((nRead = this.populateReadBuffer(to)) > 0) {
                CompletionHandler<Integer, ByteBuffer> completionHandler2 = this.readCompletionHandler;
                synchronized (completionHandler2) {
                    this.readNotify = false;
                }
                this.readPending.release();
                return nRead;
            }
            CompletionHandler<Integer, ByteBuffer> completionHandler3 = this.readCompletionHandler;
            synchronized (completionHandler3) {
                int limit = this.socketBufferHandler.getReadBuffer().capacity();
                if (block && to.remaining() >= limit) {
                    to.limit(to.position() + limit);
                    nRead = this.fillReadBuffer(block, to);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Socket: [" + this + "], Read direct from socket: [" + nRead + "]"));
                    }
                } else {
                    nRead = this.fillReadBuffer(block);
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Socket: [" + this + "], Read into buffer: [" + nRead + "]"));
                    }
                    if (nRead > 0) {
                        nRead = this.populateReadBuffer(to);
                    } else if (nRead == 0 && !block) {
                        this.readInterest = true;
                    }
                }
                return nRead;
            }
        }

        @Override
        protected void doClose() {
            block13: {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Calling [" + this.getEndpoint() + "].closeSocket([" + this + "])"));
                }
                try {
                    this.getEndpoint().connections.remove(((Nio2Channel)this.getSocket()).getIOChannel());
                    if (((Nio2Channel)this.getSocket()).isOpen()) {
                        ((Nio2Channel)this.getSocket()).close(true);
                    }
                    if (this.getEndpoint().running && (this.nioChannels == null || !this.nioChannels.push((Object)((Nio2Channel)this.getSocket())))) {
                        ((Nio2Channel)this.getSocket()).free();
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
                    this.reset(Nio2Channel.CLOSED_NIO2_CHANNEL);
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

        @Override
        public boolean hasAsyncIO() {
            return this.getEndpoint().getUseAsyncIO();
        }

        @Override
        public boolean needSemaphores() {
            return true;
        }

        @Override
        public boolean hasPerOperationTimeout() {
            return true;
        }

        @Override
        protected <A> SocketWrapperBase.OperationState<A> newOperationState(boolean read, ByteBuffer[] buffers, int offset, int length, SocketWrapperBase.BlockingMode block, long timeout, TimeUnit unit, A attachment, SocketWrapperBase.CompletionCheck check, CompletionHandler<Long, ? super A> handler, Semaphore semaphore, SocketWrapperBase.VectoredIOCompletionHandler<A> completion) {
            return new Nio2OperationState(read, buffers, offset, length, block, timeout, unit, attachment, check, handler, semaphore, completion);
        }

        private int fillReadBuffer(boolean block) throws IOException {
            this.socketBufferHandler.configureReadBufferForWrite();
            return this.fillReadBuffer(block, this.socketBufferHandler.getReadBuffer());
        }

        private int fillReadBuffer(boolean block, ByteBuffer to) throws IOException {
            int nRead = 0;
            Future<Integer> integer = null;
            if (block) {
                try {
                    integer = ((Nio2Channel)this.getSocket()).read(to);
                    long timeout = this.getReadTimeout();
                    if (timeout > 0L) {
                        nRead = integer.get(timeout, TimeUnit.MILLISECONDS);
                    }
                    nRead = integer.get();
                }
                catch (ExecutionException e) {
                    if (e.getCause() instanceof IOException) {
                        throw (IOException)e.getCause();
                    }
                    throw new IOException(e);
                }
                catch (InterruptedException e) {
                    throw new IOException(e);
                }
                catch (TimeoutException e) {
                    integer.cancel(true);
                    throw new SocketTimeoutException();
                }
                finally {
                    this.readPending.release();
                }
            } else {
                Nio2Endpoint.startInline();
                ((Nio2Channel)this.getSocket()).read(to, AbstractEndpoint.toTimeout(this.getReadTimeout()), TimeUnit.MILLISECONDS, to, this.readCompletionHandler);
                Nio2Endpoint.endInline();
                if (this.readPending.availablePermits() == 1) {
                    nRead = to.position();
                }
            }
            return nRead;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected void writeNonBlocking(byte[] buf, int off, int len) throws IOException {
            CompletionHandler<Integer, ByteBuffer> completionHandler = this.writeCompletionHandler;
            synchronized (completionHandler) {
                this.checkError();
                if (this.writeNotify || this.writePending.tryAcquire()) {
                    this.socketBufferHandler.configureWriteBufferForWrite();
                    int thisTime = Nio2SocketWrapper.transfer(buf, off, len, this.socketBufferHandler.getWriteBuffer());
                    off += thisTime;
                    if ((len -= thisTime) > 0) {
                        this.nonBlockingWriteBuffer.add(buf, off, len);
                    }
                    this.flushNonBlockingInternal(true);
                } else {
                    this.nonBlockingWriteBuffer.add(buf, off, len);
                }
            }
        }

        @Override
        protected void writeNonBlocking(ByteBuffer from) throws IOException {
            this.writeNonBlockingInternal(from);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected void writeNonBlockingInternal(ByteBuffer from) throws IOException {
            CompletionHandler<Integer, ByteBuffer> completionHandler = this.writeCompletionHandler;
            synchronized (completionHandler) {
                this.checkError();
                if (this.writeNotify || this.writePending.tryAcquire()) {
                    this.socketBufferHandler.configureWriteBufferForWrite();
                    Nio2SocketWrapper.transfer(from, this.socketBufferHandler.getWriteBuffer());
                    if (from.remaining() > 0) {
                        this.nonBlockingWriteBuffer.add(from);
                    }
                    this.flushNonBlockingInternal(true);
                } else {
                    this.nonBlockingWriteBuffer.add(from);
                }
            }
        }

        @Override
        protected void doWrite(boolean block, ByteBuffer from) throws IOException {
            Future<Integer> integer = null;
            try {
                do {
                    integer = ((Nio2Channel)this.getSocket()).write(from);
                    long timeout = this.getWriteTimeout();
                    if (!(timeout > 0L ? integer.get(timeout, TimeUnit.MILLISECONDS) < 0 : integer.get() < 0)) continue;
                    throw new EOFException(sm.getString("iob.failedwrite"));
                } while (from.hasRemaining());
            }
            catch (ExecutionException e) {
                if (e.getCause() instanceof IOException) {
                    throw (IOException)e.getCause();
                }
                throw new IOException(e);
            }
            catch (InterruptedException e) {
                throw new IOException(e);
            }
            catch (TimeoutException e) {
                integer.cancel(true);
                throw new SocketTimeoutException();
            }
        }

        @Override
        protected void flushBlocking() throws IOException {
            this.checkError();
            try {
                if (!this.writePending.tryAcquire(AbstractEndpoint.toTimeout(this.getWriteTimeout()), TimeUnit.MILLISECONDS)) {
                    throw new SocketTimeoutException();
                }
                this.writePending.release();
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            super.flushBlocking();
        }

        @Override
        protected boolean flushNonBlocking() throws IOException {
            this.checkError();
            return this.flushNonBlockingInternal(false);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private boolean flushNonBlockingInternal(boolean hasPermit) {
            CompletionHandler<Integer, ByteBuffer> completionHandler = this.writeCompletionHandler;
            synchronized (completionHandler) {
                if (this.writeNotify || hasPermit || this.writePending.tryAcquire()) {
                    this.writeNotify = false;
                    this.socketBufferHandler.configureWriteBufferForRead();
                    if (!this.nonBlockingWriteBuffer.isEmpty()) {
                        ByteBuffer[] array = this.nonBlockingWriteBuffer.toArray(this.socketBufferHandler.getWriteBuffer());
                        Nio2Endpoint.startInline();
                        ((Nio2Channel)this.getSocket()).write(array, 0, array.length, AbstractEndpoint.toTimeout(this.getWriteTimeout()), TimeUnit.MILLISECONDS, array, this.gatheringWriteCompletionHandler);
                        Nio2Endpoint.endInline();
                    } else if (this.socketBufferHandler.getWriteBuffer().hasRemaining()) {
                        Nio2Endpoint.startInline();
                        ((Nio2Channel)this.getSocket()).write(this.socketBufferHandler.getWriteBuffer(), AbstractEndpoint.toTimeout(this.getWriteTimeout()), TimeUnit.MILLISECONDS, this.socketBufferHandler.getWriteBuffer(), this.writeCompletionHandler);
                        Nio2Endpoint.endInline();
                    } else {
                        if (!hasPermit) {
                            this.writePending.release();
                        }
                        this.writeInterest = false;
                    }
                }
                return this.hasDataToWrite();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean hasDataToRead() {
            CompletionHandler<Integer, ByteBuffer> completionHandler = this.readCompletionHandler;
            synchronized (completionHandler) {
                return !this.socketBufferHandler.isReadBufferEmpty() || this.readNotify || this.getError() != null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean hasDataToWrite() {
            CompletionHandler<Integer, ByteBuffer> completionHandler = this.writeCompletionHandler;
            synchronized (completionHandler) {
                return !this.socketBufferHandler.isWriteBufferEmpty() || !this.nonBlockingWriteBuffer.isEmpty() || this.writeNotify || this.writePending.availablePermits() == 0 || this.getError() != null;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isReadPending() {
            CompletionHandler<Integer, ByteBuffer> completionHandler = this.readCompletionHandler;
            synchronized (completionHandler) {
                return this.readPending.availablePermits() == 0;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean isWritePending() {
            CompletionHandler<Integer, ByteBuffer> completionHandler = this.writeCompletionHandler;
            synchronized (completionHandler) {
                return this.writePending.availablePermits() == 0;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean awaitReadComplete(long timeout, TimeUnit unit) {
            CompletionHandler<Integer, ByteBuffer> completionHandler = this.readCompletionHandler;
            synchronized (completionHandler) {
                try {
                    if (this.readNotify) {
                        return true;
                    }
                    if (this.readPending.tryAcquire(timeout, unit)) {
                        this.readPending.release();
                        return true;
                    }
                    return false;
                }
                catch (InterruptedException e) {
                    return false;
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean awaitWriteComplete(long timeout, TimeUnit unit) {
            CompletionHandler<Integer, ByteBuffer> completionHandler = this.writeCompletionHandler;
            synchronized (completionHandler) {
                try {
                    if (this.writeNotify) {
                        return true;
                    }
                    if (this.writePending.tryAcquire(timeout, unit)) {
                        this.writePending.release();
                        return true;
                    }
                    return false;
                }
                catch (InterruptedException e) {
                    return false;
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void registerReadInterest() {
            CompletionHandler<Integer, ByteBuffer> completionHandler = this.readCompletionHandler;
            synchronized (completionHandler) {
                if (this.readNotify) {
                    return;
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("endpoint.debug.registerRead", new Object[]{this}));
                }
                this.readInterest = true;
                if (this.readPending.tryAcquire()) {
                    try {
                        if (this.fillReadBuffer(false) > 0 && !this.getEndpoint().processSocket(this, SocketEvent.OPEN_READ, true)) {
                            this.close();
                        }
                    }
                    catch (IOException e) {
                        this.setError(e);
                    }
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void registerWriteInterest() {
            CompletionHandler<Integer, ByteBuffer> completionHandler = this.writeCompletionHandler;
            synchronized (completionHandler) {
                if (this.writeNotify) {
                    return;
                }
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("endpoint.debug.registerWrite", new Object[]{this}));
                }
                this.writeInterest = true;
                if (this.writePending.availablePermits() == 1 && !this.getEndpoint().processSocket(this, SocketEvent.OPEN_WRITE, true)) {
                    this.close();
                }
            }
        }

        @Override
        public SendfileDataBase createSendfileData(String filename, long pos, long length) {
            return new SendfileData(filename, pos, length);
        }

        @Override
        public SendfileState processSendfile(SendfileDataBase sendfileData) {
            SendfileData data = (SendfileData)sendfileData;
            this.setSendfileData(data);
            if (data.fchannel == null || !data.fchannel.isOpen()) {
                Path path = new File(sendfileData.fileName).toPath();
                try {
                    data.fchannel = FileChannel.open(path, StandardOpenOption.READ).position(sendfileData.pos);
                }
                catch (IOException e) {
                    return SendfileState.ERROR;
                }
            }
            ((Nio2Channel)this.getSocket()).getBufHandler().configureWriteBufferForWrite();
            ByteBuffer buffer = ((Nio2Channel)this.getSocket()).getBufHandler().getWriteBuffer();
            int nRead = -1;
            try {
                nRead = data.fchannel.read(buffer);
            }
            catch (IOException e1) {
                return SendfileState.ERROR;
            }
            if (nRead >= 0) {
                data.length -= (long)nRead;
                ((Nio2Channel)this.getSocket()).getBufHandler().configureWriteBufferForRead();
                Nio2Endpoint.startInline();
                ((Nio2Channel)this.getSocket()).write(buffer, AbstractEndpoint.toTimeout(this.getWriteTimeout()), TimeUnit.MILLISECONDS, data, this.sendfileHandler);
                Nio2Endpoint.endInline();
                if (data.doneInline) {
                    if (data.error) {
                        return SendfileState.ERROR;
                    }
                    return SendfileState.DONE;
                }
                return SendfileState.PENDING;
            }
            return SendfileState.ERROR;
        }

        @Override
        protected void populateRemoteAddr() {
            AsynchronousSocketChannel sc = ((Nio2Channel)this.getSocket()).getIOChannel();
            if (sc != null) {
                SocketAddress socketAddress = null;
                try {
                    socketAddress = sc.getRemoteAddress();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                if (socketAddress instanceof InetSocketAddress) {
                    this.remoteAddr = ((InetSocketAddress)socketAddress).getAddress().getHostAddress();
                }
            }
        }

        @Override
        protected void populateRemoteHost() {
            AsynchronousSocketChannel sc = ((Nio2Channel)this.getSocket()).getIOChannel();
            if (sc != null) {
                SocketAddress socketAddress = null;
                try {
                    socketAddress = sc.getRemoteAddress();
                }
                catch (IOException e) {
                    log.warn((Object)sm.getString("endpoint.warn.noRemoteHost", new Object[]{this.getSocket()}), (Throwable)e);
                }
                if (socketAddress instanceof InetSocketAddress) {
                    this.remoteHost = ((InetSocketAddress)socketAddress).getAddress().getHostName();
                    if (this.remoteAddr == null) {
                        this.remoteAddr = ((InetSocketAddress)socketAddress).getAddress().getHostAddress();
                    }
                }
            }
        }

        @Override
        protected void populateRemotePort() {
            AsynchronousSocketChannel sc = ((Nio2Channel)this.getSocket()).getIOChannel();
            if (sc != null) {
                SocketAddress socketAddress = null;
                try {
                    socketAddress = sc.getRemoteAddress();
                }
                catch (IOException e) {
                    log.warn((Object)sm.getString("endpoint.warn.noRemotePort", new Object[]{this.getSocket()}), (Throwable)e);
                }
                if (socketAddress instanceof InetSocketAddress) {
                    this.remotePort = ((InetSocketAddress)socketAddress).getPort();
                }
            }
        }

        @Override
        protected void populateLocalName() {
            AsynchronousSocketChannel sc = ((Nio2Channel)this.getSocket()).getIOChannel();
            if (sc != null) {
                SocketAddress socketAddress = null;
                try {
                    socketAddress = sc.getLocalAddress();
                }
                catch (IOException e) {
                    log.warn((Object)sm.getString("endpoint.warn.noLocalName", new Object[]{this.getSocket()}), (Throwable)e);
                }
                if (socketAddress instanceof InetSocketAddress) {
                    this.localName = ((InetSocketAddress)socketAddress).getHostName();
                }
            }
        }

        @Override
        protected void populateLocalAddr() {
            AsynchronousSocketChannel sc = ((Nio2Channel)this.getSocket()).getIOChannel();
            if (sc != null) {
                SocketAddress socketAddress = null;
                try {
                    socketAddress = sc.getLocalAddress();
                }
                catch (IOException e) {
                    log.warn((Object)sm.getString("endpoint.warn.noLocalAddr", new Object[]{this.getSocket()}), (Throwable)e);
                }
                if (socketAddress instanceof InetSocketAddress) {
                    this.localAddr = ((InetSocketAddress)socketAddress).getAddress().getHostAddress();
                }
            }
        }

        @Override
        protected void populateLocalPort() {
            AsynchronousSocketChannel sc = ((Nio2Channel)this.getSocket()).getIOChannel();
            if (sc != null) {
                SocketAddress socketAddress = null;
                try {
                    socketAddress = sc.getLocalAddress();
                }
                catch (IOException e) {
                    log.warn((Object)sm.getString("endpoint.warn.noLocalPort", new Object[]{this.getSocket()}), (Throwable)e);
                }
                if (socketAddress instanceof InetSocketAddress) {
                    this.localPort = ((InetSocketAddress)socketAddress).getPort();
                }
            }
        }

        @Override
        public SSLSupport getSslSupport() {
            if (this.getSocket() instanceof SecureNio2Channel) {
                SecureNio2Channel ch = (SecureNio2Channel)this.getSocket();
                return ch.getSSLSupport();
            }
            return null;
        }

        @Override
        public void doClientAuth(SSLSupport sslSupport) throws IOException {
            SecureNio2Channel sslChannel = (SecureNio2Channel)this.getSocket();
            SSLEngine engine = sslChannel.getSslEngine();
            if (!engine.getNeedClientAuth()) {
                engine.setNeedClientAuth(true);
                sslChannel.rehandshake();
                ((JSSESupport)sslSupport).setSession(engine.getSession());
            }
        }

        @Override
        public void setAppReadBufHandler(ApplicationBufferHandler handler) {
            ((Nio2Channel)this.getSocket()).setAppReadBufHandler(handler);
        }

        private class Nio2OperationState<A>
        extends SocketWrapperBase.OperationState<A> {
            private Nio2OperationState(boolean read, ByteBuffer[] buffers, int offset, int length, SocketWrapperBase.BlockingMode block, long timeout, TimeUnit unit, A attachment, SocketWrapperBase.CompletionCheck check, CompletionHandler<Long, ? super A> handler, Semaphore semaphore, SocketWrapperBase.VectoredIOCompletionHandler<A> completion) {
                super(Nio2SocketWrapper.this, read, buffers, offset, length, block, timeout, unit, attachment, check, handler, semaphore, completion);
            }

            @Override
            protected boolean isInline() {
                return Nio2Endpoint.isInline();
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            protected void start() {
                if (this.read) {
                    CompletionHandler completionHandler = Nio2SocketWrapper.this.readCompletionHandler;
                    synchronized (completionHandler) {
                        Nio2SocketWrapper.this.readNotify = true;
                    }
                }
                CompletionHandler completionHandler = Nio2SocketWrapper.this.writeCompletionHandler;
                synchronized (completionHandler) {
                    Nio2SocketWrapper.this.writeNotify = true;
                }
                Nio2Endpoint.startInline();
                try {
                    this.run();
                }
                finally {
                    Nio2Endpoint.endInline();
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                if (this.read) {
                    long nBytes = 0L;
                    if (!Nio2SocketWrapper.this.socketBufferHandler.isReadBufferEmpty()) {
                        CompletionHandler completionHandler = Nio2SocketWrapper.this.readCompletionHandler;
                        synchronized (completionHandler) {
                            Nio2SocketWrapper.this.socketBufferHandler.configureReadBufferForRead();
                            for (int i = 0; i < this.length && !Nio2SocketWrapper.this.socketBufferHandler.isReadBufferEmpty(); ++i) {
                                nBytes += (long)SocketWrapperBase.transfer(Nio2SocketWrapper.this.socketBufferHandler.getReadBuffer(), this.buffers[this.offset + i]);
                            }
                        }
                        if (nBytes > 0L) {
                            this.completion.completed(nBytes, this);
                        }
                    }
                    if (nBytes == 0L) {
                        ((Nio2Channel)Nio2SocketWrapper.this.getSocket()).read(this.buffers, this.offset, this.length, this.timeout, this.unit, this, this.completion);
                    }
                } else {
                    if (!Nio2SocketWrapper.this.socketBufferHandler.isWriteBufferEmpty()) {
                        CompletionHandler completionHandler = Nio2SocketWrapper.this.writeCompletionHandler;
                        synchronized (completionHandler) {
                            Nio2SocketWrapper.this.socketBufferHandler.configureWriteBufferForRead();
                            ByteBuffer[] array = Nio2SocketWrapper.this.nonBlockingWriteBuffer.toArray(Nio2SocketWrapper.this.socketBufferHandler.getWriteBuffer());
                            if (SocketWrapperBase.buffersArrayHasRemaining(array, 0, array.length)) {
                                ((Nio2Channel)Nio2SocketWrapper.this.getSocket()).write(array, 0, array.length, this.timeout, this.unit, array, new CompletionHandler<Long, ByteBuffer[]>(){

                                    @Override
                                    public void completed(Long nBytes, ByteBuffer[] buffers) {
                                        if (nBytes < 0L) {
                                            this.failed((Throwable)new EOFException(), null);
                                        } else if (SocketWrapperBase.buffersArrayHasRemaining(buffers, 0, buffers.length)) {
                                            ((Nio2Channel)Nio2SocketWrapper.this.getSocket()).write(buffers, 0, buffers.length, AbstractEndpoint.toTimeout(Nio2SocketWrapper.this.getWriteTimeout()), TimeUnit.MILLISECONDS, buffers, this);
                                        } else {
                                            Nio2OperationState.this.process();
                                        }
                                    }

                                    @Override
                                    public void failed(Throwable exc, ByteBuffer[] buffers) {
                                        Nio2OperationState.this.completion.failed(exc, Nio2OperationState.this);
                                    }
                                });
                                return;
                            }
                        }
                    }
                    ((Nio2Channel)Nio2SocketWrapper.this.getSocket()).write(this.buffers, this.offset, this.length, this.timeout, this.unit, this, this.completion);
                }
            }
        }
    }

    protected class SocketProcessor
    extends SocketProcessorBase<Nio2Channel> {
        public SocketProcessor(SocketWrapperBase<Nio2Channel> socketWrapper, SocketEvent event) {
            super(socketWrapper, event);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected void doRun() {
            boolean launch = false;
            try {
                int handshake;
                block30: {
                    handshake = -1;
                    try {
                        if (((Nio2Channel)this.socketWrapper.getSocket()).isHandshakeComplete()) {
                            handshake = 0;
                        } else if (this.event == SocketEvent.STOP || this.event == SocketEvent.DISCONNECT || this.event == SocketEvent.ERROR) {
                            handshake = -1;
                        } else {
                            handshake = ((Nio2Channel)this.socketWrapper.getSocket()).handshake();
                            this.event = SocketEvent.OPEN_READ;
                        }
                    }
                    catch (IOException x) {
                        handshake = -1;
                        if (!logHandshake.isDebugEnabled()) break block30;
                        logHandshake.debug((Object)AbstractEndpoint.sm.getString("endpoint.err.handshake", new Object[]{this.socketWrapper.getRemoteAddr(), Integer.toString(this.socketWrapper.getRemotePort())}), (Throwable)x);
                    }
                }
                if (handshake == 0) {
                    AbstractEndpoint.Handler.SocketState state = AbstractEndpoint.Handler.SocketState.OPEN;
                    state = this.event == null ? Nio2Endpoint.this.getHandler().process(this.socketWrapper, SocketEvent.OPEN_READ) : Nio2Endpoint.this.getHandler().process(this.socketWrapper, this.event);
                    if (state == AbstractEndpoint.Handler.SocketState.CLOSED) {
                        this.socketWrapper.close();
                    } else if (state == AbstractEndpoint.Handler.SocketState.UPGRADING) {
                        launch = true;
                    }
                } else if (handshake == -1) {
                    Nio2Endpoint.this.getHandler().process(this.socketWrapper, SocketEvent.CONNECT_FAIL);
                    this.socketWrapper.close();
                }
            }
            catch (VirtualMachineError vme) {
                ExceptionUtils.handleThrowable((Throwable)vme);
            }
            catch (Throwable t) {
                log.error((Object)AbstractEndpoint.sm.getString("endpoint.processing.fail"), t);
                if (this.socketWrapper != null) {
                    ((Nio2SocketWrapper)this.socketWrapper).close();
                }
            }
            finally {
                block32: {
                    if (launch) {
                        try {
                            Nio2Endpoint.this.getExecutor().execute(new SocketProcessor(this.socketWrapper, SocketEvent.OPEN_READ));
                        }
                        catch (NullPointerException npe) {
                            if (!Nio2Endpoint.this.running) break block32;
                            log.error((Object)AbstractEndpoint.sm.getString("endpoint.launch.fail"), (Throwable)npe);
                        }
                    }
                }
                this.socketWrapper = null;
                this.event = null;
                if (Nio2Endpoint.this.running && Nio2Endpoint.this.processorCache != null) {
                    Nio2Endpoint.this.processorCache.push((Object)this);
                }
            }
        }
    }

    public static class SendfileData
    extends SendfileDataBase {
        private FileChannel fchannel;
        private boolean doneInline = false;
        private boolean error = false;

        public SendfileData(String filename, long pos, long length) {
            super(filename, pos, length);
        }
    }
}

