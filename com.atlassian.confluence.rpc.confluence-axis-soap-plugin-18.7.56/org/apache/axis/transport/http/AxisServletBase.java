/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.transport.http;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.axis.AxisFault;
import org.apache.axis.AxisProperties;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.configuration.EngineConfigurationFactoryFinder;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.JavaUtils;
import org.apache.commons.logging.Log;

public class AxisServletBase
extends HttpServlet {
    protected AxisServer axisServer = null;
    private static Log log = LogFactory.getLog((class$org$apache$axis$transport$http$AxisServlet == null ? (class$org$apache$axis$transport$http$AxisServlet = AxisServletBase.class$("org.apache.axis.transport.http.AxisServlet")) : class$org$apache$axis$transport$http$AxisServlet).getName());
    private static boolean isDebug = false;
    private static int loadCounter = 0;
    private static Object loadCounterLock = new Object();
    protected static final String ATTR_AXIS_ENGINE = "AxisEngine";
    private String webInfPath = null;
    private String homeDir = null;
    private boolean isDevelopment;
    private static final String INIT_PROPERTY_DEVELOPMENT_SYSTEM = "axis.development.system";
    static /* synthetic */ Class class$org$apache$axis$transport$http$AxisServlet;

    public void init() throws ServletException {
        ServletContext context = this.getServletConfig().getServletContext();
        this.webInfPath = context.getRealPath("/WEB-INF");
        this.homeDir = context.getRealPath("/");
        isDebug = log.isDebugEnabled();
        if (log.isDebugEnabled()) {
            log.debug((Object)"In AxisServletBase init");
        }
        this.isDevelopment = JavaUtils.isTrueExplicitly(this.getOption(context, INIT_PROPERTY_DEVELOPMENT_SYSTEM, null));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void destroy() {
        super.destroy();
        if (this.axisServer != null) {
            AxisServer axisServer = this.axisServer;
            synchronized (axisServer) {
                if (this.axisServer != null) {
                    this.axisServer.cleanup();
                    this.axisServer = null;
                    AxisServletBase.storeEngine(this, null);
                }
            }
        }
    }

    public AxisServer getEngine() throws AxisFault {
        if (this.axisServer == null) {
            this.axisServer = AxisServletBase.getEngine(this);
        }
        return this.axisServer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static AxisServer getEngine(HttpServlet servlet) throws AxisFault {
        ServletContext context;
        AxisServer engine = null;
        if (isDebug) {
            log.debug((Object)"Enter: getEngine()");
        }
        ServletContext servletContext = context = servlet.getServletContext();
        synchronized (servletContext) {
            engine = AxisServletBase.retrieveEngine(servlet);
            if (engine == null) {
                Map environment = AxisServletBase.getEngineEnvironment(servlet);
                engine = AxisServer.getServer(environment);
                engine.setName(servlet.getServletName());
                AxisServletBase.storeEngine(servlet, engine);
            }
        }
        if (isDebug) {
            log.debug((Object)"Exit: getEngine()");
        }
        return engine;
    }

    private static void storeEngine(HttpServlet servlet, AxisServer engine) {
        ServletContext context = servlet.getServletContext();
        String axisServletName = servlet.getServletName();
        if (engine == null) {
            context.removeAttribute(axisServletName + ATTR_AXIS_ENGINE);
            AxisServer server = (AxisServer)context.getAttribute(ATTR_AXIS_ENGINE);
            if (server != null && servlet.getServletName().equals(server.getName())) {
                context.removeAttribute(ATTR_AXIS_ENGINE);
            }
        } else {
            if (context.getAttribute(ATTR_AXIS_ENGINE) == null) {
                context.setAttribute(ATTR_AXIS_ENGINE, (Object)engine);
            }
            context.setAttribute(axisServletName + ATTR_AXIS_ENGINE, (Object)engine);
        }
    }

    private static AxisServer retrieveEngine(HttpServlet servlet) {
        Object contextObject = servlet.getServletContext().getAttribute(servlet.getServletName() + ATTR_AXIS_ENGINE);
        if (contextObject == null) {
            contextObject = servlet.getServletContext().getAttribute(ATTR_AXIS_ENGINE);
        }
        if (contextObject instanceof AxisServer) {
            AxisServer server = (AxisServer)contextObject;
            if (server != null && servlet.getServletName().equals(server.getName())) {
                return server;
            }
            return null;
        }
        return null;
    }

    protected static Map getEngineEnvironment(HttpServlet servlet) {
        EngineConfiguration config;
        HashMap<String, Object> environment = new HashMap<String, Object>();
        String attdir = servlet.getInitParameter("axis.attachments.Directory");
        if (attdir != null) {
            environment.put("axis.attachments.Directory", attdir);
        }
        ServletContext context = servlet.getServletContext();
        environment.put("servletContext", context);
        String webInfPath = context.getRealPath("/WEB-INF");
        if (webInfPath != null) {
            environment.put("servlet.realpath", webInfPath + File.separator + "attachments");
        }
        if ((config = EngineConfigurationFactoryFinder.newFactory(servlet).getServerEngineConfig()) != null) {
            environment.put("engineConfig", config);
        }
        return environment;
    }

    public static int getLoadCounter() {
        return loadCounter;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected static void incLockCounter() {
        Object object = loadCounterLock;
        synchronized (object) {
            ++loadCounter;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected static void decLockCounter() {
        Object object = loadCounterLock;
        synchronized (object) {
            --loadCounter;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AxisServletBase.incLockCounter();
        try {
            super.service(req, resp);
        }
        finally {
            AxisServletBase.decLockCounter();
        }
    }

    protected String getWebappBase(HttpServletRequest request) {
        StringBuffer baseURL = new StringBuffer(128);
        baseURL.append(request.getScheme());
        baseURL.append("://");
        baseURL.append(request.getServerName());
        if (request.getServerPort() != 80) {
            baseURL.append(":");
            baseURL.append(request.getServerPort());
        }
        baseURL.append(request.getContextPath());
        return baseURL.toString();
    }

    public ServletContext getServletContext() {
        return this.getServletConfig().getServletContext();
    }

    protected String getWebInfPath() {
        return this.webInfPath;
    }

    protected String getHomeDir() {
        return this.homeDir;
    }

    protected String getOption(ServletContext context, String param, String dephault) {
        String value = AxisProperties.getProperty(param);
        if (value == null) {
            value = this.getInitParameter(param);
        }
        if (value == null) {
            value = context.getInitParameter(param);
        }
        try {
            AxisServer engine = AxisServletBase.getEngine(this);
            if (value == null && engine != null) {
                value = (String)engine.getOption(param);
            }
        }
        catch (AxisFault axisFault) {
            // empty catch block
        }
        return value != null ? value : dephault;
    }

    public boolean isDevelopment() {
        return this.isDevelopment;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

