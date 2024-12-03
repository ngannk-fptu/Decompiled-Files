/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.context.ConfigurableOsgiBundleApplicationContext
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEventMulticaster
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEventMulticasterAdapter
 *  org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener
 *  org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext
 *  org.eclipse.gemini.blueprint.util.BundleDelegatingClassLoader
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleActivator
 *  org.osgi.framework.BundleContext
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.context.event.ApplicationEventMulticaster
 *  org.springframework.context.event.SimpleApplicationEventMulticaster
 *  org.springframework.core.task.TaskExecutor
 *  org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.extender.internal.support;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.context.ConfigurableOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEventMulticaster;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEventMulticasterAdapter;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext;
import org.eclipse.gemini.blueprint.extender.OsgiApplicationContextCreator;
import org.eclipse.gemini.blueprint.extender.OsgiBeanFactoryPostProcessor;
import org.eclipse.gemini.blueprint.extender.OsgiServiceDependencyFactory;
import org.eclipse.gemini.blueprint.extender.internal.dependencies.startup.MandatoryImporterDependencyFactory;
import org.eclipse.gemini.blueprint.extender.internal.support.DefaultOsgiBundleApplicationContextListener;
import org.eclipse.gemini.blueprint.extender.internal.support.OsgiAnnotationPostProcessor;
import org.eclipse.gemini.blueprint.util.BundleDelegatingClassLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class ExtenderConfiguration
implements BundleActivator {
    protected final Log log = LogFactory.getLog(this.getClass());
    private static final String TASK_EXECUTOR_NAME = "taskExecutor";
    private static final String SHUTDOWN_TASK_EXECUTOR_NAME = "shutdownTaskExecutor";
    private static final String CONTEXT_CREATOR_NAME = "applicationContextCreator";
    private static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "osgiApplicationEventMulticaster";
    private static final String CONTEXT_LISTENER_NAME = "osgiApplicationContextListener";
    private static final String PROPERTIES_NAME = "extenderProperties";
    private static final String SHUTDOWN_ASYNCHRONOUS_KEY = "shutdown.asynchronously";
    private static final String SHUTDOWN_WAIT_KEY = "shutdown.wait.time";
    private static final String PROCESS_ANNOTATIONS_KEY = "process.annotations";
    private static final String WAIT_FOR_DEPS_TIMEOUT_KEY = "dependencies.wait.time";
    private static final String EXTENDER_CFG_LOCATION = "META-INF/spring/extender";
    private static final String XML_PATTERN = "*.xml";
    private static final String ANNOTATION_DEPENDENCY_FACTORY = "org.eclipse.gemini.blueprint.extensions.annotation.ServiceReferenceDependencyBeanFactoryPostProcessor";
    private static final String AUTO_ANNOTATION_PROCESSING = "org.eclipse.gemini.blueprint.extender.annotation.auto.processing";
    private static final long DEFAULT_DEP_WAIT = 300000L;
    private static final boolean DEFAULT_NS_BUNDLE_STATE = true;
    private static final boolean DEFAULT_SHUTDOWN_ASYNCHRONOUS = true;
    private static final long DEFAULT_SHUTDOWN_WAIT = 10000L;
    private static final boolean DEFAULT_PROCESS_ANNOTATION = true;
    private ConfigurableOsgiBundleApplicationContext extenderConfiguration;
    private TaskExecutor taskExecutor;
    private TaskExecutor shutdownTaskExecutor;
    private boolean isTaskExecutorManagedInternally;
    private boolean isShutdownTaskExecutorManagedInternally;
    private boolean isMulticasterManagedInternally;
    private long shutdownWaitTime;
    private long dependencyWaitTime;
    private boolean shutdownAsynchronously;
    private boolean processAnnotation;
    private boolean nsBundledResolved;
    private OsgiBundleApplicationContextEventMulticaster eventMulticaster;
    private OsgiBundleApplicationContextListener contextEventListener;
    private boolean forceThreadShutdown;
    private OsgiApplicationContextCreator contextCreator = null;
    private ClassLoader classLoader;
    private final List<OsgiBeanFactoryPostProcessor> postProcessors = Collections.synchronizedList(new ArrayList(0));
    private final List<OsgiServiceDependencyFactory> dependencyFactories = Collections.synchronizedList(new ArrayList(0));
    private final Object lock = new Object();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void start(BundleContext extenderBundleContext) {
        Object object;
        Bundle bundle = extenderBundleContext.getBundle();
        Properties properties = new Properties(this.createDefaultProperties());
        Enumeration enm = bundle.findEntries(EXTENDER_CFG_LOCATION, XML_PATTERN, false);
        if (enm == null) {
            this.log.info((Object)"No custom extender configuration detected; using defaults...");
            object = this.lock;
            synchronized (object) {
                this.taskExecutor = this.createDefaultTaskExecutor();
                this.shutdownTaskExecutor = this.createDefaultShutdownTaskExecutor();
                this.eventMulticaster = this.createDefaultEventMulticaster();
                this.contextEventListener = this.createDefaultApplicationContextListener();
            }
            this.classLoader = BundleDelegatingClassLoader.createBundleClassLoaderFor((Bundle)bundle);
        } else {
            Object[] configs = this.copyEnumerationToList(enm);
            this.log.info((Object)("Detected extender custom configurations at " + ObjectUtils.nullSafeToString((Object[])configs)));
            OsgiBundleXmlApplicationContext extenderAppCtx = new OsgiBundleXmlApplicationContext((String[])configs);
            extenderAppCtx.setBundleContext(extenderBundleContext);
            extenderAppCtx.refresh();
            Object object2 = this.lock;
            synchronized (object2) {
                this.extenderConfiguration = extenderAppCtx;
                this.taskExecutor = this.extenderConfiguration.containsBean(TASK_EXECUTOR_NAME) ? (TaskExecutor)this.extenderConfiguration.getBean(TASK_EXECUTOR_NAME, TaskExecutor.class) : this.createDefaultTaskExecutor();
                this.shutdownTaskExecutor = this.extenderConfiguration.containsBean(SHUTDOWN_TASK_EXECUTOR_NAME) ? (TaskExecutor)this.extenderConfiguration.getBean(SHUTDOWN_TASK_EXECUTOR_NAME, TaskExecutor.class) : this.createDefaultShutdownTaskExecutor();
                this.eventMulticaster = this.extenderConfiguration.containsBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME) ? (OsgiBundleApplicationContextEventMulticaster)this.extenderConfiguration.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, OsgiBundleApplicationContextEventMulticaster.class) : this.createDefaultEventMulticaster();
                this.contextCreator = this.extenderConfiguration.containsBean(CONTEXT_CREATOR_NAME) ? (OsgiApplicationContextCreator)this.extenderConfiguration.getBean(CONTEXT_CREATOR_NAME, OsgiApplicationContextCreator.class) : null;
                this.contextEventListener = this.extenderConfiguration.containsBean(CONTEXT_LISTENER_NAME) ? (OsgiBundleApplicationContextListener)this.extenderConfiguration.getBean(CONTEXT_LISTENER_NAME, OsgiBundleApplicationContextListener.class) : this.createDefaultApplicationContextListener();
            }
            this.postProcessors.addAll(this.extenderConfiguration.getBeansOfType(OsgiBeanFactoryPostProcessor.class).values());
            this.dependencyFactories.addAll(this.extenderConfiguration.getBeansOfType(OsgiServiceDependencyFactory.class).values());
            this.classLoader = this.extenderConfiguration.getClassLoader();
            if (this.extenderConfiguration.containsBean(PROPERTIES_NAME)) {
                Properties customProperties = (Properties)this.extenderConfiguration.getBean(PROPERTIES_NAME, Properties.class);
                Enumeration<?> propertyKey = customProperties.propertyNames();
                while (propertyKey.hasMoreElements()) {
                    String property = (String)propertyKey.nextElement();
                    properties.setProperty(property, customProperties.getProperty(property));
                }
            }
        }
        object = this.lock;
        synchronized (object) {
            this.shutdownWaitTime = this.getShutdownWaitTime(properties);
            this.shutdownAsynchronously = this.getShutdownAsynchronously(properties);
            this.dependencyWaitTime = this.getDependencyWaitTime(properties);
            this.processAnnotation = this.getProcessAnnotations(properties);
        }
        this.addDefaultDependencyFactories();
        this.contextCreator = this.postProcess(this.contextCreator);
    }

    protected OsgiApplicationContextCreator postProcess(OsgiApplicationContextCreator contextCreator) {
        return contextCreator;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stop(BundleContext extenderBundleContext) {
        Object object = this.lock;
        synchronized (object) {
            if (this.isMulticasterManagedInternally) {
                this.eventMulticaster.removeAllListeners();
                this.eventMulticaster = null;
            }
            if (this.extenderConfiguration != null) {
                this.extenderConfiguration.close();
                this.extenderConfiguration = null;
            }
            if (this.forceThreadShutdown) {
                if (this.isTaskExecutorManagedInternally) {
                    this.log.warn((Object)"Forcing the (internally created) taskExecutor to stop...");
                    ThreadGroup th = ((ThreadPoolTaskExecutor)this.taskExecutor).getThreadGroup();
                    if (!th.isDestroyed()) {
                        th.interrupt();
                    }
                }
                this.taskExecutor = null;
            }
            if (this.isShutdownTaskExecutorManagedInternally) {
                try {
                    ((DisposableBean)this.shutdownTaskExecutor).destroy();
                }
                catch (Exception ex) {
                    this.log.debug((Object)"Received exception while shutting down shutdown task executor", (Throwable)ex);
                }
                this.shutdownTaskExecutor = null;
            }
        }
    }

    private String[] copyEnumerationToList(Enumeration<?> enm) {
        ArrayList<String> urls = new ArrayList<String>(4);
        while (enm != null && enm.hasMoreElements()) {
            URL configURL = (URL)enm.nextElement();
            if (configURL == null) continue;
            String configURLAsString = configURL.toExternalForm();
            try {
                urls.add(URLDecoder.decode(configURLAsString, "UTF8"));
            }
            catch (UnsupportedEncodingException uee) {
                this.log.warn((Object)"UTF8 encoding not supported, using the platform default");
                urls.add(URLDecoder.decode(configURLAsString));
            }
        }
        return urls.toArray(new String[urls.size()]);
    }

    private Properties createDefaultProperties() {
        Properties properties = new Properties();
        properties.setProperty(SHUTDOWN_WAIT_KEY, "10000");
        properties.setProperty(SHUTDOWN_ASYNCHRONOUS_KEY, "true");
        properties.setProperty(PROCESS_ANNOTATIONS_KEY, "true");
        properties.setProperty(WAIT_FOR_DEPS_TIMEOUT_KEY, "300000");
        return properties;
    }

    protected void addDefaultDependencyFactories() {
        boolean debug = this.log.isDebugEnabled();
        this.dependencyFactories.add(0, new MandatoryImporterDependencyFactory());
        if (this.processAnnotation) {
            Class<?> annotationProcessor = null;
            try {
                annotationProcessor = Class.forName(ANNOTATION_DEPENDENCY_FACTORY, false, ExtenderConfiguration.class.getClassLoader());
            }
            catch (ClassNotFoundException cnfe) {
                this.log.warn((Object)"Gemini Blueprint extensions bundle not present, annotation processing disabled.");
                this.log.debug((Object)"Gemini Blueprint extensions bundle not present, annotation processing disabled.", (Throwable)cnfe);
                return;
            }
            Object processor = BeanUtils.instantiateClass(annotationProcessor);
            Assert.isInstanceOf(OsgiServiceDependencyFactory.class, (Object)processor);
            this.dependencyFactories.add(1, (OsgiServiceDependencyFactory)processor);
            if (debug) {
                this.log.debug((Object)"Succesfully loaded annotation dependency processor [org.eclipse.gemini.blueprint.extensions.annotation.ServiceReferenceDependencyBeanFactoryPostProcessor]");
            }
            this.postProcessors.add(0, new OsgiAnnotationPostProcessor());
            this.log.info((Object)"Gemini Blueprint extensions annotation processing enabled");
        } else if (debug) {
            this.log.debug((Object)"Gemini Blueprint extensions annotation processing disabled; [org.eclipse.gemini.blueprint.extensions.annotation.ServiceReferenceDependencyBeanFactoryPostProcessor] not loaded");
        }
    }

    private TaskExecutor createDefaultTaskExecutor() {
        ThreadGroup threadGroup = new ThreadGroup("eclipse-gemini-blueprint-extender[" + ObjectUtils.getIdentityHexString((Object)this) + "]-threads");
        threadGroup.setDaemon(false);
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
        taskExecutor.setThreadGroup(threadGroup);
        taskExecutor.setThreadNamePrefix("EclipseGeminiBlueprintExtenderThread-");
        taskExecutor.initialize();
        this.isTaskExecutorManagedInternally = true;
        return taskExecutor;
    }

    private TaskExecutor createDefaultShutdownTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadNamePrefix("Gemini Blueprint context shutdown thread ");
        taskExecutor.setDaemon(true);
        taskExecutor.setMaxPoolSize(1);
        taskExecutor.initialize();
        this.isShutdownTaskExecutorManagedInternally = true;
        return taskExecutor;
    }

    private OsgiBundleApplicationContextEventMulticaster createDefaultEventMulticaster() {
        this.isMulticasterManagedInternally = true;
        return new OsgiBundleApplicationContextEventMulticasterAdapter((ApplicationEventMulticaster)new SimpleApplicationEventMulticaster());
    }

    private OsgiBundleApplicationContextListener createDefaultApplicationContextListener() {
        return new DefaultOsgiBundleApplicationContextListener(this.log);
    }

    private long getShutdownWaitTime(Properties properties) {
        return Long.parseLong(properties.getProperty(SHUTDOWN_WAIT_KEY));
    }

    private boolean getShutdownAsynchronously(Properties properties) {
        return Boolean.valueOf(properties.getProperty(SHUTDOWN_ASYNCHRONOUS_KEY));
    }

    private long getDependencyWaitTime(Properties properties) {
        return Long.parseLong(properties.getProperty(WAIT_FOR_DEPS_TIMEOUT_KEY));
    }

    private boolean getProcessAnnotations(Properties properties) {
        return Boolean.valueOf(properties.getProperty(PROCESS_ANNOTATIONS_KEY)) != false || Boolean.getBoolean(AUTO_ANNOTATION_PROCESSING);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TaskExecutor getTaskExecutor() {
        Object object = this.lock;
        synchronized (object) {
            return this.taskExecutor;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TaskExecutor getShutdownTaskExecutor() {
        Object object = this.lock;
        synchronized (object) {
            return this.shutdownTaskExecutor;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public OsgiBundleApplicationContextListener getContextEventListener() {
        Object object = this.lock;
        synchronized (object) {
            return this.contextEventListener;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getShutdownWaitTime() {
        Object object = this.lock;
        synchronized (object) {
            return this.shutdownWaitTime;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean shouldProcessAnnotation() {
        Object object = this.lock;
        synchronized (object) {
            return this.processAnnotation;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean shouldShutdownAsynchronously() {
        Object object = this.lock;
        synchronized (object) {
            return this.shutdownAsynchronously;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getDependencyWaitTime() {
        Object object = this.lock;
        synchronized (object) {
            return this.dependencyWaitTime;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public OsgiBundleApplicationContextEventMulticaster getEventMulticaster() {
        Object object = this.lock;
        synchronized (object) {
            return this.eventMulticaster;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setForceThreadShutdown(boolean forceThreadShutdown) {
        Object object = this.lock;
        synchronized (object) {
            this.forceThreadShutdown = forceThreadShutdown;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public OsgiApplicationContextCreator getContextCreator() {
        Object object = this.lock;
        synchronized (object) {
            return this.contextCreator;
        }
    }

    public List<OsgiBeanFactoryPostProcessor> getPostProcessors() {
        return this.postProcessors;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public List<OsgiServiceDependencyFactory> getDependencyFactories() {
        return this.dependencyFactories;
    }
}

