/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.gemini.blueprint.io.OsgiBundleResource
 *  org.eclipse.gemini.blueprint.io.OsgiBundleResourcePatternResolver
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.PropertyEditorRegistrar
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.config.Scope
 *  org.springframework.beans.factory.support.AbstractBeanFactory
 *  org.springframework.beans.factory.support.SecurityContextProvider
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextException
 *  org.springframework.context.support.AbstractRefreshableApplicationContext
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.support.ResourcePatternResolver
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.eclipse.gemini.blueprint.context.support;

import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Dictionary;
import java.util.Map;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.eclipse.gemini.blueprint.context.ConfigurableOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.context.support.AccessControlFactory;
import org.eclipse.gemini.blueprint.context.support.BundleContextAwareProcessor;
import org.eclipse.gemini.blueprint.context.support.OsgiPropertyEditorRegistrar;
import org.eclipse.gemini.blueprint.context.support.internal.classloader.ClassLoaderFactory;
import org.eclipse.gemini.blueprint.context.support.internal.scope.OsgiBundleScope;
import org.eclipse.gemini.blueprint.io.OsgiBundleResource;
import org.eclipse.gemini.blueprint.io.OsgiBundleResourcePatternResolver;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.eclipse.gemini.blueprint.util.OsgiServiceUtils;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.eclipse.gemini.blueprint.util.internal.ClassUtils;
import org.eclipse.gemini.blueprint.util.internal.MapBasedDictionary;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.SecurityContextProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public abstract class AbstractOsgiBundleApplicationContext
extends AbstractRefreshableApplicationContext
implements ConfigurableOsgiBundleApplicationContext {
    private static final String EXPORTER_IMPORTER_DEPENDENCY_MANAGER = "org.eclipse.gemini.blueprint.service.dependency.internal.MandatoryDependencyBeanPostProcessor";
    private Bundle bundle;
    private BundleContext bundleContext;
    private String[] configLocations;
    private ServiceRegistration serviceRegistration;
    private boolean publishContextAsService = true;
    private ClassLoader classLoader;
    private ResourcePatternResolver osgiPatternResolver;
    private volatile AccessControlContext acc;

    public AbstractOsgiBundleApplicationContext() {
        this.setDisplayName("Root OsgiBundleApplicationContext");
    }

    public AbstractOsgiBundleApplicationContext(ApplicationContext parent) {
        super(parent);
    }

    @Override
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
        this.bundle = bundleContext.getBundle();
        this.osgiPatternResolver = this.createResourcePatternResolver();
        if (this.getClassLoader() == null) {
            this.setClassLoader(this.createBundleClassLoader(this.bundle));
        }
        this.setDisplayName(ClassUtils.getShortName(this.getClass()) + "(bundle=" + this.getBundleSymbolicName() + ", config=" + StringUtils.arrayToCommaDelimitedString((Object[])this.getConfigLocations()) + ")");
        this.acc = AccessControlFactory.createContext(this.bundle);
    }

    @Override
    public BundleContext getBundleContext() {
        return this.bundleContext;
    }

    @Override
    public Bundle getBundle() {
        return this.bundle;
    }

    @Override
    public void setConfigLocations(String ... configLocations) {
        this.configLocations = configLocations;
    }

    public String[] getConfigLocations() {
        return this.configLocations != null ? this.configLocations : this.getDefaultConfigLocations();
    }

    protected void doClose() {
        this.unpublishContextAsOsgiService();
        super.doClose();
    }

    protected void destroyBeans() {
        super.destroyBeans();
        try {
            this.cleanOsgiBundleScope(this.getBeanFactory());
        }
        catch (Exception ex) {
            this.logger.warn((Object)"got exception when closing", (Throwable)ex);
        }
    }

    protected String[] getDefaultConfigLocations() {
        return null;
    }

    protected void prepareRefresh() {
        super.prepareRefresh();
        this.unpublishContextAsOsgiService();
    }

    protected void finishRefresh() {
        super.finishRefresh();
        this.publishContextAsOsgiServiceIfNecessary();
    }

    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        super.postProcessBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor((BeanPostProcessor)new BundleContextAwareProcessor(this.bundleContext));
        beanFactory.ignoreDependencyInterface(BundleContextAware.class);
        if (beanFactory instanceof AbstractBeanFactory) {
            AbstractBeanFactory bf = (AbstractBeanFactory)beanFactory;
            bf.setSecurityContextProvider(new SecurityContextProvider(){

                public AccessControlContext getAccessControlContext() {
                    return AbstractOsgiBundleApplicationContext.this.acc;
                }
            });
        }
        this.enforceExporterImporterDependency(beanFactory);
        this.addPredefinedBean(beanFactory, "bundleContext", this.bundleContext);
        this.addPredefinedBean(beanFactory, "bundle", this.bundle);
        this.registerPropertyEditors(beanFactory);
        beanFactory.registerScope("bundle", (Scope)new OsgiBundleScope());
    }

    private void addPredefinedBean(ConfigurableListableBeanFactory beanFactory, String name, Object value) {
        if (!beanFactory.containsLocalBean(name)) {
            this.logger.debug((Object)("Registering pre-defined bean named " + name));
            beanFactory.registerSingleton(name, value);
        } else {
            this.logger.warn((Object)("A bean named " + name + " already exists; aborting registration of the predefined value..."));
        }
    }

    private void enforceExporterImporterDependency(ConfigurableListableBeanFactory beanFactory) {
        Object instance = null;
        instance = AccessController.doPrivileged(new PrivilegedAction<Object>(){

            @Override
            public Object run() {
                ClassLoader loader = AbstractOsgiBundleApplicationContext.class.getClassLoader();
                try {
                    Class<?> managerClass = loader.loadClass(AbstractOsgiBundleApplicationContext.EXPORTER_IMPORTER_DEPENDENCY_MANAGER);
                    return BeanUtils.instantiateClass(managerClass);
                }
                catch (ClassNotFoundException cnfe) {
                    throw new ApplicationContextException("Cannot load class org.eclipse.gemini.blueprint.service.dependency.internal.MandatoryDependencyBeanPostProcessor", (Throwable)cnfe);
                }
            }
        });
        Assert.isInstanceOf(BeanFactoryAware.class, (Object)instance);
        Assert.isInstanceOf(BeanPostProcessor.class, (Object)instance);
        ((BeanFactoryAware)instance).setBeanFactory((BeanFactory)beanFactory);
        beanFactory.addBeanPostProcessor((BeanPostProcessor)instance);
    }

    private void registerPropertyEditors(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.addPropertyEditorRegistrar((PropertyEditorRegistrar)new OsgiPropertyEditorRegistrar(this.getClassLoader()));
    }

    private void cleanOsgiBundleScope(ConfigurableListableBeanFactory beanFactory) {
        Scope scope = beanFactory.getRegisteredScope("bundle");
        if (scope != null && scope instanceof OsgiBundleScope) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)"Destroying existing bundle scope beans...");
            }
            ((OsgiBundleScope)scope).destroy();
        }
    }

    private void publishContextAsOsgiServiceIfNecessary() {
        if (this.publishContextAsService && this.serviceRegistration == null) {
            boolean hasSecurity;
            MapBasedDictionary serviceProperties = new MapBasedDictionary();
            this.customizeApplicationContextServiceProperties(serviceProperties);
            if (this.logger.isInfoEnabled()) {
                this.logger.info((Object)("Publishing application context as OSGi service with properties " + serviceProperties));
            }
            Class<?>[] classes = org.eclipse.gemini.blueprint.util.internal.ClassUtils.getClassHierarchy(this.getClass(), ClassUtils.ClassSet.INTERFACES);
            Class<?>[] filterClasses = org.eclipse.gemini.blueprint.util.internal.ClassUtils.getVisibleClasses(classes, this.getClass().getClassLoader());
            Object[] serviceNames = org.eclipse.gemini.blueprint.util.internal.ClassUtils.toStringArray(filterClasses);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Publishing service under classes " + ObjectUtils.nullSafeToString((Object[])serviceNames)));
            }
            boolean bl = hasSecurity = System.getSecurityManager() != null;
            if (hasSecurity) {
                try {
                    this.serviceRegistration = AccessController.doPrivileged(new PrivilegedAction<ServiceRegistration>((String[])serviceNames, serviceProperties){
                        final /* synthetic */ String[] val$serviceNames;
                        final /* synthetic */ Dictionary val$serviceProperties;
                        {
                            this.val$serviceNames = stringArray;
                            this.val$serviceProperties = dictionary;
                        }

                        @Override
                        public ServiceRegistration run() {
                            return AbstractOsgiBundleApplicationContext.this.getBundleContext().registerService(this.val$serviceNames, (Object)AbstractOsgiBundleApplicationContext.this, this.val$serviceProperties);
                        }
                    }, this.acc);
                }
                catch (AccessControlException ex) {
                    this.logger.error((Object)"Application context service publication aborted due to security issues - does the bundle has the rights to publish the service ? ", (Throwable)ex);
                }
            } else {
                this.serviceRegistration = this.getBundleContext().registerService((String[])serviceNames, (Object)this, serviceProperties);
            }
        } else if (this.logger.isInfoEnabled()) {
            this.logger.info((Object)("Not publishing application context OSGi service for bundle " + OsgiStringUtils.nullSafeNameAndSymName(this.bundle)));
        }
    }

    private void unpublishContextAsOsgiService() {
        if (OsgiServiceUtils.unregisterService(this.serviceRegistration)) {
            this.logger.info((Object)("Unpublishing application context OSGi service for bundle " + OsgiStringUtils.nullSafeNameAndSymName(this.bundle)));
            this.serviceRegistration = null;
        } else if (this.publishContextAsService) {
            this.logger.info((Object)"Application Context service already unpublished");
        }
    }

    protected void customizeApplicationContextServiceProperties(Map<String, Object> serviceProperties) {
        serviceProperties.put("org.eclipse.gemini.blueprint.context.service.name", this.getBundleSymbolicName());
        serviceProperties.put("org.springframework.context.service.name", this.getBundleSymbolicName());
        serviceProperties.put("Bundle-SymbolicName", this.getBundleSymbolicName());
        serviceProperties.put("Bundle-Version", OsgiBundleUtils.getBundleVersion(this.bundle));
    }

    private String getBundleSymbolicName() {
        return OsgiStringUtils.nullSafeSymbolicName(this.getBundle());
    }

    protected ResourcePatternResolver createResourcePatternResolver() {
        return new OsgiBundleResourcePatternResolver(this.getBundle());
    }

    protected ResourcePatternResolver getResourcePatternResolver() {
        return this.osgiPatternResolver;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public Resource getResource(String location) {
        return this.osgiPatternResolver != null ? this.osgiPatternResolver.getResource(location) : null;
    }

    public Resource[] getResources(String locationPattern) throws IOException {
        return this.osgiPatternResolver != null ? this.osgiPatternResolver.getResources(locationPattern) : null;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    protected Resource getResourceByPath(String path) {
        Assert.notNull((Object)path, (String)"Path is required");
        return new OsgiBundleResource(this.bundle, path);
    }

    @Override
    public void setPublishContextAsService(boolean publishContextAsService) {
        this.publishContextAsService = publishContextAsService;
    }

    private ClassLoader createBundleClassLoader(Bundle bundle) {
        return ClassLoaderFactory.getBundleClassLoaderFor(bundle);
    }
}

