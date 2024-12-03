/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.UsesSunHttpServer
 */
package org.springframework.remoting.support;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.UsesSunHttpServer;

@Deprecated
@UsesSunHttpServer
public class SimpleHttpServerFactoryBean
implements FactoryBean<HttpServer>,
InitializingBean,
DisposableBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private int port = 8080;
    private String hostname;
    private int backlog = -1;
    private int shutdownDelay = 0;
    private Executor executor;
    private Map<String, HttpHandler> contexts;
    private List<Filter> filters;
    private Authenticator authenticator;
    private HttpServer server;

    public void setPort(int port) {
        this.port = port;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public void setShutdownDelay(int shutdownDelay) {
        this.shutdownDelay = shutdownDelay;
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public void setContexts(Map<String, HttpHandler> contexts) {
        this.contexts = contexts;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public void afterPropertiesSet() throws IOException {
        InetSocketAddress address = this.hostname != null ? new InetSocketAddress(this.hostname, this.port) : new InetSocketAddress(this.port);
        this.server = HttpServer.create(address, this.backlog);
        if (this.executor != null) {
            this.server.setExecutor(this.executor);
        }
        if (this.contexts != null) {
            this.contexts.forEach((key, context) -> {
                HttpContext httpContext = this.server.createContext((String)key, (HttpHandler)context);
                if (this.filters != null) {
                    httpContext.getFilters().addAll(this.filters);
                }
                if (this.authenticator != null) {
                    httpContext.setAuthenticator(this.authenticator);
                }
            });
        }
        if (this.logger.isInfoEnabled()) {
            this.logger.info((Object)("Starting HttpServer at address " + address));
        }
        this.server.start();
    }

    public HttpServer getObject() {
        return this.server;
    }

    public Class<? extends HttpServer> getObjectType() {
        return this.server != null ? this.server.getClass() : HttpServer.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void destroy() {
        this.logger.info((Object)"Stopping HttpServer");
        this.server.stop(this.shutdownDelay);
    }
}

