/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.bootstrap;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.http.ExceptionLogger;
import org.apache.http.impl.nio.DefaultHttpServerIODispatch;
import org.apache.http.impl.nio.DefaultNHttpServerConnection;
import org.apache.http.impl.nio.bootstrap.ThreadFactoryImpl;
import org.apache.http.impl.nio.reactor.DefaultListeningIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.NHttpConnectionFactory;
import org.apache.http.nio.NHttpServerEventHandler;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.nio.reactor.IOReactorExceptionHandler;
import org.apache.http.nio.reactor.ListenerEndpoint;

public class HttpServer {
    private final int port;
    private final InetAddress ifAddress;
    private final IOReactorConfig ioReactorConfig;
    private final NHttpServerEventHandler serverEventHandler;
    private final NHttpConnectionFactory<? extends DefaultNHttpServerConnection> connectionFactory;
    private final ExceptionLogger exceptionLogger;
    private final ExecutorService listenerExecutorService;
    private final ThreadGroup dispatchThreads;
    private final AtomicReference<Status> status;
    private final DefaultListeningIOReactor ioReactor;
    private volatile ListenerEndpoint endpoint;

    HttpServer(int port, InetAddress ifAddress, IOReactorConfig ioReactorConfig, NHttpServerEventHandler serverEventHandler, NHttpConnectionFactory<? extends DefaultNHttpServerConnection> connectionFactory, final ExceptionLogger exceptionLogger) {
        this.port = port;
        this.ifAddress = ifAddress;
        this.ioReactorConfig = ioReactorConfig;
        this.serverEventHandler = serverEventHandler;
        this.connectionFactory = connectionFactory;
        this.exceptionLogger = exceptionLogger;
        this.listenerExecutorService = Executors.newSingleThreadExecutor(new ThreadFactoryImpl("HTTP-listener-" + this.port));
        this.dispatchThreads = new ThreadGroup("I/O-dispatchers");
        try {
            this.ioReactor = new DefaultListeningIOReactor(this.ioReactorConfig, new ThreadFactoryImpl("I/O-dispatch", this.dispatchThreads));
        }
        catch (IOReactorException ex) {
            throw new IllegalStateException(ex);
        }
        this.ioReactor.setExceptionHandler(new IOReactorExceptionHandler(){

            @Override
            public boolean handle(IOException ex) {
                exceptionLogger.log(ex);
                return false;
            }

            @Override
            public boolean handle(RuntimeException ex) {
                exceptionLogger.log(ex);
                return false;
            }
        });
        this.status = new AtomicReference<Status>(Status.READY);
    }

    public ListenerEndpoint getEndpoint() {
        return this.endpoint;
    }

    public void start() throws IOException {
        if (this.status.compareAndSet(Status.READY, Status.ACTIVE)) {
            this.endpoint = this.ioReactor.listen(new InetSocketAddress(this.ifAddress, this.port > 0 ? this.port : 0));
            final DefaultHttpServerIODispatch<NHttpServerEventHandler> ioEventDispatch = new DefaultHttpServerIODispatch<NHttpServerEventHandler>(this.serverEventHandler, this.connectionFactory);
            this.listenerExecutorService.execute(new Runnable(){

                @Override
                public void run() {
                    try {
                        HttpServer.this.ioReactor.execute(ioEventDispatch);
                    }
                    catch (Exception ex) {
                        HttpServer.this.exceptionLogger.log(ex);
                    }
                }
            });
        }
    }

    public void awaitTermination(long timeout, TimeUnit timeUnit) throws InterruptedException {
        this.listenerExecutorService.awaitTermination(timeout, timeUnit);
    }

    public void shutdown(long gracePeriod, TimeUnit timeUnit) {
        if (this.status.compareAndSet(Status.ACTIVE, Status.STOPPING)) {
            try {
                this.ioReactor.shutdown(timeUnit.toMillis(gracePeriod));
            }
            catch (IOException ex) {
                this.exceptionLogger.log(ex);
            }
            this.listenerExecutorService.shutdown();
        }
    }

    static enum Status {
        READY,
        ACTIVE,
        STOPPING;

    }
}

