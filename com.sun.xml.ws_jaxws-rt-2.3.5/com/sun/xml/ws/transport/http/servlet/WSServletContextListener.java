/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletContextAttributeEvent
 *  javax.servlet.ServletContextAttributeListener
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 *  javax.servlet.ServletRegistration
 *  javax.servlet.ServletRegistration$Dynamic
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.transport.http.servlet;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.resources.WsservletMessages;
import com.sun.xml.ws.transport.http.DeploymentDescriptorParser;
import com.sun.xml.ws.transport.http.servlet.JAXWSRIDeploymentProbeProvider;
import com.sun.xml.ws.transport.http.servlet.ServletAdapter;
import com.sun.xml.ws.transport.http.servlet.ServletAdapterList;
import com.sun.xml.ws.transport.http.servlet.ServletContainer;
import com.sun.xml.ws.transport.http.servlet.ServletResourceLoader;
import com.sun.xml.ws.transport.http.servlet.ServletUtil;
import com.sun.xml.ws.transport.http.servlet.WSServlet;
import com.sun.xml.ws.transport.http.servlet.WSServletDelegate;
import com.sun.xml.ws.transport.http.servlet.WSServletException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.xml.ws.WebServiceException;

public final class WSServletContextListener
implements ServletContextAttributeListener,
ServletContextListener {
    private WSServletDelegate delegate;
    private List<ServletAdapter> adapters;
    private final JAXWSRIDeploymentProbeProvider probe = new JAXWSRIDeploymentProbeProvider();
    private static final String WSSERVLET_CONTEXT_LISTENER_INVOKED = "com.sun.xml.ws.transport.http.servlet.WSServletContextListener.Invoked";
    static final String JAXWS_RI_RUNTIME = "/WEB-INF/sun-jaxws.xml";
    private static final Logger logger = Logger.getLogger("com.sun.xml.ws.server.http");

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
        if (this.adapters != null) {
            for (ServletAdapter a : this.adapters) {
                try {
                    a.getEndpoint().dispose();
                }
                catch (Throwable e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
                this.probe.undeploy(a);
            }
        }
        if (logger.isLoggable(Level.INFO)) {
            logger.info(WsservletMessages.LISTENER_INFO_DESTROY());
        }
    }

    void parseAdaptersAndCreateDelegate(ServletContext context) {
        String alreadyInvoked = (String)context.getAttribute(WSSERVLET_CONTEXT_LISTENER_INVOKED);
        if (Boolean.valueOf(alreadyInvoked).booleanValue()) {
            return;
        }
        context.setAttribute(WSSERVLET_CONTEXT_LISTENER_INVOKED, (Object)"true");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }
        try {
            URL sunJaxWsXml = context.getResource(JAXWS_RI_RUNTIME);
            if (sunJaxWsXml == null) {
                throw new WebServiceException(WsservletMessages.NO_SUNJAXWS_XML(JAXWS_RI_RUNTIME));
            }
            DeploymentDescriptorParser<ServletAdapter> parser = new DeploymentDescriptorParser<ServletAdapter>(classLoader, new ServletResourceLoader(context), this.createContainer(context), new ServletAdapterList(context));
            this.adapters = parser.parse(sunJaxWsXml.toExternalForm(), sunJaxWsXml.openStream());
            this.registerWSServlet(this.adapters, context);
            this.delegate = this.createDelegate(this.adapters, context);
            context.setAttribute("com.sun.xml.ws.server.http.servletDelegate", (Object)this.delegate);
        }
        catch (Throwable e) {
            logger.log(Level.SEVERE, WsservletMessages.LISTENER_PARSING_FAILED(e), e);
            context.removeAttribute("com.sun.xml.ws.server.http.servletDelegate");
            throw new WSServletException("listener.parsingFailed", e);
        }
    }

    public void contextInitialized(ServletContextEvent event) {
        if (logger.isLoggable(Level.INFO)) {
            logger.info(WsservletMessages.LISTENER_INFO_INITIALIZE());
        }
        ServletContext context = event.getServletContext();
        this.parseAdaptersAndCreateDelegate(context);
        if (this.adapters != null) {
            for (ServletAdapter adapter : this.adapters) {
                this.probe.deploy(adapter);
            }
        }
    }

    private void registerWSServlet(List<ServletAdapter> adapters, ServletContext context) {
        if (!ServletUtil.isServlet30Based()) {
            return;
        }
        HashSet<String> unregisteredUrlPatterns = new HashSet<String>();
        try {
            Collection registrations = context.getServletRegistrations().values();
            for (ServletAdapter adapter : adapters) {
                if (this.existsServletForUrlPattern(adapter.urlPattern, registrations)) continue;
                unregisteredUrlPatterns.add(adapter.urlPattern);
            }
            if (!unregisteredUrlPatterns.isEmpty()) {
                ServletRegistration.Dynamic registration = context.addServlet("Dynamic JAXWS Servlet", WSServlet.class);
                registration.addMapping(unregisteredUrlPatterns.toArray(new String[unregisteredUrlPatterns.size()]));
                registration.setAsyncSupported(true);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean existsServletForUrlPattern(String urlpattern, Collection<? extends ServletRegistration> registrations) {
        for (ServletRegistration servletRegistration : registrations) {
            if (!servletRegistration.getMappings().contains(urlpattern)) continue;
            return true;
        }
        return false;
    }

    @NotNull
    protected Container createContainer(ServletContext context) {
        return new ServletContainer(context);
    }

    @NotNull
    protected WSServletDelegate createDelegate(List<ServletAdapter> adapters, ServletContext context) {
        return new WSServletDelegate(adapters, context);
    }
}

