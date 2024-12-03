/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.DispatcherType
 *  javax.servlet.FilterRegistration$Dynamic
 *  org.apache.tomcat.util.descriptor.web.FilterDef
 *  org.apache.tomcat.util.descriptor.web.FilterMap
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import org.apache.catalina.Context;
import org.apache.catalina.util.ParameterMap;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.apache.tomcat.util.res.StringManager;

public class ApplicationFilterRegistration
implements FilterRegistration.Dynamic {
    private static final StringManager sm = StringManager.getManager(ApplicationFilterRegistration.class);
    private final FilterDef filterDef;
    private final Context context;

    public ApplicationFilterRegistration(FilterDef filterDef, Context context) {
        this.filterDef = filterDef;
        this.context = context;
    }

    public void addMappingForServletNames(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String ... servletNames) {
        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(this.filterDef.getFilterName());
        if (dispatcherTypes != null) {
            for (DispatcherType dispatcherType : dispatcherTypes) {
                filterMap.setDispatcher(dispatcherType.name());
            }
        }
        if (servletNames != null) {
            for (String servletName : servletNames) {
                filterMap.addServletName(servletName);
            }
            if (isMatchAfter) {
                this.context.addFilterMap(filterMap);
            } else {
                this.context.addFilterMapBefore(filterMap);
            }
        }
    }

    public void addMappingForUrlPatterns(EnumSet<DispatcherType> dispatcherTypes, boolean isMatchAfter, String ... urlPatterns) {
        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(this.filterDef.getFilterName());
        if (dispatcherTypes != null) {
            for (DispatcherType dispatcherType : dispatcherTypes) {
                filterMap.setDispatcher(dispatcherType.name());
            }
        }
        if (urlPatterns != null) {
            for (String urlPattern : urlPatterns) {
                filterMap.addURLPattern(urlPattern);
            }
            if (isMatchAfter) {
                this.context.addFilterMap(filterMap);
            } else {
                this.context.addFilterMapBefore(filterMap);
            }
        }
    }

    public Collection<String> getServletNameMappings() {
        FilterMap[] filterMaps;
        HashSet<String> result = new HashSet<String>();
        for (FilterMap filterMap : filterMaps = this.context.findFilterMaps()) {
            if (!filterMap.getFilterName().equals(this.filterDef.getFilterName())) continue;
            result.addAll(Arrays.asList(filterMap.getServletNames()));
        }
        return result;
    }

    public Collection<String> getUrlPatternMappings() {
        FilterMap[] filterMaps;
        HashSet<String> result = new HashSet<String>();
        for (FilterMap filterMap : filterMaps = this.context.findFilterMaps()) {
            if (!filterMap.getFilterName().equals(this.filterDef.getFilterName())) continue;
            result.addAll(Arrays.asList(filterMap.getURLPatterns()));
        }
        return result;
    }

    public String getClassName() {
        return this.filterDef.getFilterClass();
    }

    public String getInitParameter(String name) {
        return (String)this.filterDef.getParameterMap().get(name);
    }

    public Map<String, String> getInitParameters() {
        ParameterMap<String, String> result = new ParameterMap<String, String>();
        result.putAll(this.filterDef.getParameterMap());
        result.setLocked(true);
        return result;
    }

    public String getName() {
        return this.filterDef.getFilterName();
    }

    public boolean setInitParameter(String name, String value) {
        if (name == null || value == null) {
            throw new IllegalArgumentException(sm.getString("applicationFilterRegistration.nullInitParam", new Object[]{name, value}));
        }
        if (this.getInitParameter(name) != null) {
            return false;
        }
        this.filterDef.addInitParameter(name, value);
        return true;
    }

    public Set<String> setInitParameters(Map<String, String> initParameters) {
        HashSet<String> conflicts = new HashSet<String>();
        for (Map.Entry<String, String> entry : initParameters.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                throw new IllegalArgumentException(sm.getString("applicationFilterRegistration.nullInitParams", new Object[]{entry.getKey(), entry.getValue()}));
            }
            if (this.getInitParameter(entry.getKey()) == null) continue;
            conflicts.add(entry.getKey());
        }
        for (Map.Entry<String, String> entry : initParameters.entrySet()) {
            this.setInitParameter(entry.getKey(), entry.getValue());
        }
        return conflicts;
    }

    public void setAsyncSupported(boolean asyncSupported) {
        this.filterDef.setAsyncSupported(Boolean.valueOf(asyncSupported).toString());
    }
}

