/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.net;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.channels.ReadPendingException;
import java.nio.channels.WritePendingException;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.net.SendfileDataBase;
import org.apache.tomcat.util.net.SendfileState;
import org.apache.tomcat.util.net.SocketBufferHandler;
import org.apache.tomcat.util.net.SocketEvent;
import org.apache.tomcat.util.net.WriteBuffer;
import org.apache.tomcat.util.res.StringManager;

public abstract class SocketWrapperBase<E> {
    private static final Log log = LogFactory.getLog(SocketWrapperBase.class);
    protected static final StringManager sm = StringManager.getManager(SocketWrapperBase.class);
    private E socket;
    private final AbstractEndpoint<E, ?> endpoint;
    private final Lock lock = new ReentrantLock();
    protected final AtomicBoolean closed = new AtomicBoolean(false);
    private volatile long readTimeout = -1L;
    private volatile long writeTimeout = -1L;
    protected volatile IOException previousIOException = null;
    private volatile int keepAliveLeft = 100;
    private volatile boolean upgraded = false;
    private boolean secure = false;
    private String negotiatedProtocol = null;
    protected String localAddr = null;
    protected String localName = null;
    protected int localPort = -1;
    protected String remoteAddr = null;
    protected String remoteHost = null;
    protected int remotePort = -1;
    private volatile IOException error = null;
    protected volatile SocketBufferHandler socketBufferHandler = null;
    protected int bufferedWriteSize = 65536;
    protected final WriteBuffer nonBlockingWriteBuffer = new WriteBuffer(this.bufferedWriteSize);
    protected final Semaphore readPending;
    protected volatile OperationState<?> readOperation = null;
    protected final Semaphore writePending;
    protected volatile OperationState<?> writeOperation = null;
    private final AtomicReference<Object> currentProcessor = new AtomicReference();
    public static final CompletionCheck COMPLETE_WRITE = new CompletionCheck(){

        @Override
        public CompletionHandlerCall callHandler(CompletionState state, ByteBuffer[] buffers, int offset, int length) {
            for (int i = 0; i < length; ++i) {
                if (!buffers[offset + i].hasRemaining()) continue;
                return CompletionHandlerCall.CONTINUE;
            }
            return state == CompletionState.DONE ? CompletionHandlerCall.DONE : CompletionHandlerCall.NONE;
        }
    };
    public static final CompletionCheck COMPLETE_WRITE_WITH_COMPLETION = new CompletionCheck(){

        @Override
        public CompletionHandlerCall callHandler(CompletionState state, ByteBuffer[] buffers, int offset, int length) {
            for (int i = 0; i < length; ++i) {
                if (!buffers[offset + i].hasRemaining()) continue;
                return CompletionHandlerCall.CONTINUE;
            }
            return CompletionHandlerCall.DONE;
        }
    };
    public static final CompletionCheck READ_DATA = new CompletionCheck(){

        @Override
        public CompletionHandlerCall callHandler(CompletionState state, ByteBuffer[] buffers, int offset, int length) {
            return state == CompletionState.DONE ? CompletionHandlerCall.DONE : CompletionHandlerCall.NONE;
        }
    };
    public static final CompletionCheck COMPLETE_READ_WITH_COMPLETION = COMPLETE_WRITE_WITH_COMPLETION;
    public static final CompletionCheck COMPLETE_READ = COMPLETE_WRITE;

    public SocketWrapperBase(E socket, AbstractEndpoint<E, ?> endpoint) {
        this.socket = socket;
        this.endpoint = endpoint;
        if (endpoint.getUseAsyncIO() || this.needSemaphores()) {
            this.readPending = new Semaphore(1);
            this.writePending = new Semaphore(1);
        } else {
            this.readPending = null;
            this.writePending = null;
        }
    }

    public E getSocket() {
        return this.socket;
    }

    protected void reset(E closedSocket) {
        this.socket = closedSocket;
    }

