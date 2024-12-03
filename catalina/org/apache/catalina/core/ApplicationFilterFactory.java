/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.DispatcherType
 *  javax.servlet.Servlet
 *  javax.servlet.ServletRequest
 *  org.apache.tomcat.util.descriptor.web.FilterMap
 */
package org.apache.catalina.core;

import javax.servlet.DispatcherType;
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import org.apache.catalina.Globals;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Request;
import org.apache.catalina.core.ApplicationFilterChain;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.core.StandardContext;
import org.apache.tomcat.util.descriptor.web.FilterMap;

public final class ApplicationFilterFactory {
    private ApplicationFilterFactory() {
    }

    public static ApplicationFilterChain createFilterChain(ServletRequest request, Wrapper wrapper, Servlet servlet) {
        ApplicationFilterConfig filterConfig;
        if (servlet == null) {
            return null;
        }
        ApplicationFilterChain filterChain = null;
        if (request instanceof Request) {
            Request req = (Request)request;
            if (Globals.IS_SECURITY_ENABLED) {
                filterChain = new ApplicationFilterChain();
            } else {
                filterChain = (ApplicationFilterChain)req.getFilterChain();
                if (filterChain == null) {
                    filterChain = new ApplicationFilterChain();
                    req.setFilterChain(filterChain);
                }
            }
        } else {
            filterChain = new ApplicationFilterChain();
        }
        filterChain.setServlet(servlet);
        filterChain.setServletSupportsAsync(wrapper.isAsyncSupported());
        StandardContext context = (StandardContext)wrapper.getParent();
        FilterMap[] filterMaps = context.findFilterMaps();
        if (filterMaps == null || filterMaps.length == 0) {
            return filterChain;
        }
        DispatcherType dispatcher = (DispatcherType)request.getAttribute("org.apache.catalina.core.DISPATCHER_TYPE");
        String requestPath = null;
        Object attribute = request.getAttribute("org.apache.catalina.core.DISPATCHER_REQUEST_PATH");
        if (attribute != null) {
            requestPath = attribute.toString();
        }
        String servletName = wrapper.getName();
        for (FilterMap filterMap : filterMaps) {
            if (!ApplicationFilterFactory.matchDispatcher(filterMap, dispatcher) || !ApplicationFilterFactory.matchFiltersURL(filterMap, requestPath) || (filterConfig = (ApplicationFilterConfig)context.findFilterConfig(filterMap.getFilterName())) == null) continue;
            filterChain.addFilter(filterConfig);
        }
        for (FilterMap filterMap : filterMaps) {
            if (!ApplicationFilterFactory.matchDispatcher(filterMap, dispatcher) || !ApplicationFilterFactory.matchFiltersServlet(filterMap, servletName) || (filterConfig = (ApplicationFilterConfig)context.findFilterConfig(filterMap.getFilterName())) == null) continue;
            filterChain.addFilter(filterConfig);
        }
        return filterChain;
    }

    private static boolean matchFiltersURL(FilterMap filterMap, String requestPath) {
        String[] testPaths;
        if (filterMap.getMatchAllUrlPatterns()) {
            return true;
        }
        if (requestPath == null) {
            return false;
        }
        for (String testPath : testPaths = filterMap.getURLPatterns()) {
            if (!ApplicationFilterFactory.matchFiltersURL(testPath, requestPath)) continue;
            return true;
        }
        return false;
    }

    private static boolean matchFiltersURL(String testPath, String requestPath) {
        if (testPath == null) {
            return false;
        }
        if (testPath.equals(requestPath)) {
            return true;
        }
        if (testPath.equals("/*")) {
            return true;
        }
        if (testPath.endsWith("/*")) {
            if (testPath.regionMatches(0, requestPath, 0, testPath.length() - 2)) {
                if (requestPath.length() == testPath.length() - 2) {
                    return true;
                }
                if ('/' == requestPath.charAt(testPath.length() - 2)) {
                    return true;
                }
            }
            return false;
        }
        if (testPath.startsWith("*.")) {
            int slash = requestPath.lastIndexOf(47);
            int period = requestPath.lastIndexOf(46);
            if (slash >= 0 && period > slash && period != requestPath.length() - 1 && requestPath.length() - period == testPath.length() - 1) {
                return testPath.regionMatches(2, requestPath, period + 1, testPath.length() - 2);
            }
        }
        return false;
    }

    private static boolean matchFiltersServlet(FilterMap filterMap, String servletName) {
        String[] servletNames;
        if (servletName == null) {
            return false;
        }
        if (filterMap.getMatchAllServletNames()) {
            return true;
        }
        for (String name : servletNames = filterMap.getServletNames()) {
            if (!servletName.equals(name)) continue;
            return true;
        }
        return false;
    }

    private static boolean matchDispatcher(FilterMap filterMap, DispatcherType type) {
        switch (type) {
            case FORWARD: {
                if ((filterMap.getDispatcherMapping() & 2) == 0) break;
                return true;
            }
            case INCLUDE: {
                if ((filterMap.getDispatcherMapping() & 4) == 0) break;
                return true;
            }
            case REQUEST: {
                if ((filterMap.getDispatcherMapping() & 8) == 0) break;
                return true;
            }
            case ERROR: {
                if ((filterMap.getDispatcherMapping() & 1) == 0) break;
                return true;
            }
            case ASYNC: {
                if ((filterMap.getDispatcherMapping() & 0x10) == 0) break;
                return true;
            }
        }
        return false;
    }
}

