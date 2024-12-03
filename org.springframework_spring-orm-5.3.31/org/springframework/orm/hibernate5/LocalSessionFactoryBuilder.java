/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Converter
 *  javax.persistence.Embeddable
 *  javax.persistence.Entity
 *  javax.persistence.MappedSuperclass
 *  javax.transaction.TransactionManager
 *  org.hibernate.HibernateException
 *  org.hibernate.MappingException
 *  org.hibernate.SessionFactory
 *  org.hibernate.boot.MetadataSources
 *  org.hibernate.boot.registry.BootstrapServiceRegistryBuilder
 *  org.hibernate.cache.spi.RegionFactory
 *  org.hibernate.cfg.Configuration
 *  org.hibernate.context.spi.CurrentTenantIdentifierResolver
 *  org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode
 *  org.hibernate.service.ServiceRegistry
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.core.InfrastructureProxy
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.io.support.PathMatchingResourcePatternResolver
 *  org.springframework.core.io.support.ResourcePatternResolver
 *  org.springframework.core.io.support.ResourcePatternUtils
 *  org.springframework.core.task.AsyncTaskExecutor
 *  org.springframework.core.type.classreading.CachingMetadataReaderFactory
 *  org.springframework.core.type.classreading.MetadataReader
 *  org.springframework.core.type.classreading.MetadataReaderFactory
 *  org.springframework.core.type.filter.AnnotationTypeFilter
 *  org.springframework.core.type.filter.TypeFilter
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.jta.JtaTransactionManager
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.orm.hibernate5;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.persistence.Converter;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.InfrastructureProxy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.Nullable;
import org.springframework.orm.hibernate5.ConfigurableJtaPlatform;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.orm.hibernate5.SpringSessionContext;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class LocalSessionFactoryBuilder
extends Configuration {
    private static final String RESOURCE_PATTERN = "/**/*.class";
    private static final String PACKAGE_INFO_SUFFIX = ".package-info";
    private static final TypeFilter[] DEFAULT_ENTITY_TYPE_FILTERS = new TypeFilter[]{new AnnotationTypeFilter(Entity.class, false), new AnnotationTypeFilter(Embeddable.class, false), new AnnotationTypeFilter(MappedSuperclass.class, false)};
    private static final TypeFilter CONVERTER_TYPE_FILTER = new AnnotationTypeFilter(Converter.class, false);
    private final ResourcePatternResolver resourcePatternResolver;
    @Nullable
    private TypeFilter[] entityTypeFilters = DEFAULT_ENTITY_TYPE_FILTERS;

    public LocalSessionFactoryBuilder(@Nullable DataSource dataSource) {
        this(dataSource, (ResourceLoader)new PathMatchingResourcePatternResolver());
    }

    public LocalSessionFactoryBuilder(@Nullable DataSource dataSource, ClassLoader classLoader) {
        this(dataSource, (ResourceLoader)new PathMatchingResourcePatternResolver(classLoader));
    }

    public LocalSessionFactoryBuilder(@Nullable DataSource dataSource, ResourceLoader resourceLoader) {
        this(dataSource, resourceLoader, new MetadataSources((ServiceRegistry)new BootstrapServiceRegistryBuilder().applyClassLoader(resourceLoader.getClassLoader()).build()));
    }

    public LocalSessionFactoryBuilder(@Nullable DataSource dataSource, ResourceLoader resourceLoader, MetadataSources metadataSources) {
        super(metadataSources);
        this.getProperties().put("hibernate.current_session_context_class", SpringSessionContext.class.getName());
        if (dataSource != null) {
            this.getProperties().put("hibernate.connection.datasource", dataSource);
        }
        this.getProperties().put("hibernate.connection.handling_mode", PhysicalConnectionHandlingMode.DELAYED_ACQUISITION_AND_HOLD);
        this.getProperties().put("hibernate.classLoaders", Collections.singleton(resourceLoader.getClassLoader()));
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver((ResourceLoader)resourceLoader);
    }

    public LocalSessionFactoryBuilder setJtaTransactionManager(Object jtaTransactionManager) {
        Assert.notNull((Object)jtaTransactionManager, (String)"Transaction manager reference must not be null");
        if (jtaTransactionManager instanceof JtaTransactionManager) {
            boolean webspherePresent = ClassUtils.isPresent((String)"com.ibm.wsspi.uow.UOWManager", (ClassLoader)((Object)((Object)this)).getClass().getClassLoader());
            if (webspherePresent) {
                this.getProperties().put("hibernate.transaction.jta.platform", "org.hibernate.engine.transaction.jta.platform.internal.WebSphereExtendedJtaPlatform");
            } else {
                JtaTransactionManager jtaTm = (JtaTransactionManager)jtaTransactionManager;
                if (jtaTm.getTransactionManager() == null) {
                    throw new IllegalArgumentException("Can only apply JtaTransactionManager which has a TransactionManager reference set");
                }
                this.getProperties().put("hibernate.transaction.jta.platform", new ConfigurableJtaPlatform(jtaTm.getTransactionManager(), jtaTm.getUserTransaction(), jtaTm.getTransactionSynchronizationRegistry()));
            }
        } else if (jtaTransactionManager instanceof TransactionManager) {
            this.getProperties().put("hibernate.transaction.jta.platform", new ConfigurableJtaPlatform((TransactionManager)jtaTransactionManager, null, null));
        } else {
            throw new IllegalArgumentException("Unknown transaction manager type: " + jtaTransactionManager.getClass().getName());
        }
        this.getProperties().put("hibernate.transaction.coordinator_class", "jta");
        this.getProperties().put("hibernate.connection.handling_mode", PhysicalConnectionHandlingMode.DELAYED_ACQUISITION_AND_RELEASE_AFTER_STATEMENT);
        return this;
    }

    public LocalSessionFactoryBuilder setBeanContainer(ConfigurableListableBeanFactory beanFactory) {
        this.getProperties().put("hibernate.resource.beans.container", new SpringBeanContainer(beanFactory));
        return this;
    }

    public LocalSessionFactoryBuilder setCacheRegionFactory(RegionFactory cacheRegionFactory) {
        this.getProperties().put("hibernate.cache.region.factory_class", cacheRegionFactory);
        return this;
    }

    public LocalSessionFactoryBuilder setMultiTenantConnectionProvider(MultiTenantConnectionProvider multiTenantConnectionProvider) {
        this.getProperties().put("hibernate.multi_tenant_connection_provider", multiTenantConnectionProvider);
        return this;
    }

    public void setCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
        this.getProperties().put("hibernate.tenant_identifier_resolver", currentTenantIdentifierResolver);
        super.setCurrentTenantIdentifierResolver(currentTenantIdentifierResolver);
    }

    public LocalSessionFactoryBuilder setEntityTypeFilters(TypeFilter ... entityTypeFilters) {
        this.entityTypeFilters = entityTypeFilters;
        return this;
    }

    public LocalSessionFactoryBuilder addAnnotatedClasses(Class<?> ... annotatedClasses) {
        for (Class<?> annotatedClass : annotatedClasses) {
            this.addAnnotatedClass(annotatedClass);
        }
        return this;
    }

    public LocalSessionFactoryBuilder addPackages(String ... annotatedPackages) {
        for (String annotatedPackage : annotatedPackages) {
            this.addPackage(annotatedPackage);
        }
        return this;
    }

    public LocalSessionFactoryBuilder scanPackages(String ... packagesToScan) throws HibernateException {
        TreeSet<String> entityClassNames = new TreeSet<String>();
        TreeSet<String> converterClassNames = new TreeSet<String>();
        TreeSet<String> packageNames = new TreeSet<String>();
        try {
            for (String pkg : packagesToScan) {
                String pattern = "classpath*:" + ClassUtils.convertClassNameToResourcePath((String)pkg) + RESOURCE_PATTERN;
                Resource[] resources = this.resourcePatternResolver.getResources(pattern);
                CachingMetadataReaderFactory readerFactory = new CachingMetadataReaderFactory((ResourceLoader)this.resourcePatternResolver);
                for (Resource resource : resources) {
                    try {
                        MetadataReader reader = readerFactory.getMetadataReader(resource);
                        String className = reader.getClassMetadata().getClassName();
                        if (this.matchesEntityTypeFilter(reader, (MetadataReaderFactory)readerFactory)) {
                            entityClassNames.add(className);
                            continue;
                        }
                        if (CONVERTER_TYPE_FILTER.match(reader, (MetadataReaderFactory)readerFactory)) {
                            converterClassNames.add(className);
                            continue;
                        }
                        if (!className.endsWith(PACKAGE_INFO_SUFFIX)) continue;
                        packageNames.add(className.substring(0, className.length() - PACKAGE_INFO_SUFFIX.length()));
                    }
                    catch (FileNotFoundException fileNotFoundException) {
                        // empty catch block
                    }
                }
            }
        }
        catch (IOException ex) {
            throw new MappingException("Failed to scan classpath for unlisted classes", (Throwable)ex);
        }
        try {
            ClassLoader cl = this.resourcePatternResolver.getClassLoader();
            for (String className : entityClassNames) {
                this.addAnnotatedClass(ClassUtils.forName((String)className, (ClassLoader)cl));
            }
            for (String className : converterClassNames) {
                this.addAttributeConverter(ClassUtils.forName((String)className, (ClassLoader)cl));
            }
            for (String packageName : packageNames) {
                this.addPackage(packageName);
            }
        }
        catch (ClassNotFoundException ex) {
            throw new MappingException("Failed to load annotated classes from classpath", (Throwable)ex);
        }
        return this;
    }

    private boolean matchesEntityTypeFilter(MetadataReader reader, MetadataReaderFactory readerFactory) throws IOException {
        if (this.entityTypeFilters != null) {
            for (TypeFilter filter : this.entityTypeFilters) {
                if (!filter.match(reader, readerFactory)) continue;
                return true;
            }
        }
        return false;
    }

    public SessionFactory buildSessionFactory(AsyncTaskExecutor bootstrapExecutor) {
        Assert.notNull((Object)bootstrapExecutor, (String)"AsyncTaskExecutor must not be null");
        return (SessionFactory)Proxy.newProxyInstance(this.resourcePatternResolver.getClassLoader(), new Class[]{SessionFactoryImplementor.class, InfrastructureProxy.class}, (InvocationHandler)new BootstrapSessionFactoryInvocationHandler(bootstrapExecutor));
    }

    private class BootstrapSessionFactoryInvocationHandler
    implements InvocationHandler {
        private final Future<SessionFactory> sessionFactoryFuture;

        public BootstrapSessionFactoryInvocationHandler(AsyncTaskExecutor bootstrapExecutor) {
            this.sessionFactoryFuture = bootstrapExecutor.submit(() -> ((LocalSessionFactoryBuilder)LocalSessionFactoryBuilder.this).buildSessionFactory());
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "equals": {
                    return proxy == args[0];
                }
                case "hashCode": {
                    return System.identityHashCode(proxy);
                }
                case "getProperties": {
                    return LocalSessionFactoryBuilder.this.getProperties();
                }
                case "getWrappedObject": {
                    return this.getSessionFactory();
                }
            }
            try {
                return method.invoke((Object)this.getSessionFactory(), args);
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }

        private SessionFactory getSessionFactory() {
            try {
                return this.sessionFactoryFuture.get();
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted during initialization of Hibernate SessionFactory", ex);
            }
            catch (ExecutionException ex) {
                Throwable cause = ex.getCause();
                if (cause instanceof HibernateException) {
                    throw (HibernateException)cause;
                }
                throw new IllegalStateException("Failed to asynchronously initialize Hibernate SessionFactory: " + ex.getMessage(), cause);
            }
        }
    }
}

