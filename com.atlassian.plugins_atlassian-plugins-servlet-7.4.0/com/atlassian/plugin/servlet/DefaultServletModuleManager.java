/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.PluginException
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.event.PluginEventListener
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkShutdownEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkShuttingDownEvent
 *  com.atlassian.plugin.event.events.PluginFrameworkStartedEvent
 *  com.atlassian.plugin.scope.ScopeManager
 *  com.atlassian.plugin.util.ClassLoaderStack
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  io.atlassian.util.concurrent.LazyReference
 *  javax.servlet.DispatcherType
 *  javax.servlet.Filter
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  org.dom4j.Element
 *  org.dom4j.dom.DOMElement
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.servlet;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.PluginException;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.event.PluginEventListener;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginFrameworkShutdownEvent;
import com.atlassian.plugin.event.events.PluginFrameworkShuttingDownEvent;
import com.atlassian.plugin.event.events.PluginFrameworkStartedEvent;
import com.atlassian.plugin.scope.ScopeManager;
import com.atlassian.plugin.servlet.DelegatingPluginServlet;
import com.atlassian.plugin.servlet.FilterFactory;
import com.atlassian.plugin.servlet.PluginServletConfig;
import com.atlassian.plugin.servlet.PluginServletContextWrapper;
import com.atlassian.plugin.servlet.ServletModuleManager;
import com.atlassian.plugin.servlet.descriptors.ServletContextListenerModuleDescriptor;
import com.atlassian.plugin.servlet.descriptors.ServletContextParamModuleDescriptor;
import com.atlassian.plugin.servlet.descriptors.ServletFilterModuleDescriptor;
import com.atlassian.plugin.servlet.descriptors.ServletModuleDescriptor;
import com.atlassian.plugin.servlet.filter.FilterDispatcherCondition;
import com.atlassian.plugin.servlet.filter.FilterLocation;
import com.atlassian.plugin.servlet.filter.PluginFilterConfig;
import com.atlassian.plugin.servlet.util.DefaultPathMapper;
import com.atlassian.plugin.servlet.util.PathMapper;
import com.atlassian.plugin.servlet.util.ServletContextServletModuleManagerAccessor;
import com.atlassian.plugin.util.ClassLoaderStack;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import io.atlassian.util.concurrent.LazyReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultServletModuleManager
implements ServletModuleManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultServletModuleManager.class);
    private final PathMapper servletMapper;
    private final Map<String, ServletModuleDescriptor> servletDescriptors = new ConcurrentHashMap<String, ServletModuleDescriptor>();
    private final ConcurrentMap<String, LazyReference<HttpServlet>> servletRefs = new ConcurrentHashMap<String, LazyReference<HttpServlet>>();
    private final PathMapper filterMapper;
    private final Map<String, ServletFilterModuleDescriptor> filterDescriptors = new ConcurrentHashMap<String, ServletFilterModuleDescriptor>();
    private final ConcurrentMap<String, LazyReference<Filter>> filterRefs = new ConcurrentHashMap<String, LazyReference<Filter>>();
    private final FilterFactory filterFactory;
    private final ConcurrentMap<Plugin, ContextLifecycleReference> pluginContextRefs = new ConcurrentHashMap<Plugin, ContextLifecycleReference>();
    private final AtomicReference<PluginController> pluginControllerRef = new AtomicReference();

    public DefaultServletModuleManager(ServletContext servletContext, PluginEventManager pluginEventManager) {
        this(pluginEventManager, new DefaultPathMapper(), new DefaultPathMapper(), new FilterFactory());
        ServletContextServletModuleManagerAccessor.setServletModuleManager(servletContext, this);
    }

    @Deprecated
    public DefaultServletModuleManager(ServletContext servletContext, PluginEventManager pluginEventManager, ScopeManager scopeManager) {
        this(servletContext, pluginEventManager);
    }

    public DefaultServletModuleManager(PluginEventManager pluginEventManager) {
        this(pluginEventManager, new DefaultPathMapper(), new DefaultPathMapper(), new FilterFactory());
    }

    @Deprecated
    public DefaultServletModuleManager(PluginEventManager pluginEventManager, ScopeManager scopeManager) {
        this(pluginEventManager);
    }

    public DefaultServletModuleManager(PluginEventManager pluginEventManager, PathMapper servletPathMapper, PathMapper filterPathMapper) {
        this(pluginEventManager, servletPathMapper, filterPathMapper, new FilterFactory());
    }

    public DefaultServletModuleManager(PluginEventManager pluginEventManager, PathMapper servletMapper, PathMapper filterMapper, FilterFactory filterFactory) {
        this.servletMapper = servletMapper;
        this.filterMapper = filterMapper;
        this.filterFactory = filterFactory;
        pluginEventManager.register((Object)this);
    }

    @Deprecated
    public DefaultServletModuleManager(PluginEventManager pluginEventManager, PathMapper servletMapper, PathMapper filterMapper, FilterFactory filterFactory, ScopeManager scopeManager) {
        this(pluginEventManager, servletMapper, filterMapper, filterFactory);
    }

    @Deprecated
    public DefaultServletModuleManager(ServletContext servletContext, PluginEventManager pluginEventManager, PathMapper servletMapper, PathMapper filterMapper, FilterFactory filterFactory, ScopeManager scopeManager) {
        this(pluginEventManager, servletMapper, filterMapper, filterFactory, scopeManager);
        ServletContextServletModuleManagerAccessor.setServletModuleManager(servletContext, this);
    }

    @Override
    public void addServletModule(ServletModuleDescriptor descriptor) {
        this.servletDescriptors.put(descriptor.getCompleteKey(), descriptor);
        List<String> paths = descriptor.getPaths();
        for (String path : paths) {
            this.servletMapper.put(descriptor.getCompleteKey(), path);
        }
        LazyReference servletRef = (LazyReference)this.servletRefs.remove(descriptor.getCompleteKey());
        if (servletRef != null) {
            ((HttpServlet)servletRef.get()).destroy();
        }
    }

    @Override
    public HttpServlet getServlet(String path, ServletConfig servletConfig) throws ServletException {
        String completeKey = this.servletMapper.get(path);
        if (completeKey == null) {
            return null;
        }
        ServletModuleDescriptor descriptor = this.servletDescriptors.get(completeKey);
        if (descriptor == null) {
            return null;
        }
        HttpServlet servlet = this.getServlet(descriptor, servletConfig);
        if (servlet == null) {
            this.servletRefs.remove(descriptor.getCompleteKey());
        }
        return servlet;
    }

    @Override
    public void removeServletModule(ServletModuleDescriptor descriptor) {
        this.servletDescriptors.remove(descriptor.getCompleteKey());
        this.servletMapper.put(descriptor.getCompleteKey(), null);
        LazyReference servletRef = (LazyReference)this.servletRefs.remove(descriptor.getCompleteKey());
        if (servletRef != null) {
            ((HttpServlet)servletRef.get()).destroy();
        }
    }

    @Override
    public void addFilterModule(ServletFilterModuleDescriptor descriptor) {
        this.filterDescriptors.put(descriptor.getCompleteKey(), descriptor);
        for (String path : descriptor.getPaths()) {
            this.filterMapper.put(descriptor.getCompleteKey(), path);
        }
        LazyReference filterRef = (LazyReference)this.filterRefs.remove(descriptor.getCompleteKey());
        if (filterRef != null) {
            ((Filter)filterRef.get()).destroy();
        }
    }

    @Override
    public Iterable<Filter> getFilters(FilterLocation location, String path, FilterConfig filterConfig, FilterDispatcherCondition condition) {
        return this.getFilters(location, path, filterConfig, condition.toDispatcherType());
    }

    @Override
    public Iterable<Filter> getFilters(FilterLocation location, String path, FilterConfig filterConfig, DispatcherType dispatcher) {
        Preconditions.checkNotNull((Object)dispatcher);
        ArrayList<ServletFilterModuleDescriptor> matchingFilterDescriptors = new ArrayList<ServletFilterModuleDescriptor>();
        for (String completeKey : this.filterMapper.getAll(path)) {
            ServletFilterModuleDescriptor descriptor = this.filterDescriptors.get(completeKey);
            if (!descriptor.getDispatcherTypes().contains(dispatcher)) {
                if (!log.isTraceEnabled()) continue;
                log.trace("Skipping filter {} as dispatcher {} doesn't match list: {}", new Object[]{descriptor.getCompleteKey(), dispatcher, descriptor.getDispatcherTypes()});
                continue;
            }
            if (!location.equals((Object)descriptor.getLocation())) continue;
            matchingFilterDescriptors.add(descriptor);
        }
        List scopedFilterDescriptors = matchingFilterDescriptors.stream().sorted(ServletFilterModuleDescriptor.byWeight).collect(Collectors.toList());
        LinkedList<Filter> filters = new LinkedList<Filter>();
        for (ServletFilterModuleDescriptor descriptor : scopedFilterDescriptors) {
            Filter filter = this.getFilter(descriptor, filterConfig);
            if (filter == null) {
                this.filterRefs.remove(descriptor.getCompleteKey());
                continue;
            }
            filters.add(filter);
        }
        return filters;
    }

    @Override
    public void removeFilterModule(ServletFilterModuleDescriptor descriptor) {
        this.filterDescriptors.remove(descriptor.getCompleteKey());
        this.filterMapper.put(descriptor.getCompleteKey(), null);
        LazyReference filterRef = (LazyReference)this.filterRefs.remove(descriptor.getCompleteKey());
        if (filterRef != null) {
            ((Filter)filterRef.get()).destroy();
        }
    }

    @Override
    public void addServlet(Plugin plugin, String servletName, String className) {
        Element e = this.createServletModuleElement(servletName);
        e.addAttribute("class", className);
        this.pluginControllerRef.get().addDynamicModule(plugin, e);
    }

    @Override
    public void addServlet(Plugin plugin, String servletName, HttpServlet servlet, ServletContext servletContext) {
        Element e = this.createServletModuleElement(servletName);
        ModuleDescriptor moduleDescriptor = this.pluginControllerRef.get().addDynamicModule(plugin, e);
        if (!(moduleDescriptor instanceof ServletModuleDescriptor)) {
            throw new PluginException("expected com.atlassian.plugin.PluginController#addDynamicModule(com.atlassian.plugin.Plugin, org.dom4j.Element)} to return an instance of com.atlassian.plugin.servlet.descriptors.ServletModuleDescriptor; a " + (moduleDescriptor == null ? null : moduleDescriptor.getClass()) + " was returned");
        }
        LazyLoadedServletReference servletRef = new LazyLoadedServletReference(servlet, (ServletModuleDescriptor)moduleDescriptor, servletContext);
        if (this.servletRefs.putIfAbsent(moduleDescriptor.getCompleteKey(), servletRef) != null) {
            this.pluginControllerRef.get().removeDynamicModule(plugin, moduleDescriptor);
            throw new IllegalStateException("a servlet with atlassian-plugins module key '" + moduleDescriptor.getCompleteKey() + "' has already been registered");
        }
    }

    @PluginEventListener
    public void onPluginFrameworkStartingEvent(PluginFrameworkStartedEvent event) {
        this.pluginControllerRef.set(event.getPluginController());
    }

    @PluginEventListener
    public void onPluginFrameworkShutdownEvent(PluginFrameworkShutdownEvent event) {
        if (this.pluginControllerRef.getAndSet(null) != event.getPluginController()) {
            log.warn("PluginController passed via the PluginFrameworkShutdownEvent did not match that passed via PluginFrameworkStartedEvent");
        }
    }

    @PluginEventListener
    public void onPluginDisabled(PluginDisabledEvent event) {
        Plugin plugin = event.getPlugin();
        ContextLifecycleReference context = (ContextLifecycleReference)((Object)this.pluginContextRefs.remove(plugin));
        if (context == null) {
            return;
        }
        ((ContextLifecycleManager)context.get()).contextDestroyed();
    }

    @PluginEventListener
    public void onPluginFrameworkBeforeShutdown(PluginFrameworkShuttingDownEvent event) {
        this.destroy();
    }

    private void destroy() {
        this.destroyModuleDescriptors(this.servletDescriptors);
        this.destroyModuleDescriptors(this.filterDescriptors);
        for (ContextLifecycleReference context : new ArrayList(this.pluginContextRefs.values())) {
            ContextLifecycleManager lifecycleManager;
            if (context == null || (lifecycleManager = (ContextLifecycleManager)context.get()) == null) continue;
            lifecycleManager.contextDestroyed();
        }
        this.pluginContextRefs.clear();
    }

    private <T extends ModuleDescriptor> void destroyModuleDescriptors(Map<String, T> descriptors) {
        for (ModuleDescriptor moduleDescriptor : new ArrayList<T>(descriptors.values())) {
            if (moduleDescriptor == null) continue;
            moduleDescriptor.destroy();
        }
        descriptors.clear();
    }

    HttpServlet getServlet(ServletModuleDescriptor descriptor, ServletConfig servletConfig) {
        return this.getInstance(this.servletRefs, descriptor, new LazyLoadedServletReference(null, descriptor, servletConfig.getServletContext()));
    }

    Filter getFilter(ServletFilterModuleDescriptor descriptor, FilterConfig filterConfig) {
        return this.getInstance(this.filterRefs, descriptor, new LazyLoadedFilterReference(descriptor, filterConfig));
    }

    private <T> T getInstance(ConcurrentMap<String, LazyReference<T>> refs, AbstractModuleDescriptor descriptor, LazyReference<T> newRef) {
        try {
            LazyReference<T> oldRef = refs.putIfAbsent(descriptor.getCompleteKey(), newRef);
            return (T)(oldRef != null ? oldRef.get() : newRef.get());
        }
        catch (RuntimeException ex) {
            log.error("Unable to create new reference " + newRef, (Throwable)ex);
            return null;
        }
    }

    private ServletContext getWrappedContext(Plugin plugin, ServletContext baseContext) {
        ContextLifecycleReference pluginContextRef = (ContextLifecycleReference)((Object)this.pluginContextRefs.get(plugin));
        if (pluginContextRef == null && this.pluginContextRefs.putIfAbsent(plugin, pluginContextRef = new ContextLifecycleReference(this, plugin, baseContext)) != null) {
            pluginContextRef = (ContextLifecycleReference)((Object)this.pluginContextRefs.get(plugin));
        }
        return ((ContextLifecycleManager)pluginContextRef.get()).servletContext;
    }

    private Element createServletModuleElement(String servletName) {
        DOMElement e = new DOMElement("servlet");
        e.addAttribute("key", servletName + "-servlet");
        e.addAttribute("name", servletName + "Servlet");
        DOMElement url = new DOMElement("url-pattern");
        url.setText("/" + servletName);
        e.add((Element)url);
        return e;
    }

    @VisibleForTesting
    ImmutableMap<String, LazyReference<HttpServlet>> getServletRefs() {
        return ImmutableMap.copyOf(this.servletRefs);
    }

    static <T extends ModuleDescriptor<?>> Iterable<T> findModuleDescriptorsByType(Class<T> type, Plugin plugin) {
        HashSet<T> descriptors = new HashSet<T>();
        for (ModuleDescriptor descriptor : plugin.getModuleDescriptors()) {
            if (!type.isAssignableFrom(descriptor.getClass())) continue;
            descriptors.add(type.cast(descriptor));
        }
        return descriptors;
    }

    static final class ContextLifecycleManager {
        private final ServletContext servletContext;
        private final Iterable<ServletContextListener> listeners;

        ContextLifecycleManager(ServletContext servletContext, Iterable<ServletContextListener> listeners) {
            this.servletContext = servletContext;
            this.listeners = listeners;
            for (ServletContextListener listener : listeners) {
                listener.contextInitialized(new ServletContextEvent(servletContext));
            }
        }

        ServletContext getServletContext() {
            return this.servletContext;
        }

        void contextDestroyed() {
            ServletContextEvent event = new ServletContextEvent(this.servletContext);
            for (ServletContextListener listener : this.listeners) {
                listener.contextDestroyed(event);
            }
        }
    }

    private static final class ContextLifecycleReference
    extends LazyReference<ContextLifecycleManager> {
        private final ServletModuleManager servletModuleManager;
        private final Plugin plugin;
        private final ServletContext baseContext;

        private ContextLifecycleReference(ServletModuleManager servletModuleManager, Plugin plugin, ServletContext baseContext) {
            this.servletModuleManager = servletModuleManager;
            this.plugin = plugin;
            this.baseContext = baseContext;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected ContextLifecycleManager create() {
            ConcurrentHashMap<String, Object> contextAttributes = new ConcurrentHashMap<String, Object>();
            Map<String, String> initParams = this.mergeInitParams(this.baseContext, this.plugin);
            PluginServletContextWrapper context = new PluginServletContextWrapper(this.servletModuleManager, this.plugin, this.baseContext, contextAttributes, initParams);
            ClassLoaderStack.push((ClassLoader)this.plugin.getClassLoader());
            ArrayList<ServletContextListener> listeners = new ArrayList<ServletContextListener>();
            try {
                for (ServletContextListenerModuleDescriptor descriptor : DefaultServletModuleManager.findModuleDescriptorsByType(ServletContextListenerModuleDescriptor.class, this.plugin)) {
                    listeners.add(descriptor.getModule());
                }
            }
            finally {
                ClassLoaderStack.pop();
            }
            return new ContextLifecycleManager(context, listeners);
        }

        private Map<String, String> mergeInitParams(ServletContext baseContext, Plugin plugin) {
            HashMap<String, String> mergedInitParams = new HashMap<String, String>();
            Enumeration e = baseContext.getInitParameterNames();
            while (e.hasMoreElements()) {
                String paramName = (String)e.nextElement();
                mergedInitParams.put(paramName, baseContext.getInitParameter(paramName));
            }
            for (ServletContextParamModuleDescriptor descriptor : DefaultServletModuleManager.findModuleDescriptorsByType(ServletContextParamModuleDescriptor.class, plugin)) {
                mergedInitParams.put(descriptor.getParamName(), descriptor.getParamValue());
            }
            return Collections.unmodifiableMap(mergedInitParams);
        }
    }

    @VisibleForTesting
    final class LazyLoadedServletReference
    extends LazyReference<HttpServlet> {
        private HttpServlet servlet;
        private final ServletModuleDescriptor descriptor;
        private final ServletContext servletContext;

        private LazyLoadedServletReference(HttpServlet servlet, ServletModuleDescriptor descriptor, ServletContext servletContext) {
            this.servlet = servlet;
            this.descriptor = descriptor;
            this.servletContext = servletContext;
        }

        protected HttpServlet create() throws Exception {
            if (this.servlet == null) {
                this.servlet = new DelegatingPluginServlet(this.descriptor);
            }
            ServletContext wrappedContext = DefaultServletModuleManager.this.getWrappedContext(this.descriptor.getPlugin(), this.servletContext);
            this.servlet.init((ServletConfig)new PluginServletConfig(this.descriptor, wrappedContext));
            return this.servlet;
        }

        public String toString() {
            return MoreObjects.toStringHelper((Object)((Object)this)).add("descriptor", (Object)this.descriptor).add("servletContext", (Object)this.servletContext).toString();
        }
    }

    private final class LazyLoadedFilterReference
    extends LazyReference<Filter> {
        private final ServletFilterModuleDescriptor descriptor;
        private final FilterConfig filterConfig;

        private LazyLoadedFilterReference(ServletFilterModuleDescriptor descriptor, FilterConfig filterConfig) {
            this.descriptor = descriptor;
            this.filterConfig = filterConfig;
        }

        protected Filter create() throws Exception {
            Filter filter = DefaultServletModuleManager.this.filterFactory.newFilter(this.descriptor);
            ServletContext servletContext = DefaultServletModuleManager.this.getWrappedContext(this.descriptor.getPlugin(), this.filterConfig.getServletContext());
            filter.init((FilterConfig)new PluginFilterConfig(this.descriptor, servletContext));
            return filter;
        }

        public String toString() {
            return MoreObjects.toStringHelper((Object)((Object)this)).add("descriptor", (Object)this.descriptor).add("filterConfig", (Object)this.filterConfig).toString();
        }
    }
}

