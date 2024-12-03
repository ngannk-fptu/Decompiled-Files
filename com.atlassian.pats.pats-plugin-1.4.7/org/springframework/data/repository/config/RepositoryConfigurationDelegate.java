/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.beans.factory.config.DependencyDescriptor
 *  org.springframework.beans.factory.parsing.BeanComponentDefinition
 *  org.springframework.beans.factory.support.AbstractBeanDefinition
 *  org.springframework.beans.factory.support.AutowireCandidateResolver
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver
 *  org.springframework.context.support.GenericApplicationContext
 *  org.springframework.core.env.Environment
 *  org.springframework.core.env.EnvironmentCapable
 *  org.springframework.core.env.StandardEnvironment
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.io.support.SpringFactoriesLoader
 *  org.springframework.core.log.LogMessage
 *  org.springframework.core.metrics.ApplicationStartup
 *  org.springframework.core.metrics.StartupStep
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StopWatch
 */
package org.springframework.data.repository.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.log.LogMessage;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.data.repository.config.DeferredRepositoryInitializationListener;
import org.springframework.data.repository.config.RepositoryBeanDefinitionBuilder;
import org.springframework.data.repository.config.RepositoryConfiguration;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

public class RepositoryConfigurationDelegate {
    private static final String REPOSITORY_REGISTRATION = "Spring Data %s - Registering repository: %s - Interface: %s - Factory: %s";
    private static final String MULTIPLE_MODULES = "Multiple Spring Data modules found, entering strict repository configuration mode!";
    private static final String NON_DEFAULT_AUTOWIRE_CANDIDATE_RESOLVER = "Non-default AutowireCandidateResolver (%s) detected. Skipping the registration of LazyRepositoryInjectionPointResolver. Lazy repository injection will not be working!";
    static final String FACTORY_BEAN_OBJECT_TYPE = "factoryBeanObjectType";
    private static final Log logger = LogFactory.getLog(RepositoryConfigurationDelegate.class);
    private final RepositoryConfigurationSource configurationSource;
    private final ResourceLoader resourceLoader;
    private final Environment environment;
    private final boolean isXml;
    private final boolean inMultiStoreMode;

    public RepositoryConfigurationDelegate(RepositoryConfigurationSource configurationSource, ResourceLoader resourceLoader, Environment environment) {
        this.isXml = configurationSource instanceof XmlRepositoryConfigurationSource;
        boolean isAnnotation = configurationSource instanceof AnnotationRepositoryConfigurationSource;
        Assert.isTrue((this.isXml || isAnnotation ? 1 : 0) != 0, (String)"Configuration source must either be an Xml- or an AnnotationBasedConfigurationSource!");
        Assert.notNull((Object)resourceLoader, (String)"ResourceLoader must not be null!");
        this.configurationSource = configurationSource;
        this.resourceLoader = resourceLoader;
        this.environment = RepositoryConfigurationDelegate.defaultEnvironment(environment, resourceLoader);
        this.inMultiStoreMode = this.multipleStoresDetected();
    }

    private static Environment defaultEnvironment(@Nullable Environment environment, @Nullable ResourceLoader resourceLoader) {
        if (environment != null) {
            return environment;
        }
        return resourceLoader instanceof EnvironmentCapable ? ((EnvironmentCapable)resourceLoader).getEnvironment() : new StandardEnvironment();
    }

    public List<BeanComponentDefinition> registerRepositoriesIn(BeanDefinitionRegistry registry, RepositoryConfigurationExtension extension) {
        if (logger.isInfoEnabled()) {
            logger.info((Object)LogMessage.format((String)"Bootstrapping Spring Data %s repositories in %s mode.", (Object)extension.getModuleName(), (Object)this.configurationSource.getBootstrapMode().name()));
        }
        extension.registerBeansForRoot(registry, this.configurationSource);
        RepositoryBeanDefinitionBuilder builder = new RepositoryBeanDefinitionBuilder(registry, extension, this.configurationSource, this.resourceLoader, this.environment);
        ArrayList<BeanComponentDefinition> definitions = new ArrayList<BeanComponentDefinition>();
        StopWatch watch = new StopWatch();
        if (logger.isDebugEnabled()) {
            logger.debug((Object)LogMessage.format((String)"Scanning for %s repositories in packages %s.", (Object)extension.getModuleName(), (Object)this.configurationSource.getBasePackages().stream().collect(Collectors.joining(", "))));
        }
        ApplicationStartup startup = RepositoryConfigurationDelegate.getStartup(registry);
        StartupStep repoScan = startup.start("spring.data.repository.scanning");
        repoScan.tag("dataModule", extension.getModuleName());
        repoScan.tag("basePackages", () -> this.configurationSource.getBasePackages().stream().collect(Collectors.joining(", ")));
        watch.start();
        Collection<RepositoryConfiguration<RepositoryConfigurationSource>> configurations = extension.getRepositoryConfigurations(this.configurationSource, this.resourceLoader, this.inMultiStoreMode);
        HashMap configurationsByRepositoryName = new HashMap(configurations.size());
        for (RepositoryConfiguration<RepositoryConfigurationSource> configuration : configurations) {
            configurationsByRepositoryName.put(configuration.getRepositoryInterface(), configuration);
            BeanDefinitionBuilder definitionBuilder = builder.build(configuration);
            extension.postProcess(definitionBuilder, this.configurationSource);
            if (this.isXml) {
                extension.postProcess(definitionBuilder, (XmlRepositoryConfigurationSource)this.configurationSource);
            } else {
                extension.postProcess(definitionBuilder, (AnnotationRepositoryConfigurationSource)this.configurationSource);
            }
            AbstractBeanDefinition beanDefinition = definitionBuilder.getBeanDefinition();
            beanDefinition.setResourceDescription(configuration.getResourceDescription());
            String beanName = this.configurationSource.generateBeanName((BeanDefinition)beanDefinition);
            if (logger.isTraceEnabled()) {
                logger.trace((Object)LogMessage.format((String)REPOSITORY_REGISTRATION, (Object)extension.getModuleName(), (Object)beanName, (Object)configuration.getRepositoryInterface(), (Object)configuration.getRepositoryFactoryBeanClassName()));
            }
            beanDefinition.setAttribute(FACTORY_BEAN_OBJECT_TYPE, (Object)configuration.getRepositoryInterface());
            registry.registerBeanDefinition(beanName, (BeanDefinition)beanDefinition);
            definitions.add(new BeanComponentDefinition((BeanDefinition)beanDefinition, beanName));
        }
        RepositoryConfigurationDelegate.potentiallyLazifyRepositories(configurationsByRepositoryName, registry, this.configurationSource.getBootstrapMode());
        watch.stop();
        repoScan.tag("repository.count", Integer.toString(configurations.size()));
        repoScan.end();
        if (logger.isInfoEnabled()) {
            logger.info((Object)LogMessage.format((String)"Finished Spring Data repository scanning in %s ms. Found %s %s repository interfaces.", (Object)watch.getLastTaskTimeMillis(), (Object)configurations.size(), (Object)extension.getModuleName()));
        }
        return definitions;
    }

