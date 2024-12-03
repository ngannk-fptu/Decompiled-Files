/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.servlet.AsyncContext
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.transport.http.servlet;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.ha.HighAvailabilityProvider;
import com.sun.xml.ws.api.ha.StickyFeature;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.server.BoundEndpoint;
import com.sun.xml.ws.api.server.Module;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WebModule;
import com.sun.xml.ws.transport.http.HttpAdapter;
import com.sun.xml.ws.transport.http.WSHTTPConnection;
import com.sun.xml.ws.transport.http.servlet.ServletAdapterList;
import com.sun.xml.ws.transport.http.servlet.ServletConnectionImpl;
import com.sun.xml.ws.transport.http.servlet.ServletUtil;
import com.sun.xml.ws.transport.http.servlet.WSAsyncListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public class ServletAdapter
extends HttpAdapter
implements BoundEndpoint {
    final String name;
    private static final Logger LOGGER = Logger.getLogger(ServletAdapter.class.getName());
    private boolean isServlet30Based = ServletUtil.isServlet30Based();

    protected ServletAdapter(String name, String urlPattern, WSEndpoint endpoint, ServletAdapterList owner) {
        super(endpoint, owner, urlPattern);
        this.name = name;
        Module module = endpoint.getContainer().getSPI(Module.class);
        if (module == null) {
            LOGGER.log(Level.WARNING, "Container {0} doesn''t support {1}", new Object[]{endpoint.getContainer(), Module.class});
        } else {
            module.getBoundEndpoints().add(this);
        }
        boolean sticky = false;
        if (HighAvailabilityProvider.INSTANCE.isHaEnvironmentConfigured()) {
            WebServiceFeature[] features;
            for (WebServiceFeature f : features = endpoint.getBinding().getFeatures().toArray()) {
                if (!(f instanceof StickyFeature)) continue;
                sticky = true;
                break;
            }
            this.disableJreplicaCookie = HighAvailabilityProvider.INSTANCE.isDisabledJreplica();
        }
        this.stickyCookie = sticky;
    }

    public ServletContext getServletContext() {
        return ((ServletAdapterList)this.owner).getServletContext();
    }

    public String getName() {
        return this.name;
    }

    @Override
    @NotNull
    public URI getAddress() {
        WebModule webModule = this.endpoint.getContainer().getSPI(WebModule.class);
        if (webModule == null) {
            throw new WebServiceException("Container " + this.endpoint.getContainer() + " doesn't support " + WebModule.class);
        }
        return this.getAddress(webModule.getContextPath());
    }

    @Override
    @NotNull
    public URI getAddress(String baseAddress) {
        String adrs = baseAddress + this.getValidPath();
        try {
            return new URI(adrs);
        }
        catch (URISyntaxException e) {
            throw new WebServiceException("Unable to compute address for " + this.endpoint, (Throwable)e);
        }
    }

    public QName getPortName() {
        WSDLPort port = this.getEndpoint().getPort();
        if (port == null) {
            return null;
        }
        return port.getName();
    }

    public void handle(ServletContext context, HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.handle(this.createConnection(context, request, response));
    }

    protected WSHTTPConnection createConnection(ServletContext context, HttpServletRequest request, HttpServletResponse response) {
        return new ServletConnectionImpl(this, context, request, response);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void invokeAsync(ServletContext context, HttpServletRequest request, HttpServletResponse response, HttpAdapter.CompletionCallback callback) throws IOException {
        boolean asyncStarted = false;
        try {
            ServletConnectionImpl connection = new ServletConnectionImpl(this, context, request, response);
            if (this.handleGet(connection)) {
                return;
            }
            boolean asyncRequest = false;
            try {
                asyncRequest = this.isServlet30Based && request.isAsyncSupported() && !request.isAsyncStarted();
            }
            catch (Throwable t) {
                LOGGER.log(Level.INFO, request.getClass().getName() + " does not support Async API, Continuing with synchronous processing", t);
                this.isServlet30Based = false;
            }
            if (asyncRequest) {
                final AsyncContext asyncContext = request.startAsync((ServletRequest)request, (ServletResponse)response);
                final AsyncCompletionCheck completionCheck = new AsyncCompletionCheck();
                new WSAsyncListener(connection, callback).addListenerTo(asyncContext, completionCheck);
                super.invokeAsync(connection, new HttpAdapter.CompletionCallback(){

                    /*
                     * WARNING - Removed try catching itself - possible behaviour change.
                     */
                    @Override
                    public void onCompletion() {
                        AsyncCompletionCheck asyncCompletionCheck = completionCheck;
                        synchronized (asyncCompletionCheck) {
                            if (!completionCheck.isCompleted()) {
                                asyncContext.complete();
                                completionCheck.markComplete();
                            }
                        }
                    }
                });
                asyncStarted = true;
            } else {
                super.handle(connection);
            }
        }
        finally {
            if (!asyncStarted) {
                callback.onCompletion();
            }
        }
    }

    public void publishWSDL(ServletContext context, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletConnectionImpl connection = new ServletConnectionImpl(this, context, request, response);
        super.handle(connection);
    }

    public String toString() {
        return super.toString() + "[name=" + this.name + ']';
    }

    static class AsyncCompletionCheck {
        boolean completed = false;

        AsyncCompletionCheck() {
        }

        synchronized void markComplete() {
            this.completed = true;
        }

        synchronized boolean isCompleted() {
            return this.completed;
        }
    }
}

