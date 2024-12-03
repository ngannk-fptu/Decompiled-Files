/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletContextAttributeEvent
 *  javax.servlet.ServletContextAttributeListener
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.transport.httpspi.servlet;

import com.sun.xml.ws.transport.httpspi.servlet.DeploymentDescriptorParser;
import com.sun.xml.ws.transport.httpspi.servlet.EndpointAdapter;
import com.sun.xml.ws.transport.httpspi.servlet.EndpointAdapterFactory;
import com.sun.xml.ws.transport.httpspi.servlet.ServletResourceLoader;
import com.sun.xml.ws.transport.httpspi.servlet.WSServletDelegate;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.ws.WebServiceException;

public final class WSSPIContextListener
implements ServletContextAttributeListener,
ServletContextListener {
    private WSServletDelegate delegate;
    private static final String JAXWS_RI_RUNTIME = "/WEB-INF/sun-jaxws.xml";
    private static final Logger logger = Logger.getLogger(WSSPIContextListener.class.getName());

    public void attributeAdded(ServletContextAttributeEvent event) {
    }

    public void attributeRemoved(ServletContextAttributeEvent event) {
    }

    public void attributeReplaced(ServletContextAttributeEvent event) {
    }

    public void contextDestroyed(ServletContextEvent event) {
        if (this.delegate != null) {
            this.delegate.destroy();
        }
        if (logger.isLoggable(Level.INFO)) {
            logger.info("JAX-WS context listener destroyed");
        }
    }

    public void contextInitialized(ServletContextEvent event) {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("JAX-WS context listener initializing");
        }
        ServletContext context = event.getServletContext();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }
        try {
            DeploymentDescriptorParser<EndpointAdapter> parser = new DeploymentDescriptorParser<EndpointAdapter>(classLoader, new ServletResourceLoader(context), new EndpointAdapterFactory());
            URL sunJaxWsXml = context.getResource(JAXWS_RI_RUNTIME);
            if (sunJaxWsXml == null) {
                throw new WebServiceException("Runtime descriptor /WEB-INF/sun-jaxws.xml is mising");
            }
            List<EndpointAdapter> adapters = parser.parse(sunJaxWsXml.toExternalForm(), sunJaxWsXml.openStream());
            for (EndpointAdapter adapter : adapters) {
                adapter.publish();
            }
            this.delegate = this.createDelegate(adapters, context);
            context.setAttribute("com.sun.xml.ws.server.http.servletDelegate", (Object)this.delegate);
        }
        catch (Throwable e) {
            logger.log(Level.SEVERE, "failed to parse runtime descriptor", e);
            context.removeAttribute("com.sun.xml.ws.server.http.servletDelegate");
            throw new WebServiceException("failed to parse runtime descriptor", e);
        }
    }

    protected WSServletDelegate createDelegate(List<EndpointAdapter> adapters, ServletContext context) {
        return new WSServletDelegate(adapters, context);
    }
}