    private static void potentiallyLazifyRepositories(Map<String, RepositoryConfiguration<?>> configurations, BeanDefinitionRegistry registry, BootstrapMode mode) {
        if (!DefaultListableBeanFactory.class.isInstance(registry) || mode.equals((Object)BootstrapMode.DEFAULT)) {
            return;
        }
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)DefaultListableBeanFactory.class.cast(registry);
        AutowireCandidateResolver resolver = beanFactory.getAutowireCandidateResolver();
        if (!Arrays.asList(ContextAnnotationAutowireCandidateResolver.class, LazyRepositoryInjectionPointResolver.class).contains(resolver.getClass())) {
            logger.warn((Object)LogMessage.format((String)NON_DEFAULT_AUTOWIRE_CANDIDATE_RESOLVER, (Object)resolver.getClass().getName()));
            return;
        }
        LazyRepositoryInjectionPointResolver newResolver = LazyRepositoryInjectionPointResolver.class.isInstance(resolver) ? ((LazyRepositoryInjectionPointResolver)((Object)LazyRepositoryInjectionPointResolver.class.cast(resolver))).withAdditionalConfigurations(configurations) : new LazyRepositoryInjectionPointResolver(configurations);
        beanFactory.setAutowireCandidateResolver((AutowireCandidateResolver)newResolver);
        if (mode.equals((Object)BootstrapMode.DEFERRED)) {
            logger.debug((Object)"Registering deferred repository initialization listener.");
            beanFactory.registerSingleton(DeferredRepositoryInitializationListener.class.getName(), (Object)new DeferredRepositoryInitializationListener((ListableBeanFactory)beanFactory));
        }
    }

    private boolean multipleStoresDetected() {
        boolean multipleModulesFound;
        boolean bl = multipleModulesFound = SpringFactoriesLoader.loadFactoryNames(RepositoryFactorySupport.class, (ClassLoader)this.resourceLoader.getClassLoader()).size() > 1;
        if (multipleModulesFound) {
            logger.info((Object)MULTIPLE_MODULES);
        }
        return multipleModulesFound;
    }

    private static ApplicationStartup getStartup(BeanDefinitionRegistry registry) {
        if (registry instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory)registry).getApplicationStartup();
        }
        if (registry instanceof GenericApplicationContext) {
            return ((GenericApplicationContext)registry).getDefaultListableBeanFactory().getApplicationStartup();
        }
        return ApplicationStartup.DEFAULT;
    }

    static class LazyRepositoryInjectionPointResolver
    extends ContextAnnotationAutowireCandidateResolver {
        private static final Log logger = LogFactory.getLog(LazyRepositoryInjectionPointResolver.class);
        private final Map<String, RepositoryConfiguration<?>> configurations;

        public LazyRepositoryInjectionPointResolver(Map<String, RepositoryConfiguration<?>> configurations) {
            this.configurations = configurations;
        }

        LazyRepositoryInjectionPointResolver withAdditionalConfigurations(Map<String, RepositoryConfiguration<?>> configurations) {
            HashMap map = new HashMap(this.configurations);
            map.putAll(configurations);
            return new LazyRepositoryInjectionPointResolver(map);
        }

        protected boolean isLazy(DependencyDescriptor descriptor) {
            Class type = descriptor.getDependencyType();
            RepositoryConfiguration<?> configuration = this.configurations.get(type.getName());
            if (configuration == null) {
                return super.isLazy(descriptor);
            }
            boolean lazyInit = configuration.isLazyInit();
            if (lazyInit) {
                logger.debug((Object)LogMessage.format((String)"Creating lazy injection proxy for %s\u2026", (Object)configuration.getRepositoryInterface()));
            }
            return lazyInit;
        }
    }
}

