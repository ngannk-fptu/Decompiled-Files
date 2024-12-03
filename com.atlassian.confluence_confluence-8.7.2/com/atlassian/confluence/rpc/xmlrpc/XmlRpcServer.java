/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletInputStream
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 *  org.apache.struts2.ServletActionContext
 *  org.apache.xmlrpc.SurrogatePairCapableXmlRpcServer
 *  org.apache.xmlrpc.XmlRpc
 *  org.apache.xmlrpc.XmlRpcServer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.rpc.xmlrpc;

import com.atlassian.confluence.plugin.descriptor.rpc.XmlRpcModuleDescriptor;
import com.atlassian.confluence.rpc.RpcServer;
import com.atlassian.confluence.rpc.auth.TokenAuthenticationInvocationHandler;
import com.atlassian.confluence.rpc.xmlrpc.SafeXMLParser;
import com.atlassian.confluence.servlet.ServletManager;
import com.atlassian.confluence.servlet.SpringManagedServlet;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.plugin.PluginAccessor;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import org.apache.xmlrpc.SurrogatePairCapableXmlRpcServer;
import org.apache.xmlrpc.XmlRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlRpcServer
implements ServletManager,
RpcServer {
    private static final Logger log = LoggerFactory.getLogger(XmlRpcServer.class);
    private org.apache.xmlrpc.XmlRpcServer xmlrpc;
    private PluginAccessor pluginAccessor;
    private SettingsManager settingsManager;

    private static void configureSaxDriver() {
        XmlRpc.setDriver(SafeXMLParser.class);
    }

    @Override
    public void reloadConfiguration() {
        if (this.xmlrpc == null) {
            log.debug("Deferring XML-RPC reload until first request");
            return;
        }
        log.info("Removing RPC handler for reload");
        this.xmlrpc = null;
    }

    @Override
    public void servletDestroyed(SpringManagedServlet springManagedServlet) {
    }

    @Override
    public void service(SpringManagedServlet springManagedServlet, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!this.settingsManager.getGlobalSettings().isAllowRemoteApi()) {
            response.sendError(403, "Remote API is not enabled on this server. Ask a site administrator to enable it.");
            return;
        }
        if (request.getMethod().equals("GET") || request.getMethod().equals("POST")) {
            try {
                this.serviceXmlRpcRequest(request, response);
            }
            catch (Exception e) {
                log.error("Exception servicing XML-RPC request: " + e, (Throwable)e);
            }
        } else {
            response.sendError(405, "XML-RPC only supports GET or POST requests");
        }
    }

    private void serviceXmlRpcRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            if (this.xmlrpc == null) {
                this.loadRpcHandler();
            }
            ServletActionContext.setRequest((HttpServletRequest)request);
            ServletActionContext.setResponse((HttpServletResponse)response);
            try (ServletInputStream is = request.getInputStream();){
                byte[] result = this.xmlrpc.execute((InputStream)is);
                response.setContentType("text/xml");
                response.setContentLength(result.length);
                ServletOutputStream out = response.getOutputStream();
                out.write(result);
                out.flush();
            }
        }
        catch (RuntimeException e) {
            throw new ServletException((Throwable)e);
        }
        finally {
            HttpSession session;
            ServletActionContext.setRequest(null);
            ServletActionContext.setResponse(null);
            if (Boolean.getBoolean("confluence.invalidate.rpc.sessions") && (session = request.getSession(false)) != null) {
                session.invalidate();
            }
        }
    }

    @Override
    public void servletInitialised(SpringManagedServlet springManagedServlet, ServletConfig servletConfig) throws ServletException {
        log.info("Initialising XML-RPC service (servlet init)");
        this.loadRpcHandler();
    }

    private void loadRpcHandler() {
        log.info("Loading XML-RPC handlers");
        this.xmlrpc = new SurrogatePairCapableXmlRpcServer();
        List handlers = this.pluginAccessor.getEnabledModuleDescriptorsByClass(XmlRpcModuleDescriptor.class);
        for (XmlRpcModuleDescriptor descriptor : handlers) {
            log.info("Adding handler: {} ({})", (Object)descriptor.getServicePath(), (Object)descriptor.getCompleteKey());
            try {
                Object handler = descriptor.getModule();
                if (descriptor.isAuthenticated()) {
                    handler = TokenAuthenticationInvocationHandler.makeAuthenticatingProxy(handler, descriptor.getPublishedInterface());
                }
                this.xmlrpc.addHandler(descriptor.getServicePath(), handler);
            }
            catch (Exception e) {
                log.error("Unable to add XML-RPC handler: {} - {}", (Object)descriptor.getCompleteKey(), (Object)e.getMessage());
                log.error("", (Throwable)e);
            }
        }
    }

    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    static {
        XmlRpcServer.configureSaxDriver();
    }
}

