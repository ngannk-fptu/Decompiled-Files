/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.springframework.web.context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ConfigurableWebEnvironment;
import org.springframework.web.context.WebApplicationContext;

public class ContextLoader {
    public static final String CONTEXT_ID_PARAM = "contextId";
    public static final String CONFIG_LOCATION_PARAM = "contextConfigLocation";
    public static final String CONTEXT_CLASS_PARAM = "contextClass";
    public static final String CONTEXT_INITIALIZER_CLASSES_PARAM = "contextInitializerClasses";
    public static final String GLOBAL_INITIALIZER_CLASSES_PARAM = "globalInitializerClasses";
    private static final String INIT_PARAM_DELIMITERS = ",; \t\n";
    private static final String DEFAULT_STRATEGIES_PATH = "ContextLoader.properties";
    private static final Properties defaultStrategies;
    private static final Map<ClassLoader, WebApplicationContext> currentContextPerThread;
    @Nullable
    private static volatile WebApplicationContext currentContext;
    @Nullable
    private WebApplicationContext context;
    private final List<ApplicationContextInitializer<ConfigurableApplicationContext>> contextInitializers = new ArrayList<ApplicationContextInitializer<ConfigurableApplicationContext>>();

    public ContextLoader() {
    }

    public ContextLoader(WebApplicationContext context) {
        this.context = context;
    }

    public void setContextInitializers(ApplicationContextInitializer<?> ... initializers) {
        if (initializers != null) {
            for (ApplicationContextInitializer<?> initializer : initializers) {
                this.contextInitializers.add(initializer);
            }
        }
    }

