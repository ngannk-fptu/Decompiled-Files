/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.MailException
 *  com.atlassian.mail.server.MailServer
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.mail.server.SMTPMailServer
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.jmx;

import com.atlassian.confluence.jmx.ComponentResolver;
import com.atlassian.confluence.jmx.CurrentTimeFacade;
import com.atlassian.confluence.jmx.MBeanExporterWithUnregister;
import com.atlassian.confluence.jmx.RequestMetrics;
import com.atlassian.mail.MailException;
import com.atlassian.mail.server.MailServer;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.SMTPMailServer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmxFilter
implements Filter {
    private final RequestMetrics requestMetrics = new RequestMetrics();
    private MBeanExporterWithUnregister exporter;
    private static final Logger LOG = LoggerFactory.getLogger(JmxFilter.class);
    private ComponentResolver resolver = ComponentResolver.DEFAULT_RESOLVER;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.registerBeans();
    }

    private void registerBeans() {
        this.exporter = (MBeanExporterWithUnregister)this.getComponentThatCanFail("exporter");
        if (this.exporter == null || !this.exporter.isEnabled()) {
            return;
        }
        this.registerObject(this.requestMetrics, "Confluence:name=RequestMetrics");
        MailServerManager manager = (MailServerManager)this.getComponentThatCanFail("mailServerManager");
        if (manager != null) {
            this.doMailServer(manager);
        }
    }

    private void doMailServer(MailServerManager manager) {
        try {
            List serverNames = manager.getServerNames();
            ArrayList<MailServer> smtpMailServers = new ArrayList<MailServer>();
            for (String serverName : serverNames) {
                MailServer mailServer = manager.getMailServer(serverName);
                if (!(mailServer instanceof SMTPMailServer)) continue;
                smtpMailServers.add(mailServer);
            }
            for (MailServer mailServer : smtpMailServers) {
                this.safeRegisterObject(mailServer, "Confluence:name=MailServer-" + mailServer.getName());
            }
        }
        catch (MailException e) {
            LOG.error("Unable to retrieve mail servers", (Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        long startTime = CurrentTimeFacade.getCurrentTime().getTime();
        this.requestMetrics.beginRequest();
        if (this.exporter == null) {
            this.registerBeans();
        }
        try {
            chain.doFilter(request, response);
        }
        finally {
            long endTime = CurrentTimeFacade.getCurrentTime().getTime();
            this.requestMetrics.endRequest();
            this.requestMetrics.recordRequestTime(endTime - startTime);
        }
    }

    public void destroy() {
    }

    void setResolver(ComponentResolver resolver) {
        this.resolver = resolver;
    }

    void setExporter(MBeanExporterWithUnregister exporter) {
        this.exporter = exporter;
    }

    private Object getComponentThatCanFail(String key) {
        return this.resolver.resolveComponent(key);
    }

    private void safeRegisterObject(Object value, String name) {
        try {
            this.exporter.safeRegisterManagedResource(value, new ObjectName(name));
        }
        catch (MalformedObjectNameException e) {
            LOG.warn("Error registering object : " + name);
        }
    }

    private void registerObject(Object value, String name) {
        try {
            this.exporter.registerManagedResource(value, new ObjectName(name));
        }
        catch (MalformedObjectNameException e) {
            throw new IllegalArgumentException("Error registering object : " + name);
        }
    }

    public RequestMetrics getRequestMetrics() {
        return this.requestMetrics;
    }
}

