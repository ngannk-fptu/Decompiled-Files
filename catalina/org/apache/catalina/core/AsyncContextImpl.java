/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.AsyncContext
 *  javax.servlet.AsyncEvent
 *  javax.servlet.AsyncListener
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.coyote.ActionCode
 *  org.apache.coyote.AsyncContextCallback
 *  org.apache.coyote.Request
 *  org.apache.coyote.RequestInfo
 *  org.apache.coyote.Response
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.buf.UDecoder
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.core;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.naming.NamingException;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.AsyncDispatcher;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Valve;
import org.apache.catalina.connector.Request;
import org.apache.catalina.core.AsyncListenerWrapper;
import org.apache.catalina.core.StandardHostValve;
import org.apache.coyote.ActionCode;
import org.apache.coyote.AsyncContextCallback;
import org.apache.coyote.RequestInfo;
import org.apache.coyote.Response;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.res.StringManager;

public class AsyncContextImpl
implements AsyncContext,
AsyncContextCallback {
    private static final Log log = LogFactory.getLog(AsyncContextImpl.class);
    protected static final StringManager sm = StringManager.getManager(AsyncContextImpl.class);
    private final Object asyncContextLock = new Object();
    private volatile ServletRequest servletRequest = null;
    private volatile ServletResponse servletResponse = null;
    private final List<AsyncListenerWrapper> listeners = new ArrayList<AsyncListenerWrapper>();
    private boolean hasOriginalRequestAndResponse = true;
    private volatile Runnable dispatch = null;
    private Context context = null;
    private long timeout = -1L;
    private AsyncEvent event = null;
    private volatile Request request;
    AtomicBoolean hasProcessedError = new AtomicBoolean(false);

    public AsyncContextImpl(Request request) {
        if (log.isDebugEnabled()) {
            this.logDebug("Constructor");
        }
        this.request = request;
    }

    public void complete() {
        if (log.isDebugEnabled()) {
            this.logDebug("complete   ");
        }
        this.check();
        this.request.getCoyoteRequest().action(ActionCode.ASYNC_COMPLETE, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void fireOnComplete() {
        if (log.isDebugEnabled()) {
            log.debug((Object)sm.getString("asyncContextImpl.fireOnComplete"));
        }
        ArrayList<AsyncListenerWrapper> listenersCopy = new ArrayList<AsyncListenerWrapper>(this.listeners);
        ClassLoader oldCL = this.context.bind(Globals.IS_SECURITY_ENABLED, null);
        try {
            for (AsyncListenerWrapper listener : listenersCopy) {
                try {
                    listener.fireOnComplete(this.event);
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    log.warn((Object)sm.getString("asyncContextImpl.onCompleteError", new Object[]{listener.getClass().getName()}), t);
                }
            }
        }
        finally {
            this.context.fireRequestDestroyEvent((ServletRequest)this.request.getRequest());
            this.clearServletRequestResponse();
            this.context.unbind(Globals.IS_SECURITY_ENABLED, oldCL);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean timeout() {
        AtomicBoolean result = new AtomicBoolean();
        this.request.getCoyoteRequest().action(ActionCode.ASYNC_TIMEOUT, (Object)result);
        Context context = this.context;
        if (result.get()) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("asyncContextImpl.fireOnTimeout"));
            }
            ClassLoader oldCL = context.bind(false, null);
            try {
                ArrayList<AsyncListenerWrapper> listenersCopy = new ArrayList<AsyncListenerWrapper>(this.listeners);
                for (AsyncListenerWrapper listener : listenersCopy) {
                    try {
                        listener.fireOnTimeout(this.event);
                    }
                    catch (Throwable t) {
                        ExceptionUtils.handleThrowable((Throwable)t);
                        log.warn((Object)sm.getString("asyncContextImpl.onTimeoutError", new Object[]{listener.getClass().getName()}), t);
                    }
                }
                this.request.getCoyoteRequest().action(ActionCode.ASYNC_IS_TIMINGOUT, (Object)result);
            }
            finally {
                context.unbind(false, oldCL);
            }
        }
        return !result.get();
    }

    public void dispatch() {
        String cpath;
        String path;
        this.check();
        ServletRequest servletRequest = this.getRequest();
        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest sr = (HttpServletRequest)servletRequest;
            path = sr.getRequestURI();
            cpath = sr.getContextPath();
        } else {
            path = this.request.getRequestURI();
            cpath = this.request.getContextPath();
        }
        if (cpath.length() > 1) {
            path = path.substring(cpath.length());
        }
        if (!this.context.getDispatchersUseEncodedPaths()) {
            path = UDecoder.URLDecode((String)path, (Charset)StandardCharsets.UTF_8);
        }
        this.dispatch(path);
    }

    public void dispatch(String path) {
        this.check();
        this.dispatch(this.getRequest().getServletContext(), path);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void dispatch(ServletContext servletContext, String path) {
        Object object = this.asyncContextLock;
        synchronized (object) {
            RequestDispatcher requestDispatcher;
            if (log.isDebugEnabled()) {
                this.logDebug("dispatch   ");
            }
            this.check();
            if (this.dispatch != null) {
                throw new IllegalStateException(sm.getString("asyncContextImpl.dispatchingStarted"));
            }
            if (this.request.getAttribute("javax.servlet.async.request_uri") == null) {
                this.request.setAttribute("javax.servlet.async.request_uri", this.request.getRequestURI());
                this.request.setAttribute("javax.servlet.async.context_path", this.request.getContextPath());
                this.request.setAttribute("javax.servlet.async.servlet_path", this.request.getServletPath());
                this.request.setAttribute("javax.servlet.async.path_info", this.request.getPathInfo());
                this.request.setAttribute("javax.servlet.async.query_string", this.request.getQueryString());
            }
            if (!((requestDispatcher = servletContext.getRequestDispatcher(path)) instanceof AsyncDispatcher)) {
                throw new UnsupportedOperationException(sm.getString("asyncContextImpl.noAsyncDispatcher"));
            }
            AsyncDispatcher applicationDispatcher = (AsyncDispatcher)requestDispatcher;
            ServletRequest servletRequest = this.getRequest();
            ServletResponse servletResponse = this.getResponse();
            this.dispatch = new AsyncRunnable(this.request, applicationDispatcher, servletRequest, servletResponse);
            this.request.getCoyoteRequest().action(ActionCode.ASYNC_DISPATCH, null);
            this.clearServletRequestResponse();
        }
    }

    public ServletRequest getRequest() {
        this.check();
        if (this.servletRequest == null) {
            throw new IllegalStateException(sm.getString("asyncContextImpl.request.ise"));
        }
        return this.servletRequest;
    }

    public ServletResponse getResponse() {
        this.check();
        if (this.servletResponse == null) {
            throw new IllegalStateException(sm.getString("asyncContextImpl.response.ise"));
        }
        return this.servletResponse;
    }

    public void start(Runnable run) {
        if (log.isDebugEnabled()) {
            this.logDebug("start      ");
        }
        this.check();
        RunnableWrapper wrapper = new RunnableWrapper(run, this.context, this.request.getCoyoteRequest());
        this.request.getCoyoteRequest().action(ActionCode.ASYNC_RUN, (Object)wrapper);
    }

    public void addListener(AsyncListener listener) {
        this.check();
        AsyncListenerWrapper wrapper = new AsyncListenerWrapper();
        wrapper.setListener(listener);
        this.listeners.add(wrapper);
    }

    public void addListener(AsyncListener listener, ServletRequest servletRequest, ServletResponse servletResponse) {
        this.check();
        AsyncListenerWrapper wrapper = new AsyncListenerWrapper();
        wrapper.setListener(listener);
        wrapper.setServletRequest(servletRequest);
        wrapper.setServletResponse(servletResponse);
        this.listeners.add(wrapper);
    }

    public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException {
        this.check();
        AsyncListener listener = null;
        try {
            listener = (AsyncListener)this.context.getInstanceManager().newInstance(clazz.getName(), clazz.getClassLoader());
        }
        catch (ReflectiveOperationException | NamingException e) {
            ServletException se = new ServletException((Throwable)e);
            throw se;
        }
        catch (Exception e) {
            ExceptionUtils.handleThrowable((Throwable)e.getCause());
            ServletException se = new ServletException((Throwable)e);
            throw se;
        }
        return (T)listener;
    }

    public void recycle() {
        if (log.isDebugEnabled()) {
            this.logDebug("recycle    ");
        }
        this.context = null;
        this.dispatch = null;
        this.event = null;
        this.hasOriginalRequestAndResponse = true;
        this.listeners.clear();
        this.request = null;
        this.clearServletRequestResponse();
        this.timeout = -1L;
        this.hasProcessedError.set(false);
    }

    private void clearServletRequestResponse() {
        this.servletRequest = null;
        this.servletResponse = null;
    }

    public boolean isStarted() {
        AtomicBoolean result = new AtomicBoolean(false);
        this.request.getCoyoteRequest().action(ActionCode.ASYNC_IS_STARTED, (Object)result);
        return result.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setStarted(Context context, ServletRequest request, ServletResponse response, boolean originalRequestResponse) {
        Object object = this.asyncContextLock;
        synchronized (object) {
            this.request.getCoyoteRequest().action(ActionCode.ASYNC_START, (Object)this);
            this.context = context;
            context.incrementInProgressAsyncCount();
            this.servletRequest = request;
            this.servletResponse = response;
            this.hasOriginalRequestAndResponse = originalRequestResponse;
            this.event = new AsyncEvent((AsyncContext)this, request, response);
            ArrayList<AsyncListenerWrapper> listenersCopy = new ArrayList<AsyncListenerWrapper>(this.listeners);
            this.listeners.clear();
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("asyncContextImpl.fireOnStartAsync"));
            }
            for (AsyncListenerWrapper listener : listenersCopy) {
                try {
                    listener.fireOnStartAsync(this.event);
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    log.warn((Object)sm.getString("asyncContextImpl.onStartAsyncError", new Object[]{listener.getClass().getName()}), t);
                }
            }
        }
    }

    public boolean hasOriginalRequestAndResponse() {
        this.check();
        return this.hasOriginalRequestAndResponse;
    }

    protected void doInternalDispatch() throws ServletException, IOException {
        if (log.isDebugEnabled()) {
            this.logDebug("intDispatch");
        }
        try {
            Runnable runnable = this.dispatch;
            this.dispatch = null;
            runnable.run();
            if (!this.request.isAsync()) {
                this.fireOnComplete();
            }
        }
        catch (RuntimeException x) {
            if (x.getCause() instanceof ServletException) {
                throw (ServletException)x.getCause();
            }
            if (x.getCause() instanceof IOException) {
                throw (IOException)x.getCause();
            }
            throw new ServletException((Throwable)x);
        }
    }

    public long getTimeout() {
        this.check();
        return this.timeout;
    }

    public void setTimeout(long timeout) {
        this.check();
        this.timeout = timeout;
        this.request.getCoyoteRequest().action(ActionCode.ASYNC_SETTIMEOUT, (Object)timeout);
    }

    public boolean isAvailable() {
        Context context = this.context;
        if (context == null) {
            return false;
        }
        return context.getState().isAvailable();
    }

    public void setErrorState(Throwable t, boolean fireOnError) {
        if (!this.hasProcessedError.compareAndSet(false, true)) {
            return;
        }
        if (t != null) {
            this.request.setAttribute("javax.servlet.error.exception", t);
        }
        this.request.getCoyoteRequest().action(ActionCode.ASYNC_ERROR, null);
        if (fireOnError) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("asyncContextImpl.fireOnError"));
            }
            AsyncEvent errorEvent = new AsyncEvent(this.event.getAsyncContext(), this.event.getSuppliedRequest(), this.event.getSuppliedResponse(), t);
            ArrayList<AsyncListenerWrapper> listenersCopy = new ArrayList<AsyncListenerWrapper>(this.listeners);
            for (AsyncListenerWrapper listener : listenersCopy) {
                try {
                    listener.fireOnError(errorEvent);
                }
                catch (Throwable t2) {
                    ExceptionUtils.handleThrowable((Throwable)t2);
                    log.warn((Object)sm.getString("asyncContextImpl.onErrorError", new Object[]{listener.getClass().getName()}), t2);
                }
            }
        }
        AtomicBoolean result = new AtomicBoolean();
        this.request.getCoyoteRequest().action(ActionCode.ASYNC_IS_ERROR, (Object)result);
        if (result.get()) {
            Host host;
            Valve stdHostValve;
            ServletResponse servletResponse = this.servletResponse;
            if (servletResponse instanceof HttpServletResponse) {
                ((HttpServletResponse)servletResponse).setStatus(500);
            }
            if ((stdHostValve = (host = (Host)this.context.getParent()).getPipeline().getBasic()) instanceof StandardHostValve) {
                ((StandardHostValve)stdHostValve).throwable(this.request, this.request.getResponse(), t);
            }
            this.request.getCoyoteRequest().action(ActionCode.ASYNC_IS_ERROR, (Object)result);
            if (result.get()) {
                this.complete();
            }
        }
    }

    public void incrementInProgressAsyncCount() {
        this.context.incrementInProgressAsyncCount();
    }

    public void decrementInProgressAsyncCount() {
        this.context.decrementInProgressAsyncCount();
    }

    private void logDebug(String method) {
        String stage;
        String rpHashCode;
        String crHashCode;
        String rHashCode;
        StringBuilder uri = new StringBuilder();
        if (this.request == null) {
            rHashCode = "null";
            crHashCode = "null";
            rpHashCode = "null";
            stage = "-";
            uri.append("N/A");
        } else {
            rHashCode = Integer.toHexString(this.request.hashCode());
            org.apache.coyote.Request coyoteRequest = this.request.getCoyoteRequest();
            if (coyoteRequest == null) {
                crHashCode = "null";
                rpHashCode = "null";
                stage = "-";
            } else {
                crHashCode = Integer.toHexString(coyoteRequest.hashCode());
                RequestInfo rp = coyoteRequest.getRequestProcessor();
                if (rp == null) {
                    rpHashCode = "null";
                    stage = "-";
                } else {
                    rpHashCode = Integer.toHexString(rp.hashCode());
                    stage = Integer.toString(rp.getStage());
                }
            }
            uri.append(this.request.getRequestURI());
            if (this.request.getQueryString() != null) {
                uri.append('?');
                uri.append(this.request.getQueryString());
            }
        }
        String threadName = Thread.currentThread().getName();
        int len = threadName.length();
        if (len > 20) {
            threadName = threadName.substring(len - 20, len);
        }
        String msg = String.format("Req: %1$8s  CReq: %2$8s  RP: %3$8s  Stage: %4$s  Thread: %5$20s  State: %6$20s  Method: %7$11s  URI: %8$s", rHashCode, crHashCode, rpHashCode, stage, threadName, "N/A", method, uri);
        if (log.isTraceEnabled()) {
            log.trace((Object)msg, (Throwable)new DebugException());
        } else {
            log.debug((Object)msg);
        }
    }

    private void check() {
        if (this.request == null) {
            throw new IllegalStateException(sm.getString("asyncContextImpl.requestEnded"));
        }
    }

    private static class AsyncRunnable
    implements Runnable {
        private final AsyncDispatcher applicationDispatcher;
        private final Request request;
        private final ServletRequest servletRequest;
        private final ServletResponse servletResponse;

        AsyncRunnable(Request request, AsyncDispatcher applicationDispatcher, ServletRequest servletRequest, ServletResponse servletResponse) {
            this.request = request;
            this.applicationDispatcher = applicationDispatcher;
            this.servletRequest = servletRequest;
            this.servletResponse = servletResponse;
        }

        @Override
        public void run() {
            this.request.getCoyoteRequest().action(ActionCode.ASYNC_DISPATCHED, null);
            try {
                this.applicationDispatcher.dispatch(this.servletRequest, this.servletResponse);
            }
            catch (Exception e) {
                throw new RuntimeException(sm.getString("asyncContextImpl.asyncDispatchError"), e);
            }
        }
    }

    private static class RunnableWrapper
    implements Runnable {
        private final Runnable wrapped;
        private final Context context;
        private final org.apache.coyote.Request coyoteRequest;

        RunnableWrapper(Runnable wrapped, Context ctxt, org.apache.coyote.Request coyoteRequest) {
            this.wrapped = wrapped;
            this.context = ctxt;
            this.coyoteRequest = coyoteRequest;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            ClassLoader oldCL = this.context.bind(Globals.IS_SECURITY_ENABLED, null);
            try {
                this.wrapped.run();
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable((Throwable)t);
                this.context.getLogger().error((Object)sm.getString("asyncContextImpl.asyncRunnableError"), t);
                this.coyoteRequest.setAttribute("javax.servlet.error.exception", (Object)t);
                Response coyoteResponse = this.coyoteRequest.getResponse();
                coyoteResponse.setStatus(500);
                coyoteResponse.setError();
            }
            finally {
                this.context.unbind(Globals.IS_SECURITY_ENABLED, oldCL);
            }
            this.coyoteRequest.action(ActionCode.DISPATCH_EXECUTE, null);
        }
    }

    private static class DebugException
    extends Exception {
        private static final long serialVersionUID = 1L;

        private DebugException() {
        }
    }
}