    public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
        if (servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE) != null) {
            throw new IllegalStateException("Cannot initialize context because there is already a root application context present - check whether you have multiple ContextLoader* definitions in your web.xml!");
        }
        Log logger = LogFactory.getLog(ContextLoader.class);
        servletContext.log("Initializing Spring root WebApplicationContext");
        if (logger.isInfoEnabled()) {
            logger.info("Root WebApplicationContext: initialization started");
        }
        long startTime = System.currentTimeMillis();
        try {
            ConfigurableWebApplicationContext cwac;
            if (this.context == null) {
                this.context = this.createWebApplicationContext(servletContext);
            }
            if (this.context instanceof ConfigurableWebApplicationContext && !(cwac = (ConfigurableWebApplicationContext)this.context).isActive()) {
                if (cwac.getParent() == null) {
                    ApplicationContext parent = this.loadParentContext(servletContext);
                    cwac.setParent(parent);
                }
                this.configureAndRefreshWebApplicationContext(cwac, servletContext);
            }
            servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, (Object)this.context);
            ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            if (ccl == ContextLoader.class.getClassLoader()) {
                currentContext = this.context;
            } else if (ccl != null) {
                currentContextPerThread.put(ccl, this.context);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Published root WebApplicationContext as ServletContext attribute with name [" + WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE + "]");
            }
            if (logger.isInfoEnabled()) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                logger.info("Root WebApplicationContext: initialization completed in " + elapsedTime + " ms");
            }
            return this.context;
        }
        catch (RuntimeException ex) {
            logger.error("Context initialization failed", ex);
            servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, (Object)ex);
            throw ex;
        }
        catch (Error err) {
            logger.error("Context initialization failed", err);
            servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, (Object)err);
            throw err;
        }
    }

    protected WebApplicationContext createWebApplicationContext(ServletContext sc) {
        Class<?> contextClass = this.determineContextClass(sc);
        if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
            throw new ApplicationContextException("Custom context class [" + contextClass.getName() + "] is not of type [" + ConfigurableWebApplicationContext.class.getName() + "]");
        }
        return (ConfigurableWebApplicationContext)BeanUtils.instantiateClass(contextClass);
    }

    protected Class<?> determineContextClass(ServletContext servletContext) {
        String contextClassName = servletContext.getInitParameter(CONTEXT_CLASS_PARAM);
        if (contextClassName != null) {
            try {
                return ClassUtils.forName(contextClassName, ClassUtils.getDefaultClassLoader());
            }
            catch (ClassNotFoundException ex) {
                throw new ApplicationContextException("Failed to load custom context class [" + contextClassName + "]", ex);
            }
        }
        contextClassName = defaultStrategies.getProperty(WebApplicationContext.class.getName());
        try {
            return ClassUtils.forName(contextClassName, ContextLoader.class.getClassLoader());
        }
        catch (ClassNotFoundException ex) {
            throw new ApplicationContextException("Failed to load default context class [" + contextClassName + "]", ex);
        }
    }

    protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac, ServletContext sc) {
        Environment env;
        if (ObjectUtils.identityToString(wac).equals(wac.getId())) {
            String idParam = sc.getInitParameter(CONTEXT_ID_PARAM);
            if (idParam != null) {
                wac.setId(idParam);
            } else {
                wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX + ObjectUtils.getDisplayString(sc.getContextPath()));
            }
        }
        wac.setServletContext(sc);
        String configLocationParam = sc.getInitParameter(CONFIG_LOCATION_PARAM);
        if (configLocationParam != null) {
            wac.setConfigLocation(configLocationParam);
        }
        if ((env = wac.getEnvironment()) instanceof ConfigurableWebEnvironment) {
            ((ConfigurableWebEnvironment)env).initPropertySources(sc, null);
        }
        this.customizeContext(sc, wac);
        wac.refresh();
    }

    protected void customizeContext(ServletContext sc, ConfigurableWebApplicationContext wac) {
        List<Class<ApplicationContextInitializer<ConfigurableApplicationContext>>> initializerClasses = this.determineContextInitializerClasses(sc);
        for (Class<ApplicationContextInitializer<ConfigurableApplicationContext>> clazz : initializerClasses) {
            Class<?> initializerContextClass = GenericTypeResolver.resolveTypeArgument(clazz, ApplicationContextInitializer.class);
            if (initializerContextClass != null && !initializerContextClass.isInstance(wac)) {
                throw new ApplicationContextException(String.format("Could not apply context initializer [%s] since its generic parameter [%s] is not assignable from the type of application context used by this context loader: [%s]", clazz.getName(), initializerContextClass.getName(), wac.getClass().getName()));
            }
            this.contextInitializers.add(BeanUtils.instantiateClass(clazz));
        }
        AnnotationAwareOrderComparator.sort(this.contextInitializers);
        for (ApplicationContextInitializer applicationContextInitializer : this.contextInitializers) {
            applicationContextInitializer.initialize(wac);
        }
    }

    protected List<Class<ApplicationContextInitializer<ConfigurableApplicationContext>>> determineContextInitializerClasses(ServletContext servletContext) {
        String localClassNames;
        ArrayList<Class<ApplicationContextInitializer<ConfigurableApplicationContext>>> classes = new ArrayList<Class<ApplicationContextInitializer<ConfigurableApplicationContext>>>();
        String globalClassNames = servletContext.getInitParameter(GLOBAL_INITIALIZER_CLASSES_PARAM);
        if (globalClassNames != null) {
            for (String className : StringUtils.tokenizeToStringArray(globalClassNames, INIT_PARAM_DELIMITERS)) {
                classes.add(this.loadInitializerClass(className));
            }
        }
        if ((localClassNames = servletContext.getInitParameter(CONTEXT_INITIALIZER_CLASSES_PARAM)) != null) {
            for (String className : StringUtils.tokenizeToStringArray(localClassNames, INIT_PARAM_DELIMITERS)) {
                classes.add(this.loadInitializerClass(className));
            }
        }
        return classes;
    }

    private Class<ApplicationContextInitializer<ConfigurableApplicationContext>> loadInitializerClass(String className) {
        try {
            Class<ApplicationContextInitializer<ConfigurableApplicationContext>> clazz = ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
            if (!ApplicationContextInitializer.class.isAssignableFrom(clazz)) {
                throw new ApplicationContextException("Initializer class does not implement ApplicationContextInitializer interface: " + clazz);
            }
            return clazz;
        }
        catch (ClassNotFoundException ex) {
            throw new ApplicationContextException("Failed to load context initializer class [" + className + "]", ex);
        }
    }

    @Nullable
    protected ApplicationContext loadParentContext(ServletContext servletContext) {
        return null;
    }

    public void closeWebApplicationContext(ServletContext servletContext) {
        servletContext.log("Closing Spring root WebApplicationContext");
        try {
            if (this.context instanceof ConfigurableWebApplicationContext) {
                ((ConfigurableWebApplicationContext)this.context).close();
            }
        }
        finally {
            ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            if (ccl == ContextLoader.class.getClassLoader()) {
                currentContext = null;
            } else if (ccl != null) {
                currentContextPerThread.remove(ccl);
            }
            servletContext.removeAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        }
    }

    @Nullable
    public static WebApplicationContext getCurrentWebApplicationContext() {
        WebApplicationContext ccpt;
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        if (ccl != null && (ccpt = currentContextPerThread.get(ccl)) != null) {
            return ccpt;
        }
        return currentContext;
    }

    static {
        try {
            ClassPathResource resource = new ClassPathResource(DEFAULT_STRATEGIES_PATH, ContextLoader.class);
            defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
        }
        catch (IOException ex) {
            throw new IllegalStateException("Could not load 'ContextLoader.properties': " + ex.getMessage());
        }
        currentContextPerThread = new ConcurrentHashMap<ClassLoader, WebApplicationContext>(1);
    }
}

