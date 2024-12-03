/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Servlet
 *  javax.servlet.ServletContainerInitializer
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletRegistration
 *  javax.servlet.annotation.HandlesTypes
 */
package com.sun.jersey.server.impl.container.servlet;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.server.impl.application.DeferredResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.servlet.Servlet;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.HandlesTypes;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;

@HandlesTypes(value={Path.class, Provider.class, Application.class, ApplicationPath.class})
public class JerseyServletContainerInitializer
implements ServletContainerInitializer {
    private static final Logger LOGGER = Logger.getLogger(JerseyServletContainerInitializer.class.getName());

    public void onStartup(Set<Class<?>> classes, ServletContext sc) {
        if (classes == null) {
            classes = Collections.emptySet();
        }
        int nOfRegisterations = sc.getServletRegistrations().size();
        for (Class<? extends Application> a : this.getApplicationClasses(classes)) {
            ServletRegistration appReg = sc.getServletRegistration(a.getName());
            if (appReg != null) {
                this.addServletWithExistingRegistration(sc, appReg, a, classes);
                continue;
            }
            List<ServletRegistration> srs = this.getInitParamDeclaredRegistrations(sc, a);
            if (!srs.isEmpty()) {
                for (ServletRegistration sr : srs) {
                    this.addServletWithExistingRegistration(sc, sr, a, classes);
                }
                continue;
            }
            this.addServletWithApplication(sc, a, classes);
        }
        if (nOfRegisterations == sc.getServletRegistrations().size()) {
            this.addServletWithDefaultConfiguration(sc, classes);
        }
    }

    private List<ServletRegistration> getInitParamDeclaredRegistrations(ServletContext sc, Class<? extends Application> a) {
        ArrayList<ServletRegistration> srs = new ArrayList<ServletRegistration>(1);
        for (ServletRegistration sr : sc.getServletRegistrations().values()) {
            Map ips = sr.getInitParameters();
            if (ips.containsKey("javax.ws.rs.Application")) {
                if (!((String)ips.get("javax.ws.rs.Application")).equals(a.getName()) || sr.getClassName() != null) continue;
                srs.add(sr);
                continue;
            }
            if (!ips.containsKey("com.sun.jersey.config.property.resourceConfigClass") || !((String)ips.get("com.sun.jersey.config.property.resourceConfigClass")).equals(a.getName()) || sr.getClassName() != null) continue;
            srs.add(sr);
        }
        return srs;
    }

    private void addServletWithDefaultConfiguration(ServletContext sc, Set<Class<?>> classes) {
        ServletRegistration appReg = sc.getServletRegistration(Application.class.getName());
        if (appReg != null && appReg.getClassName() == null) {
            Set<Class<?>> x = this.getRootResourceAndProviderClasses(classes);
            ServletContainer s = new ServletContainer(new DefaultResourceConfig(x));
            if ((appReg = sc.addServlet(appReg.getName(), (Servlet)s)).getMappings().isEmpty()) {
                LOGGER.severe("The Jersey servlet application, named " + appReg.getName() + ", has no servlet mapping");
            } else {
                LOGGER.info("Registering the Jersey servlet application, named " + appReg.getName() + ", with the following root resource and provider classes: " + x);
            }
        }
    }

    private void addServletWithApplication(ServletContext sc, Class<? extends Application> a, Set<Class<?>> classes) {
        ApplicationPath ap = a.getAnnotation(ApplicationPath.class);
        if (ap != null) {
            ServletContainer s = new ServletContainer(new DeferredResourceConfig(a, this.getRootResourceAndProviderClasses(classes)));
            String mapping = this.createMappingPath(ap);
            if (!this.mappingExists(sc, mapping)) {
                sc.addServlet(a.getName(), (Servlet)s).addMapping(new String[]{mapping});
                LOGGER.info("Registering the Jersey servlet application, named " + a.getName() + ", at the servlet mapping, " + mapping + ", with the Application class of the same name");
            } else {
                LOGGER.severe("Mapping conflict. A Servlet declaration exists with same mapping as the Jersey servlet application, named " + a.getName() + ", at the servlet mapping, " + mapping + ". The Jersey servlet is not deployed.");
            }
        }
    }

    private void addServletWithExistingRegistration(ServletContext sc, ServletRegistration sr, Class<? extends Application> a, Set<Class<?>> classes) {
        if (sr.getClassName() == null) {
            DeferredResourceConfig rc = new DeferredResourceConfig(a, this.getRootResourceAndProviderClasses(classes));
            HashMap<String, Object> initParams = new HashMap<String, Object>();
            for (Map.Entry entry : sr.getInitParameters().entrySet()) {
                initParams.put((String)entry.getKey(), entry.getValue());
            }
            rc.setPropertiesAndFeatures(initParams);
            ServletContainer s = new ServletContainer(rc);
            sr = sc.addServlet(a.getName(), (Servlet)s);
            if (sr.getMappings().isEmpty()) {
                ApplicationPath ap = a.getAnnotation(ApplicationPath.class);
                if (ap != null) {
                    String mapping = this.createMappingPath(ap);
                    if (!this.mappingExists(sc, mapping)) {
                        sr.addMapping(new String[]{mapping});
                        LOGGER.info("Registering the Jersey servlet application, named " + a.getName() + ", at the servlet mapping, " + mapping + ", with the Application class of the same name");
                    } else {
                        LOGGER.severe("Mapping conflict. A Servlet registration exists with same mapping as the Jersey servlet application, named " + a.getName() + ", at the servlet mapping, " + mapping + ". The Jersey servlet is not deployed.");
                    }
                } else {
                    LOGGER.severe("The Jersey servlet application, named " + a.getName() + ", is not annotated with " + ApplicationPath.class.getSimpleName() + " and has no servlet mapping");
                }
            } else {
                LOGGER.info("Registering the Jersey servlet application, named " + a.getName() + ", with the Application class of the same name");
            }
        }
    }

    private boolean mappingExists(ServletContext sc, String mapping) {
        for (ServletRegistration sr : sc.getServletRegistrations().values()) {
            for (String declaredMapping : sr.getMappings()) {
                if (!mapping.equals(declaredMapping)) continue;
                return true;
            }
        }
        return false;
    }

    private String createMappingPath(ApplicationPath ap) {
        String path = ap.value();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (!path.endsWith("/*")) {
            path = path.endsWith("/") ? path + "*" : path + "/*";
        }
        return path;
    }

    private Set<Class<? extends Application>> getApplicationClasses(Set<Class<?>> classes) {
        LinkedHashSet<Class<? extends Application>> s = new LinkedHashSet<Class<? extends Application>>();
        for (Class<?> c : classes) {
            if (Application.class == c || !Application.class.isAssignableFrom(c)) continue;
            s.add(c.asSubclass(Application.class));
        }
        return s;
    }

    private Set<Class<?>> getRootResourceAndProviderClasses(Set<Class<?>> classes) {
        LinkedHashSet s = new LinkedHashSet();
        for (Class<?> c : classes) {
            if (!c.isAnnotationPresent(Path.class) && !c.isAnnotationPresent(Provider.class)) continue;
            s.add(c);
        }
        return s;
    }
}