    protected AbstractEndpoint<E, ?> getEndpoint() {
        return this.endpoint;
    }

    public Lock getLock() {
        return this.lock;
    }

    public Object getCurrentProcessor() {
        return this.currentProcessor.get();
    }

    public void setCurrentProcessor(Object currentProcessor) {
        this.currentProcessor.set(currentProcessor);
    }

    public Object takeCurrentProcessor() {
        return this.currentProcessor.getAndSet(null);
    }

    public void execute(Runnable runnable) {
        Executor executor = this.endpoint.getExecutor();
        if (!this.endpoint.isRunning() || executor == null) {
            throw new RejectedExecutionException();
        }
        executor.execute(runnable);
    }

    public IOException getError() {
        return this.error;
    }

    public void setError(IOException error) {
        if (this.error != null) {
            return;
        }
        this.error = error;
    }

    public void checkError() throws IOException {
        if (this.error != null) {
            throw this.error;
        }
    }

    @Deprecated
    public boolean isUpgraded() {
        return this.upgraded;
    }

    @Deprecated
    public void setUpgraded(boolean upgraded) {
        this.upgraded = upgraded;
    }

    @Deprecated
    public boolean isSecure() {
        return this.secure;
    }

    @Deprecated
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public String getNegotiatedProtocol() {
        return this.negotiatedProtocol;
    }

    public void setNegotiatedProtocol(String negotiatedProtocol) {
        this.negotiatedProtocol = negotiatedProtocol;
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout > 0L ? readTimeout : -1L;
    }

    public long getReadTimeout() {
        return this.readTimeout;
    }

