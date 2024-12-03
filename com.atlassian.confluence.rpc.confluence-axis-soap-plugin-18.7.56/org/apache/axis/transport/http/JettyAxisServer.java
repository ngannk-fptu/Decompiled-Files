/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.mortbay.http.HttpContext
 *  org.mortbay.http.HttpHandler
 *  org.mortbay.http.HttpListener
 *  org.mortbay.http.HttpServer
 *  org.mortbay.http.SocketListener
 *  org.mortbay.http.handler.ResourceHandler
 *  org.mortbay.jetty.servlet.ServletHandler
 */
package org.apache.axis.transport.http;

import java.net.MalformedURLException;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.Options;
import org.apache.commons.logging.Log;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpHandler;
import org.mortbay.http.HttpListener;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.jetty.servlet.ServletHandler;

public class JettyAxisServer {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$transport$http$JettyAxisServer == null ? (class$org$apache$axis$transport$http$JettyAxisServer = JettyAxisServer.class$("org.apache.axis.transport.http.JettyAxisServer")) : class$org$apache$axis$transport$http$JettyAxisServer).getName());
    HttpServer server = new HttpServer();
    SocketListener listener = new SocketListener();
    HttpContext context = new HttpContext();
    static /* synthetic */ Class class$org$apache$axis$transport$http$JettyAxisServer;

    public JettyAxisServer() {
        this.context.setContextPath("/axis/*");
        this.server.addContext(this.context);
        ServletHandler servlets = new ServletHandler();
        this.context.addHandler((HttpHandler)servlets);
        servlets.addServlet("AdminServlet", "/servlet/AdminServlet", "org.apache.axis.transport.http.AdminServlet");
        servlets.addServlet("AxisServlet", "/servlet/AxisServlet", "org.apache.axis.transport.http.AxisServlet");
        servlets.addServlet("AxisServlet", "/services/*", "org.apache.axis.transport.http.AxisServlet");
        servlets.addServlet("AxisServlet", "*.jws", "org.apache.axis.transport.http.AxisServlet");
        this.context.addHandler((HttpHandler)new ResourceHandler());
    }

    public void setPort(int port) {
        this.listener.setPort(port);
        this.server.addListener((HttpListener)this.listener);
    }

    public void setResourceBase(String dir) {
        this.context.setResourceBase(dir);
    }

    public void start() throws Exception {
        this.server.start();
        log.info((Object)Messages.getMessage("start00", "JettyAxisServer", new Integer(this.listener.getServerSocket().getLocalPort()).toString()));
    }

    public static void main(String[] args) {
        Options opts = null;
        try {
            opts = new Options(args);
        }
        catch (MalformedURLException e) {
            log.error((Object)Messages.getMessage("malformedURLException00"), (Throwable)e);
            return;
        }
        JettyAxisServer server = new JettyAxisServer();
        server.setPort(opts.getPort());
        String dir = opts.isValueSet('d');
        if (dir == null) {
            dir = System.getProperty("jetty.home", ".") + "/webapps/axis/";
        }
        server.setResourceBase(dir);
        try {
            server.start();
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
        }
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

