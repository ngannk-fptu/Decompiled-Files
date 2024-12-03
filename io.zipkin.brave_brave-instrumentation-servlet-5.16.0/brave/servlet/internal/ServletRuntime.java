/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  brave.Span
 *  brave.http.HttpServerHandler
 *  brave.http.HttpServerRequest
 *  brave.http.HttpServerResponse
 *  brave.internal.Throwables
 *  javax.servlet.AsyncContext
 *  javax.servlet.AsyncEvent
 *  javax.servlet.AsyncListener
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 */
package brave.servlet.internal;

import brave.Span;
import brave.http.HttpServerHandler;
import brave.http.HttpServerRequest;
import brave.http.HttpServerResponse;
import brave.internal.Throwables;
import brave.servlet.HttpServletResponseWrapper;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class ServletRuntime {
    private static final ServletRuntime SERVLET_RUNTIME = ServletRuntime.findServletRuntime();

    public HttpServletResponse httpServletResponse(ServletResponse response) {
        return (HttpServletResponse)response;
    }

    public abstract int status(HttpServletResponse var1);

    public abstract boolean isAsync(HttpServletRequest var1);

    public abstract void handleAsync(HttpServerHandler<HttpServerRequest, HttpServerResponse> var1, HttpServletRequest var2, HttpServletResponse var3, Span var4);

    ServletRuntime() {
    }

    public static ServletRuntime get() {
        return SERVLET_RUNTIME;
    }

    private static ServletRuntime findServletRuntime() {
        try {
            Class.forName("javax.servlet.AsyncEvent");
            HttpServletRequest.class.getMethod("isAsyncStarted", new Class[0]);
            return new Servlet3();
        }
        catch (NoSuchMethodException noSuchMethodException) {
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        return new Servlet25();
    }

    static final class Servlet25ServerResponseAdapter
    extends javax.servlet.http.HttpServletResponseWrapper {
        int httpStatus = 200;

        Servlet25ServerResponseAdapter(ServletResponse response) {
            super((HttpServletResponse)response);
        }

        public void setStatus(int sc, String sm) {
            this.httpStatus = sc;
            super.setStatus(sc, sm);
        }

        public void sendError(int sc) throws IOException {
            this.httpStatus = sc;
            super.sendError(sc);
        }

        public void sendError(int sc, String msg) throws IOException {
            this.httpStatus = sc;
            super.sendError(sc, msg);
        }

        public void setStatus(int sc) {
            this.httpStatus = sc;
            super.setStatus(sc);
        }

        int getStatusInServlet25() {
            return this.httpStatus;
        }
    }

    static final class Servlet25
    extends ServletRuntime {
        final AtomicReference<Map<Class<?>, Object>> classToGetStatus = new AtomicReference(new LinkedHashMap());
        static final String RETURN_NULL = "RETURN_NULL";

        Servlet25() {
        }

        @Override
        public HttpServletResponse httpServletResponse(ServletResponse response) {
            return new Servlet25ServerResponseAdapter(response);
        }

        @Override
        public boolean isAsync(HttpServletRequest request) {
            return false;
        }

        @Override
        public void handleAsync(HttpServerHandler<HttpServerRequest, HttpServerResponse> handler, HttpServletRequest request, HttpServletResponse response, Span span) {
            assert (false) : "this should never be called in Servlet 2.5";
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int status(HttpServletResponse response) {
            if (response instanceof Servlet25ServerResponseAdapter) {
                return ((Servlet25ServerResponseAdapter)response).getStatusInServlet25();
            }
            Class<?> clazz = response.getClass();
            Map<Class<?>, Object> classesToCheck = this.classToGetStatus.get();
            Object getStatusMethod = classesToCheck.get(clazz);
            if (getStatusMethod == RETURN_NULL || getStatusMethod == null && classesToCheck.size() == 10) {
                return 0;
            }
            if (getStatusMethod == null) {
                if (clazz.isLocalClass() || clazz.isAnonymousClass()) {
                    return 0;
                }
                try {
                    getStatusMethod = clazz.getMethod("getStatus", new Class[0]);
                    int n = (Integer)((Method)getStatusMethod).invoke((Object)response, new Object[0]);
                    return n;
                }
                catch (Throwable throwable) {
                    Throwables.propagateIfFatal((Throwable)throwable);
                    getStatusMethod = RETURN_NULL;
                    int replacement = 0;
                    return replacement;
                }
                finally {
                    LinkedHashMap replacement = new LinkedHashMap(classesToCheck);
                    replacement.put(clazz, getStatusMethod);
                    this.classToGetStatus.set(replacement);
                }
            }
            try {
                return (Integer)((Method)getStatusMethod).invoke((Object)response, new Object[0]);
            }
            catch (Throwable throwable) {
                Throwables.propagateIfFatal((Throwable)throwable);
                LinkedHashMap replacement = new LinkedHashMap(classesToCheck);
                replacement.put(clazz, RETURN_NULL);
                this.classToGetStatus.set(replacement);
                return 0;
            }
        }
    }

    static final class Servlet3
    extends ServletRuntime {
        Servlet3() {
        }

        @Override
        public boolean isAsync(HttpServletRequest request) {
            return request.isAsyncStarted();
        }

        @Override
        public int status(HttpServletResponse response) {
            return response.getStatus();
        }

        @Override
        public void handleAsync(HttpServerHandler<HttpServerRequest, HttpServerResponse> handler, HttpServletRequest request, HttpServletResponse response, Span span) {
            if (span.isNoop()) {
                return;
            }
            TracingAsyncListener listener = new TracingAsyncListener(handler, span);
            request.getAsyncContext().addListener((AsyncListener)listener, (ServletRequest)request, (ServletResponse)response);
        }

        static final class AsyncTimeoutException
        extends TimeoutException {
            AsyncTimeoutException(AsyncEvent e) {
                super("Timed out after " + e.getAsyncContext().getTimeout() + "ms");
            }

            @Override
            public Throwable fillInStackTrace() {
                return this;
            }
        }

        static final class TracingAsyncListener
        implements AsyncListener {
            final HttpServerHandler<HttpServerRequest, HttpServerResponse> handler;
            final Span span;

            TracingAsyncListener(HttpServerHandler<HttpServerRequest, HttpServerResponse> handler, Span span) {
                this.handler = handler;
                this.span = span;
            }

            public void onComplete(AsyncEvent e) {
                HttpServletRequest req = (HttpServletRequest)e.getSuppliedRequest();
                Object sendHandled = req.getAttribute("brave.servlet.TracingFilter$SendHandled");
                if (sendHandled instanceof AtomicBoolean && ((AtomicBoolean)sendHandled).compareAndSet(false, true)) {
                    HttpServletResponse res = (HttpServletResponse)e.getSuppliedResponse();
                    HttpServerResponse response = HttpServletResponseWrapper.create(req, res, e.getThrowable());
                    this.handler.handleSend(response, this.span);
                }
            }

            public void onTimeout(AsyncEvent e) {
                ServletRequest request = e.getSuppliedRequest();
                if (request.getAttribute("error") == null) {
                    request.setAttribute("error", (Object)new AsyncTimeoutException(e));
                }
            }

            public void onError(AsyncEvent e) {
                ServletRequest request = e.getSuppliedRequest();
                if (request.getAttribute("error") == null) {
                    request.setAttribute("error", (Object)e.getThrowable());
                }
            }

            public void onStartAsync(AsyncEvent e) {
                AsyncContext eventAsyncContext = e.getAsyncContext();
                if (eventAsyncContext != null) {
                    eventAsyncContext.addListener((AsyncListener)this, e.getSuppliedRequest(), e.getSuppliedResponse());
                }
            }

            public String toString() {
                return "TracingAsyncListener{" + this.span + "}";
            }
        }
    }
}

