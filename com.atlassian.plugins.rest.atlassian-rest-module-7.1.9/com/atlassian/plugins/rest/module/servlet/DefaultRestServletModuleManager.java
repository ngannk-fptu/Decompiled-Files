/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.servlet.DefaultServletModuleManager
 *  com.atlassian.plugin.servlet.ServletModuleManager
 *  com.atlassian.plugin.servlet.descriptors.ServletFilterModuleDescriptor
 *  com.atlassian.plugin.servlet.descriptors.ServletModuleDescriptor
 *  com.atlassian.plugin.servlet.filter.FilterDispatcherCondition
 *  com.atlassian.plugin.servlet.filter.FilterLocation
 *  com.atlassian.plugin.servlet.util.DefaultPathMapper
 *  com.atlassian.plugin.servlet.util.PathMapper
 *  com.google.common.collect.Multimaps
 *  com.google.common.collect.Ordering
 *  com.google.common.collect.SortedSetMultimap
 *  com.google.common.collect.TreeMultimap
 *  javax.servlet.DispatcherType
 *  javax.servlet.Filter
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.rest.module.servlet;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.servlet.DefaultServletModuleManager;
import com.atlassian.plugin.servlet.ServletModuleManager;
import com.atlassian.plugin.servlet.descriptors.ServletFilterModuleDescriptor;
import com.atlassian.plugin.servlet.descriptors.ServletModuleDescriptor;
import com.atlassian.plugin.servlet.filter.FilterDispatcherCondition;
import com.atlassian.plugin.servlet.filter.FilterLocation;
import com.atlassian.plugin.servlet.util.DefaultPathMapper;
import com.atlassian.plugin.servlet.util.PathMapper;
import com.atlassian.plugins.rest.module.RestServletFilterModuleDescriptor;
import com.atlassian.plugins.rest.module.servlet.RestServletModuleManager;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import java.util.Comparator;
import java.util.SortedSet;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.apache.commons.lang3.StringUtils;

