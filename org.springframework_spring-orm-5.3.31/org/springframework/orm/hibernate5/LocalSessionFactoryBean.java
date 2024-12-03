/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.Interceptor
 *  org.hibernate.SessionFactory
 *  org.hibernate.boot.MetadataSources
 *  org.hibernate.boot.model.naming.ImplicitNamingStrategy
 *  org.hibernate.boot.model.naming.PhysicalNamingStrategy
 *  org.hibernate.boot.registry.BootstrapServiceRegistryBuilder
 *  org.hibernate.cache.spi.RegionFactory
 *  org.hibernate.cfg.Configuration
 *  org.hibernate.context.spi.CurrentTenantIdentifierResolver
 *  org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider
 *  org.hibernate.integrator.spi.Integrator
 *  org.hibernate.service.ServiceRegistry
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.context.ResourceLoaderAware
 *  org.springframework.core.io.ClassPathResource
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.io.support.PathMatchingResourcePatternResolver
 *  org.springframework.core.io.support.ResourcePatternResolver
 *  org.springframework.core.io.support.ResourcePatternUtils
 *  org.springframework.core.task.AsyncTaskExecutor
 *  org.springframework.core.type.filter.TypeFilter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 */
package org.springframework.orm.hibernate5;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.Nullable;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.util.ClassUtils;

