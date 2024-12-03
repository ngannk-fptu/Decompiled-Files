/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.DispatcherType
 *  javax.servlet.RequestDispatcher
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  org.apache.coyote.ActionCode
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.descriptor.web.ErrorPage
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.core;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.coyote.ActionCode;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.descriptor.web.ErrorPage;
import org.apache.tomcat.util.res.StringManager;

final class StandardHostValve
extends ValveBase {
    private static final Log log = LogFactory.getLog(StandardHostValve.class);
    private static final StringManager sm = StringManager.getManager(StandardHostValve.class);
    private static final ClassLoader MY_CLASSLOADER = StandardHostValve.class.getClassLoader();
    static final boolean STRICT_SERVLET_COMPLIANCE = Globals.STRICT_SERVLET_COMPLIANCE;
    static final boolean ACCESS_SESSION;

    StandardHostValve() {
        super(true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        Context context = request.getContext();
        if (context == null) {
            if (!response.isError()) {
                response.sendError(404);
            }
            return;
        }
        if (request.isAsyncSupported()) {
            request.setAsyncSupported(context.getPipeline().isAsyncSupported());
        }
        boolean asyncAtStart = request.isAsync();
        try {
            block19: {
                context.bind(Globals.IS_SECURITY_ENABLED, MY_CLASSLOADER);
                if (!asyncAtStart && !context.fireRequestInitEvent((ServletRequest)request.getRequest())) {
                    return;
                }
                try {
                    if (!response.isErrorReportRequired()) {
                        context.getPipeline().getFirst().invoke(request, response);
                    }
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                    this.container.getLogger().error((Object)("Exception Processing " + request.getRequestURI()), t);
                    if (response.isErrorReportRequired()) break block19;
                    request.setAttribute("javax.servlet.error.exception", t);
                    this.throwable(request, response, t);
                }
            }
            response.setSuspended(false);
            Throwable t = (Throwable)request.getAttribute("javax.servlet.error.exception");
            if (!context.getState().isAvailable()) {
                return;
            }
            if (response.isErrorReportRequired()) {
                AtomicBoolean result = new AtomicBoolean(false);
                response.getCoyoteResponse().action(ActionCode.IS_IO_ALLOWED, (Object)result);
                if (result.get()) {
                    if (t != null) {
                        this.throwable(request, response, t);
                    } else {
                        this.status(request, response);
                    }
                }
            }
            if (!request.isAsync() && !asyncAtStart) {
                context.fireRequestDestroyEvent((ServletRequest)request.getRequest());
            }
        }
        finally {
            if (ACCESS_SESSION) {
                request.getSession(false);
            }
            context.unbind(Globals.IS_SECURITY_ENABLED, MY_CLASSLOADER);
        }
    }

    private void status(Request request, Response response) {
        int statusCode = response.getStatus();
        Context context = request.getContext();
        if (context == null) {
            return;
        }
        if (!response.isError()) {
            return;
        }
        ErrorPage errorPage = context.findErrorPage(statusCode);
        if (errorPage == null) {
            errorPage = context.findErrorPage(0);
        }
        if (errorPage != null && response.isErrorReportRequired()) {
            response.setAppCommitted(false);
            request.setAttribute("javax.servlet.error.status_code", statusCode);
            String message = response.getMessage();
            if (message == null) {
                message = "";
            }
            request.setAttribute("javax.servlet.error.message", message);
            request.setAttribute("org.apache.catalina.core.DISPATCHER_REQUEST_PATH", errorPage.getLocation());
            request.setAttribute("org.apache.catalina.core.DISPATCHER_TYPE", DispatcherType.ERROR);
            Wrapper wrapper = request.getWrapper();
            if (wrapper != null) {
                request.setAttribute("javax.servlet.error.servlet_name", wrapper.getName());
            }
            request.setAttribute("javax.servlet.error.request_uri", request.getRequestURI());
            if (this.custom(request, response, errorPage)) {
                response.setErrorReported();
                try {
                    response.finishResponse();
                }
                catch (ClientAbortException clientAbortException) {
                }
                catch (IOException e) {
                    this.container.getLogger().warn((Object)("Exception Processing " + errorPage), (Throwable)e);
                }
            }
        }
    }

    protected void throwable(Request request, Response response, Throwable throwable) {
        Context context = request.getContext();
        if (context == null) {
            return;
        }
        Throwable realError = throwable;
        if (realError instanceof ServletException && (realError = ((ServletException)realError).getRootCause()) == null) {
            realError = throwable;
        }
        if (realError instanceof ClientAbortException) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("standardHost.clientAbort", new Object[]{realError.getCause().getMessage()}));
            }
            return;
        }
        ErrorPage errorPage = context.findErrorPage(throwable);
        if (errorPage == null && realError != throwable) {
            errorPage = context.findErrorPage(realError);
        }
        if (errorPage != null) {
            if (response.setErrorReported()) {
                response.setAppCommitted(false);
                request.setAttribute("org.apache.catalina.core.DISPATCHER_REQUEST_PATH", errorPage.getLocation());
                request.setAttribute("org.apache.catalina.core.DISPATCHER_TYPE", DispatcherType.ERROR);
                request.setAttribute("javax.servlet.error.status_code", 500);
                request.setAttribute("javax.servlet.error.message", throwable.getMessage());
                request.setAttribute("javax.servlet.error.exception", realError);
                Wrapper wrapper = request.getWrapper();
                if (wrapper != null) {
                    request.setAttribute("javax.servlet.error.servlet_name", wrapper.getName());
                }
                request.setAttribute("javax.servlet.error.request_uri", request.getRequestURI());
                request.setAttribute("javax.servlet.error.exception_type", realError.getClass());
                if (this.custom(request, response, errorPage)) {
                    try {
                        response.finishResponse();
                    }
                    catch (IOException e) {
                        this.container.getLogger().warn((Object)("Exception Processing " + errorPage), (Throwable)e);
                    }
                }
            }
        } else {
            if (response.getStatus() < 400) {
                response.setStatus(500);
            }
            response.setError();
            this.status(request, response);
        }
    }

    private boolean custom(Request request, Response response, ErrorPage errorPage) {
        if (this.container.getLogger().isDebugEnabled()) {
            this.container.getLogger().debug((Object)("Processing " + errorPage));
        }
        try {
            ServletContext servletContext = request.getContext().getServletContext();
            RequestDispatcher rd = servletContext.getRequestDispatcher(errorPage.getLocation());
            if (rd == null) {
                this.container.getLogger().error((Object)sm.getString("standardHostValue.customStatusFailed", new Object[]{errorPage.getLocation()}));
                return false;
            }
            if (response.isCommitted()) {
                rd.include((ServletRequest)request.getRequest(), (ServletResponse)response.getResponse());
                try {
                    response.flushBuffer();
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable((Throwable)t);
                }
                response.getCoyoteResponse().action(ActionCode.CLOSE_NOW, request.getAttribute("javax.servlet.error.exception"));
            } else {
                response.resetBuffer(true);
                response.setContentLength(-1);
                rd.forward((ServletRequest)request.getRequest(), (ServletResponse)response.getResponse());
                response.setSuspended(false);
            }
            return true;
        }
        catch (Throwable t) {
            ExceptionUtils.handleThrowable((Throwable)t);
            this.container.getLogger().error((Object)("Exception Processing " + errorPage), t);
            return false;
        }
    }

    static {
        String accessSession = System.getProperty("org.apache.catalina.core.StandardHostValve.ACCESS_SESSION");
        ACCESS_SESSION = accessSession == null ? STRICT_SERVLET_COMPLIANCE : Boolean.parseBoolean(accessSession);
    }
}

