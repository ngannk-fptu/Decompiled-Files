/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Servlet
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletContextEvent
 *  javax.servlet.ServletContextListener
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRegistration$Dynamic
 */
package org.springframework.web.server.adapter;

import java.util.EventListener;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServletHttpHandlerAdapter;
import org.springframework.util.Assert;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

public abstract class AbstractReactiveWebInitializer
implements WebApplicationInitializer {
    public static final String DEFAULT_SERVLET_NAME = "http-handler-adapter";

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        String servletName = this.getServletName();
        Assert.hasLength(servletName, "getServletName() must not return null or empty");
        ApplicationContext applicationContext = this.createApplicationContext();
        Assert.notNull((Object)applicationContext, "createApplicationContext() must not return null");
        this.refreshApplicationContext(applicationContext);
        this.registerCloseListener(servletContext, applicationContext);
        HttpHandler httpHandler = WebHttpHandlerBuilder.applicationContext(applicationContext).build();
        ServletHttpHandlerAdapter servlet = new ServletHttpHandlerAdapter(httpHandler);
        ServletRegistration.Dynamic registration = servletContext.addServlet(servletName, (Servlet)servlet);
        if (registration == null) {
            throw new IllegalStateException("Failed to register servlet with name '" + servletName + "'. Check if there is another servlet registered under the same name.");
        }
        registration.setLoadOnStartup(1);
        registration.addMapping(new String[]{this.getServletMapping()});
        registration.setAsyncSupported(true);
    }

    protected String getServletName() {
        return DEFAULT_SERVLET_NAME;
    }

    protected ApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        Object[] configClasses = this.getConfigClasses();
        Assert.notEmpty(configClasses, "No Spring configuration provided through getConfigClasses()");
        context.register((Class<?>[])configClasses);
        return context;
    }

    protected abstract Class<?>[] getConfigClasses();

    protected void refreshApplicationContext(ApplicationContext context) {
        ConfigurableApplicationContext cac;
        if (context instanceof ConfigurableApplicationContext && !(cac = (ConfigurableApplicationContext)context).isActive()) {
            cac.refresh();
        }
    }

    protected void registerCloseListener(ServletContext servletContext, ApplicationContext applicationContext) {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            ConfigurableApplicationContext cac = (ConfigurableApplicationContext)applicationContext;
            ServletContextDestroyedListener listener = new ServletContextDestroyedListener(cac);
            servletContext.addListener((EventListener)((Object)listener));
        }
    }

    protected String getServletMapping() {
        return "/";
    }

    private static class ServletContextDestroyedListener
    implements ServletContextListener {
        private final ConfigurableApplicationContext applicationContext;

        public ServletContextDestroyedListener(ConfigurableApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        public void contextInitialized(ServletContextEvent sce) {
        }

        public void contextDestroyed(ServletContextEvent sce) {
            this.applicationContext.close();
        }
    }
}