public class LocalSessionFactoryBean
extends HibernateExceptionTranslator
implements FactoryBean<SessionFactory>,
ResourceLoaderAware,
BeanFactoryAware,
InitializingBean,
DisposableBean {
    @Nullable
    private DataSource dataSource;
    @Nullable
    private Resource[] configLocations;
    @Nullable
    private String[] mappingResources;
    @Nullable
    private Resource[] mappingLocations;
    @Nullable
    private Resource[] cacheableMappingLocations;
    @Nullable
    private Resource[] mappingJarLocations;
    @Nullable
    private Resource[] mappingDirectoryLocations;
    @Nullable
    private Interceptor entityInterceptor;
    @Nullable
    private ImplicitNamingStrategy implicitNamingStrategy;
    @Nullable
    private PhysicalNamingStrategy physicalNamingStrategy;
    @Nullable
    private Object jtaTransactionManager;
    @Nullable
    private RegionFactory cacheRegionFactory;
    @Nullable
    private MultiTenantConnectionProvider multiTenantConnectionProvider;
    @Nullable
    private CurrentTenantIdentifierResolver currentTenantIdentifierResolver;
    @Nullable
    private Properties hibernateProperties;
    @Nullable
    private TypeFilter[] entityTypeFilters;
    @Nullable
    private Class<?>[] annotatedClasses;
    @Nullable
    private String[] annotatedPackages;
    @Nullable
    private String[] packagesToScan;
    @Nullable
    private AsyncTaskExecutor bootstrapExecutor;
    @Nullable
    private Integrator[] hibernateIntegrators;
    private boolean metadataSourcesAccessed = false;
    @Nullable
    private MetadataSources metadataSources;
    @Nullable
    private ResourcePatternResolver resourcePatternResolver;
    @Nullable
    private ConfigurableListableBeanFactory beanFactory;
    @Nullable
    private Configuration configuration;
    @Nullable
    private SessionFactory sessionFactory;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setConfigLocation(Resource configLocation) {
        this.configLocations = new Resource[]{configLocation};
    }

    public void setConfigLocations(Resource ... configLocations) {
        this.configLocations = configLocations;
    }

    public void setMappingResources(String ... mappingResources) {
        this.mappingResources = mappingResources;
    }

    public void setMappingLocations(Resource ... mappingLocations) {
        this.mappingLocations = mappingLocations;
    }

    public void setCacheableMappingLocations(Resource ... cacheableMappingLocations) {
        this.cacheableMappingLocations = cacheableMappingLocations;
    }

    public void setMappingJarLocations(Resource ... mappingJarLocations) {
        this.mappingJarLocations = mappingJarLocations;
    }

    public void setMappingDirectoryLocations(Resource ... mappingDirectoryLocations) {
        this.mappingDirectoryLocations = mappingDirectoryLocations;
    }

    public void setEntityInterceptor(Interceptor entityInterceptor) {
        this.entityInterceptor = entityInterceptor;
    }

    public void setImplicitNamingStrategy(ImplicitNamingStrategy implicitNamingStrategy) {
        this.implicitNamingStrategy = implicitNamingStrategy;
    }

    public void setPhysicalNamingStrategy(PhysicalNamingStrategy physicalNamingStrategy) {
        this.physicalNamingStrategy = physicalNamingStrategy;
    }

    public void setJtaTransactionManager(Object jtaTransactionManager) {
        this.jtaTransactionManager = jtaTransactionManager;
    }

    public void setCacheRegionFactory(RegionFactory cacheRegionFactory) {
        this.cacheRegionFactory = cacheRegionFactory;
    }

    public void setMultiTenantConnectionProvider(MultiTenantConnectionProvider multiTenantConnectionProvider) {
        this.multiTenantConnectionProvider = multiTenantConnectionProvider;
    }

    public void setCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
        this.currentTenantIdentifierResolver = currentTenantIdentifierResolver;
    }

    public void setHibernateProperties(Properties hibernateProperties) {
        this.hibernateProperties = hibernateProperties;
    }

    public Properties getHibernateProperties() {
        if (this.hibernateProperties == null) {
            this.hibernateProperties = new Properties();
        }
        return this.hibernateProperties;
    }

    public void setEntityTypeFilters(TypeFilter ... entityTypeFilters) {
        this.entityTypeFilters = entityTypeFilters;
    }

    public void setAnnotatedClasses(Class<?> ... annotatedClasses) {
        this.annotatedClasses = annotatedClasses;
    }

    public void setAnnotatedPackages(String ... annotatedPackages) {
        this.annotatedPackages = annotatedPackages;
    }

    public void setPackagesToScan(String ... packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    public void setBootstrapExecutor(AsyncTaskExecutor bootstrapExecutor) {
        this.bootstrapExecutor = bootstrapExecutor;
    }

    public void setHibernateIntegrators(Integrator ... hibernateIntegrators) {
        this.hibernateIntegrators = hibernateIntegrators;
    }

    public void setMetadataSources(MetadataSources metadataSources) {
        this.metadataSourcesAccessed = true;
        this.metadataSources = metadataSources;
    }

    public MetadataSources getMetadataSources() {
        this.metadataSourcesAccessed = true;
        if (this.metadataSources == null) {
            BootstrapServiceRegistryBuilder builder = new BootstrapServiceRegistryBuilder();
            if (this.resourcePatternResolver != null) {
                builder = builder.applyClassLoader(this.resourcePatternResolver.getClassLoader());
            }
            if (this.hibernateIntegrators != null) {
                for (Integrator integrator : this.hibernateIntegrators) {
                    builder = builder.applyIntegrator(integrator);
                }
            }
            this.metadataSources = new MetadataSources((ServiceRegistry)builder.build());
        }
        return this.metadataSources;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver((ResourceLoader)resourceLoader);
    }

    public ResourceLoader getResourceLoader() {
        if (this.resourcePatternResolver == null) {
            this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
        }
        return this.resourcePatternResolver;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableListableBeanFactory && ClassUtils.isPresent((String)"org.hibernate.resource.beans.container.spi.BeanContainer", (ClassLoader)this.getClass().getClassLoader())) {
            this.beanFactory = (ConfigurableListableBeanFactory)beanFactory;
        }
    }

    public void afterPropertiesSet() throws IOException {
        if (this.metadataSources != null && !this.metadataSourcesAccessed) {
            this.metadataSources = null;
        }
        LocalSessionFactoryBuilder sfb = new LocalSessionFactoryBuilder(this.dataSource, this.getResourceLoader(), this.getMetadataSources());
        if (this.configLocations != null) {
            for (Resource resource : this.configLocations) {
                sfb.configure(resource.getURL());
            }
        }
        if (this.mappingResources != null) {
            for (String string : this.mappingResources) {
                ClassPathResource mr = new ClassPathResource(string.trim(), this.getResourceLoader().getClassLoader());
                sfb.addInputStream(mr.getInputStream());
            }
        }
        if (this.mappingLocations != null) {
            for (Resource resource : this.mappingLocations) {
                sfb.addInputStream(resource.getInputStream());
            }
        }
        if (this.cacheableMappingLocations != null) {
            for (Resource resource : this.cacheableMappingLocations) {
                sfb.addCacheableFile(resource.getFile());
            }
        }
        if (this.mappingJarLocations != null) {
            for (Resource resource : this.mappingJarLocations) {
                sfb.addJar(resource.getFile());
            }
        }
        if (this.mappingDirectoryLocations != null) {
            for (Resource resource : this.mappingDirectoryLocations) {
                File file = resource.getFile();
                if (!file.isDirectory()) {
                    throw new IllegalArgumentException("Mapping directory location [" + resource + "] does not denote a directory");
                }
                sfb.addDirectory(file);
            }
        }
        if (this.entityInterceptor != null) {
            sfb.setInterceptor(this.entityInterceptor);
        }
        if (this.implicitNamingStrategy != null) {
            sfb.setImplicitNamingStrategy(this.implicitNamingStrategy);
        }
        if (this.physicalNamingStrategy != null) {
            sfb.setPhysicalNamingStrategy(this.physicalNamingStrategy);
        }
        if (this.jtaTransactionManager != null) {
            sfb.setJtaTransactionManager(this.jtaTransactionManager);
        }
        if (this.beanFactory != null) {
            sfb.setBeanContainer(this.beanFactory);
        }
        if (this.cacheRegionFactory != null) {
            sfb.setCacheRegionFactory(this.cacheRegionFactory);
        }
        if (this.multiTenantConnectionProvider != null) {
            sfb.setMultiTenantConnectionProvider(this.multiTenantConnectionProvider);
        }
        if (this.currentTenantIdentifierResolver != null) {
            sfb.setCurrentTenantIdentifierResolver(this.currentTenantIdentifierResolver);
        }
        if (this.hibernateProperties != null) {
            sfb.addProperties(this.hibernateProperties);
        }
        if (this.entityTypeFilters != null) {
            sfb.setEntityTypeFilters(this.entityTypeFilters);
        }
        if (this.annotatedClasses != null) {
            sfb.addAnnotatedClasses(this.annotatedClasses);
        }
        if (this.annotatedPackages != null) {
            sfb.addPackages(this.annotatedPackages);
        }
        if (this.packagesToScan != null) {
            sfb.scanPackages(this.packagesToScan);
        }
        this.configuration = sfb;
        this.sessionFactory = this.buildSessionFactory(sfb);
    }

    protected SessionFactory buildSessionFactory(LocalSessionFactoryBuilder sfb) {
        return this.bootstrapExecutor != null ? sfb.buildSessionFactory(this.bootstrapExecutor) : sfb.buildSessionFactory();
    }

    public final Configuration getConfiguration() {
        if (this.configuration == null) {
            throw new IllegalStateException("Configuration not initialized yet");
        }
        return this.configuration;
    }

    @Nullable
    public SessionFactory getObject() {
        return this.sessionFactory;
    }

    public Class<?> getObjectType() {
        return this.sessionFactory != null ? this.sessionFactory.getClass() : SessionFactory.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void destroy() {
        if (this.sessionFactory != null) {
            this.sessionFactory.close();
        }
    }
}

