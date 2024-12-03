/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.DispatcherType
 *  javax.servlet.Filter
 *  javax.servlet.FilterRegistration$Dynamic
 *  javax.servlet.Servlet
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRegistration$Dynamic
 *  org.springframework.context.ApplicationContextInitializer
 *  org.springframework.core.Conventions
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.web.context.AbstractContextLoaderInitializer
 *  org.springframework.web.context.WebApplicationContext
 */
package org.springframework.web.servlet.support;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.Conventions;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.AbstractContextLoaderInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FrameworkServlet;

public abstract class AbstractDispatcherServletInitializer
extends AbstractContextLoaderInitializer {
    public static final String DEFAULT_SERVLET_NAME = "dispatcher";

    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        this.registerDispatcherServlet(servletContext);
    }

    protected void registerDispatcherServlet(ServletContext servletContext) {
        String servletName = this.getServletName();
        Assert.hasLength((String)servletName, (String)"getServletName() must not return null or empty");
        WebApplicationContext servletAppContext = this.createServletApplicationContext();
        Assert.notNull((Object)servletAppContext, (String)"createServletApplicationContext() must not return null");
        FrameworkServlet dispatcherServlet = this.createDispatcherServlet(servletAppContext);
        Assert.notNull((Object)((Object)dispatcherServlet), (String)"createDispatcherServlet(WebApplicationContext) must not return null");
        dispatcherServlet.setContextInitializers(this.getServletApplicationContextInitializers());
        ServletRegistration.Dynamic registration = servletContext.addServlet(servletName, (Servlet)dispatcherServlet);
        if (registration == null) {
            throw new IllegalStateException("Failed to register servlet with name '" + servletName + "'. Check if there is another servlet registered under the same name.");
        }
        registration.setLoadOnStartup(1);
        registration.addMapping(this.getServletMappings());
        registration.setAsyncSupported(this.isAsyncSupported());
        Object[] filters = this.getServletFilters();
        if (!ObjectUtils.isEmpty((Object[])filters)) {
            for (Object filter2 : filters) {
                this.registerServletFilter(servletContext, (Filter)filter2);
            }
        }
        this.customizeRegistration(registration);
    }

    protected String getServletName() {
        return DEFAULT_SERVLET_NAME;
    }

    protected abstract WebApplicationContext createServletApplicationContext();

    protected FrameworkServlet createDispatcherServlet(WebApplicationContext servletAppContext) {
        return new DispatcherServlet(servletAppContext);
    }

    @Nullable
    protected ApplicationContextInitializer<?>[] getServletApplicationContextInitializers() {
        return null;
    }

    protected abstract String[] getServletMappings();

    @Nullable
    protected Filter[] getServletFilters() {
        return null;
    }

    protected FilterRegistration.Dynamic registerServletFilter(ServletContext servletContext, Filter filter2) {
        String filterName = Conventions.getVariableName((Object)filter2);
        FilterRegistration.Dynamic registration = servletContext.addFilter(filterName, filter2);
        if (registration == null) {
            int counter = 0;
            while (registration == null) {
                if (counter == 100) {
                    throw new IllegalStateException("Failed to register filter with name '" + filterName + "'. Check if there is another filter registered under the same name.");
                }
                registration = servletContext.addFilter(filterName + "#" + counter, filter2);
                ++counter;
            }
        }
        registration.setAsyncSupported(this.isAsyncSupported());
        registration.addMappingForServletNames(this.getDispatcherTypes(), false, new String[]{this.getServletName()});
        return registration;
    }

    private EnumSet<DispatcherType> getDispatcherTypes() {
        return this.isAsyncSupported() ? EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ASYNC) : EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE);
    }

    protected boolean isAsyncSupported() {
        return true;
    }

    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
    }
}

