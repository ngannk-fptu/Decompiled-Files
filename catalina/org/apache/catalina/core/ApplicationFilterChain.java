/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.Servlet
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.tomcat.util.ExceptionUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.core;

import java.io.IOException;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Globals;
import org.apache.catalina.core.ApplicationDispatcher;
import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.security.SecurityUtil;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.res.StringManager;

public final class ApplicationFilterChain
implements FilterChain {
    private static final ThreadLocal<ServletRequest> lastServicedRequest;
    private static final ThreadLocal<ServletResponse> lastServicedResponse;
    public static final int INCREMENT = 10;
    private ApplicationFilterConfig[] filters = new ApplicationFilterConfig[0];
    private int pos = 0;
    private int n = 0;
    private Servlet servlet = null;
    private boolean servletSupportsAsync = false;
    private static final StringManager sm;
    private static final Class<?>[] classType;
    private static final Class<?>[] classTypeUsedInService;

    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (Globals.IS_SECURITY_ENABLED) {
            ServletRequest req = request;
            ServletResponse res = response;
            try {
                AccessController.doPrivileged(() -> {
                    this.internalDoFilter(req, res);
                    return null;
                });
            }
            catch (PrivilegedActionException pe) {
                Exception e = pe.getException();
                if (e instanceof ServletException) {
                    throw (ServletException)((Object)e);
                }
                if (e instanceof IOException) {
                    throw (IOException)e;
                }
                if (e instanceof RuntimeException) {
                    throw (RuntimeException)e;
                }
                throw new ServletException(e.getMessage(), (Throwable)e);
            }
        } else {
            this.internalDoFilter(request, response);
        }
    }

    private void internalDoFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (this.pos < this.n) {
            ApplicationFilterConfig filterConfig = this.filters[this.pos++];
            try {
                Filter filter = filterConfig.getFilter();
                if (request.isAsyncSupported() && "false".equalsIgnoreCase(filterConfig.getFilterDef().getAsyncSupported())) {
                    request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", (Object)Boolean.FALSE);
                }
                if (Globals.IS_SECURITY_ENABLED) {
                    ServletRequest req = request;
                    ServletResponse res = response;
                    Principal principal = ((HttpServletRequest)req).getUserPrincipal();
                    Object[] args = new Object[]{req, res, this};
                    SecurityUtil.doAsPrivilege("doFilter", filter, classType, args, principal);
                } else {
                    filter.doFilter(request, response, (FilterChain)this);
                }
            }
            catch (IOException | RuntimeException | ServletException e) {
                throw e;
            }
            catch (Throwable e) {
                e = ExceptionUtils.unwrapInvocationTargetException((Throwable)e);
                ExceptionUtils.handleThrowable((Throwable)e);
                throw new ServletException(sm.getString("filterChain.filter"), e);
            }
            return;
        }
        try {
            if (ApplicationDispatcher.WRAP_SAME_OBJECT) {
                lastServicedRequest.set(request);
                lastServicedResponse.set(response);
            }
            if (request.isAsyncSupported() && !this.servletSupportsAsync) {
                request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", (Object)Boolean.FALSE);
            }
            if (request instanceof HttpServletRequest && response instanceof HttpServletResponse && Globals.IS_SECURITY_ENABLED) {
                ServletRequest req = request;
                ServletResponse res = response;
                Principal principal = ((HttpServletRequest)req).getUserPrincipal();
                Object[] args = new Object[]{req, res};
                SecurityUtil.doAsPrivilege("service", this.servlet, classTypeUsedInService, args, principal);
            } else {
                this.servlet.service(request, response);
            }
        }
        catch (IOException | RuntimeException | ServletException e) {
            throw e;
        }
        catch (Throwable e) {
            e = ExceptionUtils.unwrapInvocationTargetException((Throwable)e);
            ExceptionUtils.handleThrowable((Throwable)e);
            throw new ServletException(sm.getString("filterChain.servlet"), e);
        }
        finally {
            if (ApplicationDispatcher.WRAP_SAME_OBJECT) {
                lastServicedRequest.set(null);
                lastServicedResponse.set(null);
            }
        }
    }

    public static ServletRequest getLastServicedRequest() {
        return lastServicedRequest.get();
    }

    public static ServletResponse getLastServicedResponse() {
        return lastServicedResponse.get();
    }

    void addFilter(ApplicationFilterConfig filterConfig) {
        for (ApplicationFilterConfig filter : this.filters) {
            if (filter != filterConfig) continue;
            return;
        }
        if (this.n == this.filters.length) {
            ApplicationFilterConfig[] newFilters = new ApplicationFilterConfig[this.n + 10];
            System.arraycopy(this.filters, 0, newFilters, 0, this.n);
            this.filters = newFilters;
        }
        this.filters[this.n++] = filterConfig;
    }

    void release() {
        for (int i = 0; i < this.n; ++i) {
            this.filters[i] = null;
        }
        this.n = 0;
        this.pos = 0;
        this.servlet = null;
        this.servletSupportsAsync = false;
    }

    void reuse() {
        this.pos = 0;
    }

    void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

    void setServletSupportsAsync(boolean servletSupportsAsync) {
        this.servletSupportsAsync = servletSupportsAsync;
    }

    public void findNonAsyncFilters(Set<String> result) {
        for (int i = 0; i < this.n; ++i) {
            ApplicationFilterConfig filter = this.filters[i];
            if (!"false".equalsIgnoreCase(filter.getFilterDef().getAsyncSupported())) continue;
            result.add(filter.getFilterClass());
        }
    }

    static {
        if (ApplicationDispatcher.WRAP_SAME_OBJECT) {
            lastServicedRequest = new ThreadLocal();
            lastServicedResponse = new ThreadLocal();
        } else {
            lastServicedRequest = null;
            lastServicedResponse = null;
        }
        sm = StringManager.getManager(ApplicationFilterChain.class);
        classType = new Class[]{ServletRequest.class, ServletResponse.class, FilterChain.class};
        classTypeUsedInService = new Class[]{ServletRequest.class, ServletResponse.class};
    }
}

