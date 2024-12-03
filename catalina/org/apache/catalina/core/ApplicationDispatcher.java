/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.DispatcherType
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.Servlet
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletRequestWrapper
 *  javax.servlet.ServletResponse
 *  javax.servlet.ServletResponseWrapper
 *  javax.servlet.UnavailableException
 *  javax.servlet.http.HttpServletMapping
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.coyote.BadRequestException
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.AsyncDispatcher;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.ResponseFacade;
import org.apache.catalina.core.ApplicationFilterChain;
import org.apache.catalina.core.ApplicationFilterFactory;
import org.apache.catalina.core.ApplicationHttpRequest;
import org.apache.catalina.core.ApplicationHttpResponse;
import org.apache.catalina.core.ApplicationRequest;
import org.apache.catalina.core.ApplicationResponse;
import org.apache.catalina.core.StandardWrapper;
import org.apache.coyote.BadRequestException;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

final class ApplicationDispatcher
implements AsyncDispatcher,
RequestDispatcher {
    static final boolean STRICT_SERVLET_COMPLIANCE = Globals.STRICT_SERVLET_COMPLIANCE;
    static final boolean WRAP_SAME_OBJECT;
    private final Context context;
    private final String name;
    private final String pathInfo;
    private final String queryString;
    private final String requestURI;
    private final String servletPath;
    private final HttpServletMapping mapping;
    private static final StringManager sm;
    private final Wrapper wrapper;

    ApplicationDispatcher(Wrapper wrapper, String requestURI, String servletPath, String pathInfo, String queryString, HttpServletMapping mapping, String name) {
        this.wrapper = wrapper;
        this.context = (Context)wrapper.getParent();
        this.requestURI = requestURI;
        this.servletPath = servletPath;
        this.pathInfo = pathInfo;
        this.queryString = queryString;
        this.mapping = mapping;
        this.name = name;
    }

    public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if (Globals.IS_SECURITY_ENABLED) {
            try {
                PrivilegedForward dp = new PrivilegedForward(request, response);
                AccessController.doPrivileged(dp);
            }
            catch (PrivilegedActionException pe) {
                Exception e = pe.getException();
                if (e instanceof ServletException) {
                    throw (ServletException)((Object)e);
                }
                throw (IOException)e;
            }
        } else {
            this.doForward(request, response);
        }
    }

    private void doForward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        HttpServletRequest hrequest;
        ApplicationHttpRequest wrequest;
        if (response.isCommitted()) {
            throw new IllegalStateException(sm.getString("applicationDispatcher.forward.ise"));
        }
        response.resetBuffer();
        State state = new State(request, response, false);
        if (WRAP_SAME_OBJECT) {
            this.checkSameObjects(request, response);
        }
        this.wrapResponse(state);
        if (this.servletPath == null && this.pathInfo == null) {
            wrequest = (ApplicationHttpRequest)this.wrapRequest(state);
            hrequest = state.hrequest;
            wrequest.setRequestURI(hrequest.getRequestURI());
            wrequest.setContextPath(hrequest.getContextPath());
            wrequest.setServletPath(hrequest.getServletPath());
            wrequest.setPathInfo(hrequest.getPathInfo());
            wrequest.setQueryString(hrequest.getQueryString());
            this.processRequest(request, response, state);
        } else {
            wrequest = (ApplicationHttpRequest)this.wrapRequest(state);
            hrequest = state.hrequest;
            if (hrequest.getAttribute("javax.servlet.forward.request_uri") == null) {
                wrequest.setAttribute("javax.servlet.forward.request_uri", hrequest.getRequestURI());
                wrequest.setAttribute("javax.servlet.forward.context_path", hrequest.getContextPath());
                wrequest.setAttribute("javax.servlet.forward.servlet_path", hrequest.getServletPath());
                wrequest.setAttribute("javax.servlet.forward.path_info", hrequest.getPathInfo());
                wrequest.setAttribute("javax.servlet.forward.query_string", hrequest.getQueryString());
                wrequest.setAttribute("javax.servlet.forward.mapping", hrequest.getHttpServletMapping());
            }
            wrequest.setContextPath(this.context.getEncodedPath());
            wrequest.setRequestURI(this.requestURI);
            wrequest.setServletPath(this.servletPath);
            wrequest.setPathInfo(this.pathInfo);
            if (this.queryString != null) {
                wrequest.setQueryString(this.queryString);
                wrequest.setQueryParams(this.queryString);
            }
            wrequest.setMapping(this.mapping);
            this.processRequest(request, response, state);
        }
        if (request.isAsyncStarted()) {
            return;
        }
        if (this.wrapper.getLogger().isDebugEnabled()) {
            this.wrapper.getLogger().debug((Object)" Disabling the response for further output");
        }
        if (response instanceof ResponseFacade) {
            ((ResponseFacade)response).finish();
        } else {
            if (this.wrapper.getLogger().isDebugEnabled()) {
                this.wrapper.getLogger().debug((Object)(" The Response is vehiculed using a wrapper: " + response.getClass().getName()));
            }
            try {
                PrintWriter writer = response.getWriter();
                writer.close();
            }
            catch (IllegalStateException e) {
                try {
                    ServletOutputStream stream = response.getOutputStream();
                    stream.close();
                }
                catch (IOException | IllegalStateException exception) {}
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    private void processRequest(ServletRequest request, ServletResponse response, State state) throws IOException, ServletException {
        DispatcherType disInt = (DispatcherType)request.getAttribute("org.apache.catalina.core.DISPATCHER_TYPE");
        if (disInt != null) {
            boolean doInvoke = true;
            if (this.context.getFireRequestListenersOnForwards() && !this.context.fireRequestInitEvent(request)) {
                doInvoke = false;
            }
            if (doInvoke) {
                if (disInt != DispatcherType.ERROR) {
                    state.outerRequest.setAttribute("org.apache.catalina.core.DISPATCHER_REQUEST_PATH", (Object)this.getCombinedPath());
                    state.outerRequest.setAttribute("org.apache.catalina.core.DISPATCHER_TYPE", (Object)DispatcherType.FORWARD);
                    this.invoke(state.outerRequest, response, state);
                } else {
                    this.invoke(state.outerRequest, response, state);
                }
                if (this.context.getFireRequestListenersOnForwards()) {
                    this.context.fireRequestDestroyEvent(request);
                }
            }
        }
    }

    private String getCombinedPath() {
        if (this.servletPath == null) {
            return null;
        }
        if (this.pathInfo == null) {
            return this.servletPath;
        }
        return this.servletPath + this.pathInfo;
    }

    public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if (Globals.IS_SECURITY_ENABLED) {
            try {
                PrivilegedInclude dp = new PrivilegedInclude(request, response);
                AccessController.doPrivileged(dp);
            }
            catch (PrivilegedActionException pe) {
                Exception e = pe.getException();
                if (e instanceof ServletException) {
                    throw (ServletException)((Object)e);
                }
                throw (IOException)e;
            }
        } else {
            this.doInclude(request, response);
        }
    }

    private void doInclude(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        State state = new State(request, response, true);
        if (WRAP_SAME_OBJECT) {
            this.checkSameObjects(request, response);
        }
        this.wrapResponse(state);
        if (this.name != null) {
            ApplicationHttpRequest wrequest = (ApplicationHttpRequest)this.wrapRequest(state);
            wrequest.setAttribute("org.apache.catalina.NAMED", this.name);
            if (this.servletPath != null) {
                wrequest.setServletPath(this.servletPath);
            }
            wrequest.setAttribute("org.apache.catalina.core.DISPATCHER_TYPE", DispatcherType.INCLUDE);
            wrequest.setAttribute("org.apache.catalina.core.DISPATCHER_REQUEST_PATH", this.getCombinedPath());
            this.invoke(state.outerRequest, state.outerResponse, state);
        } else {
            ApplicationHttpRequest wrequest = (ApplicationHttpRequest)this.wrapRequest(state);
            String contextPath = this.context.getPath();
            if (this.requestURI != null) {
                wrequest.setAttribute("javax.servlet.include.request_uri", this.requestURI);
            }
            if (contextPath != null) {
                wrequest.setAttribute("javax.servlet.include.context_path", contextPath);
            }
            if (this.servletPath != null) {
                wrequest.setAttribute("javax.servlet.include.servlet_path", this.servletPath);
            }
            if (this.pathInfo != null) {
                wrequest.setAttribute("javax.servlet.include.path_info", this.pathInfo);
            }
            if (this.queryString != null) {
                wrequest.setAttribute("javax.servlet.include.query_string", this.queryString);
                wrequest.setQueryParams(this.queryString);
            }
            if (this.mapping != null) {
                wrequest.setAttribute("javax.servlet.include.mapping", this.mapping);
            }
            wrequest.setAttribute("org.apache.catalina.core.DISPATCHER_TYPE", DispatcherType.INCLUDE);
            wrequest.setAttribute("org.apache.catalina.core.DISPATCHER_REQUEST_PATH", this.getCombinedPath());
            this.invoke(state.outerRequest, state.outerResponse, state);
        }
    }

    @Override
    public void dispatch(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        if (Globals.IS_SECURITY_ENABLED) {
            try {
                PrivilegedDispatch dp = new PrivilegedDispatch(request, response);
                AccessController.doPrivileged(dp);
            }
            catch (PrivilegedActionException pe) {
                Exception e = pe.getException();
                if (e instanceof ServletException) {
                    throw (ServletException)((Object)e);
                }
                throw (IOException)e;
            }
        } else {
            this.doDispatch(request, response);
        }
    }

    private void doDispatch(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        State state = new State(request, response, false);
        this.wrapResponse(state);
        ApplicationHttpRequest wrequest = (ApplicationHttpRequest)this.wrapRequest(state);
        HttpServletRequest hrequest = state.hrequest;
        wrequest.setAttribute("org.apache.catalina.core.DISPATCHER_TYPE", DispatcherType.ASYNC);
        wrequest.setAttribute("org.apache.catalina.core.DISPATCHER_REQUEST_PATH", this.getCombinedPath());
        wrequest.setAttribute("javax.servlet.async.mapping", hrequest.getHttpServletMapping());
        wrequest.setContextPath(this.context.getEncodedPath());
        wrequest.setRequestURI(this.requestURI);
        wrequest.setServletPath(this.servletPath);
        wrequest.setPathInfo(this.pathInfo);
        if (this.queryString != null) {
            wrequest.setQueryString(this.queryString);
            wrequest.setQueryParams(this.queryString);
        }
        if (!Globals.STRICT_SERVLET_COMPLIANCE) {
            wrequest.setMapping(this.mapping);
        }
        this.invoke(state.outerRequest, state.outerResponse, state);
    }

    private void invoke(ServletRequest request, ServletResponse response, State state) throws IOException, ServletException {
        ClassLoader oldCCL = this.context.bind(false, null);
        HttpServletResponse hresponse = state.hresponse;
        Servlet servlet = null;
        Throwable ioException = null;
        Throwable servletException = null;
        RuntimeException runtimeException = null;
        boolean unavailable = false;
        if (this.wrapper.isUnavailable()) {
            this.wrapper.getLogger().warn((Object)sm.getString("applicationDispatcher.isUnavailable", new Object[]{this.wrapper.getName()}));
            long available = this.wrapper.getAvailable();
            if (available > 0L && available < Long.MAX_VALUE) {
                hresponse.setDateHeader("Retry-After", available);
            }
            hresponse.sendError(503, sm.getString("applicationDispatcher.isUnavailable", new Object[]{this.wrapper.getName()}));
            unavailable = true;
        }
        try {
            if (!unavailable) {
                servlet = this.wrapper.allocate();
            }
        }
        catch (ServletException e) {
            this.wrapper.getLogger().error((Object)sm.getString("applicationDispatcher.allocateException", new Object[]{this.wrapper.getName()}), StandardWrapper.getRootCause(e));
            servletException = e;
        }
        catch (Throwable e) {
            ExceptionUtils.handleThrowable((Throwable)e);
            this.wrapper.getLogger().error((Object)sm.getString("applicationDispatcher.allocateException", new Object[]{this.wrapper.getName()}), e);
            servletException = new ServletException(sm.getString("applicationDispatcher.allocateException", new Object[]{this.wrapper.getName()}), e);
            servlet = null;
        }
        ApplicationFilterChain filterChain = ApplicationFilterFactory.createFilterChain(request, this.wrapper, servlet);
        try {
            if (servlet != null && filterChain != null) {
                filterChain.doFilter(request, response);
            }
        }
        catch (BadRequestException e) {
            ioException = e;
        }
        catch (IOException e) {
            this.wrapper.getLogger().error((Object)sm.getString("applicationDispatcher.serviceException", new Object[]{this.wrapper.getName()}), (Throwable)e);
            ioException = e;
        }
        catch (UnavailableException e) {
            this.wrapper.getLogger().error((Object)sm.getString("applicationDispatcher.serviceException", new Object[]{this.wrapper.getName()}), (Throwable)e);
            servletException = e;
            this.wrapper.unavailable(e);
        }
        catch (ServletException e) {
            Throwable rootCause = StandardWrapper.getRootCause(e);
            if (!(rootCause instanceof BadRequestException)) {
                this.wrapper.getLogger().error((Object)sm.getString("applicationDispatcher.serviceException", new Object[]{this.wrapper.getName()}), rootCause);
            }
            servletException = e;
        }
        catch (RuntimeException e) {
            this.wrapper.getLogger().error((Object)sm.getString("applicationDispatcher.serviceException", new Object[]{this.wrapper.getName()}), (Throwable)e);
            runtimeException = e;
        }
        if (filterChain != null) {
            filterChain.release();
        }
        try {
            if (servlet != null) {
                this.wrapper.deallocate(servlet);
            }
        }
        catch (ServletException e) {
            this.wrapper.getLogger().error((Object)sm.getString("applicationDispatcher.deallocateException", new Object[]{this.wrapper.getName()}), (Throwable)e);
            servletException = e;
        }
        catch (Throwable e) {
            ExceptionUtils.handleThrowable((Throwable)e);
            this.wrapper.getLogger().error((Object)sm.getString("applicationDispatcher.deallocateException", new Object[]{this.wrapper.getName()}), e);
            servletException = new ServletException(sm.getString("applicationDispatcher.deallocateException", new Object[]{this.wrapper.getName()}), e);
        }
        this.context.unbind(false, oldCCL);
        this.unwrapRequest(state);
        this.unwrapResponse(state);
        this.recycleRequestWrapper(state);
        if (ioException != null) {
            throw ioException;
        }
        if (servletException != null) {
            throw servletException;
        }
        if (runtimeException != null) {
            throw runtimeException;
        }
    }

    private void unwrapRequest(State state) {
        if (state.wrapRequest == null) {
            return;
        }
        if (state.outerRequest.isAsyncStarted() && !state.outerRequest.getAsyncContext().hasOriginalRequestAndResponse()) {
            return;
        }
        ServletRequest previous = null;
        ServletRequest current = state.outerRequest;
        while (current != null && !(current instanceof Request) && !(current instanceof RequestFacade)) {
            if (current == state.wrapRequest) {
                ServletRequest next = ((ServletRequestWrapper)current).getRequest();
                if (previous == null) {
                    state.outerRequest = next;
                    break;
                }
                ((ServletRequestWrapper)previous).setRequest(next);
                break;
            }
            previous = current;
            current = ((ServletRequestWrapper)current).getRequest();
        }
    }

    private void unwrapResponse(State state) {
        if (state.wrapResponse == null) {
            return;
        }
        if (state.outerRequest.isAsyncStarted() && !state.outerRequest.getAsyncContext().hasOriginalRequestAndResponse()) {
            return;
        }
        ServletResponse previous = null;
        ServletResponse current = state.outerResponse;
        while (current != null && !(current instanceof Response) && !(current instanceof ResponseFacade)) {
            if (current == state.wrapResponse) {
                ServletResponse next = ((ServletResponseWrapper)current).getResponse();
                if (previous == null) {
                    state.outerResponse = next;
                    break;
                }
                ((ServletResponseWrapper)previous).setResponse(next);
                break;
            }
            previous = current;
            current = ((ServletResponseWrapper)current).getResponse();
        }
    }

    private ServletRequest wrapRequest(State state) {
        ServletRequest previous = null;
        ServletRequest current = state.outerRequest;
        while (current != null) {
            if (state.hrequest == null && current instanceof HttpServletRequest) {
                state.hrequest = (HttpServletRequest)current;
            }
            if (!(current instanceof ServletRequestWrapper) || current instanceof ApplicationHttpRequest || current instanceof ApplicationRequest) break;
            previous = current;
            current = ((ServletRequestWrapper)current).getRequest();
        }
        Object wrapper = null;
        if (current instanceof ApplicationHttpRequest || current instanceof Request || current instanceof HttpServletRequest) {
            HttpServletRequest hcurrent = (HttpServletRequest)current;
            boolean crossContext = false;
            if (state.outerRequest instanceof ApplicationHttpRequest || state.outerRequest instanceof Request || state.outerRequest instanceof HttpServletRequest) {
                HttpServletRequest houterRequest = (HttpServletRequest)state.outerRequest;
                Object contextPath = houterRequest.getAttribute("javax.servlet.include.context_path");
                if (contextPath == null) {
                    contextPath = houterRequest.getContextPath();
                }
                crossContext = !this.context.getPath().equals(contextPath);
            }
            wrapper = new ApplicationHttpRequest(hcurrent, this.context, crossContext);
        } else {
            wrapper = new ApplicationRequest(current);
        }
        if (previous == null) {
            state.outerRequest = wrapper;
        } else {
            ((ServletRequestWrapper)previous).setRequest((ServletRequest)wrapper);
        }
        state.wrapRequest = wrapper;
        return wrapper;
    }

    private ServletResponse wrapResponse(State state) {
        ServletResponse previous = null;
        ServletResponse current = state.outerResponse;
        while (current != null) {
            if (state.hresponse == null && current instanceof HttpServletResponse) {
                state.hresponse = (HttpServletResponse)current;
                if (!state.including) {
                    return null;
                }
            }
            if (!(current instanceof ServletResponseWrapper) || current instanceof ApplicationHttpResponse || current instanceof ApplicationResponse) break;
            previous = current;
            current = ((ServletResponseWrapper)current).getResponse();
        }
        Object wrapper = null;
        wrapper = current instanceof ApplicationHttpResponse || current instanceof Response || current instanceof HttpServletResponse ? new ApplicationHttpResponse((HttpServletResponse)current, state.including) : new ApplicationResponse(current, state.including);
        if (previous == null) {
            state.outerResponse = wrapper;
        } else {
            ((ServletResponseWrapper)previous).setResponse((ServletResponse)wrapper);
        }
        state.wrapResponse = wrapper;
        return wrapper;
    }

    private void checkSameObjects(ServletRequest appRequest, ServletResponse appResponse) throws ServletException {
        ServletRequest originalRequest = ApplicationFilterChain.getLastServicedRequest();
        ServletResponse originalResponse = ApplicationFilterChain.getLastServicedResponse();
        if (originalRequest == null || originalResponse == null) {
            return;
        }
        boolean same = false;
        ServletRequest dispatchedRequest = appRequest;
        while (originalRequest instanceof ServletRequestWrapper && ((ServletRequestWrapper)originalRequest).getRequest() != null) {
            originalRequest = ((ServletRequestWrapper)originalRequest).getRequest();
        }
        while (!same) {
            if (originalRequest.equals(dispatchedRequest)) {
                same = true;
            }
            if (same || !(dispatchedRequest instanceof ServletRequestWrapper)) break;
            dispatchedRequest = ((ServletRequestWrapper)dispatchedRequest).getRequest();
        }
        if (!same) {
            throw new ServletException(sm.getString("applicationDispatcher.specViolation.request"));
        }
        same = false;
        ServletResponse dispatchedResponse = appResponse;
        while (originalResponse instanceof ServletResponseWrapper && ((ServletResponseWrapper)originalResponse).getResponse() != null) {
            originalResponse = ((ServletResponseWrapper)originalResponse).getResponse();
        }
        while (!same) {
            if (originalResponse.equals(dispatchedResponse)) {
                same = true;
            }
            if (same || !(dispatchedResponse instanceof ServletResponseWrapper)) break;
            dispatchedResponse = ((ServletResponseWrapper)dispatchedResponse).getResponse();
        }
        if (!same) {
            throw new ServletException(sm.getString("applicationDispatcher.specViolation.response"));
        }
    }

    private void recycleRequestWrapper(State state) {
        if (state.wrapRequest instanceof ApplicationHttpRequest) {
            ((ApplicationHttpRequest)state.wrapRequest).recycle();
        }
    }

    static {
        String wrapSameObject = System.getProperty("org.apache.catalina.core.ApplicationDispatcher.WRAP_SAME_OBJECT");
        WRAP_SAME_OBJECT = wrapSameObject == null ? STRICT_SERVLET_COMPLIANCE : Boolean.parseBoolean(wrapSameObject);
        sm = StringManager.getManager(ApplicationDispatcher.class);
    }

    protected class PrivilegedForward
    implements PrivilegedExceptionAction<Void> {
        private final ServletRequest request;
        private final ServletResponse response;

        PrivilegedForward(ServletRequest request, ServletResponse response) {
            this.request = request;
            this.response = response;
        }

        @Override
        public Void run() throws Exception {
            ApplicationDispatcher.this.doForward(this.request, this.response);
            return null;
        }
    }

    private static class State {
        ServletRequest outerRequest = null;
        ServletResponse outerResponse = null;
        ServletRequest wrapRequest = null;
        ServletResponse wrapResponse = null;
        boolean including = false;
        HttpServletRequest hrequest = null;
        HttpServletResponse hresponse = null;

        State(ServletRequest request, ServletResponse response, boolean including) {
            this.outerRequest = request;
            this.outerResponse = response;
            this.including = including;
        }
    }

    protected class PrivilegedInclude
    implements PrivilegedExceptionAction<Void> {
        private final ServletRequest request;
        private final ServletResponse response;

        PrivilegedInclude(ServletRequest request, ServletResponse response) {
            this.request = request;
            this.response = response;
        }

        @Override
        public Void run() throws ServletException, IOException {
            ApplicationDispatcher.this.doInclude(this.request, this.response);
            return null;
        }
    }

    protected class PrivilegedDispatch
    implements PrivilegedExceptionAction<Void> {
        private final ServletRequest request;
        private final ServletResponse response;

        PrivilegedDispatch(ServletRequest request, ServletResponse response) {
            this.request = request;
            this.response = response;
        }

        @Override
        public Void run() throws ServletException, IOException {
            ApplicationDispatcher.this.doDispatch(this.request, this.response);
            return null;
        }
    }
}

