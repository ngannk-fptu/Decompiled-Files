/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.AsyncChannelWrapper;
import org.apache.tomcat.websocket.ReadBufferOverflowException;

public class AsyncChannelWrapperSecure
implements AsyncChannelWrapper {
    private final Log log = LogFactory.getLog(AsyncChannelWrapperSecure.class);
    private static final StringManager sm = StringManager.getManager(AsyncChannelWrapperSecure.class);
    private static final ByteBuffer DUMMY = ByteBuffer.allocate(16921);
    private final AsynchronousSocketChannel socketChannel;
    private final SSLEngine sslEngine;
    private final ByteBuffer socketReadBuffer;
    private final ByteBuffer socketWriteBuffer;
    private final ExecutorService executor = Executors.newFixedThreadPool(2, new SecureIOThreadFactory());
    private AtomicBoolean writing = new AtomicBoolean(false);
    private AtomicBoolean reading = new AtomicBoolean(false);

    public AsyncChannelWrapperSecure(AsynchronousSocketChannel socketChannel, SSLEngine sslEngine) {
        this.socketChannel = socketChannel;
        this.sslEngine = sslEngine;
        int socketBufferSize = sslEngine.getSession().getPacketBufferSize();
        this.socketReadBuffer = ByteBuffer.allocateDirect(socketBufferSize);
        this.socketWriteBuffer = ByteBuffer.allocateDirect(socketBufferSize);
    }

    @Override
    public Future<Integer> read(ByteBuffer dst) {
        WrapperFuture future = new WrapperFuture();
        if (!this.reading.compareAndSet(false, true)) {
            throw new IllegalStateException(sm.getString("asyncChannelWrapperSecure.concurrentRead"));
        }
        ReadTask readTask = new ReadTask(dst, future);
        this.executor.execute(readTask);
        return future;
    }

    @Override
    public <B, A extends B> void read(ByteBuffer dst, A attachment, CompletionHandler<Integer, B> handler) {
        WrapperFuture<Integer, B> future = new WrapperFuture<Integer, B>(handler, attachment);
        if (!this.reading.compareAndSet(false, true)) {
            throw new IllegalStateException(sm.getString("asyncChannelWrapperSecure.concurrentRead"));
        }
        ReadTask readTask = new ReadTask(dst, future);
        this.executor.execute(readTask);
    }

    @Override
    public Future<Integer> write(ByteBuffer src) {
        WrapperFuture inner = new WrapperFuture();
        if (!this.writing.compareAndSet(false, true)) {
            throw new IllegalStateException(sm.getString("asyncChannelWrapperSecure.concurrentWrite"));
        }
        WriteTask writeTask = new WriteTask(new ByteBuffer[]{src}, 0, 1, inner);
        this.executor.execute(writeTask);
        LongToIntegerFuture future = new LongToIntegerFuture(inner);
        return future;
    }

    @Override
    public <B, A extends B> void write(ByteBuffer[] srcs, int offset, int length, long timeout, TimeUnit unit, A attachment, CompletionHandler<Long, B> handler) {
        WrapperFuture<Long, B> future = new WrapperFuture<Long, B>(handler, attachment);
        if (!this.writing.compareAndSet(false, true)) {
            throw new IllegalStateException(sm.getString("asyncChannelWrapperSecure.concurrentWrite"));
        }
        WriteTask writeTask = new WriteTask(srcs, offset, length, future);
        this.executor.execute(writeTask);
    }

    @Override
    public void close() {
        try {
            this.socketChannel.close();
        }
        catch (IOException e) {
            this.log.info((Object)sm.getString("asyncChannelWrapperSecure.closeFail"));
        }
        this.executor.shutdownNow();
    }

    @Override
    public Future<Void> handshake() throws SSLException {
        WrapperFuture<Void, Void> wFuture = new WrapperFuture<Void, Void>();
        WebSocketSslHandshakeThread t = new WebSocketSslHandshakeThread(wFuture);
        t.start();
        return wFuture;
    }

    @Override
    public SocketAddress getLocalAddress() throws IOException {
        return this.socketChannel.getLocalAddress();
    }

    static /* synthetic */ AtomicBoolean access$700(AsyncChannelWrapperSecure x0) {
        return x0.reading;
    }

    private static class SecureIOThreadFactory
    implements ThreadFactory {
        private AtomicInteger count = new AtomicInteger(0);

        private SecureIOThreadFactory() {
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("WebSocketClient-SecureIO-" + this.count.incrementAndGet());
            t.setDaemon(true);
            return t;
        }
    }

    private static class WrapperFuture<T, A>
    implements Future<T> {
        private final CompletionHandler<T, A> handler;
        private final A attachment;
        private volatile T result = null;
        private volatile Throwable throwable = null;
        private CountDownLatch completionLatch = new CountDownLatch(1);

        WrapperFuture() {
            this(null, null);
        }

        WrapperFuture(CompletionHandler<T, A> handler, A attachment) {
            this.handler = handler;
            this.attachment = attachment;
        }

        public void complete(T result) {
            this.result = result;
            this.completionLatch.countDown();
            if (this.handler != null) {
                this.handler.completed(result, this.attachment);
            }
        }

        public void fail(Throwable t) {
            this.throwable = t;
            this.completionLatch.countDown();
            if (this.handler != null) {
                this.handler.failed(this.throwable, this.attachment);
            }
        }

        @Override
        public final boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public final boolean isCancelled() {
            return false;
        }

        @Override
        public final boolean isDone() {
            return this.completionLatch.getCount() > 0L;
        }

        @Override
        public T get() throws InterruptedException, ExecutionException {
            this.completionLatch.await();
            if (this.throwable != null) {
                throw new ExecutionException(this.throwable);
            }
            return this.result;
        }

        @Override
        public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            boolean latchResult = this.completionLatch.await(timeout, unit);
            if (!latchResult) {
                throw new TimeoutException();
            }
            if (this.throwable != null) {
                throw new ExecutionException(this.throwable);
            }
            return this.result;
        }
    }

    private class ReadTask
    implements Runnable {
        private final ByteBuffer dest;
        private final WrapperFuture<Integer, ?> future;

        ReadTask(ByteBuffer dest, WrapperFuture<Integer, ?> future) {
            this.dest = dest;
            this.future = future;
        }

        /*
         * Unable to fully structure code
         */
        @Override
        public void run() {
            read = 0;
            forceRead = false;
lbl3:
            // 2 sources

            try {
                while (read == 0) {
                    block15: {
                        AsyncChannelWrapperSecure.access$600(AsyncChannelWrapperSecure.this).compact();
                        if (forceRead) {
                            forceRead = false;
                            f = AsyncChannelWrapperSecure.access$400(AsyncChannelWrapperSecure.this).read(AsyncChannelWrapperSecure.access$600(AsyncChannelWrapperSecure.this));
                            socketRead = f.get();
                            if (socketRead == -1) {
                                throw new EOFException(AsyncChannelWrapperSecure.access$300().getString("asyncChannelWrapperSecure.eof"));
                            }
                        }
                        AsyncChannelWrapperSecure.access$600(AsyncChannelWrapperSecure.this).flip();
                        if (!AsyncChannelWrapperSecure.access$600(AsyncChannelWrapperSecure.this).hasRemaining()) break block15;
                        r = AsyncChannelWrapperSecure.access$200(AsyncChannelWrapperSecure.this).unwrap(AsyncChannelWrapperSecure.access$600(AsyncChannelWrapperSecure.this), this.dest);
                        read += r.bytesProduced();
                        s = r.getStatus();
                        if (s != SSLEngineResult.Status.OK) {
                            if (s == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                                if (read == 0) {
                                    forceRead = true;
                                }
                            } else if (s == SSLEngineResult.Status.BUFFER_OVERFLOW) {
                                if (AsyncChannelWrapperSecure.access$700(AsyncChannelWrapperSecure.this).compareAndSet(true, false)) {
                                    throw new ReadBufferOverflowException(AsyncChannelWrapperSecure.access$200(AsyncChannelWrapperSecure.this).getSession().getApplicationBufferSize());
                                }
                                this.future.fail(new IllegalStateException(AsyncChannelWrapperSecure.access$300().getString("asyncChannelWrapperSecure.wrongStateRead")));
                            } else {
                                throw new IllegalStateException(AsyncChannelWrapperSecure.access$300().getString("asyncChannelWrapperSecure.statusUnwrap"));
                            }
                        }
                        if (r.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NEED_TASK) continue;
                        runnable = AsyncChannelWrapperSecure.access$200(AsyncChannelWrapperSecure.this).getDelegatedTask();
                        while (runnable != null) {
                            runnable.run();
                            runnable = AsyncChannelWrapperSecure.access$200(AsyncChannelWrapperSecure.this).getDelegatedTask();
                        }
                        ** GOTO lbl3
                    }
                    forceRead = true;
                }
                if (AsyncChannelWrapperSecure.access$700(AsyncChannelWrapperSecure.this).compareAndSet(true, false)) {
                    this.future.complete(read);
                } else {
                    this.future.fail(new IllegalStateException(AsyncChannelWrapperSecure.access$300().getString("asyncChannelWrapperSecure.wrongStateRead")));
                }
            }
            catch (EOFException | InterruptedException | RuntimeException | ExecutionException | SSLException | ReadBufferOverflowException e) {
                AsyncChannelWrapperSecure.access$700(AsyncChannelWrapperSecure.this).set(false);
                this.future.fail(e);
            }
        }
    }

    private class WriteTask
    implements Runnable {
        private final ByteBuffer[] srcs;
        private final int offset;
        private final int length;
        private final WrapperFuture<Long, ?> future;

        WriteTask(ByteBuffer[] srcs, int offset, int length, WrapperFuture<Long, ?> future) {
            this.srcs = srcs;
            this.future = future;
            this.offset = offset;
            this.length = length;
        }

        @Override
        public void run() {
            long written = 0L;
            try {
                for (int i = this.offset; i < this.offset + this.length; ++i) {
                    ByteBuffer src = this.srcs[i];
                    while (src.hasRemaining()) {
                        Integer socketWrite;
                        AsyncChannelWrapperSecure.this.socketWriteBuffer.clear();
                        SSLEngineResult r = AsyncChannelWrapperSecure.this.sslEngine.wrap(src, AsyncChannelWrapperSecure.this.socketWriteBuffer);
                        written += (long)r.bytesConsumed();
                        SSLEngineResult.Status s = r.getStatus();
                        if (s != SSLEngineResult.Status.OK && s != SSLEngineResult.Status.BUFFER_OVERFLOW) {
                            throw new IllegalStateException(sm.getString("asyncChannelWrapperSecure.statusWrap"));
                        }
                        if (r.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                            Runnable runnable = AsyncChannelWrapperSecure.this.sslEngine.getDelegatedTask();
                            while (runnable != null) {
                                runnable.run();
                                runnable = AsyncChannelWrapperSecure.this.sslEngine.getDelegatedTask();
                            }
                        }
                        AsyncChannelWrapperSecure.this.socketWriteBuffer.flip();
                        for (int toWrite = r.bytesProduced(); toWrite > 0; toWrite -= socketWrite.intValue()) {
                            Future<Integer> f = AsyncChannelWrapperSecure.this.socketChannel.write(AsyncChannelWrapperSecure.this.socketWriteBuffer);
                            socketWrite = f.get();
                        }
                    }
                }
                if (AsyncChannelWrapperSecure.this.writing.compareAndSet(true, false)) {
                    this.future.complete(written);
                } else {
                    this.future.fail(new IllegalStateException(sm.getString("asyncChannelWrapperSecure.wrongStateWrite")));
                }
            }
            catch (Exception e) {
                AsyncChannelWrapperSecure.this.writing.set(false);
                this.future.fail(e);
            }
        }
    }

    private static final class LongToIntegerFuture
    implements Future<Integer> {
        private final Future<Long> wrapped;

        LongToIntegerFuture(Future<Long> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return this.wrapped.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return this.wrapped.isCancelled();
        }

        @Override
        public boolean isDone() {
            return this.wrapped.isDone();
        }

        @Override
        public Integer get() throws InterruptedException, ExecutionException {
            Long result = this.wrapped.get();
            if (result > Integer.MAX_VALUE) {
                throw new ExecutionException(sm.getString("asyncChannelWrapperSecure.tooBig", new Object[]{result}), null);
            }
            return result.intValue();
        }

        @Override
        public Integer get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            Long result = this.wrapped.get(timeout, unit);
            if (result > Integer.MAX_VALUE) {
                throw new ExecutionException(sm.getString("asyncChannelWrapperSecure.tooBig", new Object[]{result}), null);
            }
            return result.intValue();
        }
    }

    private class WebSocketSslHandshakeThread
    extends Thread {
        private final WrapperFuture<Void, Void> hFuture;
        private SSLEngineResult.HandshakeStatus handshakeStatus;
        private SSLEngineResult.Status resultStatus;

        WebSocketSslHandshakeThread(WrapperFuture<Void, Void> hFuture) {
            this.hFuture = hFuture;
        }

        @Override
        public void run() {
            try {
                AsyncChannelWrapperSecure.this.sslEngine.beginHandshake();
                AsyncChannelWrapperSecure.this.socketReadBuffer.position(AsyncChannelWrapperSecure.this.socketReadBuffer.limit());
                this.handshakeStatus = AsyncChannelWrapperSecure.this.sslEngine.getHandshakeStatus();
                this.resultStatus = SSLEngineResult.Status.OK;
                boolean handshaking = true;
                while (handshaking) {
                    switch (this.handshakeStatus) {
                        case NEED_WRAP: {
                            AsyncChannelWrapperSecure.this.socketWriteBuffer.clear();
                            Object r = AsyncChannelWrapperSecure.this.sslEngine.wrap(DUMMY, AsyncChannelWrapperSecure.this.socketWriteBuffer);
                            this.checkResult((SSLEngineResult)r, true);
                            AsyncChannelWrapperSecure.this.socketWriteBuffer.flip();
                            Future<Integer> fWrite = AsyncChannelWrapperSecure.this.socketChannel.write(AsyncChannelWrapperSecure.this.socketWriteBuffer);
                            fWrite.get();
                            break;
                        }
                        case NEED_UNWRAP: {
                            AsyncChannelWrapperSecure.this.socketReadBuffer.compact();
                            if (AsyncChannelWrapperSecure.this.socketReadBuffer.position() == 0 || this.resultStatus == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                                Future<Integer> fRead = AsyncChannelWrapperSecure.this.socketChannel.read(AsyncChannelWrapperSecure.this.socketReadBuffer);
                                fRead.get();
                            }
                            AsyncChannelWrapperSecure.this.socketReadBuffer.flip();
                            Object r = AsyncChannelWrapperSecure.this.sslEngine.unwrap(AsyncChannelWrapperSecure.this.socketReadBuffer, DUMMY);
                            this.checkResult((SSLEngineResult)r, false);
                            break;
                        }
                        case NEED_TASK: {
                            Object r = null;
                            while ((r = AsyncChannelWrapperSecure.this.sslEngine.getDelegatedTask()) != null) {
                                r.run();
                            }
                            this.handshakeStatus = AsyncChannelWrapperSecure.this.sslEngine.getHandshakeStatus();
                            break;
                        }
                        case FINISHED: {
                            handshaking = false;
                            break;
                        }
                        case NOT_HANDSHAKING: {
                            throw new SSLException(sm.getString("asyncChannelWrapperSecure.notHandshaking"));
                        }
                    }
                }
            }
            catch (Exception e) {
                this.hFuture.fail(e);
                return;
            }
            this.hFuture.complete(null);
        }

        private void checkResult(SSLEngineResult result, boolean wrap) throws SSLException {
            this.handshakeStatus = result.getHandshakeStatus();
            this.resultStatus = result.getStatus();
            if (this.resultStatus != SSLEngineResult.Status.OK && (wrap || this.resultStatus != SSLEngineResult.Status.BUFFER_UNDERFLOW)) {
                throw new SSLException(sm.getString("asyncChannelWrapperSecure.check.notOk", new Object[]{this.resultStatus}));
            }
            if (wrap && result.bytesConsumed() != 0) {
                throw new SSLException(sm.getString("asyncChannelWrapperSecure.check.wrap"));
            }
            if (!wrap && result.bytesProduced() != 0) {
                throw new SSLException(sm.getString("asyncChannelWrapperSecure.check.unwrap"));
            }
        }
    }
}