    public void setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout > 0L ? writeTimeout : -1L;
    }

    public long getWriteTimeout() {
        return this.writeTimeout;
    }

    public void setKeepAliveLeft(int keepAliveLeft) {
        this.keepAliveLeft = keepAliveLeft;
    }

    public int decrementKeepAlive() {
        return --this.keepAliveLeft;
    }

    public String getRemoteHost() {
        if (this.remoteHost == null) {
            this.populateRemoteHost();
        }
        return this.remoteHost;
    }

    protected abstract void populateRemoteHost();

    public String getRemoteAddr() {
        if (this.remoteAddr == null) {
            this.populateRemoteAddr();
        }
        return this.remoteAddr;
    }

    protected abstract void populateRemoteAddr();

    public int getRemotePort() {
        if (this.remotePort == -1) {
            this.populateRemotePort();
        }
        return this.remotePort;
    }

    protected abstract void populateRemotePort();

    public String getLocalName() {
        if (this.localName == null) {
            this.populateLocalName();
        }
        return this.localName;
    }

    protected abstract void populateLocalName();

    public String getLocalAddr() {
        if (this.localAddr == null) {
            this.populateLocalAddr();
        }
        return this.localAddr;
    }

    protected abstract void populateLocalAddr();

    public int getLocalPort() {
        if (this.localPort == -1) {
            this.populateLocalPort();
        }
        return this.localPort;
    }

    protected abstract void populateLocalPort();

    public SocketBufferHandler getSocketBufferHandler() {
        return this.socketBufferHandler;
    }

    public boolean hasDataToRead() {
        return true;
    }

    public boolean hasDataToWrite() {
        return !this.socketBufferHandler.isWriteBufferEmpty() || !this.nonBlockingWriteBuffer.isEmpty();
    }

    public boolean isReadyForWrite() {
        boolean result = this.canWrite();
        if (!result) {
            this.registerWriteInterest();
        }
        return result;
    }

    public boolean canWrite() {
        if (this.socketBufferHandler == null) {
            throw new IllegalStateException(sm.getString("socket.closed"));
        }
        return this.socketBufferHandler.isWriteBufferWritable() && this.nonBlockingWriteBuffer.isEmpty();
    }

    public String toString() {
        return super.toString() + ":" + String.valueOf(this.socket);
    }

    public abstract int read(boolean var1, byte[] var2, int var3, int var4) throws IOException;

    public abstract int read(boolean var1, ByteBuffer var2) throws IOException;

    public abstract boolean isReadyForRead() throws IOException;

    public abstract void setAppReadBufHandler(ApplicationBufferHandler var1);

    protected int populateReadBuffer(byte[] b, int off, int len) {
        this.socketBufferHandler.configureReadBufferForRead();
        ByteBuffer readBuffer = this.socketBufferHandler.getReadBuffer();
        int remaining = readBuffer.remaining();
        if (remaining > 0) {
            remaining = Math.min(remaining, len);
            readBuffer.get(b, off, remaining);
            if (log.isDebugEnabled()) {
                log.debug((Object)("Socket: [" + this + "], Read from buffer: [" + remaining + "]"));
            }
        }
        return remaining;
    }

    protected int populateReadBuffer(ByteBuffer to) {
        this.socketBufferHandler.configureReadBufferForRead();
        int nRead = SocketWrapperBase.transfer(this.socketBufferHandler.getReadBuffer(), to);
        if (log.isDebugEnabled()) {
            log.debug((Object)("Socket: [" + this + "], Read from buffer: [" + nRead + "]"));
        }
        return nRead;
    }

    public void unRead(ByteBuffer returnedInput) {
        if (returnedInput != null) {
            this.socketBufferHandler.unReadReadBuffer(returnedInput);
        }
    }

    public void close() {
        if (this.closed.compareAndSet(false, true)) {
            try {
                this.getEndpoint().getHandler().release(this);
            }
            catch (Throwable e) {
                ExceptionUtils.handleThrowable((Throwable)e);
                if (log.isDebugEnabled()) {
                    log.error((Object)sm.getString("endpoint.debug.handlerRelease"), e);
                }
            }
            finally {
                this.getEndpoint().countDownConnection();
                this.doClose();
            }
        }
    }

    protected abstract void doClose();

    public boolean isClosed() {
        return this.closed.get();
    }

    public final void write(boolean block, byte[] buf, int off, int len) throws IOException {
        if (len == 0 || buf == null) {
            return;
        }
        if (block) {
            this.writeBlocking(buf, off, len);
        } else {
            this.writeNonBlocking(buf, off, len);
        }
    }

    public final void write(boolean block, ByteBuffer from) throws IOException {
        if (from == null || from.remaining() == 0) {
            return;
        }
        if (block) {
            this.writeBlocking(from);
        } else {
            this.writeNonBlocking(from);
        }
    }

    protected void writeBlocking(byte[] buf, int off, int len) throws IOException {
        if (len > 0) {
            this.socketBufferHandler.configureWriteBufferForWrite();
            int thisTime = SocketWrapperBase.transfer(buf, off, len, this.socketBufferHandler.getWriteBuffer());
            len -= thisTime;
            while (len > 0) {
                this.doWrite(true);
                this.socketBufferHandler.configureWriteBufferForWrite();
                thisTime = SocketWrapperBase.transfer(buf, off += thisTime, len, this.socketBufferHandler.getWriteBuffer());
                len -= thisTime;
            }
        }
    }

    protected void writeBlocking(ByteBuffer from) throws IOException {
        if (from.hasRemaining()) {
            this.socketBufferHandler.configureWriteBufferForWrite();
            SocketWrapperBase.transfer(from, this.socketBufferHandler.getWriteBuffer());
            while (from.hasRemaining()) {
                this.doWrite(true);
                this.socketBufferHandler.configureWriteBufferForWrite();
                SocketWrapperBase.transfer(from, this.socketBufferHandler.getWriteBuffer());
            }
        }
    }

    protected void writeNonBlocking(byte[] buf, int off, int len) throws IOException {
        if (len > 0 && this.nonBlockingWriteBuffer.isEmpty() && this.socketBufferHandler.isWriteBufferWritable()) {
            this.socketBufferHandler.configureWriteBufferForWrite();
            int thisTime = SocketWrapperBase.transfer(buf, off, len, this.socketBufferHandler.getWriteBuffer());
            len -= thisTime;
            while (len > 0) {
                off += thisTime;
                this.doWrite(false);
                if (len <= 0 || !this.socketBufferHandler.isWriteBufferWritable()) break;
                this.socketBufferHandler.configureWriteBufferForWrite();
                thisTime = SocketWrapperBase.transfer(buf, off, len, this.socketBufferHandler.getWriteBuffer());
                len -= thisTime;
            }
        }
        if (len > 0) {
            this.nonBlockingWriteBuffer.add(buf, off, len);
        }
    }

    protected void writeNonBlocking(ByteBuffer from) throws IOException {
        if (from.hasRemaining() && this.nonBlockingWriteBuffer.isEmpty() && this.socketBufferHandler.isWriteBufferWritable()) {
            this.writeNonBlockingInternal(from);
        }
        if (from.hasRemaining()) {
            this.nonBlockingWriteBuffer.add(from);
        }
    }

    protected void writeNonBlockingInternal(ByteBuffer from) throws IOException {
        this.socketBufferHandler.configureWriteBufferForWrite();
        SocketWrapperBase.transfer(from, this.socketBufferHandler.getWriteBuffer());
        while (from.hasRemaining()) {
            this.doWrite(false);
            if (!this.socketBufferHandler.isWriteBufferWritable()) break;
            this.socketBufferHandler.configureWriteBufferForWrite();
            SocketWrapperBase.transfer(from, this.socketBufferHandler.getWriteBuffer());
        }
    }

    public boolean flush(boolean block) throws IOException {
        boolean result = false;
        if (block) {
            this.flushBlocking();
        } else {
            result = this.flushNonBlocking();
        }
        return result;
    }

    protected void flushBlocking() throws IOException {
        this.doWrite(true);
        if (!this.nonBlockingWriteBuffer.isEmpty()) {
            this.nonBlockingWriteBuffer.write(this, true);
            if (!this.socketBufferHandler.isWriteBufferEmpty()) {
                this.doWrite(true);
            }
        }
    }

    protected abstract boolean flushNonBlocking() throws IOException;

    protected void doWrite(boolean block) throws IOException {
        this.socketBufferHandler.configureWriteBufferForRead();
        this.doWrite(block, this.socketBufferHandler.getWriteBuffer());
    }

    protected abstract void doWrite(boolean var1, ByteBuffer var2) throws IOException;

    public void processSocket(SocketEvent socketStatus, boolean dispatch) {
        this.endpoint.processSocket(this, socketStatus, dispatch);
    }

    public abstract void registerReadInterest();

    public abstract void registerWriteInterest();

    public abstract SendfileDataBase createSendfileData(String var1, long var2, long var4);

    public abstract SendfileState processSendfile(SendfileDataBase var1);

    public abstract void doClientAuth(SSLSupport var1) throws IOException;

    @Deprecated
    public SSLSupport getSslSupport(String clientCertProvider) {
        return this.getSslSupport();
    }

    public abstract SSLSupport getSslSupport();

    public boolean hasAsyncIO() {
        return this.readPending != null;
    }

    public boolean needSemaphores() {
        return false;
    }

    public boolean hasPerOperationTimeout() {
        return false;
    }

    public boolean isReadPending() {
        return false;
    }

    public boolean isWritePending() {
        return false;
    }

    @Deprecated
    public boolean awaitReadComplete(long timeout, TimeUnit unit) {
        return true;
    }

    @Deprecated
    public boolean awaitWriteComplete(long timeout, TimeUnit unit) {
        return true;
    }

    public final <A> CompletionState read(long timeout, TimeUnit unit, A attachment, CompletionHandler<Long, ? super A> handler, ByteBuffer ... dsts) {
        if (dsts == null) {
            throw new IllegalArgumentException();
        }
        return this.read(dsts, 0, dsts.length, BlockingMode.CLASSIC, timeout, unit, attachment, null, handler);
    }

    public final <A> CompletionState read(BlockingMode block, long timeout, TimeUnit unit, A attachment, CompletionCheck check, CompletionHandler<Long, ? super A> handler, ByteBuffer ... dsts) {
        if (dsts == null) {
            throw new IllegalArgumentException();
        }
        return this.read(dsts, 0, dsts.length, block, timeout, unit, attachment, check, handler);
    }

    public final <A> CompletionState read(ByteBuffer[] dsts, int offset, int length, BlockingMode block, long timeout, TimeUnit unit, A attachment, CompletionCheck check, CompletionHandler<Long, ? super A> handler) {
        return this.vectoredOperation(true, dsts, offset, length, block, timeout, unit, attachment, check, handler);
    }

    public final <A> CompletionState write(long timeout, TimeUnit unit, A attachment, CompletionHandler<Long, ? super A> handler, ByteBuffer ... srcs) {
        if (srcs == null) {
            throw new IllegalArgumentException();
        }
        return this.write(srcs, 0, srcs.length, BlockingMode.CLASSIC, timeout, unit, attachment, null, handler);
    }

    public final <A> CompletionState write(BlockingMode block, long timeout, TimeUnit unit, A attachment, CompletionCheck check, CompletionHandler<Long, ? super A> handler, ByteBuffer ... srcs) {
        if (srcs == null) {
            throw new IllegalArgumentException();
        }
        return this.write(srcs, 0, srcs.length, block, timeout, unit, attachment, check, handler);
    }

    public final <A> CompletionState write(ByteBuffer[] srcs, int offset, int length, BlockingMode block, long timeout, TimeUnit unit, A attachment, CompletionCheck check, CompletionHandler<Long, ? super A> handler) {
        return this.vectoredOperation(false, srcs, offset, length, block, timeout, unit, attachment, check, handler);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final <A> CompletionState vectoredOperation(boolean read, ByteBuffer[] buffers, int offset, int length, BlockingMode block, long timeout, TimeUnit unit, A attachment, CompletionCheck check, CompletionHandler<Long, ? super A> handler) {
        block24: {
            IOException ioe = this.getError();
            if (ioe != null) {
                handler.failed(ioe, attachment);
                return CompletionState.ERROR;
            }
            if (timeout == -1L) {
                timeout = AbstractEndpoint.toTimeout(read ? this.getReadTimeout() : this.getWriteTimeout());
                unit = TimeUnit.MILLISECONDS;
            } else if (!this.hasPerOperationTimeout() && unit.toMillis(timeout) != (read ? this.getReadTimeout() : this.getWriteTimeout())) {
                if (read) {
                    this.setReadTimeout(unit.toMillis(timeout));
                } else {
                    this.setWriteTimeout(unit.toMillis(timeout));
                }
            }
            if (block == BlockingMode.BLOCK || block == BlockingMode.SEMI_BLOCK) {
                try {
                    if (read ? !this.readPending.tryAcquire(timeout, unit) : !this.writePending.tryAcquire(timeout, unit)) {
                        handler.failed(new SocketTimeoutException(), attachment);
                        return CompletionState.ERROR;
                    }
                    break block24;
                }
                catch (InterruptedException e) {
                    handler.failed(e, attachment);
                    return CompletionState.ERROR;
                }
            }
            if (read ? !this.readPending.tryAcquire() : !this.writePending.tryAcquire()) {
                if (block == BlockingMode.NON_BLOCK) {
                    return CompletionState.NOT_DONE;
                }
                handler.failed(read ? new ReadPendingException() : new WritePendingException(), attachment);
                return CompletionState.ERROR;
            }
        }
        VectoredIOCompletionHandler completion = new VectoredIOCompletionHandler();
        OperationState<A> state = this.newOperationState(read, buffers, offset, length, block, timeout, unit, attachment, check, handler, read ? this.readPending : this.writePending, completion);
        if (read) {
            this.readOperation = state;
        } else {
            this.writeOperation = state;
        }
        state.start();
        if (block == BlockingMode.BLOCK) {
            OperationState<A> operationState = state;
            synchronized (operationState) {
                if (state.state == CompletionState.PENDING) {
                    try {
                        state.wait(unit.toMillis(timeout));
                        if (state.state == CompletionState.PENDING) {
                            if (handler != null && state.callHandler.compareAndSet(true, false)) {
                                handler.failed(new SocketTimeoutException(this.getTimeoutMsg(read)), attachment);
                            }
                            return CompletionState.ERROR;
                        }
                    }
                    catch (InterruptedException e) {
                        if (handler != null && state.callHandler.compareAndSet(true, false)) {
                            handler.failed(new SocketTimeoutException(this.getTimeoutMsg(read)), attachment);
                        }
                        return CompletionState.ERROR;
                    }
                }
            }
        }
        return state.state;
    }

    private String getTimeoutMsg(boolean read) {
        if (read) {
            return sm.getString("socketWrapper.readTimeout");
        }
        return sm.getString("socketWrapper.writeTimeout");
    }

    protected abstract <A> OperationState<A> newOperationState(boolean var1, ByteBuffer[] var2, int var3, int var4, BlockingMode var5, long var6, TimeUnit var8, A var9, CompletionCheck var10, CompletionHandler<Long, ? super A> var11, Semaphore var12, VectoredIOCompletionHandler<A> var13);

    protected static int transfer(byte[] from, int offset, int length, ByteBuffer to) {
        int max = Math.min(length, to.remaining());
        if (max > 0) {
            to.put(from, offset, max);
        }
        return max;
    }

    protected static int transfer(ByteBuffer from, ByteBuffer to) {
        int max = Math.min(from.remaining(), to.remaining());
        if (max > 0) {
            int fromLimit = from.limit();
            from.limit(from.position() + max);
            to.put(from);
            from.limit(fromLimit);
        }
        return max;
    }

    protected static boolean buffersArrayHasRemaining(ByteBuffer[] buffers, int offset, int length) {
        for (int pos = offset; pos < offset + length; ++pos) {
            if (!buffers[pos].hasRemaining()) continue;
            return true;
        }
        return false;
    }

    protected abstract class OperationState<A>
    implements Runnable {
        protected final boolean read;
        protected final ByteBuffer[] buffers;
        protected final int offset;
        protected final int length;
        protected final A attachment;
        protected final long timeout;
        protected final TimeUnit unit;
        protected final BlockingMode block;
        protected final CompletionCheck check;
        protected final CompletionHandler<Long, ? super A> handler;
        protected final Semaphore semaphore;
        protected final VectoredIOCompletionHandler<A> completion;
        protected final AtomicBoolean callHandler;
        protected volatile long nBytes = 0L;
        protected volatile CompletionState state = CompletionState.PENDING;
        protected boolean completionDone = true;

        protected OperationState(boolean read, ByteBuffer[] buffers, int offset, int length, BlockingMode block, long timeout, TimeUnit unit, A attachment, CompletionCheck check, CompletionHandler<Long, ? super A> handler, Semaphore semaphore, VectoredIOCompletionHandler<A> completion) {
            this.read = read;
            this.buffers = buffers;
            this.offset = offset;
            this.length = length;
            this.block = block;
            this.timeout = timeout;
            this.unit = unit;
            this.attachment = attachment;
            this.check = check;
            this.handler = handler;
            this.semaphore = semaphore;
            this.completion = completion;
            this.callHandler = handler != null ? new AtomicBoolean(true) : null;
        }

        protected abstract boolean isInline();

        protected boolean hasOutboundRemaining() {
            return false;
        }

        protected boolean process() {
            try {
                SocketWrapperBase.this.getEndpoint().getExecutor().execute(this);
                return true;
            }
            catch (RejectedExecutionException ree) {
                log.warn((Object)sm.getString("endpoint.executor.fail", new Object[]{SocketWrapperBase.this}), (Throwable)ree);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                log.error((Object)sm.getString("endpoint.process.fail"), t);
            }
            return false;
        }

        protected void start() {
            this.run();
        }

        protected void end() {
        }
    }

    public static enum BlockingMode {
        CLASSIC,
        NON_BLOCK,
        SEMI_BLOCK,
        BLOCK;

    }

    public static interface CompletionCheck {
        public CompletionHandlerCall callHandler(CompletionState var1, ByteBuffer[] var2, int var3, int var4);
    }

    public static enum CompletionState {
        PENDING,
        NOT_DONE,
        INLINE,
        ERROR,
        DONE;

    }

    protected class VectoredIOCompletionHandler<A>
    implements CompletionHandler<Long, OperationState<A>> {
        protected VectoredIOCompletionHandler() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void completed(Long nBytes, OperationState<A> state) {
            if (nBytes < 0L) {
                this.failed((Throwable)new EOFException(), state);
            } else {
                state.nBytes += nBytes.longValue();
                CompletionState currentState = state.isInline() ? CompletionState.INLINE : CompletionState.DONE;
                boolean complete = true;
                boolean completion = true;
                if (state.check != null) {
                    CompletionHandlerCall call = state.check.callHandler(currentState, state.buffers, state.offset, state.length);
                    if (call == CompletionHandlerCall.CONTINUE || !state.read && state.hasOutboundRemaining()) {
                        complete = false;
                    } else if (call == CompletionHandlerCall.NONE) {
                        completion = false;
                    }
                }
                if (complete) {
                    boolean notify = false;
                    if (state.read) {
                        SocketWrapperBase.this.readOperation = null;
                    } else {
                        SocketWrapperBase.this.writeOperation = null;
                    }
                    state.semaphore.release();
                    if (state.block == BlockingMode.BLOCK && currentState != CompletionState.INLINE) {
                        notify = true;
                    } else {
                        state.state = currentState;
                    }
                    state.end();
                    if (completion && state.handler != null && state.callHandler.compareAndSet(true, false)) {
                        state.handler.completed(state.nBytes, state.attachment);
                    }
                    OperationState<A> operationState = state;
                    synchronized (operationState) {
                        state.completionDone = true;
                        if (notify) {
                            state.state = currentState;
                            state.notify();
                        }
                    }
                }
                OperationState<A> operationState = state;
                synchronized (operationState) {
                    state.completionDone = true;
                }
                state.run();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void failed(Throwable exc, OperationState<A> state) {
            IOException ioe = null;
            if (exc instanceof InterruptedByTimeoutException) {
                ioe = new SocketTimeoutException();
                exc = ioe;
            } else if (exc instanceof IOException) {
                ioe = (IOException)exc;
            }
            SocketWrapperBase.this.setError(ioe);
            boolean notify = false;
            if (state.read) {
                SocketWrapperBase.this.readOperation = null;
            } else {
                SocketWrapperBase.this.writeOperation = null;
            }
            state.semaphore.release();
            if (state.block == BlockingMode.BLOCK) {
                notify = true;
            } else {
                state.state = state.isInline() ? CompletionState.ERROR : CompletionState.DONE;
            }
            state.end();
            if (state.handler != null && state.callHandler.compareAndSet(true, false)) {
                state.handler.failed(exc, state.attachment);
            }
            OperationState<A> operationState = state;
            synchronized (operationState) {
                state.completionDone = true;
                if (notify) {
                    state.state = state.isInline() ? CompletionState.ERROR : CompletionState.DONE;
                    state.notify();
                }
            }
        }
    }

    public static enum CompletionHandlerCall {
        CONTINUE,
        NONE,
        DONE;

    }
}

