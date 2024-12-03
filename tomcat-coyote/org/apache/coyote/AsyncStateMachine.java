/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 *  org.apache.tomcat.util.security.PrivilegedGetTccl
 *  org.apache.tomcat.util.security.PrivilegedSetTccl
 */
package org.apache.coyote;

import java.security.AccessController;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.coyote.AbstractProcessor;
import org.apache.coyote.AsyncContextCallback;
import org.apache.coyote.Constants;
import org.apache.coyote.Request;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.AbstractEndpoint;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.PrivilegedGetTccl;
import org.apache.tomcat.util.security.PrivilegedSetTccl;

class AsyncStateMachine {
    private static final Log log = LogFactory.getLog(AsyncStateMachine.class);
    private static final StringManager sm = StringManager.getManager(AsyncStateMachine.class);
    private volatile AsyncState state = AsyncState.DISPATCHED;
    private volatile long lastAsyncStart = 0L;
    private final AtomicLong generation = new AtomicLong(0L);
    private boolean hasProcessedError = false;
    private AsyncContextCallback asyncCtxt = null;
    private final AbstractProcessor processor;

    AsyncStateMachine(AbstractProcessor processor) {
        this.processor = processor;
    }

    boolean isAsync() {
        return this.state.isAsync();
    }

    boolean isAsyncDispatching() {
        return this.state.isDispatching();
    }

    boolean isAsyncStarted() {
        return this.state.isStarted();
    }

    boolean isAsyncTimingOut() {
        return this.state == AsyncState.TIMING_OUT;
    }

    boolean isAsyncError() {
        return this.state == AsyncState.ERROR;
    }

    boolean isCompleting() {
        return this.state.isCompleting();
    }

    long getLastAsyncStart() {
        return this.lastAsyncStart;
    }

    long getCurrentGeneration() {
        return this.generation.get();
    }

    synchronized void asyncStart(AsyncContextCallback asyncCtxt) {
        if (this.state != AsyncState.DISPATCHED) {
            throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", new Object[]{"asyncStart()", this.state}));
        }
        this.generation.incrementAndGet();
        this.updateState(AsyncState.STARTING);
        this.asyncCtxt = asyncCtxt;
        this.lastAsyncStart = System.currentTimeMillis();
    }

    synchronized void asyncOperation() {
        if (this.state != AsyncState.STARTED) {
            throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", new Object[]{"asyncOperation()", this.state}));
        }
        this.updateState(AsyncState.READ_WRITE_OP);
    }

    synchronized AbstractEndpoint.Handler.SocketState asyncPostProcess() {
        if (this.state == AsyncState.COMPLETE_PENDING) {
            this.clearNonBlockingListeners();
            this.updateState(AsyncState.COMPLETING);
            return AbstractEndpoint.Handler.SocketState.ASYNC_END;
        }
        if (this.state == AsyncState.DISPATCH_PENDING) {
            this.clearNonBlockingListeners();
            this.updateState(AsyncState.DISPATCHING);
            return AbstractEndpoint.Handler.SocketState.ASYNC_END;
        }
        if (this.state == AsyncState.STARTING || this.state == AsyncState.READ_WRITE_OP) {
            this.updateState(AsyncState.STARTED);
            return AbstractEndpoint.Handler.SocketState.LONG;
        }
        if (this.state == AsyncState.MUST_COMPLETE || this.state == AsyncState.COMPLETING) {
            this.asyncCtxt.fireOnComplete();
            this.updateState(AsyncState.DISPATCHED);
            this.asyncCtxt.decrementInProgressAsyncCount();
            return AbstractEndpoint.Handler.SocketState.ASYNC_END;
        }
        if (this.state == AsyncState.MUST_DISPATCH) {
            this.updateState(AsyncState.DISPATCHING);
            return AbstractEndpoint.Handler.SocketState.ASYNC_END;
        }
        if (this.state == AsyncState.DISPATCHING) {
            this.updateState(AsyncState.DISPATCHED);
            this.asyncCtxt.decrementInProgressAsyncCount();
            return AbstractEndpoint.Handler.SocketState.ASYNC_END;
        }
        if (this.state == AsyncState.STARTED) {
            return AbstractEndpoint.Handler.SocketState.LONG;
        }
        throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", new Object[]{"asyncPostProcess()", this.state}));
    }

    synchronized boolean asyncComplete() {
        Request request = this.processor.getRequest();
        if (!(request != null && request.isRequestThread() || this.state != AsyncState.STARTING && this.state != AsyncState.READ_WRITE_OP)) {
            this.updateState(AsyncState.COMPLETE_PENDING);
            return false;
        }
        this.clearNonBlockingListeners();
        boolean triggerDispatch = false;
        if (this.state == AsyncState.STARTING || this.state == AsyncState.MUST_ERROR) {
            this.updateState(AsyncState.MUST_COMPLETE);
        } else if (this.state == AsyncState.STARTED) {
            this.updateState(AsyncState.COMPLETING);
            triggerDispatch = true;
        } else if (this.state == AsyncState.READ_WRITE_OP || this.state == AsyncState.TIMING_OUT || this.state == AsyncState.ERROR) {
            this.updateState(AsyncState.COMPLETING);
        } else {
            throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", new Object[]{"asyncComplete()", this.state}));
        }
        return triggerDispatch;
    }

    synchronized boolean asyncTimeout() {
        if (this.state == AsyncState.STARTED) {
            this.updateState(AsyncState.TIMING_OUT);
            return true;
        }
        if (this.state == AsyncState.COMPLETING || this.state == AsyncState.DISPATCHING || this.state == AsyncState.DISPATCHED) {
            return false;
        }
        throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", new Object[]{"asyncTimeout()", this.state}));
    }

