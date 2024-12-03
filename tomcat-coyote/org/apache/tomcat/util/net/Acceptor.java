/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.jni.Error
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.net;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jni.Error;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.res.StringManager;

public class Acceptor<U>
implements Runnable {
    private static final Log log = LogFactory.getLog(Acceptor.class);
    private static final StringManager sm = StringManager.getManager(Acceptor.class);
    private static final int INITIAL_ERROR_DELAY = 50;
    private static final int MAX_ERROR_DELAY = 1600;
    private final AbstractEndpoint<?, U> endpoint;
    private String threadName;
    private volatile boolean stopCalled = false;
    private final CountDownLatch stopLatch = new CountDownLatch(1);
    protected volatile AcceptorState state = AcceptorState.NEW;

    public Acceptor(AbstractEndpoint<?, U> endpoint) {
        this.endpoint = endpoint;
    }

    public final AcceptorState getState() {
        return this.state;
    }

    final void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    final String getThreadName() {
        return this.threadName;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void run() {
        int errorDelay = 0;
        long pauseStart = 0L;
        try {
            while (!this.stopCalled) {
                while (this.endpoint.isPaused() && !this.stopCalled) {
                    if (this.state != AcceptorState.PAUSED) {
                        pauseStart = System.nanoTime();
                        this.state = AcceptorState.PAUSED;
                    }
                    if (System.nanoTime() - pauseStart <= 1000000L) continue;
                    try {
                        if (System.nanoTime() - pauseStart > 10000000L) {
                            Thread.sleep(10L);
                            continue;
                        }
                        Thread.sleep(1L);
                    }
                    catch (InterruptedException interruptedException) {}
                }
                if (this.stopCalled) break;
                this.state = AcceptorState.RUNNING;
                try {
                    this.endpoint.countUpOrAwaitConnection();
                    if (this.endpoint.isPaused()) continue;
                    Object socket = null;
                    try {
                        socket = this.endpoint.serverSocketAccept();
                    }
                    catch (Exception ioe) {
                        this.endpoint.countDownConnection();
                        if (this.endpoint.isRunning()) {
                            errorDelay = this.handleExceptionWithDelay(errorDelay);
                            throw ioe;
                        }
                        break;
                    }
                    errorDelay = 0;
                    if (!this.stopCalled && !this.endpoint.isPaused()) {
                        if (this.endpoint.setSocketOptions(socket)) continue;
                        this.endpoint.closeSocket(socket);
                        continue;
                    }
                    this.endpoint.destroySocket(socket);
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    String msg = sm.getString("endpoint.accept.fail");
                    if (t instanceof Error) {
                        Error e = (Error)t;
                        if (e.getError() == 233) {
                            log.warn((Object)msg, t);
                            continue;
                        }
                        log.error((Object)msg, t);
                        continue;
                    }
                    log.error((Object)msg, t);
                }
            }
        }
        finally {
            this.stopLatch.countDown();
        }
        this.state = AcceptorState.ENDED;
    }

    @Deprecated
    public void stop() {
        this.stop(10);
    }

    public void stop(int waitSeconds) {
        this.stopCalled = true;
        if (waitSeconds > 0) {
            try {
                if (!this.stopLatch.await(waitSeconds, TimeUnit.SECONDS)) {
                    log.warn((Object)sm.getString("acceptor.stop.fail", new Object[]{this.getThreadName()}));
                }
            }
            catch (InterruptedException e) {
                log.warn((Object)sm.getString("acceptor.stop.interrupted", new Object[]{this.getThreadName()}), (Throwable)e);
            }
        }
    }

    protected int handleExceptionWithDelay(int currentErrorDelay) {
        if (currentErrorDelay > 0) {
            try {
                Thread.sleep(currentErrorDelay);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
        if (currentErrorDelay == 0) {
            return 50;
        }
        if (currentErrorDelay < 1600) {
            return currentErrorDelay * 2;
        }
        return 1600;
    }

    public static enum AcceptorState {
        NEW,
        RUNNING,
        PAUSED,
        ENDED;

    }
}