public class DefaultRestServletModuleManager
implements RestServletModuleManager {
    private static final RestServletFilterModuleDescriptorComparator VALUE_COMPARATOR = new RestServletFilterModuleDescriptorComparator();
    private final SortedSetMultimap<String, RestServletFilterModuleDescriptor> filterModuleDescriptors = Multimaps.synchronizedSortedSetMultimap((SortedSetMultimap)TreeMultimap.create((Comparator)Ordering.natural(), (Comparator)VALUE_COMPARATOR));
    private final ServletModuleManager delegateModuleManager;
    private final PathMapper filterPathMapper;
    private final String path;

    public DefaultRestServletModuleManager(PluginEventManager pluginEventManager, String path) {
        this.filterPathMapper = new DefaultPathMapper();
        this.delegateModuleManager = new DefaultServletModuleManager(pluginEventManager, (PathMapper)new DefaultPathMapper(), this.filterPathMapper);
        this.path = StringUtils.isNotBlank((CharSequence)path) ? path : "";
    }

    DefaultRestServletModuleManager(ServletModuleManager delegate, PathMapper filterPathMapper, String path) {
        this.filterPathMapper = filterPathMapper;
        this.delegateModuleManager = delegate;
        this.path = StringUtils.isNotBlank((CharSequence)path) ? path : "";
    }

    public void addServlet(Plugin plugin, String servletName, String className) {
        this.delegateModuleManager.addServlet(plugin, servletName, className);
    }

    public void addServlet(Plugin plugin, String servletName, HttpServlet servlet, ServletContext servletContext) {
        this.delegateModuleManager.addServlet(plugin, servletName, servlet, servletContext);
    }

    public void addServletModule(ServletModuleDescriptor descriptor) {
        this.delegateModuleManager.addServletModule(descriptor);
    }

    public HttpServlet getServlet(String path, ServletConfig servletConfig) throws ServletException {
        return this.delegateModuleManager.getServlet(path, servletConfig);
    }

    public void removeServletModule(ServletModuleDescriptor descriptor) {
        this.delegateModuleManager.removeServletModule(descriptor);
    }

    public void addFilterModule(ServletFilterModuleDescriptor descriptor) {
        if (descriptor instanceof RestServletFilterModuleDescriptor) {
            RestServletFilterModuleDescriptor restServletFilterModuleDescriptor = (RestServletFilterModuleDescriptor)descriptor;
            RestServletFilterModuleDescriptor latest = this.getRestServletFilterModuleDescriptorForLatest(restServletFilterModuleDescriptor.getBasePath());
            if (VALUE_COMPARATOR.compare(latest, restServletFilterModuleDescriptor) < 0) {
                if (latest != null) {
                    this.filterPathMapper.put(latest.getCompleteKey(), null);
                    latest.getPaths().forEach(p -> this.filterPathMapper.put(latest.getCompleteKey(), p));
                }
                this.filterPathMapper.put(descriptor.getCompleteKey(), this.getPathPattern(restServletFilterModuleDescriptor.getBasePath()));
            }
            this.filterModuleDescriptors.put((Object)restServletFilterModuleDescriptor.getBasePath(), (Object)restServletFilterModuleDescriptor);
        }
        this.delegateModuleManager.addFilterModule(descriptor);
    }

    private RestServletFilterModuleDescriptor getRestServletFilterModuleDescriptorForLatest(String path) {
        if (path == null) {
            return null;
        }
        SortedSet moduleDescriptors = this.filterModuleDescriptors.get((Object)path);
        return moduleDescriptors.isEmpty() ? null : (RestServletFilterModuleDescriptor)((Object)moduleDescriptors.last());
    }

    @Deprecated
    public Iterable<Filter> getFilters(FilterLocation location, String pathInfo, FilterConfig filterConfig, FilterDispatcherCondition filterDispatcherCondition) throws ServletException {
        return this.delegateModuleManager.getFilters(location, StringUtils.removeStart((String)pathInfo, (String)this.path), filterConfig, filterDispatcherCondition);
    }

    public Iterable<Filter> getFilters(FilterLocation location, String pathInfo, FilterConfig filterConfig, DispatcherType dispatcherType) {
        return this.delegateModuleManager.getFilters(location, StringUtils.removeStart((String)pathInfo, (String)this.path), filterConfig, dispatcherType);
    }

    public void removeFilterModule(ServletFilterModuleDescriptor descriptor) {
        if (descriptor instanceof RestServletFilterModuleDescriptor) {
            RestServletFilterModuleDescriptor restServletFilterModuleDescriptor = (RestServletFilterModuleDescriptor)descriptor;
            RestServletFilterModuleDescriptor latest = this.getRestServletFilterModuleDescriptorForLatest(restServletFilterModuleDescriptor.getBasePath());
            this.filterModuleDescriptors.remove((Object)restServletFilterModuleDescriptor.getBasePath(), (Object)restServletFilterModuleDescriptor);
            if (latest != null && latest.getCompleteKey().equals(descriptor.getCompleteKey()) && (latest = this.getRestServletFilterModuleDescriptorForLatest(restServletFilterModuleDescriptor.getBasePath())) != null) {
                this.filterPathMapper.put(latest.getCompleteKey(), this.getPathPattern(latest.getBasePath()));
            }
        }
        this.delegateModuleManager.removeFilterModule(descriptor);
    }

    String getPathPattern(String basePath) {
        return basePath + "/latest" + "/*";
    }

    private static final class RestServletFilterModuleDescriptorComparator
    implements Comparator<RestServletFilterModuleDescriptor> {
        private RestServletFilterModuleDescriptorComparator() {
        }

        @Override
        public int compare(RestServletFilterModuleDescriptor descriptor1, RestServletFilterModuleDescriptor descriptor2) {
            if (descriptor1 == null) {
                return -1;
            }
            if (descriptor2 == null) {
                return 1;
            }
            return descriptor1.getVersion().compareTo(descriptor2.getVersion());
        }
    }
}