    synchronized boolean asyncDispatch() {
        Request request = this.processor.getRequest();
        if (!(request != null && request.isRequestThread() || this.state != AsyncState.STARTING && this.state != AsyncState.READ_WRITE_OP)) {
            this.updateState(AsyncState.DISPATCH_PENDING);
            return false;
        }
        this.clearNonBlockingListeners();
        boolean triggerDispatch = false;
        if (this.state == AsyncState.STARTING || this.state == AsyncState.MUST_ERROR) {
            this.updateState(AsyncState.MUST_DISPATCH);
        } else if (this.state == AsyncState.STARTED) {
            this.updateState(AsyncState.DISPATCHING);
            triggerDispatch = true;
        } else if (this.state == AsyncState.READ_WRITE_OP || this.state == AsyncState.TIMING_OUT || this.state == AsyncState.ERROR) {
            this.updateState(AsyncState.DISPATCHING);
        } else {
            throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", new Object[]{"asyncDispatch()", this.state}));
        }
        return triggerDispatch;
    }

    synchronized void asyncDispatched() {
        if (this.state != AsyncState.DISPATCHING && this.state != AsyncState.MUST_DISPATCH) {
            throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", new Object[]{"asyncDispatched()", this.state}));
        }
        this.updateState(AsyncState.DISPATCHED);
        this.asyncCtxt.decrementInProgressAsyncCount();
    }

    synchronized boolean asyncError() {
        boolean containerThread;
        Request request = this.processor.getRequest();
        boolean bl = containerThread = request != null && request.isRequestThread();
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("asyncStateMachine.asyncError.start"));
        }
        this.clearNonBlockingListeners();
        if (this.state == AsyncState.STARTING) {
            this.updateState(AsyncState.MUST_ERROR);
        } else {
            if (this.hasProcessedError) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("asyncStateMachine.asyncError.skip"));
                }
                return false;
            }
            this.hasProcessedError = true;
            if (this.state == AsyncState.DISPATCHED) {
                this.asyncCtxt.incrementInProgressAsyncCount();
                this.updateState(AsyncState.ERROR);
            } else {
                this.updateState(AsyncState.ERROR);
            }
        }
        return !containerThread;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    synchronized void asyncRun(Runnable runnable) {
        if (this.state == AsyncState.STARTING || this.state == AsyncState.STARTED || this.state == AsyncState.READ_WRITE_OP) {
            ClassLoader oldCL;
            PrivilegedGetTccl pa;
            Thread currentThread = Thread.currentThread();
            if (Constants.IS_SECURITY_ENABLED) {
                pa = new PrivilegedGetTccl(currentThread);
                oldCL = (ClassLoader)AccessController.doPrivileged(pa);
            } else {
                oldCL = currentThread.getContextClassLoader();
            }
            try {
                if (Constants.IS_SECURITY_ENABLED) {
                    pa = new PrivilegedSetTccl(currentThread, this.getClass().getClassLoader());
                    AccessController.doPrivileged(pa);
                } else {
                    currentThread.setContextClassLoader(this.getClass().getClassLoader());
                }
                this.processor.execute(runnable);
            }
            finally {
                if (Constants.IS_SECURITY_ENABLED) {
                    pa = new PrivilegedSetTccl(currentThread, oldCL);
                    AccessController.doPrivileged(pa);
                } else {
                    currentThread.setContextClassLoader(oldCL);
                }
            }
        } else {
            throw new IllegalStateException(sm.getString("asyncStateMachine.invalidAsyncState", new Object[]{"asyncRun()", this.state}));
        }
    }

    synchronized boolean isAvailable() {
        if (this.asyncCtxt == null) {
            return false;
        }
        return this.asyncCtxt.isAvailable();
    }

    synchronized void recycle() {
        if (this.lastAsyncStart == 0L) {
            return;
        }
        this.notifyAll();
        this.asyncCtxt = null;
        this.state = AsyncState.DISPATCHED;
        this.lastAsyncStart = 0L;
        this.hasProcessedError = false;
    }

    private void clearNonBlockingListeners() {
        this.processor.getRequest().listener = null;
        this.processor.getRequest().getResponse().listener = null;
    }

    private synchronized void updateState(AsyncState newState) {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("asyncStateMachine.stateChange", new Object[]{this.state, newState}));
        }
        this.state = newState;
    }

    private static enum AsyncState {
        DISPATCHED(false, false, false, false),
        STARTING(true, true, false, false),
        STARTED(true, true, false, false),
        MUST_COMPLETE(true, true, true, false),
        COMPLETE_PENDING(true, true, false, false),
        COMPLETING(true, false, true, false),
        TIMING_OUT(true, true, false, false),
        MUST_DISPATCH(true, true, false, true),
        DISPATCH_PENDING(true, true, false, false),
        DISPATCHING(true, false, false, true),
        READ_WRITE_OP(true, true, false, false),
        MUST_ERROR(true, true, false, false),
        ERROR(true, true, false, false);

        private final boolean isAsync;
        private final boolean isStarted;
        private final boolean isCompleting;
        private final boolean isDispatching;

        private AsyncState(boolean isAsync, boolean isStarted, boolean isCompleting, boolean isDispatching) {
            this.isAsync = isAsync;
            this.isStarted = isStarted;
            this.isCompleting = isCompleting;
            this.isDispatching = isDispatching;
        }

        boolean isAsync() {
            return this.isAsync;
        }

        boolean isStarted() {
            return this.isStarted;
        }

        boolean isDispatching() {
            return this.isDispatching;
        }

        boolean isCompleting() {
            return this.isCompleting;
        }
    }
}

