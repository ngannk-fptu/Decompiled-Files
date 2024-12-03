/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugin.descriptor.rpc.SoapModuleDescriptor
 *  com.atlassian.confluence.security.access.AccessStatus
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.user.User
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.rpc.jsonrpc;

import com.atlassian.confluence.plugin.descriptor.rpc.SoapModuleDescriptor;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.rpc.jsonrpc.JsonRpcFilter;
import com.atlassian.rpc.jsonrpc.SoapModuleMethodMapper;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.user.User;
import com.atlassian.voorhees.JsonRpcHandler;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceJsonRpcFilter
extends JsonRpcFilter<SoapModuleDescriptor> {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceJsonRpcFilter.class);
    private final GlobalSettingsManager settingsManager;
    private final ConfluenceAccessManager confluenceAccessManager;

    public ConfluenceJsonRpcFilter(@ComponentImport PluginEventManager pluginEventManager, @ComponentImport PluginAccessor pluginAccessor, @ComponentImport I18nResolver i18nResolver, @ComponentImport GlobalSettingsManager settingsManager, @ComponentImport ConfluenceAccessManager confluenceAccessManager) {
        super(pluginEventManager, pluginAccessor, i18nResolver);
        this.settingsManager = settingsManager;
        this.confluenceAccessManager = confluenceAccessManager;
    }

    @Override
    protected void register(SoapModuleDescriptor soapModule) {
        try {
            this.register(soapModule.getCompleteKey(), soapModule.getServicePath(), new JsonRpcHandler(new SoapModuleMethodMapper(soapModule.getModule(), soapModule.getPublishedInterface(), soapModule.isAuthenticated()), this.i18nAdapter));
        }
        catch (ClassNotFoundException e) {
            log.error("Unable to register SOAP module " + soapModule.getCompleteKey() + " because published interface could not be instantiated. " + e.getMessage(), (Throwable)e);
        }
    }

    @Override
    protected Class<SoapModuleDescriptor> getModuleDescriptorClass() {
        return SoapModuleDescriptor.class;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) throws IOException, ServletException {
        boolean isAnonymousUser;
        HttpServletRequest request = (HttpServletRequest)rq;
        HttpServletResponse response = (HttpServletResponse)rs;
        ConfluenceUser remoteUser = AuthenticatedUserThreadLocal.get();
        boolean bl = isAnonymousUser = remoteUser == null;
        if (this.hasApiAccess(isAnonymousUser) && this.canUseConfluence((User)remoteUser)) {
            ServletActionContext.setRequest((HttpServletRequest)request);
            ServletActionContext.setResponse((HttpServletResponse)response);
            try {
                super.doFilter(rq, rs, chain);
            }
            finally {
                ServletActionContext.setRequest(null);
                ServletActionContext.setResponse(null);
            }
        } else if (isAnonymousUser) {
            response.sendError(401);
        } else {
            response.sendError(403);
        }
    }

    private boolean hasApiAccess(boolean isUserAnonymous) {
        return !isUserAnonymous || this.settingsManager.getGlobalSettings().isAllowRemoteApiAnonymous();
    }

    private boolean canUseConfluence(User remoteUser) {
        AccessStatus userAccessStatus = this.confluenceAccessManager.getUserAccessStatus(remoteUser);
        return userAccessStatus.canUseConfluence();
    }
}

