/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceSystemProperties
 *  com.atlassian.confluence.plugin.descriptor.rpc.SoapModuleDescriptor
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.util.profiling.ProfilingSiteMeshFilter
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.rpc.axis;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.plugin.descriptor.rpc.SoapModuleDescriptor;
import com.atlassian.confluence.rpc.axis.AxisServletHack;
import com.atlassian.confluence.rpc.axis.ConfluenceAxisServerFactory;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.profiling.ProfilingSiteMeshFilter;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.axis.AxisProperties;
import org.apache.axis.encoding.TypeMappingImpl;
import org.apache.axis.transport.http.AxisServlet;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AxisSoapServer
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(AxisSoapServer.class);
    private volatile AxisServlet axisServlet;
    private final SettingsManager settingsManager;
    private final PluginAccessor pluginAccessor;
    private final TransactionTemplate transactionTemplate;
    private final EventPublisher eventPublisher;
    private ServletConfig cachedServletConfig;

    public AxisSoapServer(@ComponentImport SettingsManager settingsManager, @ComponentImport PluginAccessor pluginAccessor, @ComponentImport EventPublisher eventPublisher, @ComponentImport TransactionTemplate transactionTemplate) {
        this.settingsManager = settingsManager;
        this.pluginAccessor = pluginAccessor;
        this.transactionTemplate = transactionTemplate;
        this.eventPublisher = eventPublisher;
        eventPublisher.register((Object)this);
    }

    @EventListener
    public void onModuleEnabled(PluginModuleEnabledEvent event) {
        if (event.getModule() instanceof SoapModuleDescriptor) {
            log.debug("Received module enabled event for SoapModuleDescriptor: " + event.getModule().getCompleteKey());
            this.destroyAxisServlet();
        }
    }

    @EventListener
    public void onModuleDisabled(PluginModuleDisabledEvent event) {
        if (event.getModule() instanceof SoapModuleDescriptor) {
            log.debug("Received module disabled event for SoapModuleDescriptor: " + event.getModule().getCompleteKey());
        }
        this.destroyAxisServlet();
    }

    public void destroy() {
        log.info("Terminating AXIS SOAP service (servlet destroyed)");
        this.eventPublisher.unregister((Object)this);
        this.destroyAxisServlet();
        super.destroy();
    }

    private void destroyAxisServlet() {
        if (this.axisServlet == null) {
            log.debug("AXIS soap service hasn't been initialised yet");
        } else {
            log.debug("Shutting down AXIS servlet");
            this.axisServlet.destroy();
            this.axisServlet = null;
        }
    }

    public void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        Exception oops = (Exception)this.transactionTemplate.execute((TransactionCallback)new TransactionCallback<Exception>(){

            public Exception doInTransaction() {
                try {
                    AxisSoapServer.this.serviceInTransaction(request, response);
                    return null;
                }
                catch (Exception e) {
                    return e;
                }
            }
        });
        if (oops instanceof ServletException) {
            throw (ServletException)oops;
        }
        if (oops instanceof IOException) {
            throw (IOException)oops;
        }
        if (oops instanceof RuntimeException) {
            throw (RuntimeException)oops;
        }
        if (oops != null) {
            throw new ServletException("Unexpected exception during SOAP service: " + oops, (Throwable)oops);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void serviceInTransaction(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!this.settingsManager.getGlobalSettings().isAllowRemoteApi()) {
            response.sendError(403, "Remote API is not enabled on this server. Ask a site administrator to enable it.");
            return;
        }
        if (this.cachedServletConfig == null) {
            throw new ServletException("AXIS SOAP service has not been initialised");
        }
        ClassLoader prevClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            if (this.axisServlet == null) {
                this.recycleAxisServlet(this.cachedServletConfig);
            }
            ServletActionContext.setRequest((HttpServletRequest)request);
            ServletActionContext.setResponse((HttpServletResponse)response);
            Thread.currentThread().setContextClassLoader(this.pluginAccessor.getClassLoader());
            this.axisServlet.service((ServletRequest)request, (ServletResponse)response);
        }
        finally {
            ServletActionContext.setRequest(null);
            ServletActionContext.setResponse(null);
            Thread.currentThread().setContextClassLoader(prevClassLoader);
        }
    }

    public void init(ServletConfig servletConfig) throws ServletException {
        if (this.axisServlet != null) {
            this.destroy();
        }
        log.warn("Initialising AXIS SOAP service (servlet initialised)");
        this.recycleAxisServlet(servletConfig);
        ProfilingSiteMeshFilter.ensureFactorySetup((ServletConfig)servletConfig);
        this.cachedServletConfig = servletConfig;
    }

    private void recycleAxisServlet(ServletConfig servletConfig) throws ServletException {
        ClassLoader prevClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.pluginAccessor.getClassLoader());
            AxisProperties.setProperty("axis.ServerFactory", ConfluenceAxisServerFactory.class.getName());
            AxisProperties.setProperty("axis.doAutoTypes", Boolean.TRUE.toString());
            if (ConfluenceSystemProperties.isDevMode()) {
                AxisProperties.setProperty("axis.development.system", Boolean.TRUE.toString());
            }
            TypeMappingImpl.dotnet_soapenc_bugfix = true;
            log.warn("Initialising AXIS SOAP service (loading)");
            this.removeEngineFromServletContext(servletConfig);
            this.axisServlet = new AxisServlet();
            this.axisServlet.init(servletConfig);
        }
        finally {
            Thread.currentThread().setContextClassLoader(prevClassLoader);
        }
    }

    private void removeEngineFromServletContext(ServletConfig servletConfig) {
        servletConfig.getServletContext().removeAttribute(servletConfig.getServletName() + AxisServletHack.attrAxisEngine);
        servletConfig.getServletContext().removeAttribute(AxisServletHack.attrAxisEngine);
    }
}

