/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleDisabledEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.rpc.jsonrpc;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleDisabledEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.rpc.jsonrpc.SALI18nAdapter;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.voorhees.I18nAdapter;
import com.atlassian.voorhees.JsonRpcHandler;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class JsonRpcFilter<T extends ModuleDescriptor>
implements Filter {
    protected final PluginEventManager pluginEventManager;
    protected final PluginAccessor pluginAccessor;
    protected final I18nAdapter i18nAdapter;
    private ConcurrentMap<String, MappedHandler> handlers = new ConcurrentHashMap<String, MappedHandler>();

    public JsonRpcFilter(PluginEventManager pluginEventManager, PluginAccessor pluginAccessor, I18nResolver i18nResolver) {
        this.pluginEventManager = pluginEventManager;
        this.pluginAccessor = pluginAccessor;
        this.i18nAdapter = new SALI18nAdapter(i18nResolver);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        this.pluginEventManager.register((Object)this);
        List soapModules = this.pluginAccessor.getEnabledModuleDescriptorsByClass(this.getModuleDescriptorClass());
        for (ModuleDescriptor soapModule : soapModules) {
            this.register(soapModule);
        }
    }

    public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) throws IOException, ServletException {
        String uri;
        int contextIndex;
        HttpServletRequest request = (HttpServletRequest)rq;
        HttpServletResponse response = (HttpServletResponse)rs;
        if ("POST".equals(request.getMethod()) && (contextIndex = (uri = request.getRequestURI()).indexOf("/rpc/json-rpc/")) + 14 < uri.length()) {
            String pathPart = uri.substring(contextIndex + 14);
            for (MappedHandler mappedHandler : this.handlers.values()) {
                if (pathPart.equals(mappedHandler.servicePath)) {
                    mappedHandler.handler.process(request, response);
                    return;
                }
                if (!pathPart.startsWith(mappedHandler.servicePath + "/") || pathPart.length() <= mappedHandler.servicePath.length() + 2) continue;
                String methodName = pathPart.substring(mappedHandler.servicePath.length() + 1);
                mappedHandler.handler.process(methodName, request, response);
                return;
            }
        }
        chain.doFilter((ServletRequest)request, (ServletResponse)response);
    }

    @PluginEventListener
    public void onPluginModuleEnabled(PluginModuleEnabledEvent event) {
        if (this.getModuleDescriptorClass().isAssignableFrom(event.getModule().getClass())) {
            this.register(event.getModule());
        }
    }

    @PluginEventListener
    public void onPluginModuleDisabled(PluginModuleDisabledEvent event) {
        this.unregister(event.getModule());
    }

    @PluginEventListener
    public void onPluginDisabled(PluginDisabledEvent event) {
        for (ModuleDescriptor moduleDescriptor : event.getPlugin().getModuleDescriptors()) {
            this.unregister(moduleDescriptor);
        }
    }

    public void destroy() {
        this.pluginEventManager.unregister((Object)this);
    }

    protected abstract Class<T> getModuleDescriptorClass();

    protected abstract void register(T var1);

    protected void register(String pluginModuleKey, String servicePath, JsonRpcHandler handler) {
        this.handlers.put(pluginModuleKey, new MappedHandler(handler, servicePath));
    }

    protected void unregister(ModuleDescriptor moduleDescriptor) {
        this.handlers.remove(moduleDescriptor.getCompleteKey());
    }

    private static class MappedHandler {
        public final JsonRpcHandler handler;
        public final String servicePath;

        private MappedHandler(JsonRpcHandler handler, String servicePath) {
            this.handler = handler;
            this.servicePath = servicePath;
        }
    }
}

