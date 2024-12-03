/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.net.SocketBufferHandler;
import org.apache.tomcat.util.net.SocketWrapperBase;

public class Nio2Channel
implements AsynchronousByteChannel {
    protected static final ByteBuffer emptyBuf = ByteBuffer.allocate(0);
    protected final SocketBufferHandler bufHandler;
    protected AsynchronousSocketChannel sc = null;
    protected SocketWrapperBase<Nio2Channel> socketWrapper = null;
    private static final Future<Boolean> DONE = new Future<Boolean>(){

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public Boolean get() throws InterruptedException, ExecutionException {
            return Boolean.TRUE;
        }

        @Override
        public Boolean get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return Boolean.TRUE;
        }
    };
    private ApplicationBufferHandler appReadBufHandler;
    private static final Future<Integer> DONE_INT = new Future<Integer>(){

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return true;
        }

        @Override
        public Integer get() throws InterruptedException, ExecutionException {
            return -1;
        }

        @Override
        public Integer get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return -1;
        }
    };
    static final Nio2Channel CLOSED_NIO2_CHANNEL = new Nio2Channel(SocketBufferHandler.EMPTY){

        @Override
        public void close() throws IOException {
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public void reset(AsynchronousSocketChannel channel, SocketWrapperBase<Nio2Channel> socket) throws IOException {
        }

        @Override
        public void free() {
        }

        @Override
        protected ApplicationBufferHandler getAppReadBufHandler() {
            return ApplicationBufferHandler.EMPTY;
        }

        @Override
        public void setAppReadBufHandler(ApplicationBufferHandler handler) {
        }

        @Override
        public Future<Integer> read(ByteBuffer dst) {
            return DONE_INT;
        }

        @Override
        public <A> void read(ByteBuffer dst, long timeout, TimeUnit unit, A attachment, CompletionHandler<Integer, ? super A> handler) {
            handler.failed(new ClosedChannelException(), attachment);
        }

        @Override
        public <A> void read(ByteBuffer[] dsts, int offset, int length, long timeout, TimeUnit unit, A attachment, CompletionHandler<Long, ? super A> handler) {
            handler.failed(new ClosedChannelException(), attachment);
        }

        @Override
        public Future<Integer> write(ByteBuffer src) {
            return DONE_INT;
        }

        @Override
        public <A> void write(ByteBuffer src, long timeout, TimeUnit unit, A attachment, CompletionHandler<Integer, ? super A> handler) {
            handler.failed(new ClosedChannelException(), attachment);
        }

        @Override
        public <A> void write(ByteBuffer[] srcs, int offset, int length, long timeout, TimeUnit unit, A attachment, CompletionHandler<Long, ? super A> handler) {
            handler.failed(new ClosedChannelException(), attachment);
        }

        @Override
        public String toString() {
            return "Closed Nio2Channel";
        }
    };

    public Nio2Channel(SocketBufferHandler bufHandler) {
        this.bufHandler = bufHandler;
    }

    public void reset(AsynchronousSocketChannel channel, SocketWrapperBase<Nio2Channel> socketWrapper) throws IOException {
        this.sc = channel;
        this.socketWrapper = socketWrapper;
        this.bufHandler.reset();
    }

    public void free() {
        this.bufHandler.free();
    }

    SocketWrapperBase<Nio2Channel> getSocketWrapper() {
        return this.socketWrapper;
    }

    @Override
    public void close() throws IOException {
        this.sc.close();
    }

    public void close(boolean force) throws IOException {
        if (this.isOpen() || force) {
            this.close();
        }
    }

    @Override
    public boolean isOpen() {
        return this.sc.isOpen();
    }

    public SocketBufferHandler getBufHandler() {
        return this.bufHandler;
    }

    public AsynchronousSocketChannel getIOChannel() {
        return this.sc;
    }

    public boolean isClosing() {
        return false;
    }

    public boolean isHandshakeComplete() {
        return true;
    }

    public int handshake() throws IOException {
        return 0;
    }

    public String toString() {
        return super.toString() + ":" + this.sc;
    }

    @Override
    public Future<Integer> read(ByteBuffer dst) {
        return this.sc.read(dst);
    }

    @Override
    public <A> void read(ByteBuffer dst, A attachment, CompletionHandler<Integer, ? super A> handler) {
        this.read(dst, 0L, TimeUnit.MILLISECONDS, attachment, handler);
    }

    public <A> void read(ByteBuffer dst, long timeout, TimeUnit unit, A attachment, CompletionHandler<Integer, ? super A> handler) {
        this.sc.read(dst, timeout, unit, attachment, handler);
    }

    public <A> void read(ByteBuffer[] dsts, int offset, int length, long timeout, TimeUnit unit, A attachment, CompletionHandler<Long, ? super A> handler) {
        this.sc.read(dsts, offset, length, timeout, unit, attachment, handler);
    }

    @Override
    public Future<Integer> write(ByteBuffer src) {
        return this.sc.write(src);
    }

    @Override
    public <A> void write(ByteBuffer src, A attachment, CompletionHandler<Integer, ? super A> handler) {
        this.write(src, 0L, TimeUnit.MILLISECONDS, attachment, handler);
    }

    public <A> void write(ByteBuffer src, long timeout, TimeUnit unit, A attachment, CompletionHandler<Integer, ? super A> handler) {
        this.sc.write(src, timeout, unit, attachment, handler);
    }

    public <A> void write(ByteBuffer[] srcs, int offset, int length, long timeout, TimeUnit unit, A attachment, CompletionHandler<Long, ? super A> handler) {
        this.sc.write(srcs, offset, length, timeout, unit, attachment, handler);
    }

    public Future<Boolean> flush() {
        return DONE;
    }

    public void setAppReadBufHandler(ApplicationBufferHandler handler) {
        this.appReadBufHandler = handler;
    }

    protected ApplicationBufferHandler getAppReadBufHandler() {
        return this.appReadBufHandler;
    }
}

